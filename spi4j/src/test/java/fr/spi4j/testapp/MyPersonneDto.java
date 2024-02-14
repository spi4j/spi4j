/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.spi4j.business.dto.DtoUtil;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jValidationException;

/**
 * DTO 'Personne'.
 * @author MINARM
 */
public class MyPersonneDto implements Dto_Itf<Long>
{
   /**
    * SerialUid.
    */
   private static final long serialVersionUID = -1;

   // ATTRIBUTS

   /** L'identifiant. */
   private Long _id;

   /** le nom de la personne. */
   private String _nom;

   /** le prénom de la personne. */
   private String _prenom;

   /** . */
   private Boolean _civil;

   /** . */
   private Date _dateNaissance;

   /** Le salaire de la personne. */
   private Double _salaire;

   /** La FK sur le Type 'Grade'. */
   private Long _grade_id;

   /** La valeur de la version. */
   @SuppressWarnings("all")
   private Object _versionValue;

   // METHODES

   @Override
   public Long getId ()
   {
      return _id;
   }

   @Override
   public void setId (final Long p_id)
   {
      _id = p_id;
   }

   /**
    * Obtenir le nom de la personne.
    * @return le nom de la personne.
    */
   public String getNom ()
   {
      return _nom;
   }

   /**
    * Affecter le nom de la personne.
    * @param p_nom
    *           (In)(*) le nom de la personne.
    */
   public void setNom (final String p_nom)
   {
      _nom = p_nom;
   }

   /**
    * Obtenir le prénom de la personne.
    * @return le prénom de la personne.
    */
   public String getPrenom ()
   {
      return _prenom;
   }

   /**
    * Affecter le prénom de la personne.
    * @param p_prenom
    *           (In) le prénom de la personne.
    */
   public void setPrenom (final String p_prenom)
   {
      _prenom = p_prenom;
   }

   /**
    * Obtenir civil.
    * @return civil.
    */
   public Boolean getCivil ()
   {
      return _civil;
   }

   /**
    * Affecter civil.
    * @param p_civil
    *           (In)(*) civil.
    */
   public void setCivil (final Boolean p_civil)
   {
      _civil = p_civil;
   }

   /**
    * Obtenir dateNaissance.
    * @return dateNaissance.
    */
   public Date getDateNaissance ()
   {
      return _dateNaissance;
   }

   /**
    * Affecter dateNaissance.
    * @param p_dateNaissance
    *           (In) dateNaissance.
    */
   public void setDateNaissance (final Date p_dateNaissance)
   {
      _dateNaissance = p_dateNaissance;
   }

   /**
    * Obtenir age.
    * @return age.
    */
   public Integer getAge ()
   {
      final Integer v_result;
      if (getDateNaissance() == null)
      {
         v_result = null;
      }
      else
      {
         v_result = (int) ((System.currentTimeMillis() - getDateNaissance().getTime()) / (365L * 24 * 60 * 60 * 1000));
      }
      return v_result;
   }

   /**
    * Obtenir infosDetaillees.
    * @return infosDetaillees.
    */
   public String getInfosDetaillees ()
   {
      // génération par défaut avec return null;
      return null;
   }

   /**
    * Obtenir le salaire de la personne.
    * @return Le salaire de la personne.
    */
   public Double getSalaire ()
   {
      return _salaire;
   }

   /**
    * Affecter le salaire de la personne.
    * @param p_salaire
    *           (In) Le salaire de la personne.
    */
   public void setSalaire (final Double p_salaire)
   {
      _salaire = p_salaire;
   }

   /**
    * Obtenir la liste de type 'Personne' associee à  l'instance de 'Personne' courante.
    * @return La liste desiree.
    */
   public List<MyPersonneDto> getNeuveux ()
   {
      // méthode utilisée par le test unitaire FetchingStrategyFetcher_Test
      return Collections.emptyList();
   }

   /**
    * Obtenir l'id du type 'Personne' associe à  l'instance de 'Personne' courante.
    * @return L'id du dto 'Personne'.
    */
   public Long getAncetreId ()
   {
      // méthode utilisée par le test unitaire FetchingStrategyFetcher_Test
      return -1L;
   }

   /**
    * Obtenir le type 'Personne' associe à  l'instance de 'Personne' courante.
    * @return Le dto 'Personne'.
    */
   public MyPersonneDto getAncetre ()
   {
      // méthode utilisée par le test unitaire FetchingStrategyFetcher_Test
      final MyPersonneDto v_result = new MyPersonneDto();
      v_result.setId(-1L);
      return v_result;
   }

   /**
    * Affecter le type 'Personne' associe à  l'instance de 'Personne' courante.
    * @param p_ancetre
    *           Le dto 'Personne'.
    */
   public void setAncetre (final MyPersonneDto p_ancetre)
   {
      // méthode utilisée par le test unitaire FetchingStrategyFetcher_Test
   }

   /**
    * Obtenir grade.
    * @return grade.
    */
   public Long getGradeId ()
   {
      return _grade_id;
   }

   /**
    * Affecter grade.
    * @param p_grade_id
    *           (In) grade.
    */
   public void setGradeId (final Long p_grade_id)
   {
      _grade_id = p_grade_id;
   }

   @Override
   public void validate () throws Spi4jValidationException
   {
      List<String> v_champsInvalides = null;
      v_champsInvalides = DtoUtil.checkMandatoryField("nom", _nom, v_champsInvalides);
      v_champsInvalides = DtoUtil.checkMandatoryField("civil", _civil, v_champsInvalides);
      // si la liste des champs invalides n'est pas nulle, elle ne peut pas être vide à cet endroit
      if (v_champsInvalides != null)
      {
         throw new Spi4jValidationException(this, v_champsInvalides.toArray(new String[v_champsInvalides.size()]));
      }
   }

   @Override
   public String toString ()
   {
      return String.valueOf(getNom()) + ' ' + getPrenom();
   }
}
