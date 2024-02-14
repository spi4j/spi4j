/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.io.File;
import java.util.Optional;

/**
 * @author MinArm
 */
public abstract class ConfigFileUtils {

	/**
	 * Build the complete access chain for the configuration resource.
	 *
	 * @param p_resource     the resource (complete )
	 * @param p_resourceBase the optional base information for the resource.
	 */
	static String getResource(final String p_resource, final Optional<String> p_resourceBase) {
		if (!p_resourceBase.isPresent() || p_resourceBase.isEmpty()) {
			return p_resource.toString();
		}
		if (p_resourceBase.toString().lastIndexOf(File.separator) == p_resourceBase.toString().length()) {
			return p_resourceBase.get() + p_resource.toString();
		}
		return p_resourceBase.get() + File.separator + p_resource.toString();
	}
}
