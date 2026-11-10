/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.entity;

/**
 * L'interface des énumérations des colonnes dans les entités.
 * @author MINARM
 */
public interface ColumnsNames_Itf
{
   /**
    * Retourne le nom logique de la colonne.
    * @return le nom logique de la colonne.
    */
   String getLogicalColumnName ();

   /**
    * Retourne le nom physique de la colonne.
    * @return le nom physique de la colonne.
    */
   String getPhysicalColumnName ();

   /**
    * Retourne le type Java de la colonne.
    * @return Class
    */
   Class<?> getTypeColumn ();

   /**
    * Retourne true si la colonne a une contrainte obligatoire (propriété "multiplicity" dans le modèle).
    * @return boolean
    */
   boolean isMandatory ();

   /**
    * Retourne la taille de la colonne (-1 si pas de limite).
    * @return la taille de la colonne
    */
   int getSize ();

   /**
    * Retourne true si la colonne est la clé primaire.
    * @return true si la colonne est la clé primaire.
    */
   boolean isId ();

   /**
    * Retourne le nom de la table de cette colonne.
    * @return le nom de la table de cette colonne
    */
   String getTableName ();

   /**
    * Retourne le nom physique complet de la colonne (avec TABLE. en préfixe).
    * @return le nom physique complet de la colonne.
    */
   String getCompletePhysicalName ();

   /**
    * @return le getter de la colonne
    */
   // Method getGetterMethod ();

   /**
    * @return le setter de la colonne
    */
   // Method getSetterMethod ();

}
