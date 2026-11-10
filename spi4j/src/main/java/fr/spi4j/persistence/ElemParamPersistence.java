/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.persistence.resource.ResourceManager_Abs;

/**
 * .
 */
public class ElemParamPersistence
{
   /** L'attribut 'Entity'. */
   private final Class<? extends Entity_Itf<?>> _entity;

   /** L'attribut 'Dao'. */
   private final Class<? extends Dao_Itf<?, ?, ?>> _dao;

   /** L'attribut 'Resource'. */
   private final ElemResourceManager _elemResourceManager;

   /**
    * Constructeur max : avec tous les attributs. Exemple : <code><pre>
    *    ElemParamPersistence v_ElemParamPersistence = new ElemParamPersistence(...);
    * </pre></code>
    * @param p_Entity
    *           (In)(*) L'attribut 'Entity'.
    * @param p_Dao
    *           (In)(*) L'attribut 'Dao'.
    * @param p_ResourceManager
    *           (In)(*) L'attribut 'Resource'.
    */
   public ElemParamPersistence (final Class<? extends Entity_Itf<?>> p_Entity,
            final Class<? extends Dao_Itf<?, ?, ?>> p_Dao, final ElemResourceManager p_ResourceManager)
   {
      if (p_Entity == null)
      {
         throw new IllegalArgumentException("Le paramètre 'Entity' est obligatoire, il ne peut pas être 'null'\n");
      }
      if (p_Dao == null)
      {
         throw new IllegalArgumentException("Le paramètre 'Dao' est obligatoire, il ne peut pas être 'null'\n");
      }
      if (p_ResourceManager == null)
      {
         throw new IllegalArgumentException("Le paramètre 'Resource' est obligatoire, il ne peut pas être 'null'\n");
      }
      _entity = p_Entity;
      _dao = p_Dao;
      _elemResourceManager = p_ResourceManager;
   }

   /**
    * Permet d'obtenir l'attribut 'Entity'.
    * @return L'attribut 'Entity'.
    */
   public final Class<? extends Entity_Itf<?>> getEntity ()
   {
      return _entity;
   }

   /**
    * Permet d'obtenir l'attribut 'Dao'.
    * @return L'attribut 'Dao'.
    */
   public final Class<? extends Dao_Itf<?, ?, ?>> getDao ()
   {
      return _dao;
   }

   /**
    * Permet d'obtenir l'attribut 'Resource'.
    * @return L'attribut 'Resource'.
    */
   public final ResourceManager_Abs getResourceManager ()
   {
      return _elemResourceManager.getResourceManager();
   }

   /**
    * Permet d'obtenir une chaîne représentant tous les attributs du bean. Exemple : <code><pre>
    *    ElemParamPersistence v_ElemParamPersistence = new ElemParamPersistence(...);
    *    // ...
    *    System.out.println(v_ElemParamPersistence);
    * </pre></code>
    * @return La chaîne représentant tous les attributs du bean.
    */
   @Override
   public String toString ()
   {
      return ElemParamPersistence.class.getName() + "[_entity=" + _entity + ", _dao=" + _dao
               + ", _elemResourceManager=" + _elemResourceManager + ']';
   }
}
