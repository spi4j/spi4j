/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import java.util.Set;

/**
 * Classe qui contient les permissions d'un utilisateur et le fait qu'il soit super admin ou non
 * @author MINARM
 */
public class PermissionContainer
{
   private final Set<String> _permissions;

   private final boolean _isSuperAdmin;

   /**
    * Constructeur
    * @param p_permissions
    *           les permissions de l'utilisateur
    * @param p_isSuperAdmin
    *           true s'il est super admin, false sinon
    */
   public PermissionContainer (final Set<String> p_permissions, final boolean p_isSuperAdmin)
   {
      this._permissions = p_permissions;
      this._isSuperAdmin = p_isSuperAdmin;
   }

   /**
    * @return les permissions de l'utilisateur
    */
   public Set<String> getPermissions ()
   {
      return _permissions;
   }

   /**
    * @return true s'il est super admin, false sinon
    */
   public boolean isSuperAdmin ()
   {
      return _isSuperAdmin;
   }

}
