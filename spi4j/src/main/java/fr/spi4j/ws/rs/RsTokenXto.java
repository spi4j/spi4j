/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * A default minimalist container for a token.
 *
 * @author MINARM.
 */
@XmlRootElement(name = "RsTokenXto")
@XmlAccessorType(XmlAccessType.FIELD)
public class RsTokenXto implements RsXto_Itf {

	/**
	 * Default serial id
	 */
	private static final long serialVersionUID = 2012914687745264702L;

	/**
	 * The token.
	 */
	@JsonProperty("token")
	private final String _token;

	/**
	 * The type for the access token (Bearer, etc...).
	 */
	@JsonProperty("type")
	private final String _type;

	/**
	 * The expiration time in seconds for the access token.
	 */
	@JsonProperty("expires")
	private final long _expires;

	/**
	 * Constructor.
	 *
	 * @param p_tokenWrapper :
	 */
	public RsTokenXto(final RsAuthTokenXtoWrapper p_tokenWrapper) {
		_token = p_tokenWrapper.get_accesstoken();
		_type = p_tokenWrapper.get_accessTokenType();
		_expires = p_tokenWrapper.get_accesTokenExpiresIn();
	}

	/**
	 * Constructor.
	 *
	 * @param p_token  : the token.
	 * @param p_type   : the type for the token (access, id, refresh, applicative
	 *                 ...)
	 * @param p_expire : the expiration time for the token.
	 */
	public RsTokenXto(final String p_token, final String p_type, final long p_expire) {
		_token = p_token;
		_type = p_type;
		_expires = p_expire;
	}

	/**
	 * Retrieve the token.
	 *
	 * @return The token.
	 */
	public String get_token() {
		return _token;
	}

	/**
	 * Retrieve the type for the token.
	 *
	 * @return The type for the token.
	 */
	public String get_type() {
		return _type;
	}

	/**
	 * Retrieve the expiration time in seconds for the token.
	 *
	 * @return The expiration in seconds for the token.
	 */
	public long get_expires() {
		return _expires;
	}
}
