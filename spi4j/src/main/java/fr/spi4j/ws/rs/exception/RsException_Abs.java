/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Super class for exception when executing Spi4J for a REST service.
 *
 * @author MINARM
 */
public abstract class RsException_Abs extends RuntimeException {
	/**
	 * If set to TRUE, the displayed message will be the one written by default in
	 * the body of the exception.
	 */
	private boolean _displayDefaultMessage = Boolean.TRUE;

	/**
	 * SerialUid.
	 */
	private static final long serialVersionUID = -39023619274710529L;

	/**
	 * Constructor.
	 */
	public RsException_Abs() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception : the original exception.
	 */
	public RsException_Abs(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : The message to be displayed.
	 */
	public RsException_Abs(final String p_message) {
		super(p_message);
		_displayDefaultMessage = Boolean.FALSE;
	}

	/**
	 * Get the indicator for displaying the default message.
	 *
	 * @return true if the default message must be displayed.
	 */
	public boolean is_displayDefaultMessage() {
		return _displayDefaultMessage;
	}

	/**
	 * Get the status object for the exception.
	 *
	 * @return The response status to send at the caller of a REST service.
	 */
	public abstract Status get_status();

	/**
	 * Get the default message for the exception if none in parameter.
	 *
	 * @return The default message to display for the user.
	 */
	public abstract String get_defaultMessage();
}
