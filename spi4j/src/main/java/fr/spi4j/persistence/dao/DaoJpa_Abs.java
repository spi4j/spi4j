/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.persistence.resource.ResourceManager_Abs;
import fr.spi4j.persistence.resource.jdbc.JdbcResourceManager_Itf;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Classe abstraite commune à tous les DAO JPA.
 * @author MINARM, inspiré de code écrit par CNE Robin
 * @param <TypeId>
 *           Type de l'identifiant de l'entity
 * @param <TypeEntity>
 *           Type de l'entity
 * @param <Columns_Enum>
 *           Type de l'énumération des colonnes
 */
public abstract class DaoJpa_Abs<TypeId, TypeEntity extends Entity_Itf<TypeId>, Columns_Enum extends ColumnsNames_Itf>
         implements Dao_Itf<TypeId, TypeEntity, Columns_Enum>
{
   private static final Logger c_log = LogManager.getLogger(DaoJpa_Abs.class);

   // TODO Une question ouverte est quel est l'intérêt de _EntityManagerFactory et _JdbcResourceManager ici par rapport au @PersistenceContext utilisé pour l'instant,
   // lorsqu'il y a déjà un conteneur EJB ?
   // à noter que le code écrit dans cette classe est supposé fonctionner mais reste à être finalisé et à être testé

   /**
    * L'ensemble des factory de sessions JPA associées à un gestionnaire de ressource. Instancier un EntityManagerFactory par programmation en lui fournissant un DataSourceAdapter pour façader le gestionnaire de ressource.
    */
   private static Map<ResourceManager_Abs, EntityManagerFactory> map_entityManagerFactory = new HashMap<>();

   /** La factory de session JPA associé au DAO. */
   private EntityManagerFactory _EntityManagerFactory;

   /** Le gestionnaire de ressource associée au DAO. */
   private JdbcResourceManager_Itf _JdbcResourceManager;

   @PersistenceContext
   private EntityManager _entityManager;

   /** Le nom de la table. */
   private final String _tableName;

   private final Class<TypeEntity> _entityClass;

   /**
    * Constructeur.
    * @param p_tableName
    *           (In) Nom de la table liée à ce DAO
    * @param p_entityClass
    *           (In) Classe d'implémentation de l'entity
    */
   public DaoJpa_Abs (final String p_tableName, final Class<TypeEntity> p_entityClass)
   {
      super();
      if (p_tableName == null)
      {
         throw new IllegalArgumentException("Le paramètre 'tableName' est obligatoire, il ne peut pas être 'null'\n");
      }
      if (p_entityClass == null)
      {
         throw new IllegalArgumentException("Le paramètre 'entityClass' est obligatoire, il ne peut pas être 'null'\n");
      }
      _tableName = p_tableName;
      // il est supposé ici que les DAO JPA générés fourniront la classe exact de l'implémentation de l'entity
      // (TypeEntity n'étant le type générique que de l'interface de l'entity)
      _entityClass = p_entityClass;
      // note : pour l'instant, le constructeur ne prend pas en paramètre un tableau contenant la liste des colonnes issues de l'énumération pour l'entity
      // mais cela pourrait se faire comme pour DaoJdbc_Abs
   }

   @Override
   public String getTableName ()
   {
      return _tableName;
   }

   @Override
   public ResourceManager_Abs getResourceManager ()
   {
      if (_JdbcResourceManager == null)
      {
         throw new Spi4jRuntimeException(null, "Aucun gestionnaire de ressource n'as pas été associé au DAO: "
                  + this.getClass(), "");
      }
      return (ResourceManager_Abs) _JdbcResourceManager;
   }

   @Override
   public void setResourceManager (final ResourceManager_Abs p_ResourceManager)
   {
      if (p_ResourceManager instanceof JdbcResourceManager_Itf)
      {
         _JdbcResourceManager = (JdbcResourceManager_Itf) p_ResourceManager;
      }
      else
      {
         throw new IllegalArgumentException("Le gestionnaire de ressource n'est pas compatible: ");
      }

      final EntityManagerFactory v_EntityManagerFactory = map_entityManagerFactory.get(p_ResourceManager);
      if (v_EntityManagerFactory == null)
      {
         // final DataSourceAdapter v_DataSourceAdapter = new DataSourceAdapter(_JdbcResourceManager);
         //
         // v_EntityManagerFactory = new EntityManagerFactoryImpl();
         //
         // map_entityManagerFactory.put(p_ResourceManager, v_EntityManagerFactory);

         throw new UnsupportedOperationException(
                  "Reste A Implémenter: Instancier ici un EntityManagerFactory par programmation");
      }
      _EntityManagerFactory = v_EntityManagerFactory;
   }

   /**
    * @return la factory du gestionnaire d'entités.
    */
   protected EntityManagerFactory getEntityManagerFactory ()
   {
      return _EntityManagerFactory;
   }

   @Override
   public void create (final TypeEntity p_Entity) throws Spi4jValidationException
   {
      // validation de l'entity passé en paramètre
      p_Entity.validate();

      _entityManager.persist(p_Entity);
   }

   @Override
   public TypeEntity findById (final TypeId p_id)
   {
      if (p_id == null)
      {
         throw new Spi4jRuntimeException("La clé primaire de l'entité n'est pas renseigné", "???");
      }
      return _entityManager.find(_entityClass, p_id);
   }

   @Override
   public void update (final TypeEntity p_Entity) throws Spi4jValidationException
   {
      // validation de l'entity passé en paramètre
      p_Entity.validate();

      _entityManager.merge(p_Entity);
   }

   @Override
   public void delete (final TypeEntity p_Entity)
   {
      // "t" est détaché
      // avec un "find" on récupère une instance attachée
      final TypeEntity v_attachedEntity = _entityManager.find(_entityClass, p_Entity.getId());

      // que l'on peut supprimer
      _entityManager.remove(v_attachedEntity);
   }

   @Override
   public int deleteByCriteria (final TableCriteria<Columns_Enum> p_tableCriteria)
   {
      final String v_description = p_tableCriteria.getDescription();
      if (!v_description.isEmpty())
      {
         c_log.debug("Exécution de deleteByCriteria avec le critère : " + v_description);
      }
      final String v_criteriaSql = p_tableCriteria.getCriteriaSql();
      final Map<String, Object> v_mapValue = p_tableCriteria.getMapValue();
      return executeUpdate("delete from " + getTableName() + v_criteriaSql, v_mapValue);
   }

   @Override
   public int deleteAll ()
   {
      final String v_deleteAllQuery = "delete from " + _tableName;
      return executeUpdate(v_deleteAllQuery, null);
   }

   @Override
   public List<TypeEntity> findAll ()
   {
      return findByCriteria("", null, -1, 0);

      // Remarque : on aurait aussi pu faire un criteria de JPA 2 comme ceci
      // final CriteriaBuilder qb = _entityManager.getCriteriaBuilder();
      // final CriteriaQuery<typee> c = qb.createQuery(_entityClass);
      // final Root<T> p = c.from(_entityClass);
      // final TypedQuery<T> q = _entityManager.createQuery(c);
      // return q.getResultList();
   }

   @Override
   public List<TypeEntity> findAll (final Columns_Enum p_orderByColumn)
   {
      final TableCriteria<Columns_Enum> v_tableCriteria = new TableCriteria<>("");
      v_tableCriteria.addOrderByAsc(p_orderByColumn);
      return findByCriteria(v_tableCriteria);
   }

   @Override
   public List<TypeEntity> findByColumn (final Columns_Enum p_column, final Object p_value)
   {
      final TableCriteria<Columns_Enum> v_tableCriteria = new TableCriteria<>("");
      v_tableCriteria.addCriteria(p_column, Operator_Enum.equals, p_value);
      return findByCriteria(v_tableCriteria);
   }

   @Override
   public List<TypeEntity> findByCriteria (final TableCriteria<Columns_Enum> p_tableCriteria)
   {
      final String v_description = p_tableCriteria.getDescription();
      if (!v_description.isEmpty())
      {
         c_log.debug("Exécution de findByCriteria avec le critère : " + v_description);
      }
      // remarque: lorsque l'implémentation JPA/JPL du DAO sera finalisée, on pourrait ici ajouter et utiliser dans cette implémentation
      // p_tableCriteria.getCriteriaJpql() ou p_tableCriteria.getCriteriaJpa() (javax.persistence.criteria) à la place de p_tableCriteria.getCriteriaSql()
      return findByCriteria(p_tableCriteria.getCriteriaSql(), p_tableCriteria.getMapValue(),
               p_tableCriteria.getNbLignesMax(), p_tableCriteria.getNbLignesStart());
   }

   @Override
   public List<TypeEntity> findByCriteria (final String p_sqlCriteria,
            final Map<String, ? extends Object> p_map_value_by_name)
   {
      return findByCriteria(p_sqlCriteria, p_map_value_by_name, -1, 0);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<TypeEntity> findByCriteria (final String p_sqlCriteria,
            final Map<String, ? extends Object> p_map_value_by_name, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      if (p_sqlCriteria == null)
      {
         throw new IllegalArgumentException("Le paramètre 'sqlCriteria' est obligatoire, il ne peut pas être 'null'\n");
      }

      final String v_SelectQuery = "select " + _tableName + ".* from " + _tableName + ' ' + p_sqlCriteria;
      final Query v_nativeQuery = _entityManager.createNativeQuery(v_SelectQuery, _entityClass);

      for (final Map.Entry<String, ? extends Object> v_entry : p_map_value_by_name.entrySet())
      {
         v_nativeQuery.setParameter(v_entry.getKey(), v_entry.getValue());
      }
      if (p_nbLignesStart > 0)
      {
         v_nativeQuery.setFirstResult(p_nbLignesStart);
      }
      if (p_nbLignesMax >= 0)
      {
         v_nativeQuery.setMaxResults(p_nbLignesMax);
      }

      final List<TypeEntity> v_result = v_nativeQuery.getResultList();

      return v_result;
   }

   @Override
   public int executeUpdate (final String p_sqlQuery, final Map<String, ? extends Object> p_map_value_by_name,
            final String... p_commentaires)
   {
      if (p_sqlQuery == null)
      {
         throw new IllegalArgumentException("Le paramètre 'sqlQuery' est obligatoire, il ne peut pas être 'null'\n");
      }
      // a colocaliser avec le log du code SQL
      c_log.debug("Exécution de executeUpdate");
      for (final String v_commentaire : p_commentaires)
      {
         c_log.debug(v_commentaire);
      }
      final Query v_nativeQuery = _entityManager.createNativeQuery(p_sqlQuery);
      for (final Map.Entry<String, ? extends Object> v_entry : p_map_value_by_name.entrySet())
      {
         v_nativeQuery.setParameter(v_entry.getKey(), v_entry.getValue());
      }
      return v_nativeQuery.executeUpdate();
   }

   @Override
   public Cursor_Abs executeQuery (final String p_sqlQuery, final Map<String, ? extends Object> p_map_value_by_name,
            final String... p_commentaires)
   {
      return executeQuery(p_sqlQuery, p_map_value_by_name, -1, 0, p_commentaires);
   }

   @Override
   public Cursor_Abs executeQuery (final String p_sqlQuery, final Map<String, ? extends Object> p_map_value_by_name,
            final int p_nbLignesMax, final int p_nbLignesStart, final String... p_commentaires)
   {
      if (p_sqlQuery == null)
      {
         throw new IllegalArgumentException("Le paramètre 'sqlQuery' est obligatoire, il ne peut pas être 'null'\n");
      }
      c_log.debug("Exécution de executeQuery");
      for (final String v_commentaire : p_commentaires)
      {
         c_log.debug(v_commentaire);
      }
      final Query v_nativeQuery = _entityManager.createNativeQuery(p_sqlQuery);
      for (final Map.Entry<String, ? extends Object> v_entry : p_map_value_by_name.entrySet())
      {
         v_nativeQuery.setParameter(v_entry.getKey(), v_entry.getValue());
      }
      if (p_nbLignesStart > 0)
      {
         v_nativeQuery.setFirstResult(p_nbLignesStart);
      }
      if (p_nbLignesMax >= 0)
      {
         v_nativeQuery.setMaxResults(p_nbLignesMax);
      }
      // final List<?> v_resultList =
      v_nativeQuery.getResultList();
      // à corriger
      return null;
   }
}
