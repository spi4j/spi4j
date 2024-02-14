/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Classe utilitaire pour le MVP.
 * @author MINARM
 */
public class MVPUtils
{

   private static ViewManager defaultViewManager = new ViewManager();

   // instance de MVPUtils dans le cas d'une gestion mono-thread
   private static MVPUtils instance = new MVPUtils();

   /** le view manager de l'instance courante */
   protected ViewManager _viewManager = instanciateViewManager();

   /** le bus d'événements de l'instance courante */
   protected final EventBus _eventBus = new EventBus();

   /**
    * Constructeur privé.
    */
   protected MVPUtils ()
   {
      super();
   }

   /**
    * Reinitialisation du MVPUtils.
    */
   public static void reinit ()
   {
      instance = new MVPUtils();
   }

   /**
    * Affcte le ViewManager.
    * @param p_viewManager
    *           le type de ViewManager
    */
   public static void setViewManager (final ViewManager p_viewManager)
   {
      defaultViewManager = p_viewManager;
      instance._viewManager = instanciateViewManager();
   }

   /**
    * Instancie un nouveau ViewManager.
    * @return le ViewManager
    */
   protected static ViewManager instanciateViewManager ()
   {
      try
      {
         return defaultViewManager.create();
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Impossible d'instancier le ViewManager",
                  "Vérifiez la méthode clone de la classe " + defaultViewManager.getClass().getName());
      }
   }

   /**
    * @return l'instance courante de MVP Utils.
    */
   public static MVPUtils getInstance ()
   {
      return instance;
   }

   /**
    * Affecte le MVP Utils (pour gestion multi thread par exemple).
    * @param p_instance
    *           la nouvelle instance
    */
   public static void setInstance (final MVPUtils p_instance)
   {
      instance = p_instance;
   }

   /**
    * @return le ViewManager
    */
   public ViewManager getViewManager ()
   {
      return _viewManager;
   }

   /**
    * @return le EventBus
    */
   public EventBus getEventBus ()
   {
      return _eventBus;
   }

}
