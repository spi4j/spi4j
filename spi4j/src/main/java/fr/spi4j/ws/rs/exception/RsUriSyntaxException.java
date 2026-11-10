/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * @author MINARM
 */
public class RsUriSyntaxException extends RsException_Abs {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 2717721916798230640L;

	/**
	 * Constructor.
	 */
	public RsUriSyntaxException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_exception The initial exception.
	 */
	public RsUriSyntaxException(final Throwable p_exception) {
		super(p_exception);
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : A specific message for the exception.
	 */
	public RsUriSyntaxException(final String p_message) {
		super(p_message);
	}

	/**
	 * HTTP status corresponding for the exception.
	 */
	@Override
	public Status get_status() {
		return Status.INTERNAL_SERVER_ERROR;
	}

	@Override
	public String get_defaultMessage() {
		return "Erreur de syntaxe dans la construction d'une URI.";
	}

}
