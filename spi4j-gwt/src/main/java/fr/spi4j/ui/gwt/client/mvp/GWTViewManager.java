/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client.mvp;

import com.google.gwt.core.shared.GWT;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.SpiFlowManager_Abs;
import fr.spi4j.ui.mvp.ViewManager;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * View Manager pour GWT.
 * @author MINARM
 */
public class GWTViewManager extends ViewManager
{

   @Override
   public <TypeView extends View_Itf> void registerPresenter (final Presenter_Abs<TypeView, ?> p_presenter)
   {

      super.registerPresenter(p_presenter);

      GWT.log("Ouverture du presenter : " + p_presenter + " (" + p_presenter.getView().getClass().getName() + ')');
   }

   @Override
   public void unregisterPresenter (final Presenter_Abs<? extends View_Itf, ?> p_presenter, final boolean p_cascade)
   {
      GWT.log("Fermeture du presenter : " + p_presenter);
      super.unregisterPresenter(p_presenter, p_cascade);
   }

   @Override
   public void registerFlow (final SpiFlowManager_Abs p_flow)
   {
      super.registerFlow(p_flow);

      GWT.log("Enregistrement d'un flow : " + p_flow.getClass().getName());
   }

   @Override
   public void unregisterFlow (final SpiFlowManager_Abs p_flow)
   {
      GWT.log("Desenregistrement d'un flow : " + p_flow.getClass().getName());

      super.unregisterFlow(p_flow);
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
   @Override
   public void updatePresenterId (@SuppressWarnings("rawtypes") final Class<? extends Presenter_Abs> p_presenterClass,
            final Object p_oldId, final Object p_newId)
   {
      super.updatePresenterId(p_presenterClass, p_oldId, p_newId);
      if (!p_oldId.equals(p_newId))
      {
         GWT.log("Mise à jour de l'id du presenter : " + prefixForId(p_presenterClass) + p_oldId + " --> "
                  + prefixForId(p_presenterClass) + p_newId);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public <TypePresenter extends Presenter_Abs<? extends View_Itf, ?>> TypePresenter getPresenter (
            final Class<TypePresenter> p_presenterClass, final Object... p_params)
   {
      if (p_params != null && p_params.length > 0)
      {
         throw new Spi4jRuntimeException("Impossible de chercher un présenteur selon son type et des paramètres",
                  "Ne mettez aucun paramètre lors de la recherche du présenteur");
      }
      for (final Presenter_Abs<? extends View_Itf, ?> v_presenter : _presenters.values())
      {
         if (v_presenter.getClass().equals(p_presenterClass))
         {
            return (TypePresenter) v_presenter;
         }
      }
      // Le présenteur n'a pas été trouvé
      return null;
   }

   /**
    * @return une nouvelle instance (vierge) de ce view manager
    */
   @Override
   public ViewManager create ()
   {
      return new GWTViewManager();
   }

   @Override
   protected void warn (final String p_message)
   {
      GWT.log(p_message);
   }

}
