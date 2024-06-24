/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exception when executing Spi4J for a REST service. This exception is thrown
 * when the caller has not been properly authenticated.
 *
 * @author MINARM
 */
public class RsUnauthorizedException extends RsException_Abs {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 7109553870748104521L;

	/**
	 * Constructor.
	 */
	public RsUnauthorizedException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception : The initial exception.
	 */
	public RsUnauthorizedException(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : A default specific message for the exception.
	 */
	public RsUnauthorizedException(final String p_message) {
		super(p_message);
	}

	/**
	 * HTTP status corresponding for the exception.
	 */
	@Override
	public Status get_status() {
		return Status.UNAUTHORIZED;
	}

	/**
	 * Default corresponding message for the exception.
	 */
	@Override
	public String get_defaultMessage() {
		return "Accès non autorisé.";
	}

	/**
	 * Associated default xto container for the exception.
	 */
	@Override
	public RsExceptionXtoContainer_Itf get_xtoContainer() {
		return new RsExceptionXtoDefaultContainer(this);
	}
}
