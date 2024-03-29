/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs.exception;

/**
 * Add a specific method to retrieve additional informations when the end
 * developer use a specific library for accessing to Api Rest. This interface is
 * useful for example, when the end developer has also a jsf or jsp cinematic
 * that call rest service. If the service return an error, it is important for
 * the jsp framework (for example) to have access at the full information
 * returned by the service (and not only <code>ex.getMessage()</code>).
 *
 * @author MINARM
 */
public interface RsFrontException {

	/**
	 * Retrieve additional informations for the front RS exception, this field may
	 * have been filled by a filter for example (in the case of Pacman, a filter
	 * automatically generated by Pacman).
	 *
	 * @return additional informations returned by the filter.
	 */
	public String get_additionalInfo();
}
