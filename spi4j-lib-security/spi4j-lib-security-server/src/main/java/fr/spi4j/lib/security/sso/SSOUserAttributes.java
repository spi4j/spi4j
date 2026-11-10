/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.sso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: yblazart , Steria Date: 2 avr. 2010 Time: 13:18:45 Cette classe encapsule les attribut OpenSSO d'un utilisateur : Le token Id Les roles, représentés par l'inner class Role Les attributs (une map de liste de valeurs)
 */
public class SSOUserAttributes
{

   private String _tokenId;

   private final List<Role> _roles = new ArrayList<>();

   private final Map<String, List<String>> _attributes = new HashMap<>();

   /**
    * Mise à jour du token.
    * @param p_tokenId
    *           le token
    */
   void setTokenId (final String p_tokenId)
   {
      _tokenId = p_tokenId;
   }

   /**
    * @return la liste des roles
    */
   public List<Role> getRoles ()
   {
      return _roles;
   }

   /**
    * @return la liste de tous les attributs.
    */
   public Map<String, List<String>> getAttributes ()
   {
      return _attributes;
   }

   /**
    * @param p_name
    *           le nom de la propriété cherchée
    * @return les attributs d'une propriété
    */
   public List<String> getAttributeListByName (final String p_name)
   {
      return _attributes.get(p_name);
   }

   /**
    * Method package protected pour créer une liste de valeur vide pour un attribut s'il n'existe pas encore
    * @param p_name
    *           le nom de l'attribut créé
    */
   void createAttributeListByName (final String p_name)
   {
      if (!_attributes.containsKey(p_name))
      {
         _attributes.put(p_name, new ArrayList<String>());
      }
   }

   /**
    * @return le token id
    */
   public String getTokenId ()
   {
      return _tokenId;
   }

   @Override
   public String toString ()
   {
      final StringBuilder v_stringBuilder = new StringBuilder();
      v_stringBuilder.append("token=").append(getTokenId()).append("\n");
      for (final Role v_role : getRoles())
      {
         v_stringBuilder.append(v_role).append("\n");
      }
      for (final String v_name : getAttributes().keySet())
      {
         v_stringBuilder.append(v_name).append("=");
         for (final String v_value : getAttributeListByName(v_name))
         {
            v_stringBuilder.append(v_value).append(", ");
         }
         v_stringBuilder.append("\n");
      }

      return v_stringBuilder.toString();
   }

   /**
    * Class d'encapsulation d'un role OpenSSO rendu sous la forme groupe.sygap,ou=group,dc=opensso,dc=java,dc=net
    */
   public static class Role
   {

      private final String _fullString;

      private final String _root;

      /**
       * Constructeur.
       * @param p_fullString
       *           le nom complet du role
       */
      public Role (final String p_fullString)
      {
         _fullString = p_fullString;
         final int v_indexStart = p_fullString.indexOf("id=");
         final int v_indexStop = p_fullString.indexOf(',', v_indexStart);
         if (v_indexStop > -1)
         {
            _root = p_fullString.substring(v_indexStart + 3, v_indexStop);
         }
         else
         {
            _root = p_fullString.substring(v_indexStart + 3, p_fullString.length());
         }
      }

      /**
       * Renvoi la chaine de rôle tel que donnée par OpenSSO
       * @return chaine complête : groupe.sygap,ou=group,dc=opensso,dc=java,dc=net
       */
      public String getFullString ()
      {
         return _fullString;
      }

      /**
       * Donne l'élément final du role
       * @return nom du role , exemple : groupe.sygap
       */
      public String getRoot ()
      {
         return _root;
      }

      @Override
      public String toString ()
      {
         return _root;
      }
   }
}
