/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * Store the different modes for application.
 *
 * @author MINARM
 */
public enum RsFilterMode_Enum {

	/**
	 * The application is in development mode.
	 */
	debug,

	/**
	 * The application is in testing phase.
	 */
	test,

	/**
	 * The application is in integration phase.
	 */
	integ,

	/**
	 * The application is in production.
	 */
	prod
}
