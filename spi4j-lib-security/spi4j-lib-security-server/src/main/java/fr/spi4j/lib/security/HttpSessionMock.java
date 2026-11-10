/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.filter.HttpSessionFilter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

/**
 * Mock d'une HttpSession à des fins de tests seulement.
 * @author MINARM
 */
@SuppressWarnings("deprecation")
public class HttpSessionMock implements HttpSession
{

   private final Map<String, Object> _attributes = new HashMap<>();

   @Override
   public long getCreationTime ()
   {
      return 0;
   }

   @Override
   public String getId ()
   {
      return null;
   }

   @Override
   public long getLastAccessedTime ()
   {
      return 0;
   }

   @Override
   public ServletContext getServletContext ()
   {
      return null;
   }

   @Override
   public void setMaxInactiveInterval (final int p_interval)
   {
      // RAS
   }

   @Override
   public int getMaxInactiveInterval ()
   {
      return 0;
   }

   @Override
   public HttpSessionContext getSessionContext ()
   {
      return null;
   }

   @Override
   public Object getAttribute (final String p_name)
   {
      return _attributes.get(p_name);
   }

   @Override
   public Object getValue (final String p_name)
   {
      return null;
   }

   @Override
   public Enumeration<String> getAttributeNames ()
   {
      return Collections.enumeration(_attributes.keySet());
   }

   @Override
   public String[] getValueNames ()
   {
      return null;
   }

   @Override
   public void setAttribute (final String p_name, final Object p_value)
   {
      _attributes.put(p_name, p_value);
   }

   @Override
   public void putValue (final String p_name, final Object p_value)
   {
      // RAS
   }

   @Override
   public void removeAttribute (final String p_name)
   {
      _attributes.remove(p_name);
   }

   @Override
   public void removeValue (final String p_name)
   {
      // RAS
   }

   @Override
   public void invalidate ()
   {
      _attributes.clear();
   }

   @Override
   public boolean isNew ()
   {
      return false;
   }

   /**
    * Initialise une session mockée pour les tests.
    */
   public static void setHttpSessionMockInHttpSessionFilter ()
   {
      try
      {
         final Class<HttpSessionFilter> v_clazz = HttpSessionFilter.class;
         final Method v_initializedMethod = v_clazz.getDeclaredMethod("setInitialized", Boolean.TYPE);
         v_initializedMethod.setAccessible(true);
         v_initializedMethod.invoke(null, true);
         final Method v_bindMethod = v_clazz.getDeclaredMethod("bindHttpSessionOnCurrentThread", HttpSession.class);
         v_bindMethod.setAccessible(true);
         v_bindMethod.invoke(null, new HttpSessionMock());
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Impossible d'initialiser la session mockée",
                  "Vérifier le champ c_threadLocal dans la classe RemotingServlet");
      }

   }

}
