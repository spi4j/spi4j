/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exception when executing Spi4J for a REST service. This exception is thrown
 * when the server for is closed for maintenance.
 *
 * @author MINARM
 */
public class RsServiceClosedException extends RsException_Abs {
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 8776490030467085347L;

	/**
	 * Constructor.
	 */
	public RsServiceClosedException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception : The initial exception.
	 */
	public RsServiceClosedException(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : A specific message for the exception.
	 */
	public RsServiceClosedException(final String p_message) {
		super(p_message);
	}

	/**
	 * HTTP status corresponding for the exception.
	 */
	@Override
	public Status get_status() {
		return Status.SERVICE_UNAVAILABLE;
	}

	/**
	 * Default corresponding message for the exception.
	 */
	@Override
	public String get_defaultMessage() {
		return "Service fermé pour cause de maintenance.";
	}
}
