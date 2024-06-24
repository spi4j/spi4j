/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.spi4j.ws.rs.exception.RsUnexpectedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

/**
 * Contains the full definition (configuration) for a token. This configuration
 * is specified by the developer in the final project.
 * <p>
 * This class is shared by all the processes in the filter.
 *
 * @author MINARM
 */
public class RsToken implements Cloneable {
	/**
	 * Default random length for signing key id.
	 */
	private static final int c_default_signing_random = 30;

	/**
	 * Store all String properties in tab to avoid deep copy ! Shallow copy.
	 */
	private final String[] _properties;

	/**
	 * The claims for the token.Make a deep copy !!
	 */
	private Claims _claims;

	/**
	 * The required claims for the token.Shallow copy.
	 */
	private final Claims _requiredClaims;

	/**
	 * The type (mode) for passing the token.Shallow copy.
	 */
	private final RsPassingMode_Enum _passingMode;

	/**
	 * The signature algorithm for the token. Shallow copy.
	 */
	private final RsSigningAlgo_Enum _signingKeyAlgorithm;

	/**
	 * The type for the token (access, id , refresh, applicative). Shallow copy.
	 */
	private final RsTokenType_Enum _type;

	/**
	 * The authentication protocol. Shallow copy.
	 */
	private final RsAuthProtocol_Enum _authProtocol;

	/**
	 * The loading mode for the signing key. Shallow copy.
	 */
	private final RsSigningKeysLoad_Enum _signingKeyLoad;

	/**
	 * The authentication flow. Shallow copy.
	 */
	private final RsAuthFlow_Enum _authFlow;

	/**
	 * The required authentication grant type (if needed). Shallow copy.
	 */
	private final RsAuthGrantType_Enum _authGrantType;

	/**
	 * @param p_spi4jId               : ras.
	 * @param p_type                  : ras.
	 * @param p_passingMode           : ras.
	 * @param p_key                   : ras.
	 * @param p_prefixedBy            : ras.
	 * @param p_expirationNbMin       : ras.
	 * @param p_expirationNbHour      : ras.
	 * @param p_signingKeyAlgorithm   : ras.
	 * @param p_clientId              : ras.
	 * @param p_clientSecret          : ras.
	 * @param p_serverAuthEndPoint    : ras.
	 * @param p_serverTokenEndPoint   : ras.
	 * @param p_serverUserEndPoint    : ras.
	 * @param p_serverLogoutEndPoint  : ras.
	 * @param p_certResourcePath      : ras.
	 * @param p_authProtocol          : ras.
	 * @param p_signingkeyLoad        : ras.
	 * @param p_callback              : ras.
	 * @param p_scopes                : ras.
	 * @param p_randomStringLength    : ras.
	 * @param p_authFlow              : ras.
	 * @param p_authGrantType         : ras.
	 * @param p_requiredClaims        : ras.
	 * @param p_readTimeout           : ras.
	 * @param p_connectTimeOut        : ras.
	 * @param p_application_secret    : ras.
	 * @param p_serverConnectEndPoint : ras.
	 */
	private RsToken(final String p_spi4jId, final String p_linkedSpi4jId, final String p_loadSigningKeys,
			final RsTokenType_Enum p_type, final RsPassingMode_Enum p_passingMode, final String p_key,
			final String p_prefixedBy, final String p_expirationNbMin, final String p_expirationNbHour,
			final RsSigningAlgo_Enum p_signingKeyAlgorithm, final String p_clientId, final String p_clientSecret,
			final String p_serverAuthEndPoint, final String p_serverTokenEndPoint, final String p_serverUserEndPoint,
			final String p_serverLogoutEndPoint, final String p_serverRefreshEndPoint, final String p_certResourcePath,
			final RsAuthProtocol_Enum p_authProtocol, final RsSigningKeysLoad_Enum p_signingkeyLoad,
			final String p_callback, final String p_scopes, final String p_randomStringLength,
			final RsAuthFlow_Enum p_authFlow, final RsAuthGrantType_Enum p_authGrantType, final Claims p_requiredClaims,
			final String p_readTimeout, final String p_connectTimeOut, final String p_application_secret,
			final String p_serverConnectEndPoint) {
		// Init the claims...
		_claims = new DefaultClaims();
		// Init the Strings tab...
		_properties = new String[24];

		// The expiration time in hours.
		_properties[14] = p_expirationNbHour;
		// The expiration time in minutes.
		_properties[13] = p_expirationNbMin;
		// The length for the random String for refresh token.
		_properties[15] = p_randomStringLength;
		// If oidc / oauth2, the /token endpoint.
		_properties[10] = p_serverTokenEndPoint;
		// If oidc / oauth2, the /auth endpoint.
		_properties[9] = p_serverAuthEndPoint;
		// If oidc / oauth2, the /user endpoint.
		_properties[11] = p_serverUserEndPoint;
		// If oidc / oauth2, the /logout endpoint.
		_properties[16] = p_serverLogoutEndPoint;
		// Indicate if the token has associated signing key to load.
		_properties[17] = p_loadSigningKeys;
		// If oidc / oauth2, the /refresh endpoint.
		_properties[18] = p_serverRefreshEndPoint;
		// If refresh token, the link with the access token.
		_properties[19] = p_linkedSpi4jId;
		// If oidc / oauth2, the secret for the client (application).
		_properties[5] = p_clientSecret;
		// If oidc / oauth2, the id for the client (application).
		_properties[6] = p_clientId;
		// If oidc / oauth2, the /user endpoint.
		_properties[12] = p_certResourcePath;
		// The prefix for the token (bearer, etc..)
		_properties[8] = p_prefixedBy;
		// A private application id for the token.
		_properties[0] = p_spi4jId;
		// If oidc / oauth2, the callback URI under String format.
		_properties[7] = p_callback;
		// The list of scopes for the token.
		_properties[1] = p_scopes;
		// The key (parameter name) to find the required token.
		_properties[2] = p_key;
		// If oidc / oauth2, the reading delay before timeout.
		_properties[20] = p_readTimeout;
		// If oidc / oauth2, the connecting delay before timeout.
		_properties[21] = p_connectTimeOut;
		// If credential grant, internal secret for the application.
		_properties[22] = p_application_secret;
		// If oidc the url connect endpoint for autodiscovery.
		_properties[23] = p_serverConnectEndPoint;

		// Init complex objects.
		_signingKeyAlgorithm = p_signingKeyAlgorithm;
		_signingKeyLoad = p_signingkeyLoad;
		_authProtocol = p_authProtocol;
		_authGrantType = p_authGrantType;
		_requiredClaims = p_requiredClaims;
		_passingMode = p_passingMode;
		_authFlow = p_authFlow;
		_type = p_type;
	}

