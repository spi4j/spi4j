/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao;

import java.util.List;
import java.util.Map;

import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.persistence.resource.ResourceManager_Abs;

/**
 * Classe abstraite permettant de centraliser toutes les traitements communs aux DAO.
 * @author MINARM
 * @param <TypeId>
 *           Type de l'identifiant de l'entity
 * @param <TypeEntity>
 *           Type d'entity
 * @param <Columns_Enum>
 *           Type de l'énumération des colonnes
 */
abstract public interface Dao_Itf<TypeId, TypeEntity extends Entity_Itf<TypeId>, Columns_Enum extends ColumnsNames_Itf>
{

   /**
    * Permet d'obtenir le nom de la table associée à l'entity.
    * @return Le nom de la table
    */
   String getTableName ();

   /**
    * Permet d'obtenir la colonne associée à la clé primaire de la table.
    * @return la colonne associée à la clé primaire de la table
    */
   ColumnsNames_Itf getColumnId ();

   /**
    * Permet d'obtenir le gestionnaire de ressource associé au DAO.
    * @return Le gestionnaire de ressource associé au DAO.
    * @see #setResourceManager(ResourceManager_Abs p_Resource)
    */
   ResourceManager_Abs getResourceManager ();

   /**
    * Permet d'affecter le gestionnaire de ressource associé au DAO.
    * @param p_ResourceManager
    *           (In) Le gestionnaire de ressource associé au DAO.
    * @see #getResourceManager()
    */
   void setResourceManager (ResourceManager_Abs p_ResourceManager);

   /**
    * Créer un tuple.
    * @param p_Entity
    *           (In/Out)(*) Le tuple à inséer est obligatoire (sauf la clé primaire affectée dans la méthode).
    * @throws Spi4jValidationException
    *            Si le tuple n'est pas valide
    */
   void create (TypeEntity p_Entity) throws Spi4jValidationException;

   /**
    * Obtenir le tuple à partir de sa clé primaire.
    * @param p_id
    *           (In)(*) La clé primaire de l'objet recherché.
    * @return Le tuple désiré (non null ou exception si donnée non trouvée en base de données).
    */
   TypeEntity findById (TypeId p_id);

   /**
    * Obtenir la liste des tuples.
    * @return La liste des tuples.
    */
   List<TypeEntity> findAll ();

   /**
    * Obtenir la liste des tuples ordonnée selon une colonne.
    * @param p_orderByColumn
    *           Colonne pour le tri
    * @return La liste des tuples.
    */
   List<TypeEntity> findAll (Columns_Enum p_orderByColumn);

   /**
    * Mettre à jour le tuple (et lance une exception si donnée non trouvée en base de données).
    * @param p_Entity
    *           (In)(*) Le tuple à mettre à jour est obligatoire.
    * @throws Spi4jValidationException
    *            Si le tuple n'est pas valide
    */
   void update (TypeEntity p_Entity) throws Spi4jValidationException;

   /**
    * Supprimer un tuple à partir de sa clé primaire (et lance une exception si donnée non trouvée en base de données).
    * @param p_Entity
    *           (In)(*) Le tuple à supprimer avec la clé primaire.
    */
   void delete (TypeEntity p_Entity);

   /**
    * Supprimer tous les tuples.
    * @return Le nombre de ligne(s) supprimée(s).
    */
   int deleteAll ();

   /**
    * Supprime des tuples à partir d'un TableCriteria (le critère sql sera généré automatiquement). <br/>
    * <h4>Exemple:</h4>
    *
    * <pre>
    * final TableCriteria v_tableCriteria = new TableCriteria();
    * v_tableCriteria.addCriteria(PersonneEntity_Itf.PersonneColumns_Enum.nom, Operator_Enum.contains, &quot;DUPONT&quot;);
    * v_tableCriteria.addOrderBy(PersonneEntity_Itf.PersonneColumns_Enum.gradeId, false);
    * personDao.deleteByCriteria(v_tableCriteria);
    * </pre>
    * @param p_tableCriteria
    *           un Criteria décrivant les lignes à supprimer (obligatoire)
    * @return Le nombre de ligne(s) supprimée(s).
    */
   int deleteByCriteria (final TableCriteria<Columns_Enum> p_tableCriteria);

   /**
    * Obtenir la liste des tuples à partir de la valeur d'une colonne. <br/>
    * <h4>Exemple:</h4>
    *
    * <pre>
    * final List&lt;PersonneEntity_Itf&gt; v_personnes = personDao.findByColumn(PersonneEntity_Itf.PersonneColumns_Enum.nom,
    *          &quot;DUPONT&quot;);
    * </pre>
    * 
    * @param p_column
    *           (In)(*) Le paramètre 'column' (obligatoire).
    * @param p_value
    *           (In) La valeur recherchée dans p_column, valeur qui peut être une Entity ou même null
    * @return La liste des tuples.
    */
   List<TypeEntity> findByColumn (Columns_Enum p_column, Object p_value);

