/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * Servlet Handler.
 */
final class HttpServerHandler implements HttpHandler
{
   private final RemotingServlet _servlet = new RemotingServlet();

   /**
    * Constructeur.
    * @throws ServletException
    *            erreur
    */
   HttpServerHandler () throws ServletException
   {
      super();
      _servlet.init();
   }

   @Override
   public void handle (final HttpExchange p_exchange) throws IOException
   {
      p_exchange.getResponseHeaders().set("Content-Encoding", "gzip");
      p_exchange.getResponseHeaders().set("Set-Cookie", "jsessionid=CookieDeLu");
      p_exchange.sendResponseHeaders(200, 0);

      final HttpServletRequest v_request = new RequestWrapper(p_exchange);
      final HttpServletResponse v_response = new ResponseWrapper(p_exchange);
      try
      {
         _servlet.doPost(v_request, v_response);
      }
      catch (final Exception v_ex)
      {
         LogManager.getRootLogger().warn(v_ex, v_ex);
         throw new IOException(v_ex);
      }
   }

   /**
    * Destruction.
    */
   void destroy ()
   {
      _servlet.destroy();
   }

   /**
    * Request Wrapper.
    */
   static final class RequestWrapper implements HttpServletRequest
   {
      private final HttpExchange _exchange;

      private final ServletInputStream _input;

      private BufferedReader _bufferedReader;

      /**
       * Constructeur.
       * @param p_exchange
       *           exchange.
       */
      RequestWrapper (final HttpExchange p_exchange)
      {
         super();
         _exchange = p_exchange;
         _input = new MyServletInputStream(p_exchange.getRequestBody());
      }

      @Override
      public Object getAttribute (final String p_key)
      {
         return _exchange.getAttribute(p_key);
      }

      @Override
      public Enumeration<String> getAttributeNames ()
      {
         return Collections.enumeration(_exchange.getHttpContext().getAttributes().keySet());
      }

      @Override
      public String getCharacterEncoding ()
      {
         return null;
      }

      @Override
      public int getContentLength ()
      {
         return 0;
      }

      @Override
      public String getContentType ()
      {
         return null;
      }

      @Override
      public ServletInputStream getInputStream () throws IOException
      {
         return _input;
      }

      @Override
      public String getLocalAddr ()
      {
         return null;
      }

      @Override
      public String getLocalName ()
      {
         return null;
      }

      @Override
      public int getLocalPort ()
      {
         return -1;
      }

      @Override
      public Locale getLocale ()
      {
         return null;
      }

      @Override
      public Enumeration<Locale> getLocales ()
      {
         return null;
      }

      @Override
      public String getParameter (final String p_arg)
      {
         return null;
      }

      @Override
      public Map<String, String[]> getParameterMap ()
      {
         return null;
      }

      @Override
      public Enumeration<String> getParameterNames ()
      {
         return null;
      }

      @Override
      public String[] getParameterValues (final String p_arg)
      {
         return null;
      }

      @Override
      public String getProtocol ()
      {
         return null;
      }

      @Override
      public BufferedReader getReader () throws IOException
      {
         if (_bufferedReader == null)
         {
            _bufferedReader = new BufferedReader(new InputStreamReader(_exchange.getRequestBody()));
         }
         return _bufferedReader;
      }

      @Override
      public String getRemoteAddr ()
      {
         return null;
      }

      @Override
      public String getRemoteHost ()
      {
         return null;
      }

      @Override
      public int getRemotePort ()
      {
         return 0;
      }

      @Override
      public RequestDispatcher getRequestDispatcher (final String p_arg)
      {
         return null;
      }

      @Override
      public String getScheme ()
      {
         return null;
      }

      @Override
      public String getServerName ()
      {
         return null;
      }

      @Override
      public int getServerPort ()
      {
         return 0;
      }

      @Override
      public boolean isSecure ()
      {
         return false;
      }

      @Override
      public void removeAttribute (final String p_arg)
      {
         // NA
      }

      @Override
      public void setAttribute (final String p_key, final Object p_value)
      {
         _exchange.setAttribute(p_key, p_value);
      }

      @Override
      public void setCharacterEncoding (final String p_arg) throws UnsupportedEncodingException
      {
         // NA
      }

      @Override
      public String getAuthType ()
      {
         return null;
      }

      @Override
      public String getContextPath ()
      {
         return "/testwebapp";
      }

      @Override
      public Cookie[] getCookies ()
      {
         return null;
      }

      @Override
      public long getDateHeader (final String p_arg)
      {
         return 0;
      }

      @Override
      public String getHeader (final String p_arg)
      {
         return _exchange.getRequestHeaders().getFirst(p_arg);
      }

