/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.io.IOException;

import org.json.JSONObject;

import fr.spi4j.ws.rs.exception.RsExceptionXtoContainer;
import fr.spi4j.ws.rs.exception.RsException_Abs;
import fr.spi4j.ws.rs.exception.RsOidcSecurityException;
import fr.spi4j.ws.rs.exception.RsServiceClosedException;
import fr.spi4j.ws.rs.exception.RsTokenNotFoundException;
import fr.spi4j.ws.rs.exception.RsUnauthorizedException;
import fr.spi4j.ws.rs.exception.RsUnexpectedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response.Status;

/**
 * Filter for secured or unsecured REST resources. This solution uses only the
 * JAX-RS 2.0 API, avoiding any vendor specific solution.
 *
 * @author MINARM
 */
public abstract class RsFilter_Abs implements ContainerRequestFilter, ContainerResponseFilter {

	/**
	 * The configuration for the filter.
	 */
	private static RsFilterConfigurator _filterConfig;

	/**
	 * All technical informations about the called resource.
	 */
	@Context
	private ResourceInfo _resourceInfo;

	/**
	 * All request informations for HTTP request.
	 */
	@Context
	private HttpServletRequest _webRequest;

	/**
	 * Constructor.
	 */
	protected RsFilter_Abs() {
		// Instantiate the filter.
		super();

		// Retrieve the basic configuration for filter.
		_filterConfig = initFilterConfig();

		// Initialization of an empty tokens container.
		final RsTokensContainer v_tokensContainer = new RsTokensContainer();

		// Try to initiate a 'properties' object from a configuration file (if exists).
		v_tokensContainer.loadProperties(_filterConfig.get_configPathPrefix());

		// Complete the tokens container with required tokens.
		initTokensConfig().defineRequiredTokens(v_tokensContainer);

		// Complete the operational keys from required tokens.
		_filterConfig.set_tokenSigningKeys(v_tokensContainer);

		// Complete the configuration with the required tokens.
		_filterConfig.set_tokensContainer(v_tokensContainer);

		// Complete the configuration with header keys for paging.
		_filterConfig.set_headersPageParams(initPaginatorHeaderKeys());

		// Display configuration at startup.
		_filterConfig.displayFilterConfigToConsole(RsLogger.get_log());
	}

	/**
	 * Filter the request before accessing to the resource.
	 *
	 * @param p_requestCtx : The request context for the call.
	 */
	@Override
	public final void filter(final ContainerRequestContext p_requestCtx) throws IOException {
		try {
			// Check if current maintenance.
			checkUnderMaintenance();

			// Do some preliminary work if needed (developer).
			beforeFilter(p_requestCtx);

			// If resource is secured, check the required token(s).
			checkAllTokens(p_requestCtx);

			// If specific authentication refresh URI, proceed.
			checkForRefreshToken(p_requestCtx);
		}
		// Not authenticated (all authentication protocol).
		catch (final JwtException p_exception) {
			abortWithUnauthorizedException(p_requestCtx, p_exception);
		}
		// Service under maintenance.
		catch (final RsServiceClosedException p_exception) {
			abortWithServiceClosedException(p_requestCtx, p_exception);
		}
		// For OIDC security exception in communication protocol.
		catch (final RsOidcSecurityException p_exception) {
			abortWithOidcSecurityException(p_requestCtx, p_exception);
		}
		// All other exception of RS type.
		catch (final RsException_Abs p_exception) {
			abortWithRsException(p_requestCtx, p_exception);
		}
		// Unknown exception.
		catch (final Exception p_exception) {
			abortWithUnexpectedException(p_requestCtx, p_exception);
		}
	}

	/**
	 * Filter the response after accessing the resource.
	 *
	 * @param p_requestCtx  : The request context for the call.
	 * @param p_responseCtx : The response context for the call.
	 */
	@Override
	public final void filter(final ContainerRequestContext p_requestCtx, final ContainerResponseContext p_responseCtx)
			throws IOException {
		try {
			// Add the default Headers.
			addDefaultHeaders(p_requestCtx, p_responseCtx);

			// Add the headers for the PES (if required).
			addPESHeader(p_requestCtx, p_responseCtx);

			// Do some complementary work if needed (developer).
			afterFilter(p_responseCtx);
		}
		// Unknown exception.
		catch (final Exception p_exception) {
			// If error, we can't use the abort method for the
			// request as we are already in response phase.
			abortWithException(p_responseCtx, p_exception);
		}
	}

