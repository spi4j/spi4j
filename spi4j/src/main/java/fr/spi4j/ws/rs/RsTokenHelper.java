/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import fr.spi4j.ws.rs.exception.RsTokenRecoveryException;
import fr.spi4j.ws.rs.exception.RsUnexpectedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

/**
 * <ul>
 * <li>iss: Principal that issued the token.</li>
 * <li>sub: Principal that is the subject of the JWT.</li>
 * <li>exp: Expiration date for the token.</li>
 * <li>nbf: Time on which the token will start to be accepted for
 * processing.<:li>
 * <li>iat: Time on which the token was issued.</li>
 * <li>jti: Unique identifier for the token.</li>
 * </ul>
 * <p>
 * It’s very important to understand that the signature does not provide
 * confidentiality. The signature just guarantees that the token hasn’t been
 * tampered. A JWT must be encrypted to send sensitive information. If the JWT
 * has been tampered with in any way, parsing the claims will throw a
 * SignatureException and the value of the subject variable will stay HACKER.
 *
 * @author MINARM
 */
public final class RsTokenHelper {

	/**
	 * Specific logger for this utility class.
	 */
	private static final Logger c_log = LogManager.getLogger(RsTokenHelper.class);

	/**
	 * Private constructor. (Only static methods).
	 */
	private RsTokenHelper() {
		super();
	}

	/**
	 * Request for any token(s) from authentication server (OAUTH2 / OIDC). The
	 * request is based on data issued from the configuration of a specific token.
	 * The configuration of the token was generated specifically for a project.
	 *
	 * @param p_connector : The connector for the token(s).
	 *
	 * @return A list of tokens in a Json object (depending the initial requested
	 *         protocol).
	 */
	public static JSONObject get_tokens(final RsTokenConnector_Itf p_connector) {

		BufferedWriter v_writer = null;
		HttpURLConnection v_conn = null;

		try {
			final String v_tokenEndPoint = p_connector.get_tokenConfig().get_serverTokenEndPoint();
			v_conn = (HttpURLConnection) new URL(v_tokenEndPoint).openConnection();
			v_conn.setReadTimeout(p_connector.get_tokenConfig().get_readTimeout());
			v_conn.setConnectTimeout(p_connector.get_tokenConfig().get_connectTimeout());
			v_conn.setUseCaches(Boolean.FALSE);
			v_conn.setDoInput(Boolean.TRUE);
			v_conn.setDoOutput(Boolean.TRUE);
			v_conn.setRequestMethod("POST");

			// Add all necessary default header parameters.
			v_conn.setRequestProperty(RsConstants.c_auth_header_cache_control, "no-cache");
			v_conn.setRequestProperty(RsConstants.c_auth_header_accept, "application/json");
			v_conn.setRequestProperty(RsConstants.c_auth_header_content_type, "application/x-www-form-urlencoded");

			// Add specific client id and client secret to header (base 64 / basic
			// authentication).
			v_conn.setRequestProperty(RsConstants.c_auth_header_authorization,
					new RsAuthCredentials(p_connector.get_tokenConfig().get_clientId(),
							p_connector.get_tokenConfig().get_clientSecret()).get_credentials());

			// Add all necessary header parameters, depending of the selected flow.
			for (final Entry<String, String> v_entry : p_connector.get_headerParams().entrySet()) {
				v_conn.setRequestProperty(v_entry.getKey(), v_entry.getValue());
			}

			// Add all necessary parameters, depending of the selected flow.
			p_connector.get_params().put(RsConstants.c_param_oauth2_grant_type,
					p_connector.get_tokenConfig().get_authGrant());
			v_writer = new BufferedWriter(new OutputStreamWriter(v_conn.getOutputStream(), StandardCharsets.UTF_8));
			v_writer.write(RsUtils.convertParamsToHttpString(p_connector.get_params()));

			// Connect to authentication server.
			v_writer.flush();
			v_conn.connect();

			if (HttpURLConnection.HTTP_OK == v_conn.getResponseCode()) {
				return p_connector
						.get_tokens(new JSONObject(new BufferedReader(new InputStreamReader(v_conn.getInputStream()))
								.lines().collect(Collectors.joining())));

			} else {
				final JSONObject jsonError = new JSONObject(
						new BufferedReader(new InputStreamReader(v_conn.getErrorStream())).lines()
								.collect(Collectors.joining()));

				throw new RsTokenRecoveryException(
						"Récupération du/des jeton(s)." + " Le serveur d'authentification a retourné le code : "
								+ v_conn.getResponseCode() + " avec le(s) message(s) suivant(s) : "
								+ jsonError.toMap().toString().replaceAll("=", " -> "));
			}

		} catch (final RsTokenRecoveryException p_e) {

			throw p_e;

		} catch (final Exception p_e) {

			throw new RsUnexpectedException(
					"Une erreur est apparue dans la récupération pour le(s) jeton(s) : " + p_e.getMessage());

		} finally {

			if (v_writer != null) {
				try {
					v_writer.close();
				} catch (final IOException e) {
					c_log.error("Problème de fermeture pour le writer de la connexion http.");
				}
			}
			if (v_conn != null) {
				try {
					v_conn.disconnect();
				} catch (final Exception e) {
					c_log.error("Problème de fermeture pour la connexion http.");
				}
			}
		}
	}

