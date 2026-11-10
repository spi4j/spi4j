/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * Small enumeration for the four authentication token types managed by spi4j.
 *
 * @author MINARM.
 */
public enum RsTokenType_Enum {
	/**
	 * Bearer access token (jwt / oauth2).
	 */
	access,

	/**
	 * id token (oidc).
	 */
	id,

	/**
	 * refresh token.
	 */
	refresh,

	/**
	 * applicative token (not for authentication).
	 */
	applicative;
}
