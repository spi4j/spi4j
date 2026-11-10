/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

/**
 * Just indicate to the spi4j framework that the reader which implements this
 * interface may not have the need of any resource to work.
 * <p>
 * As this option is uncommon, it is easier to go through a dedicated interface
 * rather than unnecessarily cluttering the main interface with a boolean.
 *
 * @author MinArm
 */
public interface ConfigOptionalResource {
	// RAS.
}