	/**
	 * Retrieve the private application id for the token.
	 *
	 * @return the private application id for the token.
	 */
	public String get_spi4jId() {
		return _properties[0];
	}

	/**
	 * Retrieve the list of required claims.
	 *
	 * @return The list of all required claims.
	 */
	Claims get_requiredClaims() {
		return _requiredClaims;
	}

	/**
	 * Retrieve the mode of passing for the token.
	 *
	 * @return Passing mode.
	 */
	RsPassingMode_Enum get_passingMode() {
		return _passingMode;
	}

	/**
	 * Retrieve the list of scopes for the token.
	 *
	 * @return The list of scopes for the token.
	 */
	public String get_scopes() {
		return _properties[1];
	}

	/**
	 * Retrieve the key to find the token.
	 *
	 * @return The key for the token.
	 */
	String get_key() {
		return _properties[2];
	}

	/**
	 * Retrieve the random generated String for the refresh token.
	 *
	 * @return The random generated String.
	 */
	String get_randomString() {
		return RandomStringUtils.randomAlphanumeric(Integer.valueOf(_properties[15]));
	}

	/**
	 * Retrieve the internal id for an access token (if refresh token).
	 *
	 * @return The associated internal spi4j id for the access token.
	 */
	String get_linkedSpi4jId() {
		return _properties[19];
	}

	/**
	 * Retrieve the id for the signing associated key.
	 *
	 * @return The id for the signing key.
	 */
	String get_createSigningKeyId() {
		return _properties[3];
	}

	/**
	 * Retrieve the id for the signing associated key.
	 *
	 * @return The id for the signing key.
	 */
	String get_readSigningKeyId() {
		return _properties[4];
	}

	/**
	 * Retrieve the internal secret in case of credential grant. Do not with the
	 * client secret which is a secret between the application an the authentication
	 * server.
	 *
	 * @return An internal secret for the application.
	 */
	String get_application_secret() {
		return _properties[22];
	}

	/**
	 * Retrieve the callback URI (for pacman application).
	 * <p>
	 * Not for use by external developer on now !
	 *
	 * @return The callback URI under String format.
	 */
	public String get_callbackEndPoint() {
		return _properties[7];
	}

	/**
	 * Retrieve the prefix for the token.
	 *
	 * @return The prefix for the token.
	 */
	String get_prefixedBy() {
		return _properties[8];
	}

	/**
	 * Check if the token is an access token (shortcut).
	 *
	 * @return true if the token is type of access.
	 */
	boolean isAccess() {
		return _type == RsTokenType_Enum.access;
	}

	/**
	 * Check if the token is a refresh token (shortcut).
	 *
	 * @return true if the token is type of refresh.
	 */
	boolean isRefresh() {
		return _type == RsTokenType_Enum.refresh;
	}

	/**
	 * Check if the token is an applicative token (shortcut).
	 *
	 * @return true if the token is type of applicative.
	 */
	boolean isApplicative() {
		return _type == RsTokenType_Enum.applicative;
	}

	/**
	 * Retrieve the type of token.
	 *
	 * @return the type of token.
	 */
	RsTokenType_Enum get_type() {
		return _type;
	}

	/**
	 * Retrieve the required authentication grant type for the token.
	 *
	 * @return the required authentication grant type.
	 */
	RsAuthGrantType_Enum get_authGrantType() {
		return _authGrantType;
	}

