/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.filter;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Test unitaire de la classe HttpSessionFilter.
 * @author MINARM
 */
public class HttpSessionFilter_Test
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
      final HttpSessionFilter v_httpSessionFilter = new HttpSessionFilter();
      final FilterConfig v_config = Mockito.mock(FilterConfig.class);
      try
      {
         v_httpSessionFilter.init(v_config);
         final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
         final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
         final FilterChain v_chain = Mockito.mock(FilterChain.class);
         final HttpSession v_session = Mockito.mock(HttpSession.class);
         Mockito.when(v_request.getSession()).thenReturn(v_session);
         v_httpSessionFilter.doFilter(v_request, v_response, v_chain);

         final ServletResponse v_response2 = Mockito.mock(ServletResponse.class);
         v_httpSessionFilter.doFilter(v_request, v_response2, v_chain);
         final ServletRequest v_request2 = Mockito.mock(ServletRequest.class);
         v_httpSessionFilter.doFilter(v_request2, v_response2, v_chain);
      }
      finally
      {
         v_httpSessionFilter.destroy();
      }

      assertNull(HttpSessionFilter.getSessionForCurrentThread(),
               "Après la fin de doFilter, getSessionForCurrentThread() doit toujours retourner null");
   }
}
