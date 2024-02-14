/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dbpopulate;

/**
 * Pour les erreurs de combinaison pour le peuplement de la BdD (ex : p_tablePersonne_VOL_NB_ROWS < v_taille * v_relationPersonne2Competence_VOL_MIN_OCCURS).
 * @author MINARM
 */
public class ImpossibleCombDatabaseException extends Exception
{
   /**
    *
    */
   private static final long serialVersionUID = 1L;

   /**
    *
    */
   public ImpossibleCombDatabaseException ()
   {
      super();
   }

   /**
    * Constructeur de l'exception avec le message décrivant l'erreur de combinaison.
    * @param p_message
    *           Le message
    */
   public ImpossibleCombDatabaseException (final String p_message)
   {
      super(p_message);
   }

}
