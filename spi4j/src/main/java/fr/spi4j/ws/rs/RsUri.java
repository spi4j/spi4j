/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.RandomStringUtils;

import fr.spi4j.ws.rs.exception.RsSpi4jUnexpectedException;
import fr.spi4j.ws.rs.exception.RsUriSyntaxException;

/**
 * Builder for URI (EndPoint). Can have specific parameter to set in session and
 * / or to check for callback URI.
 * <p>
 * Not in use for now, as callback URIs are no more used for backend generation.
 *
 * @author MINARM.
 */
public class RsUri {

	/**
	 * Separator between the EndPoint and the first parameter.
	 */
	private static final String c_first_param_sep = "?";

	/**
	 * Separator between parameters.
	 */
	private static final String c_next_param_sep = "&";

	/**
	 * Separator between parameter name and parameter value.
	 */
	private static final String c_param_value_sep = "=";

	/**
	 * Default number of char for random string.
	 */
	private static final int c_default_max_random = 15;

	/**
	 * The final URI to build.
	 */
	private URI _uri;

	/**
	 * List of parameters to set in session.
	 */
	private Map<String, String> _paramsToSession;

	/**
	 * List of parameters to check if callback URI (also in session but in specific
	 * key).
	 */
	private Map<String, String> _paramsToCheck;

	/**
	 * Retrieve the URI.
	 *
	 * @return The final URI.
	 */
	URI get_uri() {
		return _uri;
	}

	/**
	 * Retrieve the list of params stored with a specific key in the http session.
	 *
	 * @return : The list of parameters to check.
	 */
	Map<String, String> get_paramsToCheck() {
		// Defensive copy.
		return new HashMap<>(_paramsToCheck);
	}

	/**
	 * Retrieve the list of params stored with their own key in the http session.
	 *
	 * @return : The list of parameters to set in http session.
	 */
	Map<String, String> get_paramsToSesssion() {
		return _paramsToSession;
	}

	/**
	 * Check if the URI has parameters to check for callback URI.
	 *
	 * @return : true if the list exists.
	 */
	boolean hasParamsToCheck() {
		return (null != _paramsToCheck);
	}

	/**
	 * Check if the URI has parameters to set in session.
	 *
	 * @return : true if the list exists.
	 */
	boolean hasParamsToSession() {

		return (null != _paramsToSession);
	}

	/**
	 * Constructor.
	 *
	 * @param p_uri             : The URI to build.
	 * @param p_paramsToCheck   : The list of parameters to check (if exists).
	 * @param p_paramsToSession : The list of parameters to set in session (if
	 *                          exists).
	 * @throws RsUriSyntaxException : transform to a runtime exception.
	 */
	RsUri(final String p_uri, final Map<String, String> p_paramsToCheck, final Map<String, String> p_paramsToSession) {
		try {
			_uri = new URI(p_uri);
			_paramsToCheck = p_paramsToCheck;
			_paramsToSession = p_paramsToSession;
		} catch (final URISyntaxException p_e) {
			throw new RsUriSyntaxException(p_e);
		}
	}

	/**
	 * Builder for the URI.
	 *
	 * @author MINARM
	 */
	public static class Builder {
		private String _endPoint;

		private Map<String, String> _params;

		private Map<String, String> _paramsToCheck;

		private Map<String, String> _paramToSession;

		/**
		 * @param p_endPoint : The endpoint for the URI.
		 * @return The Builder.
		 */
		public Builder withEndPoint(final String p_endPoint) {
			if (null != _endPoint) {
				throw new RsSpi4jUnexpectedException("EndPoint déjà créé pour l'URI !");
			}
			_endPoint = p_endPoint;
			return this;
		}

		/**
		 * Add a new param to the final URI.
		 *
		 * @param p_paramName  : The name for the parameter of the URI.
		 * @param p_paramValue : The value for the parameter of the URI.
		 * @return The Builder.
		 */
		public Builder withParam(final String p_paramName, final String p_paramValue) {
			if (null == _params) {
				_params = new HashMap<>();
			}

			if (null == p_paramName || null == p_paramValue) {
				throw new RsSpi4jUnexpectedException("Impossible de construire l'URI, un des paramètres est null !");
			}

			_params.put(p_paramName.trim(), p_paramValue.trim());
			return this;
		}

