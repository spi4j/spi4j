/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import java.io.Serializable;

/**
 * Objet contenant un identifiant et un mot de passe : utilisé pour le service "connexion"
 * @author MINARM
 */
public class Login implements Serializable
{
   private static final long serialVersionUID = 1L;

   private String _identifiant;

   private String _motDePasse;

   /**
    * Constructeur (visibilité package) sans paramètres pour compatibilité avec la sérialisation GWT.
    */
   Login ()
   {
      super();
      // les champs _identifiant et _motDePasse ne sont pas finaux car la sérialisation GWT doit pouvoir les remplir
   }

   /**
    * Constructeur.
    * @param p_identifiant
    *           Identifiant
    * @param p_motDePasse
    *           Mot de passe
    */
   public Login (final String p_identifiant, final String p_motDePasse)
   {
      super();
      _identifiant = p_identifiant;
      _motDePasse = p_motDePasse;
   }

   /**
    * @return l'identifiant
    */
   public String getIdentifiant ()
   {
      return _identifiant;
   }

   /**
    * @return le mot de passe
    */
   public String getMotDePasse ()
   {
      return _motDePasse;
   }

   @Override
   public String toString ()
   {
      // sécurité : mot de passe masqué dans le toString pour que les mots de passe n'apparaissent pas dans les fichiers de log
      return getClass().getName() + '[' + _identifiant + ", <motDePasse masqué>]";
   }
}
