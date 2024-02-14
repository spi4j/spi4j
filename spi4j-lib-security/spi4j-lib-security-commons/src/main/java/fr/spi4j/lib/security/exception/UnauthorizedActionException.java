/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.exception;

import java.util.List;

import fr.spi4j.lib.security.annotations.PermissionsOperator_Enum;

/**
 * Exception levée lorsque l'utilisateur n'a pas les permissions pour effectuer une action donnée.
 * @author MINARM
 */
public class UnauthorizedActionException extends Spi4jSecurityException
{

   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    * @param p_message
    *           Le message d'erreur
    */
   public UnauthorizedActionException (final String p_message)
   {
      super(p_message);
   }

   /**
    * Constructeur surchargé.
    * @param p_utilisateur
    *           l'utilisateur
    * @param p_permissions
    *           les permissions requises pour effectuer l'action
    * @param p_operator
    *           l'opérateur 'AND' ou 'OR'
    * @return le message d'erreur
    */

   public static UnauthorizedActionException createException (final String p_utilisateur,
            final List<String> p_permissions, final PermissionsOperator_Enum p_operator)
   {
      final StringBuilder v_builder = new StringBuilder();

      // formatage de la liste des permissions
      for (final String v_string : p_permissions)
      {
         v_builder.append(v_string).append(", ");
      }
      // suppression de la virgule en trop
      if (v_builder.length() > 0)
      {
         v_builder.setLength(v_builder.length() - 2);
      }

      // traitement de l'erreur suivant l'opérateur
      if (p_operator == PermissionsOperator_Enum.AND)
      {
         return new UnauthorizedActionException("Permission non accordée pour l'utilisateur '" + p_utilisateur
                  + "'. \nIl lui faut toutes les permissions suivantes : " + v_builder.toString());
      }
      else if (p_operator == PermissionsOperator_Enum.OR)
      {

         return new UnauthorizedActionException("Permission non accordée pour l'utilisateur '" + p_utilisateur
                  + "'. \nIl lui faut au moins l'une des permissions suivantes : " + v_builder.toString());
      }
      else
      {
         return new UnauthorizedActionException("Permission non accordée.");
      }
   }
}
