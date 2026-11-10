/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.admin;

/**
 * Interface d'un service d'administration.
 * @author MINARM
 */
public interface AdministrationService_Itf
{

   /**
    * @return le chemin de la jsp à afficher à l'utilisateur
    */
   String getServletContentPath ();

}
