/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import fr.spi4j.persistence.entity.ColumnsNames_Itf;

/**
 * Méthodes utilitaires pour la couche DAO.
 * @author MINARM
 */
public final class DaoUtils
{
   /**
    * Constructeur privé : pas d'instance.
    */
   private DaoUtils ()
   {
      super();
   }

   /**
    * Permet de retourner le nombre d'occurrences de la table, filtrées par des critères.
    * @param p_dao
    *           Une instance de DAO lié à une table pour exécuter la requête (ParamPersistenceGen_Abs.getUserPersistence().getPersonneDao())
    * @return Le nombre d'occurrences de la table
    */
   public static Integer getCount (final Dao_Itf<?, ?, ?> p_dao)
   {
      return getCount(p_dao, p_dao.getTableName(), "", null);
   }

   /**
    * Permet de retourner le nombre d'occurrences de la table, filtrées par des critères.
    * @param p_dao
    *           Une instance de DAO pour exécuter la requête (ParamPersistenceGen_Abs.getUserPersistence().getDefaultDao())
    * @param p_tableName
    *           Le nom de la table
    * @param p_tableCriteria
    *           Criteres de filtre
    * @return Le nombre d'occurrences de la table
    */
   public static Integer getCount (final Dao_Itf<?, ?, ?> p_dao, final String p_tableName,
            final TableCriteria<?> p_tableCriteria)
   {
      return getCount(p_dao, p_tableName, p_tableCriteria.getCriteriaSql(), p_tableCriteria.getMapValue());
   }

   /**
    * Permet de retourner le nombre d'occurrences de la table, filtrées par des critères.
    * @param p_dao
    *           Une instance de DAO pour exécuter la requête (ParamPersistenceGen_Abs.getUserPersistence().getDefaultDao())
    * @param p_tableName
    *           Le nom de la table
    * @param p_sqlCriteria
    *           Criteres de filtre avec éventuellement des paramètres nommés
    * @param p_map_value_by_name
    *           Map des valeurs des paramètres selon les noms de paramètres (non obligatoire).
    * @return Le nombre d'occurrences de la table
    */
   public static Integer getCount (final Dao_Itf<?, ?, ?> p_dao, final String p_tableName, final String p_sqlCriteria,
            final Map<String, ? extends Object> p_map_value_by_name)
   {
      Integer v_result;

      // Construire la requête SQL
      final String v_request = "SELECT COUNT(*) COUNT FROM " + p_tableName + ' ' + p_sqlCriteria;
      final Cursor_Abs v_Cursor = p_dao.executeQuery(v_request, p_map_value_by_name);
      if (v_Cursor != null)
      {
         try
         {
            v_Cursor.next();
            v_result = v_Cursor.getInteger("COUNT");
         }
         finally
         {
            v_Cursor.close();
         }
      }
      else
      {
         v_result = Integer.valueOf(0);
      }
      return v_result;
   }

   /**
    * Permet de retourner le nombre d'occurrences de la table, filtrées par des critères.
    * @param p_dao
    *           Une instance de DAO pour exécuter la requête (ParamPersistenceGen_Abs.getUserPersistence().getDefaultDao())
    * @param p_column
    *           (In)(*) Le paramètre 'column' (obligatoire).
    * @param p_value
    *           (In) La valeur recherchée dans p_column, valeur qui peut être une Entity
    * @return Le nombre d'occurrences de la table
    */
   public static Integer getCount (final Dao_Itf<?, ?, ?> p_dao, final ColumnsNames_Itf p_column, final Object p_value)
   {
      if (p_column == null)
      {
         throw new IllegalArgumentException("Le paramètre 'column' est obligatoire, il ne peut pas être 'null'\n");
      }
      final String v_sqlCriteria;
      final Map<String, Object> v_map_valueByLogicalName;
      if (p_value == null)
      {
         v_sqlCriteria = "where " + p_column.getPhysicalColumnName() + " is null";
         v_map_valueByLogicalName = Collections.emptyMap();
      }
      else
      {
         if (p_value instanceof Collection)
         {
            v_sqlCriteria = "where " + p_column.getPhysicalColumnName() + " in :" + p_column.getLogicalColumnName();
         }
         else
         {
            v_sqlCriteria = "where " + p_column.getPhysicalColumnName() + " = :" + p_column.getLogicalColumnName();
         }
         v_map_valueByLogicalName = Collections.singletonMap(p_column.getLogicalColumnName(), p_value);
      }
      return getCount(p_dao, p_column.getTableName(), v_sqlCriteria, v_map_valueByLogicalName);
   }
}
