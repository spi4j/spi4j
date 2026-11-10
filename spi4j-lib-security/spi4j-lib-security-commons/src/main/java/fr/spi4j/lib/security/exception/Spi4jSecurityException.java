/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.exception;

/**
 * La classe mère des exceptions de sécurité.
 * @author MINARM
 */
public class Spi4jSecurityException extends RuntimeException
{

   private static final long serialVersionUID = 1L;

   /**
    * Constructeur parent.
    * @param p_message
    *           le message
    */
   public Spi4jSecurityException (final String p_message)
   {
      super(p_message);
   }

   /**
    * Constructeur parent surchargé.
    * @param p_message
    *           le message
    * @param p_cause
    *           la cause
    */
   public Spi4jSecurityException (final String p_message, final Throwable p_cause)
   {
      super(p_message, p_cause);
   }
}
