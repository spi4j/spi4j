package fr.spi4j.ui.mvp;

import java.util.List;

/**
 * Gestionnaire de flow.
 * 
 * @author MINARM
 */
public abstract class SpiFlowManager_Abs {

	/**
	 * Le présenteur qui a initié ce flow.
	 */
	protected final Presenter_Abs<?, ?> _previousPresenter;

	/**
	 * Prépare un nouveau flow (pas encore démarré).
	 * 
	 * @param p_previousPresenter le présenteur qui a initié ce flow
	 */
	public SpiFlowManager_Abs(final Presenter_Abs<?, ?> p_previousPresenter) {
		_previousPresenter = p_previousPresenter;
	}

	/**
	 * Démarrer le flow.
	 */
	public void start() {
		getViewManager().registerFlow(this);
		onStart();
	}

	/**
	 * Cette méthode effectue un traitement au démarrage du flow : appel d'une
	 * action, affichage d'un écran.
	 */
	protected abstract void onStart();

	/**
	 * Arrêt normal du flow.
	 */
	public void stop() {
		getViewManager().unregisterFlow(this);
		onStop();
	}

	/**
	 * Cette méthode effectue un traitement à l'arrêt normal du flow : récupération
	 * du flow précédent pour trouver le prochain écran à afficher.
	 * <p>
	 * Par défaut : ferme tous les présenteurs du flow.
	 */
	protected void onStop() {
		closePresenters();
	}

	/**
	 * Arrêt en erreur du flow
	 */
	public void abort() {
		getViewManager().unregisterFlow(this);
		onAbort();
	}

	/**
	 * Cette méthode effectue un traitement à l'arrêt en erreur du flow :
	 * récupération du flow précédent pour trouver le prochain écran à afficher.
	 * <p>
	 * Par défaut : ferme tous les présenteurs du flow.
	 */
	protected void onAbort() {
		closePresenters();
	}

	/**
	 * Ferme les présenteurs associés à ce flow.
	 */
	protected void closePresenters() {
		List<Presenter_Abs<?, ?>> v_presenters = getViewManager().getPresentersForFlow(this);
		while (!v_presenters.isEmpty()) {
			final Presenter_Abs<?, ?> v_pres = v_presenters.get(0);
			v_pres.close();
			// on ne fait pas un close sur chaque présenteur récupéré initialement car
			// certains présenteurs peuvent en fermer d'autres
			// donc on récupère à chaque itération les présenteurs restant pour ce flow
			v_presenters = getViewManager().getPresentersForFlow(this);
		}
	}

	/**
	 * @return le ViewManager
	 */
	public ViewManager getViewManager() {
		return MVPUtils.getInstance().getViewManager();
	}

}
