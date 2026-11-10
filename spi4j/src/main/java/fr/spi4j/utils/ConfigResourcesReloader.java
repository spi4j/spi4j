/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

/**
 * Main interface for all refresh strategy (also called configuration reloader)
 * which is bounded with a reader handler. Any developer who want to create a
 * new custom reload strategy should implements this interface in order to make
 * his reload strategy correctly handled by the frameworK.
 *
 * @author MinArm
 */
public interface ConfigResourcesReloader extends ComponentObservable {

	/**
	 * Start the refresh strategy for the reader handler (if needed).
	 *
	 * @param p_resourcesContent      the main container for the properties (all
	 *                                resources of all reader handlers).
	 * @param p_resourceReaderHandler the technical reader handler for the
	 *                                associated configuration resources.
	 * @throws Exception any unknown exception to be catch and handled by the upper
	 *                   classes.
	 */
	void start(final ConfigResourcesContent p_resourcesContent, final ConfigResourcesHandler p_resourceReaderHandler)
			throws Exception;
}
