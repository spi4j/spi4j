/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author MINARM.
 */
public class RsLogger {

	private static final Logger c_log = LogManager.getLogger(RsFilter_Abs.class);

	/**
	 * Constructor.
	 */
	private RsLogger() {
		super();
	}

	@SuppressWarnings("javadoc")
	public static Logger get_log() {
		return c_log;
	}
}
