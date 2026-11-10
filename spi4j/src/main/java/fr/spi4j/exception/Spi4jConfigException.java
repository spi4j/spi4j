/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.exception;

/**
 * @author MinArm
 */
public class Spi4jConfigException extends Spi4jExtendedRuntimeException {

	/**
	 * Default serial id.
	 */
	private static final long serialVersionUID = -5479467487414632553L;

	/**
	 *
	 * @param p_message
	 */
	public Spi4jConfigException(final String p_message) {
		super(p_message);
	}

	/**
	 *
	 * @param p_message
	 * @param p_params
	 */
	public Spi4jConfigException(final String p_message, final Object... p_params) {
		super(p_message, p_params);
	}

	/**
	 *
	 * @param p_cause
	 * @param p_message
	 * @param p_params
	 */
	public Spi4jConfigException(final Throwable p_cause, final String p_message, final Object... p_params) {
		super(p_cause, p_message, p_params);
	}
}
