/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

/**
 * Service de gestion des permissions.
 * @author MINARM
 */
public interface AuthorizationService_Itf
{
   /**
    * @return les permissions pour l'utilisateur courant.
    */
   PermissionContainer getPermissionsOfCurrentUser ();

}
