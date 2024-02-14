/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.dto;

import java.lang.reflect.Method;

/**
 * L'interface des énumérations des attributs dans les DTOs.
 * @author MINARM
 */
public interface AttributesNames_Itf
{
   /**
    * Retourne le nom de l'attribut.
    * @return le nom de l'attribut.
    */
   String getName ();

   /**
    * Retourne la description de l'attribut.
    * @return la description de l'attribut.
    */
   String getDescription ();

   /**
    * Retourne le type Java de l'attribut.
    * @return Class
    */
   Class<?> getType ();

   /**
    * Retourne true si l'attribut a une contrainte obligatoire (propriété "multiplicity" dans le modèle).
    * @return boolean
    */
   boolean isMandatory ();

   /**
    * Retourne la taille de l'attribut (-1 si pas de limite).
    * @return la taille de l'attribut
    */
   int getSize ();

   /**
    * @return le getter de l'attribut
    */
   Method getGetterMethod ();

   /**
    * @return le setter de l'attribut
    */
   Method getSetterMethod ();
}
