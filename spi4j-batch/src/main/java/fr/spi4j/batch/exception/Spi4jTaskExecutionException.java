/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch.exception;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * 
 * @author MINARM
 */
public class Spi4jTaskExecutionException extends Spi4jRuntimeException {

	private static final long serialVersionUID = -7353943831005038073L;

	public Spi4jTaskExecutionException(String p_msgError, String p_msgSolution) {
		super(p_msgError, p_msgSolution);
	}

	public Spi4jTaskExecutionException(final Throwable p_rootCause, final String p_msgError,
			final String p_msgSolution) {
		super(p_rootCause, p_msgError, p_msgSolution);
	}
}
