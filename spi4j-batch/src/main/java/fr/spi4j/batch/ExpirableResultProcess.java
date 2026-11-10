/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe de base de toutes les taches avec un resultat qui peut expirer.
 * 
 * @param <T> le type de resultat.
 * 
 * @author MINARM
 */
public abstract class ExpirableResultProcess<T> extends ProcessAbstract<T> {

	/** ILog for userstory. */
	protected static final Logger _log = LogManager.getLogger(ExpirableResultProcess.class);

	/**
	 * Le temps de disponibilite du resultat avant que la tache ne soit marquee
	 * expiree.
	 */
	private int _expirationDelay;

	/**
	 * L'unite de {@link #expirationDelay}.
	 */
	private TimeUnit _expirationDelayUnit;

	/**
	 * Creation d'une tache.
	 * 
	 * @param pExpirationDelay     Pendant combien de temps le resultat qui n'a pas
	 *                             encore ete lu pour une tache terminee avant
	 *                             qu'elle ne soit marquee 'Expiree'.
	 * @param pExpirationDelayUnit L'unite de temps du parametre
	 *                             'pResultExpirationDelay'.
	 * 
	 */
	protected ExpirableResultProcess(int p_expirationDelay, TimeUnit p_expirationDelayUnit) {

		_expirationDelay = p_expirationDelay;
		_expirationDelayUnit = p_expirationDelayUnit;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasExpired() {

		boolean v_result = false;
		_log.trace("Last Stop Date pour {} : {}", getId(), _chrono.getLastStopDate());

		// je ne fais le calcul d'expiration que si la tâche est terminée.
		if (_chrono.getLastStopDate() != null) {
			long v_now = System.currentTimeMillis();
			long v_date = _chrono.getLastStopDate().getTime();
			v_result = (v_now - v_date) > _expirationDelayUnit.toMillis(_expirationDelay);
		}
		return v_result;
	}
}