      @Override
      public Enumeration<String> getHeaderNames ()
      {
         return Collections.enumeration(_exchange.getRequestHeaders().keySet());
      }

      @Override
      public Enumeration<String> getHeaders (final String p_arg)
      {
         return Collections.enumeration(_exchange.getRequestHeaders().get(p_arg));
      }

      @Override
      public int getIntHeader (final String p_arg)
      {
         return 0;
      }

      @Override
      public String getMethod ()
      {
         return _exchange.getRequestMethod();
      }

      @Override
      public String getPathInfo ()
      {
         return null;
      }

      @Override
      public String getPathTranslated ()
      {
         return null;
      }

      @Override
      public String getQueryString ()
      {
         return null;
      }

      @Override
      public String getRemoteUser ()
      {
         return null;
      }

      @Override
      public String getRequestURI ()
      {
         return null;
      }

      @Override
      public StringBuffer getRequestURL ()
      {
         return null;
      }

      @Override
      public String getRequestedSessionId ()
      {
         return null;
      }

      @Override
      public String getServletPath ()
      {
         return null;
      }

      @Override
      public HttpSession getSession ()
      {
         return null;
      }

      @Override
      public HttpSession getSession (final boolean p_arg)
      {
         return null;
      }

      @Override
      public Principal getUserPrincipal ()
      {
         return null;
      }

      @Override
      public boolean isRequestedSessionIdFromCookie ()
      {
         return false;
      }

      @Override
      public boolean isRequestedSessionIdFromURL ()
      {
         return false;
      }

      @Override
      public boolean isRequestedSessionIdValid ()
      {
         return false;
      }

      @Override
      public boolean isUserInRole (final String p_arg)
      {
         return false;
      }

      @Override
      public ServletContext getServletContext ()
      {
         return null;
      }

      @Override
      public AsyncContext startAsync ()
      {
         return null;
      }

      @Override
      public AsyncContext startAsync (final ServletRequest p_servletRequest, final ServletResponse p_servletResponse)
      {
         return null;
      }

      @Override
      public boolean isAsyncStarted ()
      {
         return false;
      }

      @Override
      public boolean isAsyncSupported ()
      {
         return false;
      }

      @Override
      public AsyncContext getAsyncContext ()
      {
         return null;
      }

      @Override
      public DispatcherType getDispatcherType ()
      {
         return null;
      }

      @Override
      public boolean authenticate (final HttpServletResponse p_response) throws IOException, ServletException
      {
         return false;
      }

      @Override
      public void login (final String p_username, final String p_password) throws ServletException
      {
         // NA
      }

      @Override
      public void logout () throws ServletException
      {
         // NA
      }

      @Override
      public Collection<Part> getParts () throws IOException, ServletException
      {
         return null;
      }

      @Override
      public Part getPart (final String p_name) throws IOException, ServletException
      {
         return null;
      }

      @Override
      public long getContentLengthLong ()
      {
         return 0;
      }

      @Override
      public String changeSessionId ()
      {
         return null;
      }

      @Override
      public <T extends HttpUpgradeHandler> T upgrade (final Class<T> arg0) throws IOException, ServletException
      {
         return null;
      }

      @Override
      @Deprecated
      public String getRealPath (final String path)
      {
         return null;
      }

      @Override
      @Deprecated
      public boolean isRequestedSessionIdFromUrl ()
      {
         return false;
      }
   }

   /**
    * Reponse Wrapper.
    */
   static final class ResponseWrapper implements HttpServletResponse
   {
      private final HttpExchange _exchange;

      private final ServletOutputStream _output;

      private PrintWriter _printWriter;

      /**
       * Constructeur.
       * @param p_exchange
       *           exchange.
       */
      ResponseWrapper (final HttpExchange p_exchange)
      {
         super();
         _exchange = p_exchange;
         _output = new MyServletOutputStream(p_exchange.getResponseBody());
      }

      @Override
      public void flushBuffer () throws IOException
      {
         _output.flush();
      }

      @Override
      public int getBufferSize ()
      {
         return 0;
      }

      @Override
      public String getCharacterEncoding ()
      {
         return null;
      }

      @Override
      public String getContentType ()
      {
         return null;
      }

      @Override
      public Locale getLocale ()
      {
         return null;
      }

      @Override
      public ServletOutputStream getOutputStream () throws IOException
      {
         return _output;
      }

      @Override
      public PrintWriter getWriter () throws IOException
      {
         if (_printWriter == null)
         {
            _printWriter = new PrintWriter(_exchange.getResponseBody());
         }
         return _printWriter;
      }

      @Override
      public boolean isCommitted ()
      {
         return false;
      }

