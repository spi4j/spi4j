/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import fr.spi4j.exception.Spi4jEntityNotFoundException;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.requirement.RequirementException;
import fr.spi4j.ws.rs.RsResponseHelper;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * This handler (from JAX-RS) catch an exception and try to convert in an
 * RSException. The RsException as all the elements to create a proper Response
 * Object the the REST service.
 *
 * @author MINARM
 */
public abstract class RsExceptionHandler_Abs implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(final Exception p_exception) {
		try {
			// Let the developer to customize the exception if needed for the application.
			handleException(p_exception);

			// Convert the exception to an exception specialized for REST service.
			final RsException_Abs v_exception = convertToCustomSpi4jRestException(p_exception);

			// Create the completed response, ready to be sent.
			return RsResponseHelper.responseForException(v_exception.get_xtoContainer());
		} catch (final Exception p_ex) {
			// Return a very basic error.
			return RsResponseHelper.response(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Convert an exception to an RsException. The initial exception becomes the
	 * cause of the RsException.
	 *
	 * @param p_exception : The initial exception to be converted.
	 * @return a new RsException with initial eception in cause.
	 */
	private RsException_Abs convertToCustomSpi4jRestException(final Exception p_exception) {

		final Exception v_exception = convertToSpi4jRestException(p_exception);

		if (v_exception instanceof RsException_Abs) {
			return (RsException_Abs) v_exception;
		}

		if (p_exception instanceof Spi4jEntityNotFoundException) {
			return new RsNoResultException(p_exception);
		}

		if (p_exception instanceof NotFoundException) {
			return new RsServiceNotFoundException(p_exception);
		}

		if (p_exception instanceof Spi4jValidationException) {
			return new RsParameterException(p_exception);
		}

		if (p_exception instanceof RequirementException) {
			return new RsRequirementException(p_exception);
		}

		if (p_exception instanceof Spi4jRuntimeException) {
			return new RsSpi4jUnexpectedException(p_exception);
		}

		return new RsUnexpectedException(p_exception);
	}

	/**
	 * Let the developer do some preliminary operations before handling the
	 * exception.
	 *
	 * @param p_exception : The exception caught from the system.
	 */
	protected abstract void handleException(Exception p_exception);

	/**
	 * Let the developer to create new RsException and convert an initial exception
	 * to this RsException
	 *
	 * @param p_exception : The initial exception to convert.
	 * @return The exception (if no needed conversion) or an new RsException.
	 */
	protected abstract Exception convertToSpi4jRestException(final Exception p_exception);
}
