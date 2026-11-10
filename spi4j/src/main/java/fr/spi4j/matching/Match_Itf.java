/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.matching;

import java.util.List;
import java.util.Map;

import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.dao.TableCriteria;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Interface de Matching entre le DTO et l'entity.
 *
 * @author MINARM
 * @param <TypeId>       Type de l'identifiant du DTO
 * @param <TypeDto>      Type de DTO
 * @param <TypeEntity>   Type d'entity
 * @param <Columns_Enum> Type de l'énumération des colonnes
 */
abstract public interface Match_Itf<TypeId, TypeDto extends Dto_Itf<TypeId>, TypeEntity extends Entity_Itf<TypeId>, Columns_Enum extends ColumnsNames_Itf> {
	/**
	 * Créer un tuple.
	 *
	 * @param p_dto (In/Out)(*) Le tuple à inséer est obligatoire (sauf la clé
	 *              primaire affectée dans la méthode).
	 * @throws Spi4jValidationException Si le tuple n'est pas valide
	 */
	void create(TypeDto p_dto) throws Spi4jValidationException;

	/**
	 * Obtenir le tuple à partir de sa clé primaire.
	 *
	 * @param p_id (In)(*) La clé primaire de l'objet recherché.
	 * @return Le tuple désiré.
	 */
	TypeDto findById(TypeId p_id);

	/**
	 * Obtenir la liste des tuples.
	 *
	 * @return La liste des tuples.
	 */
	List<TypeDto> findAll();

	/**
	 * Obtenir la liste des tuples ordonnée selon une colonne.
	 *
	 * @param p_orderByColumn Colonne pour le tri
	 * @return La liste des tuples.
	 */
	List<TypeDto> findAll(Columns_Enum p_orderByColumn);

	/**
	 * Mettre à jour le tuple.
	 *
	 * @param p_dto (In)(*) Le tuple à mettre à jour est obligatoire.
	 * @throws Spi4jValidationException Si le tuple n'est pas valide
	 */
	void update(TypeDto p_dto) throws Spi4jValidationException;

	/**
	 * Supprimer un tuple à partir de sa clé primaire.
	 *
	 * @param p_dto (In)(*) Le tuple à supprimer avec la clé primaire.
	 * @throws Spi4jValidationException Si le tuple n'est pas valide
	 */
	void delete(TypeDto p_dto) throws Spi4jValidationException;

	/**
	 * Supprimer tous les tuples.
	 *
	 * @return Le nombre de ligne(s) supprimée(s).
	 */
	int deleteAll();

	/**
	 * Supprime des tuples à partir d'un TableCriteria (le critère sql sera généré
	 * automatiquement).
	 * <h4>Exemple:</h4>
	 *
	 * <pre>
	 * final TableCriteria v_tableCriteria = new TableCriteria();
	 * v_tableCriteria.addCriteria(PersonneEntity_Itf.PersonneColumns_Enum.nom, Operator_Enum.contains, &quot;DUPONT&quot;);
	 * v_tableCriteria.addOrderBy(PersonneEntity_Itf.PersonneColumns_Enum.gradeId, false);
	 * personDao.deleteByCriteria(v_tableCriteria);
	 * </pre>
	 *
	 * @param p_tableCriteria un Criteria décrivant les lignes à supprimer
	 *                        (obligatoire)
	 * @return Le nombre de ligne(s) supprimée(s).
	 */
	int deleteByCriteria(final TableCriteria<Columns_Enum> p_tableCriteria);

	/**
	 * Obtenir la liste des tuples à partir de la valeur d'une colonne.
	 * <h4>Exemple:</h4>
	 *
	 * <pre>
	 * final List&lt;PersonneDto&gt; v_personnes = personneMatch.findByColumn(PersonneEntity_Itf.PersonneColumns_Enum.nom,
	 * 		&quot;DUPONT&quot;);
	 * </pre>
	 *
	 * @param p_column (In)(*) Le paramètre 'column' (obligatoire).
	 * @param p_value  (In) La valeur recherchée dans p_column, valeur qui peut être
	 *                 un DTO ou une Entity ou même null
	 * @return La liste des tuples.
	 */
	List<TypeDto> findByColumn(Columns_Enum p_column, Object p_value);

