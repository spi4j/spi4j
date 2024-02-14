/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is a sort of wrapper for the reader and all his associated
 * configuration resources. This handler manage a technical configuration reader
 * with his proper configuration (reload strategy and encoding) and one or more
 * configuration resources.
 *
 * @author MinArm
 */
@SuppressWarnings("javadoc")
class ConfigResourcesHandler {

	/**
	 * The character encoding for all resources handled by the reader. If not
	 * supplied, the handler use the default encoding previously registered in the
	 * reader.
	 */
	@SuppressWarnings("unused")
	private Charset _resourceEncoding;

	/**
	 * The technical reader for the associated list of resources. The handler will
	 * use this registered reader to load and parse all the associated configuration
	 * resources.
	 */
	private final ConfigResourceReader _resourcesAssessor;

	/**
	 * The refresh strategy associated to the reader (this is optional). If
	 * presents, the reader will use this strategy to reload the associated
	 * configuration resources and refresh all concerned variables of the target
	 * application.
	 */
	private ConfigResourcesReloader _resourcesReloader;
	/**
	 * The developer has the ability add any "base" information for the
	 * configuration resource. This can be anything depending of the type of the
	 * resource. For example, for a resource of type "file", it could be the base
	 * path of the resource.
	 */
	private String _resourcesBase;

	/**
	 * The list of all associated configuration resources to be handled by the
	 * reader. All associated resources are necessarily of the same type for a same
	 * reader handler as there is only one reader for a reader handler.
	 */
	private final List<String> _resources = new ArrayList<>();

	/**
	 * Constructor for the reader/writer technical handler. As for now, the resource
	 * encoding is not yet provided, the suggested default reader encoding is used.
	 * By default the object is necessarily a reader, but it could be also a
	 * writer...
	 *
	 * @param p_resourcesAccessor the reader/writer to handle for all associated
	 *                            resources.
	 */
	ConfigResourcesHandler(final ConfigResourceReader p_resourcesAccessor) {
		_resourceEncoding = p_resourcesAccessor.getDefaultEncoding();
		_resourcesAssessor = p_resourcesAccessor;
	}

	/**
	 * Add a configuration resource to be handled by the reader. The resource may be
	 * a file path, a connection string or a jndi name for a datasource, directly a
	 * variable key, or anything else that could be needed by the reader.
	 *
	 * @param resource the specific resource to be handled by the reader.
	 */
	void addResource(final String resource) {
		_resources.add(resource);
	}

	/**
	 * Retrieve the list of all the resources handled by this configuration
	 * resources handler.
	 *
	 * @return the list of the resources handled by this class.
	 */
	List<String> getResources() {
		return _resources;
	}

	/**
	 * Retrieve the associated reader for all the configuration resources handled by
	 * this configuration resources handler.
	 *
	 * @return the associated reader for all the resources handled by this class.
	 */
	ConfigResourceReader getResourcesAssessor() {
		return _resourcesAssessor;
	}

	/**
	 * Set the refresh strategy for the resources reader.
	 *
	 * @param p__resourcesReloader the specific refresh strategy for the resources.
	 */
	void setResourceReloader(final ConfigResourcesReloader p_resourcesReloader) {
		_resourcesReloader = p_resourcesReloader;
	}

	/**
	 * Retrieve the refresh strategy for the resource reader handler. If no refresh
	 * strategy is specified, return a <code>null</code> value.
	 *
	 * @return the associated refresh strategy for the reader and all the associated
	 *         configuration resources.
	 */
	public ConfigResourcesReloader getResourcesReloader() {
		return _resourcesReloader;
	}

	/**
	 * Override the new encoding for all the resources associated to the reader.
	 *
	 * @param resourceEncoding if defined, the specific character encoding for all
	 *                         the resources associated and handled by the reader.
	 *                         If not provided, the suggested default reader
	 *                         encoding is used.
	 */
	void setEncoding(final Charset p_resourceEncoding) {
		_resourceEncoding = p_resourceEncoding;
	}

	/**
	 * Set the base information (optional) for all the resources handled by this
	 * class. If not null, this information can be anything depending of the type of
	 * the configuration resource. For example, in case of file resource, it could
	 * be the base path to concat with the name of the resource in order to obtain
	 * the fully absolute path of the resource..
	 *
	 * @param p_resourcesBasePath the base path retrieved from environment variable.
	 */
	void setResourcesBase(final String p_resourcesBase) {
		_resourcesBase = p_resourcesBase;
	}

	/**
	 * Retrieve the resource bas information for all the configuration resources
	 * handled by this configuration resource handler.
	 *
	 * @return the resource base for the configuration resources or null if not
	 *         defined.
	 */
	String getResourcesBase() {
		return _resourcesBase;
	}

	/**
	 * Unique external entry point for the class. Initiates the loading and reading
	 * of all associated configuration resources in order to add all concerned
	 * properties to the main container. If a specific refresh strategy is defined
	 * for the reader, start the reload strategy.
	 *
	 * @param resourcesContent the container for all properties with their value.
	 * @throws Exception any exception thrown by the treatment.
	 */
	void init(final ConfigResourcesContent p_resourcesContent) throws Exception {
		readResources(p_resourcesContent);
	}

	/**
	 * This is the main method for the class. Read all associated configuration
	 * resources with the help of the registered reader and store the result in a
	 * <code>ConfigResourceContent</code>.
	 *
	 * @param p_resourcesContent the container for all properties with their value.
	 * @throws Exception any exception thrown by the treatment.
	 */
	private void readResources(final ConfigResourcesContent p_resourcesContent) throws Exception {
		for (final String v_resource : _resources) {
			p_resourcesContent.putAll(_resourcesAssessor.readContent(v_resource, Optional.ofNullable(_resourcesBase)),
					this);
			if (null != _resourcesReloader) {
				_resourcesReloader.start(p_resourcesContent, this);
			}
		}
	}
}
