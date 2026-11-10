/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exception when executing Spi4J for a REST service. This exception is thrown
 * when the parameters of the request are missing or incorrect.
 *
 * @author MINARM
 */
public class RsParameterException extends RsException_Abs {

	/**
	 * SerialUid.
	 */
	private static final long serialVersionUID = 6299457809522189645L;

	/**
	 * Constructor.
	 */
	public RsParameterException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception : The initial exception.
	 */
	public RsParameterException(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor
	 *
	 * @param p_message : A specific message for the exception.
	 */
	public RsParameterException(final String p_message) {
		super(p_message);
	}

	/**
	 * HTTP status corresponding for the exception.
	 */
	@Override
	public Status get_status() {
		return Status.BAD_REQUEST;
	}

	/**
	 * Default corresponding message for the exception.
	 */
	@Override
	public String get_defaultMessage() {
		return "La requête n'est pas correctement constituée.";
	}
}
