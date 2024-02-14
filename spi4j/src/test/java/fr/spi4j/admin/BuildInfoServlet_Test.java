/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.admin;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Classe de test de la servlet d'administration.
 * @author MINARM
 */
public class BuildInfoServlet_Test
{

   /**
    * Test de la méthode service index : seul le get est autorisé par la servlet d'administration.
    * @throws IOException
    *            e
    * @throws ServletException
    *            e
    */
   @Test
   public void testGetIndex () throws ServletException, IOException
   {
      final ServletOutputStream v_servletOutputStream = new ServletOutputStream()
      {
         @Override
         public void write (final int p_b) throws IOException
         {
            // flux sans fond
         }

         @Override
         public boolean isReady ()
         {
            return false;
         }

         @Override
         public void setWriteListener (final WriteListener arg0)
         {
            // flux sans fond
         }
      };

      final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
      final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
      Mockito.when(v_request.getMethod()).thenReturn("GET");
      Mockito.when(v_request.getPathInfo()).thenReturn("/version");
      Mockito.when(v_response.getOutputStream()).thenReturn(v_servletOutputStream);
      final BuildInfoServlet v_servlet = new BuildInfoServlet()
      {
         private static final long serialVersionUID = 1L;

         @Override
         protected BuildInfo_Abs getBuildInfo ()
         {
            return new BuildInfo_Abs()
            {

               @Override
               public String getVersion ()
               {
                  return "1.0";
               }

               @Override
               public String getUrl ()
               {
                  return "http://spi4j.googlecode.com";
               }

               @Override
               public String getRevision ()
               {
                  return "42";
               }

               @Override
               public String getNomApplication ()
               {
                  return "Spi4J-Test";
               }
            };
         }

      };
      v_servlet.service(v_request, v_response);
   }

}
