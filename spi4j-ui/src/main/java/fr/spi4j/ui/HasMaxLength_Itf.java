/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

/**
 * Interface générique d'un champ qui a une contrainte de taille.
 * @author MINARM
 */
public interface HasMaxLength_Itf
{
   /**
    * Retourne la taille maximum dans le widget.
    * @return int
    */
   int getMaxLength ();

   /**
    * Définit la valeur contenue dans le widget.
    * @param p_maxLength
    *           int
    */
   void setMaxLength (int p_maxLength);
}
