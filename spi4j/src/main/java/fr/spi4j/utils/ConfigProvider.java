/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import fr.spi4j.exception.Spi4jConfigException;
import fr.spi4j.utils.ConfigHandler.Builder;

/**
 * At configuration level, this class is the unique external entry point for the
 * target application. All the implementation is hidden by the
 * <code>configurationHandler</code> class which is not directly accessible from
 * the target application. With this class, the developer can specify all the
 * configuration resources he wants to use.
 *
 * @author MinArm
 */
public class ConfigProvider {

	/**
	 * The handler for all the configuration resources (all types of resources
	 * combined). The configuration handler gathers all configuration resource
	 * readers and there associated configuration resources, it is a container that
	 * allows to control all the configuration resources and make them available to
	 * this class.
	 */
	private final ConfigHandler _configHandler;

	/**
	 * Unique instance for the configuration provider. The instantiation of this
	 * class is made by the synchronized method <code>build(...)</code> which is is
	 * responsible for ensuring the uniqueness of the class.
	 */
	private static ConfigProvider _configProvider;

	/**
	 * Private constructor for the configuration provider. The configuration handler
	 * manage all configuration resources, the configuration provider is an entry
	 * point for the configuration handler.
	 *
	 * @param p_configHandler the configuration handler to inject in the
	 *                        configuration provider.
	 */
	private ConfigProvider(final ConfigHandler p_configHandler) {
		_configHandler = p_configHandler;
	}

	/**
	 * Retrieve a integer property from the configurationHandler.
	 *
	 * @param key the property key always under string format.
	 * @return the property under integer format.
	 */
	public static int getInteger(final String key) {
		return Integer.valueOf(getProperty(key, null));
	}

	/**
	 * Retrieve a float property from the configurationHandler.
	 *
	 * @param key the property key always under string format.
	 * @return the property under float format.
	 */
	public static float getFloat(final String key) {
		return Float.valueOf(getProperty(key, null));
	}

	/**
	 * Retrieve a long property from the configurationHandler.
	 *
	 * @param key the property key always under string format.
	 * @return the property under long format.
	 */
	public static long getLong(final String key) {
		return Long.valueOf(getProperty(key, null));
	}

	/**
	 * Retrieve a string property from the configurationHandler.
	 *
	 * @param key the property key always under string format.
	 * @return the property under long format.
	 */
	public static String getString(final String key) {
		return getProperty(key, null).trim();
	}

	/**
	 * Retrieve a boolean property from the configurationHandler.
	 *
	 * @param key the property key always under string format.
	 * @return the property under boolean format.
	 */
	public static boolean getBoolean(final String key) {
		return Boolean.valueOf(getProperty(key, null));
	}

	/**
	 * Retrieve a short property from the configurationHandler.
	 *
	 * @param key the property key always under string format.
	 * @return the property under short format.
	 */
	public static short getShort(final String key) {
		return Short.valueOf(getProperty(key, null));
	}

	/**
	 * Retrieve a double property from the configurationHandler.
	 *
	 * @param p_key the property key always under string format.
	 * @return the property under double format.
	 */
	public static double getDouble(final String p_key) {
		return Double.valueOf(getProperty(p_key, null));
	}

	/**
	 * Retrieve a integer property from the configurationHandler. If no property
	 * found, apply the default value.
	 *
	 * @param key          the property key always under string format.
	 * @param defaultValue the defaultValue if the key is not found.
	 * @return the property under integer format.
	 */
	public static int getInteger(final String key, final String defaultValue) {
		return Integer.valueOf(getProperty(key, defaultValue));
	}

	/**
	 * Retrieve a float property from the configurationHandler. If no property
	 * found, apply the default value.
	 *
	 * @param key          the property key always under string format.
	 * @param defaultValue the defaultValue if the key is not found.
	 * @return the property under float format.
	 */
	public static float getFloat(final String key, final String defaultValue) {
		return Float.valueOf(getProperty(key, defaultValue));
	}

	/**
	 * Retrieve a long property from the configurationHandler. If no property found,
	 * apply the default value.
	 *
	 * @param key          the property key always under string format.
	 * @param defaultValue the defaultValue if the key is not found.
	 * @return the property under long format.
	 */
	public static long getLong(final String key, final String defaultValue) {
		return Long.valueOf(getProperty(key, defaultValue));
	}

	/**
	 * Retrieve a string property from the configurationHandler. If no property
	 * found, apply the default value.
	 *
	 * @param key          the property key always under string format.
	 * @param defaultValue the defaultValue if the key is not found.
	 * @return the property under long format.
	 */
	public static String getString(final String key, final String defaultValue) {
		return getProperty(key, defaultValue).trim();
	}

	/**
	 * Retrieve a boolean property from the configurationHandler. If no property
	 * found, apply the default value.
	 *
	 * @param key          the property key always under string format.
	 * @param defaultValue the defaultValue if the key is not found.
	 * @return the property under boolean format.
	 */
	public static boolean getBoolean(final String key, final String defaultValue) {
		return Boolean.valueOf(getProperty(key, defaultValue));
	}

	/**
	 * Retrieve a short property from the configurationHandler. If no property
	 * found, apply the default value.
	 *
	 * @param key          the property key always under string format.
	 * @param defaultValue the defaultValue if the key is not found.
	 * @return the property under short format.
	 */
	public static short getShort(final String key, final String defaultValue) {
		return Short.valueOf(getProperty(key, defaultValue));
	}

	/**
	 * Retrieve a double property from the configurationHandler. If no property
	 * found, apply the default value.
	 *
	 * @param p_key        the property key always under string format.
	 * @param defaultValue the defaultValue if the key is not found.
	 * @return the property under double format.
	 */
	public static double getDouble(final String p_key, final String defaultValue) {
		return Double.valueOf(getProperty(p_key, defaultValue));
	}

