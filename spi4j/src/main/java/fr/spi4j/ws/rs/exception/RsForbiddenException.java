/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exception when executing Spi4J for a REST service. This exception is thrown
 * when the caller does not exist in the internal base.
 *
 * @author MINARM
 */
public class RsForbiddenException extends RsException_Abs {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1478346092817590592L;

	/**
	 * Constructor.
	 */
	public RsForbiddenException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception : The initial exception.
	 */
	public RsForbiddenException(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : A default specific message for the exception.
	 */
	public RsForbiddenException(final String p_message) {
		super(p_message);
	}

	/**
	 * HTTP status corresponding for the exception.
	 */
	@Override
	public Status get_status() {
		return Status.FORBIDDEN;
	}

	/**
	 * Default corresponding message for the exception.
	 */
	@Override
	public String get_defaultMessage() {
		return "Utilisateur non trouvé en base";
	}

	/**
	 * Associated default xto container for the exception.
	 */
	@Override
	public RsExceptionXtoContainer_Itf get_xtoContainer() {
		return new RsExceptionXtoDefaultContainer(this);
	}
}