	/**
	 * Check if the resource (service) has some security defined. Retrieve and
	 * decode the requested token(s).
	 * <p>
	 * If an access token is invalid or expired, abort directly with an exception.
	 *
	 * @param p_requestCtx : The request context for the call.
	 */
	private void checkAllTokens(final ContainerRequestContext p_requestCtx) {
		// Check if the resource is secured.
		if (hasToCheckTokens()) {
			// By default, the resource is locked.
			boolean v_resourceLocked = Boolean.TRUE;

			// List all tokens in the tokens container.
			for (final RsToken v_token : _filterConfig.get_tokensContainer().get_tokens()) {
				// Check each token.
				if (checkToken(p_requestCtx, v_token)) {
					// If an access token is valid, unlock the resource.
					v_resourceLocked = Boolean.FALSE;
				}
			}
			// Check if always locked.
			if (v_resourceLocked) {
				throw new JwtException("Aucun jeton de sécurité n'a été trouvé !");
			}
		}
	}

	/**
	 * Check and decode a token (access | id | applicative).
	 *
	 * @param p_requestCtx : The request context for the call.
	 * @param v_token      : The current token definition.
	 * @return True if an access token is found and is valid.
	 */
	private boolean checkToken(final ContainerRequestContext p_requestCtx, final RsToken v_token) {
		// Threat only access token(s) associated with the current
		// resource and applicative or id token(s).
		if (isTokenToProcess(v_token)) {
			// Decode the token and retrieve the claims.
			final Claims v_claims = decodeToken(p_requestCtx, v_token);

			// Check if the token has been found and is valid...
			if (null != v_claims) {
				// Provide the claims to the application.
				provideClaimsToApplication(v_token.get_spi4jId(), v_claims);

				// If the type is 'access' unlock the resource.
				if (v_token.isAccess()) {
					return Boolean.TRUE;
				}
			}
		}
		// The current token is not an access type or is invalid.
		// Do not ask for unlocking the resource.
		return Boolean.FALSE;
	}

	/**
	 * Decode the specific token and retrieve the claims for the token.
	 * <p>
	 * If the token is not found, return null for the claims.
	 *
	 * @param p_requestCtx : The request context for the call.
	 * @param p_token      : The definition (and the token) for the requested token.
	 * @return The claims for the token or null if the token is not found.
	 */
	private Claims decodeToken(final ContainerRequestContext p_requestCtx, final RsToken p_token) {
		try {
			// Decode the token and retrieve all the claims.
			return RsTokenHelper.decodeToken(p_token, getTokenFromRequest(p_requestCtx, p_token),
					RsConstants.c_token_default_allwd_nbsec);
		}
		// The token is valid but has expired.
		catch (final ExpiredJwtException p_exception) {
			// Abort with the initial exception.
			throw p_exception;
		}
		// The token is invalid.
		catch (final JwtException p_exception) {
			// Abort with the initial exception.
			throw p_exception;
		}
		// The token is not found...wait for another access token.
		catch (final RsTokenNotFoundException p_exception) {
			// Nothing to do...
			return null;
		}
	}

