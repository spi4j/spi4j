/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.admin;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
public class AdministrationServlet_Test
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
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public void setWriteListener (final WriteListener arg0)
         {
            // TODO Auto-generated method stub

         }
      };

      final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
      final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
      Mockito.when(v_request.getMethod()).thenReturn("GET");
      Mockito.when(v_request.getPathInfo()).thenReturn("/");
      Mockito.when(v_response.getOutputStream()).thenReturn(v_servletOutputStream);
      final AdministrationServlet v_servlet = new AdministrationServlet();
      v_servlet.service(v_request, v_response);
   }

   /**
    * Test de la méthode service clear caches: seul le get est autorisé par la servlet d'administration.
    * @throws IOException
    *            e
    * @throws ServletException
    *            e
    */
   @Test
   public void testGetClearCaches () throws ServletException, IOException
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
            // flux sans fond.
         }
      };

      final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
      final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
      Mockito.when(v_request.getMethod()).thenReturn("GET");
      Mockito.when(v_request.getPathInfo()).thenReturn("/clearCaches");
      Mockito.when(v_response.getOutputStream()).thenReturn(v_servletOutputStream);
      final AdministrationServlet v_servlet = new AdministrationServlet();
      v_servlet.service(v_request, v_response);
   }

   /**
    * Test de la méthode service inconnu : seul le get est autorisé par la servlet d'administration.
    */
   public void testGetServiceInconnu ()
   {
      assertThrows(ServletException.class, () -> {
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
         Mockito.when(v_request.getPathInfo()).thenReturn("/serviceInconnu");
         Mockito.when(v_response.getOutputStream()).thenReturn(v_servletOutputStream);
         final AdministrationServlet v_servlet = new AdministrationServlet();
         v_servlet.service(v_request, v_response);
      });
   }

   /**
    * Test de la méthode service post : seul le get est autorisé par la servlet d'administration.
    * @throws IOException
    *            e
    * @throws ServletException
    *            e
    */
   @Test
   public void testPost () throws ServletException, IOException
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
      Mockito.when(v_request.getMethod()).thenReturn("POST");
      Mockito.when(v_request.getPathInfo()).thenReturn("/serviceInconnu");
      Mockito.when(v_response.getOutputStream()).thenReturn(v_servletOutputStream);
      final AdministrationServlet v_servlet = new AdministrationServlet();
      v_servlet.service(v_request, v_response);
   }

}