		/**
		 * Add a new param to the final URI. The parameter will be checked for a
		 * callback URI.
		 *
		 * @param p_paramName  : The name for the parameter of the URI.
		 * @param p_paramValue : The value for the parameter of the URI.
		 * @return The Builder.
		 */
		public Builder withParamToCheck(final String p_paramName, final String p_paramValue) {
			if (null == _paramsToCheck) {
				_paramsToCheck = new HashMap<>();
			}
			_paramsToCheck.put(p_paramName, p_paramValue);
			return withParam(p_paramName, p_paramValue);
		}

		/**
		 * Add a new param to the final URI. The parameter will be set in http session.
		 *
		 * @param p_paramName  : The name for the parameter of the URI.
		 * @param p_paramValue : The value for the parameter of the URI.
		 * @return The Builder.
		 */
		public Builder withParamToSession(final String p_paramName, final String p_paramValue) {
			if (null == _paramToSession) {
				_paramToSession = new HashMap<>();
			}
			_paramToSession.put(p_paramName, p_paramValue);
			return withParam(p_paramName, p_paramValue);
		}

		/**
		 * Add automatically a default state parameter with a random string (OAuth 2).
		 *
		 * @return The Builder.
		 */
		public Builder withParamState() {
			return withParamToCheck(RsConstants.c_param_oauth2_state,
					RandomStringUtils.randomAlphanumeric(c_default_max_random));
		}

		/**
		 * Add automatically a default nonce parameter with a random string (OIDC). If
		 * present, placed in ID token.
		 *
		 * @return The Builder.
		 */
		public Builder withParamNonce() {
			return withParamToSession(RsConstants.c_param_oidc_nonce,
					RandomStringUtils.randomAlphanumeric(c_default_max_random));
		}

		/**
		 * Add automatically a parameter with 'client_id' for key.
		 *
		 * @param p_paramValue : The value for the parameter of the URI.
		 * @return The Builder.
		 */
		public Builder withParamClientId(final String p_paramValue) {
			return withParam(RsConstants.c_param_oauth2_client_id, p_paramValue);
		}

		/**
		 * Add automatically a parameter with 'redirect_uri' for key.
		 *
		 * @param p_paramValue : The value for the parameter of the URI.
		 * @return The Builder.
		 */
		public Builder withParamRedirectUri(final String p_paramValue) {
			return withParam(RsConstants.c_param_oauth2_redirect_uri, p_paramValue);
		}

		/**
		 * Add automatically a parameter with 'response_type' for key.
		 *
		 * @param p_paramValue : The value for the parameter of the URI.
		 * @return The Builder.
		 */
		public Builder withParamResponseType(final String p_paramValue) {
			return withParam(RsConstants.c_param_oauth2_response_type, p_paramValue);
		}

		/**
		 * Add automatically a parameter with 'response_type' for key.
		 *
		 * @param p_flow : The Enumeration for the parameter of the URI.
		 * @return The Builder.
		 */
		public Builder withParamResponseType(final RsAuthFlow_Enum p_flow) {
			return withParam(RsConstants.c_param_oauth2_response_type, p_flow.get_value());
		}

		/**
		 * Add automatically a parameter with 'grant_type' for key.
		 *
		 * @param p_paramValue : The value for the parameter of the URI.
		 * @return The Builder.
		 */
		public Builder withParamGrantType(final String p_paramValue) {
			return withParam(RsConstants.c_param_oauth2_grant_type, p_paramValue);
		}

		/**
		 * Add automatically a parameter with 'scope' for key.
		 *
		 * @param p_paramValue : The value for the parameter of the URI.
		 * @return The Builder.
		 */
		public Builder withParamScope(final String p_paramValue) {
			return withParam(RsConstants.c_param_oauth2_scope, p_paramValue);
		}

		/**
		 * Construct the URI with all requested parameters.
		 *
		 * @return The URI under string format.
		 */
		public RsUri build() {
			final StringBuilder v_strBuilder = new StringBuilder(_endPoint);
			boolean v_start = Boolean.TRUE;

			if (null == _params) {
				throw new RsSpi4jUnexpectedException("Aucun parametre pour l'URI !");
			}

			for (final Entry<String, String> v_entry : _params.entrySet()) {
				if (v_start) {
					v_strBuilder.append(c_first_param_sep);
					v_start = Boolean.FALSE;
				}

				v_strBuilder.append(v_entry.getKey()).append(c_param_value_sep);
				v_strBuilder.append(v_entry.getValue());
				v_strBuilder.append(c_next_param_sep);
			}

			v_strBuilder.deleteCharAt(v_strBuilder.length() - 1);
			return new RsUri(v_strBuilder.toString(), _paramsToCheck, _paramToSession);
		}
	}
}
