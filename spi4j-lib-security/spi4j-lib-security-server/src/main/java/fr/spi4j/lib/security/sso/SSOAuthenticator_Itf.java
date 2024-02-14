/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.sso;

import java.util.List;

/**
 * Interface pour connecteur pour SSO.
 * @author MINARM
 */
public interface SSOAuthenticator_Itf
{

   /**
    * Connecte l'utilisateur au SSO
    * @param p_user
    *           le nom d'utilisateur
    * @param p_password
    *           le mot de passe de l'utilisateur
    * @return les cookies de l'utilisateur connecté
    */
   SSOCookies login (final String p_user, final String p_password);

   /**
    * Vérifie le token de l'utilisateur
    * @param p_cookies
    *           les cookies de l'utilisateur
    * @return true si l'utilisateur est bien connecté, ou false sinon
    */
   boolean checkTokenValidity (final SSOCookies p_cookies);

   /**
    * Récupère les attributs (LDAP) de l'utilisateur connecté.
    * @param p_cookies
    *           les cookies de l'utilisateur
    * @return les attributs LDAP de l'utilisateur connecté
    */
   SSOUserAttributes getAttributes (final SSOCookies p_cookies);

   /**
    * Affecte le timeout en milliseconds (par défaut : 0 = aucun timeout)
    * @param p_timeout
    *           le nouveau timeout
    */
   void setTimeout (final int p_timeout);

   /**
    * Affecte le timeout sur connexion en milliseconds (par défaut : 0 = aucun timeout)
    * @param p_connectionTimeout
    *           le nouveau timeout de connexion
    */
   void setConnectionTimeout (final int p_connectionTimeout);

   /**
    * Définit le charset utilisé pour lire une réponse. Par défaut : ISO-8859-1
    * @param p_charset
    *           le charset à utiliser
    */
   void setCharset (final String p_charset);

   /**
    * Déconnexion de l'utilisateur courant.
    * @param p_cookies
    *           les cookies de l'utilisateur
    */
   void logout (final SSOCookies p_cookies);

   /**
    * Recherche les cookies utilisés par SSO et remplit la Map des cookies utilisés par SSO, avec une valeur null (ils n'ont pas encore été remplis lors du login)
    * @return les cookies qui doivent être forwardés au serveur SSO
    */
   List<String> searchCookiesToForward ();
}
