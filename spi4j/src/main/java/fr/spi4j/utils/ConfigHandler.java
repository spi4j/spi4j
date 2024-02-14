/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.spi4j.exception.Spi4jConfigException;

/**
 * This class is the real manager for loading and reading all configuration
 * resources. It handles all the reader handlers configured by the developer of
 * the target application and makes available all the properties for the
 * configuration provider.
 * <p>
 * No specific test for null values, we consider the framework is used by
 * developers.
 *
 * @author MinArm
 */
class ConfigHandler implements ComponentObservable {

	/**
	 * To be used with cron reloader : specific key for resource temporary storage.
	 */
	static final String c_refresh_main_resources_content_key = "config.main.resources.content";

	/**
	 * To be used with cron reloader : specific key for resource reader temporary
	 * storage.
	 */
	static final String c_refresh_reader_handler_key = "config.resources.reader.handler";

	/**
	 * To be used with cron reloader : specific key for observer temporary storage.
	 */
	static final String c_refresh_observers_key = "config.observers";

	/**
	 * To be used with cron reloader : suffix for job uuid.
	 */
	static final String c_refresh_qtz_job_suffix = ".qtz.job";

	/**
	 * To be used with cron reloader : suffix for trigger uuid.
	 */
	static final String c_refresh_qtz_trigger_suffix = ".qtz.trigger";

	/**
	 * To be used with cron reloader : suffix for group uuid.
	 */
	static final String c_refresh_qtz_group_suffix = ".qtz.group";

	/**
	 * This is the main container for the concatenation and centralization of all
	 * properties from all reader handlers. All configuration properties by the
	 * application are added to this unique container.
	 */
	private final ConfigResourcesContent _resourcesContent = new ConfigResourcesContent();

	/**
	 * If needed, store all the configuration resources handlers which also take
	 * care of the writing in the configuration resources. When the developer needs
	 * to write in a specific configuration resource, the system will browse the
	 * list of handlers to find which one handles the desired resource.
	 */
	private final List<ConfigResourcesHandler> _writingResourcesHandlers = new ArrayList<>();

	/**
	 * If the end developer wants to use configuration resource for writing or
	 * deleting properties, since he add or delete a property, the writing
	 * configuration resource is then stored in this variable for future use (add,
	 * replace or delete another property, flush the content in the resource).
	 */
	private ConfigResourceWriter _currentResourceWriter;

	/**
	 * Unique private constructor for the configuration handler. At initialization,
	 * the handler requests the loading and reading of all configuration resources.
	 * For simplicity, the main container is directly passed to each reader in order
	 * to add the properties.
	 * <p>
	 * If the developer needs to write in one ore more configuration resource(s) the
	 * configuration provider store the associated configuration resources handlers.
	 *
	 * @param _resourceReaderHandlers the list of all reader handlers to be managed
	 *                                by the configuration handler.
	 * @throws Exception any exception to be handled by the configuration framework.
	 */
	private ConfigHandler(final List<ConfigResourcesHandler> _resourceReaderHandlers) throws Exception {
		for (final ConfigResourcesHandler v_resourcesHandler : _resourceReaderHandlers) {
			v_resourcesHandler.init(_resourcesContent);
			if (v_resourcesHandler.getResourcesAssessor() instanceof ConfigResourceWriter) {
				_writingResourcesHandlers.add(v_resourcesHandler);
			}
		}
	}

	/**
	 * Retrieve a specific configuration resource assessor for writing.
	 *
	 * @param p_resourceName the name of the configuration resource (the name must
	 *                       be unique).
	 * @return the writing resource. If the configuration resource is not found or
	 *         the resource is not
	 */
	ConfigResourceWriter getResourceWriter(final String p_resourceName) throws Exception {
		for (final ConfigResourcesHandler v_resourcesHandler : _writingResourcesHandlers) {
			for (final Object v_resource : v_resourcesHandler.getResources()) {
				if (p_resourceName.equals(v_resource.toString())) {
					final ConfigResourceReader v_configResourcesAssessor = v_resourcesHandler.getResourcesAssessor();
					v_configResourcesAssessor.readContent(p_resourceName,
							Optional.ofNullable(v_resourcesHandler.getResourcesBase()));
					_currentResourceWriter = (ConfigResourceWriter) v_configResourcesAssessor;
					return _currentResourceWriter;
				}
			}
		}
		throw new Spi4jConfigException(
				"Aucune ressource de configuration disponible pour écriture sous le nom : " + p_resourceName);
	}

