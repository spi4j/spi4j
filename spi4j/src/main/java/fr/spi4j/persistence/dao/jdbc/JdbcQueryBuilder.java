/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.DatabaseType;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Classe interne à l'implémentation JDBC et permettant de construire des requêtes sql simples.
 * @author MINARM
 * @param <TypeEntity>
 *           Type d'entity
 */
class JdbcQueryBuilder<TypeEntity extends Entity_Itf<Long>>
{
   private final String _tableName;

   private final ColumnsNames_Itf[] _columnNames;

   /**
    * Constructeur.
    * @param p_tableName
    *           Nom de la table
    * @param p_columnNames
    *           Tableau de l'énumération des colonnes dans cette table
    */
   JdbcQueryBuilder (final String p_tableName, final ColumnsNames_Itf[] p_columnNames)
   {
      super();
      if (p_tableName == null)
      {
         throw new IllegalArgumentException("Le paramètre 'tableName' est obligatoire, il ne peut pas être 'null'\n");
      }
      if (p_columnNames == null)
      {
         throw new IllegalArgumentException("Le paramètre 'columnNames' est obligatoire, il ne peut pas être 'null'\n");
      }
      _tableName = p_tableName;
      _columnNames = p_columnNames;
   }

   /**
    * Retourne le nom de la colonne servant de clé primaire.
    * @return String
    */
   String getPkColumnName ()
   {
      for (final ColumnsNames_Itf v_column : _columnNames)
      {
         if (v_column.isId())
         {
            return v_column.getPhysicalColumnName();
         }
      }
      throw new Spi4jRuntimeException("Aucune colonne 'PRIMARY KEY' n'a été trouvé dans la table " + _tableName,
               "Vérifier la modélisation de la table " + _tableName);
   }

   /**
    * Retourne le nom de la séquence servant en Oracle/Postgresql/H2 pour obtenir la valeur de la clé primaire.
    * @return String
    */
   String getSequenceName ()
   {
      // On récupère seulement les 26 premiers caractères du nom de la table car c'est ainsi que PacMan génère la séquence (voir fr.pacman.entity.api.sql.common.mtl)
      return _tableName.substring(0, Math.min(_tableName.length(), 26)) + "_SEQ";
   }

   /**
    * Requête sql d'insert unitaire.
    * @param p_databaseType
    *           Le type de base de données
    * @param p_withSequence
    *           Utilisation de la séquence ou non
    * @return String
    */
   private String getInsertQuery (final DatabaseType p_databaseType, final boolean p_withSequence)
   {
      final String v_pkName = getPkColumnName();
      final String v_sequenceName = getSequenceName();
      final StringBuilder v_sb = new StringBuilder();
      v_sb.append("insert into ").append(_tableName).append(" (");
      if (!p_withSequence || p_databaseType != DatabaseType.MYSQL)
      {
         // ni id, ni nextval en mysql car id en autoincrement
         v_sb.append(v_pkName).append(", ");
      }
      for (final ColumnsNames_Itf v_columnName : _columnNames)
      {
         if (!v_pkName.equalsIgnoreCase(v_columnName.getPhysicalColumnName()))
         {
            v_sb.append(v_columnName.getPhysicalColumnName()).append(", ");
         }
      }
      v_sb.delete(v_sb.length() - 2, v_sb.length());
      v_sb.append(") values (");
      if (p_withSequence)
      {
         if (p_databaseType == DatabaseType.POSTGRESQL)
         {
            // on utilise nextval('sequence') si connexion postgresql
            v_sb.append("nextval('").append(v_sequenceName).append("'), ");
         }
         else if (p_databaseType != DatabaseType.MYSQL)
         {
            // on utilise sequence.nextval si connexion Oracle ou H2,
            // et ni id, ni nextval en mysql car id en autoincrement
            v_sb.append(v_sequenceName).append(".nextval, ");
         }
      }
      for (final ColumnsNames_Itf v_columnName : _columnNames)
      {
         if (!p_withSequence || !v_pkName.equalsIgnoreCase(v_columnName.getPhysicalColumnName()))
         {
            v_sb.append(':').append(v_columnName.getLogicalColumnName()).append(", ");
         }
      }
      v_sb.delete(v_sb.length() - 2, v_sb.length());
      v_sb.append(")");
      return v_sb.toString();
   }

