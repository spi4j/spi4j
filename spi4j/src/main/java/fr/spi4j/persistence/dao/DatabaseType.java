/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Enumération du type de base de données.
 * @author MINARM
 */
public enum DatabaseType
{
   /** Oracle : jdbc:oracle:thin:@pcdcsid.sid.defense.gouv.fr:1521:dac1 */
   ORACLE(true),
   /** Mysql : jdbc:mysql://localhost:3306/dac1 */
   MYSQL(true),
   /**
    * MS SQLServer : jdbc:jtds:sqlserver://localhost:1433/dac1 ou bien jdbc:sqlserver://localhost;databaseName=dac1 selon le choix du driver
    */
   SQLSERVER(false),
   /** PostgreSQL : jdbc:postgresql://localhost/dac1 */
   POSTGRESQL(true),
   /** H2 : jdbc:h2:./appwhite1-webapp/target/dbfile */
   H2(true),
   /** HSQLDB : jdbc:hsqldb:mem: ou jdbc:hsqldb:file: */
   HSQLDB(true),
   /** ODBC : jdbc:odbc:name */
   ODBC(false);

   /**
    * Paramètre de requête paginée : start.
    */
   public static final String c_limitStartParameter = "spi4j_limit_start";

   /**
    * Paramètre de requête paginée : max.
    */
   public static final String c_limitMaxParameter = "spi4j_limit_max";

   private static final Logger c_log = LogManager.getLogger(DatabaseType.class);

   private final boolean _optimizedLimitQuery;

   /**
    * Constructeur.
    * @param p_optimizedLimitQuery
    *           true si la requête "limit" sera optimisée, false sinon
    */
   private DatabaseType (final boolean p_optimizedLimitQuery)
   {
      _optimizedLimitQuery = p_optimizedLimitQuery;
   }

   /**
    * @return true si la requête "limit" a été optimisée, false sinon.
    */
   public boolean is_optimizedLimitQuery ()
   {
      return _optimizedLimitQuery;
   }

   /**
    * Recherche le type de base de données en fonction de l'url de la connexion à la base.
    * @param p_connection
    *           la connexion à la base
    * @return le type de base de données
    */
   public static DatabaseType findTypeForConnection (final Connection p_connection)
   {
      final String v_jdbcUrl = getJdbcUrl(p_connection);
      final String[] v_split = v_jdbcUrl.split(":");
      if (v_split.length >= 2 && "jdbc".equalsIgnoreCase(v_split[0]))
      {
         final String v_driverType = v_split[1];
         // on recherche d'abord dans l'énumération un type de base de données qui a le même nom que dans l'url jdbc utilisée par la connexion
         for (final DatabaseType v_dbType : DatabaseType.values())
         {
            if (v_dbType.toString().equalsIgnoreCase(v_driverType))
            {
               return v_dbType;
            }
         }
         if ("jtds".equalsIgnoreCase(v_driverType) && "sqlserver".equalsIgnoreCase(v_split[2]))
         {
            // le driver jtds pour sqlserver est un cas spécifique qui ne correspond pas à une énumération
            // (note: ne pas confondre le driver "jtds" pour sqlserver et le driver "sqlserver" de Microsoft)
            return SQLSERVER;
         }
      }
      throw new Spi4jRuntimeException("Type de base de données inconnu pour la connexion " + v_jdbcUrl,
               "Définir le type de base de données pour la connexion " + v_jdbcUrl);
   }

   /**
    * Retourne l'url jdbc utilisé pour établir la connexion.
    * @param p_connection
    *           Connection
    * @return String
    */
   private static String getJdbcUrl (final Connection p_connection)
   {
      final String v_jdbcUrl;
      try
      {
         // cet appel à getMetaData().getURL() instancie certainement l'instance d'un objet implémentant DatabaseMetaData
         // mais ne fait pas a priori de requête sql ni d'appel réseau juste pour avoir l'URL
         // (en tout cas dans le driver Oracle, cela ne fait aucun appel réseau)
         v_jdbcUrl = p_connection.getMetaData().getURL();
      }
      catch (final SQLException v_ex)
      {
         throw new RuntimeException(v_ex.getMessage(), v_ex);
      }
      return v_jdbcUrl;
   }

   /**
    * Construit une requête paginée en oracle.
    * @param p_sql
    *           le sql d'origine
    * @param p_nbLignesMax
    *           le nombre maximum de lignes retournées
    * @param p_nbLignesStart
    *           l'indice de début de recherche
    * @return la requête paginée
    */
   public String getLimitQuery (final String p_sql, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      switch (this)
      {
      case ORACLE:
         return getOracleLimitQuery(p_sql, p_nbLignesMax, p_nbLignesStart);
      case MYSQL:
         return getMysqlLimitQuery(p_sql, p_nbLignesMax, p_nbLignesStart);
      case POSTGRESQL:
         return getPostgresqlLimitQuery(p_sql, p_nbLignesMax, p_nbLignesStart);
      case SQLSERVER:
         return getSqlserverLimitQuery(p_sql, p_nbLignesMax, p_nbLignesStart);
      case HSQLDB:
         return getHsqldbLimitQuery(p_sql, p_nbLignesMax, p_nbLignesStart);
      case H2:
         return getH2LimitQuery(p_sql, p_nbLignesMax, p_nbLignesStart);
      default:
         return p_sql;
      }
   }

