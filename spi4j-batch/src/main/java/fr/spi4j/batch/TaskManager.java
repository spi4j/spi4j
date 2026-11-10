/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.batch.exception.Spi4jTaskExecutionException;

/**
 * Cette classe gere l'ensemble des taches asynchrones (batchs).
 * 
 * @author MINARM
 */
public class TaskManager {

	/**
	 * LogCache.
	 */
	private static final Logger _log = LogManager.getLogger(TaskManager.class);

	/** Le pool de thread. */
	private final ThreadPoolExecutor _executor;

	/** Le pool de thread pour le scheduled cleaner (1 thread). */
	private final ScheduledThreadPoolExecutor _scheduledExecutor;

	protected Map<String, ProcessInterface<?>> _mapInstances;

	/** L'instance du scheduled cleaner qui sera executee periodiquement. */
	private final TaskCleanerScheduledProcess _scheduledCleaner;

	/**
	 * Cree une instance de {@link TaskManager}.
	 * 
	 * @param p_corePoolSize      La taille minimale du pool de taches.
	 * @param p_maximumPoolSize   La taille maximale du pool de tache, qui indique
	 *                            le nombre maximum de taches concurrentes possibles
	 * @param p_keepAliveTime     Le temps maximum qu'un thread qui excede la taille
	 *                            nominale du pool doit attendre avant d'etre
	 *                            supprime.
	 * @param p_keepAliveTimeUnit L'unite de temps du parametre 'keepAliveTime'.
	 * @param p_queueCapacity     La taille de la file d'attente, qui indique le
	 *                            nombre maximum de tache en attente avant que les
	 *                            threads qui ajoute des taches ne soient bloques.
	 * @param p_cleanInterval     L'interval de temps entre chaque nettoyage des
	 *                            taches expirees (et non lue)
	 * @param p_cleanIntervalUnit L'unite de temps du parametre 'cleanInterval'.
	 */
	public TaskManager(final int p_corePoolSize, final int p_maximumPoolSize, final long p_keepAliveTime,
			final TimeUnit p_keepAliveTimeUnit, final int p_queueCapacity, final int p_cleanInterval,
			final TimeUnit p_cleanIntervalUnit) {

		_executor = new ThreadPoolExecutor(p_corePoolSize, p_maximumPoolSize, p_keepAliveTime, p_keepAliveTimeUnit,
				new LinkedBlockingQueue<Runnable>(p_queueCapacity));
		_scheduledExecutor = new ScheduledThreadPoolExecutor(1);

		_mapInstances = Collections.synchronizedMap(new HashMap<String, ProcessInterface<?>>());
		_log.info("Initialisation du scheduled cleaner avec un interval de " + p_cleanInterval + " "
				+ p_cleanIntervalUnit.toString());

		_scheduledCleaner = new TaskCleanerScheduledProcess(this);
		_scheduledExecutor.scheduleAtFixedRate(_scheduledCleaner, 0, p_cleanInterval, p_cleanIntervalUnit);
	}

	/**
	 * Cette methode arrete les executors.
	 */
	public void shutdown() {

		_log.info("Arrêt du " + this.getClass().getSimpleName());

		if (null != _executor) {
			synchronized (_mapInstances) {
				for (final ProcessInterface<?> v_instance : _mapInstances.values()) {
					_executor.remove(v_instance);
				}
			}
			_executor.shutdownNow();
		}

		if (null != _scheduledExecutor) {
			_scheduledExecutor.remove(_scheduledCleaner);
			_scheduledExecutor.shutdownNow();
		}
	}

	/**
	 * @param uuid UUid du process.
	 * @param <T>  Le type de parametre du {@link ProcessInterface}.
	 * @return Une instance de {@link ProcessInterface}, ou null sinon.
	 */
	@SuppressWarnings("unchecked")
	<T> ProcessInterface<T> taskGet(final String uuid) {

		_log.trace("task | " + uuid);
		ProcessInterface<T> vInstance = null;
		synchronized (_mapInstances) {
			vInstance = (ProcessInterface<T>) _mapInstances.get(uuid);
		}
		return vInstance;
	}

	/**
	 * Demarre un {@link ProcessInterface}.
	 * 
	 * Attention, l'instance ne doit etre demarre qu'une seule fois.
	 * 
	 * @param p_instance Le processus a demarrer.
	 * @return uuid du process demarre.
	 * @throws RejectedExecutionException quand les capacités pool + blockingqueue
	 *                                    sont dépassées.
	 */
	public String taskStart(final ProcessInterface<?> p_instance) {

		if (null == p_instance) {
			throw new Spi4jTaskExecutionException("Le processus à exécuter ne doit pas être null.", null);
		}
		final String v_uuid = p_instance.queue();
		_log.info("Enregistrement du processus n° " + v_uuid);

		synchronized (_mapInstances) {
			if (_mapInstances.containsKey(v_uuid)) {
				throw new Spi4jTaskExecutionException("Le process " + v_uuid + " a déjà été enregistré."
						+ "Merci d'utiliser une nouvelle instance pour relancer un processus", null);
			}
			_mapInstances.put(v_uuid, p_instance);
		}
		_executor.execute(p_instance);
		return v_uuid;
	}
}
