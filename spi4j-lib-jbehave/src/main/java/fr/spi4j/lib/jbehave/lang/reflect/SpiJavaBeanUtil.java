/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.lang.reflect;

import java.lang.reflect.Method;

/**
 * Classe utilitaire pour gérer le mock des JavaBeans
 * @author MINARM
 */
public final class SpiJavaBeanUtil
{
   /**
    * Constructeur privé.
    */
   private SpiJavaBeanUtil ()
   {
      super();
   }

   /**
    * Récupère la propriété associée au getter ou au setter.
    * @param p_methodName
    *           le nom de la méthode (getter ou setter)
    * @return le nom de la propriété associée à la méthode
    */
   protected static String getPropertyName (final String p_methodName)
   {
      String v_propertyName = p_methodName;

      if (v_propertyName.startsWith("get") || v_propertyName.startsWith("set"))
      {
         v_propertyName = v_propertyName.substring(3);
      }
      else if (v_propertyName.startsWith("is"))
      {
         v_propertyName = v_propertyName.substring(2);
      }

      if (v_propertyName.charAt(0) == '_')
      {
         v_propertyName = v_propertyName.substring(1);
      }

      return v_propertyName;
   }

   /**
    * Calcule le nom de propriété à partir d'une méthode getter ou setter.
    * @param p_method
    *           la méthode
    * @return le nom de propriété associée à la méthode
    */
   public static String getPropertyName (final Method p_method)
   {
      return getPropertyName(p_method.getName());
   }

}
