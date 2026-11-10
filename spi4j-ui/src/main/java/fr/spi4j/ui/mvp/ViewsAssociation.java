/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

import java.util.HashMap;
import java.util.Map;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Classe d'association entre présenteur et vue.
 * @author MINARM
 */
public abstract class ViewsAssociation
{
   private final Map<Class<? extends Presenter_Abs<? extends View_Itf, ?>>, Class<? extends View_Itf>> _associations;

   /**
    * Constructeur.
    */
   public ViewsAssociation ()
   {
      super();
      _associations = new HashMap<Class<? extends Presenter_Abs<? extends View_Itf, ?>>, Class<? extends View_Itf>>();
   }

   /**
    * Ajoute une association présenteur --> vue.
    * @param <TypeView>
    *           le type de vue
    * @param p_presenterClass
    *           la classe de présenteur
    * @param p_viewClass
    *           la classe de vue
    */
   protected <TypeView extends View_Itf> void addAssociation (
            final Class<? extends Presenter_Abs<TypeView, ?>> p_presenterClass,
            final Class<? extends TypeView> p_viewClass)
   {
      _associations.put(p_presenterClass, p_viewClass);
   }

   /**
    * Retourne la classe de vue associée à la classe de présenteur.
    * @param p_presenterClass
    *           la classe de présenteur.
    * @return la classe de vue associée à la classe de présenteur.
    */
   protected Class<? extends View_Itf> getAssociation (
            final Class<? extends Presenter_Abs<? extends View_Itf, ?>> p_presenterClass)
   {
      final Class<? extends View_Itf> v_view = _associations.get(p_presenterClass);
      if (v_view == null)
      {
         throw new Spi4jRuntimeException("Le présenteur " + p_presenterClass.getName()
                  + " n'a aucune vue associée dans " + getClass().getName(), "Rajouter une association entre "
                  + p_presenterClass.getName() + " et sa vue correspondante dans " + getClass().getName());
      }
      return v_view;
   }

   /**
    * @return les associations
    */
   protected Map<Class<? extends Presenter_Abs<? extends View_Itf, ?>>, Class<? extends View_Itf>> getAssociations ()
   {
      return _associations;
   }

   /**
    * Retourne la vue associée à un présenteur.
    * @param <TypeView>
    *           le type de vue retourné
    * @param p_presenter
    *           le présenteur
    * @return la vue associée au présenteur
    */
   abstract public <TypeView extends View_Itf> TypeView getViewForPresenter (
            final Presenter_Abs<TypeView, ?> p_presenter);

   /**
    * Retourne la vue qui porte cette annotation.
    * @param p_userView
    *           l'annotation sur la vue
    * @return l'instance du présenteur de la vue trouvée avec cette annotation, ou null si aucune vue n'a été trouvée
    */
   public abstract Presenter_Abs<? extends View_Itf, ?> getPresenterForAnnotatedView (String p_userView);

}
