/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.entity.fetching;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.EntityAttributeHelper;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Classe permettant de fetcher, et donc d'appliquer une FetchingStrategy, sur une liste de Entitys.<br/>
 * Seule la méthode statique applyFetchingStrategy(FetchingStrategy, List) doit être utilisée.<br/>
 * <br/>
 * Les instances de cette classe contiennent un état dépendant de la transaction en cours de leurs fetchingStrategies ;<br/>
 * ces instances ne doivent donc pas être ni réutilisées, ni réutilisables.
 * @param <TypeId>
 *           Type de l'id
 * @author MINARM
 */
public final class FetchingStrategyFetcher<TypeId>
{
   private final FetchingStrategy_Abs<TypeId, ? extends Entity_Itf<TypeId>> _fetchingStrategy;

   private final ColumnsNames_Itf _attribute;

   private final List<FetchingStrategy_Abs<TypeId, ? extends Entity_Itf<TypeId>>> _childrenStrategies;

   private final Method _getter_method;

   private final Method _setter_method;

   private final Method _getter_method_for_id;

   private final Map<TypeId, Entity_Itf<TypeId>> _loadedEntities;

   /**
    * Constructeur.
    * @param p_fetchingStrategy
    *           FetchingStrategy_Abs
    * @param p_entityClass
    *           Classe du Entity
    */
   // ce constructeur doit rester private pour en maîtriser l'instanciation et la fin de vie
   private FetchingStrategyFetcher (
            final FetchingStrategy_Abs<TypeId, ? extends Entity_Itf<TypeId>> p_fetchingStrategy,
            final Class<? extends Entity_Itf<TypeId>> p_entityClass)
   {
      super();

      _fetchingStrategy = p_fetchingStrategy;
      _attribute = _fetchingStrategy.getAttribute();

      _childrenStrategies = _fetchingStrategy.getChildren();

      _loadedEntities = new HashMap<>();

      try
      {
         _getter_method = EntityAttributeHelper.getInstance().getGetterMethodForColumn(p_entityClass, _attribute);
         if (_getter_method == null)
         {
            throw new NoSuchMethodException("Getter de l'attribut " + _attribute.getLogicalColumnName()
                     + " non trouvé pour le Entity"
                     + p_entityClass.getSimpleName());
         }
         if (!List.class.equals(_getter_method.getReturnType()))
         {
            _getter_method_for_id = EntityAttributeHelper.getInstance().getGetterMethodForAttributeId(p_entityClass,
                     _attribute);
            _setter_method = EntityAttributeHelper.getInstance().getSetterMethodForColumn(p_entityClass, _attribute);
         }
         else
         {
            _getter_method_for_id = null;
            _setter_method = null;
         }
      }
      catch (final Exception v_ex)
      {
         throw new RuntimeException(v_ex);
      }
   }

   /**
    * Applique des fetchingStrategy filles à une liste de Entity.
    * @param p_fetchingStrategy
    *           FetchingStrategy
    * @param p_listeEntity
    *           Liste de Entities
    * @param <TypeId>
    *           Type de l'id dans les Entities
    * @param <TypeEntity>
    *           Type des Entities qui doit être le même pour la liste et pour la FetchingStrategy
    */
   // cette méthode est la seule méthode typée et doit être la seule publique
   public static <TypeId, TypeEntity extends Entity_Itf<TypeId>> void applyFetchingStrategy (
            final FetchingStrategy_Abs<TypeId, TypeEntity> p_fetchingStrategy, final List<TypeEntity> p_listeEntity)
   {
      final List<FetchingStrategy_Abs<TypeId, ? extends Entity_Itf<TypeId>>> v_childrenStrategies = p_fetchingStrategy
               .getChildren();
      applyFetchingStrategy(v_childrenStrategies, p_listeEntity);
   }

