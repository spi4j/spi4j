package fr.spi4j.utils;

/**
 * Used for the configuration cron reloader.
 *
 * @author MinArm
 */
public enum ConfigCronTimeUnit {

	/**
	 * Time unit representing sixty seconds.
	 */
	MINUTES(60 * ConfigCronTimeUnit.SECOND),

	/**
	 * Time unit representing sixty minutes.
	 */
	HOURS(60 * 60 * ConfigCronTimeUnit.SECOND),

	/**
	 * Time unit representing twenty four hours.
	 */
	DAYS(24 * 60 * 60 * ConfigCronTimeUnit.SECOND);

	/**
	 * Time unit in milliseconds.
	 */
	private static final long SECOND = 1000L;

	/**
	 * The delay for cron activation.
	 */
	private long _delay;

	/**
	 * Constructor for the enumeration.
	 *
	 * @param delay the delay for the activation of the configuration reloader.
	 */
	ConfigCronTimeUnit(final long delay) {
		_delay = delay;
	}

	/**
	 * Retrieve the delay for the activation of the configuration reloader.
	 *
	 * @return the delay for the cron activation.
	 */
	public long getDelay() {
		return _delay;
	}
}
