/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.entity;

import java.lang.reflect.Method;

/**
 * Classe helper interne pour centraliser les noms des getters et setters sur les columns des Entitys.
 * @author MINARM
 */
public class EntityAttributeHelper
{

   private static EntityAttributeHelper instance = new EntityAttributeHelper();

   /**
    * Retourne l'instance de EntityAttributeHelper.
    * @return l'instance de EntityAttributeHelper
    */
   public static EntityAttributeHelper getInstance ()
   {
      return instance;
   }

   /**
    * Affecte l'instance de EntityAttributeHelper.
    * @param p_instance
    *           l'instance de EntityAttributeHelper
    */
   public static void setInstance (final EntityAttributeHelper p_instance)
   {
      EntityAttributeHelper.instance = p_instance;
   }

   /**
    * Retourne le getter de la classe d'un Entity pour un attribut ou null si non trouvé.
    * @param p_entityClass
    *           Classe du Entity
    * @param p_column
    *           ColumnNames_Itf
    * @return Method (peut être null)
    */
   public Method getGetterMethodForColumn (final Class<? extends Entity_Itf<?>> p_entityClass,
            final ColumnsNames_Itf p_column)
   {
      Method v_methodReturn = null;

      if (p_column.isId())
      {
         try
         {
            v_methodReturn = p_entityClass.getClass().getMethod("getId");
         }
         catch (final NoSuchMethodException v_ex)
         {
            // v_methodReturn = null;
         }
      }
      else
      {
         final String v_nomGetterCamelCase = "get" + Character.toUpperCase(p_column.getLogicalColumnName().charAt(0))
                  + p_column.getLogicalColumnName().substring(1);

         final String v_nomGetterUnderScore = "get_" + p_column.getLogicalColumnName();

         final Method[] v_methods = p_entityClass.getClass().getDeclaredMethods();
         for (final Method v_method : v_methods)
         {
            if (v_method.getName().equals(v_nomGetterCamelCase) || v_method.getName().equals(v_nomGetterUnderScore))
            {
               v_methodReturn = v_method;
               break;
            }
         }
      }

      return v_methodReturn;
   }

   /**
    * Retourne le getter de la classe d'un Entity pour un attribut ou null si non trouvé.
    * @param p_entityClass
    *           Classe du Entity
    * @param p_column
    *           ColumnNames_Itf
    * @return Method (peut être null)
    */
   public Method getGetterMethodForColumn (final Entity_Itf<?> p_entityClass, final ColumnsNames_Itf p_column)
   {
      Method v_methodReturn = null;

      if (p_column.isId())
      {
         try
         {
            v_methodReturn = p_entityClass.getClass().getMethod("getId");
         }
         catch (final NoSuchMethodException v_ex)
         {
            // v_methodReturn = null;
         }
      }
      else
      {
         final String v_nomGetterCamelCase = "get" + Character.toUpperCase(p_column.getLogicalColumnName().charAt(0))
                  + p_column.getLogicalColumnName().substring(1);

         final String v_nomGetterUnderScore = "get_" + p_column.getLogicalColumnName();

         final Method[] v_methods = p_entityClass.getClass().getDeclaredMethods();
         for (final Method v_method : v_methods)
         {
            if (v_method.getName().equals(v_nomGetterCamelCase) || v_method.getName().equals(v_nomGetterUnderScore))
            {
               v_methodReturn = v_method;
               break;
            }
         }
      }

      return v_methodReturn;
   }

   /**
    * Retourne le setter de la classe d'une entity pour un attribut
    * @param p_entityClass
    *           Classe du Entity
    * @param p_column
    *           ColumnNames_Itf
    * @return Method (peut être null)
    */
   public Method getSetterMethodForColumn (final Class<? extends Entity_Itf<?>> p_entityClass,
            final ColumnsNames_Itf p_column)
   {
      Method v_methodReturn = null;

      if (p_column.isId())
      {
         try
         {
            v_methodReturn = p_entityClass.getClass().getMethod("setId", long.class);
         }
         catch (final NoSuchMethodException v_ex)
         {
            // v_methodReturn = null;
         }
      }
      else
      {
         final String v_nomSetterCamelCase = "set" + Character.toUpperCase(p_column.getLogicalColumnName().charAt(0))
                  + p_column.getLogicalColumnName().substring(1);

         final String v_nomSetterUnderScore = "set_" + p_column.getLogicalColumnName();

         final Method[] v_methods = p_entityClass.getClass().getDeclaredMethods();
         for (final Method v_method : v_methods)
         {
            if (v_method.getName().equals(v_nomSetterCamelCase) || v_method.getName().equals(v_nomSetterUnderScore))
            {
               v_methodReturn = v_method;
               break;
            }
         }
      }

      return v_methodReturn;
   }

