/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import jakarta.interceptor.InvocationContext;
import net.bull.javamelody.MonitoringInterceptor;

/**
 * Intercepteur de monitoring pour EJB 3.
 * @author MINARM
 */
public class MonitoringEjbInterceptor extends MonitoringInterceptor
{
   private static final long serialVersionUID = 1L;

   @Override
   protected String getRequestName (final InvocationContext p_context)
   {
      final Class<?> v_interface = ServiceLogInterceptor.getInterfaceOfService(p_context);
      return v_interface.getSimpleName() + '.' + p_context.getMethod().getName();
   }
}
