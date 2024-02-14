/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.requirement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import fr.spi4j.ReflectUtil;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.lib.jbehave.SpiStoryContextHandler;
import fr.spi4j.requirement.Requirement_Itf;

/**
 * Proxy pour intercepter les méthodes annotées avec des Requirements.
 * @author MINARM
 */
public final class RequirementsInServiceProxy implements InvocationHandler
{

   private static final Logger c_log = LogManager.getLogger(RequirementsInServiceProxy.class);

   private final Object _delegate;

   private final List<Class<? extends Annotation>> _annotationClasses;

   /**
    * Constructeur privé.
    * @param p_delegate
    *           le service réel
    * @param p_annotationClasses
    *           l'annotation recherchée
    */
   private RequirementsInServiceProxy (final Object p_delegate,
            final List<Class<? extends Annotation>> p_annotationClasses)
   {
      super();
      _delegate = p_delegate;
      _annotationClasses = p_annotationClasses;
   }

   @Override
   public Object invoke (final Object p_proxy, final Method p_method, final Object[] p_args) throws Throwable
   {
      // création du mock
      // recherche le champ _requirements dans la classe du service
      for (final Field v_field : _delegate.getClass().getDeclaredFields())
      {
         // déduire le type du champ
         final Class<?> v_fieldType = v_field.getType();

         if (Modifier.isFinal(v_fieldType.getModifiers()) || v_fieldType.isAnonymousClass()
                  || v_fieldType.isPrimitive()
                  || (Modifier.isStatic(v_field.getModifiers()) && Modifier.isFinal(v_field.getModifiers())))
         {
            c_log.debug("Aucun mock positionné sur " + v_field.getName() + " dans la classe "
                     + _delegate.getClass().getSimpleName());
            continue;
         }

         // construit le mock des requirements
         final Object v_mock = Mockito.mock(v_fieldType, new Answer<Object>()
         {
            @Override
            public Object answer (final InvocationOnMock p_invocation) throws Throwable
            {
               if (v_fieldType.equals(p_invocation.getMethod().getDeclaringClass()))
               {
                  final List<Annotation> v_annotations = getMethodAnnotation(p_invocation.getMethod(), v_fieldType,
                           _annotationClasses);
                  if (v_annotations != null && !v_annotations.isEmpty())
                  {
                     for (final Annotation v_annotation : v_annotations)
                     {
                        final Requirement_Itf[] v_reqs = (Requirement_Itf[]) v_annotation.getClass().getMethod("value")
                                 .invoke(v_annotation);
                        for (final Requirement_Itf v_req : v_reqs)
                        {
                           final RequirementContext v_context = SpiStoryContextHandler.get_currentRequirementContext();
                           if (v_context == null)
                           {
                              throw new Spi4jRuntimeException(
                                       "La session d'enregistrement des requirements n'a pas été initialisée",
                                       "Vérifier qu'un appel à RequirementSession.start() a été fait");
                           }
                           v_context.used(v_req, p_method);
                        }
                     }
                  }
               }
               return p_invocation.callRealMethod();
            }
         });

         try
         {
            // injecte le requirement mocké dans l'instance du service.
            v_field.setAccessible(true);
            v_field.set(_delegate, v_mock);
         }
         catch (final Exception v_e)
         {
            c_log.warn(v_e.toString(), v_e);
         }
      }
      return ReflectUtil.invokeMethod(p_method, _delegate, p_args);
   }

   /**
    * Récupère l'annotation sur la méthode.
    * @param <RequirementAnnotation>
    *           le type d'annotation recherchée
    * @param p_method
    *           la méthode interceptée
    * @param p_class
    *           la classe à partir de laquelle rechercher
    * @param p_annotationClasses
    *           le type d'annotation recherchée
    * @return l'annotation trouvée ou null si elle n'a pas été trouvée
    */
   private static <RequirementAnnotation extends Annotation> List<RequirementAnnotation> getMethodAnnotation (
            final Method p_method, final Class<?> p_class,
            final List<Class<? extends RequirementAnnotation>> p_annotationClasses)
   {
      try
      {
         final Method v_method = p_class.getMethod(p_method.getName(), p_method.getParameterTypes());
         final List<RequirementAnnotation> v_foundAnnotations = new ArrayList<>();
         for (final Class<? extends RequirementAnnotation> v_annotationClass : p_annotationClasses)
         {
            final RequirementAnnotation v_annotation = v_method.getAnnotation(v_annotationClass);
            if (v_annotation != null)
            {
               v_foundAnnotations.add(v_annotation);
            }
            else
            {
               for (final Class<?> v_interface : p_class.getInterfaces())
               {
                  v_foundAnnotations.addAll(getMethodAnnotation(p_method, v_interface, p_annotationClasses));
               }
            }
         }
         return v_foundAnnotations;
      }
      catch (final NoSuchMethodException v_ex)
      {
         return null;
      }
   }

   /**
    * Création d'un aspect sur les services.
    * @param <TypeService>
    *           le type du service
    * @param p_delegate
    *           l'instance réel du service
    * @param p_annotationClass
    *           l'annotation Requirement recherchée
    * @return une instance du service avec son aspect
    */
   public static <TypeService> TypeService createProxy (final TypeService p_delegate,
            final Class<? extends Annotation> p_annotationClass)
   {
      final List<Class<? extends Annotation>> v_annotationClasses = new ArrayList<>();
      v_annotationClasses.add(p_annotationClass);
      return createProxy(p_delegate, v_annotationClasses);
   }

   /**
    * Création d'un aspect sur les services.
    * @param <TypeService>
    *           le type du service
    * @param p_delegate
    *           l'instance réel du service
    * @param p_annotationClasses
    *           les annotations Requirement recherchées
    * @return une instance du service avec son aspect
    */
   @SuppressWarnings("unchecked")
   public static <TypeService> TypeService createProxy (final TypeService p_delegate,
            final List<Class<? extends Annotation>> p_annotationClasses)
   {
      final RequirementsInServiceProxy v_requirementsProxy = new RequirementsInServiceProxy(p_delegate,
               p_annotationClasses);
      return (TypeService) Proxy.newProxyInstance(p_delegate.getClass().getClassLoader(), p_delegate.getClass()
               .getInterfaces(), v_requirementsProxy);
   }
}
