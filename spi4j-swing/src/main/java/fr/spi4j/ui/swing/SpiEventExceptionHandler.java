/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.ui.mvp.MVPUtils;

/**
 * Handler for uncaught exceptions on any event dispatch thread.<br/>
 * Once this has been installed, the class must be accessible by any subsequently launched dispatch thread.
 * <p>
 * This handler is installed by setting the System property sun.awt.exception.handler.<br/>
 * See javadoc for java.awt.EventDispatchThread#handleException for details.<br/>
 * This is sort of a patch to Sun's implementation, which only checks the property once and caches the result ever after.<br/>
 * This implementation will always chain to the handler indicated by the current value of the property.<br/>
 * @see "java.awt.EventDispatchThread#handleException"
 * @author Abbot
 */
public class SpiEventExceptionHandler
{
   /**
    * @see "java.awt.EventDispatchThread"
    */
   public static final String PROP_NAME = "sun.awt.exception.handler";

   /**
    * Logger.
    */
   private static final Logger c_log = LogManager.getRootLogger();

   // protected static java.util.Properties oldProperties = null;

   private static boolean installed;

   /**
    * Internal exception.
    * @author MINARM
    */
   private static class DummyException extends RuntimeException
   {
      private static final long serialVersionUID = 1L;

      /**
       * Constructeur.
       */
      DummyException ()
      {
         super();
      }
   }

   /**
    * Implémentation par défaut de la gestion de l'exception : à surcharger dans les applications
    * @param p_throwable
    *           Throwable
    */
   protected void exceptionCaught (final Throwable p_throwable)
   {
      c_log.warn(p_throwable.toString(), p_throwable);
      c_log.warn("Présenteurs ouverts : " + MVPUtils.getInstance().getViewManager().getActivePresenters());
   }

   /**
    * Install a handler for event dispatch exceptions.<br/>
    * This is kind of a hack, but it's Sun's hack.<br/>
    * See the javadoc for java.awt.EventDispatchThread for details.<br/>
    * NOTE: we throw an exception immediately, which ensures that our handler is installed, since otherwise someone might set this property later.<br/>
    * java.awt.EventDispatchThread doesn't actually load the handler specified by the property until an exception is caught by the event dispatch thread.<br/>
    * SwingSet2 in 1.4.1 installs its own.<br/>
    * Note that a new instance is created for each exception thrown.
    * @param p_handlerClass
    *           Class
    * @see "java.awt.EventDispatchThread"
    */
   public static void install (final Class<?> p_handlerClass)
   {
      final boolean isJava17OrMore = "1.7".compareTo(System.getProperty("java.version")) < 0;
      if (isJava17OrMore)
      {
         // the hack with "sun.awt.exception.handler" throws an exception in Java 1.7, so do nothing here
         // Thread.setDefaultUncaughtExceptionHandler will be used instead
         return;
      }
      if (SwingUtilities.isEventDispatchThread())
      {
         final Thread v_thread = new Thread()
         {
            /** {@inheritDoc} */
            @Override
            public void run ()
            {
               install(p_handlerClass); // Handler must not be installed from the event dispatch thread
            }
         };
         v_thread.start();

         return;
      }
      if (!SpiEventExceptionHandler.class.isAssignableFrom(p_handlerClass))
      {
         throw new IllegalArgumentException("Handler must be derived from " + SpiEventExceptionHandler.class.getName());
      }

      final String oldHandler = System.getProperty(PROP_NAME);
      if (installed)
      {
         // If we've already installed an instance of
         // AbstractEventExceptionHandler, all we need to do is set the
         // property name.
         // Log.debug("Exception handler class already installed");
         System.setProperty(PROP_NAME, p_handlerClass.getName());
      }
      else
      {
         // Log.debug("Installing handler " + handlerClass.getName());

         // Even if it's been set to something else, we can override it if
         // there hasn't been an event exception thrown yet.
         EventQueue.invokeLater(new Runnable()
         {
            /** {@inheritDoc} */
            @Override
            public void run ()
            {
               // oldProperties = (java.util.Properties) System.getProperties().clone();
               // Set the property just before throwing the exception;
               // OSX sets the property as part of the VM startup, so
               // we have to override it here.
               System.setProperty(PROP_NAME, p_handlerClass.getName());
               throw new DummyException();
            }
         });
         // Does nothing but wait for the previous invocation to finish
         try
         {
            EventQueue.invokeAndWait(new Runnable()
            {
               /** {@inheritDoc} */
               @Override
               public void run ()
               {
                  // rien ici
               }
            });
         }
         catch (final Exception e)
         {
            c_log.warn(e.toString(), e);
         }
         // System.setProperties(oldProperties);
         // String oldHandler = System.getProperty(PROP_NAME);

         if (!installed)
         {
            String msg = "The handler for event dispatch thread exceptions could not be installed";
            if (oldHandler != null)
            {
               msg += " (" + oldHandler + " has already been set and cached; there is no way to override it)";
            }
            throw new RuntimeException(msg);
         }
         // else {
         // if (oldHandler != null) {
         // Log.debug("Replaced an existing event exception handler (" + oldHandler + ')');
         // }
         // }
      }
   }

   /**
    * Handle exceptions thrown on the event dispatch thread.
    * @param thrown
    *           Throwable
    */
   public void handle (final Throwable thrown)
   {
      // Log.debug("Handling event dispatch exception: " + thrown);
      final String handler = System.getProperty(PROP_NAME);
      if (!handler.equals(getClass().getName()))
      {
         // Log.debug("A user exception handler (" + handler + ") has been set, invoking it");
         try
         {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            final Class<?> myClass = Class.forName(handler, true, cl);
            myClass.getMethod("handle", Throwable.class).invoke(myClass.getDeclaredConstructor().newInstance(), thrown);
         }
         catch (final Throwable e)
         {
            c_log.warn(e.toString(), e);
         }
      }
      // The exception may be created by a different class loader
      // so compare by name only
      if (thrown.getClass().getName().equals(DummyException.class.getName()))
      {
         // Install succeeded
         // Log.debug("Installation succeeded");
         setInstalled(true);
      }
      else
      {
         // Log.debug("Handling exception on event dispatch thread: " + thrown);
         // Log.debug(thrown);
         exceptionCaught(thrown);
      }
      // Log.debug("Handling done");
   }

   /**
    * Définit installed
    * @param newInstalled
    *           boolean
    */
   private static void setInstalled (final boolean newInstalled)
   {
      installed = newInstalled;
   }
}
