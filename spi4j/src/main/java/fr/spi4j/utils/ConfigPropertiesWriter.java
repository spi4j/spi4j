/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.io.File;
import java.io.FileWriter;

/**
 * Implementation for a properties configuration resource in which the end
 * developer can write some new properties, delete or modify existing
 * properties.
 *
 * @author MinArm
 */
public class ConfigPropertiesWriter extends ConfigPropertiesReader implements ConfigResourceWriter {

	/**
	 * Add or replace a property for the specific resource. Fluent pattern.
	 *
	 * @param p_key   the unique key to identify the value of the property.
	 * @param p_value the value for the property.
	 * @return the resources writer.
	 * @throws Exception any unexpected exception.
	 */
	@Override
	public <T> ConfigPropertiesWriter addContent(final String p_key, final T p_value) throws Exception {
		_properties.put(p_key, p_value);
		return this;
	}

	/**
	 * Delete a property for the specific resource. Fluent pattern
	 *
	 * @param p_key the unique key to identify the value of the property.
	 * @return the resources writer.
	 * @throws Exception any unexpected exception.
	 */
	@Override
	public ConfigPropertiesWriter deleteContent(final String p_key) throws Exception {
		_properties.remove(p_key);
		return this;
	}

	/**
	 * Flush the content of the resource.
	 *
	 * @throws Exception any unexpected exception.
	 */
	@Override
	public void store() throws Exception {
		try (FileWriter v_fileWriter = new FileWriter(new File(_resourceToString))) {
			_properties.store(v_fileWriter, null);
		}
	}
}
