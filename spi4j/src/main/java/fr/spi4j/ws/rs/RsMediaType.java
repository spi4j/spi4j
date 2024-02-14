/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import jakarta.ws.rs.core.MediaType;

/**
 * Defaults mediaTypes for a rest service in Spi4J.
 *
 * @author MINARM
 */
public class RsMediaType extends MediaType {

	/** "text/plain" */
	public final static String c_text_plain_utf8 = "text/plain;" + RsConstants.c_charset_utf8;

	/** "application/json" */
	public final static String c_application_json_utf8 = "application/json;" + RsConstants.c_charset_utf8;
}
