/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.ws.rs.exception.RsPagingException;
import fr.spi4j.ws.rs.exception.RsSpi4jUnexpectedException;

/**
 * Configuration storage for the application.
 *
 * @author MINARM
 */
public class RsFilterConfigurator {

	/**
	 * The logger for the class.
	 */
	private static final Logger c_log = LogManager.getLogger(RsFilterConfigurator.class);

	/**
	 * The full base path for configuration directory (with prefix).
	 */
	private String _configPathPrefix;

	/**
	 * The operation mode for the filter.
	 */
	private RsFilterMode_Enum _operatingMode;

	/**
	 * The routing strategy for the filter.
	 */
	private RsFilterRouting_Enum _routingStrategy;

	/**
	 * The name of the application defined in the PES (DR).
	 */
	private String _pesApplicationName;

	/**
	 * An information header for the PES (DR).
	 */
	private String _pesHeaderSecu;

	/**
	 * An information header for the PES (DR).
	 */
	private String _pesHeaderMention;

	/**
	 * An information header for the PES (DR).
	 */
	private String _pesHeaderConstraint;

	/**
	 * A specific key for PES (DR).
	 */
	private String _pesHeaderSecuKey;

	/**
	 * A specific key for PES (DR).
	 */
	private String _pesHeaderMentionKey;

	/**
	 * A specific key for PES (DR).
	 */
	private String _pesHeaderConstraintKey;

	/**
	 * A specific key for PES (DR).
	 */
	private String _pesHeaderAppKey;

	/**
	 * A specific key for PES (DR).
	 */
	private String _pesHeaderDlppKey;

	/**
	 * A specific key for PES (DR).
	 */
	private String _pesHeaderTokenKey;

	/**
	 * The operational java signature keys for tokens.
	 */
	private final Map<String, Key> _tokenSigningKeys = new HashMap<>();

	/**
	 * The required tokens with all the required claims.
	 */
	private RsTokensContainer _tokensContainer;

	/**
	 * Store the keys names for pagination.
	 */
	private RsHeaderPageParams _headersPageParams;

	/**
	 * Empty Constructor.
	 */
	public RsFilterConfigurator() {
		// RAS.
	}

	/**
	 * Constructor.
	 *
	 * @param p_configFilePath : The complete URI file path for the configuration
	 *                         file.
	 */
	public RsFilterConfigurator(final URI p_configFilePath) {
		loadFilterConfiguration(p_configFilePath.getPath());
	}

	/**
	 * Constructor.
	 *
	 * @param p_prefix : By default, the logical name of the application (name of
	 *                 the project).
	 */
	public RsFilterConfigurator(final String p_prefix) {
		// Store the configuration file path with '/applicationName'.
		_configPathPrefix = System.getenv(p_prefix.toUpperCase() + RsConstants.c_conf_filter_properties_file)
				+ File.separator + p_prefix.toUpperCase();

		// Load the configuration for the filter.
		loadFilterConfiguration(_configPathPrefix + RsConstants.c_conf_filter_properties_application_file);
	}

	/**
	 * Retrieve the full base path (with prefix) for other configuration file(s) (if
	 * needed) .
	 *
	 * @return The full base path for the other configuration file(s).
	 */
	String get_configPathPrefix() {
		return _configPathPrefix;
	}

	/**
	 * Retrieve the mode of operation for the filter (DEBUG / etc...).
	 *
	 * @return The mode of operation.
	 */
	public RsFilterMode_Enum get_operatingMode() {
		return _operatingMode;
	}

	/**
	 * Set the mode mode of operation for the filter (DEBUG / etc...).
	 *
	 * @param p_operatingMode : The operation mode fort the filter.
	 */
	public void set_operatingMode(final RsFilterMode_Enum p_operatingMode) {
		_operatingMode = p_operatingMode;
	}

	/**
	 * Retrieve the routing mode for the filter.
	 *
	 * @return The routing mode for the filter.
	 */
	public RsFilterRouting_Enum get_routingStrategy() {
		return _routingStrategy;
	}