      @Override
      public void reset ()
      {
         // NA
      }

      @Override
      public void resetBuffer ()
      {
         // NA
      }

      @Override
      public void setBufferSize (final int p_arg)
      {
         // NA
      }

      @Override
      public void setCharacterEncoding (final String p_arg)
      {
         // NA
      }

      @Override
      public void setContentLength (final int p_arg)
      {
         // NA
      }

      @Override
      public void setContentType (final String p_arg)
      {
         // NA
      }

      @Override
      public void setLocale (final Locale p_arg)
      {
         // NA
      }

      @Override
      public void addCookie (final Cookie p_arg)
      {
         // NA
      }

      @Override
      public void addDateHeader (final String p_arg0, final long p_arg1)
      {
         // NA
      }

      @Override
      public void addHeader (final String p_arg0, final String p_arg1)
      {
         _exchange.getResponseHeaders().add(p_arg0, p_arg1);
      }

      @Override
      public void addIntHeader (final String p_arg0, final int p_arg1)
      {
         // NA
      }

      @Override
      public boolean containsHeader (final String p_arg)
      {
         return _exchange.getResponseHeaders().containsKey(p_arg);
      }

      @Override
      public String encodeRedirectURL (final String p_arg)
      {
         return null;
      }

      @Override
      public String encodeURL (final String p_arg)
      {
         return null;
      }

      @Override
      public void sendError (final int p_arg) throws IOException
      {
         _exchange.sendResponseHeaders(p_arg, 0);
      }

      @Override
      public void sendError (final int p_arg0, final String p_arg1) throws IOException
      {
         _exchange.sendResponseHeaders(p_arg0, 0);
      }

      @Override
      public void sendRedirect (final String p_arg) throws IOException
      {
         // NA
      }

      @Override
      public void setDateHeader (final String p_arg0, final long p_arg1)
      {
         // NA
      }

      @Override
      public void setHeader (final String p_arg0, final String p_arg1)
      {
         _exchange.getResponseHeaders().set(p_arg0, p_arg1);
      }

      @Override
      public void setIntHeader (final String p_arg0, final int p_arg1)
      {
         // NA
      }

      @Override
      public void setStatus (final int p_arg)
      {
         // NA
      }

      @Override
      public int getStatus ()
      {
         return 0;
      }

      @Override
      public String getHeader (final String p_name)
      {
         return null;
      }

      @Override
      public Collection<String> getHeaders (final String p_name)
      {
         return null;
      }

      @Override
      public Collection<String> getHeaderNames ()
      {
         return null;
      }

      @Override
      public void setContentLengthLong (final long arg0)
      {
         // NA
      }

      @Override
      @Deprecated
      public String encodeUrl (final String url)
      {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      @Deprecated
      public String encodeRedirectUrl (final String url)
      {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      @Deprecated
      public void setStatus (final int sc, final String sm)
      {
         // TODO Auto-generated method stub

      }
   }

   /**
    * Input Stream.
    */
   private static final class MyServletInputStream extends ServletInputStream
   {
      private final InputStream _input;

      /**
       * Constructeur.
       * @param p_input
       *           stream.
       */
      MyServletInputStream (final InputStream p_input)
      {
         super();
         _input = p_input;
      }

      @Override
      public int read () throws IOException
      {
         return _input.read();
      }

      @Override
      public int read (final byte[] p_bytes) throws IOException
      {
         return _input.read(p_bytes);
      }

      @Override
      public int read (final byte[] p_bytes, final int p_off, final int p_len) throws IOException
      {
         return _input.read(p_bytes, p_off, p_len);
      }

      @Override
      public void close () throws IOException
      {
         _input.close();
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
   }

   /**
    * Output Stream.
    */
   private static final class MyServletOutputStream extends ServletOutputStream
   {
      private final OutputStream _output;

      /**
       * Constructeur.
       * @param p_output
       *           stream.
       */
      MyServletOutputStream (final OutputStream p_output)
      {
         super();
         _output = p_output;
      }

      @Override
      public void write (final int p_b) throws IOException
      {
         _output.write(p_b);
      }

      @Override
      public void write (final byte[] p_bytes) throws IOException
      {
         _output.write(p_bytes);
      }

      @Override
      public void write (final byte[] p_bytes, final int p_off, final int p_len) throws IOException
      {
         _output.write(p_bytes, p_off, p_len);
      }

      @Override
      public void close () throws IOException
      {
         _output.close();
      }

      @Override
      public void flush () throws IOException
      {
         _output.flush();
      }

      @Override
      public boolean isReady ()
      {
         return false;
      }

      @Override
      public void setWriteListener (final WriteListener arg0)
      {
         // NA
      }
   }
}
