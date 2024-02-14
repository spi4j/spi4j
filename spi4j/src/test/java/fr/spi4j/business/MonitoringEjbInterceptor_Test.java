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
 * Test unitaire de la classe MonitoringEjbInterceptor.
 * @author MINARM
 */
public class MonitoringEjbInterceptor_Test
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

      new MonitoringEjbInterceptor().intercept(v_mockInvocationContext);
   }
}
