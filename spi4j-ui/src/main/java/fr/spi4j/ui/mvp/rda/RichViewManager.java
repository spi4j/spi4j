/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp.rda;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.ViewManager;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * View Manager.
 * @author MINARM
 */
public class RichViewManager extends ViewManager
{

   private static final Logger c_log = LogManager.getLogger(RichViewManager.class);

   @SuppressWarnings("unchecked")
   @Override
   public <TypePresenter extends Presenter_Abs<? extends View_Itf, ?>> TypePresenter getPresenter (
            final Class<TypePresenter> p_presenterClass, final Object... p_params)
   {
      return (TypePresenter) _presenters
               .get(prefixForId(p_presenterClass) + getPresenterId(p_presenterClass, p_params));
   }

   /**
    * Retourne l'id d'un présenteur
    * @param <TypePresenter>
    *           le type du présenteur
    * @param p_presenterClass
    *           l'interface du présenteur
    * @param p_params
    *           les paramètres facultatifs pour la génération de l'id du présenteur
    * @return l'id du présenteur
    */
   public <TypePresenter extends Presenter_Abs<? extends View_Itf, ?>> Object getPresenterId (
            final Class<TypePresenter> p_presenterClass, final Object... p_params)
   {
      Object v_presenterId = null;
      boolean v_idGeneratorFound = false;
      boolean v_generateurAppele = false;
      // parcours des méthodes du présenteur
      for (final Method v_method : p_presenterClass.getDeclaredMethods())
      {
         // recherche d'une méthode annotée avec @PresenterId
         final Annotation v_annotation = v_method.getAnnotation(PresenterId.class);
         if (v_annotation != null)
         {
            v_idGeneratorFound = true;
            if (methodCanGenerateId(p_presenterClass, v_method, p_params))
            {
               // vérification qu'un générateur d'id n'a pas déjà été appelé
               if (v_generateurAppele)
               {
                  throw new Spi4jRuntimeException(
                           "Il existe plusieurs générateurs d'id avec les mêmes paramètres dans le présenteur "
                                    + p_presenterClass.getName(), "Vérifier les paramètres des méthodes annotées @"
                                    + PresenterId.class.getSimpleName() + " dans " + p_presenterClass.getName());
               }
               v_presenterId = getPresenterIdWithMethod(p_presenterClass, v_method, p_params);
               v_generateurAppele = true;
            }
         }
      }
      // aucun id généré
      if (v_presenterId == null)
      {
         if (v_idGeneratorFound)
         {
            // un générateur a été trouvé, mais ne correspondait pas aux paramètres demandés
            final String[] v_paramsTypes = new String[p_params.length];
            int v_i = 0;
            for (final Object v_o : p_params)
            {
               v_paramsTypes[v_i++] = v_o.getClass().getSimpleName();
            }
            throw new Spi4jRuntimeException("Aucun générateur d'id trouvé dans le présenteur "
                     + p_presenterClass.getName() + " pour les paramètres " + Arrays.toString(v_paramsTypes),
                     "Vérifier les générateurs d'id du présenteur " + p_presenterClass.getName());
         }
         else
         {
            // aucun générateur n'a été trouvé, génération d'un id par défaut
            v_presenterId = getDefaultId();
         }
      }
      return v_presenterId;
   }

