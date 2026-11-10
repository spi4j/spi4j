/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import java.util.Date;

import fr.spi4j.exception.Spi4jValidationException;

/**
 * Entité du type Personne.
 * @author MINARM
 */
public class MyPersonneEntity implements MyPersonneEntity_Itf
{
   // CONSTANTES

   /**
    * SerialUid.
    */
   private static final long serialVersionUID = -1;

   // ATTRIBUTS

   /** Id. */
   private Long _Personne_id;

   /** le nom de la personne. */
   private String _nom;

   /** le prénom de la personne. */
   private String _prenom;

   /** indicateur si la personne est civile ou non. */
   private Boolean _civil;

   /** la date de naissance de la personne. */
   private Date _dateNaissance;

   /** salaire. */
   private Double _salaire;

   /** vesion. */
   private Date _version;

   /** le grade d'une personne (facultatif). */
   private Long _grade_id;

   // METHODES

   @Override
   public Long getId ()
   {
      return _Personne_id;
   }

   @Override
   public void setId (final Long p_id)
   {
      _Personne_id = p_id;
   }

   @Override
   public String getNom ()
   {
      return _nom;
   }

   @Override
   public void setNom (final String p_nom)
   {
      _nom = p_nom;
   }

   @Override
   public String getPrenom ()
   {
      return _prenom;
   }

   @Override
   public void setPrenom (final String p_prenom)
   {
      _prenom = p_prenom;
   }

   @Override
   public Boolean getCivil ()
   {
      return _civil;
   }

   @Override
   public void setCivil (final Boolean p_civil)
   {
      _civil = p_civil;
   }

   @Override
   public Date getDateNaissance ()
   {
      return _dateNaissance;
   }

   @Override
   public void setDateNaissance (final Date p_dateNaissance)
   {
      _dateNaissance = p_dateNaissance;
   }

   @Override
   public Double getSalaire ()
   {
      return _salaire;
   }

   @Override
   public void setSalaire (final Double p_salaire)
   {
      _salaire = p_salaire;
   }

   @Override
   public Date getVersion ()
   {
      return _version;
   }

   @Override
   public void setVersion (final Date p_version)
   {
      _version = p_version;
   }

   @Override
   public Long getGradeId ()
   {
      return _grade_id;
   }

   @Override
   public void setGradeId (final Long p_grade_id)
   {
      _grade_id = p_grade_id;
   }

   @Override
   public String toString ()
   {
      return super.toString() + " : " + _Personne_id + ", " + _nom + ", " + _prenom + ", " + _civil + ", "
               + _dateNaissance + ", " + _salaire + ", " + _version + ", " + _grade_id;
   }

   @Override
   public void validate () throws Spi4jValidationException
   {
      // RAS
   }

}
