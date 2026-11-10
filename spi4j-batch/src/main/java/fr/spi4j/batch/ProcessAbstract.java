/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.batch.exception.Spi4jTaskExecutionException;

/**
 * Implementation par defaut d'un process.
 * 
 * La methode {@link #process()} doit etre implementee par les enfants.
 * 
 * @param <RESULT> Valeur de retour du process asynchrone.
 * 
 * @author MINARM
 */
public abstract class ProcessAbstract<RESULT> implements ProcessInterface<RESULT> {

	/** ILog for userstory. */
	protected static final Logger _log = LogManager.getLogger(ProcessAbstract.class);

	/**
	 * Etat de la tâche.
	 */
	protected TaskStateEnum _state;

	/**
	 * Avancement de 0 à 100.
	 */
	protected int _progression;

	/**
	 * Le résultat du traitement.
	 */
	protected RESULT _result;

	/**
	 * La date à laquelle la tâche a été créé.
	 */
	protected long _creationDate;

	/**
	 * Chronometre de la tâche.
	 */
	protected Chronometre _chrono = new Chronometre();

	/**
	 * L'id attribué au job en cours. Peut-être null.
	 */
	private final String _id;

	/**
	 * 
	 */
	private boolean _queued = false;

	/**
	 * @return L'id. null si non renseigné.
	 */
	@Override
	public final String getId() {
		return _id;
	}

	public ProcessAbstract() {

		_id = UUID.randomUUID().toString();
		_state = TaskStateEnum.pending;
		_creationDate = System.currentTimeMillis();
	}

	/**
	 * Gère démarre avec l'état {@link TaskStateEnum#RUNNING}, lance la méthode
	 * {@link #process()} et termine avec l'état {@link TaskStateEnum#DONE} si pas
	 * d'erreur.
	 * 
	 * <br>
	 * 
	 * Pattern : Template Method.
	 */
	@Override
	public final void run() {
		try {
			_chrono.start();
			_progression = 0;
			_state = TaskStateEnum.running;
			_result = process();
		} catch (final Throwable t) {
			_state = TaskStateEnum.error;
			_log.error("Erreur lors du traitement du processus asynchrone", t);
		} finally {
			if (_state != TaskStateEnum.error) {
				_progression = 100;
				_state = TaskStateEnum.done;
			}
			_chrono.stop();
		}
	}

	@Override
	public final RESULT getResult() {
		return _result;
	}

	@Override
	public final TaskStateEnum getState() {

		return _state;
	}

	@Override
	public final int getProgression() {
		return _progression;
	}

	@Override
	public final String queue() {
		if (_queued) {
			throw new Spi4jTaskExecutionException(
					"Désolé, le process d'id " + this.getId() + " a déjà été utilisé dans un "
							+ TaskManager.class.getSimpleName(),
					"Merci d'utiliser une nouvelle instance pour relancer un " + this.getClass().getSimpleName());
		}
		_queued = true;
		return getId();
	}

	/**
	 * Le traitement proprement dit à implementer par les classes filles. <br>
	 * Pattern : Template Method.
	 * 
	 * @return L'instance du type approprie pour le resultat du traitement.
	 */
	public abstract RESULT process();
}
