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
 * Intercepteur pour EJB 3 qui met en cache par JVM les résultats des appels de findById et de findAll.<br/>
 * (utile par exemple, pour limiter les appels du client sur le serveur sur des données du référentiel)
 * @author MINARM
 */
public final class ServiceCacheInterceptor implements Serializable
{
   private static final long serialVersionUID = 1L;

   private static final Map<Class<?>, ServiceCacheProxy<?>> c_serviceProxyByInterfaceService = Collections
            .synchronizedMap(new HashMap<Class<?>, ServiceCacheProxy<?>>());

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
      final Class<?> v_interface = ServiceLogInterceptor.getInterfaceOfService(p_context);
      if (!ServiceReferentiel_Itf.class.isAssignableFrom(v_interface))
      {
         return p_context.proceed();
      }

      // on garde un cache de ces ServiceCacheProxy pour éviter de les réinstancier à chaque fois
      // sans copier dans cette classe tout le code correspondant
      final ServiceCacheProxy<?> v_serviceCacheProxy = getServiceCacheProxyFromCache(v_interface);

      final Method v_method = p_context.getMethod();
      final Object[] v_parameters = p_context.getParameters();
      Object v_result = v_serviceCacheProxy.lookInCache(v_method, v_parameters);
      if (v_result != null)
      {
         // trouvé dans le cache ! inutile d'appeler le service
         return v_result;
      }

      // on appelle le service
      v_result = p_context.proceed();

      // on met en cache
      v_serviceCacheProxy.putInCache(v_method, v_parameters, v_result);

      return v_result;
   }

   /**
    * Retourne une instance de ServiceCacheProxy pour l'interface de service en paramètre.
    * @param p_interfaceService
    *           Class
    * @return ServiceCacheProxy
    */
   @SuppressWarnings(
   {"rawtypes", "unchecked" })
   private static ServiceCacheProxy<?> getServiceCacheProxyFromCache (final Class<?> p_interfaceService)
   {
      ServiceCacheProxy<?> v_serviceCacheProxy = c_serviceProxyByInterfaceService.get(p_interfaceService);
      if (v_serviceCacheProxy == null)
      {
         // il n'y aura pas besoin du service juste pour lookInCache et putInCache donc on met null pour le service
         v_serviceCacheProxy = new ServiceCacheProxy(p_interfaceService, null);
         c_serviceProxyByInterfaceService.put(p_interfaceService, v_serviceCacheProxy);
      }
      return v_serviceCacheProxy;
   }
}