	/**
	 * Retrieve the string current token from the request, depends of required token
	 * parameters.
	 * <p>
	 * Here work with a copy of the token definition !!!
	 *
	 * @param p_requestCtx : The request context for the call.
	 * @param p_token      : The definition for the requested token.
	 * @return The required token completed with the token string without the type
	 *         (bearer or anything else).
	 */
	private String getTokenFromRequest(final ContainerRequestContext p_requestCtx, final RsToken p_token) {
		// Beware with multivalued map, for now getFirst only !

		// Get the key for retrieving the token.
		final String v_key = p_token.get_key();

		// The token to retrieve and to decode.
		String v_tokenToDecode = null;

		// Default offset for the token.
		int v_offset = 0;

		// Retrieve token in request json body.
		if (p_token.isPassedByBody()) {
			v_tokenToDecode = new JSONObject(p_requestCtx.getEntityStream()).getString(v_key);
		}

		// Retrieve token from Header.
		else if (p_token.isPassedByHeader()) {
			v_tokenToDecode = p_requestCtx.getHeaderString(v_key);
		}

		// Retrieve token from Cookie.
		else if (p_token.isPassedByCookie()) {
			v_tokenToDecode = p_requestCtx.getCookies().get(v_key).getValue();
		}

		// Retrieve token from Query parameter.
		else if (p_token.isPassedByQuery()) {
			v_tokenToDecode = p_requestCtx.getUriInfo().getQueryParameters().getFirst(v_key);
		}

		// Retrieve token from Path parameter (why not ...!)
		else if (p_token.isPassedByPath()) {
			v_tokenToDecode = p_requestCtx.getUriInfo().getPathParameters().getFirst(v_key);
		}

		// Check for token existence.
		if (null == v_tokenToDecode || v_tokenToDecode.isEmpty()) {
			throw new RsTokenNotFoundException();
		}

		// Check for a possible offset (prefix) ?
		if (null != p_token.get_prefixedBy() && !p_token.get_prefixedBy().isEmpty()) {
			v_offset = (p_token.get_prefixedBy().length() + 1);
		}

		// Check the substring capacity...
		if (v_offset > 0 && v_tokenToDecode.length() < v_offset) {
			throw new JwtException("Erreur de longueur pour le jeton, vérifier la chaine envoyée !");
		}

		// Do the possible substring on the initial string token and return directly the
		// token without the prefix.
		if (v_offset > 0) {
			return v_tokenToDecode.substring(v_offset);
		}

		// Return the token.
		return v_tokenToDecode;
	}

	/**
	 * Check if a refresh token exists and is valid. If so, create and send a new
	 * valid access token.
	 * <p>
	 * Only to use with API KEY authentication protocol !
	 *
	 * @param p_requestCtx : The request context for the call.
	 */
	private void checkForRefreshToken(final ContainerRequestContext p_requestCtx) {
		// Try processing the refresh for an access token.
		if (hasToCheckRefreshToken()) {
			// List all tokens in the tokens container.
			for (final RsToken v_token : _filterConfig.get_tokensContainer().get_tokens()) {
				// If the token definition is eligible (it is a refresh token with apiKey
				// protocol).
				if (v_token.isRefresh() && RsAuthProtocol_Enum.apiKey == v_token.get_authProtocol()) {
					// Decode the refresh token.
					final Claims v_refreshClaims = decodeToken(p_requestCtx, v_token);

					// Get out if the refresh token is invalid.
					if (null == v_refreshClaims) {
						throw new RsUnauthorizedException();
					}

					// Let the developer check the refresh token (if wanted by the developer).
					provideClaimsToApplication(v_token.get_spi4jId(), v_refreshClaims);

					// Look for the access token definition.
					final RsToken v_accessToken = _filterConfig.get_tokensContainer()
							.get_token(v_token.get_linkedSpi4jId());

					// Decode (again) the initial access token with an extended timeout to bypass
					// the expired date.
					final Claims v_claims = RsTokenHelper.decodeToken(v_accessToken,
							getTokenFromRequest(p_requestCtx, v_accessToken),
							RsConstants.c_token_default_allwd_after_refresh_nbsec);

					// Send the new access token to the client.
					abortWithRefreshedToken(p_requestCtx, buildRefreshedTokenResponse(
							RsAuthTokenXtoWrapper.create(v_accessToken.get_spi4jId(), v_claims)));
				}
			}
		}
	}

	/**
	 * Check first if REST resources are "granted" for the application.
	 */
	private void checkUnderMaintenance() {
		if (!RsAccess.isAccessGranted()) {
			throw new RsServiceClosedException();
		}
	}

	/**
	 * Check if the current token to decode is associated with the current resource.
	 * <p>
	 * Be sure to check the annotation is present (not null) before calling this
	 * method !
	 * <p>
	 * Refresh tokens are not present in the annotation, so they will not be
	 * processed.
	 *
	 * @param v_token : The current token to decode.
	 * @return true if the token is associated with the current resource.
	 */
	private boolean isTokenToProcess(final RsToken v_token) {
		return _resourceInfo.getResourceMethod().getAnnotation(RsSecured.class).tokens()
				.indexOf(v_token.get_spi4jId()) != -1;
	}