	/**
	 * Obtenir la liste des tuples à partir d'un critère et d'éventuels paramètres
	 * nommés.
	 * <h4>Exemple:</h4>
	 *
	 * <pre>
	 * final List&lt;PersonneDto&gt; v_personnes = personneMatch.findByCriteria(&quot;where grade_id &gt;= :grade_id&quot;,
	 * 		Collections.singletonMap(&quot;grade_id&quot;, colonelGradeId));
	 * </pre>
	 *
	 * @param p_queryCriteria     (In)(*) Le critère (obligatoire).
	 * @param p_map_value_by_name (In) Map des valeurs des paramètres selon les noms
	 *                            de paramètres (non obligatoire).
	 * @return La liste des tuples.
	 */
	List<TypeDto> findByCriteria(String p_queryCriteria, Map<String, ? extends Object> p_map_value_by_name);

	/**
	 * Obtenir la liste des tuples à partir d'un critère, d'éventuels paramètres
	 * nommés et de nombre de lignes pour une pagination.
	 * <h4>Exemple:</h4>
	 *
	 * <pre>
	 * final List&lt;PersonneDto&gt; v_personnes = personneMatch.findByCriteria(&quot;where grade_id &gt;= :grade_id&quot;,
	 * 		Collections.singletonMap(&quot;grade_id&quot;, colonelGradeId), 25, 100);
	 * </pre>
	 *
	 * @param p_queryCriteria     (In)(*) Le critère (obligatoire).
	 * @param p_map_value_by_name (In) Map des valeurs des paramètres selon les noms
	 *                            de paramètres (non obligatoire).
	 * @param p_nbLignesMax       (In) Le nombre de lignes maximum à ramener (-1 :
	 *                            toutes les lignes)
	 * @param p_nbLignesStart     (In) Nombre de lignes à passer avant de commencer
	 *                            la lecture.
	 * @return La liste des tuples.
	 */
	List<TypeDto> findByCriteria(String p_queryCriteria, Map<String, ? extends Object> p_map_value_by_name,
			int p_nbLignesMax, int p_nbLignesStart);

	/**
	 * Obtenir la liste des tuples à partir d'un TableCriteria (le critère sera
	 * généré automatiquement).
	 * <h4>Exemple:</h4>
	 *
	 * <pre>
	 * final TableCriteria v_tableCriteria = new TableCriteria();
	 * v_tableCriteria.addCriteria(PersonneEntity_Itf.PersonneColumns_Enum.nom, Operator_Enum.contains, &quot;DUPONT&quot;);
	 * v_tableCriteria.addOrderBy(PersonneEntity_Itf.PersonneColumns_Enum.gradeId, false);
	 * v_tableCriteria.setNbLignesStart(100); // On démarre à la 100ème ligne
	 * v_tableCriteria.setNbLignesMax(25); // On en lit 25 maximum
	 * final List&lt;PersonneEntity_Itf&gt; v_personnes = personneMatch.findByCriteria(v_tableCriteria);
	 * </pre>
	 *
	 * @param p_tableCriteria (In)(*) Le paramètre 'tableCriteria' (obligatoire).
	 * @return La liste des tuples.
	 */
	List<TypeDto> findByCriteria(TableCriteria<Columns_Enum> p_tableCriteria);

	/**
	 * Convertit une liste d'entités en liste de DTOs.
	 *
	 * @param p_tab_entity (In)(*) La liste des entités
	 * @return la liste des DTOs correspondant aux entités p_tab_entity
	 */
	List<TypeDto> convertListEntityToListDto(final List<TypeEntity> p_tab_entity);
}
