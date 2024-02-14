/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * View Manager.
 * @author MINARM
 */
public class ViewManager
{

   private static final SpiFlowManager_Abs c_defaultFlow = new SpiFlowManager_Abs(null)
   {
      @Override
      protected void onStart ()
      {
         // RAS
      }
   };

   /**
    * Les presenteurs enregistrés.
    */
   protected final Map<Object, Presenter_Abs<? extends View_Itf, ?>> _presenters = new HashMap<Object, Presenter_Abs<? extends View_Itf, ?>>();

   /**
    * Les flows enregistrés.
    */
   // Utilisation de LinkedList au lieu de Dequeue pour compatibilité avec GWT
   protected final LinkedList<SpiFlowManager_Abs> _flows = new LinkedList<SpiFlowManager_Abs>(); 

   /**
    * Indicateur pour savoir si l'application gère les flows.
    */
   protected boolean _useFlows; // = false

   /**
    * Vue active lors des tests JBehave
    */
   protected final List<Presenter_Abs<? extends View_Itf, ?>> _activePresenterStack = new ArrayList<Presenter_Abs<? extends View_Itf, ?>>();

   /**
    * Les associations presenteur <-> vue
    */
   protected ViewsAssociation _viewsAssociation;

   /**
    * Constructeur.
    */
   public ViewManager ()
   {
      _flows.add(c_defaultFlow);
   }

   /**
    * Génère un préfixe pour les ids des présenteurs.
    * @param p_presenterClass
    *           la classe du présenteur
    * @return le préfixe de l'id du présenteur
    */
   protected static String prefixForId (final Class<?> p_presenterClass)
   {
      // le préfixe commun à tous les id
      return p_presenterClass.getName() + '/';
   }

   /**
    * Enregistre un présenteur dans le manager, qui pourra être récupérée par la suite.
    * @param <TypeView>
    *           le type de vue
    * @param p_presenter
    *           le présenteur à enregistrer
    */
   public <TypeView extends View_Itf> void registerPresenter (final Presenter_Abs<TypeView, ?> p_presenter)
   {

      final Class<?> v_presenterClass = p_presenter.getClass();
      if (_presenters.containsKey(prefixForId(p_presenter.getClass()) + p_presenter.getId()))
      {
         throw new ViewAlreadyRegisteredException(
                  "Un présenteur avec cet identifiant existe déjà dans le ViewManager : "
                           + prefixForId(p_presenter.getClass()) + p_presenter.getId(),
                  "Vérifier que ce présenteur a bien été désenregistré, via un appel à sa méthode close");
      }
      if (_viewsAssociation == null)
      {
         throw new Spi4jRuntimeException(
                  "Aucune vue associée au présenteur " + v_presenterClass.getName(),
                  "Vérifier qu'un appel à MVPUtils.getInstance().getViewManager().setViewsAssociation(new MyViewsAssociation()) a été fait et que cette classe d'association gère le présenteur "
                           + v_presenterClass.getName());
      }

      // affecte un flow à ce présenteur
      final SpiFlowManager_Abs v_lastFlow = _flows.getLast();
      if (c_defaultFlow.equals(v_lastFlow))
      {
         if (_useFlows)
         {
            throw new Spi4jRuntimeException("Aucun flow n'est enregistré, impossible d'en associer un au présenteur "
                     + prefixForId(p_presenter.getClass()) + p_presenter.getId(), "Vérifiez qu'un flow a été démarré");
         }
      }
      else
      {
         p_presenter.setCurrentFlowManager(v_lastFlow);
      }

      // initialisation de la vue associée à ce présenteur
      @SuppressWarnings("unchecked")
      final TypeView v_view = (TypeView) _viewsAssociation
               .getViewForPresenter((Presenter_Abs<? extends View_Itf, ?>) p_presenter);
      p_presenter.setView(v_view);
      p_presenter.initView();

      // enregistre le présenteur
      _presenters.put(prefixForId(p_presenter.getClass()) + p_presenter.getId(), p_presenter);

      // affecte la vue du presenter comme vue active
      // ne devrait pas arriver car le presenter ne devrait pas déjà exister
      if (_activePresenterStack.contains(p_presenter))
      {
         _activePresenterStack.remove(p_presenter);
      }
      // ajoute le présenteur en début de liste
      _activePresenterStack.add(0, p_presenter);
   }

