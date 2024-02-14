/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.proxy;

import fr.spi4j.lib.security.annotations.Permissions;
import fr.spi4j.lib.security.annotations.PermissionsOperator_Enum;

/**
 * Service de test
 * @author MINARM
 */
public interface Service_Itf
{
   /**
    * Méthode de test pour tester l'opérateur 'or'
    */
   @Permissions(value =
   {"titi", "tutu" }, operator = PermissionsOperator_Enum.OR)
   void orTest ();

   /**
    * Méthode de test pour tester l'opérateur 'and'
    */
   @Permissions(value =
   {"tutu", "tata" }, operator = PermissionsOperator_Enum.AND)
   void andTest ();

   /**
    * Méthode qui jette une exception
    */
   void jeterExceptionAvecCause ();

   // Méthodes pour tester les méthodes methodEquals et findPermissions
   /**
    * Méthode sans type de retour avec deux paramètres String
    * @param p_param
    *           premier paramètre
    * @param p_param2
    *           second paramètre
    */
   @Permissions(value =
   {"tata" }, operator = PermissionsOperator_Enum.OR)
   void maMethode (String p_param, String p_param2);

   /**
    * Méthode sans type de retour avec un paramètre String et un paramètre int
    * @param p_param
    *           premier paramètre
    * @param p_param2
    *           second paramètre
    */
   @Permissions(value =
   {"tata" }, operator = PermissionsOperator_Enum.OR)
   void maMethode (String p_param, int p_param2);

   /**
    * Méthode qui retourne un int avec un paramètre String
    * @param p_param
    *           premier paramètre
    * @return un int
    */
   @Permissions(value =
   {"tata" }, operator = PermissionsOperator_Enum.OR)
   int maMethode (String p_param);

   /**
    * Méthode qui retourne un int avec 0 paramètre
    * @return un int
    */
   @Permissions(value =
   {"tata" }, operator = PermissionsOperator_Enum.OR)
   int maMethode ();
}
