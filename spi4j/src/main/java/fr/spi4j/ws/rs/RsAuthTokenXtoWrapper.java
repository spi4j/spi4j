/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import fr.spi4j.ws.rs.exception.RsUnexpectedException;
import io.jsonwebtoken.Claims;

/**
 * Specific wrapper for the new application token to send to the client. Used
 * for apiKey protocol only.
 *
 * @author MINARM
 */
public class RsAuthTokenXtoWrapper {

	/**
	 * The access token.
	 */
	private final String _accessToken;

	/**
	 * The expiration in milliseconds for the access token.
	 */
	private final long _accesTokenExpiresIn;

	/**
	 * The type for the access token;
	 */
	private String _accessTokenType;

	/**
	 * The refresh token.
	 */
	private String _refreshToken;

	/**
	 * The expiration in milliseconds for the refresh token.
	 */
	private long _refreshTokenExpiresIn;

	/**
	 * Private constructor.
	 */
	private RsAuthTokenXtoWrapper(final String p_spi4jId, final String p_refreshSpi4jId, final Claims p_claims,
			final Claims p_refreshClaims) {
		// Get the copy for the access token.
		final RsToken v_token = getTokenDefinition(p_spi4jId, p_claims);

		// Ask for the access token creation.
		_accessToken = RsTokenHelper.createToken(v_token);
		_accesTokenExpiresIn = get_expirationInSeconds(v_token);
		_accessTokenType = v_token.get_prefixedBy().trim();

		// Ask for a refresh token creation.
		if (null != p_refreshSpi4jId) {
			// Get the copy for the refresh token (if exists).
			final RsToken v_refreshToken = getTokenDefinition(p_refreshSpi4jId, p_refreshClaims);

			v_refreshToken.get_claims().put("rand", v_refreshToken.get_randomString());
			_refreshToken = RsTokenHelper.createRefreshToken(v_refreshToken);
			_refreshTokenExpiresIn = get_expirationInSeconds(v_refreshToken);
		}
	}

	/**
	 * Retrieve the copy of the token definition.
	 *
	 * @param p_spi4jId : The internal spi4j id for the access token.
	 * @param p_claims  : The payload for the token.
	 * @return The access token.
	 */
	private RsToken getTokenDefinition(final String p_spi4jId, final Claims p_claims) {

		// Get the tokens container.
		final RsTokensContainer v_container = RsFilter_Abs.get_config().get_tokensContainer();

		// Retrieve the copy of the token definition.
		final RsToken v_token = v_container.get_tokenCopy(p_spi4jId);

		// Check the existence for objects.
		if (null != v_token && null != p_claims) {
			// Complete the token with the claims.
			v_token.set_claims(p_claims);

			// Return the completed token.
			return v_token;
		}

		// No token definition -> exit with error.
		throw new RsUnexpectedException("Impossible de trouver la définition du jeton !");
	}

	/**
	 * Calculate the offset for the expiration time in seconds.
	 *
	 * @param p_token : The token with all parameters.
	 * @return The offset for the expiration time in seconds.
	 */
	private long get_expirationInSeconds(final RsToken p_token) {
		long v_expires = p_token.get_expirationNbMin() * 60;

		if (v_expires == 0) {
			v_expires = p_token.get_expirationNbHour() * 3600;
		}
		return v_expires;
	}

	/**
	 * Ask for an xto wrapper construction.
	 *
	 * @param p_spi4jId : The internal spi4j id for the acess token.
	 * @param p_claims  : The token parameters object.
	 * @return The xto wrapper for authentication token.
	 */
	public static RsAuthTokenXtoWrapper create(final String p_spi4jId, final Claims p_claims) {
		// Create the xto container for the token.
		return new RsAuthTokenXtoWrapper(p_spi4jId, null, p_claims, null);
	}

	/**
	 * Ask for an xto wrapper construction.
	 *
	 * @param p_spi4jId        : The internal spi4j id for the access token.
	 * @param p_refreshSpi4jId : The internal spi4j id for the refresh token.
	 * @param p_claims         : The token parameters object.
	 * @param p_refreshClaims  : The refresh token parameters object.
	 * @return The xto wrapper for authentication token and refresh token.
	 */
	public static final RsAuthTokenXtoWrapper create(final String p_spi4jId, final String p_refreshSpi4jId,
			final Claims p_claims, final Claims p_refreshClaims) {
		// Create the xto container for the token(s).
		return new RsAuthTokenXtoWrapper(p_spi4jId, p_refreshSpi4jId, p_claims, p_refreshClaims);
	}

	/**
	 * A "Bearer Token" is a JSON Web Token whose role is to indicate that the user
	 * who accesses the resources is properly authenticated.
	 *
	 * @return The type for the token.
	 */
	public String get_accessTokenType() {
		return _accessTokenType;
	}

	/**
	 * Set the API fluent...
	 *
	 * @param p_accessTokenType : The type of token.
	 * @return The container.
	 */
	public RsAuthTokenXtoWrapper set_accessTokenType(final String p_accessTokenType) {
		_accessTokenType = p_accessTokenType;
		return this;
	}

	/**
	 * @return The token.
	 */
	public String get_accesstoken() {
		return _accessToken;
	}

	/**
	 * @return The expiration in seconds.
	 */
	public long get_accesTokenExpiresIn() {
		return _accesTokenExpiresIn;
	}

	/**
	 * @return The token.
	 */
	public String get_refreshToken() {
		return _refreshToken;
	}

	/**
	 * @return The expiration in seconds.
	 */
	public long get_refreshTokenExpiresIn() {
		return _refreshTokenExpiresIn;
	}
}
