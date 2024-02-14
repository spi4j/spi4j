/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import java.util.Collections;
import java.util.Set;

import fr.spi4j.filter.HttpSessionFilter;
import fr.spi4j.lib.security.exception.Spi4jSecurityException;
import fr.spi4j.lib.security.exception.UnauthentifiedUserException;
import jakarta.servlet.http.HttpSession;

/**
 * DTO 'Spi4jSecurity'.
 * @author MINARM
 */
public final class Spi4jServerSecurity extends Spi4jSecurity_Abs
{

   /**
    * La clé de stockage dans le HttpSession pour l'utilisateur courant.
    */
   public static final String c_keyUser = "user";

   /**
    * La clé de stockage dans le HttpSession pour les permissions de l'utilisateur courant.
    */
   private static final String c_keyPermissions = "permissions";

   /**
    * La clé de stockage dans le HttpSession pour savoir si l'utilisateur courant est super admin.
    */
   private static final String c_keySuperAdmin = "super_admin";

   /**
    * Retourne l'utilisateur courant dans la session http s'il est connecté. <br/>
    * throws UnauthentifiedUserException si aucun utilisateur n'existe dans la session http<br/>
    * throws SecurityException si la session http n'existe pas<br/>
    * @return l'utilisateur courant dans la session http s'il est connecté
    */
   @Override
   public User_Itf getCurrentUser ()
   {
      // On vérifie que la sessionHttp n'est pas nulle
      final HttpSession v_httpSession = getCurrentSession();

      // On récupère l'utilisateur en attribut
      final Object v_utilisateur = v_httpSession.getAttribute(c_keyUser);
      if (v_utilisateur instanceof User_Itf)
      {
         final User_Itf v_utilisateurDto = (User_Itf) v_utilisateur;
         return v_utilisateurDto;
      }
      throw new UnauthentifiedUserException();
   }

   /**
    * {@inheritDoc} Définit l'utilisateur courant dans la session http
    * @param p_utilisateur
    *           UtilisateurDto
    */
   @Override
   public void setCurrentUser (final User_Itf p_utilisateur)
   {
      final HttpSession v_httpSession = getCurrentSession();
      v_httpSession.removeAttribute(c_keyPermissions);
      v_httpSession.removeAttribute(c_keySuperAdmin);
      if (p_utilisateur == null)
      {
         v_httpSession.removeAttribute(c_keyUser);
      }
      else
      {
         v_httpSession.setAttribute(c_keyUser, p_utilisateur);
      }
   }

   @Override
   public void refreshPermissions ()
   {
      final HttpSession v_httpSession = getCurrentSession();
      v_httpSession.removeAttribute(c_keyPermissions);
      v_httpSession.removeAttribute(c_keySuperAdmin);
      initPermissionsInSession();
   }

   /**
    * {@inheritDoc} Invalide la session http de l'utilisateur courant.
    */
   @Override
   public void deconnexion ()
   {
      final HttpSession v_httpSession = HttpSessionFilter.getSessionForCurrentThread();
      if (v_httpSession != null)
      {
         v_httpSession.invalidate();
      }
   }

   @Override
   protected Set<String> getPermissionsOfCurrentUser ()
   {
      return getPermissionsOfCurrentUser(true);
   }

   /**
    * Retourne les permissions de l'utilisateur courant
    * @param p_initIfUnknown
    *           true si il faut initialiser les permissions si elles n'existent pas
    * @return les permissions de l'utilisateur courant
    */
   @SuppressWarnings("unchecked")
   private Set<String> getPermissionsOfCurrentUser (final boolean p_initIfUnknown)
   {
      // On vérifie que la sessionHttp n'est pas nulle
      final HttpSession v_httpSession = getCurrentSession();

      // On récupère les permissions en attribut de la sessionHttp
      final Object v_permissions = v_httpSession.getAttribute(c_keyPermissions);
      final Set<String> v_result;
      if (v_permissions instanceof Set)
      {
         // les permissions de l'utilisateur sont déjà initialisées dans la session
         v_result = (Set<String>) v_permissions;
      }
      else if (p_initIfUnknown)
      {
         // les permissions ne sont pas encore initialisées dans la session ou bien l'utilisateur n'est pas authentifié
         initPermissionsInSession();
         v_result = getPermissionsOfCurrentUser(false);

      }
      else
      {
         v_result = Collections.emptySet();
      }
      return v_result;
   }

   @Override
   protected boolean isCurrentUserSuperAdmin ()
   {
      return isCurrentUserSuperAdmin(true);
   }

   /**
    * Retourne true si l'utilisateur courant est super admin, false sinon
    * @param p_initIfUnknown
    *           true si il faut initialiser les permissions si elles n'existent pas
    * @return true si l'utilisateur courant est super admin, false sinon
    */
   private boolean isCurrentUserSuperAdmin (final boolean p_initIfUnknown)
   {
      final boolean v_superAdmin;
      final HttpSession v_httpSession = getCurrentSession();
      final Object v_valueSuperAdmin = v_httpSession.getAttribute(c_keySuperAdmin);
      if (v_valueSuperAdmin instanceof Boolean)
      {
         // les permissions de l'utilisateur sont déjà initialisées dans la session
         v_superAdmin = (Boolean) v_valueSuperAdmin;
      }
      else if (p_initIfUnknown)
      {
         // les permissions ne sont pas encore initialisées dans la session ou bien l'utilisateur n'est pas authentifié
         initPermissionsInSession();
         v_superAdmin = isCurrentUserSuperAdmin(false);
      }
      else
      {
         v_superAdmin = false;
      }
      return v_superAdmin;
   }

   /**
    * @return la session http pour le thread courant.
    */
   private HttpSession getCurrentSession ()
   {
      final HttpSession v_httpSession = HttpSessionFilter.getSessionForCurrentThread();
      if (v_httpSession == null)
      {
         throw new Spi4jSecurityException("Aucune session HTTP courante.");
      }
      return v_httpSession;
   }

   /**
    * Initialise les permissions dans la session.
    */
   private void initPermissionsInSession ()
   {
      final PermissionContainer v_perms = getAuthorizationService().getPermissionsOfCurrentUser();

      // On rend le set non modifiable pour être sûr qu'il n'est pas changé insidieusement
      final Set<String> v_result = Collections.unmodifiableSet(v_perms.getPermissions());

      // On vérifie que la sessionHttp n'est pas nulle
      final HttpSession v_httpSession = getCurrentSession();

      // on stocke les noms de permissions dans la session pour la prochaine fois
      v_httpSession.setAttribute(c_keyPermissions, v_result);
      // on stocke le fait que l'utilisateur soit super admin ou non
      v_httpSession.setAttribute(c_keySuperAdmin, v_perms.isSuperAdmin());
   }
}
