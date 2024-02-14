/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import fr.spi4j.testapp.MyPersonneService_Itf;
import fr.spi4j.testapp.MyUserBusinessGen;
import fr.spi4j.testapp.test.DatabaseInitialization;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Test unitaire de la classe RemotingServlet (remarque : Remoting_Test teste aussi le remoting complet en démarrant un serveur http).
 * @author MINARM
 */
public class RemotingServlet_Test
{
   /**
    * Test de la méthode service : seul le post est autorisé par la servlet de remoting.
    * @throws IOException
    *            e
    * @throws ServletException
    *            e
    */
   @Test
   public void testGetService () throws ServletException, IOException
   {
      final RemotingServlet v_servlet = new RemotingServlet();
      final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
      final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
      Mockito.when(v_request.getMethod()).thenReturn("GET");
      v_servlet.service(v_request, v_response);
      // on vérifie que la servlet à appeler sendError pour ce service qui n'est pas un POST
      Mockito.verify(v_response).sendError(HttpServletResponse.SC_FORBIDDEN, "Direct access forbidden");
   }

   /**
    * Test de la méthode service : seul le post est autorisé par la servlet de remoting.
    * @throws IOException
    *            e
    * @throws ServletException
    *            e
    * @throws NoSuchMethodException
    *            e
    */
   @Test
   public void testPostService () throws ServletException, IOException, NoSuchMethodException
   {
      DatabaseInitialization.initDatabase();
      RemotingServlet.setUserBusiness(MyUserBusinessGen.getSingleton());

      final Method v_method = MyPersonneService_Itf.class.getMethod("findAll", new Class[]
      {});
      // fetchingStrategy null pour avoir une exception qui sera retournée dans le flux de réponse
      final RemotingRequest v_remotingRequest = new RemotingRequest(MyPersonneService_Itf.class, v_method, new Object[]
      {});
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

      // test avec gzip
      final InputStream v_inputStream = new ByteArrayInputStream(SerializationUtil.serializeAndGZip(v_remotingRequest));
      try
      {
         final ServletInputStream v_servletInputStream = new ServletInputStream()
         {
            @Override
            public int read () throws IOException
            {
               return v_inputStream.read();
            }

            @Override
            public boolean isFinished ()
            {
               return false;
            }

            @Override
            public boolean isReady ()
            {
               return false;
            }

            @Override
            public void setReadListener (final ReadListener arg0)
            {
               // flux sans fond
            }
         };
         final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
         final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
         Mockito.when(v_request.getMethod()).thenReturn("POST");
         Mockito.when(v_request.getHeader("Accept-Encoding")).thenReturn("gzip");
         Mockito.when(v_request.getHeader("Content-Encoding")).thenReturn("gzip");
         Mockito.when(v_request.getInputStream()).thenReturn(v_servletInputStream);
         Mockito.when(v_response.getOutputStream()).thenReturn(v_servletOutputStream);
         final RemotingServlet v_servlet = new RemotingServlet();
         v_servlet.service(v_request, v_response);
      }
      finally
      {
         v_inputStream.close();
      }

      // test sans gzip
      final InputStream v_inputStream2 = new ByteArrayInputStream(SerializationUtil.serialize(v_remotingRequest));
      try
      {
         final ServletInputStream v_servletInputStream = new ServletInputStream()
         {
            @Override
            public int read () throws IOException
            {
               return v_inputStream2.read();
            }

            @Override
            public boolean isFinished ()
            {
               return false;
            }

            @Override
            public boolean isReady ()
            {
               return false;
            }

            @Override
            public void setReadListener (final ReadListener arg0)
            {
               // NA
            }
         };
         final HttpServletRequest v_request = Mockito.mock(HttpServletRequest.class);
         final HttpServletResponse v_response = Mockito.mock(HttpServletResponse.class);
         Mockito.when(v_request.getMethod()).thenReturn("POST");
         // pas de gzip envoyé ni accepté pour ce test
         Mockito.when(v_request.getInputStream()).thenReturn(v_servletInputStream);
         Mockito.when(v_response.getOutputStream()).thenReturn(v_servletOutputStream);
         final RemotingServlet v_servlet = new RemotingServlet();
         v_servlet.service(v_request, v_response);
      }
      finally
      {
         v_inputStream2.close();
      }
   }
}
