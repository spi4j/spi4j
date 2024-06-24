/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exception when executing Spi4J for a REST service. This exception is thrown
 * when security is compromised for OIDC.
 *
 * @author MINARM
 */
public class RsOidcSecurityException extends RsException_Abs {

	/**
	 * SerialUid.
	 */
	private static final long serialVersionUID = 1355600192211984121L;

	/**
	 * Constructor
	 */
	public RsOidcSecurityException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception : The initial exception.
	 */
	public RsOidcSecurityException(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : A specific message for the exception.
	 */
	public RsOidcSecurityException(final String p_message) {
		super(p_message);
	}

	/**
	 * HTTP status corresponding for the exception.
	 */
	@Override
	public Status get_status() {
		return Status.CONFLICT;
	}

	/**
	 * Default corresponding message for the exception.
	 */
	@Override
	public String get_defaultMessage() {
		return "Incident de sécurité dans la communication avec un serveur OIDC.";
	}

	/**
	 * Associated default xto container for the exception.
	 */
	@Override
	public RsExceptionXtoContainer_Itf get_xtoContainer() {
		return new RsExceptionXtoDefaultContainer(this);
	}
}