	/**
	 * Retrieve the required authentication grant type for the token.
	 *
	 * @return the required authentication grant type.
	 */
	public String get_authGrant() {
		return _authGrantType.get_value();
	}

	/**
	 * Check if the token is passed in the body of the request.
	 *
	 * @return true if the token is passed in the body.
	 */
	boolean isPassedByBody() {
		return _passingMode == RsPassingMode_Enum.body;
	}

	/**
	 * Check if the token is passed in the header of the request.
	 *
	 * @return true if the token is passed in the header.
	 */
	boolean isPassedByHeader() {
		return _passingMode == RsPassingMode_Enum.header;
	}

	/**
	 * Check if the token is passed as a query parameter in the request.
	 *
	 * @return true if the token is passed as a query parameter.
	 */
	boolean isPassedByQuery() {
		return _passingMode == RsPassingMode_Enum.query;
	}

	/**
	 * Check if the token is passed in a cookie.
	 *
	 * @return true if the token is passed in a cookie.
	 */
	boolean isPassedByCookie() {
		return _passingMode == RsPassingMode_Enum.cookie;
	}

	/**
	 * Check if the token is passed as a path parameter in the request.
	 *
	 * @return true if the token is passed in a path parameter.
	 */
	boolean isPassedByPath() {
		return _passingMode == RsPassingMode_Enum.path;
	}

	/**
	 * Retrieve the number of minutes for expiration time.
	 *
	 * @return The number of minutes for expiration time.
	 */
	int get_expirationNbMin() {
		return Integer.valueOf(_properties[13]);
	}

	/**
	 * Retrieve the number of hours for expiration time.
	 *
	 * @return The number of hours for expiration time.
	 */
	int get_expirationNbHour() {
		return Integer.valueOf(_properties[14]);
	}

	/**
	 * Retrieve the timeout in milliseconds for reading from auth server.
	 *
	 * @return The reading delay before timeout.
	 */
	int get_readTimeout() {
		return Integer.valueOf(_properties[20]);
	}

	/**
	 * Retrieve the timeout in milliseconds for connecting from auth server .
	 *
	 * @return The connecting delay before timeout.
	 */
	int get_connectTimeout() {
		return Integer.valueOf(_properties[21]);
	}

	/**
	 * Retrieve the client secret for the authentication server.
	 *
	 * @return The client secret for oidc / oauth authentication server.
	 */
	public String get_clientSecret() {
		return _properties[5];
	}

	/**
	 * Retrieve the client id for the authentication server.
	 *
	 * @return The client id for oidc / oauth authentication server.
	 */
	public String get_clientId() {
		return _properties[6];
	}

	/**
	 * Retrieve the protocol for the token.
	 *
	 * @return The protocol for the token.
	 */
	public RsAuthProtocol_Enum get_authProtocol() {
		return _authProtocol;
	}

	/**
	 * Indicate if the token has associated signing keys.
	 *
	 * @return true if the token has to load associated signing keys.
	 */
	public boolean hasToLoadSigningKeys() {
		return Boolean.valueOf(_properties[17]);
	}

	/**
	 * Retrieve the authentication flow.
	 *
	 * @return The authentication flow.
	 */
	public RsAuthFlow_Enum get_authFlow() {
		return _authFlow;
	}

	/**
	 * Retrieve the endpoint /auth for the authentication server.
	 *
	 * @return The endpoint /auth for oidc / oauth authentication server.
	 */
	public String get_serverAuthEndPoint() {
		return _properties[9];
	}

	/**
	 * Retrieve the endpoint /token for the authentication server.
	 *
	 * @return The endpoint /token for oidc / oauth authentication server.
	 */
	public String get_serverTokenEndPoint() {
		return _properties[10];
	}

	/**
	 * Retrieve the endpoint /userInfo for the authentication server.
	 *
	 * @return The endpoint /userInfo for oidc / oauth authentication server.
	 */
	public String get_serverUserEndPoint() {
		return _properties[11];
	}

	/**
	 * Retrieve the endpoint /logout for the authentication server.
	 *
	 * @return The endpoint /logout for oidc / oauth2 authentication server.
	 */
	public String get_serverLogoutEndPoint() {

		return _properties[16];
	}

	/**
	 * Retrieve the endpoint /refresh for the authentication server.
	 *
	 * @return The endpoint for /refresh.
	 */
	public String get_serverRefreshEndPoint() {
		return _properties[18];
	}

	/**
	 * Retrieve the signature algorithm for the token.
	 *
	 * @return The signature algorithm for the token.
	 */
	public RsSigningAlgo_Enum get_signingKeyAlgorithm() {
		return _signingKeyAlgorithm;
	}

	/**
	 * Retrieve the resource path for the signing key.
	 *
	 * @return The resource path for the signing key.
	 */
	public String get_resourcePath() {
		return _properties[12];
	}

