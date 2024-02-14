/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Implémentation de jakarta.servlet.Filter utilisée pour contrôler la mise en cache dans le navigateur client. <br>
 * Note : Une "limitation de la sécurité" de MSIE 5.5 (bug non présent dans Firefox) n'accepte ni Cache-Control=no-cache, ni Pragma=no-cache<br/>
 * en SSL sur les téléchargements de fichiers pdf, doc, xls, xml : remplacer Cache-Control=no-cache par max-age=1.
 * @author MINARM
 */
public final class CacheControlFilter implements Filter
{
   private Map<String, String> _headers;

   @Override
   public void init (final FilterConfig p_filterConfig)
   {
      final Map<String, String> v_tmp = new LinkedHashMap<>();
      for (final Enumeration<?> v_en = p_filterConfig.getInitParameterNames(); v_en.hasMoreElements();)
      {
         final String v_name = (String) v_en.nextElement();
         final String v_value = p_filterConfig.getInitParameter(v_name);
         v_tmp.put(v_name, v_value);
      }
      this._headers = Collections.unmodifiableMap(v_tmp);
   }

   @Override
   public void doFilter (final ServletRequest p_req, final ServletResponse p_res, final FilterChain p_chain)
            throws IOException, ServletException
   {
      if (!(p_req instanceof HttpServletRequest) || !(p_res instanceof HttpServletResponse) || _headers.isEmpty()
               || "POST".equals(((HttpServletRequest) p_req).getMethod()))
      {
         p_chain.doFilter(p_req, p_res);
         return;
      }

      final HttpServletRequest v_httpRequest = (HttpServletRequest) p_req;
      final HttpServletResponse v_httpResponse = (HttpServletResponse) p_res;

      for (final Map.Entry<String, String> v_entry : _headers.entrySet())
      {
         v_httpResponse.setHeader(v_entry.getKey(), v_entry.getValue());
      }

      p_chain.doFilter(v_httpRequest, v_httpResponse);
   }

   @Override
   public void destroy ()
   {
      // RAS
   }
}
