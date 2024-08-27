package fr.spi4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe de lancement d'un ou plusieurs Job(s).
 *
 * @author MINARM
 */
public class SpiJobsHandler_Abs implements Runnable {

	/**
	 *
	 */
	private final Logger _logger = LogManager.getLogger(getClass());

	/**
	 *
	 */
	private boolean _run;

	@Override
	public void run() {
		_logger.info("");
		_run = true;
		while (_run) {
			// RAS.
		}
	}

	/**
	 *
	 */
	public void stop() {
		_run = false;
	}
}
