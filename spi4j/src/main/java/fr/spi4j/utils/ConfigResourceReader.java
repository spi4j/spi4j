/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Main interface for all resource readers implementation. Every new reader
 * created by the developer must implements this interface to be correctly
 * handled by the framework.
 *
 * @author MinArm
 */
public interface ConfigResourceReader {
	/**
	 * Load and read the associated configuration resource(s).
	 *
	 * @param p_resource     the resource to be read by the resource reader. It can
	 *                       be anything (file path, jdni name, jdbc connection
	 *                       string,...) needed by the reader in order to work
	 *                       properly with the targeted configuration resource.
	 * @param p_resourceBase an optional parameter to add any additional information
	 *                       to the configuration resource.
	 * @return the list of all properties defined in the configuration resource.
	 * @throws Exception any undesired exception.
	 */
	ConfigResourcesContent readContent(final String p_resource, final Optional<String> p_resourceBase) throws Exception;

	/**
	 * encoding is specified at reader level and not resource level as a reader is
	 * specialized for a specific configuration resource. The default encoding can
	 * however be overridden by the developer if specified with the help of the
	 * builder for the reader handler. See the example below :
	 *
	 * <pre>
	 * ConfigProvider.create(new ConfigurationHandler.Builder()
	 *    .withReader(new PropertiesReader())
	 *    .withResourceEncoding(StandardCharsets.ISO_8859_1);
	 * </pre>
	 *
	 * @return the default suggested encoding for the configuration resource.
	 */
	Charset getDefaultEncoding();

	/**
	 * Get a string with all the informations the developer considers necessary to
	 * identify the resource. It can be used (should) for displaying logs for
	 * example.
	 *
	 * @return a string with all the information needed to identify the resource.
	 */
	String getIdentification();
}
