/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;

/**
 * Criteria utilisé dans les DAO pour décrire les critères d'une requête de sélection en utilisant une API objet (et non sql).
 * @param <TableColumns_Enum>
 *           Type des colonnes de l'entité sur laquelle porte les critères
 */
public class TableCriteria<TableColumns_Enum extends ColumnsNames_Itf> implements Cloneable
{
   /** Le tableau permettant de mémoriser les inner join. */
   private final List<InnerJoin_intern> _tab_InnerJoin = new ArrayList<>();

   /** Le tableau permettant de mémoriser les critères. */
   private final List<Criteria_intern<TableColumns_Enum>> _tab_Criteria = new ArrayList<>();

   /** Le tableau permettant de mémoriser les order by. */
   private final List<OrderBy_intern<TableColumns_Enum>> _tab_OrderBy = new ArrayList<>();

   /** La description du critère. */
   private final String _description;

   /** Le nombre de lignes maximum à ramener (-1 : toutes les lignes par défaut). */
   private int _nbLignesMax = -1;

   /** Le nombre de lignes à passer avant de commencer la lecture (initialisé à 0 par java : on commence au début par défaut). */
   private int _nbLignesStart;

   /** Localisation pour toLowerCase pour les critères en ignoreCase **/
   private final Locale _locale = Locale.getDefault();

   /** le critère verrouillant les lignes sélectionnées **/
   private boolean _lockRowsForUpdate; // = false

   /**
    * Le nom du critère.
    * @param p_description
    *           (In)(*) La description du critère.
    */
   public TableCriteria (final String p_description)
   {
      super();
      // Si l'attribut n'est pas renseigné
      if (p_description == null)
      {
         throw new IllegalArgumentException(
                  "Le paramètre 'p_description' est obligatoire, il ne peut pas être 'null'\n");
      }
      _description = p_description;
   }

   /**
    * Permet d'obtenir la description du critère.
    * @return La description du critère.
    */
   public final String getDescription ()
   {
      return _description;
   }

   /**
    * Permet d'obtenir le nombre de lignes maximum à ramener (-1 : toutes les lignes).
    * @return Le nombre de lignes maximum à ramener.
    */
   public final int getNbLignesMax ()
   {
      return _nbLignesMax;
   }

   /**
    * Permet d'affecter le nombre de lignes maximum à ramener (-1 : toutes les lignes).
    * @param p_nbLignesMax
    *           (In) Le nombre de lignes maximum à ramener.
    */
   public final void setNbLignesMax (final int p_nbLignesMax)
   {
      _nbLignesMax = p_nbLignesMax;
   }

   /**
    * Permet d'obtenir le nombre de lignes à passer avant de commencer la lecture.
    * @return Le nombre de lignes à passer avant de commencer la lecture.
    */
   public final int getNbLignesStart ()
   {
      return _nbLignesStart;
   }

   /**
    * Permet d'affecter le nombre de lignes à passer avant de commencer la lecture.
    * @param p_nbLignesStart
    *           (In) Le nombre de lignes à passer avant de commencer la lecture.
    */
   public final void setNbLignesStart (final int p_nbLignesStart)
   {
      _nbLignesStart = p_nbLignesStart;
   }

   /**
    * le critère verrouillant les lignes sélectionnées
    * @return si les lignes sélectionnées sont verrouillées
    */
   public boolean isLockingRowsForUpdate ()
   {
      return _lockRowsForUpdate;
   }

   /**
    * verrouille les lignes sélectionnées
    */
   public void lockRowsForUpdate ()
   {
      _lockRowsForUpdate = true;
   }

   /**
    * Obtenir le critère correspondant à ce TableCriteria sous forme sql.
    * @return Le critère sql
    */
   @Override
   public String toString ()
   {
      return getCriteriaSql();
   }

   /**
    * Obtenir le critère correspondant à ce TableCriteria sous forme sql.
    * @return Le critère sql
    */
   public String getCriteriaSql ()
   {
      // final Set<String> v_nameParameters = new HashSet<String>();
      final StringBuilder v_result = new StringBuilder();

      // prépare la requête sql avec les inner join
      appendInnerJoins(v_result);

      // prépare la requête sql avec les criteria
      appendWhere(v_result);

      // prépare la requête sql avec les order by
      appendOrderBy(v_result);

      // espace pour séparer si concaténé à autre chose
      v_result.append(' ');

      // prépare la requête sql avec le verrouillage
      if (isLockingRowsForUpdate())
      {
         v_result.append("for update");
      }
      return v_result.toString();
   }

