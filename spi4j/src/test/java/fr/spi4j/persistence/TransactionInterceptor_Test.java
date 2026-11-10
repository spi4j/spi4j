/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import fr.spi4j.business.MockInvocationContext;
import fr.spi4j.testapp.MyParamPersistence;
import fr.spi4j.testapp.MyPersonneService;
import fr.spi4j.testapp.MyPersonneService_Itf;
import jakarta.interceptor.InvocationContext;

/**
 * Test unitaire de la classe TransactionInterceptor.
 * @author MINARM
 */
public class TransactionInterceptor_Test
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

      final TransactionInterceptor v_transactionInterceptor = new TransactionInterceptor();
      TransactionInterceptor.initUserPersistence(MyParamPersistence.getUserPersistence());
      v_transactionInterceptor.intercept(v_mockInvocationContext);

      final InvocationContext v_mockInvocationContext2 = new MockInvocationContext(v_service, new Exception("test"));
      try
      {
         v_transactionInterceptor.intercept(v_mockInvocationContext2);
      }
      catch (final Exception v_ex)
      {
         // ok
         assertNotNull(v_ex);
      }
      try
      {
         final InvocationContext v_mockInvocationContext3 = new MockInvocationContext(v_service, new Error("test"));
         v_transactionInterceptor.intercept(v_mockInvocationContext3);
      }
      catch (final Error v_e)
      {
         // ok
         assertNotNull(v_e);
      }
   }
}
