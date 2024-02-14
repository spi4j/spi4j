/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fr.spi4j.exception.Spi4jConfigException;

/**
 * Specific quartz job for refreshing the resource configuration. Do not
 * suppress the public modifier for the class else the quartz job wont work !!
 *
 * @author MinArm
 */
public class ConfigResourceQtzJob implements Job {

	/**
	 * The logger for the job class.
	 */
	final Logger _logger = LogManager.getLogger(ConfigResourceQtzJob.class);

	/**
	 * As this class implements the interface job from quartz framework, it is just
	 * used make the job managed by quartz. If an exception is thrown, just replace
	 * the default <code>JobExecutionException</code> by a more convenient
	 * <code>ConfigurationException</code>.
	 */
	@Override
	public void execute(final JobExecutionContext p_context) throws JobExecutionException {
		try {
			updateConfiguration(p_context);
		} catch (final Exception ex) {
			throw new Spi4jConfigException(ex,
					"Une erreur a été rencontrée lors de la demande rechargement pour la ressource {0} par le job quartz.",
					p_context.getJobDetail().getJobDataMap());
		}
	}

	/**
	 * This method performs the reloading of all the configuration resources
	 * associated with a configuration handler. First it retrieves all the
	 * informations needed to process the request from the data map, then, the
	 * proofreading of all resources is requested.
	 *
	 * @param p_context the job execution context issued from quartz framework.
	 * @throws Exception any unknown exception likely to be thrown by the method.
	 */
	@SuppressWarnings("unchecked")
	private void updateConfiguration(final JobExecutionContext p_context) throws Exception {

		final ConfigResourcesContent v_resourcesContent = (ConfigResourcesContent) getParam(p_context,
				ConfigHandler.c_refresh_main_resources_content_key);

		final ConfigResourcesHandler v_readerHandler = (ConfigResourcesHandler) getParam(p_context,
				ConfigHandler.c_refresh_reader_handler_key);

		final List<ComponentObserver> v_observers = (List<ComponentObserver>) getParam(p_context,
				ConfigHandler.c_refresh_observers_key);

		for (final String v_resource : v_readerHandler.getResources()) {
			_logger.info("Demande de rechargement pour le fichier de configuration : {}.",
					v_readerHandler.getResourcesAssessor().getIdentification());
			v_resourcesContent.putAll(v_readerHandler.getResourcesAssessor().readContent(v_resource,
					Optional.ofNullable(v_readerHandler.getResourcesBase())));
			for (final ComponentObserver v_observer : v_observers) {
				v_observer.onObservableEvent();
			}
		}
	}

	/**
	 * Retrieve an object in the job map with the unique specific key associated to
	 * the object.
	 *
	 * @param p_context the context for the quartz job.
	 * @param p_key     the specified key to retreive the associated object in the
	 *                  map.
	 * @return the object contained in the map.
	 */
	private Object getParam(final JobExecutionContext p_context, final String p_key) {
		return p_context.getJobDetail().getJobDataMap().get(p_key);
	}
}
