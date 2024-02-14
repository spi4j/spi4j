/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

/**
 * Adapter pour le listener {@link PresenterListener_Itf}.
 * @author MINARM
 * @param <TypePresenter>
 *           le type de presenter
 */
public class PresenterAdapter<TypePresenter extends Presenter_Abs<?, ?>> implements
         PresenterListener_Itf<TypePresenter>
{
   @Override
   public void beforeCreate (final TypePresenter p_presenter)
   {
      // Méthode vide pour Adapter : aucune action par défaut
   }

   @Override
   public void afterCreate (final TypePresenter p_presenter)
   {
      // Méthode vide pour Adapter : aucune action par défaut
   }
}