	/**
	 * Retreive the endpoint for oidc auto-discovery informations.
	 *
	 * @return The endpoint for oidc auto-discovery informations.
	 */
	public String get_connectEndPoint() {
		return _properties[23];
	}

	/**
	 * Retrieve the loading mode for the signing key.
	 *
	 * @return the loading mode for the signing key.
	 */
	public RsSigningKeysLoad_Enum get_signingKeyLoad() {
		return _signingKeyLoad;
	}

	/**
	 * Retrieve the claims for the token.
	 *
	 * @return The claims for the token.
	 */
	Claims get_claims() {
		return _claims;
	}

	/**
	 * Set the claims for the token.
	 *
	 * @param p_claims : The claims for the token.
	 */
	void set_claims(final Claims p_claims) {
		_claims = p_claims;
	}

	/**
	 * @param p_signingKeys :
	 */
	Map<String, Key> updateWithSigningKeys(final Map<String, Key> p_signingKeys) {
		// Signing keys id are not stored if JWKS.
		// Nothing to do, return the unmodified map.
		if (_signingKeyAlgorithm.is_jsonWebKey()) {
			return p_signingKeys;
		}

		// Create a new map (no 'map.replace(...)').
		@SuppressWarnings("unused")
		final Map<String, Key> v_signingKeys = new HashMap<String, Key>();

		// Create a random signing key id
		_properties[3] = RandomStringUtils.randomAlphanumeric(c_default_signing_random);

		// Ask for a symmetric signing key (same key for create and read).
		if (_signingKeyAlgorithm.is_symmetricKey()) {
			_properties[4] = _properties[3];
			v_signingKeys.put(_properties[3], p_signingKeys.get(RsConstants.c_token_signing_key_private));
			return v_signingKeys;
		}

		// Ask for an asymmetric signing key (create = private, read = public).
		_properties[4] = RandomStringUtils.randomAlphanumeric(c_default_signing_random);
		v_signingKeys.put(_properties[3], p_signingKeys.get(RsConstants.c_token_signing_key_private));
		v_signingKeys.put(_properties[4], p_signingKeys.get(RsConstants.c_token_signing_key_public));

		// Return the new modified list.
		return v_signingKeys;
	}

	/**
	 * Copy the token. To be used only for apikey security protocol !
	 * <p>
	 * Do not override the clone method.
	 * <p>
	 * We can thus reduce the visibility for the method.
	 */
	RsToken copy() {
		try {
			// Shallow copy for all complex objects
			// so it should no be too memory consuming...

			// As all shallow copies are for read only and
			// the copy can't be done outside this package,
			// it is under control and should be secured...

			// _tokenToDecode is immutable -> deep copy.
			final RsToken v_copy = (RsToken) super.clone();
			// Make a deep copy for the claims !
			v_copy.set_claims(new DefaultClaims(_claims));
			// Return the copy with claims and the token to decode.
			return v_copy;
		} catch (final Exception e) {
			throw new RsUnexpectedException("Impossible de copier le jeton !");
		}
	}

	/**
	 * Builder for token definition.
	 *
	 * @author MINARM
	 */
	public static class Builder {
		/**
		 * The linked internal spi4j application id for the token.
		 */
		private String _linkedSpi4jId;

		/**
		 * All required claims for the token.
		 */
		private Claims _requiredClaims;

		/**
		 * The internal spi4j application id for the token.
		 */
		private String _spi4jId;

		/**
		 * The key to find the required token.
		 */
		private String _key;

		/**
		 * The prefix for the token (bearer, etc..)
		 */
		private String _prefixedBy;

		/**
		 * The passing mode for the token.
		 */
		private RsPassingMode_Enum _passMode;

		/**
		 * The passing mode for the token (under String format).
		 */
		private String _strPassMode;

		/**
		 * The expiration time in minutes.
		 */
		private String _expirationNbMin;

		/**
		 * The expiration time in hours.
		 */
		private String _expirationNbHour;

		/**
		 * The signing algorithm (symmetric / asymmetric / etc...)
		 */
		private RsSigningAlgo_Enum _signingKeyAlgorithm;

		/**
		 * The secret for oidc authentication server.
		 */
		private String _clientSecret;

		/**
		 * The id of the application for the oidc authentication server.
		 */
		private String _clientId;

		/**
		 * The oauth2 uri for /auth.
		 */
		private String _serverAuthEndPoint;

		/**
		 * The oauth2 uri for /token.
		 */
		private String _serverTokenEndPoint;

		/**
		 * The oidc uri for /userInfo.
		 */
		private String _serverUserInfoEndPoint;

		/**
		 * The oauth2 uri for /logout.
		 */
		private String _serverLogoutEndPoint;

		/**
		 * The oauth2 uri for /refresh.
		 */
		private String _serverRefreshEndPoint;

		/**
		 * The oidc uri connect for autodiscovery.
		 */
		private String _serverConnectEndPoint;

		/**
		 * The oidc uri for /cert or file path or keytore path.
		 */
		private String _certResourcePath;

		/**
		 * The callback URI under String format.
		 */
		private String _callback;

