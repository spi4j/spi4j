/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Format the exception in a default container. This container will be sent in
 * the response object.
 *
 * @author MINARM
 */
public final class RsExceptionXtoDefaultContainer implements RsExceptionXtoContainer_Itf {

	/**
	 * Default serial id.
	 */
	private static final long serialVersionUID = 8199623788934314390L;

	/**
	 * The HTTP status code for the exception.
	 */
	@JsonProperty("code")
	private final String _code;

	/**
	 * The HTTP status for the exception.
	 */
	@JsonProperty("status")
	private Status _status;

	/**
	 * The reason phrase for the status.
	 */
	@JsonProperty("reason")
	private final String _reason;

	/**
	 * The explicit message that can be displayed to the user.
	 */
	@JsonProperty("message")
	private String _message;

	/**
	 * The complete name of the exception.
	 */
	@JsonProperty("exceptionClass")
	private final String _exceptionClass;

	/**
	 * The cause which can be null.
	 */
	@JsonProperty("exceptionCause")
	private String _exceptionCause;

	/**
	 * Constructor for the default exception container.
	 *
	 * @param p_exception the initial Rest Exception
	 */
	public RsExceptionXtoDefaultContainer(final RsException_Abs p_exception) {

		_status = p_exception.get_status();
		_message = p_exception.getMessage();

		if (null != p_exception.getCause()) {
			_exceptionCause = p_exception.getCause().toString();
		}

		if (null == _status) {
			_status = Status.INTERNAL_SERVER_ERROR;
		}

		if (p_exception.is_displayDefaultMessage() || null == _message || _message.isEmpty()) {
			_message = p_exception.get_defaultMessage();
		}

		_code = Integer.toString(_status.getStatusCode());
		_reason = Response.Status.fromStatusCode(_status.getStatusCode()).getReasonPhrase();
		_exceptionClass = p_exception.getClass().toString();
	}

	/**
	 * Get the message for the exception.
	 *
	 * @return The message for the exception.
	 */
	public String get_message() {
		return _message;
	}

	/**
	 * Get the class for the exception.
	 *
	 * @return The class of the exception.
	 */
	public String get_exceptionClass() {
		return _exceptionClass;
	}

	/**
	 * Get the exception cause.
	 *
	 * @return The exception cause if exits.
	 */
	public String get_exceptionCause() {
		return _exceptionCause;
	}

	/**
	 * Get the status for the response object.
	 *
	 * @return The status for the response.
	 */
	@Override
	public Status get_status() {
		return _status;
	}

	/**
	 * Get the reason phrase for the response object.
	 *
	 * @return The reason phrase for the response.
	 */
	public String get_reason() {
		return _reason;
	}

	/**
	 * Get the return HTTP code for the response.
	 *
	 * @return The HTTP code for the response.
	 */
	public String get_code() {
		return _code;
	}
}
