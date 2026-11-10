/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import java.io.Serializable;

/**
 * Un utilisateur de l'application
 * @author MINARM
 */
public interface User_Itf extends Serializable
{

   /**
    * @return l'identifiant de l'utilisateur
    */
   String getIdentifiant ();

}
