/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.ReflectUtil;
import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Proxy des interfaces de services pour faire des simulations d'appels distants entre une application cliente et un serveur <br/>
 * (via sérialisation/désérialisation du premier appel pour éviter le passage par référence).
 * @param <TypeService>
 *           Type du service
 * @author MINARM
 */
public final class ServiceSimulatedRemotingProxy<TypeService> implements InvocationHandler
{
   private static final ThreadLocal<Boolean> c_threadLocal = new ThreadLocal<>();

   private static final Logger c_logger = LogManager.getLogger("SIMULATED_REMOTING");

   private static final char[] c_padding = "        ".toCharArray();

   private final String _interfaceSimpleName;

   private final TypeService _delegate;

   /**
    * Exception permettant d'afficher la call stack dans les logs.
    * @author MINARM
    */
   private static class CallStack extends Exception
   {
      private static final long serialVersionUID = 1L;

      /**
       * Constructeur.
       */
      CallStack ()
      {
         super("Remoting called from :");
      }
   }

   /**
    * Constructeur privé.
    * @param p_interface
    *           La classe du composant dont le nom sera mis dans les logs
    * @param p_delegate
    *           Composant
    */
   private ServiceSimulatedRemotingProxy (final Class<TypeService> p_interface, final TypeService p_delegate)
   {
      super();
      _interfaceSimpleName = p_interface.getSimpleName();
      _delegate = p_delegate;
   }

   /**
    * Factory de création du proxy de simulation du remoting pour le service (via sérialisation/désérialisation du premier appel pour éviter le passage par référence).
    * @param <TypeService>
    *           Le type du service
    * @param p_interface
    *           La classe du composant dont le nom sera mis dans les logs
    * @param p_delegate
    *           Le composant
    * @return Le proxy du service avec la simulation remoting
    */
   @SuppressWarnings("unchecked")
   public static <TypeService> TypeService createProxy (final Class<TypeService> p_interface,
            final TypeService p_delegate)
   {
      final ServiceSimulatedRemotingProxy<TypeService> v_serviceSimulatedRemotingProxy = new ServiceSimulatedRemotingProxy<>(
               p_interface, p_delegate);
      return (TypeService) Proxy.newProxyInstance(p_delegate.getClass().getClassLoader(), p_delegate.getClass()
               .getInterfaces(), v_serviceSimulatedRemotingProxy);
   }

   @Override
   public Object invoke (final Object p_proxy, final Method p_method, final Object[] p_args) throws Throwable
   {
      if (!isInService())
      {
         final long v_start = System.currentTimeMillis();
         // si on n'est pas déjà entré dans un service pour ce thread, alors c'est le premier appel de service (non imbriqué),
         // donc on sérialise en mémoire et on désérialise les paramètres de la méthode pour éviter le passage par référence
         try
         {
            // on flag le thread pour les services imbriqués
            setInService();

            final Object[] v_args = deepClone(p_args);
            return ReflectUtil.invokeMethod(p_method, _delegate, v_args);
         }
         finally
         {
            // on déflag le thread pour les services suivants
            unsetInService();

            logMessage(p_method, v_start);
         }
      }
      else
      {
         // on est déjà entré dans un service, donc il s'agit d'un service imbriqué et on ne fait rien de spécial
         // (pour un service imbriqué, il ne faut pas empêcher d'utiliser le passage par référence)
         return ReflectUtil.invokeMethod(p_method, _delegate, p_args);
      }
   }

   /**
    * Log l'appel du remoting (avec le nom du composant) et le temps d'exécution.
    * @param p_method
    *           Method
    * @param p_start
    *           long
    */
   private void logMessage (final Method p_method, final long p_start)
   {
      if (c_logger.isInfoEnabled())
      {
         final String v_duration = String.valueOf(System.currentTimeMillis() - p_start);
         final StringBuilder v_msg = new StringBuilder();
         if (v_duration.length() < 7)
         {
            v_msg.append(c_padding, 0, 7 - v_duration.length());
         }
         v_msg.append(v_duration).append(" ms; ");

         // on ajoute le nom du composant (nom simple de l'interface) et le nom de la méthode
         v_msg.append('/').append(_interfaceSimpleName).append('/').append(p_method.getName());

         c_logger.info(v_msg.toString());

         // on log la stack-trace de l'appel au remoting au niveau trace (et non info),
         // pour pouvoir activer de temps en temps en développement des traces disant depuis quels endroits du code ont été fait les appels au remoting
         if (c_logger.isTraceEnabled())
         {
            final CallStack v_callStack = new CallStack();
            c_logger.trace(v_callStack.getMessage(), v_callStack);
         }
      }
   }

   /**
    * Est-ce que l'on est déjà entré dans un service pour ce thread ?
    * @return boolean
    */
   private static boolean isInService ()
   {
      final Boolean v_isInService = c_threadLocal.get();
      return v_isInService != null && v_isInService;
   }

   /**
    * Flag ce thread comme étant maintenant dans un service.
    */
   private static void setInService ()
   {
      c_threadLocal.set(Boolean.TRUE);
   }

   /**
    * Flag ce thread comme n'étant plus dans un service.
    */
   private static void unsetInService ()
   {
      c_threadLocal.remove();
   }

   /**
    * Sérialisation et désérialisation en mémoire des paramètre
    * @param p_args
    *           Paramètres
    * @return Paramètres sérialisés et désérialisés
    */
   private static Object[] deepClone (final Object[] p_args)
   {
      final ByteArrayOutputStream v_byteArray = new ByteArrayOutputStream();
      try
      {
         final ObjectOutputStream v_output = new ObjectOutputStream(v_byteArray);
         try
         {
            v_output.writeObject(p_args);
         }
         finally
         {
            v_output.close();
         }
         final ObjectInputStream v_input = new ObjectInputStream(new ByteArrayInputStream(v_byteArray.toByteArray()));
         try
         {
            return (Object[]) v_input.readObject();
         }
         finally
         {
            v_input.close();
         }
      }
      catch (final ClassNotFoundException v_ex)
      {
         // ne peut pas arriver mais au cas où
         throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
      }
      catch (final IOException v_ex)
      {
         // ne peut pas arriver mais au cas où
         throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
      }
   }
}
