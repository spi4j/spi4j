/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import java.util.Arrays;
import java.util.Set;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.lib.security.annotations.PermissionsOperator_Enum;

/**
 * @author MINARM
 */
public abstract class Spi4jSecurity_Abs
{
   /**
    * Permission donnant toutes les autres permissions.
    */
   public static final String c_superAdmin = "superAdmin";

   /**
    * L'instance de classe.
    */
   private static Spi4jSecurity_Abs instance;

   /**
    * Flag d'activation de la sécurité (initialisé à false).
    */
   private boolean _enabled;

   /**
    * Service d'autorisation. (Récupération des permissions pour un utilisateur)
    */
   private AuthorizationService_Itf _authorizationService;

   /**
    * Obtenir l'instance du service de sécurité.
    * @return l'instance du service de sécurité
    */
   public static Spi4jSecurity_Abs getInstance ()
   {
      if (instance == null)
      {
         throw new Spi4jRuntimeException("L'instance du service de sécurité n'a pas été affectée",
                  "Vérifiez que l'application a bien fait un appel à Spi4jSecurity_Abs.setInstance(...)");
      }
      return instance;
   }

   /**
    * Affecte l'instance du service de sécurité.
    * @param p_security
    *           l'instance du service de sécurité
    */
   public static void setInstance (final Spi4jSecurity_Abs p_security)
   {
      instance = p_security;
   }

   /**
    * Active la sécurité pour le projet.
    */
   public void enable ()
   {
      _enabled = true;
   }

   /**
    * Vérifie si la sécurité est active pour le projet.
    * @return true si la sécurité est active, false sinon.
    */
   public boolean isEnabled ()
   {
      return _enabled;
   }

   /**
    * vérifie que l'utilisateur courant a bien la permission de faire l'action demandée.
    * @param p_permission
    *           La permission requise.
    * @return Si l'utilisateur est autorisé
    */
   public boolean hasPermission (final String p_permission)
   {
      final String[] v_permissions =
      {p_permission };
      return hasPermissions(v_permissions, PermissionsOperator_Enum.OR);
   }

   /**
    * vérifie que l'utilisateur courant a bien la permission de faire l'action demandée.
    * @param p_permissions
    *           La liste des permissions requises.
    * @param p_operator
    *           L'opérateur de type PermissionsOperator_Enum
    * @return Si l'utilisateur est autorisé
    */
   public boolean hasPermissions (final String[] p_permissions, final PermissionsOperator_Enum p_operator)
   {

      boolean v_autorise = false;

      // On vérifie d'abord si parmi ses permissions, il a la permission de superAdmin (on récupère cette propriété dans la session de l'utilisateur)
      final boolean v_superAdmin = isCurrentUserSuperAdmin();
      if (v_superAdmin)
      {
         v_autorise = true;
      }
      else
      {
         // Sinon, on fait le traitement habituel :
         // On récupère les permissions de l'utilisateur courant
         final Set<String> v_permissionsOfCurrentUser = getPermissionsOfCurrentUser();

         // On traite la liste différement selon que l'opérateur est 'AND' ou 'OR'
         if (p_operator == PermissionsOperator_Enum.AND)
         {
            // On retourne un booléen vérifiant que toutes les permissions requises sont bien parmi les permissions de l'utilisateur
            v_autorise = v_permissionsOfCurrentUser.containsAll(Arrays.asList(p_permissions));
         }
         else if (p_operator == PermissionsOperator_Enum.OR)
         {
            // Parmi toutes les permissions de l'utilisateur,
            // si au moins une de ses permissions correspond à l'une des permissions requises
            // alors l'utilisateur est autorisé à faire son action
            for (final String v_permParam : p_permissions)
            {
               if (v_permissionsOfCurrentUser.contains(v_permParam))
               {
                  v_autorise = true;
                  break;
               }
            }
         }
         else
         {
            // On jette une erreur comme quoi l'opérateur utilisé n'est pas correct
            throw new Spi4jRuntimeException("Opérateur inconnu : " + p_operator,
                     "Vérifier l'implémentation de AutorisationService");
         }
      }
      return v_autorise;
   }

   /**
    * Rafraichit les permissions de l'utilisateur.
    */
   public abstract void refreshPermissions ();

   /**
    * @return l'utilisateur courant.
    */
   public abstract User_Itf getCurrentUser ();

   /**
    * Affecte l'utilisateur courant.
    * @param p_user
    *           l'utilisateur courant
    */
   public abstract void setCurrentUser (final User_Itf p_user);

   /**
    * Déconnecte l'utilisateur courant.
    */
   public abstract void deconnexion ();

   /**
    * @return les permissions de l'utilisateur courant
    */
   protected abstract Set<String> getPermissionsOfCurrentUser ();

   /**
    * @return true si l'utilisateur courant est super admin
    */
   protected abstract boolean isCurrentUserSuperAdmin ();

   /**
    * @return le service qui récupère les permissions pour l'utilisateur courant
    */
   public AuthorizationService_Itf getAuthorizationService ()
   {
      if (_authorizationService == null)
      {
         throw new Spi4jRuntimeException("Aucun service d'autorisation n'a été initialisé",
                  "Appelez la méthode Spi4jSecurity_Abs.getInstance.setAuthorizationService(...)");
      }
      return _authorizationService;
   }

   /**
    * Initialise le service d'autorisation, qui permettra de connaître quelles sont les permissions associées à l'utilisateur courant.
    * @param p_authorizationService
    *           le service d'autorisation
    */
   public void setAuthorizationService (final AuthorizationService_Itf p_authorizationService)
   {
      _authorizationService = p_authorizationService;
   }

}