	/**
	 * Retrieve the current (working resource) resource writer. The the end
	 * developer can write / delete or update configuration properties and flush
	 * again all data in the resource.
	 *
	 * @return the current resource writer.
	 */
	ConfigResourceWriter getCurrentResourceWriter() {
		return _currentResourceWriter;
	}

	/**
	 * Retrieve a property from it's key under string format. As a reminder, all the
	 * properties are stored under string format with the help of the class
	 * <code>resourceContent</code>.
	 *
	 * @param p_key          the key for the property (always under string format).
	 * @param p_defaultValue if specified the default value to return if the key is
	 *                       not found.
	 * @return the property under string format.
	 */
	String getProperty(final String p_key, final String p_defaultValue) {
		final String v_value = _resourcesContent.get(p_key);
		if (null == v_value) {
			// if (Configuration.getInstance().authorizeConfigurationDefaultValues() &&
			// null != defaultValue) {
			// return defaultValue;
			// }
			throw new Spi4jConfigException(
					"Impossible de récupérer la propriété de configuration avec la clé de configuration : ''{0}''.",
					p_key);
		}
		return v_value;
	}

	/**
	 * Very simple method to add or replace a property in the main properties
	 * container. This method is not accessible for the end developer and is driven
	 * only by the configuration provider (use only for a writing configuration
	 * resource).
	 * <p>
	 * It is important to note that this function is completely uncorrelated with
	 * the function for refreshing read configuration resources.
	 *
	 * @param p_key   the unique key for the property to add or replace in the
	 *                properties main container.
	 * @param p_value the value for the property to add or replace in the properties
	 *                main container.
	 */
	void addContent(final String p_key, final String p_value) {
		_resourcesContent.put(p_key, p_value);
	}

	/**
	 * Very simple method to delete a property in the main properties container.
	 * This method is not accessible for the end developer and is driven only by the
	 * configuration provider (use only for a writing configuration resource).
	 * <p>
	 * It is important to note that this function is completely uncorrelated with
	 * the function for refreshing read configuration resources.
	 *
	 * @param p_key the key to delete in the properties main container.
	 */
	void deleteContent(final String p_key) {
		_resourcesContent.remove(p_key);
	}

	/**
	 * Retrieve the complete list of properties (all defined configuration
	 * resources).
	 *
	 * @return the entire list of properties for the application.
	 */
	ConfigResourcesContent getResourcesContent() {
		return _resourcesContent;
	}

	/**
	 * Registration of a class (component) with an observable component (in this
	 * case the reader handler) to be warned of any change issued from the
	 * observable component. Here, any proxy issued by the 'injection' project
	 * module must register if a configuration variable is detected (and the
	 * variable is present in the list of variables to refresh).
	 * <p>
	 * For now, and for simplicity, no check is made as to the presence of the
	 * handler in the list, it is overwritten as many times as necessary.
	 * <p>
	 * Any change of configuration will warn each specific proxy to refresh the
	 * variables for which he is responsible.
	 *
	 * @apiNote Just note in this particular case, that the observer cannot know if
	 *          one of its configuration variables needs to be refreshed, the
	 *          observer always request to be registered. It is only with this
	 *          method that we can know if a configuration variable is contained in
	 *          the list, so, if there is no record (handler is null), we simply not
	 *          register the observer component, without any exception.
	 *
	 * @param p_observer any class (component) which need to be warned of a change
	 *                   from the observable class.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void registerObserver(final ComponentObserver p_observer) {
		if (null == p_observer) {
			throw new Spi4jConfigException(
					"Un observateur avec la valeur nulle tente de s'enregistrer auprès du gestionnaire de configuration.");
		}
		for (final String v_key : (List<String>) p_observer.getObserverParams()) {
			final ConfigResourcesHandler v_handler = _resourcesContent.getPropertiesToRefresh().get(v_key);
			if (null != v_handler) {
				v_handler.getResourcesReloader().registerObserver(p_observer);
			}
		}
	}

	/**
	 * Builder for the configuration handler.
	 */
	public static class Builder {

		/**
		 * Store all reader handlers with all associated resources and refresh
		 * strategies.
		 */
		private final List<ConfigResourcesHandler> _resourceReaderHandlers = new ArrayList<>();

