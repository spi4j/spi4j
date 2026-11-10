/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

/**
 * Interface générique qui pourra être héritée et mockable pour un widget représentant une valeur.
 * @param <TypeValue>
 *           Type de la valeur
 * @author MINARM
 */
public interface HasValue_Itf<TypeValue>
{
   /**
    * Retourne la valeur contenue dans le widget.
    * @return TypeValue
    */
   TypeValue getValue ();

   /**
    * Définit la valeur contenue dans le widget.
    * @param p_value
    *           TypeValue
    */
   void setValue (TypeValue p_value);
}