   /**
    * Obtenir la liste des tuples à partir d'un critère et d'éventuels paramètres nommés. <br/>
    * <h4>Exemple:</h4>
    *
    * <pre>
    * final List&lt;PersonneEntity_Itf&gt; v_personnes = personDao.findByCriteria(&quot;where grade_id &gt;= :grade_id&quot;,
    *          Collections.singletonMap(&quot;grade_id&quot;, colonelGradeId));
    * </pre>
    * @param p_queryCriteria
    *           (In)(*) Le critère (obligatoire).
    * @param p_map_value_by_name
    *           (In) Map des valeurs des paramètres selon les noms de paramètres (non obligatoire).
    * @return La liste des tuples.
    */
   List<TypeEntity> findByCriteria (String p_queryCriteria, Map<String, ? extends Object> p_map_value_by_name);

   /**
    * Obtenir la liste des tuples à partir d'un critère, d'éventuels paramètres nommés et de nombres de lignes pour une pagination. <br/>
    * <h4>Exemple:</h4>
    *
    * <pre>
    * final List&lt;PersonneEntity_Itf&gt; v_personnes = personDao.findByCriteria(&quot;where grade_id &gt;= :grade_id&quot;,
    *          Collections.singletonMap(&quot;grade_id&quot;, colonelGradeId), 25, 100);
    * </pre>
    * @param p_queryCriteria
    *           (In)(*) Le critère (obligatoire).
    * @param p_map_value_by_name
    *           (In) Map des valeurs des paramètres selon les noms de paramètres (non obligatoire).
    * @param p_nbLignesMax
    *           (In) Le nombre de lignes maximum à ramener (-1 : toutes les lignes)
    * @param p_nbLignesStart
    *           (In) Nombre de lignes à passer avant de commencer la lecture.
    * @return La liste des tuples.
    */
   List<TypeEntity> findByCriteria (String p_queryCriteria, Map<String, ? extends Object> p_map_value_by_name,
            int p_nbLignesMax, int p_nbLignesStart);

   /**
    * Obtenir la liste des tuples à partir d'un TableCriteria (le critère sql sera généré automatiquement). <br/>
    * <h4>Exemple:</h4>
    *
    * <pre>
    * final TableCriteria v_tableCriteria = new TableCriteria();
    * v_tableCriteria.addCriteria(PersonneEntity_Itf.PersonneColumns_Enum.nom, Operator_Enum.contains, &quot;DUPONT&quot;);
    * v_tableCriteria.addOrderBy(PersonneEntity_Itf.PersonneColumns_Enum.gradeId, false);
    * v_tableCriteria.setNbLignesStart(100); // On démarre à la 100ème ligne
    * v_tableCriteria.setNbLignesMax(25); // On en lit 25 maximum
    * final List&lt;PersonneEntity_Itf&gt; v_personnes = personDao.findByCriteria(v_tableCriteria);
    * </pre>
    * @param p_tableCriteria
    *           (In)(*) Le paramètre 'tableCriteria' (obligatoire).
    * @return La liste des tuples.
    */
   List<TypeEntity> findByCriteria (TableCriteria<Columns_Enum> p_tableCriteria);

   /**
    * Permet d'exécuter une requète de mise à jour.
    * @param p_query
    *           (In)(*) La requête (obligatoire).
    * @param p_map_value_by_name
    *           (In) Map des valeurs des paramètres selon les noms de paramètres (non obligatoire).
    * @param p_commentaires
    *           (In) Commentaire(s) décrivant la requête (non obligatoire : la méthode peut être spécifier aucun commentaire)
    * @return Le nombre de ligne traitées.
    */
   int executeUpdate (final String p_query, final Map<String, ? extends Object> p_map_value_by_name,
            String... p_commentaires);

   /**
    * Permet d'exécuter une requête de sélection.
    * @param p_query
    *           (In)(*) La requête (obligatoire).
    * @param p_map_value_by_name
    *           (In) Map des valeurs des paramètres selon les noms de paramètres (non obligatoire).
    * @param p_commentaires
    *           (In) Commentaire(s) décrivant la requête (non obligatoire : la méthode peut être spécifier aucun commentaire)
    * @return Une instance de 'Cursor_Abs'.
    */
   Cursor_Abs executeQuery (final String p_query, final Map<String, ? extends Object> p_map_value_by_name,
            String... p_commentaires);

   /**
    * Permet d'exécuter une requête de sélection, avec des nombres de lignes pour une pagination.
    * @param p_query
    *           (In)(*) La requête (obligatoire).
    * @param p_map_value_by_name
    *           (In) Map des valeurs des paramètres selon les noms de paramètres (non obligatoire).
    * @param p_nbLignesMax
    *           (In) Le nombre de lignes maximum à ramener (-1 : toutes les lignes)
    * @param p_nbLignesStart
    *           (In) Nombre de lignes à passer avant de commencer la lecture.
    * @param p_commentaires
    *           (In) Commentaire(s) décrivant la requête (non obligatoire : la méthode peut être spécifier aucun commentaire)
    * @return Une instance de 'Cursor_Abs'.
    */
   Cursor_Abs executeQuery (final String p_query, final Map<String, ? extends Object> p_map_value_by_name,
            int p_nbLignesMax, int p_nbLignesStart, String... p_commentaires);

}
