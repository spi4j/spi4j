/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import java.util.Date;

import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * L'interface définissant le contrat de persistance pour le type Grade.
 * @author MINARM
 */
public interface MyGradeEntity_Itf extends Entity_Itf<Long>
{
   // CONSTANTES

   // Start of user code Constantes GradeEntity_Itf

   // End of user code

   // METHODES ABSTRAITES

   /**
    * Recycler l'entité 'Grade'.
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
   public abstract void reset_GradeEntity (final Long p_Grade_id, final String p_libelle, final String p_trigramme,
            final Date p_xdmaj, final Boolean p_xtopsup);

   /**
    * Obtenir le libellé du grade.
    * @return le libellé du grade.
    */
   public abstract String get_libelle ();

   /**
    * Affecter le libellé du grade.
    * @param p_libelle
    *           (In)(*) le libellé du grade.
    */
   public abstract void set_libelle (final String p_libelle);

   /**
    * Obtenir le trigramme du grade.
    * @return le trigramme du grade.
    */
   public abstract String get_trigramme ();

   /**
    * Affecter le trigramme du grade.
    * @param p_trigramme
    *           (In)(*) le trigramme du grade.
    */
   public abstract void set_trigramme (final String p_trigramme);

   // Start of user code Methodes GradeEntity_Itf

   // End of user code

}
