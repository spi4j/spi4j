package fr.spi4j.ui.gwt.server.soalight;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implémentation de service.
 * @author MINARM
 */
public abstract class SpiRemoteServiceImpl extends RemoteServiceServlet
{

   private static final long serialVersionUID = 1L;

   @Override
   protected void doUnexpectedFailure (final Throwable p_e)
   {
      try
      {
         getThreadLocalResponse().reset();
      }
      catch (final IllegalStateException v_e)
      {
         /*
          * If we can't reset the request, the only way to signal that something has gone wrong is to throw an exception from here. It should be the case that we call the user's implementation code before emitting data into the response, so the only time that gets tripped is if the object serialization code blows up.
          */
         throw new RuntimeException("Unable to report failure", p_e);
      }
      // Send error message with specific status.

      final HttpServletResponse v_response = getThreadLocalResponse();
      try
      {
         LogManager.getLogger(SpiRemoteServiceImpl.class).error(p_e.getCause().toString(), p_e.getCause());
         v_response.setContentType("text/plain");
         v_response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         // v_response.setStatus(ExceptionManager.getInstance().getStatusForException(p_e.getCause()));
         // v_response.setStatus(HttpServletResponse.SC_OK);
         final String v_message = p_e.getCause().getClass().getName() + '/' + p_e.getCause().getMessage();
         try
         {
            v_response.getOutputStream().write(v_message.getBytes("UTF-8"));
         }
         catch (final IllegalStateException v_e)
         {
            // Handle the (unexpected) case where getWriter() was previously used
            v_response.getWriter().write(v_message);
         }
      }
      catch (final IOException ex)
      {
         super.doUnexpectedFailure(p_e);
      }
   }

}
