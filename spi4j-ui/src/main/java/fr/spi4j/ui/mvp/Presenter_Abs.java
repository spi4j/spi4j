/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

/**
 * L'ancêtre des présenteurs de vue. Chaque instance de présenteur est lié à une
 * et une seule instance de vue et à une et une seule instance Dto/Entity.
 * 
 * @param <TypeView> le type de vue présentée
 * @param <T>        le type de (DTO/Entity) affiché, ou Object si aucun
 *                   (DTO/Entity)
 * @author MINARM
 */
public abstract class Presenter_Abs<TypeView extends View_Itf, T> {

	/**
	 * Le présenteur parent.
	 */
	private Presenter_Abs<? extends View_Itf, ?> _parent;

	/**
	 * Le (DTO/Entity) de ce présenteur.
	 */
	private T _objet;

	/**
	 * L'id de ce présenteur.
	 */
	private Object _id;

	/**
	 * La vue de ce présenteur.
	 */
	private TypeView _view;

	/**
	 * Le FlowManager pour ce présenteur.
	 */
	private SpiFlowManager_Abs _flow;

	/**
	 * Constructeur du présenteur.
	 * 
	 * @param p_parent le présenteur parent
	 */
	public Presenter_Abs(final Presenter_Abs<? extends View_Itf, ?> p_parent) {
		this(p_parent, null, null);
	}

	/**
	 * Constructeur du présenteur.
	 * 
	 * @param p_parent le présenteur parent
	 * @param p_objet  le (dto/entity) de ce présenteur
	 */
	public Presenter_Abs(final Presenter_Abs<? extends View_Itf, ?> p_parent, final T p_objet) {
		this(p_parent, p_objet, null);
	}

	/**
	 * Constructeur du présenteur.
	 * 
	 * @param p_parent        le présenteur parent
	 * @param p_objet         le (dto/entity) de ce présenteur
	 * @param p_listener      le listener
	 * @param <TypePresenter> le type de presenter
	 */
	public <TypePresenter extends Presenter_Abs<TypeView, T>> Presenter_Abs(
			final Presenter_Abs<? extends View_Itf, ?> p_parent, final T p_objet,
			final PresenterListener_Itf<TypePresenter> p_listener) {
		super();
		_parent = p_parent;
		_objet = p_objet;

		if (p_listener != null) {
			@SuppressWarnings("unchecked")
			final TypePresenter v_presenter = (TypePresenter) this;
			p_listener.beforeCreate(v_presenter);
		}

		initPresenter();

		if (p_listener != null) {
			@SuppressWarnings("unchecked")
			final TypePresenter v_presenter = (TypePresenter) this;
			p_listener.afterCreate(v_presenter);
		}
	}

	/**
	 * Initialisation du présenteur. Méthode appelée entre beforeCreate et
	 * afterCreate du {@link PresenterListener_Itf}.
	 */
	protected void initPresenter() {
		// enregistrement du présenteur
		getViewManager().registerPresenter(this);
		// mise à jour du titre de la vue
		updateTitle();
	}

	/**
	 * @return la vue parente ou null si c'est la vue racine.
	 */
	public Presenter_Abs<? extends View_Itf, ?> getParentPresenter() {
		return _parent;
	}

	/**
	 * Désaffecte le présenteur parent de ce présenteur.
	 */
	// void removeParentPresenter ()
	// {
	// _parent = null;
	// }

	/**
	 * Génère un identifiant pour la vue.
	 * 
	 * @return l'identifiant du présenteur.
	 */
	public final Object getId() {
		if (_id == null) {
			_id = doGenerateId();
		}
		return _id;
	}

	/**
	 * Méthode d'initialisation de la vue (après instanciation). Cette méthode sert
	 * à remplir les champs de la vue, comme par exemple les valeurs possibles dans
	 * une liste. Elle sert également à instancier les vues enfant.
	 */
	abstract protected void initView();

	/**
	 * @return la vue associée à ce présenteur
	 */
	public final TypeView getView() {
		return _view;
	}

	/**
	 * Associe une vue à ce présenteur
	 * 
	 * @param p_view la vue associée à ce présenteur
	 */
	public final void setView(final TypeView p_view) {
		_view = p_view;
	}

	/**
	 * Méthode de fermeture de la vue.
	 */
	public void close() {
		getView().beforeClose();
		// désenregistrement du présenteur
		getViewManager().unregisterPresenter(this, isCascadeClose());
		stopListening();
	}

	/**
	 * Surcharger cette méthode pour éviter la fermeture en cascade des présenteurs.
	 * 
	 * @return true (par défaut) si lorsque ce présenteur se ferme, il doit demander
	 *         la fermeture de ses enfants automatiquement, false sinon
	 */
	protected boolean isCascadeClose() {
		return true;
	}