   /**
    * @param <TypePresenter>
    *           le type du présenteur
    * @param p_presenterClass
    *           l'interface du présenteur
    * @param p_method
    *           la méthode à appeler pour générer l'id du présenteur
    * @param p_params
    *           les paramètres facultatifs pour la génération de l'id du présenteur
    * @return l'id du présenteur
    */
   private <TypePresenter> Object getPresenterIdWithMethod (final Class<TypePresenter> p_presenterClass,
            final Method p_method, final Object... p_params)
   {
      try
      {
         // génération de l'id avec la méthode trouvée
         return p_method.invoke(null, p_params);
      }
      catch (final IllegalAccessException v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Le générateur d'id pour le présenteur " + p_presenterClass.getName()
                  + " n'est pas accessible", "Vérifier les modifieurs (public / private) des méthodes annotées @"
                  + PresenterId.class.getSimpleName() + " dans " + p_presenterClass.getName()
                  + " : ces méthodes doivent être publiques");
      }
      catch (final IllegalArgumentException v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Mauvais paramètres pour le générateur d'id du présenteur "
                  + p_presenterClass.getName(), "Vérifier les paramètres pour le générateur d'id du présenteur "
                  + p_presenterClass.getName());
      }
      catch (final InvocationTargetException v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Erreur lors de la génération d'id du présenteur "
                  + p_presenterClass.getName(), "Vérifier le code du générateur d'id du présenteur "
                  + p_presenterClass.getName());
      }
   }

   /**
    * Vérifie qu'une méthode va bien pouvoir générer un Id a priori
    * @param <TypePresenter>
    *           le type de presenter
    * @param p_presenterClass
    *           la classe du presenter
    * @param p_method
    *           la méthode à tester
    * @param p_params
    *           les paramètres attendus
    * @return true si la méthode va pouvoir générer un Id avec ces paramètres, false sinon
    */
   private static <TypePresenter extends Presenter_Abs<? extends View_Itf, ?>> boolean methodCanGenerateId (
            final Class<TypePresenter> p_presenterClass, final Method p_method, final Object... p_params)
   {
      if (!Modifier.isStatic(p_method.getModifiers()))
      {
         throw new Spi4jRuntimeException("Le générateur d'id pour le présenteur " + p_presenterClass.getName()
                  + " n'est pas static", "Modifier la méthode " + p_method.getName() + " pour qu'elle soit static");
      }
      final Class<?>[] v_methodParameters = p_method.getParameterTypes();
      if (v_methodParameters.length == p_params.length)
      {
         boolean v_rightParameters = true;
         int v_i = 0;
         // vérification que le générateur trouvé a des paramètres qui correspondent
         for (final Class<?> v_paramClass : v_methodParameters)
         {
            if (p_params[v_i] != null && !v_paramClass.isAssignableFrom(p_params[v_i++].getClass()))
            {
               v_rightParameters = false;
            }
         }
         if (v_rightParameters)
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public <TypeView extends View_Itf> void registerPresenter (final Presenter_Abs<TypeView, ?> p_presenter)
   {
      super.registerPresenter(p_presenter);
      if (c_log.isInfoEnabled())
      {
         c_log.info("Ouverture du presenter : " + p_presenter + " (" + p_presenter.getView().getClass().getSimpleName()
                  + ')');
      }
      printCallStack();
   }

   @Override
   public void unregisterPresenter (final Presenter_Abs<? extends View_Itf, ?> p_presenter, final boolean p_cascade)
   {
      if (c_log.isInfoEnabled())
      {
         c_log.info("Fermeture du presenter : " + p_presenter);
      }
      super.unregisterPresenter(p_presenter, p_cascade);
      printCallStack();
   }

   @Override
   public void updatePresenterId (@SuppressWarnings("rawtypes") final Class<? extends Presenter_Abs> p_presenterClass,
            final Object p_oldId, final Object p_newId)
   {
      super.updatePresenterId(p_presenterClass, p_oldId, p_newId);
      if (!p_oldId.equals(p_newId) && c_log.isInfoEnabled())
      {
         c_log.info("Mise à jour de l'id du presenter : " + prefixForId(p_presenterClass) + p_oldId + " --> "
                  + prefixForId(p_presenterClass) + p_newId);
      }
   }

   /**
    * Affichage d'une trace d'appels.
    */
   private void printCallStack ()
   {
      if (c_log.isDebugEnabled())
      {
         c_log.debug("********************");
         c_log.debug("*    Call Stack    *");
         c_log.debug("********************");
         for (final Presenter_Abs<? extends View_Itf, ?> v_presenter : _activePresenterStack)
         {
            c_log.debug(prefixForId(v_presenter.getClass()) + v_presenter.getId());
         }
      }
   }

   /**
    * @return une nouvelle instance (vierge) de ce view manager
    */
   @Override
   public ViewManager create ()
   {
      return new RichViewManager();
   }

   @Override
   protected void warn (final String p_message)
   {
      c_log.warn(p_message);
   }

}
