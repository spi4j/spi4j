/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import java.io.Serializable;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

/**
 * Interceptor de sécurité pour vérifier que l'utilisateur connecté a les permissions pour invoquer les méthodes des services.
 * @author MINARM
 */
public final class SecurityInterceptor implements Serializable
{
   private static final long serialVersionUID = 1L;

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
      // vérification de l'authentification et des permissions
      SecurityProxy.checkPermission(p_context.getMethod());

      // si ok, on invoque directement le service
      return p_context.proceed();
   }
}
