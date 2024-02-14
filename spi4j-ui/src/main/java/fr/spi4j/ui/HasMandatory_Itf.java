/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

/**
 * Interface générique d'un champ qui a une contrainte de saisie obligatoire.
 * @author MINARM
 */
public interface HasMandatory_Itf
{
   /**
    * Retourne un booléen si la saisie est obligatoire pour ce widget.
    * @return boolean
    */
   boolean isMandatory ();

   /**
    * Définit le booléen selon que la saisie est obligatoire pour ce widget.
    * @param p_mandatory
    *           boolean
    */
   void setMandatory (boolean p_mandatory);
}