   /**
    * prépare la requête sql avec les inner join.
    * @param p_sb
    *           StringBuilder
    */
   protected void appendInnerJoins (final StringBuilder p_sb)
   {
      for (final InnerJoin_intern v_innerJoin : _tab_InnerJoin)
      {
         appendInnerJoin(v_innerJoin, p_sb);
      }
   }

   /**
    * prépare la requête sql avec les criteria.
    * @param p_sb
    *           StringBuilder
    */
   protected void appendWhere (final StringBuilder p_sb)
   {
      int v_cpt = 0;
      if (!_tab_Criteria.isEmpty())
      {
         p_sb.append(" where ");
      }
      for (final Criteria_intern<TableColumns_Enum> v_criteria : _tab_Criteria)
      {
         // Si 2ème et plus
         if (v_cpt > 0)
         {
            p_sb.append(' ').append(v_criteria.getOperatorLogical()).append(' ');
         }

         appendCriteriaForSql(v_criteria, p_sb);
         v_cpt++;
      }
   }

   /**
    * prépare la requête sql avec les order by.
    * @param p_sb
    *           StringBuilder
    */
   protected void appendOrderBy (final StringBuilder p_sb)
   {
      int v_cpt;
      if (!_tab_OrderBy.isEmpty())
      {
         p_sb.append(" order by ");
      }
      v_cpt = 0;
      for (final OrderBy_intern<TableColumns_Enum> v_orderBy : _tab_OrderBy)
      {
         // Si 2ème et plus
         if (v_cpt > 0)
         {
            p_sb.append(", ");
         }
         appendOrderByForSql(v_orderBy, p_sb);
         v_cpt++;
      }
   }

   /**
    * Ajoute un inner joinr dans une requête sql.
    * @param p_innerJoin
    *           InnerJoin
    * @param p_result
    *           StringBuilder
    */
   private void appendInnerJoin (final InnerJoin_intern p_innerJoin, final StringBuilder p_result)
   {
      p_result.append(" inner join ").append(p_innerJoin.getReferenceJoin().getTableName());
      final String v_alias;
      // si un alias existe sur l'inner join, on le rajoute à la requête
      if (p_innerJoin.getAlias() != null)
      {
         v_alias = p_innerJoin.getAlias();
         p_result.append(' ').append(v_alias);
      }
      else
      {
         // sinon l'alias est en réalité le nom de la table de référence
         v_alias = p_innerJoin.getReferenceJoin().getTableName();
      }
      p_result.append(" on ");
      // ajout par défaut de la contrainte sur clé étrangère = clé primaire
      p_result.append(p_innerJoin.getColumnJoin().getCompletePhysicalName()).append(" = ");
      p_result.append(v_alias).append('.').append(p_innerJoin.getReferenceJoin().getPhysicalColumnName());
      // ajout des critères supplémentaires s'il y en a
      if (p_innerJoin.getCriteriaSql() != null && !p_innerJoin.getCriteriaSql().trim().isEmpty())
      {
         p_result.append(" AND ").append(p_innerJoin.getCriteriaSql());
      }
   }