		/**
		 * Add a new reader to the configuration handler. For each reader, a new reader
		 * handler is created as a specific handler can manage only one reader. On the
		 * other hand, a reader can take care of several configuration resources
		 *
		 * @param p_reader the implementation for a specific resource reader.
		 * @return the builder.
		 */
		public Builder withReader(final ConfigResourceReader p_reader) {
			_resourceReaderHandlers.add(new ConfigResourcesHandler(p_reader));
			return this;
		}

		/**
		 * Add an environment variable key in order to retrieve any base information for
		 * the resources. This information can be anything depending of the type of the
		 * configuration resource. For example, in case of file resource, it could be
		 * the base path to concat with the name of the resource in order to obtain the
		 * fully absolute path of the resource.
		 *
		 * @param p_resourceBaseKey the key for looking in environment variable.
		 * @return the builder.
		 */
		public Builder withResourcesBaseKey(final String p_resourceBaseKey) {
			final String v_resourcesBase = (null != System.getProperty(p_resourceBaseKey)
					? System.getProperty(p_resourceBaseKey)
					: System.getenv(p_resourceBaseKey));
			getCurrentReaderHandler().setResourcesBase(v_resourcesBase);
			return this;
		}

		/**
		 * Add a new encoding to override the default suggested encoding issued from the
		 * resource reader.
		 *
		 * @param p_readerEncoding the specific encoding for the resource(s) associated
		 *                         to the reader.
		 * @return the builder.
		 */
		public Builder withResourcesEncoding(final Charset p_readerEncoding) {
			getCurrentReaderHandler().setEncoding(p_readerEncoding);
			return this;
		}

		/**
		 * Add a specific resource to the current reader handler. A handler (so a reader
		 * since the relation is one-to-one) can manage as configuration resources as
		 * needed as long as the configuration remains unchanged in terms of requesting
		 * reloading and encoding.
		 *
		 * @param p_resource the resource to associate to the current reader handler.
		 *                   The resource can be anything which is necessary for the
		 *                   smooth running of the associated reader.
		 * @return the builder.
		 */
		public Builder withResource(final String p_resource) {
			getCurrentReaderHandler().addResource(p_resource);
			return this;
		}

		/**
		 * Add a specific refresh strategy (also called configuration reloader) for the
		 * resource(s) reader.
		 *
		 * @param p_resourcesReloader the specific strategy for the reader. All
		 *                            associated resources will be refreshed basis on
		 *                            this strategy.
		 * @return the builder.
		 */
		public Builder withResourcesReloader(final ConfigResourcesReloader p_resourcesReloader) {
			getCurrentReaderHandler().setResourceReloader(p_resourcesReloader);
			return this;
		}

		/**
		 * Get the current reader handler to add some associated resource or refresh
		 * strategy.
		 *
		 * @return the current (last in the list) reader.
		 */
		private ConfigResourcesHandler getCurrentReaderHandler() {
			if (_resourceReaderHandlers.isEmpty()) {
				throw new Spi4jConfigException("Impossible de créer le fournisseur de configuration, "
						+ "aucun lecteur de configuration n'a été trouvé.");
			}
			return _resourceReaderHandlers.get(_resourceReaderHandlers.size() - 1);
		}

		/**
		 * Create a new configuration handler with all the reader handlers. If no
		 * resource found for the resources handler, and the reader implements the
		 * interface <code>ConfigOptionalResource</code>, then we automatically add a
		 * null resource to pass the algo. The developer of the resource has to check
		 * the nullity in the reader.
		 */
		public void build() {
			if (_resourceReaderHandlers.isEmpty()) {
				throw new Spi4jConfigException("Impossible de créer le fournisseur de configuration, "
						+ "aucun lecteur de configuration n'a été trouvé.");
			}

			for (final ConfigResourcesHandler v_configResourcesHandler : _resourceReaderHandlers) {
				if (v_configResourcesHandler.getResources().isEmpty()) {
					if (v_configResourcesHandler.getResourcesAssessor() instanceof ConfigOptionalResource) {
						v_configResourcesHandler.getResources().add(null);
						continue;
					}
					throw new Spi4jConfigException(
							"Impossible de créer le fournisseur de configuration, "
									+ "aucune ressource n''a été ajoutée au lecteur : {0}",
							v_configResourcesHandler.getClass());
				}
			}

			try {
				ConfigProvider.build(new ConfigHandler(_resourceReaderHandlers));
			} catch (final Exception p_e) {
				throw new Spi4jConfigException(p_e,
						"Une erreur a interrompu le chargement du gestionnaire de configuration.");
			}
		}
	}
}
