/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.mockito;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import fr.spi4j.lib.jbehave.lang.reflect.SpiJavaBeanUtil;

/**
 * Comportement JavaBean d'un mock.
 * @author MINARM
 */
public class JavaBeanBehaviorAnswer implements Answer<Object>
{
   private final Map<String, Object> _values = new HashMap<String, Object>();

   private final Class<?> _mockedClass;

   private final boolean _returnSmartJavaBeanNulls;

   /**
    * Contructeur du comportement pour une classe.
    * @param p_mockedClass
    *           la classe mockée
    */
   public JavaBeanBehaviorAnswer (final Class<?> p_mockedClass)
   {
      this(p_mockedClass, false);
   }

   /**
    * Contructeur du comportement pour une classe.
    * @param p_mockedClass
    *           la classe mockée
    * @param p_returnSmartJavaBeanNulls
    *           true si mocking en profondeur, false sinon
    */
   public JavaBeanBehaviorAnswer (final Class<?> p_mockedClass, final boolean p_returnSmartJavaBeanNulls)
   {
      super();
      _mockedClass = p_mockedClass;
      _returnSmartJavaBeanNulls = p_returnSmartJavaBeanNulls;
   }

   /**
    * @return la classe mockée
    */
   protected Class<?> get_mockedClass ()
   {
      return _mockedClass;
   }

   /**
    * @return true si mocking en profondeur, false sinon
    */
   protected boolean is_returnSmartJavaBeanNulls ()
   {
      return _returnSmartJavaBeanNulls;
   }

   @Override
   public Object answer (final InvocationOnMock p_invocation) throws Throwable
   {
      final Method v_method = getMethod(p_invocation);
      final String v_methodName = v_method.getName();
      if ("toString".equals(v_methodName))
      {
         return "JavaBean mock for " + _mockedClass.getName();
      }
      else if ("finalize".equals(v_methodName))
      {
         _values.clear();
         // il faut supprimer le mock !
         return p_invocation.callRealMethod();
      }
      else if (v_methodName.length() > 3)
      {
         final String v_propertyName = SpiJavaBeanUtil.getPropertyName(v_method);
         if (v_methodName.startsWith("set") && v_method.getParameterTypes().length == 1)
         {
            final Object v_value = p_invocation.getArguments()[0];
            doSetValue(p_invocation, v_propertyName, v_value);
         }
         else if (v_methodName.startsWith("get") || v_methodName.startsWith("is"))
         {
            Object v_value = _values.get(v_propertyName);
            final Class<?> v_type = v_method.getReturnType();
            if (v_value == null && _returnSmartJavaBeanNulls && shouldReturnSmartJavaBeanMock(v_type))
            {
               v_value = newMock(v_type);
               doSetValue(p_invocation, v_propertyName, v_value);
            }
            return v_value;
         }
      }
      return null;
   }

   /**
    * Retourne la méthode de l'invocation. Implémentation par recherche de l'interface de plus bas niveau pour récupérer le vrai type de retour.
    * @param p_invocation
    *           l'invocation
    * @return la méthode de l'invocation
    */
   protected Method getMethod (final InvocationOnMock p_invocation)
   {
      final Object v_mock = p_invocation.getMock();
      final Method v_invocationMethod = p_invocation.getMethod();
      final Class<? extends Object> v_mockClass = v_mock.getClass();
      final Class<?> v_superclass = v_mockClass.getSuperclass();

      Method v_method = null;
      v_method = doGetMethod(v_invocationMethod, v_superclass);

      if (v_method != null)
      {
         return v_method;
      }

      for (final Class<?> v_class : v_mockClass.getInterfaces())
      {
         v_method = doGetMethod(v_invocationMethod, v_class);
         if (v_method != null)
         {
            return v_method;
         }
      }
      return p_invocation.getMethod();
   }

   /**
    * Retourne la méthode de l'invocation pour une classe donnée.
    * @param p_invocationMethod
    *           la méthode invoquée
    * @param p_class
    *           la classe dans laquelle rechercher cette méthode
    * @return la méthode réelle de la classe si elle a été trouvée, null sinon
    */
   protected Method doGetMethod (final Method p_invocationMethod, final Class<?> p_class)
   {
      try
      {
         return p_class.getMethod(p_invocationMethod.getName(), p_invocationMethod.getParameterTypes());
      }
      catch (final Exception v_error)
      {
         return null;
      }
   }

   /**
    * Instance un nouveau mock lors de mocking en profondeur.
    * @param p_type
    *           le type à mocker
    * @return le mock
    */
   protected Object newMock (final Class<?> p_type)
   {
      return SpiMockito.mockBean(p_type, _returnSmartJavaBeanNulls);
   }

   /**
    * Dans quel cas doit on retourner un nouveau mock ? Implémentation : si le type est primitif ou final
    * @param p_type
    *           le type dont on souhaite savoir si on instancie un nouveau mock
    * @return true si on doit instancier un nouveau mock, false sinon
    */
   protected boolean shouldReturnSmartJavaBeanMock (final Class<?> p_type)
   {
      return !isPrimitive(p_type);
   }

   /**
    * Retourne true si le type est primitif ou final, false sinon.
    * @param p_type
    *           le type à tester
    * @return true si le type est primitif ou final, false sinon
    */
   protected boolean isPrimitive (final Class<?> p_type)
   {
      return p_type.isPrimitive() || Modifier.isFinal(p_type.getModifiers());
   }

   /**
    * Méthode appelée lors de l'instanciation d'une valeur à sauvegarder. Surcharges doivent appeler super.
    * @param p_invocation
    *           l'invocation
    * @param p_propertyName
    *           la propriété mise à jour
    * @param p_value
    *           la valeur
    */
   protected void doSetValue (final InvocationOnMock p_invocation, final String p_propertyName, final Object p_value)
   {
      _values.put(p_propertyName, p_value);
   }

}