	/**
	 * Decode the base 64 string application credentials from the headers.
	 *
	 * @param p_credentials : The application credentials (username / password).
	 * @return An object for user name and password storage.
	 * @throws InvalidParameterException : Technical error for decoding credentials.
	 */
	public static RsAuthCredentials decodeCredentials(final String p_credentials) throws InvalidParameterException {
		if (null == p_credentials || p_credentials.isEmpty()
				|| !p_credentials.toLowerCase().startsWith(RsConstants.c_auth_header_basic)) {
			throw new InvalidParameterException();
		}

		// Authorization: Basic base64credentials.
		final String v_base64Credentials = p_credentials.substring(RsConstants.c_auth_header_basic.length()).trim();
		final byte[] v_credentialsDecoded = Base64.getDecoder().decode(v_base64Credentials);
		final String v_strCredentials = new String(v_credentialsDecoded, StandardCharsets.UTF_8);

		// Credentials = username:password.
		final String[] v_credentialValues = v_strCredentials.split(RsConstants.c_auth_header_credentials_separator, 2);
		final RsAuthCredentials v_credentials = new RsAuthCredentials(v_credentialValues);
		return v_credentials;
	}

	/**
	 * The access token allows the client to access the protected resource. This
	 * token has a limited period of validity and may have a limited scope. This
	 * method is primarily used to serve an access token but it can be used also for
	 * an applicative token.
	 *
	 * @param p_token : The token with specific values for the token creation.
	 * @return String containing all the values stored in the object application
	 *         token.
	 * @throws InvalidParameterException : Technical error when coding the
	 *                                   application token.
	 */
	static String createToken(final RsToken p_token) {
		// Set the JWT Claims and serializes it to a compact, URL-safe string...
		// Add the required claims first, then the 'standard' claims.
		return Jwts.builder().setIssuedAt(new Date()).addClaims(p_token.get_requiredClaims())
				.addClaims(p_token.get_claims()).setExpiration(calcExpirationTime(p_token))
				.setHeaderParam(RsConstants.c_token_key_id, p_token.get_readSigningKeyId())
				.signWith(RsSigningKeyHelper.get_signingKeyForCreation(p_token.get_createSigningKeyId()),
						p_token.get_signingKeyAlgorithm().get_algorithm())
				.compressWith(CompressionCodecs.DEFLATE).compact();
	}

	/**
	 * The refresh token allows the client to get a new access token. This token
	 * must have a long period of validity.
	 *
	 * @param p_refreshToken : The definition of the refresh token with all the
	 *                       requested values.
	 * @return String containing all the values stored in the object application
	 *         refresh token.
	 * @throws InvalidParameterException : Technical error when coding the
	 *                                   application token.
	 */
	static String createRefreshToken(final RsToken p_refreshToken) {
		// Notice that refresh token is an oauth2 concept, not a jwt concept.
		// For now, just a redirection to a standard token creation.
		return createToken(p_refreshToken);
	}

