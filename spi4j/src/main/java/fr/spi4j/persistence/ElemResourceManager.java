/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import fr.spi4j.persistence.resource.ResourceManager_Abs;
import fr.spi4j.persistence.resource.ResourcePhysical_Abs;

/**
 * Gestionnaire de ressources.
 * @author MINARM
 */
public class ElemResourceManager
{
   /** Identifiant de l'application. */
   private final String _idAppli;

   private ResourceManager_Abs _resourceManager;

   // Pour l'instant, le mode (central/nomade) ne sert à rien et n'est pas utilisé donc il est en commentaire ci-dessous.
   // Pour utiliser ce mode (nomade en plus de central), cet attribut, le constructeur et le getResourceManager() ci-dessous
   // peuvent être supprimés et l'implémentation ci-dessous avec le mode peut être décommentée
   private final ResourcePhysical_Abs _resourcePhysical;

   /**
    * Constructeur.
    * @param p_idAppli
    *           (In)(*) l'identifiant de l'application
    * @param p_resourcePhysical
    *           (In)(*) la ressource physique de base de l'application.
    */
   public ElemResourceManager (final String p_idAppli, final ResourcePhysical_Abs p_resourcePhysical)
   {
      super();
      _idAppli = p_idAppli;
      _resourcePhysical = p_resourcePhysical;
   }

   /**
    * Retourne le gestionnaire de ressources.
    * @return le gestionnaire de ressources.
    */
   public ResourceManager_Abs getResourceManager ()
   {
      if (_resourceManager == null)
      {
         _resourceManager = ResourceManager_Abs.getResourceManager(_idAppli, _resourcePhysical);
      }
      return _resourceManager;
   }

   // private final Map<Mode_Enum, ResourcePhysical_Abs> _map_ResourcePhysical = new HashMap<Mode_Enum, ResourcePhysical_Abs>();
   //
   // private Mode_Enum _mode = Mode_Enum.centrale;
   //
   // /**
   // * Enumeration des modes de ressources physiques de l'application.
   // *
   // * @author MINARM
   // */
   // public static enum Mode_Enum
   // {
   // /** Mode nomade. */
   // nomade,
   // /** Mode centrale. */
   // centrale;
   // }
   //
   // /**
   // * Constructeur.
   // *
   // * @param p_idAppli
   // * (In)(*) l'identifiant de l'application
   // * @param p_resourcePhysical
   // * (In)(*) la ressource physique de base de l'application.
   // */
   // public ElemResourceManager (final String p_idAppli, final ResourcePhysical_Abs p_resourcePhysical)
   // {
   // super();
   // _idAppli = p_idAppli;
   // _map_ResourcePhysical.put(Mode_Enum.centrale, p_resourcePhysical);
   // }
   //
   // /**
   // * Ajotue une ressource physique pour l'application.
   // *
   // * @param p_resourcePhysical
   // * la ressource à ajouter.
   // * @param p_mode
   // * mode de la ressource physique.
   // */
   // public void addResourcePhysical (final ResourcePhysical_Abs p_resourcePhysical, final Mode_Enum p_mode)
   // {
   // if (_map_ResourcePhysical.containsKey(p_mode))
   // {
   // throw new Spi4jRuntimeException(" This mode : " + p_mode + " already exist !\n", "");
   // }
   // _map_ResourcePhysical.put(p_mode, p_resourcePhysical);
   // }
   //
   // /**
   // * Modifie le mode de l'application.
   // *
   // * @param p_mode
   // * le nouveau mode.
   // */
   // public void setMode (final Mode_Enum p_mode)
   // {
   // _mode = p_mode;
   // _resourceManager = null;
   // }
   //
   // /**
   // * Retourne le mode de l'application.
   // *
   // * @return le mode de l'application.
   // */
   // public Mode_Enum getMode ()
   // {
   // return this._mode;
   // }
   //
   // /**
   // * Retourne le gestionnaire de ressources.
   // *
   // * @return le gestionnaire de ressources.
   // */
   // public ResourceManager_Abs getResourceManager ()
   // {
   // if (_resourceManager == null)
   // {
   // final ResourcePhysical_Abs v_resourcePhysical = _map_ResourcePhysical.get(_mode);
   // _resourceManager = ResourceManager_Abs.getResourceManager(_idAppli, v_resourcePhysical);
   // }
   // return _resourceManager;
   // }
}