   /**
    * Retourne le setter de la classe d'une entity pour un attribut
    * @param p_entityClass
    *           Classe du Entity
    * @param p_column
    *           ColumnNames_Itf
    * @return Method (peut être null)
    */
   public Method getSetterMethodForColumn (final Entity_Itf<?> p_entityClass, final ColumnsNames_Itf p_column)
   {
      Method v_methodReturn = null;

      if (p_column.isId())
      {
         try
         {
            v_methodReturn = p_entityClass.getClass().getMethod("setId", long.class);
         }
         catch (final NoSuchMethodException v_ex)
         {
            // v_methodReturn = null;
         }
      }
      else
      {
         final String v_nomSetterCamelCase = "set" + Character.toUpperCase(p_column.getLogicalColumnName().charAt(0))
                  + p_column.getLogicalColumnName().substring(1);

         final String v_nomSetterUnderScore = "set_" + p_column.getLogicalColumnName();

         final Method[] v_methods = p_entityClass.getClass().getDeclaredMethods();
         for (final Method v_method : v_methods)
         {
            if (v_method.getName().equals(v_nomSetterCamelCase) || v_method.getName().equals(v_nomSetterUnderScore))
            {
               v_methodReturn = v_method;
               break;
            }
         }
      }

      return v_methodReturn;
   }


   /**
    * Retourne le getter de la classe d'un Entity pour un attribut ou null si non trouvé.
    * @param p_entityClass
    *           Classe du Entity
    * @param p_columName
    *           String
    * @return Method (peut être null)
    */
   public Method getGetterMethodForAttribute (final Class<? extends Entity_Itf<?>> p_entityClass,
            final String p_columName)
   {
      try
      {
         // ceci suppose une règle nommage du getter en CamelCase
         return p_entityClass.getClass().getMethod(
                  "get" + Character.toUpperCase(p_columName.charAt(0)) + p_columName.substring(1),
                  (Class<?>[]) null);

      }
      catch (final NoSuchMethodException v_ex)
      {
         // Si la méthode recherchée n'est pas trouvée cela peut être normal : on ne propage pas l'exception et on cherche avec la règle nommage du getter propre au SID : "get_xxx"
         try
         {
            return p_entityClass.getClass().getMethod("get_" + p_columName, (Class<?>[]) null);
         }
         catch (final NoSuchMethodException v_e2)
         {
            // Si la méthode recherchée n'est pas trouvée cela peut être normal : on ne propage pas l'exception et on retourne null
            return null;
         }
      }
   }

   /**
    * Retourne le getter de la classe d'une Entity pour un attribut ou null si non trouvé.
    * @param p_entityClass
    *           Classe du Entity
    * @param p_columName
    *           String
    * @return Method (peut être null)
    */
   public Method getGetterMethodForAttributeId (final Class<? extends Entity_Itf<?>> p_entityClass,
            final ColumnsNames_Itf p_columName)
   {
      Method v_result = getGetterMethodForAttribute(p_entityClass, p_columName.getLogicalColumnName() + "Id");
      if (v_result == null)
      {
         v_result = getGetterMethodForAttribute(p_entityClass, p_columName.getLogicalColumnName() + "_id");
      }
      return v_result;
   }

   /**
    * Retourne le setter de la classe d'une entity pour un attribut
    * @param p_entityClass
    *           Classe du Entity
    * @param p_columName
    *           String
    * @param p_columnType
    *           Class
    * @return Method (ne peut pas être null)
    */
   public Method getSetterMethodForAttribute (final Class<? extends Entity_Itf<?>> p_entityClass,
            final String p_columName, final Class<?> p_columnType)
   {
      try
      {
         final String v_nomMethod = "set" + Character.toUpperCase(p_columName.charAt(0)) + p_columName.substring(1);
         return p_entityClass.getClass().getMethod(
                  v_nomMethod,
                  p_columnType);
      }
      catch (final NoSuchMethodException v_ex)
      {
         // Si la méthode recherchée n'est pas trouvée cela peut être normal : on ne propage pas l'exception et on cherche avec la règle de nommage propre au SID
         try
         {
            return p_entityClass.getClass().getMethod("set_" + p_columName, p_columnType);
         }
         catch (final NoSuchMethodException v_e2)
         {
            // Si la méthode recherchée n'est pas trouvée cela peut être normal : on ne propage pas l'exception et on retourne null
            return null;
         }
      }
   }
}
