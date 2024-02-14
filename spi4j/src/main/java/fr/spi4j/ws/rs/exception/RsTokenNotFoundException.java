/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * @author MINARM.
 */
public class RsTokenNotFoundException extends RsException_Abs {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -2762756906114714581L;

	/**
	 * Constructor.
	 */
	public RsTokenNotFoundException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param p_message : A specific message for the exception.
	 */
	public RsTokenNotFoundException(final String p_message) {
		super(p_message);
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
		return "Auncun jeton trouvé !";
	}

}