		/**
		 * The list of scopes for the token.
		 */
		private String _scopes;

		/**
		 * A random string for refresh token.
		 */
		private String _randomStringLength;

		/**
		 * The loading mode for the signing key(s).
		 */
		private RsSigningKeysLoad_Enum _signingKeyLoad;

		/**
		 * The required authentication grant type.
		 */
		private RsAuthGrantType_Enum _authGrantType;

		/**
		 * The protocol for token authentication.
		 */
		private RsAuthProtocol_Enum _authProtocol;

		/**
		 * The flow for the authentication protocol.
		 */
		private RsAuthFlow_Enum _authFlow;

		/**
		 * The flow for the authentication protocol (under String format).
		 */
		private String _strAuthFlow;

		/**
		 * The type for the token.
		 */
		private RsTokenType_Enum _type;

		/**
		 * True if prefix 'Bearer' wanted.
		 */
		private boolean _bearer;

		/**
		 * Response delay before timeout (in milliseconds).
		 */
		private String _readTimeout;

		/**
		 * Connect delay before timeout (in milliseconds).
		 */
		private String _connectTimout;

		/**
		 * Can store an internal secret for credential flow.
		 */
		private String _application_secret;

		/**
		 * Retrieve a new HasMap for the required claims (lazy mode...).
		 *
		 * @return A new hashMap for the required claims.
		 */
		private Claims get_requiredClaims() {
			if (null == _requiredClaims) {
				_requiredClaims = new DefaultClaims();
			}
			return _requiredClaims;
		}

		/**
		 * Set the specific type for the token (access, id, refresh, etc...).
		 *
		 * @param p_type : the type for the token.
		 * @return The builder.
		 */
		Builder withType(final RsTokenType_Enum p_type) {
			_type = p_type;
			return this;
		}

		/**
		 * Set a timeout for getting the token (most use with oauth2/oidc).
		 *
		 * @param p_readTimeout : The reading delay before timeout.
		 * @return The builder.
		 */
		public Builder withReadTimeout(final String p_readTimeout) {
			_readTimeout = p_readTimeout;
			return this;
		}

		/**
		 * Set a timeout for connecting to oauth2/oidc server.
		 *
		 * @param p_connectTimeout : The connecting delay before timeout.
		 * @return The builder.
		 */
		public Builder withConnectTimeout(final String p_connectTimeout) {
			_connectTimout = p_connectTimeout;
			return this;
		}

		/**
		 * Set a private application id for the token.
		 *
		 * @param p_spi4jId : The private spi4j application id for the token.
		 * @return The builder.
		 */
		public Builder withSpi4jId(final String p_spi4jId) {
			_spi4jId = p_spi4jId;
			return this;
		}

		/**
		 * Set a reference (link) between a refresh and an access token.
		 *
		 * @param p_spi4jId : The private spi4j application id for the linked token.
		 * @return The builder.
		 */
		public Builder withLinkedSpi4jId(final String p_spi4jId) {
			_linkedSpi4jId = p_spi4jId;
			return this;
		}

		/**
		 * Set the callback URI (under String format).
		 *
		 * @param p_callback : The callback URI under String format.
		 * @return The builder.
		 */
		public Builder withCallbackEndPoint(final String p_callback) {
			_callback = p_callback;
			return this;
		}

		/**
		 * Set the list of scopes for the token. Not for use by external developer on
		 * now !
		 *
		 * @param p_scopes : The list of scopes for the token.
		 * @return The builder.
		 */
		public Builder withScopes(final String p_scopes) {
			_scopes = p_scopes;
			return this;
		}

		/**
		 * Set the audience for the token.
		 *
		 * @param p_audience : The audience for the token.
		 * @return The Builder.
		 */
		public Builder withAudience(final String p_audience) {
			get_requiredClaims().setAudience(p_audience);
			return this;
		}

		/**
		 * Set the id for the token.
		 *
		 * @param p_id : The id for the token.
		 * @return The Builder.
		 */
		public Builder withId(final String p_id) {
			get_requiredClaims().setId(p_id);
			return this;
		}

		/**
		 * Set a random String with the requested length.
		 *
		 * @param p_randomStringLength : The requested length for the random String.
		 * @return The builder.
		 */
		public Builder withRandomStringLength(final String p_randomStringLength) {
			_randomStringLength = p_randomStringLength;
			return this;
		}

		/**
		 * Set the issuer for the token.
		 *
		 * @param p_issuer : The issuer for the token.
		 * @return The Builder.
		 */
		public Builder withIssuer(final String p_issuer) {
			get_requiredClaims().setIssuer(p_issuer);
			return this;
		}

		/**
		 * Set the subject for the token.
		 *
		 * @param p_subject : The subject for the token.
		 * @return The Builder.
		 */
		public Builder withSubject(final String p_subject) {
			get_requiredClaims().setSubject(p_subject);
			return this;
		}

		/**
		 * Set the not before activation for the token.
		 *
		 * @param p_date : The not before activation for the token.
		 * @return The Builder.
		 */
		public Builder withNotBefore(final Date p_date) {
			get_requiredClaims().setNotBefore(p_date);
			return this;
		}

