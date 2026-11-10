/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import fr.spi4j.testapp.MyPersonneService_Itf;
import jakarta.interceptor.InvocationContext;

/**
 * Invocation contexte EJB bouchon pour tests interceptors.
 * @author MINARM
 */
public class MockInvocationContext implements InvocationContext
{
   private final Object _target;

   private final Throwable _exceptionToBeThrown;

   /**
    * Constructeur.
    * @param p_target
    *           Object
    * @param p_exceptionToBeThown
    *           Exception ou null
    */
   public MockInvocationContext (final Object p_target, final Throwable p_exceptionToBeThown)
   {
      super();
      _target = p_target;
      _exceptionToBeThrown = p_exceptionToBeThown;
   }

   @Override
   public void setParameters (final Object[] p_params)
   {
      // RAS
   }

   @Override
   public Object proceed () throws Exception
   {
      if (_exceptionToBeThrown instanceof Exception)
      {
         throw (Exception) _exceptionToBeThrown;
      }
      else if (_exceptionToBeThrown instanceof Error)
      {
         throw (Error) _exceptionToBeThrown;
      }

      return getMethod().invoke(getTarget(), getParameters());

   }

   @Override
   public Object getTarget ()
   {
      return _target;
   }

   @Override
   public Object[] getParameters ()
   {
      return null;
   }

   @Override
   public Method getMethod ()
   {
      try
      {
         return MyPersonneService_Itf.class.getMethod("findAll");
      }
      catch (final NoSuchMethodException v_ex)
      {
         throw new RuntimeException(v_ex);
      }
   }

   @Override
   public Map<String, Object> getContextData ()
   {
      return null;
   }

   @Override
   public Constructor<?> getConstructor ()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Object getTimer ()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
