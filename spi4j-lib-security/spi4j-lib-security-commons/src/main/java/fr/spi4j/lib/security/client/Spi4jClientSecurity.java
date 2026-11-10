/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.client;

import java.util.Collections;
import java.util.Set;

import fr.spi4j.lib.security.PermissionContainer;
import fr.spi4j.lib.security.Spi4jSecurity_Abs;
import fr.spi4j.lib.security.User_Itf;

/**
 * Utilisation de la sécurité côté client.
 * @author MINARM
 */
public final class Spi4jClientSecurity extends Spi4jSecurity_Abs
{

   private User_Itf _utilisateur;

   // permissions vides quand il n'y a pas encore d'utilisateur
   private Set<String> _permissions;

   // initialisé à false
   private boolean _isSuperAdmin;

   @Override
   public User_Itf getCurrentUser ()
   {
      return _utilisateur;
   }

   @Override
   public void setCurrentUser (final User_Itf p_user)
   {
      // Si le paramètre n'est pas null
      if (p_user != null)
      {
         // on connait l'utilisateur
         _utilisateur = p_user;
         // mais on ne connait pas encore ses permissions (lors du premier appel à getPermissionsOfCurrentUser)
         _permissions = null;
         _isSuperAdmin = false;
      }
      else
      {
         // Sinon, on met les permissions et l'utilisateur comme étant null
         _utilisateur = null;
         _permissions = null;
         _isSuperAdmin = false;
      }
   }

   /**
    * {@inheritDoc} Cet appel nécessite que l'appel à {@link #setCurrentUser(User_Itf)} a été fait.
    */
   @Override
   public void refreshPermissions ()
   {
      // On crée une liste pour stocker toutes les permissions de l'utilisateur
      final PermissionContainer v_perms = getAuthorizationService().getPermissionsOfCurrentUser();
      _permissions = Collections.unmodifiableSet(v_perms.getPermissions());
      _isSuperAdmin = v_perms.isSuperAdmin();
   }

   @Override
   public void deconnexion ()
   {
      setCurrentUser(null);
   }

   @Override
   protected Set<String> getPermissionsOfCurrentUser ()
   {
      if (_permissions == null)
      {
         refreshPermissions();
      }
      return _permissions;
   }

   @Override
   protected boolean isCurrentUserSuperAdmin ()
   {
      if (_permissions == null)
      {
         refreshPermissions();
      }
      return _isSuperAdmin;
   }

}
