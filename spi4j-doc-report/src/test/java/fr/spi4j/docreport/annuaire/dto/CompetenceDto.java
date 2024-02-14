package fr.spi4j.docreport.annuaire.dto;

// Start of user code for imports

// End of user code

/**
 * DTO 'Competence'.
 * @author MINARM
 */
public class CompetenceDto
{
   // ATTRIBUTS

   /** L'identifiant. */
   private Long _id;

   /** le libellé de la compétence. */
   private String _libelle;

   // METHODES

   /**
    * Constructeur sans paramètre du dto 'Competence'.
    */
   public CompetenceDto ()
   {
      super();
   }

   /**
    * Constructeur complet du dto 'Competence'.
    * @param p_id
    *           (In)(*) L'identifiant de competence.
    * @param p_libelle
    *           (In)(*) le libellé de la compétence.
    */
   public CompetenceDto (final Long p_id, final String p_libelle)
   {
      super();
      reset_CompetenceDto(p_id, p_libelle);
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
    * Obtenir le libellé de la compétence.
    * @return le libellé de la compétence.
    */
   public String get_libelle ()
   {
      return _libelle;
   }

   /**
    * Affecter le libellé de la compétence.
    * @param p_libelle
    *           (In)(*) le libellé de la compétence.
    */
   public void set_libelle (final String p_libelle)
   {
      _libelle = p_libelle;
   }

   /**
    * Recycler le dto 'Competence'.
    * @param p_id
    *           (In)(*) L'identifiant de competence.
    * @param p_libelle
    *           (In)(*) le libellé de la compétence.
    */
   public void reset_CompetenceDto (final Long p_id, final String p_libelle)
   {
      setId(p_id);
      set_libelle(p_libelle);
   }

   @Override
   public String toString ()
   {
      // Start of user code toString

      // toString modifié manuellement pour afficher le libellé dans l'IHM
      return get_libelle();

      // End of user code
   }
}
