/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import org.junit.jupiter.api.Test;

import fr.spi4j.testapp.MyPersonneService;
import fr.spi4j.testapp.MyPersonneService_Itf;
import jakarta.interceptor.InvocationContext;

/**
 * Test unitaire de la classe ServiceCacheInterceptor.
 * @author MINARM
 */
public class ServiceCacheInterceptor_Test
{
   /**
    * Service implémentant ServiceReferentiel_Itf.
    * @author MINARM
    */
   private static class MyReferentielService extends MyPersonneService implements ServiceReferentiel_Itf
   {
      // RAS
   }

   /**
    * Test.
    * @throws Exception
    *            e
    */
   @Test
   public void testIntercept () throws Exception
   {
      final MyPersonneService_Itf v_service = new MyPersonneService();
      final InvocationContext v_mockInvocationContext = new MockInvocationContext(v_service, null);

      new ServiceCacheInterceptor().intercept(v_mockInvocationContext);

      final MyPersonneService_Itf v_service2 = new MyReferentielService();
      final InvocationContext v_mockInvocationContext2 = new MockInvocationContext(v_service2, null);

      new ServiceCacheInterceptor().intercept(v_mockInvocationContext2);
      // le 2ème appel devrait devrait trouver le proxy dans le cache de proxies
      new ServiceCacheInterceptor().intercept(v_mockInvocationContext2);
   }
}
