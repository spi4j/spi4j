/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * This class is used to activate (or deactivate) all REST type services.
 * Possible cases of deactivation are maintenance, the discovery of unauthorized
 * access, or a major bug.
 *
 * @author MINARM
 */
final class RsAccess {

	/**
	 * Constructor.
	 */
	private RsAccess() {
		super();
	}

	/**
	 * Static holder for getting the instance.
	 */
	private static final class RestApplicationGlobalAccessHolder {
		private static boolean isAccessGranted = Boolean.TRUE;

		private static final RsAccess c_instance = new RsAccess();
	}

	/**
	 * Return the instance of the through the holder class.
	 *
	 * @return The instance.
	 */
	public static RsAccess getInstance() {
		return RestApplicationGlobalAccessHolder.c_instance;
	}

	/**
	 * Check if REST services are granted or not.
	 *
	 * @return Boolean for service access (granted or not).
	 */
	public static boolean isAccessGranted() {
		return RestApplicationGlobalAccessHolder.isAccessGranted;
	}

	/**
	 * Set if REST services are granted or not.
	 *
	 * @param p_isAccessGranted : boolean for service access.
	 */
	public static void setAccessGranted(final boolean p_isAccessGranted) {
		RestApplicationGlobalAccessHolder.isAccessGranted = p_isAccessGranted;
	}
}