   /**
    * Ajoute un critère dans une requête sql avec des paramètres bindés
    * @param p_criteria
    *           Criteria
    * @param p_result
    *           StringBuilder
    */
   private void appendCriteriaForSql (final Criteria_intern<TableColumns_Enum> p_criteria, final StringBuilder p_result)
   {
      final Operator_Enum v_operator = p_criteria.getOperator();
      final Object v_value = p_criteria.getValue();
      final TableColumns_Enum v_nameColumn = p_criteria.getNameColumn();

      String v_physicalColumnName = v_nameColumn.getPhysicalColumnName();
      // S'il y a des inner join, il est préférable de préfixer les noms des colonnes par le nom de leur table
      if (!_tab_InnerJoin.isEmpty())
      {
         v_physicalColumnName = v_nameColumn.getCompletePhysicalName();
      }
      // Si le critère ne doit pas tenir compte de la casse, on demande à la requête sql de mettre en minuscule la valeur du champ récupérée en base
      if (p_criteria.isIgnoreCase())
      {
         v_physicalColumnName = "lower(" + v_physicalColumnName + ")";
      }

      // si opérateur 'égal' et valeur nulle
      if (v_operator == Operator_Enum.equals && v_value == null)
      {
         p_result.append(v_physicalColumnName).append(" is null");
      }
      // si opérateur 'différent' et valeur nulle
      else if (v_operator == Operator_Enum.different && v_value == null)
      {
         p_result.append(v_physicalColumnName).append(" is not null");
      }
      else
      {
         // Cas par défaut

         // le nom du paramètre sql est "nameColumn" ou bien "nameColumn1", "nameColumn2", etc si la colonne a plusieurs critères
         String v_nameParameter = v_nameColumn.getLogicalColumnName();
         int v_i = 0;
         // on ajoute un espace après le nom du paramètre pour être sûr qu'il s'agit du bon (éviter de matcher :libelleAbrege pour :libelle)
         while (p_result.indexOf(':' + v_nameParameter + ' ') >= 0)
         {
            v_i++;
            // Affecter le nom du paramètre à partir du nom de la colonne
            v_nameParameter = v_nameColumn.getLogicalColumnName() + v_i;
         }

         // on ajoute la contrainte avec le paramètre qui va bien
         p_result.append(v_physicalColumnName).append(' ').append(v_operator.getSqlName()).append(' ').append(':')
                  .append(v_nameParameter);
      }
   }

   /**
    * Ajoute un order by dans une requête sql.
    * @param p_orderBy
    *           OrderBy
    * @param p_result
    *           StringBuilder
    */
   private void appendOrderByForSql (final OrderBy_intern<TableColumns_Enum> p_orderBy, final StringBuilder p_result)
   {
      final TableColumns_Enum v_nameColumn = p_orderBy.getNameColumn();
      String v_physicalColumnName = v_nameColumn.getPhysicalColumnName();
      // S'il y a des inner join, il est préférable de préfixer les noms des colonnes par le nom de leur table
      if (!_tab_InnerJoin.isEmpty())
      {
         v_physicalColumnName = v_nameColumn.getCompletePhysicalName();
      }
      p_result.append(v_physicalColumnName);
      if (p_orderBy.isAscending())
      {
         p_result.append(" asc");
      }
      else
      {
         p_result.append(" desc");
      }
   }

   /**
    * Obtenir la map des valeurs des paramètres selon les noms de paramètres correspondant à ce TableCriteria.
    * @return La map des valeurs des paramètres
    */
   public Map<String, Object> getMapValue ()
   {
      final Map<String, Object> v_return = new LinkedHashMap<>();
      for (final Criteria_intern<TableColumns_Enum> v_criteria : _tab_Criteria)
      {
         // Si valeur nulle et que (opérateur 'egal' ou 'different')
         if (v_criteria.getValue() == null
                  && (v_criteria.getOperator() == Operator_Enum.equals || v_criteria.getOperator() == Operator_Enum.different))
         {
            // puisque l'on va vérifier que tous les paramètres passés sont utilisés, il ne faut pas mettre ceux-ci qui ne sont pas dans getCriteriaSql()
            continue;
         }

         final Object v_value = getSqlValue(v_criteria);

         // le nom du paramètre sql est "nameColumn" ou bien "nameColumn1", "nameColumn2", etc si la colonne a plusieurs critères
         final TableColumns_Enum v_nameColumn = v_criteria.getNameColumn();
         String v_nameParameter = v_nameColumn.getLogicalColumnName();
         int v_i = 0;
         while (v_return.containsKey(v_nameParameter) || innerJoinContainsParameter(v_nameParameter))
         {
            v_i++;
            // Affecter le nom du paramètre à partir du nom de la colonne
            v_nameParameter = v_nameColumn.getLogicalColumnName() + v_i;
         }

         v_return.put(v_nameParameter, v_value);
      }
      return Collections.unmodifiableMap(v_return);
   }

