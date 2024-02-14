package fr.spi4j.docreport.annuaire.dto;

// Start of user code for imports

import java.io.Serializable;
import java.util.Date;
import java.util.List;

// End of user code

/**
 * DTO 'Personne'.
 * @author MINARM
 */
public class PersonneDto
{
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

   /** L'instance de 'Grade' associee a  l'instance de 'Personne' courante. */
   private GradeDto _grade;

   /** La liste de type 'Competence' associee a  l'instance de 'Personne' courante. */
   private List<CompetenceDto> _tab_competences;

   /** La valeur de la version. */
   @SuppressWarnings("all")
   private Serializable _versionValue;

   // METHODES

   /**
    * Constructeur sans paramètre du dto 'Personne'.
    */
   public PersonneDto ()
   {
      super();
   }

   /**
    * Constructeur complet du dto 'Personne'.
    * @param p_id
    *           (In)(*) L'identifiant de personne.
    * @param p_nom
    *           (In)(*) le nom de la personne.
    * @param p_prenom
    *           (In) le prénom de la personne.
    * @param p_civil
    *           (In)(*) civil.
    * @param p_dateNaissance
    *           (In) dateNaissance.
    * @param p_salaire
    *           (In) Le salaire de la personne.
    * @param p_grade_id
    *           (In) grade.
    */
   public PersonneDto (final Long p_id, final String p_nom, final String p_prenom, final Boolean p_civil,
            final Date p_dateNaissance, final Double p_salaire, final Long p_grade_id)
   {
      super();
      reset_PersonneDto(p_id, p_nom, p_prenom, p_civil, p_dateNaissance, p_salaire, p_grade_id);
   }

   /**
    * Constructeur complet du dto 'Personne'.
    * @param p_id
    *           (In)(*) L'identifiant de personne.
    * @param p_nom
    *           (In)(*) le nom de la personne.
    * @param p_prenom
    *           (In) le prénom de la personne.
    * @param p_civil
    *           (In)(*) civil.
    * @param p_dateNaissance
    *           (In) dateNaissance.
    * @param p_salaire
    *           (In) Le salaire de la personne.
    * @param p_grade
    *           (In) grade.
    */
   public PersonneDto (final Long p_id, final String p_nom, final String p_prenom, final Boolean p_civil,
            final Date p_dateNaissance, final Double p_salaire, final GradeDto p_grade)
   {
      super();
      reset_PersonneDto(p_id, p_nom, p_prenom, p_civil, p_dateNaissance, p_salaire, p_grade);
   }

   /**
    * @return Long
    */
   public Long getId ()
   {
      return _id;
   }

   /**
    * @param p_id
    *           Long
    */
   public void setId (final Long p_id)
   {
      _id = p_id;
   }

   /**
    * Obtenir le nom de la personne.
    * @return le nom de la personne.
    */
   public String get_nom ()
   {
      return _nom;
   }

   /**
    * Affecter le nom de la personne.
    * @param p_nom
    *           (In)(*) le nom de la personne.
    */
   public void set_nom (final String p_nom)
   {
      _nom = p_nom;
   }

   /**
    * Obtenir le prénom de la personne.
    * @return le prénom de la personne.
    */
   public String get_prenom ()
   {
      return _prenom;
   }

   /**
    * Affecter le prénom de la personne.
    * @param p_prenom
    *           (In) le prénom de la personne.
    */
   public void set_prenom (final String p_prenom)
   {
      _prenom = p_prenom;
   }

   /**
    * Obtenir civil.
    * @return civil.
    */
   public Boolean get_civil ()
   {
      return _civil;
   }

   /**
    * Affecter civil.
    * @param p_civil
    *           (In)(*) civil.
    */
   public void set_civil (final Boolean p_civil)
   {
      _civil = p_civil;
   }

   /**
    * Obtenir dateNaissance.
    * @return dateNaissance.
    */
   public Date get_dateNaissance ()
   {
      return _dateNaissance;
   }

   /**
    * Affecter dateNaissance.
    * @param p_dateNaissance
    *           (In) dateNaissance.
    */
   public void set_dateNaissance (final Date p_dateNaissance)
   {
      _dateNaissance = p_dateNaissance;
   }

   /**
    * Obtenir age.
    * @return age.
    */
   public Integer get_age ()
   {
      // Start of user code _age
      final Integer v_result;
      if (get_dateNaissance() == null)
      {
         v_result = null;
      }
      else
      {
         v_result = (int) ((System.currentTimeMillis() - get_dateNaissance().getTime()) / (365L * 24 * 60 * 60 * 1000));
      }
      return v_result;
      // End of user code
   }