		/**
		 * Set a custom parameter for the token.
		 *
		 * @param p_paramName  : The name (key) for the parameter.
		 * @param p_paramValue : The value for the parameter.
		 * @return The Builder.
		 */
		public Builder withParam(final String p_paramName, final String p_paramValue) {
			get_requiredClaims().put(p_paramName, p_paramValue);
			return this;
		}

		/**
		 * Set a custom parameter for the token (if from '.properties' file).
		 *
		 * @param p_paramName : The name (key) for the parameter.
		 * @return The Builder.
		 */
		public Builder withParam(final String p_paramName) {
			get_requiredClaims().put(p_paramName, null);
			return this;
		}

		/**
		 * Set the header passing mode for the token.
		 *
		 * @return The Builder.
		 */
		public Builder passedByHeader() {
			_passMode = RsPassingMode_Enum.header;
			return this;
		}

		/**
		 * Set the query passing mode for the token.
		 *
		 * @return The Builder.
		 */
		public Builder passedByQuery() {
			_passMode = RsPassingMode_Enum.query;
			return this;
		}

		/**
		 * Set the body passing mode for the token.
		 *
		 * @return The Builder.
		 */
		public Builder passedByBody() {
			_passMode = RsPassingMode_Enum.body;
			return this;
		}

		/**
		 * Set the path passing mode for the token.
		 *
		 * @return The Builder.
		 */
		public Builder passedByPath() {
			_passMode = RsPassingMode_Enum.path;
			return this;
		}

		/**
		 * Set the cookie passing mode for the token.
		 *
		 * @return The Builder.
		 */
		public Builder passedByCookie() {
			_passMode = RsPassingMode_Enum.cookie;
			return this;
		}

		/**
		 * Set a state for the token (internal secret for the application);
		 *
		 * @param p_application_secret : The internal secret for the application.
		 * @return The Builder.
		 */
		public Builder withApplicationSecret(final String p_application_secret) {
			_application_secret = p_application_secret;
			return this;
		}

		/**
		 * Set the passing mode for the token (String format).
		 *
		 * @param p_strPassMode : The passing mode for the token.
		 * @return The builder.
		 */
		public Builder withPassingMode(final String p_strPassMode) {
			_strPassMode = p_strPassMode;
			return this;
		}

		/**
		 * Try to convert OBEO enumeration to Spi4j enumeration.
		 *
		 * @param p_obeoPassMode : The enumeration under OBEO format.
		 */
		private void withPassingModeFromObeo(final String p_obeoPassMode) {
			_passMode = RsPassingMode_Enum.get_passingModeFromObeo(p_obeoPassMode);
		}

		/**
		 * Try to convert OBEO enumeration to Spi4j enumeration unless the flow has
		 * already been defined.
		 *
		 * @param p_obeoAuthFlow : The enumeration under OBEO format.
		 */
		private void withAuthFlowFromObeo(final String p_obeoAuthFlow) {
			if (null == _authFlow) {
				_authFlow = RsAuthFlow_Enum.get_authFlowFromObeo(p_obeoAuthFlow);
			}
		}

		/**
		 * Set the key (parameter name) to find the token.
		 *
		 * @param p_key : The parameter name to find the token.
		 * @return The Builder.
		 */
		public Builder withCustomKey(final String p_key) {
			_key = p_key;
			return this;
		}

		/**
		 * Set the expiration time in minutes.
		 *
		 * @param p_nbMin : The expiration time in minutes.
		 * @return The builder.
		 */
		public Builder withExpirationTimeInMinutes(final String p_nbMin) {
			_expirationNbMin = p_nbMin;
			return this;
		}

		/**
		 * Set the expiration time in hours.
		 *
		 * @param p_nbHour : The expiration time in hours.
		 * @return The builder.
		 */
		public Builder withExpirationTimeInHours(final String p_nbHour) {
			_expirationNbHour = p_nbHour;
			return this;
		}

		/**
		 * Set a custom prefix for the token.
		 *
		 * @param p_prefixedBy : The custom prefix for the token.
		 * @return The builder.
		 */
		public Builder withPrefixedBy(final String p_prefixedBy) {
			_prefixedBy = p_prefixedBy;
			return this;
		}

		/**
		 * Set the signature algorithm for the token.
		 *
		 * @param p_signingKeyAlgorithm : The signature algorithm chosen for the token.
		 * @return The builder.
		 */
		public Builder withSigningKeyAlgorithm(final RsSigningAlgo_Enum p_signingKeyAlgorithm) {
			_signingKeyAlgorithm = p_signingKeyAlgorithm;
			return this;
		}

		/**
		 * Set the client id for the authentication server.
		 *
		 * @param p_clientId : The client id for the authentication server.
		 * @return The builder.
		 */
		public Builder withClientId(final String p_clientId) {
			_clientId = p_clientId;
			return this;
		}