	/**
	 * Set the routing mode for the filter.
	 *
	 * @param p_routingStrategy : The routing mode for the filter.
	 */
	public void set_routingStrategy(final RsFilterRouting_Enum p_routingStrategy) {
		_routingStrategy = p_routingStrategy;
	}

	/**
	 * Retrieve the specific information header key for the PES.
	 *
	 * @return The key.
	 */
	public String get_pesHeaderTokenKey() {
		return _pesHeaderTokenKey;
	}

	/**
	 * Set the specific information header key for the PES.
	 *
	 * @param p_pesHeaderTokenKey : A key header for the PES.
	 */
	public void set_pesHeaderTokenKey(final String p_pesHeaderTokenKey) {
		_pesHeaderTokenKey = p_pesHeaderTokenKey;
	}

	/**
	 * Retrieve the specific information header for the PES.
	 *
	 * @return The logical registered application name for the PES.
	 */
	public String get_pesApplicationName() {
		return _pesApplicationName;
	}

	/**
	 * Set the specific information header for the PES.
	 *
	 * @param p_pesApplicationName : The logical registered application name for the
	 *                             PES.
	 */
	public void set_pesApplicationName(final String p_pesApplicationName) {
		_pesApplicationName = p_pesApplicationName;
	}

	/**
	 * Retrieve the specific information header for the PES.
	 *
	 * @return An information header for the PES.
	 */
	public String get_pesHeaderSecu() {
		return _pesHeaderSecu;
	}

	/**
	 * Set the specific information header for the PES.
	 *
	 * @param p_pesHeaderSecu : An information header for the PES.
	 */
	public void set_pesHeaderSecu(final String p_pesHeaderSecu) {
		_pesHeaderSecu = p_pesHeaderSecu;
	}

	/**
	 * Retrieve the specific information header for the PES.
	 *
	 * @return An information header for the PES.
	 */
	public String get_pesHeaderMention() {
		return _pesHeaderMention;
	}

	/**
	 * Set the specific information header for the PES.
	 *
	 * @param p_pesHeaderMention : An information header for the PES.
	 */
	public void set_pesHeaderMention(final String p_pesHeaderMention) {
		_pesHeaderMention = p_pesHeaderMention;
	}

	/**
	 * Retrieve the specific information header for the PES.
	 *
	 * @return An information header for the PES.
	 */
	public String get_pesHeaderConstraint() {
		return _pesHeaderConstraint;
	}

	/**
	 * Set the specific information header for the PES.
	 *
	 * @param p_pesHeaderConstraint : An information header for the PES.
	 */
	public void set_pesHeaderConstraint(final String p_pesHeaderConstraint) {
		_pesHeaderConstraint = p_pesHeaderConstraint;
	}

	/**
	 * Retrieve the specific information header key for the PES.
	 *
	 * @return A key header for the PES.
	 */
	public String get_pesHeaderSecuKey() {
		return _pesHeaderSecuKey;
	}

	/**
	 * Set the specific information header key for the PES.
	 *
	 * @param p_pesHeaderSecuKey : A key header for the PES.
	 */
	public void set_pesHeaderSecuKey(final String p_pesHeaderSecuKey) {
		_pesHeaderSecuKey = p_pesHeaderSecuKey;
	}

	/**
	 * Retrieve the specific information header key for the PES.
	 *
	 * @return A key header for the PES.
	 */
	public String get_pesHeaderMentionKey() {
		return _pesHeaderMentionKey;
	}

	/**
	 * Set the specific information header key for the PES.
	 *
	 * @param p_pesHeaderMentionKey : A key header for the PES.
	 */
	public void set_pesHeaderMentionKey(final String p_pesHeaderMentionKey) {
		_pesHeaderMentionKey = p_pesHeaderMentionKey;
	}

	/**
	 * Retrieve the specific information header key for the PES.
	 *
	 * @return A key header for the PES.
	 */
	public String get_pesHeaderConstraintKey() {
		return _pesHeaderConstraintKey;
	}

