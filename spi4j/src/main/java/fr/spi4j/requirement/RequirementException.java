/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.requirement;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Exception lancée dans le cas d'une exigence non respectée.
 * @author MINARM
 */
public class RequirementException extends Spi4jRuntimeException
{

   private static final long serialVersionUID = 1L;

   private static final String c_messageCorrectionPossible = "Vérifier l'implémentation du service ou du test de l'exigence";

   private final Requirement_Itf _requirement;

   /**
    * Crée une exception levée pour le test de cette exigence.
    * @param p_requirement
    *           l'exigence non respectée.
    * @param p_message
    *           le message d'erreur
    */
   public RequirementException (final Requirement_Itf p_requirement, final String p_message)
   {
      this(p_requirement, p_message, c_messageCorrectionPossible);
   }

   /**
    * Crée une exception levée pour le test de cette exigence.
    * @param p_requirement
    *           l'exigence non respectée.
    * @param p_message
    *           le message d'erreur.
    * @param p_messageCorrectionPossible
    *           Le message indiquant une correction potentielle.
    */
   public RequirementException (final Requirement_Itf p_requirement, final String p_message,
            final String p_messageCorrectionPossible)
   {
      super(p_message, p_messageCorrectionPossible);
      _requirement = p_requirement;
   }

   /**
    * Crée une exception levée pour le test de cette exigence.
    * @param p_requirement
    *           l'exigence non respectée.
    * @param p_message
    *           le message d'erreur
    * @param p_rootCause
    *           l'exception cause
    */
   public RequirementException (final Requirement_Itf p_requirement, final String p_message, final Throwable p_rootCause)
   {
      super(p_rootCause, p_message, c_messageCorrectionPossible);
      _requirement = p_requirement;
   }

   /**
    * @return l'exigence qui n'a pas été respectée.
    */
   public Requirement_Itf getRequirement ()
   {
      return _requirement;
   }
}
