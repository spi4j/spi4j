/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

/**
 * Listener de création d'un presenter.
 * @author MINARM
 * @param <TypePresenter>
 *           le type de presenter
 */
public interface PresenterListener_Itf<TypePresenter extends Presenter_Abs<?, ?>>
{
   /**
    * Méthode exécutée avant la création du presenter.
    * @param p_presenter
    *           l'instance du nouveau presenter
    */
   void beforeCreate (final TypePresenter p_presenter);

   /**
    * Méthode exécutée après la création du presenter.
    * @param p_presenter
    *           l'instance du nouveau presenter
    */
   void afterCreate (final TypePresenter p_presenter);
}