   /**
    * Supprime une vue du manager, qui ne pourra plus être récupérée.
    * @param p_presenter
    *           la vue à supprimer du manager
    * @param p_cascade
    *           true pour supprimer les présenteurs fils en cascade, false sinon
    */
   public void unregisterPresenter (final Presenter_Abs<? extends View_Itf, ?> p_presenter, final boolean p_cascade)
   {
      // suppression des présenteurs fils si demandé
      if (p_cascade)
      {
         final List<Presenter_Abs<?, ?>> v_presenteursFils = new ArrayList<Presenter_Abs<?, ?>>();
         for (final Presenter_Abs<?, ?> v_presenteurActif : _presenters.values())
         {
            if (v_presenteurActif.getParentPresenter() == p_presenter)
            {
               v_presenteursFils.add(v_presenteurActif);
            }
         }
         for (final Presenter_Abs<?, ?> v_presenteurFils : v_presenteursFils)
         {
            v_presenteurFils.close();
         }
      }

      // action de suppression concrète dans la vue
      if (p_presenter.getParentPresenter() != null)
      {
         p_presenter.getParentPresenter().getView().removeView(p_presenter.getView());
      }

      removePresenter(p_presenter);
   }

   /**
    * Supprime le présenteur de la liste des présenteurs connus (après unregister ou en cas d'erreur d'initialisation).
    * @param p_presenter
    *           le présenteur à supprimer
    */
   protected void removePresenter (final Presenter_Abs<? extends View_Itf, ?> p_presenter)
   {
      // suppression de la liste des présenteurs connus
      _presenters.remove(prefixForId(p_presenter.getClass()) + p_presenter.getId());
      _activePresenterStack.remove(p_presenter);
   }

   /**
    * Enregistre un flow.
    * @param p_flow
    *           le nouveau flow
    */
   public void registerFlow (final SpiFlowManager_Abs p_flow)
   {
      _useFlows = true;
      _flows.addLast(p_flow);
   }

   /**
    * Termine un flow.
    * @param p_flow
    *           le flow terminé
    */
   public void unregisterFlow (final SpiFlowManager_Abs p_flow)
   {
      if (!_flows.contains(p_flow))
      {
         throw new Spi4jRuntimeException("Le flow " + p_flow.getClass().getName() + " n'est pas enregistré",
                  "Vérifiez que le flow n'a pas déjà été désenregistré");
      }
      _flows.remove(p_flow);
   }

   /**
    * Retourne la liste des présenteurs associés à un flow.
    * @param p_flow
    *           le flow
    * @return la liste des présenteurs dans ce flow
    */
   public List<Presenter_Abs<?, ?>> getPresentersForFlow (final SpiFlowManager_Abs p_flow)
   {
      // vérification de la nullité du paramètre
      if (p_flow == null)
      {
         throw new IllegalArgumentException("Le flow ne doit pas être null");
      }
      // recherche parmi tous les présenteurs actifs
      final List<Presenter_Abs<?, ?>> v_presenters = new ArrayList<Presenter_Abs<?, ?>>(_presenters.size());
      for (final Presenter_Abs<?, ?> v_presenter : _presenters.values())
      {
         if (p_flow.equals(v_presenter.getCurrentFlowManager()))
         {
            v_presenters.add(v_presenter);
         }
      }
      return v_presenters;

   }

   /**
    * Associe des vues aux présenteurs.
    * @param p_viewsAssociation
    *           l'association vue <-> présenteur
    */
   public void setViewsAssociation (final ViewsAssociation p_viewsAssociation)
   {
      if (_viewsAssociation != null)
      {
         warn("Attention, setViewsAssociation a déjà été appelé par l'application, vérifiez que l'appel à setViewsAssociation n'est fait qu'une seule fois lors de l'exécution de l'application");
      }
      _viewsAssociation = p_viewsAssociation;
      _presenters.clear();
   }