   /**
    * Applique des fetchingStrategy filles à une liste de Entity.
    * @param p_childrenStrategies
    *           FetchingStrategy
    * @param p_listeEntity
    *           Liste de Entities
    * @param <TypeId>
    *           Type de l'id dans les Entites
    */
   @SuppressWarnings("unchecked")
   private static <TypeId> void applyFetchingStrategy (
            final List<FetchingStrategy_Abs<TypeId, ? extends Entity_Itf<TypeId>>> p_childrenStrategies,
            final List<? extends Entity_Itf<TypeId>> p_listeEntity)
   {
      if (!p_childrenStrategies.isEmpty() && !p_listeEntity.isEmpty())
      {
         for (final FetchingStrategy_Abs<TypeId, ? extends Entity_Itf<TypeId>> v_fetchingStrategy : p_childrenStrategies)
         {
            if (isActive(v_fetchingStrategy))
            {
               final Class<? extends Entity_Itf<TypeId>> v_entityClass = (Class<? extends Entity_Itf<TypeId>>) p_listeEntity
                        .get(0)
                        .getClass();
               final FetchingStrategyFetcher<TypeId> v_fetchingStrategyFetcher = new FetchingStrategyFetcher<>(
                        v_fetchingStrategy, v_entityClass);
               v_fetchingStrategyFetcher.apply(p_listeEntity);
            }
         }
      }
   }

   /**
    * Applique des fetchingStrategy filles à une liste de Entity.
    * @param p_childrenStrategies
    *           FetchingStrategy
    * @param p_entity
    *           Entity
    * @param <TypeId>
    *           Type de l'id dans les Entitiess
    */
   @SuppressWarnings("unchecked")
   private static <TypeId> void applyFetchingStrategy (
            final List<FetchingStrategy_Abs<TypeId, ? extends Entity_Itf<TypeId>>> p_childrenStrategies,
            final Entity_Itf<TypeId> p_entity)
   {
      if (!p_childrenStrategies.isEmpty())
      {
         for (final FetchingStrategy_Abs<TypeId, ? extends Entity_Itf<TypeId>> v_fetchingStrategy : p_childrenStrategies)
         {
            if (isActive(v_fetchingStrategy))
            {
               final Class<? extends Entity_Itf<TypeId>> v_entityClass = (Class<? extends Entity_Itf<TypeId>>) p_entity
                        .getClass();
               final FetchingStrategyFetcher<TypeId> v_fetchingStrategyFetcher = new FetchingStrategyFetcher<>(
                        v_fetchingStrategy, v_entityClass);
               v_fetchingStrategyFetcher.apply(p_entity);
            }
         }
      }
   }

   /**
    * Est-ce que la fetchingStrategy est active (non null et marquée comme "fetch") ?
    * @param <TypeId>
    *           Type de l'id dans les Entities
    * @param p_fetchingStrategy
    *           FetchingStrategy
    * @return boolean
    */
   private static <TypeId> boolean isActive (
            final FetchingStrategy_Abs<TypeId, ? extends Entity_Itf<TypeId>> p_fetchingStrategy)
   {
      return p_fetchingStrategy != null && p_fetchingStrategy.isFetch();
   }

   /**
    * Application de la fetchingStrategy à tous les Entities de la liste en paramètre.
    * @param p_listeEntity
    *           Liste de Entities
    */
   private void apply (final List<? extends Entity_Itf<TypeId>> p_listeEntity)
   {
      for (final Entity_Itf<TypeId> v_entity : p_listeEntity)
      {
         apply(v_entity);
      }
   }

