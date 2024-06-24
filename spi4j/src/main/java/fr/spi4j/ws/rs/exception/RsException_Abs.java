/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import java.text.MessageFormat;

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
	 * Constructor.
	 *
	 * @param p_cause   : The original exception.
	 * @param p_message : The message to be displayed.
	 */
	public RsException_Abs(final Throwable p_cause, final String p_message) {
		super(p_message, p_cause);
	}

	/**
	 * Constructor
	 *
	 * @param p_cause   : The original exception.
	 * @param p_message : The message to be displayed.
	 * @param p_params  : Parameters to be injected in the original message.
	 */
	public RsException_Abs(final Throwable p_cause, final String p_message, final Object... p_params) {
		super(RsException_Abs.formatMessage(p_message, p_params), p_cause);
	}

	/**
	 * Utility method for message formatting with unlimited number of parameters.
	 * Use <code>{0}, {1}, etc...</code>
	 *
	 * @param p_message : The message to be displayed.
	 * @param p_params  : Parameters to be injected in the original message.
	 * @return The formatted messages with all parameters.
	 */
	private static String formatMessage(final String p_message, final Object... p_params) {
		if (p_params.length == 0) {
			return p_message;
		}
		return MessageFormat.format(p_message, p_params);
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

	/**
	 * Each Rs exception must define his proper xto container. By default the
	 * container is the {@code RsExceptionXtoDefaultContainer} class. If the
	 * developer create a new Rs Exception, he can also create an new xto container
	 * and associate the container with the specific exception. By this way, he will
	 * obtain a specific Json output format for the exception.
	 *
	 * @return The Xto container for exception format
	 */
	public abstract RsExceptionXtoContainer_Itf get_xtoContainer();
}
