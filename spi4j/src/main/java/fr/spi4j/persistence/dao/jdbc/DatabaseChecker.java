/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.DatabaseType;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;

/**
 * Vérification de la cohérence d'une table en base de données par rapport à la version de l'application Java.
 * @author MINARM
 */
public final class DatabaseChecker
{
   private static final Logger c_log = LogManager.getLogger(DatabaseChecker.class);

   /**
    * Constructeur : pas d'instance.
    */
   private DatabaseChecker ()
   {
      super();
   }

   /**
    * Vérifie la cohérence d'une table en base de données par rapport au classes Java.
    * @param p_dao
    *           DaoJdbc_Abs
    * @throws SQLException
    *            e
    */
   public static void checkDatabaseTable (final DaoJdbc_Abs<?, ?> p_dao) throws SQLException
   {
      try
      {
         p_dao.checkDatabaseTable();
         c_log.info("Check OK, Table " + p_dao.getTableName());
      }
      catch (final Spi4jRuntimeException v_ex)
      {
         c_log.info("Check NOK, Table " + p_dao.getTableName() + ": " + v_ex.getMessage());
         throw v_ex;
      }
   }

   /**
    * Vérifie la cohérence d'une table en base de données par rapport au classes Java.
    * @param p_connection
    *           Connection
    * @param p_tableName
    *           String
    * @param p_columns
    *           ColumnsNames_Itf[]
    * @throws SQLException
    *            e
    */
   static void checkDatabaseTable (final Connection p_connection, final String p_tableName,
            final ColumnsNames_Itf[] p_columns) throws SQLException
   {
      final DatabaseMetaData v_metaData = p_connection.getMetaData();
      final DatabaseType v_databaseType = DatabaseType.findTypeForConnection(p_connection);
      final String v_tableName;
      if (v_databaseType == DatabaseType.POSTGRESQL)
      {
         // dans postgresql, les noms des tables sont transformées de majuscules en minuscules
         // (quand elles ne sont pas entre double-quotes)
         v_tableName = p_tableName.toLowerCase(Locale.ENGLISH);
      }
      else
      {
         v_tableName = p_tableName;
      }
      final ResultSet v_tables = v_metaData.getTables(null, null, v_tableName, null);
      try
      {
         // remarque : mysql sous windows n'est pas sensible à la casse
         if (!v_tables.next() || !v_tables.getString("TABLE_NAME").equalsIgnoreCase(v_tableName))
         {
            throw new Spi4jRuntimeException("Table " + v_tableName + " non trouvée" + " dans la base "
                     + v_metaData.getUserName() + " de " + v_metaData.getURL(),
                     "Contactez l'administrateur pour vérifier les tables dans la base de données");
         }
      }
      finally
      {
         v_tables.close();
      }
      final List<ColumnsNames_Itf> v_columnsOk = new ArrayList<>(p_columns.length);
      final ResultSet v_columns = v_metaData.getColumns(null, null, v_tableName, null);
      try
      {
         while (v_columns.next())
         {
            final String v_columnName = v_columns.getString("COLUMN_NAME");
            final int v_dataType = v_columns.getInt("DATA_TYPE");
            final int v_columnSize = v_columns.getInt("COLUMN_SIZE");
            final int v_nullable = v_columns.getInt("NULLABLE");
            for (final ColumnsNames_Itf v_column : p_columns)
            {
               if (v_column.getPhysicalColumnName().equalsIgnoreCase(v_columnName))
               {
                  if (v_column.isMandatory() != (v_nullable == 0) && !v_column.isId())
                  {
                     throw new Spi4jRuntimeException("Contrainte not null de colonne incorrecte dans la table "
                              + v_tableName + " : " + v_columnName + " dans la base " + v_metaData.getUserName()
                              + " de " + v_metaData.getURL(),
                              "Contactez l'administrateur pour vérifier les tables dans la base de données");
                  }

                  final Class<?> v_typeColumn = v_column.getTypeColumn();
                  if (!isTypeColumnOk(v_dataType, v_typeColumn))
                  {
                     throw new Spi4jRuntimeException("Type de colonne incorrect dans la table " + v_tableName + " : "
                              + v_columnName + " dans la base " + v_metaData.getUserName() + " de "
                              + v_metaData.getURL(),
                              "Contactez l'administrateur pour vérifier les tables dans la base de données");
                  }
                  if (v_column.getSize() != -1 && v_column.getSize() != v_columnSize)
                  {
                     throw new Spi4jRuntimeException("Taille de colonne incorrecte dans la table " + v_tableName
                              + " : " + v_columnName + " (attendu : " + v_column.getSize() + ", constaté : "
                              + v_columnSize + ')' + " dans la base " + v_metaData.getUserName() + " de "
                              + v_metaData.getURL(),
                              "Contactez l'administrateur pour vérifier les tables dans la base de données");
                  }
                  // TODO on pourrait essayer aussi de vérifier si les clés primaires, clés étrangères et index de clés étrangères sont bien présents
                  v_columnsOk.add(v_column);
                  break;
               }
            }
         }
         if (v_columnsOk.size() < p_columns.length)
         {
            final List<String> v_columnsNok = new ArrayList<>();
            for (final ColumnsNames_Itf v_column : p_columns)
            {
               if (!v_columnsOk.contains(v_column))
               {
                  v_columnsNok.add(v_column.getPhysicalColumnName());
               }
            }
            throw new Spi4jRuntimeException("Colonnes manquantes dans la table " + v_tableName + " : " + v_columnsNok
                     + " dans la base " + v_metaData.getUserName() + " de " + v_metaData.getURL(),
                     "Contactez l'administrateur pour vérifier les tables dans la base de données");
         }
      }
      finally
      {
         v_columns.close();
      }
   }

   /**
    * Est-ce que le type SQL de la colonne est cohérent par rapport à la classe Java de la colonne.
    * @param p_dataType
    *           int
    * @param p_typeColumn
    *           Class
    * @return boolean
    */
   private static boolean isTypeColumnOk (final int p_dataType, final Class<?> p_typeColumn)
   {
      return
      // type chaîne de caractères
      p_typeColumn == String.class
               && (p_dataType == Types.VARCHAR || p_dataType == Types.CHAR)
               // type booléen
               || p_typeColumn == Boolean.class
               && (p_dataType == Types.DECIMAL || p_dataType == Types.NUMERIC || p_dataType == Types.BIT || p_dataType == Types.BOOLEAN)
               // types numériques
               || (p_dataType == Types.DECIMAL || p_dataType == Types.NUMERIC || p_dataType == Types.BIGINT)
               && (p_typeColumn == Long.class || p_typeColumn == Integer.class || p_typeColumn == Double.class
                        || p_typeColumn == Float.class || p_typeColumn == BigDecimal.class || p_typeColumn == Byte.class)
               // type date
               || p_typeColumn == Date.class
               && (p_dataType == Types.DATE || p_dataType == Types.TIMESTAMP || p_dataType == Types.TIME)
               // type blob
               || p_typeColumn == Blob.class && p_dataType == Types.BLOB;
   }
}
