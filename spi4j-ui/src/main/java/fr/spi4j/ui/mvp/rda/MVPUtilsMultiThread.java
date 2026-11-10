package fr.spi4j.ui.mvp.rda;

import fr.spi4j.ui.mvp.EventBus;
import fr.spi4j.ui.mvp.MVPUtils;
import fr.spi4j.ui.mvp.ViewManager;

/**
 * Classe utilitaire pour le MVP.
 * @author MINARM
 */
public class MVPUtilsMultiThread extends MVPUtils
{

   // thread local dans le cas d'une gestion de MVP multi-threadé
   private static final ThreadLocal<MVPUtilsMultiThread> c_threadLocal = new ThreadLocal<MVPUtilsMultiThread>()
   {
      @Override
      protected MVPUtilsMultiThread initialValue ()
      {
         return new MVPUtilsMultiThread();
      }
   };

   @Override
   public ViewManager getViewManager ()
   {
      return c_threadLocal.get().getSingleViewManager();
   }

   /**
    * @return l'instance du ViewManager
    */
   private ViewManager getSingleViewManager ()
   {
      return super.getViewManager();
   }

   @Override
   public EventBus getEventBus ()
   {
      return c_threadLocal.get().getSingleEventBus();
   }

   /**
    * @return l'instance du EventBus
    */
   private EventBus getSingleEventBus ()
   {
      return super.getEventBus();
   }
}