		/**
		 * Set the client secret for the authentication server.
		 *
		 * @param p_clientSecret : The client secret for authentication server.
		 * @return The builder.
		 */
		public Builder withClientSecret(final String p_clientSecret) {
			_clientSecret = p_clientSecret;
			return this;
		}

		/**
		 * Set the required authentication grant type for the token.
		 *
		 * @param p_authGrantType : The required authentication grant type.
		 * @return The builder.
		 */
		public Builder withAuthGrantType(final RsAuthGrantType_Enum p_authGrantType) {
			_authGrantType = p_authGrantType;
			return this;
		}

		/**
		 * Set the authentication protocol for the token.
		 *
		 * @param p_authProtocol : The protocol for the authentication.
		 * @return The builder.
		 */
		public Builder withAuthProtocol(final RsAuthProtocol_Enum p_authProtocol) {
			_authProtocol = p_authProtocol;
			return this;
		}

		/**
		 * Set the endpoint for getting authentication.Not for use by external developer
		 * on now !
		 *
		 * @param p_authEndPoint : The endpoint to get authentication.
		 * @return The builder.
		 */
		Builder withAuthEndPoint(final String p_authEndPoint) {
			_serverAuthEndPoint = p_authEndPoint;
			return this;
		}

		/**
		 * Set the endpoint for getting the access token.
		 *
		 * @param p_tokenEndPoint : The endpoint to get the access token.
		 * @return The builder.
		 */
		public Builder withTokenEndPoint(final String p_tokenEndPoint) {
			_serverTokenEndPoint = p_tokenEndPoint;
			return this;
		}

		/**
		 * Set the endpoint for getting user informations.
		 *
		 * @param p_userInfoEndPoint : The endpoint to get user informations (id token).
		 * @return The builder.
		 */
		public Builder withUserInfoEndPoint(final String p_userInfoEndPoint) {
			_serverUserInfoEndPoint = p_userInfoEndPoint;
			return this;
		}

		/**
		 * Set the endpoint for getting all oidc technical informations.
		 *
		 * @param p_connectEndPoint : The endpoint to get the auto discovery for oidc
		 *                          all oidc informations.
		 * @return The builder.
		 */
		public Builder withConnectEndPoint(final String p_connectEndPoint) {
			_serverConnectEndPoint = p_connectEndPoint;
			return this;
		}

		/**
		 * Set the endpoint for getting refresh informations.
		 *
		 * @param p_refreshEndPoint : The endpoint for refresh token informations.
		 * @return The builder.
		 */
		public Builder withRefreshEndPoint(final String p_refreshEndPoint) {
			_serverRefreshEndPoint = p_refreshEndPoint;
			return this;
		}

		/**
		 * Set the endpoint to invalidate the token.
		 *
		 * @param p_serverLogoutEndPoint : The endpoint for invalidate the token.
		 * @return The builder.
		 */
		public Builder withLogoutEndPoint(final String p_serverLogoutEndPoint) {
			_serverLogoutEndPoint = p_serverLogoutEndPoint;
			return this;
		}

		/**
		 * Set the endpoint for getting the json web keys.
		 *
		 * @param p_certResourcePath : The endpoint for getting the certificates (jwks).
		 * @return The builder.
		 */
		public Builder withCertificateResourcePath(final String p_certResourcePath) {
			_certResourcePath = p_certResourcePath;
			return this;
		}

		/**
		 * Set the signing mode for the singing key(s).
		 *
		 * @param p_signingKeyLoad : The loading mode for the signing key(s).
		 * @return The builder.
		 */
		public Builder withSigningKeyLoadMode(final RsSigningKeysLoad_Enum p_signingKeyLoad) {
			_signingKeyLoad = p_signingKeyLoad;
			return this;
		}

		/**
		 * Set the flow for the authentication protocol (String format).
		 *
		 * @param p_strAuthFlow : The type of flow for authentication protocol.
		 * @return The builder.
		 */
		public Builder withAuthFlow(final String p_strAuthFlow) {
			_strAuthFlow = p_strAuthFlow;
			return this;
		}

		/**
		 * Set the flow for the authentication protocol.
		 *
		 * @param p_authFlow : The flow for the authentication protocol.
		 * @return The builder.
		 */
		public Builder withAuthFlow(final RsAuthFlow_Enum p_authFlow) {
			_authFlow = p_authFlow;
			return this;
		}

		/**
		 * Set automatically the prefix to 'bearer'.
		 *
		 * @return The builder.
		 */
		public Builder withBearer() {
			_bearer = Boolean.TRUE;
			return this;
		}

		/**
		 * Check if the value entered by the user is directly the value of the property
		 * or if it is a key for retrieving the value in a configuration file. If the
		 * value is not found in the list of properties, we simply consider it is the
		 * direct value for this property.
		 *
		 * @param p_prop  : The specific property, it can be a key or a value.
		 * @param p_props : The list of all properties loaded from a configuration file.
		 * @return The value of the property or null as before (avoid to check the
		 *         nullity each time).
		 */
		private String getProperty(final String p_prop, final Properties p_props) {
			if (null != p_prop) {
				if (null != p_props.getProperty(p_prop) && !p_props.getProperty(p_prop).isEmpty()) {
					return p_props.getProperty(p_prop).trim();
				}
				return p_prop.trim();
			}
			return null;
		}

