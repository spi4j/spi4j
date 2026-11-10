/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * Must be completed with all flows...
 *
 * @author MINARM
 */
public enum RsAuthFlow_Enum {

	/**
	 * Default flow for authorization code.
	 */
	authorizationCode("AUTHORIZATIONCODE", "code", RsAuthGrantType_Enum.authorizationCode),

	/**
	 * Default flow for implicit.
	 */
	implicit("IMPLICIT", "token+id_token", RsAuthGrantType_Enum.implicit),

	/**
	 * Default flow for password credential.
	 */
	ownerPasswordCredential("PASSWORD", "password", RsAuthGrantType_Enum.password),

	/**
	 * Default flow for credential.
	 */
	clientCredential("CREDENTIALS", "client_credentials", RsAuthGrantType_Enum.clientCredentials),

	/**
	 * Default flow for hybrid.
	 */
	hybrid("NOT IMPLEMENTED", "", null), // Not implemented yet

	/**
	 * Default flow for code (authorization code).
	 */
	code("NOT IMPLEMENTED", "code", RsAuthGrantType_Enum.authorizationCode), // Not implemented yet

	/**
	*
	*/
	access_token("", "token", null), // Not implemented yet

	/**
	 * Default flow for id token.
	 */
	id_token("NOT IMPLEMENTED", "id_token", null), // Not implemented yet

	/**
	 * No default flow.
	 */
	none("NOT IMPLEMENTED", "none", null); // Not implemented yet

	/**
	 * The enumeration under OBEO format.
	 */
	final private String _obeoAuthFlow;

	/**
	 * The value(s) to pass to the authentication server.
	 */
	final private String _value;

	/**
	 * The associated default grant type.
	 */
	final private RsAuthGrantType_Enum _defaultGrantType;

	/**
	 * Constructor.
	 *
	 * @param p_obeoAuthFlow : The Obeo enumeration for passing mode.
	 * @param p_value        : The string value(s) for the flow.
	 * @param p_grantType    : The default associated grant type.
	 */
	RsAuthFlow_Enum(final String p_obeoAuthFlow, final String p_value, final RsAuthGrantType_Enum p_grantType) {
		_obeoAuthFlow = p_obeoAuthFlow;
		_defaultGrantType = p_grantType;
		_value = p_value;
	}

	/**
	 * Retrieve the associated default value for the flow.
	 *
	 * @return The string value(s) for the requested flow.
	 */
	public String get_value() {
		return _value;
	}

	/**
	 * Retrieve the default associated grant type for the flow.
	 *
	 * @return The default associated grant type.
	 */
	public RsAuthGrantType_Enum get_defaultGrantType() {
		return _defaultGrantType;
	}

	/**
	 * Make the conversion between obeo and spi4j enumeration and retrieve the
	 * enumeration.
	 *
	 * @param p_obeoAuthFlow : The obeo enumeration for the authentication flow.
	 * @return The internal spi4j enumeration for the authentication flow.
	 */
	public static RsAuthFlow_Enum get_authFlowFromObeo(final String p_obeoAuthFlow) {
		for (final RsAuthFlow_Enum v_enum : RsAuthFlow_Enum.values()) {
			if (v_enum._obeoAuthFlow.equals(p_obeoAuthFlow)) {
				return v_enum;
			}
		}
		// Should throw an exception.
		return null;
	}
}
