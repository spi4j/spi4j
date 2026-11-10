/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.dto;

import java.lang.reflect.Method;

/**
 * Classe helper interne pour centraliser les noms des getters et setters sur les attributs des DTOs.
 * @author MINARM
 */
public class DtoAttributeHelper
{

   private static DtoAttributeHelper instance = new DtoAttributeHelper();

   /**
    * Retourne l'instance de DtoAttributeHelper.
    * @return l'instance de DtoAttributeHelper
    */
   public static DtoAttributeHelper getInstance ()
   {
      return instance;
   }

   /**
    * Affecte l'instance de DtoAttributeHelper.
    * @param p_instance
    *           l'instance de DtoAttributeHelper
    */
   public static void setInstance (final DtoAttributeHelper p_instance)
   {
      DtoAttributeHelper.instance = p_instance;
   }

   /**
    * Retourne le getter de la classe d'un DTO pour un attribut ou null si non trouvé.
    * @param p_dtoClass
    *           Classe du DTO
    * @param p_attributeName
    *           String
    * @return Method (peut être null)
    */
   public Method getGetterMethodForAttribute (final Class<? extends Dto_Itf<?>> p_dtoClass, final String p_attributeName)
   {
      try
      {
         // ceci suppose une règle nommage du getter en CamelCase
         return p_dtoClass.getMethod(
                  "get" + Character.toUpperCase(p_attributeName.charAt(0)) + p_attributeName.substring(1),
                  (Class<?>[]) null);

      }
      catch (final NoSuchMethodException v_ex)
      {
         // Si la méthode recherchée n'est pas trouvée cela peut être normal : on ne propage pas l'exception et on cherche avec la règle nommage du getter propre au SID : "get_xxx"
         try
         {
            return p_dtoClass.getMethod("get_" + p_attributeName, (Class<?>[]) null);
         }
         catch (final NoSuchMethodException v_e2)
         {
            // Si la méthode recherchée n'est pas trouvée cela peut être normal : on ne propage pas l'exception et on retourne null
            return null;
         }
      }
   }

   /**
    * Retourne le getter de la classe d'un DTO pour un attribut ou null si non trouvé.
    * @param p_dtoClass
    *           Classe du DTO
    * @param p_attributeName
    *           String
    * @return Method (peut être null)
    */
   public Method getGetterMethodForAttributeId (final Class<? extends Dto_Itf<?>> p_dtoClass,
            final AttributesNames_Itf p_attributeName)
   {
      Method v_result = getGetterMethodForAttribute(p_dtoClass, p_attributeName.getName() + "Id");
      if (v_result == null)
      {
         v_result = getGetterMethodForAttribute(p_dtoClass, p_attributeName.getName() + "_id");
      }
      return v_result;
   }

   /**
    * Retourne le setter de la classe d'un DTO pour un attribut
    * @param p_dtoClass
    *           Classe du DTO
    * @param p_attributeName
    *           String
    * @param p_attributeType
    *           Class
    * @return Method (ne peut pas être null)
    */
   public Method getSetterMethodForAttribute (final Class<? extends Dto_Itf<?>> p_dtoClass,
            final String p_attributeName, final Class<?> p_attributeType)
   {
      try
      {
         return p_dtoClass.getMethod(
                  "set" + Character.toUpperCase(p_attributeName.charAt(0)) + p_attributeName.substring(1),
                  p_attributeType);
      }
      catch (final NoSuchMethodException v_ex)
      {
         // Si la méthode recherchée n'est pas trouvée cela peut être normal : on ne propage pas l'exception et on cherche avec la règle de nommage propre au SID
         try
         {
            return p_dtoClass.getMethod("set_" + p_attributeName, p_attributeType);
         }
         catch (final NoSuchMethodException v_e2)
         {
            // Si la méthode recherchée n'est pas trouvée cela peut être normal : on ne propage pas l'exception et on retourne null
            return null;
         }
      }
   }
}
