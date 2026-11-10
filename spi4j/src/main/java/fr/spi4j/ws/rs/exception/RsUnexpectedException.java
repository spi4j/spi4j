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
}
