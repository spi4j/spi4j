/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

/**
 * Get all properties from a ".properties" file.
 *
 * @author MinArm
 */
public class ConfigPropertiesReader implements ConfigResourceReader {

	/**
	 * The name of the resource under string format.
	 */
	String _resourceToString;

	/**
	 * As this configuration resource may be also used to write properties, the
	 * container is an instance variable and can be also accessed by the writer
	 * class.
	 */
	final Properties _properties = new Properties();

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
		try (FileReader v_fileReader = new FileReader(new File(_resourceToString))) {
			_properties.clear(); // If many resources, the load method concat all properties.
			_properties.load(v_fileReader);
			return new ConfigResourcesContent(_properties);
		}
	}

	/**
	 * Get the specified default encoding for the reader.
	 *
	 * @return The suggested default encoding for the reader.
	 */
	@Override
	public Charset getDefaultEncoding() {
		return StandardCharsets.ISO_8859_1;
	}

	/**
	 * Get the name of the resource under string format.
	 *
	 * @return the name of the resource.
	 */
	@Override
	public String getIdentification() {
		return _resourceToString;
	}
}
