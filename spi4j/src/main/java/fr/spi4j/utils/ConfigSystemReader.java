/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Get all properties from the system.
 *
 * @author MinArm
 */
public class ConfigSystemReader implements ConfigResourceReader, ConfigOptionalResource {

	/**
	 * The name of the resource under string format.
	 */
	private String _resourceToString;

	/**
	 * Load and read the specified resource. If no resource specified, it means we
	 * have to read the totality of the system properties.
	 *
	 * @param p_resource the resource, in this case the key of the specific property
	 *                   to be found.
	 */
	@Override
	public ConfigResourcesContent readContent(final String p_resource, final Optional<String> p_resourceBase)
			throws Exception {

		_resourceToString = p_resource;
		if (null == p_resource) {
			final ConfigResourcesContent v_sysContent = new ConfigResourcesContent();
			for (final Entry<Object, Object> v_entry : System.getProperties().entrySet()) {
				v_sysContent.put(v_entry.getKey().toString(), v_entry.getValue().toString());
			}
			return v_sysContent;
		}
		return new ConfigResourcesContent(p_resource, System.getProperty(p_resource));
	}

	/**
	 * Get the specified default encoding for the reader.
	 *
	 * @return The suggested default encoding for the reader.
	 */
	@Override
	public Charset getDefaultEncoding() {
		return StandardCharsets.UTF_8;
	}

	/**
	 * Get the name of the resource under string format.
	 *
	 * @return the name of the resource.
	 */
	@Override
	public String getIdentification() {
		if (null != _resourceToString) {
			return "system." + _resourceToString;
		}
		return "system";
	}
}