	/**
	 * Check if the filter has to check a refresh token.
	 * <p>
	 * Only for use with API KEY authentication protocol !
	 * <p>
	 * If this method is called, the resource is secured so the annotation can't be
	 * null !
	 *
	 * @return true if all conditions are validated for checking a refresh token.
	 */
	private boolean isRefreshMethod() {
		return _resourceInfo.getResourceMethod().isAnnotationPresent(RsRefreshToken.class);
	}

	/**
	 * Check if the specific resource is secured (@RsSecured annotation).
	 *
	 * @return True if the method contains the "RsSecured" annotation.
	 */
	private boolean isSecuredMethod() {
		return _resourceInfo.getResourceMethod().isAnnotationPresent(RsSecured.class);
	}

	/**
	 * Check if the filter has to check a token.
	 *
	 * @return true if the resource is secured and the authentication protocol need
	 *         a token.
	 */
	private boolean hasToCheckTokens() {
		return isSecuredMethod() || _filterConfig.get_tokensContainer().hasApplicativeOrIdentificationTokens();
	}

	/**
	 * Check if the called resource is for refreshing an access token.
	 * <p>
	 * Work only for an apiKey authentication protocol !
	 * <p>
	 * For now, no control is made for the refresh token existence and/or validity.
	 *
	 * @return true if the resource is for refresh and the authentication protocol
	 *         is apiKey.
	 */
	private boolean hasToCheckRefreshToken() {
		return isRefreshMethod(); // to be continued... ?
	}

	/**
	 * Complete the response header with additional informations () .
	 *
	 * @param p_requestCtx  : provides request-specific information for the filter.
	 * @param p_responseCtx : provides response-specific information for the filter.
	 */
	private void addDefaultHeaders(final ContainerRequestContext p_requestCtx,
			final ContainerResponseContext p_responseCtx) {
		// RAS.
	}

	/**
	 * Complete the response header with additional PES informations () .
	 *
	 * @param p_requestCtx  : provides request-specific information for the filter.
	 * @param p_responseCtx : provides response-specific information for the filter.
	 */
	private void addPESHeader(final ContainerRequestContext p_requestCtx,
			final ContainerResponseContext p_responseCtx) {
		if (_filterConfig.isPESHeadersRequired()) {
			// Set the headers needed for PES.
			p_responseCtx.getHeaders().add(_filterConfig.get_pesHeaderSecuKey(), _filterConfig.get_pesHeaderSecu());
			p_responseCtx.getHeaders().add(_filterConfig.get_pesHeaderMentionKey(),
					_filterConfig.get_pesHeaderMention());
			p_responseCtx.getHeaders().add(_filterConfig.get_pesHeaderAppKey(), _filterConfig.get_pesApplicationName());
			p_responseCtx.getHeaders().add(_filterConfig.get_pesHeaderDlppKey(), p_requestCtx.getUriInfo().getPath());
			p_responseCtx.getHeaders().add(_filterConfig.get_pesHeaderConstraintKey(),
					_filterConfig.get_pesHeaderConstraint());
		}
	}

	/**
	 * Abort the filter chain with a 200 status code response and the refreshed
	 * access token.
	 *
	 * @param p_requestCtx     : The container for the request context.
	 * @param p_tokenContainer : The new container for access token with extended
	 *                         expiration date.
	 */
	private void abortWithRefreshedToken(final ContainerRequestContext p_requestCtx, final RsXto_Itf p_tokenContainer) {
		p_requestCtx.abortWith(RsResponseHelper.responseForToken(p_tokenContainer));
	}

	/**
	 * Abort the filter chain with a 401 status code response.
	 *
	 * @param p_requestCtx : The container for the request context.
	 * @param p_exception  : The JWT exception to send.
	 */
	private void abortWithUnauthorizedException(final ContainerRequestContext p_requestCtx,
			final Exception p_exception) {
		p_requestCtx.abortWith(RsResponseHelper.responseForException(
				new RsExceptionXtoContainer.Builder().withException(new RsUnauthorizedException(p_exception)).build()));
	}

