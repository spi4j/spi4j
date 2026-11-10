/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j;

/**
 * Interface d'un objet identifiable (DTO ou Entity en l'occurence).
 * @author MINARM
 * @param <TypeId>
 *           Le type générique de la clé primaire.
 */
public interface Identifiable_Itf<TypeId>
{
   /**
    * Obtenir la clé primaire.
    * @return La clé primaire du POJO.
    */
   TypeId getId ();

   /**
    * Affecter la clé primaire.
    * @param p_id
    *           (In)(*) La clé primaire est obligatoire.
    */
   void setId (TypeId p_id);
}
