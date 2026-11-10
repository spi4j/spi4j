/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import fr.spi4j.ws.rs.exception.RsUnauthorizedException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

/**
 * Default implementation for the token connector. If the developer needs to
 * have a custom connector, he just have to create his proper connector in the
 * target project and simply implements the interface RsTokenConnector_Itf.
 *
 * @author MINARM
 */
public class RsTokenDefaultConnector implements RsTokenConnector_Itf {

	/**
	 * All parameter from GET or POST.
	 */
	protected final Map<String, String> _params = new HashMap<>();

	/**
	 * Header parameters (depending the grant flow).
	 */
	protected final Map<String, String> _headerParams = new HashMap<>();

	/**
	 * The token configuration.
	 */
	protected final RsToken _tokenConfig;

	/**
	 * Default constructor for Authentication code grant.
	 *
	 * @param p_tokenConfig : The configuration for the token.
	 * @param p_uriInfo     : All necessary informations from the initial request.
	 */
	public RsTokenDefaultConnector(final RsToken p_tokenConfig, final UriInfo p_uriInfo) {
		final MultivaluedMap<String, String> v_queryParams = p_uriInfo.getQueryParameters();
		_tokenConfig = p_tokenConfig;
		_params.put(RsConstants.c_param_oauth2_redirect_uri, _tokenConfig.get_callbackEndPoint());
		_params.put(RsConstants.c_param_oauth2_scope, _tokenConfig.get_scopes());
		_params.put(RsConstants.c_param_oauth2_code, RsUtils.get_param(RsConstants.c_param_oauth2_code, v_queryParams));
		_params.put(RsConstants.c_param_oauth2_state,
				RsUtils.get_param(RsConstants.c_param_oauth2_state, v_queryParams));
		if (RsAuthProtocol_Enum.openIdConnect == _tokenConfig.get_authProtocol()) {
			_params.put(RsConstants.c_param_oidc_nonce,
					RsUtils.get_param(RsConstants.c_param_oidc_nonce, v_queryParams));
		}
	}

	/**
	 * Default constructor for Authentication password grant.
	 *
	 * @param p_tokenConfig : The configuration for the token.
	 * @param p_credentials : The login and password for the end user.
	 * @param p_uriInfo     : All supplementary informations from the initial
	 *                      request.
	 */
	public RsTokenDefaultConnector(final RsToken p_tokenConfig, final String p_credentials, final UriInfo p_uriInfo) {
		_tokenConfig = p_tokenConfig;
		final RsAuthCredentials v_credentials = RsTokenHelper.decodeCredentials(p_credentials);
		_params.put(RsConstants.c_param_passwd_username, v_credentials.get_userName());
		_params.put(RsConstants.c_param_passwd_passord, v_credentials.get_passwd());
		_params.put(RsConstants.c_param_oauth2_scope, _tokenConfig.get_scopes());
	}

	/**
	 * Default constructor for Authentication credentials grant.
	 *
	 * As the credential type flow only concerns the application and not an end
	 * user, the resource must not be accessible outside. A secret (state) must then
	 * be sent with the request, as this secret cannot be known from the outside,
	 * any request arriving without this secret is automatically rejected.
	 *
	 * @param p_tokenConfig        : The configuration for the token.
	 * @param p_application_secret : The internal state (secret) for the
	 *                             application.
	 */
	public RsTokenDefaultConnector(final RsToken p_tokenConfig, final String p_application_secret) {
		_tokenConfig = p_tokenConfig;
		_params.put(RsConstants.c_param_oauth2_scope, _tokenConfig.get_scopes());
		if (!_tokenConfig.get_application_secret()
				.equals(RsTokenHelper.decodeCredentials(p_application_secret).get_passwd())) {
			throw new RsUnauthorizedException();
		}
	}

	/**
	 * Retrieve query parameters?
	 */
	@Override
	public Map<String, String> get_params() {
		return _params;
	}

	/**
	 * Retrieve header parameters.
	 */
	@Override
	public Map<String, String> get_headerParams() {
		return _headerParams;
	}

	/**
	 * Retrieve the initial token configuration.
	 */
	@Override
	public RsToken get_tokenConfig() {
		return _tokenConfig;
	}

	/**
	 * Retrieve the final token(s) under json format.
	 *
	 * By default, do nothing.
	 */
	@Override
	public JSONObject get_tokens(final JSONObject p_tokens) {
		return p_tokens;
	}
}
