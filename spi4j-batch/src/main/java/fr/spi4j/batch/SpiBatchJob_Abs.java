/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.SpiJob_Abs;
import fr.spi4j.batch.exception.Spi4jTaskExecutionException;

/**
 * 
 * @author MINARM
 */
public abstract class SpiBatchJob_Abs<RESULT> extends SpiJob_Abs {

	/**
	 * Le logger pour la classe.
	 */
	protected static final Logger _log = LogManager.getLogger(SpiBatchJob_Abs.class);

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
	 * Etat de la tache.
	 */
	private TaskStateEnum _state;

	/**
	 * Avancement de 0 a 100.
	 */
	private int _progression;

	/**
	 * L'id attribue au job en cours.
	 */
	private final String _id;

	/**
	 * Indique si le traitement doit etre mis ou non dans une queue.
	 */
	private boolean _queued = false;

	/**
	 * Le resultat du traitement.
	 */
	private RESULT _result;

	/**
	 * La date à laquelle la tache a ete cree.
	 */
	@SuppressWarnings("unused")
	private long _creationDate;

	/**
	 * Chronometre de la tache.
	 */
	protected Chronometer _chrono;

	/**
	 * Liste des operations a effectuer.
	 */
	protected List<Item_Itf> _items;

	/**
	 * Constructeur.
	 * 
	 * @param p_name
	 */
	protected SpiBatchJob_Abs(final String p_name, final int p_expirationDelay, final TimeUnit p_expirationDelayUnit) {
		super(p_name);
		_chrono = new Chronometer();
		_expirationDelay = p_expirationDelay;
		_expirationDelayUnit = p_expirationDelayUnit;
		_state = TaskStateEnum.pending;
		_creationDate = System.currentTimeMillis();
		_id = UUID.randomUUID().toString();
		_items = new ArrayList<>();
	}

	/**
	 * 
	 * @return
	 */
	public boolean mustExpire() {

		boolean v_result = false;
		_log.info("Dernière Date d'arrêt pour {} : {}", _id, _chrono.getLastStopDate());
		// On ne fais le calcul d'expiration que si la tâche est commencée.
		if (_chrono.getLastStartDate() != null && _state != TaskStateEnum.done) {
			long v_now = System.currentTimeMillis();
			long v_date = _chrono.getLastStartDate().getTime();
			v_result = (v_now - v_date) > _expirationDelayUnit.toMillis(_expirationDelay);
		}
		return v_result;
	}

	/**
	 * 
	 * @return
	 */
	public final String queue() {
		if (_queued) {
			throw new Spi4jTaskExecutionException(
					"Désolé, le process d'id " + _id + " a déjà été utilisé dans un "
							+ TaskManager.class.getSimpleName(),
					"Merci d'utiliser une nouvelle instance pour relancer un " + this.getClass().getSimpleName());
		}
		_queued = true;
		return get_name();
	}

	/**
	 * Cette methode surcharge celle de la classe {@link SpiJob_Abs} et est lancee
	 * dans le cadre d'un processus separe (gere par la classe mere). Cette methode
	 * ne doit plus pouvoir etre modifiee par la suite.
	 */
	@Override
	protected final void run() throws Throwable {
		try {
			_chrono.start();
			_progression = 0;
			_state = TaskStateEnum.running;
			execute();
			_result = get_result();

		} catch (Exception p_e) {
			_state = TaskStateEnum.error;
			doOnException(p_e);

		} finally {
			if (_state != TaskStateEnum.error) {
				_progression = 100;
				_state = TaskStateEnum.done;
			}
			close();
		}
	}

	/**
	 * Opérations de fermeture.
	 */
	private void close() {
		for (Item_Itf v_item : _items) {
			try {
				v_item.close();
			} catch (IOException p_e) {
				throw new Spi4jTaskExecutionException(p_e, "Impossible de fermer correctement la ressource",
						"Vérifier le code de la fermeture pour la ressource.");
			}
		}
		_chrono.stop();
		_items.clear();
	}

	/**
	 * Demande forcée pour l'arrêt de la tâche. Cette demnde ne peut être effectuée
	 * que par le {@link SpiBatchJobsHandler}.
	 */
	void stop() {
		_progression = 100;
		_state = TaskStateEnum.stopped;
		close();
	}

	/**
	 * Enregistre l'operation (la sous-tache pour le traitement) aupes du
	 * gestionnaire de traitement. Cela va permettre de lancer automatiquement les
	 * methodes d'initialisation et de fermeture des ressources si le developpeur
	 * oublie les appels (permet aussi au developeur de se concentrre auniquement
	 * sur la tache a effectuer).
	 * 
	 * @param p_item l'operation a enregister aupres du gestionnaire pour le
	 *               traitement automatique
	 * @return l'instanciation de la classe.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T extends Item_Itf> T register(Class<? extends Item_Itf> p_item) {
		Object v_item;
		try {
			Constructor v_ctor = p_item.getDeclaredConstructor();
			v_ctor.setAccessible(true);
			v_item = v_ctor.newInstance();
			_items.add((Item_Itf) v_item);
			((Item_Itf) v_item).init();
			return (T) v_item;
		} catch (Exception p_e) {
			throw new Spi4jTaskExecutionException(p_e, "Impossible d'enregister l'item.", "");
		}

	}

	/**
	 * Methode d'execution pour le traitement automatique.
	 * 
	 * @throws Throwable n'importe quelle exception lancee dans le cadre de
	 *                   l'execution du traitement.
	 */
	protected abstract void execute() throws Throwable;

	/**
	 * 
	 * @return
	 */
	public RESULT get_result() {
		return _result;
	}

	/**
	 * Retourne l'etat (le statut) du traitement.
	 * 
	 * @return l'etat du traitement.
	 */
	public final TaskStateEnum get_state() {

		return _state;
	}

	/**
	 * Retourne la progression du traitement.
	 * 
	 * @return la progression du traitement (de 0 a 100).
	 */
	public final int get_progression() {
		return _progression;
	}

	/**
	 * Permet de récupérer le résultat du traitement (résultat qui est complétement
	 * décorélé du dernier objet ou primitif utilisé dans la dernière opération).
	 * 
	 * @param p_result le résultat du traitement.
	 */
	protected void setResult(final RESULT p_result) {
		_result = p_result;
	}

	/**
	 * 
	 */
	protected String get_name() {
		return super.get_name();
	}
}