   /**
    * Retourne la valeur du paramètre sql pour un critère en transformant la valeur dans les cas ignoreCase et/ou contains, startsWith, endsWith
    * @param p_criteria
    *           Criteria
    * @return Object
    */
   private Object getSqlValue (final Criteria_intern<TableColumns_Enum> p_criteria)
   {
      Object v_value = p_criteria.getValue();

      // Si le critère doit ignorer la casse, on a fait un lower(v_nameColumn.getPhysicalColumnName()) dans toString() et dans getCriteriaSql()
      // pour mettre le champ issu de la base de données en minuscule donc on doit mettre aussi en minuscule la valeur du critère en faisant
      // toLowerCase(Locale.getDefault())
      if (v_value != null && p_criteria.isIgnoreCase())
      {
         try
         {
            v_value = ((String) v_value).toLowerCase(_locale);
         }
         catch (final ClassCastException v_e)
         {
            throw new Spi4jRuntimeException(v_e, "Incohérence entre le critère sur la colonne "
                     + p_criteria.getNameColumn().getCompletePhysicalName()
                     + " qui demande d'ignorer la casse et la valeur " + v_value + " qui n'est pas de type String",
                     "Revoir l'utilisation de la méthode addCriteriaIgnoreCase()");
         }
      }

      final Operator_Enum v_operator = p_criteria.getOperator();
      // Si opérateur 'contient'
      if (v_operator == Operator_Enum.contains)
      {
         v_value = "%" + v_value + "%";
      }
      // Si opérateur 'commence par'
      else if (v_operator == Operator_Enum.startsWith)
      {
         v_value = v_value + "%";
      }
      // Si opérateur 'fini par'
      else if (v_operator == Operator_Enum.endsWith)
      {
         v_value = "%" + v_value;
      }
      return v_value;
   }

