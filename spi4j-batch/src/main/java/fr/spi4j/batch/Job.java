/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.batch.exception.Spi4jTaskExecutionException;

/**
 * Un Job représente le traitement d'un batch, avec des steps independants. <br>
 * C'est le développeur qui doit gérer la reprise sur incident.
 * 
 * @author MINARM
 */
public class Job extends ExpirableResultProcess<String> {

	private static Logger _log = LogManager.getLogger(Job.class);
	// private static ILog logThread = LogManager.getLogger(LogCategory.THREAD);

	/**
	 * 
	 */
	private final List<Step_Itf> _steps;

	/**
	 * 
	 * @param p_expirationDelay
	 * @param p_expirationDelayUnit
	 */
	public Job(final int p_expirationDelay, final TimeUnit p_expirationDelayUnit) {

		super(p_expirationDelay, p_expirationDelayUnit);
		_steps = new LinkedList<>();
	}

	/**
	 * 
	 * @param p_step
	 */
	public void add(final Step_Itf p_step) {

		if (null == p_step) {
			throw new Spi4jTaskExecutionException("Impossible d'ajouter un step null pour le processus.", null);
		}
		_log.info("Ajout step {} d'id {} à {} d'id {}", p_step.getClass().getSimpleName(), p_step.getId(),
				getClass().getSimpleName(), getId());
		_steps.add(p_step);
	}

	/**
	 * {@inheritDoc} <br>
	 * Ce processus doit etre surcharge pour creer un environnement de connexion a
	 * une base pour tout le batch si besoin.
	 */
	@Override
	public String process() {
		for (final Step_Itf v_step : _steps) {
			_log.info("Début step '" + v_step.getId() + "'");
			v_step.run();
			_log.info("Fin step '" + v_step.getId() + "'");
		}
		return "done";
	}
}
