/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * Store the different flows used for authentication protocol.
 *
 * @author MINARM
 */
public enum RsAuthGrantType_Enum {

	/**
	 * Legacy, old flow should not be used for new projects;
	 */
	implicit("implicit"),

	/**
	 * Main flow, should be preferred for all new projects.
	 */
	authorizationCode("authorization_code"),

	/**
	 * Not commented yet.
	 */
	clientCredentials("client_credentials"),

	/**
	 * Not commented yet.
	 */
	password("password"),

	/**
	 * Specific flow for refresh token.
	 */
	refreshToken("refresh_token");

	/**
	 * The requested value for the request.
	 */
	private final String _value;

	/**
	 * Constructor.
	 *
	 * @param p_value : The requested value for the request.
	 */
	RsAuthGrantType_Enum(final String p_value) {
		_value = p_value;
	}

	/**
	 * Retrieve the requested value for the enumeration in the request.
	 *
	 * @return The requested value.
	 */
	String get_value() {
		return _value;
	}
}
