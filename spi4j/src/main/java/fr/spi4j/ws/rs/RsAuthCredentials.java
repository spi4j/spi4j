/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.io.Serializable;
import java.util.Base64;

/**
 * Credentials storage for API client (caller). This class is for the
 * application credentials and not for PES.
 *
 * Keep in mind that Base64 is an encoding, and encoding is not encryption.
 * Anything encoded in Base64 is intentionally easy to decode.
 *
 * The Base64 encoding, most importantly, ensures that the user:pass characters
 * are all part of the ASCII character set and ASCII encoded. A user:pass in
 * HTTP Basic auth is part of the Authorization header-field value. HTTP header
 * values are ASCII (or Extended ASCII) encoded/decoded. So, when you Base64
 * encode a user:pass, you ensure that it is ASCII, and is therefore a valid
 * header-field value.
 *
 * Base64 encoding also adds at least some kind of obfuscation to the clear-text
 * user:pass. Again, this is not encryption. But, it does prevent normal humans
 * from reading a user:pass at a glance.
 *
 * @author MINARM
 */

public class RsAuthCredentials implements Serializable {

	/**
	 * Default generated serial id.
	 */
	private static final long serialVersionUID = -7776947383682632157L;

	/**
	 * The user name (login).
	 */
	private String _userName;

	/**
	 * The password.
	 */
	private String _passwd;

	/**
	 * The encoded base 64 credentials.
	 */
	private String _credentials;

	/**
	 * Default public constructor for rest instantiation.
	 */
	RsAuthCredentials() {
		// RAS.
	}

	/**
	 * Constructor. No exception thrown here, just do nothing.
	 *
	 * @param p_credentials : the login and passwd for authentication.
	 */
	RsAuthCredentials(final String[] p_credentials) {
		// Quick control and escape if fail.
		if (p_credentials.length < 2) {
			return;
		}
		_userName = p_credentials[0];
		_passwd = p_credentials[1];
	}

	/**
	 * Constructor.
	 *
	 * @param p_userName : Login storage for the caller.
	 * @param p_passwd   : Password storage for the caller.
	 */
	public RsAuthCredentials(final String p_userName, final String p_passwd) {
		_credentials = RsConstants.c_auth_header_basic + " "
				+ Base64.getEncoder().encodeToString((p_userName + ":" + p_passwd).getBytes());
	}

	/**
	 * Get the user name for the caller.
	 *
	 * @return the user name.
	 */
	public String get_userName() {
		return _userName;
	}

	/**
	 * Get the password for the caller.
	 *
	 * @return the password.
	 */
	public String get_passwd() {
		return _passwd;
	}

	/**
	 * Get the base 64 basic authorization credentials.
	 *
	 * @return the credentials with base64.
	 */
	public String get_credentials() {
		return _credentials;
	}
}