   /**
    * Obtenir infosDetaillees.
    * @return infosDetaillees.
    */
   public String get_infosDetaillees ()
   {
      // Start of user code _infosDetaillees
      // génération par défaut avec return null;
      return null;
      // End of user code
   }

   /**
    * Obtenir le salaire de la personne.
    * @return Le salaire de la personne.
    */
   public Double get_salaire ()
   {
      return _salaire;
   }

   /**
    * Affecter le salaire de la personne.
    * @param p_salaire
    *           (In) Le salaire de la personne.
    */
   public void set_salaire (final Double p_salaire)
   {
      _salaire = p_salaire;
   }

   /**
    * Obtenir grade.
    * @return grade.
    */
   public Long get_grade_id ()
   {
      if (_grade != null)
      {
         _grade_id = _grade.getId();
      }
      return _grade_id;
   }

   /**
    * Affecter grade.
    * @param p_grade_id
    *           (In) grade.
    */
   public void set_grade_id (final Long p_grade_id)
   {
      _grade_id = p_grade_id;
      _grade = null;
   }

   /**
    * Obtenir grade.
    * @return grade.
    */
   public GradeDto get_grade ()
   {
      return _grade;
   }

   /**
    * Affecter grade.
    * @param p_grade
    *           (In) grade.
    */
   public void set_grade (final GradeDto p_grade)
   {
      if (p_grade == null)
      {
         set_grade_id(null);
      }
      else
      {
         set_grade_id(p_grade.getId());
      }
      _grade = p_grade;
   }

   /**
    * Obtenir la liste de 'Competence' : Competences.
    * @return Competences.
    */
   public List<CompetenceDto> get_tab_competences ()
   {
      return _tab_competences;
   }

   /**
    * Affecter la liste de 'Competence' : Competences.
    * @param p_tab_competence
    *           (In) Competences.
    */
   public void set_tab_competences (final List<CompetenceDto> p_tab_competence)
   {
      _tab_competences = p_tab_competence;
   }

   /**
    * Recycler le dto 'Personne'.
    * @param p_id
    *           (In)(*) L'identifiant de personne.
    * @param p_nom
    *           (In)(*) le nom de la personne.
    * @param p_prenom
    *           (In) le prénom de la personne.
    * @param p_civil
    *           (In)(*) civil.
    * @param p_dateNaissance
    *           (In) dateNaissance.
    * @param p_salaire
    *           (In) Le salaire de la personne.
    * @param p_grade_id
    *           (In) grade.
    */
   public void reset_PersonneDto (final Long p_id, final String p_nom, final String p_prenom, final Boolean p_civil,
            final Date p_dateNaissance, final Double p_salaire, final Long p_grade_id)
   {
      setId(p_id);
      set_nom(p_nom);
      set_prenom(p_prenom);
      set_civil(p_civil);
      set_dateNaissance(p_dateNaissance);
      set_salaire(p_salaire);
      set_grade_id(p_grade_id);
      _tab_competences = null;
      _versionValue = null;
   }

   /**
    * Recycler le dto 'Personne'.
    * @param p_id
    *           (In)(*) L'identifiant de personne.
    * @param p_nom
    *           (In)(*) le nom de la personne.
    * @param p_prenom
    *           (In) le prénom de la personne.
    * @param p_civil
    *           (In)(*) civil.
    * @param p_dateNaissance
    *           (In) dateNaissance.
    * @param p_salaire
    *           (In) Le salaire de la personne.
    * @param p_grade
    *           (In) grade.
    */
   public void reset_PersonneDto (final Long p_id, final String p_nom, final String p_prenom, final Boolean p_civil,
            final Date p_dateNaissance, final Double p_salaire, final GradeDto p_grade)
   {
      setId(p_id);
      set_nom(p_nom);
      set_prenom(p_prenom);
      set_civil(p_civil);
      set_dateNaissance(p_dateNaissance);
      set_salaire(p_salaire);
      set_grade(p_grade);
      _tab_competences = null;
      _versionValue = null;
   }

   @Override
   public String toString ()
   {
      // Start of user code toString

      // toString modifié manuellement pour afficher le libellé dans l'IHM
      return String.valueOf(get_nom()) + ' ' + get_prenom();

      // End of user code
   }

}
