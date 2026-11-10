/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.exception;

import java.text.MessageFormat;

/**
 * @author MinArm
 */
public abstract class Spi4jExtendedRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 7207258586476382546L;

	/**
	 *
	 * @param p_message
	 */
	public Spi4jExtendedRuntimeException(final String p_message) {
		super(p_message);
	}

	/**
	 *
	 * @param p_message
	 * @param p_params
	 */
	public Spi4jExtendedRuntimeException(final String p_message, final Object... p_params) {
		super(Spi4jExtendedRuntimeException.formatMessage(p_message, p_params));
	}

	/**
	 *
	 * @param p_cause
	 */
	public Spi4jExtendedRuntimeException(final Throwable p_cause) {
		super(p_cause);
	}

	/**
	 *
	 * @param p_cause
	 * @param p_message
	 */
	public Spi4jExtendedRuntimeException(final Throwable p_cause, final String p_message) {
		super(p_message, p_cause);
	}

	/**
	 *
	 * @param p_cause
	 * @param p_message
	 * @param p_params
	 */
	public Spi4jExtendedRuntimeException(final Throwable p_cause, final String p_message, final Object... p_params) {
		super(Spi4jExtendedRuntimeException.formatMessage(p_message, p_params), p_cause);
	}

	/**
	 *
	 * @param p_message
	 * @param p_params
	 * @return
	 */
	private static String formatMessage(final String p_message, final Object... p_params) {
		if (p_params.length == 0) {
			return p_message;
		}
		return MessageFormat.format(p_message, p_params);
	}
}
