package fr.spi4j.utils;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import fr.spi4j.exception.Spi4jConfigException;

/**
 *
 * This class just allows the instantiation of the scheduler for the quartz
 * framework.
 *
 * @author MinArm
 */
abstract class ConfigQtzScheduler {

	/**
	 * The singleton instance for the quartz scheduler.
	 */
	private static Scheduler _scheduler;

	/**
	 * The exception if the instantiation of the quartz scheduler has failed.
	 */
	private static SchedulerException _schedulerException;

	/**
	 * Initialize the singleton for the quartz scheduler. If an exception is thrown,
	 * we simply store the initial exception which will be retrieved immediately
	 * after by the <code>getInstance()</code> method.
	 */
	static {
		try {
			_scheduler = new StdSchedulerFactory().getScheduler();
		} catch (final SchedulerException ex) {
			_schedulerException = ex;
		}
	}

	/**
	 * Get the unique instance of the quartz scheduler. This particular conception
	 * avoid having to throw a runtime exception under a static bloc. As the
	 * exception is thrown under the getInstance method, it can be correctly catch
	 * and handled by caller classes.
	 *
	 * @return the instance of a started quart scheduler.
	 * @throws SchedulerException the default exception handled by quartz.
	 */
	public static Scheduler getInstance() throws SchedulerException {

		if (null == _scheduler) {
			throw new Spi4jConfigException(_schedulerException,
					"Impossible de démarrer le scheduler pour quartz, le scheduler a la valeur ''null''.");
		}

		if (!_scheduler.isStarted()) {
			_scheduler.start();
		}
		return _scheduler;
	}
}
