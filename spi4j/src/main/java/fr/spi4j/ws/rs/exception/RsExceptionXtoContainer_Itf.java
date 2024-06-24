/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

import fr.spi4j.ws.rs.RsXto_Itf;
import jakarta.ws.rs.core.Response.Status;

/**
 * Minimalist interface for the xto container. Each new exception xto container
 * must implement this interface. This also permits to secure the signatures for
 * all methods of the {@code RsResponseHelper} class.
 *
 * @author MINARM
 */
public interface RsExceptionXtoContainer_Itf extends RsXto_Itf {

	/**
	 * Get the status enumeration for the exception.
	 *
	 * @return the status for the exception.
	 */
	Status get_status();
}
