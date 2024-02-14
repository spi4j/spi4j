/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.graal;

/**
 * Exception GRAAL par défaut.
 * @author MINARM
 */
public class GraalException extends RuntimeException
{

   private static final long serialVersionUID = 1L;

   /**
    * Constructeur avec un message.
    * @param p_message
    *           le message d'erreur
    */
   public GraalException (final String p_message)
   {
      super(p_message);
   }
}
