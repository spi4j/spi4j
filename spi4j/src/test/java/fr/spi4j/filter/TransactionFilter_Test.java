/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import fr.spi4j.persistence.ParamPersistence_Abs;
import fr.spi4j.persistence.UserPersistence_Abs;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Test unitaire de la classe TransactionFilter.
 * @author MINARM
 */
public class TransactionFilter_Test
{
   /**
    * Test.
    * @throws ServletException
    *            e
    * @throws IOException
    *            e
    */
   @Test
   public void test () throws IOException, ServletException
   {
      final TransactionFilter v_transactionFilter = new TransactionFilter();
      final ParamPersistence_Abs v_paramPersistence_Abs = new ParamPersistence_Abs("")
      {
         @Override
         protected void afterInit ()
         {
            // RAS
         }

         @Override
         protected void initElemParamPersistence ()
         {
            // RAS
         }
      };
      final UserPersistence_Abs v_userPersistence = new UserPersistence_Abs(v_paramPersistence_Abs)
      {
         // pas d'implémentation
      };
      TransactionFilter.initUserPersistence(v_userPersistence);
      final FilterConfig v_config = Mockito.mock(FilterConfig.class);
      try
      {
         v_transactionFilter.init(v_config);
         final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
         final FilterChain v_chain = Mockito.mock(FilterChain.class);
         final HttpSession v_session = Mockito.mock(HttpSession.class);
         Mockito.when(v_request.getSession()).thenReturn(v_session);

         final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
         v_transactionFilter.doFilter(v_request, v_response, v_chain);

         final ServletResponse v_response2 = Mockito.mock(ServletResponse.class);
         v_transactionFilter.doFilter(v_request, v_response2, v_chain);

         final ServletRequest v_request2 = Mockito.mock(ServletRequest.class);
         v_transactionFilter.doFilter(v_request2, v_response2, v_chain);

         // Filtre sans UserPersistence
         TransactionFilter.initUserPersistence(null);
         try
         {
            v_transactionFilter.doFilter(v_request, v_response, v_chain);
            fail("Une exception aurait dû être levée");
         }
         catch (final IllegalStateException v_e)
         {
            assertNotNull(v_e, "Exception attendue");
         }
         finally
         {
            // on repositionne un user persistence
            TransactionFilter.initUserPersistence(v_userPersistence);
         }

         // Filtre qui renvoie une erreur
         final HttpServletResponse v_responseError = Mockito.mock(HttpServletResponse.class);
         final ServletRequest v_requestError = Mockito.mock(ServletRequest.class);
         Mockito.doThrow(new ServletException("Message")).when(v_chain).doFilter(v_requestError, v_responseError);
         try
         {
            v_transactionFilter.doFilter(v_requestError, v_responseError, v_chain);
         }
         catch (final ServletException v_e)
         {
            assertNotNull(v_e, "Exception attendue");
            assertEquals("Message", v_e.getMessage(), "Exception attendue");
         }
         Mockito.doThrow(new IOException("Message")).when(v_chain).doFilter(v_requestError, v_responseError);
         try
         {
            v_transactionFilter.doFilter(v_requestError, v_responseError, v_chain);
         }
         catch (final IOException v_e)
         {
            assertNotNull(v_e, "Exception attendue");
            assertEquals("Message", v_e.getMessage(), "Exception attendue");
         }
         Mockito.doThrow(new RuntimeException("Message")).when(v_chain).doFilter(v_requestError, v_responseError);
         try
         {
            v_transactionFilter.doFilter(v_requestError, v_responseError, v_chain);
         }
         catch (final RuntimeException v_e)
         {
            assertNotNull(v_e, "Exception attendue");
            assertEquals("Message", v_e.getMessage(), "Exception attendue");
         }
         Mockito.doThrow(new Error("Message")).when(v_chain).doFilter(v_requestError, v_responseError);
         try
         {
            v_transactionFilter.doFilter(v_requestError, v_responseError, v_chain);
         }
         catch (final Error v_e)
         {
            assertNotNull(v_e, "Exception attendue");
            assertEquals("Message", v_e.getMessage(), "Exception attendue");
         }

      }
      finally
      {
         v_transactionFilter.destroy();
      }

      /**
       * Problème de test usur Jenkins attente de correction
       */

      // try
      // {
      // // assertEquals("La transaction doit être commitée", Status.STATUS_NO_TRANSACTION, v_userPersistence.getUserTransaction().getStatus());
      // }
      // catch (final SystemException v_e)
      // {
      // fail(v_e.toString());
      // }
   }
}
