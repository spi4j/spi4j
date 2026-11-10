/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

/**
 * Main interface for all resource writers implementation. Every new writer
 * created by the developer must implements this interface to be correctly
 * handled by the framework.
 *
 * @author MinArm
 */
public interface ConfigResourceWriter {

	/**
	 * Add or replace a property in the configuration resource. Fluent pattern.
	 *
	 * @param p_key   the unique key to identify the value of the property.
	 * @param p_value the value for the property.
	 * @return the resources writer.
	 * @throws Exception any unexpected exception.
	 */
	@SuppressWarnings("javadoc")
	<T> ConfigResourceWriter addContent(final String p_key, final T p_value) throws Exception;

	/**
	 * Delete a property in the configuration resource. Fluent pattern
	 *
	 * @param p_key the unique key to identify the value of the property.
	 * @return the resources writer.
	 * @throws Exception any unexpected exception.
	 */
	ConfigResourceWriter deleteContent(final String p_key) throws Exception;

	/**
	 * Flush the content of the resource. It could be in a file, a database, an
	 * external rest service, etc...
	 *
	 * @throws Exception any unexpected exception.
	 */
	void store() throws Exception;
}
