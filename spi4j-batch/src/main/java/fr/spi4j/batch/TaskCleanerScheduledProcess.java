/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.batch.exception.Spi4jTaskExecutionException;

/**
 * Classe de nettoyage des {@link ExpirableResultProcess}.
 * 
 * @author MINARM
 */
class TaskCleanerScheduledProcess implements Runnable {

	private static final String EXPIRABLE_NAME = ExpirableResultProcess.class.getSimpleName();

	/** ILog for cache. */
	private static Logger _log = LogManager.getLogger(TaskCleanerScheduledProcess.class);

	/**
	 * 
	 */
	private final TaskManager _taskManager;

	/**
	 * 
	 * @param p_taskManager
	 */
	protected TaskCleanerScheduledProcess(TaskManager p_taskManager) {
		if (null == p_taskManager) {
			throw new Spi4jTaskExecutionException("La " + TaskManager.class.getSimpleName() + " est obligatoire", null);
		}
		_taskManager = p_taskManager;
	}

	@Override
	public final synchronized void run() {
		_log.info("Démarrage du nettoyeur planifié des " + EXPIRABLE_NAME + "... {{{");
		try {
			// 1. Vérifier si la map est vide.
			synchronized (_taskManager._mapInstances) {
				if (!_taskManager._mapInstances.isEmpty()) {

					// 2. Vérifier les conditions de suppression de chaque élément de la map.
					// supprimer les éléments périmés de la map, les enlever de la map, les enlever
					// du set files.
					ExpirableResultProcess<?> v_process;

					// pour éviter une concurrent Exception, je dois faire une copie des clés avant.
					List<String> v_keys = new ArrayList<>();
					for (String v_index : _taskManager._mapInstances.keySet()) {
						v_keys.add(v_index);
					}

					for (String v_index : v_keys) {
						Object v_o = _taskManager._mapInstances.get(v_index);
						if (v_o instanceof ExpirableResultProcess<?>) {
							v_process = (ExpirableResultProcess<?>) v_o;

							if (v_process.hasExpired()) {
								_log.info("Le processus asynchrone d'index " + v_index + " a expiré.");
								_taskManager._mapInstances.remove(v_index);
							}

						} else if (v_o == null) {
							_log.warn("Le processus asynchrone d'index " + v_index + " est null.");
							_taskManager._mapInstances.remove(v_index);
						} else {
							_log.trace("Le processus asynchrone d'index " + v_index + " n'est pas un " + EXPIRABLE_NAME
									+ ".");
						}
					}
				}
			}

		} catch (Throwable t) {
			// le catch de TOUTES les erreurs empêche le blocage éternel du scheduler.
			_log.error("Erreur lors du nettoyage planifié des " + EXPIRABLE_NAME, t);
		} finally {
			_log.info("Fin du nettoyage planifié des " + EXPIRABLE_NAME);
			_log.info("}}}");
		}
	}

}
