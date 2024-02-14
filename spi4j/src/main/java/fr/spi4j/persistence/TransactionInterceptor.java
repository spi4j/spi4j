/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import java.io.Serializable;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

/**
 * Intercepteur pour EJB 3 qui démarre une transaction Spi4J avant l'appel de la méthode puis qui commit la transaction après l'appel de la méthode ou qui rollback la transaction en cas d'exception.
 * @author MINARM
 */
public final class TransactionInterceptor implements Serializable
{
   private static final long serialVersionUID = 1L;

   private static UserPersistence_Abs userPersistence;

   /**
    * Initialise le userPersistence pour pouvoir utiliser cet interceptor :<br/>
    * au démarrage de l'application, appeler "TransactionInterceptor.initUserPersistence(XxParamPersistence.getUserPersistence());"
    * @param p_userPersistence
    *           UserPersistence_Abs
    */
   public static void initUserPersistence (final UserPersistence_Abs p_userPersistence)
   {
      userPersistence = p_userPersistence;
   }

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
      userPersistence.begin();

      try
      {
         final Object v_result = p_context.proceed();

         userPersistence.commit();

         return v_result;
      }
      catch (final Throwable v_t)
      {
         userPersistence.rollback();

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
   }
}