   /**
    * Méthode utilisée lors des tests JBehave. Retourne la liste des vues actives.
    * @return la liste des vues actives
    */
   public Collection<Presenter_Abs<? extends View_Itf, ?>> getActivePresenters ()
   {
      // les présenteurs actifs sont en fait tous les présenteurs
      // les actions GRAAL devront les tester dans l'ordre de la pile, donc les derniers instanciés sont les plus actifs

      return Collections.unmodifiableCollection(_activePresenterStack);
   }

   /**
    * Génère un identifiant par défaut pour un présenteur.
    * @return l'identifiant (par défaut) d'un présenteur
    */
   public String getDefaultId ()
   {
      return "";
   }

   /**
    * Met à jour l'id d'un présenteur.
    * @param p_presenterClass
    *           la classe de ce présenteur
    * @param p_oldId
    *           son ancien id
    * @param p_newId
    *           son nouvel id
    */
   public void updatePresenterId (@SuppressWarnings("rawtypes") final Class<? extends Presenter_Abs> p_presenterClass,
            final Object p_oldId, final Object p_newId)
   {
      final Object v_id = prefixForId(p_presenterClass) + p_oldId;
      final Presenter_Abs<? extends View_Itf, ?> v_presenter = _presenters.get(v_id);
      if (v_presenter == null)
      {
         throw new Spi4jRuntimeException("Impossible de mettre à jour l'identifiant du présenteur "
                  + p_presenterClass.getName() + " : " + p_oldId + " --> " + p_newId + ", présenteur non enregistré",
                  "???");
      }
      _presenters.remove(v_id);
      _presenters.put(prefixForId(p_presenterClass) + p_newId, v_presenter);
   }

   /**
    * Retourne l'instance de présenteur.
    * @param <TypePresenter>
    *           le type de présenteur cherché
    * @param p_presenterClass
    *           le type de présenteur cherché
    * @param p_params
    *           les paramètres utiles à la génération de l'id
    * @return le présenteur trouvée ou null, si aucun présenteur n'a été trouvé
    */
   public <TypePresenter extends Presenter_Abs<? extends View_Itf, ?>> TypePresenter getPresenter (
            final Class<TypePresenter> p_presenterClass, final Object... p_params)
   {
      throw new Spi4jRuntimeException(
               "Impossible d'appeler cette méthode sur ce type de ViewManager",
               "Utilisez un ViewManager spécialisé (tel que RichViewManager pour un client RDA) en appelant la méthode MVPUtils.setViewManager");
   }

   /**
    * Ouvre une vue, annotée par l'annotation UserView.
    * @param p_userView
    *           l'annotation portée par la vue
    * @return le présenteur associé à la vue
    */
   public Presenter_Abs<? extends View_Itf, ?> openView (final String p_userView)
   {
      // demande une instance du presenter présentant la vue p_userView
      final Presenter_Abs<? extends View_Itf, ?> v_presenter = _viewsAssociation
               .getPresenterForAnnotatedView(p_userView);
      if (v_presenter == null)
      {
         throw new Spi4jRuntimeException("Le présenteur n'a pas été trouvé pour la UserView " + p_userView,
                  "Rajouter l'annotation @UserView(\"" + p_userView + "\") sur une vue");
      }
      return v_presenter;
   }

   /**
    * Affiche un warning.
    * @param p_message
    *           le message à afficher
    */
   protected void warn (final String p_message)
   {
      // pas d'appel à Log4J ici car cette classe est utilisée par GWT et ne peut donc pas contenir de dépendance vers Log4J.
   }

   /**
    * @return une nouvelle instance (vierge) de ce view manager
    */
   public ViewManager create ()
   {
      return new ViewManager();
   }

}
