/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.spi4j.ws.rs.RsXto_Itf;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Format the exception in a specific container to be sent in the response
 * object.
 *
 * @author MINARM
 */
public final class RsExceptionXtoContainer implements RsXto_Itf {

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
	private final Status _status;

	/**
	 * The reason phrase for the status.
	 */
	@JsonProperty("reason")
	private final String _reason;

	/**
	 * The explicit message that can be displayed to the user.
	 */
	@JsonProperty("message")
	private final String _message;

	/**
	 * The complete name of the exception.
	 */
	@JsonProperty("exceptionClass")
	private final String _exceptionClass;

	/**
	 * The cause which can be null.
	 */
	@JsonProperty("exceptionCause")
	private final String _exceptionCause;

	private RsExceptionXtoContainer(final String p_code, final Status p_status, final String p_reason,
			final String p_message, final String p_exceptionClass, final String p_exceptionClause) {
		_code = p_code;
		_status = p_status;
		_reason = p_reason;
		_message = p_message;
		_exceptionCause = p_exceptionClause;
		_exceptionClass = p_exceptionClass;
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

	/**
	 * The builder for the exception container.
	 *
	 * @author MINARM
	 */
	public static class Builder {
		private RsException_Abs _exception;

		/**
		 * Add the exception to the builder for the container.
		 *
		 * @param p_exception the exception to be contained.
		 * @return The instance of the builder for the container.
		 */
		public Builder withException(final RsException_Abs p_exception) {
			_exception = p_exception;
			return this;
		}

		/**
		 * Build the new container for the exception.
		 *
		 * @return The new container for the exception.
		 */
		public RsExceptionXtoContainer build() {
			String v_exceptionCause = null;
			Status p_status = _exception.get_status();
			String v_message = _exception.getMessage();

			if (null != _exception.getCause()) {
				v_exceptionCause = _exception.getCause().toString();
			}

			if (null == p_status) {
				p_status = Status.INTERNAL_SERVER_ERROR;
			}

			if (_exception.is_displayDefaultMessage() || null == v_message || v_message.isEmpty()) {
				v_message = _exception.get_defaultMessage();
			}

			final int v_statusCode = p_status.getStatusCode();
			final String v_reason = Response.Status.fromStatusCode(v_statusCode).getReasonPhrase();
			final String v_exceptionClass = _exception.getClass().toString();

			return new RsExceptionXtoContainer(Integer.toString(v_statusCode), p_status, v_reason, v_message,
					v_exceptionClass, v_exceptionCause);
		}
	}
}
