/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Exception indiquant que les versions client et serveur sont différentes.
 * @author MINARM
 */
public class IllegalVersionException extends Spi4jRuntimeException
{

   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    * @param p_versionServeur
    *           String la version du serveur build.number
    * @param p_versionClient
    *           String la version du client build.number
    */
   public IllegalVersionException (final String p_versionServeur, final String p_versionClient)
   {
      super("La version du serveur est " + p_versionServeur, "mettez à jour votre client qui est en " + p_versionClient);
   }
}