   /**
    * Requête sql d'insert unitaire en utilisant une séquence pour la gestion de la clé primaire.
    * @param p_databaseType
    *           Le type de base de données
    * @return String
    */
   String getInsertQuery (final DatabaseType p_databaseType)
   {
      return getInsertQuery(p_databaseType, true);
   }

   /**
    * Requête sql d'insert unitaire sans utilsiation de séquence pour la gestion de la clé primaire.
    * @param p_databaseType
    *           Le type de base de données
    * @return String
    */
   String getInsertQueryWithoutSequence (final DatabaseType p_databaseType)
   {
      return getInsertQuery(p_databaseType, false);
   }

   /**
    * Requête sql d'update unitaire.
    * @return String
    */
   String getUpdateQuery ()
   {
      final String v_pkName = getPkColumnName();
      final StringBuilder v_sb = new StringBuilder();
      v_sb.append("update ").append(_tableName).append(" set ");
      // tant qu'aucune valeur n'est modifiée dans la requête, alors celle-ci ne sera pas correctement générée
      boolean v_correctRequest = false;
      for (final ColumnsNames_Itf v_columnName : _columnNames)
      {
         if (!v_pkName.equalsIgnoreCase(v_columnName.getPhysicalColumnName()))
         {
            v_sb.append(v_columnName.getPhysicalColumnName()).append(" = :")
                     .append(v_columnName.getLogicalColumnName()).append(", ");
            v_correctRequest = true;
         }
      }
      // Si la requête est incorrecte, lever une exception
      if (!v_correctRequest)
      {
         throw new Spi4jRuntimeException(
                  "La requête SQL UPDATE générée est incorrecte car l'entité mise à jour ne possède aucune colonne à part son identifiant",
                  "Ajouter des attributs à l'entité et regénérer le code de l'application");
      }
      v_sb.delete(v_sb.length() - 2, v_sb.length());
      v_sb.append(" where ");
      v_sb.append(v_pkName).append(" = :id");
      return v_sb.toString();
   }

   /**
    * Requête sql d'update unitaire avec gestion de version.
    * @param p_Entity
    *           (In)(*) Le tuple à mettre à jour est obligatoire.
    * @return String
    */
   String getVersionnedUpdateQuery (final TypeEntity p_Entity)
   {
      final StringBuilder v_sb = new StringBuilder();
      v_sb.append(getUpdateQuery());
      // gestion du lock optimiste
      appendVersionConstraint(v_sb, p_Entity);
      return v_sb.toString();
   }

   /**
    * Requête sql de delete unitaire.
    * @return String
    */
   String getDeleteQuery ()
   {
      final String v_pkName = getPkColumnName();
      final StringBuilder v_sb = new StringBuilder();
      v_sb.append("delete from ").append(_tableName);
      v_sb.append(" where ");
      v_sb.append(v_pkName).append(" = :id");
      return v_sb.toString();
   }

   /**
    * Requête sql de delete unitaire avec gestion de version.
    * @param p_Entity
    *           (In)(*) Le tuple à supprimer est obligatoire.
    * @return String
    */
   String getVersionnedDeleteQuery (final TypeEntity p_Entity)
   {
      final StringBuilder v_sb = new StringBuilder();
      v_sb.append(getDeleteQuery());
      // gestion du lock optimiste
      appendVersionConstraint(v_sb, p_Entity);
      return v_sb.toString();
   }

   /**
    * Ajoute une contrainte dans la requête pour gérer le lock optimiste
    * @param p_sb
    *           le string builder qui construit la requête
    * @param p_Entity
    *           l'entité qui contient la version
    */
   private void appendVersionConstraint (final StringBuilder p_sb, final TypeEntity p_Entity)
   {
      p_sb.append(" and ").append(JdbcVersionHelper.getVersionPhysicalName(p_Entity, _columnNames));
      p_sb.append(" = :").append(JdbcVersionHelper.c_requestParameter);
   }

   /**
    * Requête sql de select sans critère de restriction.
    * @param p_selectProjection
    *           les colonnes à sélectionner (* ou la liste des colonnes)
    * @return String
    */
   String getSelectQuery (final String p_selectProjection)
   {
      return "select " + p_selectProjection + " from " + _tableName + ' ';
   }
}
