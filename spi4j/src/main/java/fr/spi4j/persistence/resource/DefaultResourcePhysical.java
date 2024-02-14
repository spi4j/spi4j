/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource;

/**
 * Resource physique d'accès à une base de données.
 * @author MINARM
 */
public class DefaultResourcePhysical extends ResourcePhysical_Abs
{

   /** L'url de la ressource physique. */
   private final String _url;

   /** Le login pour se connecter à la ressource physique. */
   private final String _user;

   /** Le mot de passe pour se connecter à la ressource physique. */
   private final String _password;

   /**
    * Constructeur max : avec tous les attributs. Exemple :<code><pre>
    *    DefaultResourcePhysical v_ResourcePhysical = new DefaultResourcePhysical(...);
    * </pre></code>
    * @param p_url
    *           (In)(*) L'url de la ressource physique.
    * @param p_user
    *           (In)(*) Le login pour se connecter à la ressource physique.
    * @param p_password
    *           (In)(*) Le mot de passe pour se connecter à la ressource physique.
    * @param p_resourceType
    *           le type de la ressource physique
    */
   public DefaultResourcePhysical (final String p_url, final String p_user, final String p_password,
            final ResourceType_Enum p_resourceType)
   {
      // Initialiser la filiation
      super(p_resourceType);

      // Affecter l'url de la ressource physique
      if (p_url == null)
      {
         throw new IllegalArgumentException("Le paramètre 'url' est obligatoire, il ne peut pas être 'null'\n");
      }
      _url = p_url;

      // Affecter le login pour se connecter à la ressource physique
      if (p_user == null)
      {
         throw new IllegalArgumentException("Le paramètre 'user' est obligatoire, il ne peut pas être 'null'\n");
      }
      _user = p_user;

      // Affecter le mot de passe pour se connecter à la ressource physique
      if (p_password == null)
      {
         throw new IllegalArgumentException("Le paramètre 'password' est obligatoire, il ne peut pas être 'null'\n");
      }
      _password = p_password;
   }

   @Override
   public final String getUrl ()
   {
      return _url;
   }

   @Override
   public final String getUser ()
   {
      return _user;
   }

   @Override
   public final String getPassword ()
   {
      return _password;
   }

   /**
    * Permet d'obtenir une chaîne représentant tous les attributs du bean. Exemple :<code><pre>
    *    DefaultResourcePhysical v_ResourcePhysical = new DefaultResourcePhysical(...);
    *    // ...
    *    System.out.println(v_ResourcePhysical);
    * </pre></code>
    * @return La chaîne représentant tous les attributs du bean.
    */
   @Override
   public String toString ()
   {
      return getClass().getName() + "[_url=" + _url + ", _user=" + _user + ", _password=" + _password + ']';
   }
}
