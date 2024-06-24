package fr.spi4j.ws.rs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import fr.spi4j.ws.rs.exception.RsUnexpectedException;

/**
 * Container for authentication and applicative tokens.
 *
 * @author MINARM.
 */
public class RsTokensContainer {

	// Error messages.
	private static final String c_err_msg1 = "L'identifiant interne SPI4J doit obligatoirement être saisi pour le jeton !";

	private static final String c_err_msg2 = "Deux jetons ne peuvent avoir le même identifiant interne SPI4J !";

	private static final String c_err_msg3 = "L'identifiant interne de liaison SPI4J doit obligatoirement être saisi pour le jeton de rafraîchissement!";

	private static final String c_err_msg4 = "Auncun jeton d'accès n'a été trouvé pour le jeton de rafraîchissement !";

	private static final String c_err_msg5 = "Le jeton de rafraîchissement doit obligatoirement être sous protocol apiKey !";

	private static final String c_err_msg6 = "Deux jetons ne peuvent avoir la même clé ! Changez la clé pour un des jetons.";

	/**
	 * Some of token properties from a configuration file (if exists).
	 */
	private Properties _props;

	/**
	 * The ordered list with all required tokens.
	 */
	private final List<RsToken> _lstTokens;

	/**
	 * True if the container has applicative or id tokens.
	 */
	private boolean _appIdTokens;

	/**
	 * Constructor.
	 */
	RsTokensContainer() {
		_lstTokens = new ArrayList<>();
	}

	/**
	 * Try to initiate a 'properties' object from a tokens configuration file (if
	 * exists).
	 *
	 * @param p_configPathPrefix : The full base path for the configuration
	 *                           directory (with prefix).
	 */
	void loadProperties(final String p_configPathPrefix) {
		// Load the file and put all in a 'properties' object.
		try (final InputStream v_inputStream = new FileInputStream(
				p_configPathPrefix + RsConstants.c_conf_filter_properties_tokens_file)) {
			_props = new Properties();
			_props.load(v_inputStream);
		} catch (final FileNotFoundException p_exception) {
			// Nothing to do.
		} catch (final IOException p_exception) {
			throw new RsUnexpectedException(p_exception);
		}
	}

	/**
	 * Add an access token to the list of required tokens.
	 *
	 * @param p_tokenBuilder : The builder for the token.
	 */
	public void addAccessToken(final RsToken.Builder p_tokenBuilder) {
		addToken(p_tokenBuilder.withType(RsTokenType_Enum.access));
	}

	/**
	 * Add an refresh token to the list of required tokens.
	 *
	 * @param p_tokenBuilder : The builder for the token
	 */
	public void addRefreshToken(final RsToken.Builder p_tokenBuilder) {
		addToken(p_tokenBuilder.withType(RsTokenType_Enum.refresh));
	}

	/**
	 * Add an id token to the list of required tokens.
	 *
	 * @param p_tokenBuilder : The builder for the token
	 */
	public void addIdToken(final RsToken.Builder p_tokenBuilder) {
		addToken(p_tokenBuilder.withType(RsTokenType_Enum.id));
		_appIdTokens = Boolean.TRUE;
	}

	/**
	 * Add an applicative token to the list of required tokens.
	 *
	 * @param p_tokenBuilder : The builder for the token
	 */
	public void addApplicativeToken(final RsToken.Builder p_tokenBuilder) {
		addToken(p_tokenBuilder.withType(RsTokenType_Enum.applicative));
		_appIdTokens = Boolean.TRUE;
	}

	/**
	 * Add the required token to the container.
	 *
	 * <ul>
	 * <li>Build the token configuration.</li>
	 * <li>Check the minimal token validity.</li>
	 * <li>Add the token to the container.</li>
	 * </ul>
	 *
	 * @param p_tokenBuilder : The builder for the specific token.
	 */
	private void addToken(final RsToken.Builder p_tokenBuilder) {
		final RsToken v_token = p_tokenBuilder.build(_props);
		checkTokenValidity(v_token);
		_lstTokens.add(v_token);
	}