	/**
	 * Decode the received token from the client.
	 *
	 * @param p_token                   : The token definition with his base64
	 *                                  string.
	 * @param p_allowedClockSkewSeconds : The number of seconds to tolerate for
	 *                                  clock skew when verifying exp or nbf claims.
	 * @return Claims containing all the values stored in the string application
	 *         token.
	 */
	static Claims decodeToken(final RsToken p_token, final String p_tokenToDecode,
			final int p_allowedClockSkewSeconds) {
		// Get the JWT claims and validate the token.
		final Claims v_claims = Jwts.parserBuilder().setAllowedClockSkewSeconds(p_allowedClockSkewSeconds)
				.setSigningKeyResolver(RsSigningKeyHelper.get_signinKeyResolver()).build()
				.parseClaimsJws(p_tokenToDecode).getBody();

		// Check all the required claims and exit with exception....
		checkAllRequiredClaims(v_claims, p_token.get_requiredClaims());

		// Return the claims (payload)
		// for the token.
		return v_claims;
	}

	/**
	 * Check the existence and the value if needed for each required claim.
	 *
	 * @param p_claims         : The decoded claims to be checked in the token.
	 * @param p_requiredClaims : The required claims defined pad the developer.
	 */
	private static void checkAllRequiredClaims(final Claims p_claims, final Claims p_requiredClaims) {
		for (final Entry<String, Object> v_entry : p_requiredClaims.entrySet()) {
			final Object v_value = p_claims.get(v_entry.getKey());
			if (!RsConstants.c_joker.equals(v_entry.getValue()) && !v_entry.getValue().equals(v_value)) {
				throw new JwtException("Une donnée obligatoire n'est pas valide pour le jeton!");
			} else if (null == v_value) {
				throw new JwtException("Une donnée obligatoire n'est pas trouvée dans le jeton !");
			}
		}
	}

	/**
	 * Calculate the expiration date for the refresh token (old JDK 1.7).
	 *
	 * @param p_token : The token with all required informations.
	 * @return The calculated expiration date for the refresh token.
	 */
	private static Date calcExpirationTime(final RsToken p_token) {
		// Test if defined in minutes.
		int v_offsetToAdd = p_token.get_expirationNbMin() * 60 * 1000;

		// Test if defined in hours.
		if (v_offsetToAdd == 0) {
			v_offsetToAdd = p_token.get_expirationNbHour() * 3600 * 1000;
		}

		// Set the default defined in spi4j (should never happen !).
		if (v_offsetToAdd == 0) {
			v_offsetToAdd = Integer.valueOf(RsConstants.c_token_default_exp_nbmin) * 60 * 1000;
		}

		// Calculate the expiration time (milliseconds).
		return new Date(Calendar.getInstance().getTimeInMillis() + v_offsetToAdd);
	}

	/**
	 * Complete the builder for the token with the use of the connect URI. This URI
	 * permits to auto-discover a maximum numbers of informations for the
	 * authentication server. Placing this code here avoid cluttering the builder
	 * with code that will potentially grow. In addition, it is not desirable to
	 * have too much intelligence at builder level.
	 *
	 * @param p_builder         : The token builder.
	 * @param p_connectEndPoint : The URI for auto-discovery.
	 */
	static void completeWithRemoteParams(final RsToken.Builder p_builder, final String p_connectEndPoint) {

		c_log.info("Chargement et anayse des points de terminaison sur l'URI :" + p_connectEndPoint);

		if (null == p_connectEndPoint || p_connectEndPoint.isEmpty()) {
			throw new RsUnexpectedException(
					"Impossible de compléter le jeton avec l'URI d'auto-découverte, l'URI est nulle ou vide !");
		}

		try {
			final JSONObject v_jsonObj = new JSONObject(
					RsClientFactory.get_target(p_connectEndPoint).request().get().readEntity(String.class));
			p_builder.withAuthEndPoint((String) v_jsonObj.get(RsConstants.c_auth_oidc_server_auth));
			p_builder.withTokenEndPoint((String) v_jsonObj.get(RsConstants.c_auth_oidc_server_token));
			p_builder.withCertificateResourcePath((String) v_jsonObj.get(RsConstants.c_auth_oidc_server_certs));
			p_builder.withScopes(v_jsonObj.getJSONArray(RsConstants.c_auth_oidc_server_scopes).join(","));

		} catch (final Exception p_e) {

			throw new RsUnexpectedException(p_e,
					"Impossible de terminer la configuration de(s) jeton(s) pour le protocole OIDC sur l''URI : {0} !",
					p_connectEndPoint);
		}
	}
}
