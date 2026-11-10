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

import org.yaml.snakeyaml.Yaml;

/**
 * Get all properties from an yaml resource file. Go deeper to close resources
 * in case of exceptions ?
 *
 * @author MinArm
 */
public class ConfigYamlReader implements ConfigResourceReader {

	/**
	 * The name of the resource under string format.
	 */
	String _resourceToString;

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
		return new ConfigResourcesContent(new Yaml().load(new FileInputStream(new File(_resourceToString))));
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
		return _resourceToString;
	}
}
