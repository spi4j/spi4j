/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

/**
 * @author MinArm
 */
public class ConfigPropertiesXmlReader implements ConfigResourceReader {

	/**
	 * The name of the resource under string format.
	 */
	private String _resourceToString;

	/**
	 * Load and read the specified resource.
	 *
	 * @param p_resource the resource, in this case a simple file path under string
	 *                   format.
	 */
	@Override
	public ConfigResourcesContent readContent(final String p_resource, final Optional<String> p_resourceBase)
			throws Exception {
		_resourceToString = ConfigFileUtils.getResource(p_resource, p_resourceBase);
		final Properties v_properties = new Properties();
		try (FileInputStream v_inputStream = new FileInputStream(new File(_resourceToString))) {
			v_properties.loadFromXML(v_inputStream);
			return new ConfigResourcesContent(v_properties);
		}
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
	 * get the name of the resource under string format.
	 *
	 * @return the name of the resource.
	 */
	@Override
	public String getIdentification() {
		return _resourceToString;
	}
}