	/**
	 * Retrieve the list of all required tokens.
	 *
	 * @return : The list of all required tokens.
	 */
	List<RsToken> get_tokens() {
		return _lstTokens;
	}

	/**
	 * Check if the container has applicative or ID tokens.
	 *
	 * @return true if the container has one or more applicative or ID token(s).
	 */
	boolean hasApplicativeOrIdentificationTokens() {
		return _appIdTokens;
	}

	/**
	 * Check if the container has one or more refresh token.
	 *
	 * @return true if the container has one or more refresh token(s).
	 */
	boolean hasRefreshTokens() {
		return true;
	}

	/**
	 * Check the validity for the new inserted token.
	 *
	 * @param p_token : The token to check.
	 */
	private void checkTokenValidity(final RsToken p_token) {

		// The token must have an internal spi4j id.
		if (null == p_token.get_spi4jId() || p_token.get_spi4jId().isEmpty()) {
			throw new RsUnexpectedException(c_err_msg1);
		}

		// The spi4j internal id must be unique.
		if (null != get_token(p_token.get_spi4jId())) {
			throw new RsUnexpectedException(c_err_msg2);
		}

		// If refresh token, check linked spi4jId.
		if (p_token.isRefresh() && (null == p_token.get_linkedSpi4jId())) {
			throw new RsUnexpectedException(c_err_msg3);
		}

		// If refresh token, check linked spi4jId.
		if (p_token.isRefresh() && p_token.get_linkedSpi4jId().isEmpty()) {
			throw new RsUnexpectedException(c_err_msg3);
		}

		// If refresh token check linked access token.
		if (p_token.isRefresh() && null == get_token(p_token.get_linkedSpi4jId())) {
			throw new RsUnexpectedException(c_err_msg4);
		}

		// If refresh token, check apiKey (other protocols are not defined in backend).
		if (p_token.isRefresh() && RsAuthProtocol_Enum.apiKey != p_token.get_authProtocol()) {
			throw new RsUnexpectedException(c_err_msg5);
		}

		// The passing mode and key must be unique in the tokens container.
		if (!checkPassingMode(p_token)) {
			throw new RsUnexpectedException(c_err_msg6);
		}

		// The other properties are settled by default if not present.
	}

	/**
	 * Very simple method to check in the list of already registered token
	 * configurations if a configuration with same passing mode and same key exists.
	 * This control is very important as the unique distinction between multiple
	 * token configurations (when the filter receive the token) is the passing mode
	 * and the key. Without the check, there is the risk of requesting the decoding
	 * of a token with the wrong token configuration.
	 *
	 * @param p_token : the token configuration to check.
	 * @return false if a token with same key and same passing mode exists.
	 */
	private boolean checkPassingMode(final RsToken p_token) {
		for (final RsToken v_rsToken : _lstTokens) {
			if (v_rsToken.get_passingMode() == p_token.get_passingMode()
					&& v_rsToken.get_key().equalsIgnoreCase(p_token.get_key())) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Retrieve a specific token from his private application id.
	 *
	 * @param p_spi4jId : The private spi4j application id for the token.
	 * @return The requested copy for the token.
	 */
	public RsToken get_token(final String p_spi4jId) {
		for (final RsToken v_rsToken : _lstTokens) {
			if (p_spi4jId.equals(v_rsToken.get_spi4jId())) {
				return v_rsToken;
			}
		}
		// Must return null , not an exception !
		return null;
	}

	/**
	 * Retrieve a specific token copy from his private application id. To be used
	 * for apikey protocol only !
	 *
	 * @param p_spi4jId : The private application id for the token.
	 * @return The requested token copy if exists.
	 */
	RsToken get_tokenCopy(final String p_spi4jId) {
		if (null != get_token(p_spi4jId)) {
			return get_token(p_spi4jId).copy();
		}
		// Must return null , not an exception !
		return null;
	}

	/**
	 * Release the object containing all tokens properties.
	 */
	void releaseProperties() {
		_props = null;
	}
}
