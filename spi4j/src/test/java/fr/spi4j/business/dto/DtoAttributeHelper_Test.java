/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Method;
import java.sql.Date;

import org.junit.jupiter.api.Test;

import fr.spi4j.business.dto.DtoUtil_Test.TestAttributes_Enum;
import fr.spi4j.business.dto.DtoUtil_Test.TestDto;

/**
 * Classe de test de DtoUtil.
 * @author MINARM
 */
public class DtoAttributeHelper_Test
{
   /**
    * Test findInCollectionById.
    */
   @SuppressWarnings("unchecked")
   @Test
   public void test ()
   {
      assertNotNull(DtoAttributeHelper.getInstance().getGetterMethodForAttribute(TestDto.class,
               TestAttributes_Enum.nom.getName()));
      assertNotNull(DtoAttributeHelper.getInstance().getSetterMethodForAttribute(TestDto.class,
               TestAttributes_Enum.nom.getName(), TestAttributes_Enum.nom.getType()));
      assertNotNull(DtoAttributeHelper.getInstance().getGetterMethodForAttributeId(TestDto.class,
               TestAttributes_Enum.firstChild));

      // tests inutiles grâce au typage des attributs en enumérations
      assertNull(DtoAttributeHelper.getInstance().getGetterMethodForAttribute(TestDto.class, "n'existe pas"));
      assertNull(DtoAttributeHelper.getInstance().getSetterMethodForAttribute(TestDto.class, "n'existe pas",
               String.class));

      // vérifie que DtoAttributeHelper comprend aussi le nommage des getters et setters en CamelCase
      final Class<?> v_class = Date.class;
      assertNotNull(DtoAttributeHelper.getInstance().getGetterMethodForAttribute((Class<? extends Dto_Itf<?>>) v_class,
               Date_Enum.time.getName()));
      assertNotNull(DtoAttributeHelper.getInstance().getSetterMethodForAttribute((Class<? extends Dto_Itf<?>>) v_class,
               Date_Enum.time.getName(), Date_Enum.time.getType()));
   }
}

/**
 * enum pour cas de test avec le type Date
 * @author MINARM
 */
enum Date_Enum implements AttributesNames_Itf
{
   /** time */
   time("time", "", Long.TYPE, true, -1);

   /** Le nom de l'attribut. */
   private final String _name;

   /** La description de l'attribut. */
   private final String _description;

   /** Le type associé à l'attribut. */
   private final Class<?> _type;

   /** Est-ce que la saisie de la valeur est obligatoire pour cet attribut ? */
   private final boolean _mandatory;

   /** La taille de l'attribut. */
   private final int _size;

   private Method _getterMethod;

   private Method _setterMethod;

   /**
    * Constructeur.
    * @param p_name
    *           (In)(*) Le nom de l'attribut.
    * @param p_description
    *           (In)(*) La description de l'attribut.
    * @param p_ClassType
    *           (In)(*) Le type de l'attribut.
    * @param p_mandatory
    *           (In)(*) Est-ce que la saisie de la valeur est obligatoire pour cette colonne?
    * @param p_size
    *           (In)(*) La taille de la colonne
    */
   private Date_Enum (final String p_name, final String p_description, final Class<?> p_ClassType,
            final boolean p_mandatory, final int p_size)
   {
      _name = p_name;
      _description = p_description;
      _type = p_ClassType;
      _mandatory = p_mandatory;
      _size = p_size;
   }

   @Override
   public String getName ()
   {
      return _name;
   }

   @Override
   public String getDescription ()
   {
      return _description;
   }

   @Override
   public Class<?> getType ()
   {
      return _type;
   }

   @Override
   public boolean isMandatory ()
   {
      return _mandatory;
   }

   @Override
   public int getSize ()
   {
      return _size;
   }

   @Override
   public String toString ()
   {
      return _description;
   }

   @Override
   public Method getGetterMethod ()
   {
      if (_getterMethod == null)
      {
         try
         {
            _getterMethod = Date.class.getMethod(
                     "get" + Character.toUpperCase(getName().charAt(0)) + getName().substring(1), (Class<?>[]) null);
         }
         catch (final Exception v_e)
         {
            return null;
         }
      }
      return _getterMethod;
   }

   @Override
   public Method getSetterMethod ()
   {
      if (_setterMethod == null)
      {
         try
         {
            _setterMethod = Date.class.getMethod(
                     "set" + Character.toUpperCase(getName().charAt(0)) + getName().substring(1), (Class<?>[]) null);
         }
         catch (final Exception v_e)
         {
            return null;
         }
      }
      return _getterMethod;
   }
}
