/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exception when a REST resource does not exist.
 *
 * @author MINARM.
 */
public class RsServiceNotFoundException extends RsException_Abs {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -3349096441465370286L;

	/**
	 * Constructor.
	 */
	public RsServiceNotFoundException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception : The initial exception.
	 */
	public RsServiceNotFoundException(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : A specific message for the exception.
	 */
	public RsServiceNotFoundException(final String p_message) {
		super(p_message);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message   : A specific message for the exception.
	 * @param p_exception : The initial exception.
	 */
	public RsServiceNotFoundException(final String p_message, final Throwable p_exception) {
		super(p_message);
		super.initCause(p_exception);
	}

	/**
	 * HTTP status corresponding for the exception.
	 */
	@Override
	public Status get_status() {
		return Status.NOT_FOUND;
	}

	/**
	 * Default corresponding message for the exception.
	 */
	@Override
	public String get_defaultMessage() {
		return "Le service n'a pas été trouvé, vérifier l'URI.";
	}

	/**
	 * Associated default xto container for the exception.
	 */
	@Override
	public RsExceptionXtoContainer_Itf get_xtoContainer() {
		return new RsExceptionXtoDefaultContainer(this);
	}
}