   /**
    * Application de la fetchingStrategy au Entity en paramètre.
    * @param p_entity
    *           Entity
    */
   @SuppressWarnings("unchecked")
   private void apply (final Entity_Itf<TypeId> p_entity)
   {
      try
      {
         if (!List.class.equals(_getter_method.getReturnType()))
         {
            // Objectif : économiser le nombre de selects unitaires et économiser la mémoire et le réseau en réduisant le nombre d'instances de Entites s'il y a plusieurs fois les mêmes

            // Exemple : pour une liste d'opérations d'infrastructure d'un projet donné (ou de quelques projets),
            // alors le fetch des projets de chaque opération doit éviter d'appeler chaque fois le getter pour charger le Entity :
            // il faut réutiliser la même instance de Entity sans faire de select pour recharger la même chose

            // Solution : si dans l'application de la fetchingStrategy sur une liste de Entity, les getters concernent des entities de même id,
            // alors on évite d'appeler le getter pour charger le Entity et on récupère l'instance de Entity depuis le cache local
            // puis on appelle le setter ce qui a donc le même effet que d'appeler le getter

            // appelle le getter avec id
            final TypeId v_resultId = callGetterForId(p_entity);
            // regarde le cache locale selon l'id
            final Object v_result = getFromLocalCache(v_resultId);
            if (v_result != null)
            {
               // si entity trouvé en cache, on appelle le setter et c'est tout
               // (pas besoin d'appliquer les fetchingStrategy filles puisque cela a déjà été fait pour ce Entity fils)
               callSetter(p_entity, v_result);
               return;
            }
            // sinon on fait la suite pour appeler le getter
         }

         final Object v_result = callGetter(p_entity);
         if (v_result != null)
         {
            if (v_result instanceof Entity_Itf)
            {
               final Entity_Itf<TypeId> v_childEntity = (Entity_Itf<TypeId>) v_result;
               // stocke le Entity dans le cache locale à l'instance pour ne pas le recharger si on rencontre le même plus tard dans ce fetcher
               putInLocalCache(v_childEntity);

               // applique les fetchingStrategy filles au Entity fils
               FetchingStrategyFetcher.applyFetchingStrategy(_childrenStrategies, v_childEntity);
            }
            else if (v_result instanceof List)
            {
               final List<Entity_Itf<TypeId>> v_listEntity = (List<Entity_Itf<TypeId>>) v_result;
               // applique les fetchingStrategy filles aux Entites filles
               FetchingStrategyFetcher.applyFetchingStrategy(_childrenStrategies, v_listEntity);
            }
            else
            {
               throw new IllegalStateException("Type de résultat du getter non géré par la FetchingStrategy : "
                        + v_result);
            }
         }
      }
      catch (final RuntimeException v_ex)
      {
         throw v_ex;
      }
      catch (final Exception v_ex)
      {
         throw new RuntimeException(v_ex);
      }
   }

   /**
    * Stocke une Entity dans le cache de cette instance de fetcher.
    * @param p_childEntity
    *           Entity_Itf
    */
   private void putInLocalCache (final Entity_Itf<TypeId> p_childEntity)
   {
      _loadedEntities.put(p_childEntity.getId(), p_childEntity);
   }

   /**
    * Retourne une Entity depuis dans le cache s'il est présent ou null sinon.
    * @param p_entityId
    *           TypeId
    * @return Entity_Itf
    */
   private Entity_Itf<TypeId> getFromLocalCache (final TypeId p_entityId)
   {
      return _loadedEntities.get(p_entityId);
   }

   /**
    * Appelle le getter correspondant à cette fetchingStrategy sur le Entity en paramètre.
    * @param p_entity
    *           Entity
    * @return Résultat du getter (null ou Entity ou List)
    * @throws Exception
    *            e
    */
   private Object callGetter (final Entity_Itf<TypeId> p_entity) throws Exception 
   {
      return _getter_method.invoke(p_entity, (Object[]) null);
   }

   /**
    * Appelle le getter de l'id correspondant à cette fetchingStrategy sur le Entity en paramètre.
    * @param p_entity
    *           entity
    * @return L'id en résultat du getter
    * @throws Exception
    *            e
    */
   @SuppressWarnings("unchecked")
   private TypeId callGetterForId (final Entity_Itf<TypeId> p_entity) throws Exception 
   {
      return (TypeId) _getter_method_for_id.invoke(p_entity, (Object[]) null);
   }

   /**
    * Appelle le setter correspondant à cette fetchingStrategy sur le Entity en paramètre.
    * @param p_entity
    *           Entity
    * @param p_value
    *           Valeur en paramètre du setter
    * @throws Exception
    *            e
    */
   private void callSetter (final Entity_Itf<TypeId> p_entity, final Object p_value) throws Exception 
   {
      _setter_method.invoke(p_entity, p_value);
   }
}