   /**
    * Cherche si une contrainte d'inner join contient ce paramètre.
    * @param p_nameParameter
    *           le nom du paramètre
    * @return true si une contrainte d'inner join contient le paramètre, false sinon
    */
   private boolean innerJoinContainsParameter (final String p_nameParameter)
   {
      for (final InnerJoin_intern v_innerJoin : _tab_InnerJoin)
      {
         if (v_innerJoin.getCriteriaSql() != null && v_innerJoin.getCriteriaSql().contains(':' + p_nameParameter))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Ajouter un critère.
    * @param p_nameColumn
    *           (In)(*) Le paramètre 'nameColumn' (obligatoire).
    * @param p_Operator
    *           (In)(*) Le paramètre 'Operator' (obligatoire).
    * @param p_value
    *           (In)(*) Le paramètre 'value' (obligatoire), qui peut être une Entity.
    * @return this
    */
   public TableCriteria<TableColumns_Enum> addCriteria (final TableColumns_Enum p_nameColumn,
            final Operator_Enum p_Operator, final Object p_value)
   {
      // ajoute un critère sans opérateur logique en tenant compte de la casse (p_ignoreCase = false)
      return addCriteria(p_nameColumn, p_Operator, p_value, false);
   }

   /**
    * Ajouter un critère.
    * @param p_nameColumn
    *           (In)(*) Le paramètre 'nameColumn' (obligatoire).
    * @param p_Operator
    *           (In)(*) Le paramètre 'Operator' (obligatoire).
    * @param p_value
    *           (In)(*) Le paramètre 'value' (obligatoire).
    * @return this
    */
   public TableCriteria<TableColumns_Enum> addCriteriaIgnoreCase (final TableColumns_Enum p_nameColumn,
            final Operator_Enum p_Operator, final Object p_value)
   {
      // ajoute un critère sans opérateur logique en ignorant la casse (p_ignoreCase = true)
      return addCriteria(p_nameColumn, p_Operator, p_value, true);
   }

   /**
    * Ajouter un critère.
    * @param p_nameColumn
    *           (In)(*) Le paramètre 'nameColumn' (obligatoire).
    * @param p_Operator
    *           (In)(*) Le paramètre 'Operator' (obligatoire).
    * @param p_value
    *           (In)(*) Le paramètre 'value' (obligatoire).
    * @param p_ignoreCase
    *           (In)(*) Le paramètre 'ignoreCase' (obligatoire).
    * @return this
    */
   private TableCriteria<TableColumns_Enum> addCriteria (final TableColumns_Enum p_nameColumn,
            final Operator_Enum p_Operator, final Object p_value, final boolean p_ignoreCase)
   {
      // Si aucun un critère de défini : c'est le 1er
      if (!_tab_Criteria.isEmpty())
      {
         // Si on a déjà au moins un critère : appel impossible par cette surcharge
         throw new IllegalArgumentException(
                  "Appel de cette surcharge impossible car déjà "
                           + _tab_Criteria.size()
                           + " critère(s) de défini - utiliser : addCriteria (final OperatorLogical_Enum p_OperatorLogical, final TableColumns_Enum p_nameColumn, final Operator_Enum p_Operator, final String p_value)");
      }

      // ajoute un critère sans opérateur logique (p_OperatorLogical = null)
      addCriteria(null, p_nameColumn, p_Operator, p_value, p_ignoreCase);
      // api fluent comme StringBuilder
      return this;
   }

   /**
    * Ajouter un critère avec un opérateur logique.
    * @param p_OperatorLogical
    *           (In) L'opérateur logique entre 2 critères - ex : 'and'.
    * @param p_nameColumn
    *           (In)(*) Le nom de la colonne (obligatoire).
    * @param p_Operator
    *           (In)(*) L'opérateur - ex : 'equals' (obligatoire).
    * @param p_value
    *           (In) La valeur associée (obligatoire), qui peut être une Entity.
    * @return this
    */
   public TableCriteria<TableColumns_Enum> addCriteria (final OperatorLogical_Enum p_OperatorLogical,
            final TableColumns_Enum p_nameColumn, final Operator_Enum p_Operator, final Object p_value)
   {
      // ajoute un critère avec opérateur logique en tenant compte de la casse (p_ignoreCase = false)
      return addCriteria(p_OperatorLogical, p_nameColumn, p_Operator, p_value, false);
   }

   /**
    * Ajouter un critère avec un opérateur logique.
    * @param p_OperatorLogical
    *           (In) L'opérateur logique entre 2 critères - ex : 'and'.
    * @param p_nameColumn
    *           (In)(*) Le nom de la colonne (obligatoire).
    * @param p_Operator
    *           (In)(*) L'opérateur - ex : 'equals' (obligatoire).
    * @param p_value
    *           (In) La valeur associée (obligatoire).
    * @return this
    */
   public TableCriteria<TableColumns_Enum> addCriteriaIgnoreCase (final OperatorLogical_Enum p_OperatorLogical,
            final TableColumns_Enum p_nameColumn, final Operator_Enum p_Operator, final Object p_value)
   {
      // ajoute un critère avec opérateur logique en ignorant la casse (p_ignoreCase = true)
      return addCriteria(p_OperatorLogical, p_nameColumn, p_Operator, p_value, true);
   }

   /**
    * Ajouter un critère avec un opérateur logique.
    * @param p_OperatorLogical
    *           (In) L'opérateur logique entre 2 critères - ex : 'and'.
    * @param p_nameColumn
    *           (In)(*) Le nom de la colonne (obligatoire).
    * @param p_Operator
    *           (In)(*) L'opérateur - ex : 'equals' (obligatoire).
    * @param p_value
    *           (In) La valeur associée (obligatoire).
    * @param p_ignoreCase
    *           (In) La valeur associée (obligatoire).
    * @return this
    */
   private TableCriteria<TableColumns_Enum> addCriteria (final OperatorLogical_Enum p_OperatorLogical,
            final TableColumns_Enum p_nameColumn, final Operator_Enum p_Operator, final Object p_value,
            final boolean p_ignoreCase)
   {
      // Le paramètre 'p_nameColumn' de type 'TableColumns_Enum' est obligatoire
      if (p_nameColumn == null)
      {
         throw new IllegalArgumentException("Le paramètre 'p_nameColumn' de type 'TableColumns_Enum' est obligatoire");
      }
      // Le paramètre 'p_Operator' de type 'Operator_Enum' est obligatoire
      if (p_Operator == null)
      {
         throw new IllegalArgumentException("Le paramètre 'p_Operator' de type 'Operator_Enum' est obligatoire");
      }

      // Si aucun un critère de défini : c'est le 1er
      // Et si pas d'opérateur spécifié, RAS
      if (_tab_Criteria.isEmpty() && p_OperatorLogical != null)
      {
         // Si on a spécifié un opérateur
         throw new IllegalArgumentException(
                  "Appel de cette surcharge impossible car aucun critère de défini - utiliser : addCriteria (final TableColumns_Enum p_nameColumn, final Operator_Enum p_Operator, final Object p_value)");
      }
      // Sinon, on a déjà au moins un critère, RAS

      // Instancier le 'Criteria_intern'
      final Criteria_intern<TableColumns_Enum> v_Criteria_intern = new Criteria_intern<>(p_nameColumn, p_Operator,
               p_value, p_OperatorLogical, p_ignoreCase);
      // Mémoriser le critère
      _tab_Criteria.add(v_Criteria_intern);
      // api fluent comme StringBuilder
      return this;
   }

   /**
    * Ajoute un inner join sur la requête.
    * @param p_columnJoin
    *           la colonne de jointure
    * @param p_referenceJoin
    *           la référence de jointure
    * @return this
    */
   public TableCriteria<TableColumns_Enum> addInnerJoin (final ColumnsNames_Itf p_columnJoin,
            final ColumnsNames_Itf p_referenceJoin)
   {
      return addInnerJoinWithAlias(null, p_columnJoin, p_referenceJoin, null);
   }

   /**
    * Ajoute un inner join sur la requête avec critères.
    * @param p_columnJoin
    *           la colonne de jointure
    * @param p_referenceJoin
    *           la référence de jointure
    * @param p_criteriaSql
    *           les critères de jointure (sans 'AND' initial)
    * @return this
    */
   public TableCriteria<TableColumns_Enum> addInnerJoin (final ColumnsNames_Itf p_columnJoin,
            final ColumnsNames_Itf p_referenceJoin, final String p_criteriaSql)
   {
      return addInnerJoinWithAlias(null, p_columnJoin, p_referenceJoin, p_criteriaSql);
   }

   /**
    * Ajoute un inner join sur la requête.
    * @param p_alias
    *           l'alias de la jointure
    * @param p_columnJoin
    *           la colonne de jointure
    * @param p_referenceJoin
    *           la référence de jointure
    * @return this
    */
   public TableCriteria<TableColumns_Enum> addInnerJoinWithAlias (final String p_alias,
            final ColumnsNames_Itf p_columnJoin, final ColumnsNames_Itf p_referenceJoin)
   {
      return addInnerJoinWithAlias(p_alias, p_columnJoin, p_referenceJoin, null);
   }

   /**
    * Ajoute un inner join sur la requête avec critères.
    * @param p_alias
    *           l'alias de la jointure
    * @param p_columnJoin
    *           la colonne de jointure
    * @param p_referenceJoin
    *           la référence de jointure
    * @param p_criteriaSql
    *           les critères de jointure (sans 'AND' initial)
    * @return this
    */
   public TableCriteria<TableColumns_Enum> addInnerJoinWithAlias (final String p_alias,
            final ColumnsNames_Itf p_columnJoin, final ColumnsNames_Itf p_referenceJoin, final String p_criteriaSql)
   {
      // ajout de l'inner join
      final InnerJoin_intern v_InnerJoin_intern = new InnerJoin_intern(p_alias, p_columnJoin, p_referenceJoin,
               p_criteriaSql);
      _tab_InnerJoin.add(v_InnerJoin_intern);
      // api fluent comme StringBuilder
      return this;
   }

   /**
    * Ajouter un critère.
    * @param p_nameColumn
    *           (In)(*) Le paramètre 'nameColumn' (obligatoire).
    * @param p_ascending
    *           (In) Le paramètre 'ascending'.
    * @return this
    */
   private TableCriteria<TableColumns_Enum> addOrderBy (final TableColumns_Enum p_nameColumn, final boolean p_ascending)
   {
      // Le paramètre 'p_nameColumn' de type 'TableColumns_Enum' est obligatoire
      if (p_nameColumn == null)
      {
         throw new IllegalArgumentException("Le paramètre 'p_nameColumn' de type 'TableColumns_Enum' est obligatoire");
      }
      // Instancier le 'Criteria_intern'
      final OrderBy_intern<TableColumns_Enum> v_OrderBy_intern = new OrderBy_intern<>(p_nameColumn, p_ascending);
      // Mémoriser le critère
      _tab_OrderBy.add(v_OrderBy_intern);
      // api fluent comme StringBuilder
      return this;
   }

   /**
    * Ajouter un tri ascendant sur le critère.
    * @param p_nameColumn
    *           (In)(*) Le paramètre 'nameColumn' (obligatoire).
    * @return this
    */
   public TableCriteria<TableColumns_Enum> addOrderByAsc (final TableColumns_Enum p_nameColumn)
   {
      return addOrderBy(p_nameColumn, true);
   }

   /**
    * Ajouter un tri descendant sur le critère.
    * @param p_nameColumn
    *           (In)(*) Le paramètre 'nameColumn' (obligatoire).
    * @return this
    */
   public TableCriteria<TableColumns_Enum> addOrderByDesc (final TableColumns_Enum p_nameColumn)
   {
      return addOrderBy(p_nameColumn, false);
   }

   @Override
   public TableCriteria<TableColumns_Enum> clone () 
   {
      // Note : on n'utilise pas super.clone() car il faut cloner les instances de List en attributs mais on ne pourrait pas redéfinir ces instances final après l'appel de super.clone()
      final TableCriteria<TableColumns_Enum> v_tableCriteria = new TableCriteria<>(_description);
      return copy(v_tableCriteria);
   }

   /**
    * Copie le contenu de ce TableCriteria dans un autre
    * @param p_destination
    *           le TableCriteria de destination
    * @return le TableCriteria modifié
    */
   protected TableCriteria<TableColumns_Enum> copy (final TableCriteria<TableColumns_Enum> p_destination)
   {
      p_destination._nbLignesMax = _nbLignesMax;
      p_destination._nbLignesStart = _nbLignesStart;
      p_destination._lockRowsForUpdate = _lockRowsForUpdate;
      // Criteria_intern, OrderBy_intern et InnerJoin_intern sont non mutable, il est donc inutile de les cloner
      // vide les inner join, les critères et les order by avant de les remplir
      p_destination._tab_InnerJoin.clear();
      p_destination._tab_Criteria.clear();
      p_destination._tab_OrderBy.clear();
      p_destination._tab_InnerJoin.addAll(_tab_InnerJoin);
      p_destination._tab_Criteria.addAll(_tab_Criteria);
      p_destination._tab_OrderBy.addAll(_tab_OrderBy);
      return p_destination;
   }

   /**
    * Classe interne représentant un critère sur une colonne.
    * @param <TableColumns_Enum>
    *           Type des colonnes de l'entité
    */
   private static class Criteria_intern<TableColumns_Enum extends ColumnsNames_Itf>
   {
      /** Le paramètre 'nameColumn'. */
      private final TableColumns_Enum _nameColumn;

      /** Le paramètre 'Operator'. */
      private final Operator_Enum _Operator;

      /** Le paramètre 'value'. */
      private final Object _value;

      /** Le paramètre 'OperatorLogical'. */
      private final OperatorLogical_Enum _OperatorLogical;

      /**
       * Le paramètre 'ignoreCase'. Détermine si le critère doit ignorer ou tenir compte de la casse. Applicable seulement sur un champ texte.
       */
      private final boolean _ignoreCase;

      /**
       * Constructeur max : avec tous les attributs. Exemple : <code><pre>
       *    C1 v_C1 = new C1(...);
       * </pre></code>
       * @param p_nameColumn
       *           (In)(*) Le paramètre 'nameColumn'.
       * @param p_Operator
       *           (In)(*) Le paramètre 'Operator'.
       * @param p_value
       *           (In)(*) Le paramètre 'value'.
       * @param p_OperatorLogical
       *           (In) Le paramètre 'OperatorLogical'.
       * @param p_ignoreCase
       *           (In) Le paramètre 'ignoreCase'.
       */
      public Criteria_intern (final TableColumns_Enum p_nameColumn, final Operator_Enum p_Operator,
               final Object p_value, final OperatorLogical_Enum p_OperatorLogical, final boolean p_ignoreCase)
      {
         // Initialiser la classe
         // Si les attributs ne sont pas renseignés
         if (p_nameColumn == null)
         {
            throw new IllegalArgumentException(
                     "Le paramètre 'nameColumn' est obligatoire, il ne peut pas être 'null'\n");
         }
         if (p_Operator == null)
         {
            throw new IllegalArgumentException("Le paramètre 'operator' est obligatoire, il ne peut pas être 'null'\n");
         }

         if (p_ignoreCase && p_nameColumn.getTypeColumn() != String.class)
         {
            throw new IllegalArgumentException("Il est incohérent de vouloir ignorer la casse pour la colonne "
                     + p_nameColumn.getPhysicalColumnName() + " qui n'est pas de type String mais de type "
                     + p_nameColumn.getTypeColumn().getName() + "\n");
         }
         // rq: p_value peut être null
         _nameColumn = p_nameColumn;
         _Operator = p_Operator;
         _value = p_value;
         _OperatorLogical = p_OperatorLogical;
         _ignoreCase = p_ignoreCase;
      }

      /**
       * Permet d'obtenir le paramètre 'nameColumn'.
       * @return Le paramètre 'nameColumn'.
       */
      public final TableColumns_Enum getNameColumn ()
      {
         return _nameColumn;
      }

      /**
       * Permet d'obtenir le paramètre 'Operator'.
       * @return Le paramètre 'Operator'.
       */
      public final Operator_Enum getOperator ()
      {
         return _Operator;
      }

      /**
       * Permet d'obtenir le paramètre 'value'.
       * @return Le paramètre 'value'.
       */
      public final Object getValue ()
      {
         return _value;
      }

      /**
       * Permet d'obtenir le paramètre 'OperatorLogical'.
       * @return Le paramètre 'OperatorLogical'.
       */
      public final OperatorLogical_Enum getOperatorLogical ()
      {
         return _OperatorLogical;
      }

      /**
       * Permet d'obtenir le paramètre 'ignoreCase'.
       * @return Le paramètre 'ignoreCase'.
       */
      public boolean isIgnoreCase ()
      {
         return _ignoreCase;
      }
   }

   /**
    * Classe interne représentant l'ordre des lignes retournées.
    * @param <TableColumns_Enum>
    *           Type des colonnes de l'entité.
    */
   private static class OrderBy_intern<TableColumns_Enum extends ColumnsNames_Itf>
   {
      /** Le paramètre 'nameColumn'. */
      private final TableColumns_Enum _nameColumn;

      /** Le paramètre 'ascending'. */
      private final boolean _ascending;

      /**
       * Constructeur max : avec tous les attributs. Exemple : <code><pre>
       *    C1 v_C1 = new C1(...);
       * </pre></code>
       * @param p_nameColumn
       *           (In)(*) Le paramètre 'nameColumn'.
       * @param p_ascending
       *           (In)(*) Le paramètre 'ascending'.
       */
      public OrderBy_intern (final TableColumns_Enum p_nameColumn, final boolean p_ascending)
      {
         // Initialiser la classe
         // Si les attributs ne sont pas renseignés
         if (p_nameColumn == null)
         {
            throw new IllegalArgumentException(
                     "Le paramètre 'nameColumn' est obligatoire, il ne peut pas être 'null'\n");
         }
         _nameColumn = p_nameColumn;
         _ascending = p_ascending;
      }

      /**
       * Permet d'obtenir le paramètre 'nameColumn'.
       * @return Le paramètre 'nameColumn'.
       */
      public final TableColumns_Enum getNameColumn ()
      {
         return _nameColumn;
      }

      /**
       * Permet d'obtenir le paramètre 'ascending'.
       * @return Le paramètre 'ascending'.
       */
      public final boolean isAscending ()
      {
         return _ascending;
      }
   }

   /**
    * Classe interne représentant les liaisons inner join.
    */
   private static class InnerJoin_intern
   {

      private final String _alias;

      private final ColumnsNames_Itf _columnJoin;

      private final ColumnsNames_Itf _referenceJoin;

      private final String _criteriaSql;

      /**
       * Construction d'un inner join.
       * @param p_alias
       *           l'alias de la jointure
       * @param p_columnJoin
       *           la colonne de jointure
       * @param p_referenceJoin
       *           la reference de jointure
       * @param p_criteriaSql
       *           le critère de jointure
       */
      public InnerJoin_intern (final String p_alias, final ColumnsNames_Itf p_columnJoin,
               final ColumnsNames_Itf p_referenceJoin, final String p_criteriaSql)
      {
         if (p_columnJoin == null)
         {
            throw new IllegalArgumentException(
                     "Le paramètre 'columnJoin' est obligatoire, il ne peut pas être 'null'\n");
         }
         if (p_referenceJoin == null)
         {
            throw new IllegalArgumentException(
                     "Le paramètre 'referenceJoin' est obligatoire, il ne peut pas être 'null'\n");
         }
         _alias = p_alias;
         _columnJoin = p_columnJoin;
         _referenceJoin = p_referenceJoin;
         _criteriaSql = p_criteriaSql;
      }

      /**
       * @return l'alias de la jointure
       */
      public String getAlias ()
      {
         return _alias;
      }

      /**
       * @return la colonne de jointure
       */
      public ColumnsNames_Itf getColumnJoin ()
      {
         return _columnJoin;
      }

      /**
       * @return la reference de jointure
       */
      public ColumnsNames_Itf getReferenceJoin ()
      {
         return _referenceJoin;
      }

      /**
       * @return le critère sql
       */
      public String getCriteriaSql ()
      {
         return _criteriaSql;
      }
   }

}
