/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exception when executing Spi4J for a REST service.
 *
 * @author MINARM
 */
public class RsPagingException extends RsException_Abs {

	/**
	 * SerialUid.
	 */
	private static final long serialVersionUID = -1829405368219841211L;

	/**
	 * Constructor.
	 */
	public RsPagingException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception : The initial exception.
	 */
	public RsPagingException(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : A specific message for the exception.
	 */
	public RsPagingException(final String p_message) {
		super(p_message);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message   : A specific message for the exception.
	 * @param p_exception : The initial exception.
	 */
	public RsPagingException(final String p_message, final Throwable p_exception) {
		super(p_message);
		super.initCause(p_exception);
	}

	/**
	 * HTTP status corresponding for the exception.
	 */
	@Override
	public Status get_status() {
		return Status.BAD_REQUEST; // TODO
	}

	/**
	 * Default corresponding message for the exception.
	 */
	@Override
	public String get_defaultMessage() {
		return "Erreur de pagination, vérifiez les paramètres de pagination.";
	}

	/**
	 * Associated default xto container for the exception.
	 */
	@Override
	public RsExceptionXtoContainer_Itf get_xtoContainer() {
		return new RsExceptionXtoDefaultContainer(this);
	}
}