	/**
	 * Abort the filter chain with a 409 status code response. As a preventive
	 * measure, the code is currently simply duplicated, but it would be possible to
	 * add specific code for this type of exception
	 *
	 * @param p_requestCtx : The container for the request context.
	 * @param p_exception  : The initial oidc exception.
	 */
	private void abortWithOidcSecurityException(final ContainerRequestContext p_requestCtx,
			final RsOidcSecurityException p_exception) {
		p_requestCtx.abortWith(RsResponseHelper
				.responseForException(new RsExceptionXtoContainer.Builder().withException(p_exception).build()));
	}

	/**
	 * Abort the filter chain with a 403 status code response. As a preventive
	 * measure, the code is currently simply duplicated, but it would be possible to
	 * add specific code for this type of exception
	 *
	 * @param p_requestCtx : the container for the request context.
	 * @param p_exception  : the exception to send.
	 */
	private void abortWithServiceClosedException(final ContainerRequestContext p_requestCtx,
			final RsServiceClosedException p_exception) {
		p_requestCtx.abortWith(RsResponseHelper
				.responseForException(new RsExceptionXtoContainer.Builder().withException(p_exception).build()));
	}

	/**
	 * Abort the filter chain with a XXX status code response (given by original
	 * exception).
	 *
	 * @param p_requestCtx : the container for the request context.
	 * @param p_exception  : the exception to send.
	 */
	private void abortWithRsException(final ContainerRequestContext p_requestCtx, final RsException_Abs p_exception) {
		p_requestCtx.abortWith(RsResponseHelper
				.responseForException(new RsExceptionXtoContainer.Builder().withException(p_exception).build()));
	}

	/**
	 * Abort the filter chain with a 500 status code response.
	 *
	 * @param p_requestCtx : the container for the request context.
	 * @param p_exception  : the exception to send.
	 */
	private void abortWithUnexpectedException(final ContainerRequestContext p_requestCtx, final Exception p_exception) {
		p_requestCtx.abortWith(RsResponseHelper.responseForException(
				new RsExceptionXtoContainer.Builder().withException(new RsUnexpectedException(p_exception)).build()));
	}

	/**
	 * Abort the filter chain with a 500 status code response.
	 * <p>
	 * The exception is thrown on the last filtering phase (Response has already
	 * been created)
	 *
	 * @param p_responseCtx : the container for the response context.
	 * @param p_exception   : the exception to send.
	 */
	private void abortWithException(final ContainerResponseContext p_responseCtx, final Exception p_exception) {
		p_responseCtx.setStatus(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		// p_responseCtx.setEntity(RsExceptionContainerFactory.build(p_exception));
	}

	/**
	 * Retrieve the filter configuration.
	 *
	 * @return The configuration for the generic filter.
	 */
	public static RsFilterConfigurator get_config() {
		return _filterConfig;
	}

	/**
	 * Let the developer do some preliminary work before the filter.
	 *
	 * @param p_requestCtx : The container for the request context.
	 */
	protected abstract void beforeFilter(final ContainerRequestContext p_requestCtx);

	/**
	 * Let the developer do some complementary work after the filter.
	 *
	 * @param p_responseCtx : The container for the response context.
	 */
	protected abstract void afterFilter(final ContainerResponseContext p_responseCtx);

	/**
	 * Get the configuration for the application and store in the filter.
	 *
	 * @return The configuration for the operating filter.
	 */
	protected abstract RsFilterConfigurator initFilterConfig();

	/**
	 * Let the developer transfer token data(s) (claims) to spi4j.
	 *
	 * @param p_spi4jId : The internal spi4j Id for the token.
	 * @param p_claims  : The claims with all informations to copy for the SPI4J
	 *                  security or applicative.
	 */
	protected abstract void provideClaimsToApplication(final String p_spi4jId, final Claims p_claims);

	/**
	 * Let the developer to specify the required tokens to be created and checked.
	 *
	 * @return The container for all defined and required tokens.
	 */
	protected abstract RsTokensConfigurator_Itf initTokensConfig();

	/**
	 * Let the developer set the header keys for paging. No Map for simplification
	 * but need a specific order !
	 *
	 * @return The key tab.
	 */
	protected abstract String[] initPaginatorHeaderKeys();

	/**
	 * @param p_tokenWrapper : The new wrapper with the refresh access token.
	 * @return The interface for the new refreshed token container.
	 */
	protected abstract RsXto_Itf buildRefreshedTokenResponse(final RsAuthTokenXtoWrapper p_tokenWrapper);

}
