/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import java.util.List;
import java.util.Map;

import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.persistence.dao.TableCriteria;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Implémentation d'un dao JDBC non lié à une entité (pour faire des requêtes de CRUD sans passer par les entités).
 * @author MINARM
 */
@SuppressWarnings("unused")
// implements Dao_Itf doit être conservé ici sinon le proxy fait dans UserPersistence_Abs.getDefaultDao() après un appel à initDaoProxyFactory ne fonctionnerait pas en l'état
public class DefaultJdbcDao extends DaoJdbc_Abs<Entity_Itf<Long>, ColumnsNames_Itf> implements
         Dao_Itf<Long, Entity_Itf<Long>, ColumnsNames_Itf>
{

   /**
    * Constructeur par défaut (pas de nom de table, pas de colonnes).
    */
   public DefaultJdbcDao ()
   {
      super("", new ColumnsNames_Itf[0]);
   }

   @Override
   public String getTableName ()
   {
      throw error();
   }

   @Override
   public void create (final Entity_Itf<Long> p_Entity) throws Spi4jValidationException
   {
      throw error();
   }

   @Override
   public Entity_Itf<Long> findById (final Long p_id)
   {
      throw error();
   }

   @Override
   public List<Entity_Itf<Long>> findAll ()
   {
      throw error();
   }

   @Override
   public List<Entity_Itf<Long>> findAll (final ColumnsNames_Itf p_orderByColumn)
   {
      throw error();
   }

   @Override
   public void update (final Entity_Itf<Long> p_Entity) throws Spi4jValidationException
   {
      throw error();
   }

   @Override
   public void delete (final Entity_Itf<Long> p_Entity)
   {
      throw error();
   }

   @Override
   public int deleteAll ()
   {
      throw error();
   }

   @Override
   public int deleteByCriteria (final TableCriteria<ColumnsNames_Itf> p_tableCriteria)
   {
      throw error();
   }

   @Override
   public List<Entity_Itf<Long>> findByColumn (final ColumnsNames_Itf p_column, final Object p_value)
   {
      throw error();
   }

   @Override
   public List<Entity_Itf<Long>> findByCriteria (final String p_queryCriteria,
            final Map<String, ? extends Object> p_map_value_by_name)
   {
      throw error();
   }

   @Override
   public List<Entity_Itf<Long>> findByCriteria (final String p_queryCriteria,
            final Map<String, ? extends Object> p_map_value_by_name, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      throw error();
   }

   @Override
   public List<Entity_Itf<Long>> findByCriteria (final TableCriteria<ColumnsNames_Itf> p_tableCriteria)
   {
      throw error();
   }

   @Override
   protected Map<String, Object> getMapValueByLogicalNameFromEntity (final Entity_Itf<Long> p_Entity)
   {
      throw error();
   }

   @Override
   protected Entity_Itf<Long> getEntityFromMapValueByLogicalName (final Map<String, Object> p_map_valueByColumnName)
   {
      throw error();
   }

   /**
    * @return une erreur d'exécution
    */
   private RuntimeException error ()
   {
      return new UnsupportedOperationException("Opération interdite sur le DAO par défaut");
   }

}
