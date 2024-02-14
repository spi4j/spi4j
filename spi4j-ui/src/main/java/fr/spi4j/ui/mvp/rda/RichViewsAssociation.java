/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp.rda;

import java.util.Map.Entry;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ui.graal.UserView;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;
import fr.spi4j.ui.mvp.ViewsAssociation;

/**
 * Classe d'association entre présenteur et vue.
 * @author MINARM
 */
public abstract class RichViewsAssociation extends ViewsAssociation
{

   /**
    * Constructeur.
    */
   public RichViewsAssociation ()
   {
      super();
   }

   /**
    * Retourne la vue associée à un présenteur.
    * @param <TypeView>
    *           le type de vue retourné
    * @param p_presenter
    *           le présenteur
    * @return la vue associée au présenteur
    */
   @Override
   abstract public <TypeView extends View_Itf> TypeView getViewForPresenter (
            final Presenter_Abs<TypeView, ?> p_presenter);

   @Override
   public Presenter_Abs<? extends View_Itf, ?> getPresenterForAnnotatedView (final String p_userView)
   {
      for (final Entry<Class<? extends Presenter_Abs<? extends View_Itf, ?>>, Class<? extends View_Itf>> v_entry : getAssociations()
               .entrySet())
      {
         if (classOrAncestorIsAnnotatedWithUserView(v_entry.getValue(), p_userView))
         {
            try
            {
               return v_entry.getKey().getDeclaredConstructor().newInstance();
            }
            catch (final Exception v_e)
            {
               throw new Spi4jRuntimeException(v_e, "Impossible d'instancier le présenteur pour la UserView "
                        + p_userView, "Vérifier le constructeur du présenteur " + v_entry.getKey().getName());
            }
         }
      }
      throw new Spi4jRuntimeException("Le présenteur n'a pas été trouvé pour la UserView " + p_userView,
               "Piste No1 : Vérifier les associations dans " + getClass().getName() + "\n"
                        + "Piste No2 : Vérifier qu'une interface de vue possède l'annotation @UserView(\"" + p_userView
                        + "\")");
   }

   /**
    * Vérifie qu'une classe (une vue) ou un de ses parents possède une implémentation d'annotation de {@link UserView}.
    * @param p_class
    *           la classe de la vue ou un de ses parents
    * @param p_userView
    *           l'implémentation de l'annotation {@link UserView}
    * @return true si la classe (une vue) ou un de ses parents possède une implémentation d'annotation de {@link UserView}
    */
   private boolean classOrAncestorIsAnnotatedWithUserView (final Class<?> p_class, final String p_userView)
   {
      final UserView v_classUserView = p_class.getAnnotation(UserView.class);
      if (v_classUserView != null && v_classUserView.value().equals(p_userView))
      {
         return true;
      }
      else
      {
         for (final Class<?> v_interface : p_class.getInterfaces())
         {
            if (classOrAncestorIsAnnotatedWithUserView(v_interface, p_userView))
            {
               return true;
            }
         }
      }
      return false;
   }

}
