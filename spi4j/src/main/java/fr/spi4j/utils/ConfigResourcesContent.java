/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.spi4j.exception.Spi4jConfigException;

/**
 * This container does not add much in terms of storage as it is just an
 * inheritance from the class hashMap but it permits however to add more control
 * and manage substitution values over the insertion for each property.
 * <p>
 * Take into account that this class is only there to manage information at the
 * property level !
 *
 * @author MinArm
 */
public class ConfigResourcesContent extends HashMap<String, String> {

	/**
	 * Generated serial ID for serialization.
	 */
	private static final long serialVersionUID = 8900906919652222218L;

	/**
	 * Regex (begin part) for substitution key.
	 */
	static final String c_prop_subst_key_rgx_begin = "\\$\\{";

	/**
	 * Regex (end part) for substitution key.
	 */
	static final String c_prop_subst_key_rgx_end = "\\}";

	/**
	 * Full Regex for substitution key.
	 */
	static final String c_prop_subst_key_rgx = "\\$\\{(.+?)\\}";

	/**
	 * Regular expression pattern for any potential substitution variable key to
	 * find in a property value.
	 */
	private static Pattern _pattern = Pattern.compile(c_prop_subst_key_rgx);

	/**
	 * An internal list of all all property keys that need to be refreshed. Each key
	 * is associated with the handler who is responsible for reading it. With this
	 * map, it is then, very simple to bind any component observer with the
	 * observable handler, on order to be advised of a reload of the property.
	 */
	private final Map<String, ConfigResourcesHandler> _propertiesToRefresh = new HashMap<>();

	/**
	 * Constructor for the resource content. This constructor should be used for all
	 * configuration resources issued from files, database or anything else which is
	 * different from a simple and unique property (for simplicity the next
	 * constructor should be preferred).
	 *
	 * @param p_properties the list of all properties to add for the resource
	 *                     content.
	 * @throws Spi4jConfigException any exception related to the retrieval of a
	 *                              configuration resource.
	 */
	ConfigResourcesContent(final Map<Object, Object> p_properties) throws Spi4jConfigException {
		for (final Map.Entry<Object, Object> entry : p_properties.entrySet()) {
			putProperty(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), p_properties);
		}
	}

	/**
	 * Constructor for the resource content. Use this constructor when a unique
	 * property needs to be added. For example it could be for a system property.
	 *
	 * @param p_key   the unique key for property identification.
	 * @param p_value the value for the property.
	 */
	ConfigResourcesContent(final String p_key, final String p_value) {
		super.put(p_key, p_value);
	}

	/**
	 * Default constructor for the class.
	 */
	ConfigResourcesContent() {
		super();
	}

	/**
	 * Add a property in the map and do all necessary substitutions if the property
	 * has substitution key(s).
	 *
	 * @param p_key        the key to retrieve the property.
	 * @param p_value      the associated value for the key.
	 * @param p_properties the list of all properties to retrieve the associated
	 *                     value for a substitution key if needed for a property
	 * @return the previous value associated with {@code p_key}, or {@code null} if
	 *         there was no mapping for {@code p_key}. (A {@code null} return can
	 *         also indicate that the map previously associated {@code null} with
	 *         {@code p_key}.)
	 * @throws Spi4jConfigException any exception related to the retrieval of a
	 *                              configuration resource.
	 */
	private String putProperty(final String p_key, final String p_value, final Map<Object, Object> p_properties)
			throws Spi4jConfigException {
		final Matcher substitutionKeyMatcher = _pattern.matcher(p_value);

		if (substitutionKeyMatcher.find()) {
			final String substitutionKey = getSubstitutionKey(substitutionKeyMatcher.group(1), p_value, p_properties);
			final String substitutionValue = String.valueOf(p_properties.get(substitutionKeyMatcher.group(1)));
			return putProperty(p_key, p_value.replaceAll(substitutionKey, substitutionValue), p_properties);
		}
		return super.put(p_key, p_value);
	}

	/**
	 * Check key existence and wrap the key with regex pattern (ready to use for
	 * replaceAll() method).
	 *
	 * @param p_substitutionKey the key for getting the associated value and making
	 *                          the substitution.
	 * @param p_value           the initial value where substitutions have been
	 *                          detected (just used for a more readable exception).
	 * @param p_properties      the list of all properties inn order to retrieve the
	 *                          associated value for a substitution key if needed
	 *                          for a property
	 * @return the substitution key wrapped with regex pattern.
	 * @throws Spi4jConfigException any exception related to the retrieval of a
	 *                              configuration resource.
	 */
	private String getSubstitutionKey(final String p_substitutionKey, final String p_value,
			final Map<Object, Object> p_properties) throws Spi4jConfigException {
		if (!p_properties.containsKey(p_substitutionKey)) {
			throw new Spi4jConfigException(
					"Impossible de compléter la valeur ''{0}'' avec la clé de configuration ''{1}''.", p_value,
					p_substitutionKey);
		}
		return c_prop_subst_key_rgx_begin + p_substitutionKey + c_prop_subst_key_rgx_end;
	}

	/**
	 *
	 * @param p_map                   the list of properties (key/value paires)
	 *                                issued from the configuration resource and to
	 *                                concat to the global map of properties.
	 * @param p_resourceReaderHandler the resource reader handler which manage the
	 *                                specific property key.
	 */
	void putAll(final Map<String, String> p_map, final ConfigResourcesHandler p_resourceReaderHandler) {
		for (final Entry<String, String> v_property : p_map.entrySet()) {
			super.put(v_property.getKey(), v_property.getValue());
			if (null != p_resourceReaderHandler.getResourcesReloader()) {
				_propertiesToRefresh.put(v_property.getKey(), p_resourceReaderHandler);
			}
		}
	}

	/**
	 * Retrieve the map for the properties that are subject to a refresh request.
	 *
	 * @return the list of keys for all properties that are subject to a refresh
	 *         request. Each key is associated to the resource reader which handle
	 *         the property.
	 */
	Map<String, ConfigResourcesHandler> getPropertiesToRefresh() {
		return _propertiesToRefresh;
	}
}
