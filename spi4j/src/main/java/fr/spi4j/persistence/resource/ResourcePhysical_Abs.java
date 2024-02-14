/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource;

/**
 * Classe abstraite des ressources physiques.
 * @author MINARM
 */
public abstract class ResourcePhysical_Abs
{

   /** Le type de la ressource physique. */
   private final ResourceType_Enum _resourceType;

   /**
    * Constructeur.
    * @param p_resourceType
    *           le type de la ressource physique
    */
   public ResourcePhysical_Abs (final ResourceType_Enum p_resourceType)
   {
      if (p_resourceType == null)
      {
         throw new IllegalArgumentException("Le paramètre 'resourceType' est obligatoire, il ne peut pas être 'null'\n");
      }
      _resourceType = p_resourceType;
   }

   /**
    * Permet d'obtenir le type associé à la ressource physique.
    * @return Le type associé à la ressource physique.
    */
   public ResourceType_Enum getResourceType ()
   {
      return _resourceType;
   }

   /**
    * Permet d'obtenir l'url de la ressource physique.
    * @return L'url de la ressource physique.
    */
   public abstract String getUrl ();

   /**
    * Permet d'obtenir le login pour se connecter à la ressource physique.
    * @return Le login pour se connecter à la ressource physique.
    */
   public abstract String getUser ();

   /**
    * Permet d'obtenir le mot de passe pour se connecter à la ressource physique.
    * @return Le mot de passe pour se connecter à la ressource physique.
    */
   public abstract String getPassword ();

}
