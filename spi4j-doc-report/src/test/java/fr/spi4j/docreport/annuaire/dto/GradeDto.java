package fr.spi4j.docreport.annuaire.dto;

// Start of user code for imports

// End of user code

/**
 * DTO 'Grade'.
 * @author MINARM
 */
public class GradeDto
{
   // ATTRIBUTS

   /** L'identifiant. */
   private Long _id;

   /** le libellé du grade. */
   private String _libelle;

   /** le trigramme du grade. */
   private String _trigramme;

   // METHODES

   /**
    * Constructeur sans paramètre du dto 'Grade'.
    */
   public GradeDto ()
   {
      super();
   }

   /**
    * Constructeur complet du dto 'Grade'.
    * @param p_id
    *           (In)(*) L'identifiant de grade.
    * @param p_libelle
    *           (In)(*) le libellé du grade.
    * @param p_trigramme
    *           (In)(*) le trigramme du grade.
    */
   public GradeDto (final Long p_id, final String p_libelle, final String p_trigramme)
   {
      super();
      reset_GradeDto(p_id, p_libelle, p_trigramme);
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
    * Obtenir le libellé du grade.
    * @return le libellé du grade.
    */
   public String get_libelle ()
   {
      return _libelle;
   }

   /**
    * Affecter le libellé du grade.
    * @param p_libelle
    *           (In)(*) le libellé du grade.
    */
   public void set_libelle (final String p_libelle)
   {
      _libelle = p_libelle;
   }

   /**
    * Obtenir le trigramme du grade.
    * @return le trigramme du grade.
    */
   public String get_trigramme ()
   {
      return _trigramme;
   }

   /**
    * Affecter le trigramme du grade.
    * @param p_trigramme
    *           (In)(*) le trigramme du grade.
    */
   public void set_trigramme (final String p_trigramme)
   {
      _trigramme = p_trigramme;
   }

   /**
    * Recycler le dto 'Grade'.
    * @param p_id
    *           (In)(*) L'identifiant de grade.
    * @param p_libelle
    *           (In)(*) le libellé du grade.
    * @param p_trigramme
    *           (In)(*) le trigramme du grade.
    */
   public void reset_GradeDto (final Long p_id, final String p_libelle, final String p_trigramme)
   {
      setId(p_id);
      set_libelle(p_libelle);
      set_trigramme(p_trigramme);
   }

   @Override
   public String toString ()
   {
      // Start of user code toString

      return get_libelle();

      // End of user code
   }
}
