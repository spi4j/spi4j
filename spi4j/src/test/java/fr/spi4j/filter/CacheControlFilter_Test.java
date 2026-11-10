/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Test unitaire de la classe CacheControlFilter.
 * @author MINARM
 */
public class CacheControlFilter_Test
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
      final CacheControlFilter v_cacheControlFilter = new CacheControlFilter();
      final FilterConfig v_config = Mockito.mock(FilterConfig.class);
      final Map<String, String> v_initParameters = Collections.singletonMap("Cache-Control", "no-cache");
      Mockito.when(v_config.getInitParameterNames()).thenReturn(Collections.enumeration(v_initParameters.keySet()));
      for (final Map.Entry<String, String> v_entry : v_initParameters.entrySet())
      {
         Mockito.when(v_config.getInitParameter(v_entry.getKey())).thenReturn(v_entry.getValue());
      }
      try
      {
         v_cacheControlFilter.init(v_config);
         final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
         final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
         final FilterChain v_chain = Mockito.mock(FilterChain.class);
         v_cacheControlFilter.doFilter(v_request, v_response, v_chain);

         final ServletResponse v_response2 = Mockito.mock(ServletResponse.class);
         v_cacheControlFilter.doFilter(v_request, v_response2, v_chain);
         final ServletRequest v_request2 = Mockito.mock(ServletRequest.class);
         v_cacheControlFilter.doFilter(v_request2, v_response2, v_chain);
      }
      finally
      {
         v_cacheControlFilter.destroy();
      }
   }

   /**
    * Test.
    * @throws ServletException
    *            e
    * @throws IOException
    *            e
    */
   @Test
   public void testPost () throws IOException, ServletException
   {
      final CacheControlFilter v_cacheControlFilter = new CacheControlFilter();
      final FilterConfig v_config = Mockito.mock(FilterConfig.class);
      final Map<String, String> v_initParameters = Collections.singletonMap("Cache-Control", "no-cache");
      Mockito.when(v_config.getInitParameterNames()).thenReturn(Collections.enumeration(v_initParameters.keySet()));
      for (final Map.Entry<String, String> v_entry : v_initParameters.entrySet())
      {
         Mockito.when(v_config.getInitParameter(v_entry.getKey())).thenReturn(v_entry.getValue());
      }
      try
      {
         v_cacheControlFilter.init(v_config);
         final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
         final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
         final FilterChain v_chain = Mockito.mock(FilterChain.class);
         Mockito.when(v_request.getMethod()).thenReturn("POST");
         v_cacheControlFilter.doFilter(v_request, v_response, v_chain);
      }
      finally
      {
         v_cacheControlFilter.destroy();
      }
   }

   /**
    * Test.
    * @throws ServletException
    *            e
    * @throws IOException
    *            e
    */
   @Test
   public void testNoHeaders () throws IOException, ServletException
   {
      final CacheControlFilter v_cacheControlFilter = new CacheControlFilter();
      final FilterConfig v_config = Mockito.mock(FilterConfig.class);
      final Enumeration<String> v_parameterNames = Collections.enumeration(Collections.<String> emptySet());
      Mockito.when(v_config.getInitParameterNames()).thenReturn(v_parameterNames);
      try
      {
         v_cacheControlFilter.init(v_config);
         final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
         final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
         final FilterChain v_chain = Mockito.mock(FilterChain.class);
         v_cacheControlFilter.doFilter(v_request, v_response, v_chain);
      }
      finally
      {
         v_cacheControlFilter.destroy();
      }
   }
}
