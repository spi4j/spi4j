package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * This exception let the developer to customize the response status code and
 * the exception message.
 *
 * @author MINARM
 */
public class RsServiceCustomException extends RsException_Abs {

	private static final long serialVersionUID = -1684743554685687915L;

	/**
	 * The status for the response (specified by developer).
	 */
	private final Status _status;

	/**
	 * The message for the exception (specified by developer).
	 */
	private final String _message;

	/**
	 * Constructor.
	 *
	 * @param p_status    : the HTTP status to be defined for the response.
	 * @param p_message   : the message for the exception.
	 * @param p_exception the cause exception.
	 */
	public RsServiceCustomException(final Status p_status, final String p_message, final Throwable p_exception) {
		super(p_exception);
		_status = p_status;
		_message = p_message;
	}

	/**
	 * Constructor.
	 *
	 * @param p_status  : the HTTP status to be defined for the response.
	 * @param p_message : the message for the exception.
	 */
	public RsServiceCustomException(final Status p_status, final String p_message) {
		super();
		_status = p_status;
		_message = p_message;
	}

	/**
	 * Constructor.
	 *
	 * @param p_code      : the HTTP code to be defined for the response.
	 * @param p_message   : the message for the exception.
	 * @param p_exception the cause exception.
	 */
	public RsServiceCustomException(final int p_code, final String p_message, final Throwable p_exception) {
		super(p_exception);
		_status = buildReponseStatus(p_code);
		_message = p_message;
	}

	/**
	 * Constructor.
	 *
	 * @param p_code    : the HTTP code to be defined for the response.
	 * @param p_message : the message for the exception.
	 */
	public RsServiceCustomException(final int p_code, final String p_message) {
		super();
		_status = buildReponseStatus(p_code);
		_message = p_message;
	}

	/**
	 * Try to get the status response from the code sent by developer.
	 *
	 * @param p_code : the code for the status of the response.
	 * @return the status for the response.
	 */
	private Status buildReponseStatus(final int p_code) {
		final Status v_status = Status.fromStatusCode(p_code);

		if (null == v_status) {
			// TODO
			return Status.INTERNAL_SERVER_ERROR;

		}
		return v_status;
	}

	@Override
	public Status get_status() {
		return _status;
	}

	@Override
	public String get_defaultMessage() {
		return _message;
	}

	/**
	 * Associated default xto container for the exception.
	 */
	@Override
	public RsExceptionXtoContainer_Itf get_xtoContainer() {
		return new RsExceptionXtoDefaultContainer(this);
	}
}
