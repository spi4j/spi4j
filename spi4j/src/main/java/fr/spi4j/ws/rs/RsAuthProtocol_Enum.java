/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * Store the different protocols for authentication.
 *
 * @author MINARM
 */
public enum RsAuthProtocol_Enum {

	/**
	 * Basic protocol with only credentials.
	 */
	http(2),

	/**
	 * Protocol with token (JWT).
	 */
	apiKey(2),

	/**
	 * The Industry-standard protocol
	 */
	oauth2(1),

	/**
	 * Identity layer on top of the OAuth 2.0 protocol.
	 */
	openIdConnect(1),

	/**
	 * Not commented yet.
	 */
	none(0);

	/**
	 * The processing priority in case of multiple protocols.
	 */
	final private int _processingPriority;

	/**
	 * Constructor.
	 *
	 * @param p_processingPriority : The priority for processing.
	 */
	RsAuthProtocol_Enum(final int p_processingPriority) {
		_processingPriority = p_processingPriority;
	}

	/**
	 * Retrieve the priority for processing if multiple protocols selected.
	 *
	 * @return The processing priority.
	 */
	public int get_processingPriority() {
		return _processingPriority;
	}
}
