/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dbpopulate;

/**
 * Regroupe les fonctionnalités spécifiques à un compteur.
 * @param <TypeElem>
 *           Le type du compteur (ex : 'Integer').
 * @author MINARM
 */
public interface Counter_Itf<TypeElem>
{
   /**
    * Obtenir la valeur du compteur.
    * @return La valeur.
    */
   TypeElem getNextValue ();

   /**
    * Obtenir la valeur du compteur avec une valeur bornée.
    * @param p_maxValue
    *           La valeur maximale.
    * @return La valeur.
    */
   TypeElem getNextValue (TypeElem p_maxValue);

   /**
    * Obtenir le max.
    * @return La valeur.
    */
   Integer getMax ();

}
