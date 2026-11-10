/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import fr.spi4j.testapp.MyPersonneService;
import fr.spi4j.testapp.MyPersonneService_Itf;
import jakarta.interceptor.InvocationContext;

/**
 * Test unitaire de la classe ServiceLogInterceptor.
 * @author MINARM
 */
public class ServiceLogInterceptor_Test
{
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

      final ServiceLogInterceptor v_serviceLogInterceptor = new ServiceLogInterceptor();
      v_serviceLogInterceptor.intercept(v_mockInvocationContext);
      // le 2ème appel devrait devrait trouver le proxy dans le cache de proxies
      v_serviceLogInterceptor.intercept(v_mockInvocationContext);

      final InvocationContext v_mockInvocationContext2 = new MockInvocationContext(v_service, new Exception("test"));
      try
      {
         v_serviceLogInterceptor.intercept(v_mockInvocationContext2);
      }
      catch (final Exception v_ex)
      {
         // ok
         assertNotNull(v_ex);
      }
      try
      {
         final InvocationContext v_mockInvocationContext3 = new MockInvocationContext(v_service, new Error("test"));
         v_serviceLogInterceptor.intercept(v_mockInvocationContext3);
      }
      catch (final Error v_e)
      {
         // ok
         assertNotNull(v_e);
      }
   }
}