	/**
	 * Set the specific information header key for the PES.
	 *
	 * @param p_pesHeaderConstraintKey : A key header for the PES.
	 */
	public void set_pesHeaderConstraintKey(final String p_pesHeaderConstraintKey) {
		_pesHeaderConstraintKey = p_pesHeaderConstraintKey;
	}

	/**
	 * Retrieve the specific information header key for the PES.
	 *
	 * @return A key header for the PES.
	 */
	public String get_pesHeaderAppKey() {
		return _pesHeaderAppKey;
	}

	/**
	 * Set the specific information header key for the PES.
	 *
	 * @param p_pesHeaderAppKey : A key header for the PES.
	 */
	public void set_pesHeaderAppKey(final String p_pesHeaderAppKey) {
		_pesHeaderAppKey = p_pesHeaderAppKey;
	}

	/**
	 * Retrieve the specific information header key for the PES.
	 *
	 * @return A key header for the PES.
	 */
	public String get_pesHeaderDlppKey() {
		return _pesHeaderDlppKey;
	}

	/**
	 * Set the specific information header key for the PES.
	 *
	 * @param p_pesHeaderDlppKey : A key header for the PES.
	 */
	public void set_pesHeaderDlppKey(final String p_pesHeaderDlppKey) {
		_pesHeaderDlppKey = p_pesHeaderDlppKey;
	}

	/**
	 * Shortcut for setting operation mode after constructor.
	 *
	 * @return The instance of RsFilterConfigurator (pattern fluent).
	 */
	public RsFilterConfigurator set_debugMode() {
		set_operatingMode(RsFilterMode_Enum.debug);
		return this;
	}

	/**
	 * Shortcut for setting operation mode after constructor.
	 *
	 * @return The instance of RsFilterConfigurator (pattern fluent).
	 */
	public RsFilterConfigurator set_testMode() {
		set_operatingMode(RsFilterMode_Enum.test);
		return this;
	}

	/**
	 * Shortcut for setting operation mode after constructor.
	 *
	 * @return The instance of RsFilterConfigurator (pattern fluent).
	 */
	public RsFilterConfigurator set_integMode() {
		set_operatingMode(RsFilterMode_Enum.integ);
		return this;
	}

	/**
	 * Shortcut for setting operation mode after constructor.
	 *
	 * @return The instance of RsFilterConfigurator (pattern fluent).
	 */
	public RsFilterConfigurator set_prodMode() {
		set_operatingMode(RsFilterMode_Enum.prod);
		return this;
	}

	/**
	 * Check if the application is behind a Gateway (type PES).
	 *
	 * @return True if the filter must be under the PES.
	 */
	public boolean is_PESHeadersRequired() {
		return _routingStrategy == RsFilterRouting_Enum.pem_gateway
				|| _routingStrategy == RsFilterRouting_Enum.papi_gateway;
	}

	/**
	 * Retrieve all the tokens definition with all required claims.
	 *
	 * @return The tokens with all required claims and the desired values.
	 */
	public RsTokensContainer get_tokensContainer() {
		return _tokensContainer;
	}

	/**
	 * Retrieve the list of keys to decode a token signature.
	 *
	 * @return The list of operational keys.
	 */
	public Map<String, Key> get_tokenSigningKeys() {
		return _tokenSigningKeys;
	}

	/**
	 * Create the list of operational keys for the token signature.
	 *
	 * @param p_tokenContainer : All the tokens definition with all required claims
	 *                         and desired values.
	 */
	void set_tokenSigningKeys(final RsTokensContainer p_tokenContainer) {
		for (final RsToken v_token : p_tokenContainer.get_tokens()) {
			if (v_token.hasToLoadSigningKeys()) {
				c_log.info("Chargement des clés à partir de l'URI : " + v_token.get_resourcePath());
				_tokenSigningKeys.putAll(v_token.updateWithSigningKeys(v_token.get_signingKeyLoad()
						.load(v_token.get_resourcePath(), v_token.get_signingKeyAlgorithm())));
			}
		}
	}

