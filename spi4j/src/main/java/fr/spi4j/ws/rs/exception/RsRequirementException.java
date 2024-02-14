/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exception when executing Spi4J for a REST service. This exception is thrown
 * when the required functional parameters are missing or wrong.
 *
 * @author MINARM
 */
public class RsRequirementException extends RsException_Abs {

	/**
	 * SerialUid.
	 */
	private static final long serialVersionUID = -2274851643663716837L;

	/**
	 * Constructor.
	 */
	public RsRequirementException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception : The initial exception.
	 */
	public RsRequirementException(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : A specific message for the exception.
	 */
	public RsRequirementException(final String p_message) {
		super(p_message);
	}

	/**
	 * HTTP status corresponding for the exception.
	 */
	@Override
	public Status get_status() {
		return Status.PRECONDITION_FAILED;
	}

	/**
	 * Default corresponding message for the exception.
	 */
	@Override
	public String get_defaultMessage() {
		return "Pré-conditions fonctionnelles non remplies.";
	}
}
