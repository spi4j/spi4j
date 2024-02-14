/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.exception.Spi4jConfigException;

/**
 * This reloader works only with resource of type file.
 *
 * @author MinArm
 */
public class ConfigAgentFileReloader implements ConfigResourcesReloader, Runnable {

	/**
	 * The logger for the job class.
	 */
	final Logger _logger = LogManager.getLogger(ConfigAgentFileReloader.class);

	/**
	 * List of observers for the refresh strategy.
	 */
	private final List<ComponentObserver> _observers = new ArrayList<>();

	/**
	 * As to be homogeneous with the principle of the quartz library. Store all the
	 * necessary objects to a map, this allow to pass elements to the run method
	 * which has no argument by default.
	 */
	private final Map<String, Object> _map = new HashMap<>();

	@Override
	public void start(final ConfigResourcesContent p_resourcesContent,
			final ConfigResourcesHandler p_resourceReaderHandler) throws Exception {

		_map.put(ConfigHandler.c_refresh_reader_handler_key, p_resourceReaderHandler);
		_map.put(ConfigHandler.c_refresh_main_resources_content_key, p_resourcesContent);
		new Thread(this).start();

	}

	/**
	 * Run the agent for configuration resource monitoring.
	 *
	 * @throws Exception any unexpected exception.
	 */
	private void runWatchService() throws Exception {
		WatchKey v_key;
		final WatchService v_watchService = FileSystems.getDefault().newWatchService();
		final ConfigResourcesHandler v_resourceReaderHandler = (ConfigResourcesHandler) _map
				.get(ConfigHandler.c_refresh_reader_handler_key);

		final List<String> v_resources = v_resourceReaderHandler.getResources();
		final ConfigResourcesContent v_resourcesContent = (ConfigResourcesContent) _map
				.get(ConfigHandler.c_refresh_main_resources_content_key);

		for (final String v_resource : v_resources) {
			final File v_file = new File(ConfigFileUtils.getResource(v_resource,
					Optional.ofNullable(v_resourceReaderHandler.getResourcesBase())));
			final Path v_filePath = Paths.get(v_file.getPath()).getParent();
			v_filePath.register(v_watchService, ENTRY_MODIFY);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					v_watchService.close();
				} catch (final IOException p_e) {
					throw new Spi4jConfigException(p_e,
							"Problème de fermeture pour l'agent de surveillance pour le(s) fichier(s) de configuration.");
				}
			}
		});

		while (true) {
			v_key = v_watchService.take();
			// Wait until event...
			for (final WatchEvent<?> v_event : v_key.pollEvents()) {
				for (final String v_resource : v_resources) {
					final File v_file = new File(ConfigFileUtils.getResource(v_resource,
							Optional.ofNullable(v_resourceReaderHandler.getResourcesBase())));
					if (v_event.context().toString().equals(Paths.get(v_file.getPath()).getFileName().toString())) {
						_logger.info("Demande de rechargement pour le fichier de configuration : {}.",
								v_resourceReaderHandler.getResourcesAssessor().getIdentification());
						v_resourcesContent.putAll(v_resourceReaderHandler.getResourcesAssessor().readContent(v_resource,
								Optional.ofNullable(v_resourceReaderHandler.getResourcesBase())));
						for (final ComponentObserver v_observer : _observers) {
							v_observer.onObservableEvent();
						}
					}
				}
			}

			if (!v_key.reset()) {
				System.out.println("Could not reset the watch key.");
				break;
			}
		}
	}

	@Override
	public void run() {
		try {
			runWatchService();
		} catch (final Exception p_e) {
			throw new Spi4jConfigException(p_e,
					"Impossible de démarrer l'agent de surveillance pour le(s) fichier(s) de configuration. ",
					new Object[] {});
		}
	}

	/**
	 * Simple externalized method for adding an observer to the observable element.
	 *
	 * @param p_observer the component observer (class) to register.
	 */
	@Override
	public void registerObserver(final ComponentObserver p_observer) {
		_observers.add(p_observer);
	}
}
