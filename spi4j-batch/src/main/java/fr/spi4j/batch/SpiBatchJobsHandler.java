package fr.spi4j.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe de lancement d'un ou plusieurs Job(s).
 *
 * @author MINARM
 */
public class SpiBatchJobsHandler implements Runnable {

	/**
	 * Le logger pour la classe.
	 */
	private final Logger _logger = LogManager.getLogger(getClass());

	/**
	 * La taille minimale du pool de taches.
	 */
	@SuppressWarnings("unused")
	private int _corePoolSize;

	/**
	 * La taille maximale du pool de taches, qui indique le nombre maximum de taches
	 * concurrentes possibles.
	 */
	@SuppressWarnings("unused")
	private int _maximumPoolSize;

	/**
	 * Le temps maximum qu'un thread qui excede la taille nominale du pool doit
	 * attendre avant d'etre supprime.
	 */
	@SuppressWarnings("unused")
	private long _keepAliveTime;

	/**
	 * L'unite de temps du parametre 'keepAliveTime'.
	 */
	@SuppressWarnings("unused")
	private TimeUnit _keepAliveTimeUnit;

	/**
	 * La taille de la file d'attente, qui indique le nombre maximum de taches en
	 * attente avant que les threads qui ajoute des taches ne soient bloques.
	 */
	@SuppressWarnings("unused")
	private int _queueCapacity;

	/**
	 * L'interval de temps entre chaque nettoyage des taches expirees (et non lue)
	 */
	@SuppressWarnings("unused")
	private int _cleanInterval;

	/**
	 * La liste pour les différents jobs (traitements automatiques);
	 */
	@SuppressWarnings("rawtypes")
	private List<SpiBatchJob_Abs> _queue;

	/**
	 * Constructeur.
	 */
	protected SpiBatchJobsHandler() {
		_cleanInterval = 10;
		_corePoolSize = 1;
		_keepAliveTime = 10;
		_maximumPoolSize = 10;
		_queueCapacity = 10;
		_keepAliveTimeUnit = TimeUnit.MINUTES;
		_logger.info("BatchJobsHandler démaré, en attente d'exécution des différentes tâches...");
	}

	/**
	 * Positionne la taille minimale du pool de taches.
	 * 
	 * @param p_corePoolSize la taille minimale du pool de taches.
	 */
	public void set_corePoolSize(int p_corePoolSize) {
		_corePoolSize = p_corePoolSize;
	}

	/**
	 * Positionne la taille maximale du pool de taches.
	 * 
	 * @param _maximumPoolSize la taille maximale du pool de taches, qui indique le
	 *                         nombre maximum de taches concurrentes possibles.
	 */
	public void set_maximumPoolSize(int p_maximumPoolSize) {
		_maximumPoolSize = p_maximumPoolSize;
	}

	/**
	 * Positionne
	 * 
	 * @param _keepAliveTime
	 */
	public void set_keepAliveTime(long p_keepAliveTime) {
		_keepAliveTime = p_keepAliveTime;
	}

	/**
	 * Positionne
	 * 
	 * @param _keepAliveTimeUnit
	 */
	public void set_keepAliveTimeUnit(TimeUnit p_keepAliveTimeUnit) {
		_keepAliveTimeUnit = p_keepAliveTimeUnit;
	}

	/**
	 * Positionne
	 * 
	 * @param _queueCapacity
	 */
	public void set_queueCapacity(int p_queueCapacity) {
		_queueCapacity = p_queueCapacity;
	}

	/**
	 * Positionne
	 * 
	 * @param _cleanInterval
	 */
	public void set_cleanInterval(int p_cleanInterval) {
		_cleanInterval = p_cleanInterval;
	}

	/**
	 * Positionne
	 * 
	 * @param _cleanIntervalUnit
	 */
	public void set_cleanIntervalUnit(TimeUnit p_cleanIntervalUnit) {
	}

	/**
	 *
	 */
	private boolean _run;

	/**
	 * Si un job dépasse son temps d'activité alloué, on stoppe la tâche (attention
	 * on ne tue pas le process car no a pas la main dessus), on supprime la tâche
	 * dans la 'queue' et on stoppe la gestionnaire des tâches (pour pouvoir cette
	 * fois tuer le processus...) et comme on a stoppé toutes les tâches, on relance
	 * le gestionnaire de tâches.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void run() {
		_run = true;
		// @SuppressWarnings("unused")
		// TaskManager manager = new TaskManager(_corePoolSize, _maximumPoolSize,
		// _keepAliveTime, _keepAliveTimeUnit,
		// _queueCapacity, _cleanInterval, _cleanIntervalUnit);
		while (_run) {
			for (SpiBatchJob_Abs v_job : _queue) {
				if (v_job.mustExpire()) {
					_logger.warn(
							"Attention, le traitement {} a du être stoppé suite au dépassement du temps initialement alloué!",
							v_job.get_name());
					v_job.stop();
					_queue.remove(v_job);
					waitRunningJobsAndRestart(new ArrayList<>(_queue));
				}
			}
		}
	}

	/**
	 * Enregistre la tâche auprès du gestionnaire de tâches.
	 * 
	 * @param p_job la tâche à enregistrer.
	 */
	@SuppressWarnings("rawtypes")
	public void register(final SpiBatchJob_Abs p_job) {
		if (_queue == null)
			_queue = new ArrayList<>();
		_queue.add(p_job);
	}

	/**
	 * Activation du gestionnaire de tâches.
	 */
	public void waitForInterrupt() {
		run();
	}

	/**
	 * Demande l'arrêt du gestionnaire de tâches.
	 * 
	 * @param p_force demande de forcer l'arrêt.
	 */
	public void stop(final boolean p_force) {
		if (p_force) {
			_run = false;
			return;
		}
		waitRunningJobsAndRestart(new ArrayList<>(_queue));
	}

	/**
	 * Demande de rédémarrage après suppression d'une tâche qui a dépassé le temps
	 * d'exécution qui lui était imparti.
	 * 
	 * @param p_queue la la liste des tâches encore à exécuter.
	 */
	@SuppressWarnings("rawtypes")
	void waitRunningJobsAndRestart(final List<SpiBatchJob_Abs> p_queue) {
		if (!p_queue.isEmpty()) {
			SpiBatchJob_Abs v_job = p_queue.get(0);
			if (v_job.get_state() == TaskStateEnum.done) {
				p_queue.remove(0);
			}
			waitRunningJobsAndRestart(p_queue);
		}
		_run = false;
		waitForInterrupt();
	}
}
