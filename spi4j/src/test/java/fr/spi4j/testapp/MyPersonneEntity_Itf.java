/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import java.util.Date;

import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * L'interface définissant le contrat de persistance pour le type Personne.
 * @author MINARM
 */
public interface MyPersonneEntity_Itf extends Entity_Itf<Long>
{
   /**
    * Obtenir le nom de la personne.
    * @return le nom de la personne.
    */
   public abstract String getNom ();

   /**
    * Affecter le nom de la personne.
    * @param p_nom
    *           (In)(*) le nom de la personne.
    */
   public abstract void setNom (final String p_nom);

   /**
    * Obtenir le prénom de la personne.
    * @return le prénom de la personne.
    */
   public abstract String getPrenom ();

   /**
    * Affecter le prénom de la personne.
    * @param p_prenom
    *           (In) le prénom de la personne.
    */
   public abstract void setPrenom (final String p_prenom);

   /**
    * Obtenir indicateur si la personne est civile ou non.
    * @return indicateur si la personne est civile ou non.
    */
   public abstract Boolean getCivil ();

   /**
    * Affecter indicateur si la personne est civile ou non.
    * @param p_civil
    *           (In)(*) indicateur si la personne est civile ou non.
    */
   public abstract void setCivil (final Boolean p_civil);

   /**
    * Obtenir la date de naissance de la personne.
    * @return la date de naissance de la personne.
    */
   public abstract Date getDateNaissance ();

   /**
    * Affecter la date de naissance de la personne.
    * @param p_dateNaissance
    *           (In) la date de naissance de la personne.
    */
   public abstract void setDateNaissance (final Date p_dateNaissance);

   /**
    * Obtenir salaire.
    * @return salaire.
    */
   public abstract Double getSalaire ();

   /**
    * Affecter salaire.
    * @param p_salaire
    *           (In) salaire.
    */
   public abstract void setSalaire (final Double p_salaire);

   /**
    * Obtenir version.
    * @return version.
    */
   public abstract Date getVersion ();

   /**
    * Affecter version.
    * @param p_version
    *           (In) version.
    */
   public abstract void setVersion (final Date p_version);

   /**
    * Obtenir le grade d'une personne (facultatif).
    * @return le grade d'une personne (facultatif).
    */
   public abstract Long getGradeId ();

   /**
    * Affecter le grade d'une personne (facultatif).
    * @param p_grade_id
    *           (In) le grade d'une personne (facultatif).
    */
   public abstract void setGradeId (final Long p_grade_id);

}
