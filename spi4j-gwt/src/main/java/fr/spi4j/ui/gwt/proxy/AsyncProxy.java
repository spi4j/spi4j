/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.spi4j.ReflectUtil;
import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Proxy des interfaces de services qui simule un appel asynchrone.
 * @param <TypeService>
 *           Type asynchrone du service
 * @author MINARM
 */
public final class AsyncProxy<TypeService> implements InvocationHandler
{
   private final Object _serviceSynchrone;

   /**
    * Constructeur privé.
    * @param p_service
    *           Le service
    */
   private AsyncProxy (final Object p_service)
   {
      super();
      _serviceSynchrone = p_service;
   }

   /**
    * Factory de création des logs pour le service.
    * @param <TypeService>
    *           Le type du service
    * @param p_interfaceService
    *           La classe de service qui aura des logs
    * @param p_service
    *           Le service
    * @return Le proxy du service avec des logs
    */
   @SuppressWarnings("unchecked")
   public static <TypeService> TypeService createProxy (final Class<TypeService> p_interfaceService,
            final Object p_service)
   {
      final AsyncProxy<TypeService> v_syncProxy = new AsyncProxy<TypeService>(p_service);
      return (TypeService) Proxy.newProxyInstance(p_service.getClass().getClassLoader(), new Class<?>[]
      {p_interfaceService }, v_syncProxy);
   }

   @Override
   public Object invoke (final Object p_proxy, final Method p_method, final Object[] p_args) throws Throwable
   {
      // recherche de la méthode dans l'implémentation synchrone
      final AsyncCallback<Object> v_asyncCallback = getAsyncCallback(p_args);
      try
      {
         // appel réel de la méthode
         final Method v_syncMethod = findSyncMetohd(p_method);
         final Object[] v_syncArgs = buildSyncArgs(p_args);
         final Object v_ret = ReflectUtil.invokeMethod(v_syncMethod, _serviceSynchrone, v_syncArgs);
         v_asyncCallback.onSuccess(v_ret);
      }
      catch (final Throwable v_throwable)
      {
         v_asyncCallback.onFailure(v_throwable);
      }

      // appel asynchrone : aucun retour
      return null;
   }

   /**
    * Cherche le callback parmi les paramètres.
    * @param p_args
    *           les paramètres de la méthode asynchrone
    * @return le callback de la méthode
    */
   @SuppressWarnings("unchecked")
   private AsyncCallback<Object> getAsyncCallback (final Object[] p_args)
   {
      if (p_args.length <= 0)
      {
         throw new Spi4jRuntimeException("Cette méthode devrait avoir au moins un paramètre : le callback",
                  "Vérifiez la construction du proxy pour " + _serviceSynchrone.getClass().getName());
      }
      final Object v_lastParam = p_args[p_args.length - 1];
      if (!(v_lastParam instanceof AsyncCallback))
      {
         throw new Spi4jRuntimeException("Le dernier paramètre de la méthode aurait du être un callback",
                  "Vérifiez la construction du proxy pour " + _serviceSynchrone.getClass().getName());
      }
      return (AsyncCallback<Object>) v_lastParam;
   }

   /**
    * Construit un tableau de paramètres pour l'appel à la méthode synchrone.
    * @param p_args
    *           les paramètres pour la méthode asynchrone
    * @return les paramètres pour la méthode synchrone
    */
   private Object[] buildSyncArgs (final Object[] p_args)
   {
      if (p_args.length <= 0)
      {
         throw new Spi4jRuntimeException("Cette méthode devrait avoir au moins un paramètre : le callback",
                  "Vérifiez la construction du proxy pour " + _serviceSynchrone.getClass().getName());
      }
      return Arrays.copyOfRange(p_args, 0, p_args.length - 1);
   }

   /**
    * Cherche la méthode synchrone équivalente à la méthode asynchrone dans l'implémentation du service.
    * @param p_method
    *           la méthode asynchrone
    * @return la méthode synchrone
    */
   private Method findSyncMetohd (final Method p_method)
   {
      if (p_method.getParameterTypes().length <= 0)
      {
         throw new Spi4jRuntimeException("Cette méthode devrait avoir au moins un paramètre : le callback",
                  "Vérifiez la construction du proxy pour " + _serviceSynchrone.getClass().getName());
      }
      final Class<?>[] v_syncParameters = Arrays.copyOfRange(p_method.getParameterTypes(), 0,
               p_method.getParameterTypes().length - 1);
      try
      {
         return _serviceSynchrone.getClass().getMethod(p_method.getName(), v_syncParameters);
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(v_e, "La méthode n'a pas été trouvée dans le service synchrone",
                  "Vérifiez la construction du proxy pour " + _serviceSynchrone.getClass().getName());
      }
   }

}
