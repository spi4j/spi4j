/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * Store the different modes for application routing.
 *
 * @author MINARM
 */
public enum RsFilterRouting_Enum {

	/**
	 * The application has no gateway.
	 */
	standalone,

	/**
	 * The application is behind a standard gateway.
	 */
	api_gateway,

	/**
	 * The application is behind a specific gateway (MINARM).
	 */
	pem_gateway,

	/**
	 * The application is behind a specific gateway (MINARM).
	 */
	papi_gateway
}