   /**
    * Construit une requête paginée en oracle.
    * @param p_sql
    *           le sql d'origine
    * @param p_nbLignesMax
    *           le nombre maximum de lignes retournées
    * @param p_nbLignesStart
    *           l'indice de début de recherche
    * @return la requête paginée
    */
   private static String getOracleLimitQuery (final String p_sql, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      String v_sql = p_sql.trim();
      String v_forUpdateClause = null;
      boolean v_isForUpdate = false;
      final int v_forUpdateIndex = v_sql.toLowerCase().lastIndexOf("for update");
      if (v_forUpdateIndex > -1)
      {
         // save 'for update ...' and then remove it
         v_forUpdateClause = v_sql.substring(v_forUpdateIndex);
         v_sql = v_sql.substring(0, v_forUpdateIndex - 1);
         v_isForUpdate = true;
      }
      final StringBuilder v_pagingSelect = new StringBuilder(v_sql.length() + 100);
      if (p_nbLignesStart > 0)
      {
         v_pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
      }
      else
      {
         v_pagingSelect.append("select * from ( ");
      }
      v_pagingSelect.append(v_sql);
      if (p_nbLignesStart > 0)
      {
         v_pagingSelect.append(" ) row_ where rownum <= :").append(c_limitMaxParameter).append(" + :")
                  .append(c_limitStartParameter).append(") where rownum_ > :").append(c_limitStartParameter);
      }
      else
      {
         v_pagingSelect.append(" ) where rownum <= :").append(c_limitMaxParameter);
      }

      if (v_isForUpdate)
      {
         v_pagingSelect.append(' ');
         v_pagingSelect.append(v_forUpdateClause);
      }
      return v_pagingSelect.toString();
   }

   /**
    * Construit une requête paginée en mysql.
    * @param p_sql
    *           le sql d'origine
    * @param p_nbLignesMax
    *           le nombre maximum de lignes retournées
    * @param p_nbLignesStart
    *           l'indice de début de recherche
    * @return la requête paginée
    */
   private static String getMysqlLimitQuery (final String p_sql, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      return new StringBuilder(p_sql).append(" limit :").append(c_limitStartParameter).append(", :")
               .append(c_limitMaxParameter).toString();
   }

   /**
    * Construit une requête paginée en postgresql.
    * @param p_sql
    *           le sql d'origine
    * @param p_nbLignesMax
    *           le nombre maximum de lignes retournées
    * @param p_nbLignesStart
    *           l'indice de début de recherche
    * @return la requête paginée
    */
   private static String getPostgresqlLimitQuery (final String p_sql, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      // identique à h2
      return new StringBuilder(p_sql).append(" limit :").append(c_limitMaxParameter).append(" offset :")
               .append(c_limitStartParameter).toString();
   }

   /**
    * Construit une requête paginée en sql server.
    * @param p_sql
    *           le sql d'origine
    * @param p_nbLignesMax
    *           le nombre maximum de lignes retournées
    * @param p_nbLignesStart
    *           l'indice de début de recherche
    * @return la requête paginée
    */
   private static String getSqlserverLimitQuery (final String p_sql, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      if (p_nbLignesStart > 0)
      {
         c_log.debug("query result offset is not supported for sql server");
      }
      final String v_sqlLowerCase = p_sql.toLowerCase();
      final int v_selectIndex = v_sqlLowerCase.indexOf("select");
      final int v_selectDistinctIndex = v_sqlLowerCase.indexOf("select distinct");
      final int v_indexAfterSelectInsertPoint;
      if (v_selectDistinctIndex == v_selectIndex)
      {
         v_indexAfterSelectInsertPoint = v_selectIndex + 15;
      }
      else
      {
         v_indexAfterSelectInsertPoint = v_selectIndex + 6;
      }
      return new StringBuilder(p_sql).insert(v_indexAfterSelectInsertPoint, " top :" + c_limitMaxParameter).toString();
   }

   /**
    * Construit une requête paginée en h2.
    * @param p_sql
    *           le sql d'origine
    * @param p_nbLignesMax
    *           le nombre maximum de lignes retournées
    * @param p_nbLignesStart
    *           l'indice de début de recherche
    * @return la requête paginée
    */
   private static String getH2LimitQuery (final String p_sql, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      // identique à postgresql
      return new StringBuilder(p_sql).append(" limit :").append(c_limitMaxParameter).append(" offset :")
               .append(c_limitStartParameter).toString();
   }

   /**
    * Construit une requête paginée en hsql.
    * @param p_sql
    *           le sql d'origine
    * @param p_nbLignesMax
    *           le nombre maximum de lignes retournées
    * @param p_nbLignesStart
    *           l'indice de début de recherche
    * @return la requête paginée
    */
   private static String getHsqldbLimitQuery (final String p_sql, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      // fonctionne uniquement pour les versions >= 20
      return new StringBuilder(p_sql).append(" offset :").append(c_limitStartParameter).append(" limit :")
               .append(c_limitMaxParameter).toString();
   }

}
