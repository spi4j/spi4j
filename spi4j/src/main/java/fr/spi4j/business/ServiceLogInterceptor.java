/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

/**
 * Intercepteur pour EJB 3 qui log tous les appels de services (par exemple, du client sur le serveur).
 * @author MINARM
 */
public final class ServiceLogInterceptor implements Serializable
{
   private static final long serialVersionUID = 1L;

   private static final Map<Class<?>, ServiceLogProxy<?>> c_serviceProxyByInterfaceService = Collections
            .synchronizedMap(new HashMap<Class<?>, ServiceLogProxy<?>>());

   /**
    * Intercepte une exécution de méthode sur un ejb.
    * @param p_context
    *           InvocationContext
    * @return Object
    * @throws Exception
    *            e
    */
   @AroundInvoke
   public Object intercept (final InvocationContext p_context) throws Exception
   {
      // on n'utilise pas les nano-secondes car ce serait beaucoup plus coûteux
      final long v_start = System.currentTimeMillis();
      final int v_requestsCounterStart = ServiceLogProxy.getRequestsCounterValueForCurrentThread();
      Throwable v_throwable = null;
      try
      {
         return p_context.proceed();
      }
      catch (final Throwable v_t)
      {
         v_throwable = v_t;
         if (v_t instanceof Exception)
         {
            throw (Exception) v_t;
         }
         else if (v_t instanceof Error)
         {
            throw (Error) v_t;
         }
         else
         {
            // ne peut pas arriver mais au cas où
            throw new RuntimeException(v_t);
         }
      }
      finally
      {
         final Method v_method = p_context.getMethod();
         final Object[] v_parameters = p_context.getParameters();
         final Class<?> v_interface = getInterfaceOfService(p_context);

         // on garde un cache de ces ServiceLogProxy pour éviter de les réinstancier à chaque fois et pour éviter de rechercher le logger à chaque fois
         // sans copier dans cette classe tout le code correspondant
         final ServiceLogProxy<?> v_serviceLogProxy = getServiceLogProxyFromCache(v_interface);
         v_serviceLogProxy.logMessage(v_method, v_parameters, v_start, v_requestsCounterStart, v_throwable);
      }
   }

   /**
    * Retourne l'interface du service à partir du contexte d'interception.
    * @param p_context
    *           InvocationContext
    * @return Class
    */
   static Class<?> getInterfaceOfService (final InvocationContext p_context)
   {
      final Class<?>[] v_interfaces = p_context.getTarget().getClass().getInterfaces();
      final int v_length = v_interfaces.length;
      Class<?> v_interface;
      if (v_length == 0)
      {
         v_interface = p_context.getMethod().getDeclaringClass();
      }
      else
      {
         int v_i = 0;
         do
         {
            v_interface = v_interfaces[v_i];
            v_i++;
         }
         while (v_i < v_length && v_interface.getName().startsWith("fr.spi4j"));
      }
      return v_interface;
   }

   /**
    * Retourne une instance de ServiceLogProxy pour l'interface de service en paramètre.
    * @param p_interfaceService
    *           Class
    * @return ServiceLogProxy
    */
   @SuppressWarnings(
   {"rawtypes", "unchecked" })
   private static ServiceLogProxy<?> getServiceLogProxyFromCache (final Class<?> p_interfaceService)
   {
      ServiceLogProxy<?> v_serviceLogProxy = c_serviceProxyByInterfaceService.get(p_interfaceService);
      if (v_serviceLogProxy == null)
      {
         // il n'y aura pas besoin du service juste pour faire un log donc on met null pour le service
         v_serviceLogProxy = new ServiceLogProxy(p_interfaceService, null, p_interfaceService.getSimpleName());
         c_serviceProxyByInterfaceService.put(p_interfaceService, v_serviceLogProxy);
      }
      return v_serviceLogProxy;
   }
}
