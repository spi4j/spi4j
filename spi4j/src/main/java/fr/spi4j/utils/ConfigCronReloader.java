/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * This reloader works with all resource types.
 *
 * @author MinArm
 */
public class ConfigCronReloader implements ConfigResourcesReloader {

	/**
	 * List of observers for the refresh strategy.
	 */
	private final List<ComponentObserver> _observers = new ArrayList<>();

	private final long _delay;

	/**
	 * Constructor for the class.
	 *
	 * @param p_nj       the interval for the time unit.
	 * @param p_timeUnit the unit of time(hour or day) for configuration update.
	 */
	public ConfigCronReloader(final int p_nj, final ConfigCronTimeUnit p_timeUnit) {
		_delay = p_nj * p_timeUnit.getDelay();
	}

	@Override
	public void start(final ConfigResourcesContent p_resourcesContent,
			final ConfigResourcesHandler p_resourceReaderHandler) throws Exception {
		final String v_uuid = UUID.randomUUID().toString();
		final JobDetail v_job = JobBuilder.newJob(ConfigResourceQtzJob.class)
				.withIdentity(getJobID(v_uuid), getGroupId(v_uuid)).build();

		v_job.getJobDataMap().put(ConfigHandler.c_refresh_main_resources_content_key, p_resourcesContent);
		v_job.getJobDataMap().put(ConfigHandler.c_refresh_reader_handler_key, p_resourceReaderHandler);
		v_job.getJobDataMap().put(ConfigHandler.c_refresh_observers_key, _observers);

		final Trigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerId(v_uuid), getGroupId(v_uuid))
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(_delay).repeatForever())
				.build();
		ConfigQtzScheduler.getInstance().scheduleJob(v_job, trigger);
	}

	/**
	 * Just used for more readability with 80c.
	 *
	 * @param p_uuid the unique identifier for the quartz reloader.
	 * @return the full unique job name (id) for this particular job.
	 */
	private String getJobID(final String p_uuid) {
		return p_uuid + ConfigHandler.c_refresh_qtz_job_suffix;
	}

	/**
	 * Just used for more readability with 80c.
	 *
	 * @param p_uuid the unique identifier for the quartz reloader.
	 * @return the full unique group name (id) for this particular group.
	 */
	private String getGroupId(final String p_uuid) {
		return p_uuid + ConfigHandler.c_refresh_qtz_group_suffix;
	}

	/**
	 * Just used for more readability with 80c.
	 *
	 * @param p_uuid the unique identifier for the quartz reloader.
	 * @return the full unique trigger name (id) for this particular trigger.
	 */
	private String getTriggerId(final String p_uuid) {
		return p_uuid + ConfigHandler.c_refresh_qtz_trigger_suffix;
	}

	/**
	 * @param p_observer the component observer (class) to register.
	 */
	@Override
	public void registerObserver(final ComponentObserver p_observer) {
		_observers.add(p_observer);
	}
}