	/**
	 * Set the definition of the required tokens.
	 *
	 * @param p_tokensContainer : All the tokens definition with all required claims
	 *                          and desired values.
	 */
	public void set_tokensContainer(final RsTokensContainer p_tokensContainer) {
		_tokensContainer = p_tokensContainer;
	}

	/**
	 * Retrieve the keys for custom names for pagination.
	 *
	 * @return The keys for custom names for pagination.
	 */
	public RsHeaderPageParams get_headersPageParams() {
		return _headersPageParams;
	}

	/**
	 * Fill the header keys if the developer want custom names for pagination.
	 *
	 * @param p_headerKeys : The keys for paging.
	 */
	public void set_headersPageParams(final String[] p_headerKeys) {

		if (null == p_headerKeys || p_headerKeys.length != 4) {
			throw new RsPagingException("Impossible de charger les clés d'en-tête pour la pagination.");
		}

		_headersPageParams = new RsHeaderPageParams(p_headerKeys[0], p_headerKeys[1], p_headerKeys[2], p_headerKeys[3]);

	}

	/**
	 * Load all the filter configuration from a ".properties" file.
	 *
	 * @param p_propertiesFileName : The path and name to the configuration
	 *                             properties file.
	 */
	private void loadFilterConfiguration(final String p_propertiesFileName) {
		try {
			final Properties v_props = new Properties();
			v_props.load(new FileInputStream(p_propertiesFileName));

			// Load basic string properties...
			set_pesApplicationName(v_props.getProperty(RsConstants.c_conf_pes_app_name));
			set_pesHeaderConstraint(v_props.getProperty(RsConstants.c_conf_pes_header_constraint));
			set_pesHeaderMention(v_props.getProperty(RsConstants.c_conf_pes_header_mention));
			set_pesHeaderSecu(v_props.getProperty(RsConstants.c_conf_pes_header_secu));
			set_pesHeaderAppKey(v_props.getProperty(RsConstants.c_auth_header_pes_appli_name));
			set_pesHeaderConstraintKey(v_props.getProperty(RsConstants.c_auth_header_pes_constraint));
			set_pesHeaderDlppKey(v_props.getProperty(RsConstants.c_auth_header_pes_dlpp_name));
			set_pesHeaderMentionKey(v_props.getProperty(RsConstants.c_auth_header_pes_mention));
			set_pesHeaderSecuKey(v_props.getProperty(RsConstants.c_auth_header_pes_secu));
			// set_pesHeaderTokenKey(v_props.getProperty(RsApplicationConstants.c_auth_header_pes_token));

			// Load enumerations...
			set_operatingMode(RsFilterMode_Enum.valueOf(v_props.getProperty(RsConstants.c_conf_filter_operating_mode)));

			set_routingStrategy(
					RsFilterRouting_Enum.valueOf(v_props.getProperty(RsConstants.c_conf_filter_routing_strategy)));

			// All other properties are completed directly by the filter at server
			// loading...
		} catch (final FileNotFoundException p_e) {
			throw new RsSpi4jUnexpectedException(
					"Impossible de charger le fichier de configuration du filtre pour les services REST : "
							+ p_e.getMessage());
		} catch (final Exception p_e) {
			throw new RsSpi4jUnexpectedException(
					"Impossible de charger la configuration du filtre pour les services REST : " + p_e.getMessage());
		}
	}

	/**
	 * Log the basic configuration parameters at server startup. The logger is not
	 * static as it is used once.
	 *
	 * @param p_log :The logger.
	 */
	void displayFilterConfigToConsole(final Logger p_log) {
		p_log.info("Chargement de la configuration du filtre RS...");
		p_log.info(RsConstants.c_conf_filter_operating_mode + " : " + get_operatingMode());
		p_log.info(RsConstants.c_conf_filter_routing_strategy + " : " + get_routingStrategy());
	}
}
