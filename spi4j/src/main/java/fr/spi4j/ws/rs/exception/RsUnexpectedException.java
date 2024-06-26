/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exception when executing Spi4J for a REST service. This exception is thrown
 * when an unforeseen exception has been caught.
 *
 * @author MINARM
 */
public class RsUnexpectedException extends RsException_Abs {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -1970337404819466831L;

	/**
	 * Constructor.
	 */
	public RsUnexpectedException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception The initial exception.
	 */
	public RsUnexpectedException(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : A specific message for the exception.
	 */
	public RsUnexpectedException(final String p_message) {
		super(p_message);
	}

	/**
	 * Constructor.
	 *
	 * @param p_cause   : The original exception.
	 * @param p_message : The message to be displayed.
	 */
	public RsUnexpectedException(final Throwable p_cause, final String p_message) {
		super(p_cause, p_message);
	}

	/**
	 * Constructor
	 *
	 * @param p_cause   : The original exception.
	 * @param p_message : The message to be displayed.
	 * @param p_params  : Parameters to be injected in the original message.
	 */
	public RsUnexpectedException(final Throwable p_cause, final String p_message, final Object... p_params) {
		super(p_cause, p_message, p_params);
	}

	/**
	 * HTTP status corresponding for the exception.
	 */
	@Override
	public Status get_status() {
		return Status.INTERNAL_SERVER_ERROR;
	}

	@Override
	public String get_defaultMessage() {
		return "Erreur inattendue.";
	}

	/**
	 * Associated default xto container for the exception.
	 */
	@Override
	public RsExceptionXtoContainer_Itf get_xtoContainer() {
		return new RsExceptionXtoDefaultContainer(this);
	}
}
