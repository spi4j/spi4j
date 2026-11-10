/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.entity;

import java.io.Serializable;

import fr.spi4j.Identifiable_Itf;
import fr.spi4j.exception.Spi4jValidationException;

/**
 * Interface d'entité persistante.
 * @author MINARM
 * @param <TypeId>
 *           Le type générique de la clé primaire.
 */
public interface Entity_Itf<TypeId> extends Identifiable_Itf<TypeId>, Serializable
{
   /**
    * Obtenir la chaîne de caractères représentant l'instance.
    * @return La chaîne de caractères désirée.
    */
   @Override
   String toString ();

   /**
    * Méthode de validation de l'entity.
    * @throws Spi4jValidationException
    *            si l'entity n'est pas valide
    */
   void validate () throws Spi4jValidationException;
}
