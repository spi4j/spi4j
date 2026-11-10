/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * Store the different methods for passing authentication parameters.
 *
 * @author MINARM.
 */
public enum RsPassingMode_Enum {

	/**
	 * Passed in the body of the request.
	 */
	body("NOT IMPLEMENTED"), // Not implemented yet.

	/**
	 * Passed in the header of the request.
	 */
	header("HEADER"),

	/**
	 * Passed as a query parameter of the request.
	 */
	query("QUERY"),

	/**
	 * Passed as a cookie.
	 */
	cookie("COOKIE"),

	/**
	 * Passed as a path parameter of the request.
	 */
	path("NOT IMPLEMENTED"); // Not implemented yet.

	/**
	 * The enumeration under OBEO format.
	 */
	final private String _obeoPassingMode;

	/**
	 * Constructor.
	 *
	 * @param p_obeoPassingMode : The Obeo enumeration for passing mode.
	 */
	RsPassingMode_Enum(final String p_obeoPassingMode) {
		_obeoPassingMode = p_obeoPassingMode;
	}

	/**
	 * Make the conversion between obeo and spi4j enumeration and retrieve the
	 * enumeration.
	 *
	 * @param p_obeoPassingMode : The obeo enumeration for the passing mode.
	 * @return The internal spi4j enumeration for the passing mode.
	 */
	public static RsPassingMode_Enum get_passingModeFromObeo(final String p_obeoPassingMode) {
		for (final RsPassingMode_Enum v_enum : RsPassingMode_Enum.values()) {
			if (v_enum._obeoPassingMode.equals(p_obeoPassingMode)) {
				return v_enum;
			}
		}
		// Should throw an exception.
		return null;
	}
}