	/**
	 * @return le ViewManager
	 */
	protected ViewManager getViewManager() {
		return MVPUtils.getInstance().getViewManager();
	}

	/**
	 * @return le EventBus
	 */
	protected EventBus getEventBus() {
		return MVPUtils.getInstance().getEventBus();
	}

	/**
	 * Affecte le (DTO/Entity) de ce présenteur et recalcule son id et son titre.
	 * 
	 * @param p_objet le (DTO/Entity)
	 */
	public void setObjet(final T p_objet) {
		_objet = p_objet;
		// mise à jour de l'id
		updateId();
		// mise à jour du titre de la vue
		updateTitle();
	}

	/**
	 * @return le DTO/entity associé à ce présenteur (s'il a été affecté
	 *         précédement)
	 */
	public T getObjet() {
		return _objet;
	}

	/**
	 * Mise à jour de l'identifiant de ce présenteur.
	 */
	protected void updateId() {
		// récupération de l'ancien id
		final Object v_oldId = _id;
		// génération d'un nouvel id
		_id = doGenerateId();
		// mise à jour de l'id dans le registre
		getViewManager().updatePresenterId(getClass(), v_oldId, _id);
	}

	/**
	 * Mise à jour du titre de ce présenteur.
	 */
	protected void updateTitle() {
		// génération du titre
		final String v_title = doGenerateTitle();
		// mise à jour du titre dans la vue
		getView().setTitle(v_title);
	}

	/**
	 * Génération d'un id pour le présenteur. Implémentation par défaut : génère
	 * l'id par défaut. Méthode à surcharger pour les présenteurs pouvant être
	 * instanciés plusieurs fois.
	 * 
	 * @return l'identifiant généré
	 */
	protected Object doGenerateId() {
		return getViewManager().getDefaultId();
	}

	/**
	 * Génération du titre de la vue.
	 * 
	 * @return le titre de la vue
	 */
	protected abstract String doGenerateTitle();

	/**
	 * Envoi d'un événement.
	 * 
	 * @param p_event l'événement envoyé par le présenteur
	 */
	protected void fireEvent(final Event p_event) {
		getEventBus().fireEvent(p_event);
	}

	/**
	 * Ecoute un certain type d'événement. Tout événement envoyé de ce type sera
	 * intercepté dans la méthode onEvent.
	 * 
	 * @param p_eventType le type d'événement écouté.
	 */
	protected void listen(final EventType p_eventType) {
		getEventBus().listen(p_eventType, this);
	}

	/**
	 * Ecoute un certain type d'événement. Tout événement envoyé de ce type sera
	 * intercepté dans la méthode onEvent.
	 * 
	 * @param p_eventTypes les types d'événements écouté.
	 */
	protected void listen(final EventType... p_eventTypes) {
		for (final EventType v_eventType : p_eventTypes) {
			listen(v_eventType);
		}
	}

	/**
	 * Stoppe l'écoute pour un certain type d'événémenent.
	 * 
	 * @param p_eventType le type d'événément qui ne sera plus écouté par ce
	 *                    présenteur.
	 */
	protected void stopListening(final EventType p_eventType) {
		getEventBus().stopListening(p_eventType, this);
	}

	/**
	 * Stoppe l'écoute pour tous les types d'événémenent.
	 */
	protected void stopListening() {
		getEventBus().stopListening(this);
	}

	/**
	 * Permet de savoir si un présenteur écoute un certain type d'événement.
	 * 
	 * @param p_eventType le type d'événément qui ne sera plus écouté par ce
	 *                    présenteur.
	 * @return true si le présenteur écoute ce type d'événement, false sinon
	 */
	protected boolean isListening(final EventType p_eventType) {
		return getEventBus().isListening(p_eventType, this);
	}

	/**
	 * Réception d'un événement. Méthode à surcharger pour intercepter les
	 * événements après un listen.
	 * 
	 * @param p_event l'événement intercepté
	 */
	protected void onEvent(final Event p_event) {
		// nothing to do
	}

	/**
	 * Affecte le flow de ce présenteur (méthode appelée par le ViewManager)
	 * 
	 * @param p_flow le flow de ce présenteur
	 */
	void setCurrentFlowManager(final SpiFlowManager_Abs p_flow) {
		_flow = p_flow;
	}

	/**
	 * @return le flow de ce présenteur
	 */
	protected SpiFlowManager_Abs getCurrentFlowManager() {
		return _flow;
	}

	@Override
	public String toString() {
		return ViewManager.prefixForId(getClass()) + _id;
	}
}