		/**
		 * Retrieve the complete definition for the required token.
		 *
		 * @param p_props : The 'properties' for a configuration file.
		 * @return The required Token definition.
		 */
		RsToken build(final Properties p_props) {

			// Check if must retrieve specific configuration from a file.
			// Set all properties from custom keys issued of the properties...
			// Here the variable must contain the key and not the value.
			if (null != p_props) {
				withCustomKey(getProperty(_key, p_props));
				withPassingModeFromObeo(getProperty(_strPassMode, p_props));
				withTokenEndPoint(getProperty(_serverTokenEndPoint, p_props));
				withUserInfoEndPoint(getProperty(_serverUserInfoEndPoint, p_props));
				withAuthEndPoint(getProperty(_serverAuthEndPoint, p_props));
				withLogoutEndPoint(getProperty(_serverLogoutEndPoint, p_props));
				withRefreshEndPoint(getProperty(_serverRefreshEndPoint, p_props));
				withConnectEndPoint(getProperty(_serverConnectEndPoint, p_props));
				withCertificateResourcePath(getProperty(_certResourcePath, p_props));
				withCallbackEndPoint(getProperty(_callback, p_props));
				withAuthFlowFromObeo(getProperty(_strAuthFlow, p_props));
				withScopes(getProperty(_scopes, p_props));
				withClientId(getProperty(_clientId, p_props));
				withConnectTimeout(getProperty(_connectTimout, p_props));
				withReadTimeout(getProperty(_readTimeout, p_props));
				withClientSecret(getProperty(_clientSecret, p_props));
				withApplicationSecret(getProperty(_application_secret, p_props));
			}

			// Check the protocol ...
			if (null == _authProtocol) {
				throw new RsUnexpectedException("Impossible de terminer la configuration "
						+ "de(s) jeton(s) ! Il manque le protocol d'authentification.");
			}

			// For oidc and oauth2, check the flow...
			if ((_authProtocol == RsAuthProtocol_Enum.oauth2 || _authProtocol == RsAuthProtocol_Enum.openIdConnect)
					&& null == _authFlow) {
				throw new RsUnexpectedException("Impossible de terminer la configuration "
						+ "de(s) jeton(s) ! Il manque le flow d'authentification.");
			}

			// If no custom grant type, set by default (from the flow).
			if ((_authProtocol == RsAuthProtocol_Enum.oauth2 || _authProtocol == RsAuthProtocol_Enum.openIdConnect)
					&& null == _authGrantType) {
				withAuthGrantType(_authFlow.get_defaultGrantType());
			}

			// Check if the token has associated signing keys to load.
			String _loadSigningKeys = "true";
			if (_type == RsTokenType_Enum.refresh && (_authProtocol != RsAuthProtocol_Enum.apiKey)) {
				_loadSigningKeys = "false";
			}

			// Check the expiration time in hours.
			if (null == _expirationNbHour || !NumberUtils.isCreatable(_expirationNbHour)) {
				withExpirationTimeInHours("0");
			}

			// Check the expiration time in minutes.
			if (null == _expirationNbMin || !NumberUtils.isCreatable(_expirationNbMin)) {
				withExpirationTimeInMinutes("0");
			}

			// Check the read timeout (no test if aouth2/oidc).
			if (null == _readTimeout || !NumberUtils.isCreatable(_readTimeout)) {
				withReadTimeout(String.valueOf(RsConstants.c_token_default_read_timeout));
			}

			// Check the connect timeout (no test if aouth2/oidc).
			if (null == _connectTimout || !NumberUtils.isCreatable(_connectTimout)) {
				withConnectTimeout(String.valueOf(RsConstants.c_token_default_connect_timeout));
			}

			// Override prefix for he token.
			if (_bearer) {
				withPrefixedBy(RsConstants.c_auth_header_bearer);
			}

			// Complete with auto-discovery uri if oidc protocol.
			if (RsAuthProtocol_Enum.openIdConnect == _authProtocol) {
				RsTokenHelper.completeWithRemoteParams(this, _serverConnectEndPoint);
			}

			return new RsToken(_spi4jId, _linkedSpi4jId, _loadSigningKeys, _type, _passMode, _key, _prefixedBy,
					_expirationNbMin, _expirationNbHour, _signingKeyAlgorithm, _clientId, _clientSecret,
					_serverAuthEndPoint, _serverTokenEndPoint, _serverUserInfoEndPoint, _serverLogoutEndPoint,
					_serverRefreshEndPoint, _certResourcePath, _authProtocol, _signingKeyLoad, _callback, _scopes,
					_randomStringLength, _authFlow, _authGrantType, get_requiredClaims(), _readTimeout, _connectTimout,
					_application_secret, _serverConnectEndPoint);
		}
	}
}