	/**
	 * Retrieve the complete list of properties (all defined configuration
	 * resources).
	 *
	 * @return the entire list of properties for the application.
	 */
	public static ConfigResourcesContent getResourcesContent() {
		return getInternalConfig().getResourcesContent();
	}

	/**
	 * Retrieve a string property from the configurationHandler. For simplicity,
	 * this method is also used as an entry point for all other static methods.
	 *
	 * @param key p_key the property key always under string format.
	 * @return the property always under string format.
	 */
	private static String getProperty(final String key, final String defaultValue) {
		return getInternalConfig().getProperty(key, defaultValue);
	}

	/**
	 * Retrieve the handler of all configuration resources for the provider. This
	 * method also check the instances of provider and handler are not null.
	 *
	 * @return the handler for the provider (fluent pattern).
	 */
	private static ConfigHandler getInternalConfig() {
		if (null == _configProvider || null == _configProvider._configHandler) {
			throw new Spi4jConfigException("Aucun fournisseur de configuration n'a été créé !");
		}
		return _configProvider._configHandler;
	}

	/**
	 * Retrieve e specific current writing resource. This method also check the
	 * instances of provider, handler and resource writer are not null.
	 *
	 * @return the current writing resource (fluent pattern).
	 */
	private static ConfigResourceWriter getInternalWritingConfig() {
		if (null == getInternalConfig().getCurrentResourceWriter()) {
			throw new Spi4jConfigException("Aucune ressource en écriture n'est actuellement disponible !");
		}
		return getInternalConfig().getCurrentResourceWriter();
	}

	/**
	 * Retrieve the specific writing configuration resource by his name and set the
	 * resource in the current working writing resource.
	 *
	 * @param p_resourceName the name of the writing configuration resource.
	 */
	public static void getWritingResource(final String p_resourceName) {
		try {
			getInternalConfig().getResourceWriter(p_resourceName);
		} catch (final Exception p_e) {
			throw new Spi4jConfigException(p_e,
					"Erreur lors de la demande d''écriture dans la ressource de configuration ''{0}''", p_resourceName);
		}
	}

	/**
	 * Add a new property content to the specified configuration resource. The
	 * second line should have been triggered, see later to have a proper code.
	 *
	 * @param p_key   the unique key for the property.
	 * @param p_value the value for the property.
	 */
	@SuppressWarnings("javadoc")
	public static <T> void writeContent(final String p_key, final T p_value) {
		try {
			getInternalWritingConfig().addContent(p_key, p_value);
			getInternalConfig().addContent(p_key, String.valueOf(p_value));
		} catch (final Exception p_e) {
			throw new Spi4jConfigException(p_e,
					"Erreur lors de la demande d''écriture dans la ressource de configuration ''{0}'' pour la clé ''{1}''",
					((ConfigResourceReader) getInternalWritingConfig()).getIdentification(), p_key);
		}
	}

	/**
	 *
	 * @param p_key the unique key for the property.
	 */
	public static void deleteContent(final String p_key) {
		try {
			getInternalWritingConfig().deleteContent(p_key);
			getInternalConfig().deleteContent(p_key);
		} catch (final Exception p_e) {
			throw new Spi4jConfigException(p_e,
					"Erreur lors de la demande de suppression de la clé ''{0}'' dans la ressource de configuration ''{1}''",
					p_key, ((ConfigResourceReader) getInternalWritingConfig()).getIdentification());
		}
	}

	/**
	 * Flush the entire content in the current writing resource. The resource can be
	 * anything, a file, an external service, a database, etc...
	 */
	public static void store() {
		try {
			getInternalWritingConfig().store();
		} catch (final Exception p_e) {
			throw new Spi4jConfigException(p_e,
					"Erreur lors de la demande d''écriture dans la ressource de configuration ''{0}''.",
					((ConfigResourceReader) getInternalWritingConfig()).getIdentification());
		}
	}

	/**
	 * Retrieve the builder for the configuration handler. With this builder, the
	 * developer can specify the configuration resources he needs for the target
	 * application.
	 * <p>
	 * By a communication system between the tow classes, the developer will
	 * terminate the construction of the builder with the call of the method
	 * <code>build()</code> of the class <code>configurationHandler</code>. This
	 * method will then, call the the package-friendly method <code>build()</code>
	 * of this class in order to inject directly the configuration handler in the
	 * configuration provider.
	 *
	 * @return the builder for the configuration handler.
	 */
	public static Builder getBuilder() {
		return new ConfigHandler.Builder();
	}

	/**
	 * This method, not visible for the developer, is accessed by the method
	 * <code>build()</code> of the configuration handler. The method is just
	 * synchronized to avoid multiple instances of the class. As the access for this
	 * method is made only once at bootstrap time, there is absolutely no impact
	 * over performance.
	 * <p>
	 * Once the configuration provider is created, it is registered in the internal
	 * configuration system for the framework. This allow (with help of the
	 * interfaces) to have modules completely separated : for example, the end
	 * developer can import only the configuration module or only the injection
	 * module. If both are imported in the target application, then the end
	 * developer can use the configuration annotations, else he will have to use the
	 * static methods of the configuration provider.
	 *
	 * @param p_configHandler the handler for all the configuration resources (all
	 *                        types of resources combined).
	 * @throws Spi4jConfigException any exception handled by the configuration
	 *                              framework.
	 */
	static synchronized void build(final ConfigHandler p_configHandler) throws Spi4jConfigException {
		if (null != _configProvider) {
			throw new Spi4jConfigException(
					"Une instance du fournisseur de configuration pour l'application existe déjà.");
		}
		_configProvider = new ConfigProvider(p_configHandler);
	}
}
