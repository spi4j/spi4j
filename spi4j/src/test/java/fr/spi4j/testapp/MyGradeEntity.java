/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

// Start of user code for imports

import java.util.Date;

import fr.spi4j.exception.Spi4jValidationException;

// End of user code

/**
 * Entité du type Grade.
 * @author MINARM
 */
public class MyGradeEntity implements MyGradeEntity_Itf
{
   // CONSTANTES

   /**
    * SerialUid.
    */
   private static final long serialVersionUID = -1;

   // Start of user code Constantes GradeEntity

   // End of user code

   // ATTRIBUTS

   /** Id. */
   private Long _Grade_id;

   /** le libellé du grade. */
   private String _libelle;

   /** le trigramme du grade. */
   private String _trigramme;

   // Start of user code Attributs GradeEntity

   // End of user code

   // METHODES

   /**
    * Constructeur sans paramètre de l'entité 'Grade'.
    */
   public MyGradeEntity ()
   {
      super();
   }

   /**
    * Constructeur complet de l'entité 'Grade'.
    * @param p_Grade_id
    *           (In)(*) L'identifiant de 'Grade'.
    * @param p_libelle
    *           (In)(*) le libellé du grade.
    * @param p_trigramme
    *           (In)(*) le trigramme du grade.
    * @param p_xdmaj
    *           XDMAJ
    * @param p_xtopsup
    *           XTOPSUP
    */
   public MyGradeEntity (final Long p_Grade_id, final String p_libelle, final String p_trigramme, final Date p_xdmaj,
            final Boolean p_xtopsup)
   {
      super();
      reset_GradeEntity(p_Grade_id, p_libelle, p_trigramme, p_xdmaj, p_xtopsup);
   }

   @Override
   public Long getId ()
   {
      return _Grade_id;
   }

   @Override
   public void setId (final Long p_id)
   {
      _Grade_id = p_id;
   }

   @Override
   public String get_libelle ()
   {
      return _libelle;
   }

   @Override
   public void set_libelle (final String p_libelle)
   {
      _libelle = p_libelle;
   }

   @Override
   public String get_trigramme ()
   {
      return _trigramme;
   }

   @Override
   public void set_trigramme (final String p_trigramme)
   {
      _trigramme = p_trigramme;
   }

   @Override
   public void reset_GradeEntity (final Long p_Grade_id, final String p_libelle, final String p_trigramme,
            final Date p_xdmaj, final Boolean p_xtopsup)
   {
      setId(p_Grade_id);
      set_libelle(p_libelle);
      set_trigramme(p_trigramme);
   }

   @Override
   public String toString ()
   {
      return super.toString() + " : " + _Grade_id + ", " + _libelle + ", " + _trigramme;
   }

   @Override
   public void validate () throws Spi4jValidationException
   {
      // ras
   }

   // Start of user code Methodes GradeEntity

   // End of user code

}
