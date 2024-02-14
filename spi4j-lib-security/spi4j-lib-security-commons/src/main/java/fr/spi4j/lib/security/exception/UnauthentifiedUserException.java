/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.exception;

/**
 * Exception levée lorsque l'utilisateur n'est pas authentifié.
 * @author MINARM
 */

public class UnauthentifiedUserException extends Spi4jSecurityException
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public UnauthentifiedUserException ()
   {
      super("Accès refusé. L'utilisateur n'est pas authentifié.");
   }

}
