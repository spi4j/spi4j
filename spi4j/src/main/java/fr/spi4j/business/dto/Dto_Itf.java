/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.dto;

import java.io.Serializable;

import fr.spi4j.Identifiable_Itf;
import fr.spi4j.exception.Spi4jValidationException;

/**
 * Interface Data Transfert Object.
 * @author MINARM
 * @param <TypeId>
 *           Le type générique de la clé primaire.
 */
abstract public interface Dto_Itf<TypeId> extends Identifiable_Itf<TypeId>, Serializable
{
   /**
    * Obtenir la chaîne de caractères représentant l'instance.
    * @return La chaîne de caractères désirée.
    */
   @Override
   String toString ();

   /**
    * Méthode de validation du DTO.
    * @throws Spi4jValidationException
    *            si le DTO n'est pas valide
    */
   void validate () throws Spi4jValidationException;

}
