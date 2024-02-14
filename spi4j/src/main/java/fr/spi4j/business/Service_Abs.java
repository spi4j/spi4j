/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import java.util.List;
import java.util.Map;

import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.business.fetching.FetchingStrategyFetcher;
import fr.spi4j.business.fetching.FetchingStrategy_Abs;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.matching.Match_Itf;
import fr.spi4j.persistence.dao.TableCriteria;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Classe abstraite des services.
 * @author MINARM
 * @param <TypeId>
 *           Type de l'identifiant du DTO
 * @param <TypeDto>
 *           Type de DTO
 * @param <Columns_Enum>
 *           Type de l'énumération des colonnes
 */
public abstract class Service_Abs<TypeId, TypeDto extends Dto_Itf<TypeId>, Columns_Enum extends ColumnsNames_Itf>
         implements Service_Itf<TypeId, TypeDto>
{
   /**
    * @return Une instance du matching typée pour correspondre à l'instance du service.
    */
   protected abstract Match_Itf<TypeId, TypeDto, ? extends Entity_Itf<TypeId>, Columns_Enum> getMatch ();

   @Override
   public TypeDto save (final TypeDto p_dto) throws Spi4jValidationException
   {
      // validation du DTO passé en paramètre
      p_dto.validate();

      // si le DTO passé en paramètre n'a pas de clé primaire, c'est une création
      if (p_dto.getId() == null)
      {
         getMatch().create(p_dto);
      }
      // si le DTO passé en paramètre a une clé primaire : c'est une modification
      else
      {
         getMatch().update(p_dto);
      }
      return p_dto;
   }

   @Override
   public TypeDto findById (final TypeId p_id)
   {
      return getMatch().findById(p_id);
   }

   @Override
   public List<TypeDto> findAll ()
   {
      return getMatch().findAll();
   }

   /**
    * Obtenir la liste des tuples ordonnée selon une colonne.
    * @param p_orderByColumn
    *           Colonne pour le tri
    * @return La liste des tuples.
    */
   protected List<TypeDto> findAll (final Columns_Enum p_orderByColumn)
   {
      return getMatch().findAll(p_orderByColumn);
   }

   /**
    * Applique une fetching strategy sur une liste de DTOs.<br/>
    * Cette méthode a l'intérêt d'être simple à appeler dans l'implémentation d'un service,<br/>
    * et comme FetchingStrategyFetcher elle vérifie à la compilation que le type de la fetching strategy correspondant au type des DTOs.
    * @param p_fetchingStrategy
    *           Fetching Strategy à appliquer sur les DTOs
    * @param p_listeDto
    *           Liste de DTOs
    */
   protected void applyFetchingStrategy (final FetchingStrategy_Abs<TypeId, TypeDto> p_fetchingStrategy,
            final List<TypeDto> p_listeDto)
   {
      if (p_fetchingStrategy == null)
      {
         throw new IllegalArgumentException(
                  "Le paramètre 'fetchingStrategy' est obligatoire, il ne peut pas être 'null'\n");
      }
      FetchingStrategyFetcher.applyFetchingStrategy(p_fetchingStrategy, p_listeDto);
   }

   @Override
   public void delete (final TypeDto p_dto) throws Spi4jValidationException
   {
      getMatch().delete(p_dto);
   }

   // *********************************************************************************************************************
   // Remarque: les services findByColumn, findByCriteria et deleteByCriteria sont protected et non publics
   // car ils ne doivent pas être appelée directement depuis le client de la façade de service (par exemple la partie vue),
   // et la façade de service doit définir une méthode avec un nom signifiant fonctionnellement
   // dont l'implémentation appelera findByColumn, findByCriteria ou deleteByCriteria en étant elle liée aux entités persistentes.
   // *********************************************************************************************************************

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
   protected int deleteByCriteria (final TableCriteria<Columns_Enum> p_tableCriteria)
   {
      return getMatch().deleteByCriteria(p_tableCriteria);
   }

   /**
    * Obtenir la liste des tuples à partir de la valeur d'une colonne. <br/>
    * <h4>Exemple:</h4>
    *
    * <pre>
    * final List&lt;PersonneDto&gt; v_personnes = findByColumn(PersonneEntity_Itf.PersonneColumns_Enum.nom, &quot;DUPONT&quot;);
    * </pre>
    * @param p_column
    *           (In)(*) Le paramètre 'column' (obligatoire).
    * @param p_value
    *           (in) La valeur recherchée dans la colonne p_column, valeur qui peut être un DTO ou une Entity ou même null
    * @return La liste des tuples.
    */
   protected List<TypeDto> findByColumn (final Columns_Enum p_column, final Object p_value)
   {
      return getMatch().findByColumn(p_column, p_value);
   }

   /**
    * Obtenir la liste des tuples à partir d'un critère et d'éventuels paramètres nommés. <br/>
    * <h4>Exemple:</h4>
    *
    * <pre>
    * final List&lt;PersonneDto&gt; v_personnes = findByCriteria(&quot;where grade_id &gt;= :grade_id&quot;,
    *          Collections.singletonMap(&quot;grade_id&quot;, colonelGradeId));
    * </pre>
    * @param p_queryCriteria
    *           (In)(*) Le critère (obligatoire).
    * @param p_map_value_by_name
    *           (In) Map des valeurs des paramètres selon les noms de paramètres (non obligatoire).
    * @return La liste des tuples.
    */
   protected List<TypeDto> findByCriteria (final String p_queryCriteria,
            final Map<String, ? extends Object> p_map_value_by_name)
   {
      return getMatch().findByCriteria(p_queryCriteria, p_map_value_by_name);
   }

   /**
    * Obtenir la liste des tuples à partir d'un critère, d'éventuels paramètres nommés et de nombre de lignes pour une pagination. <br/>
    * <h4>Exemple:</h4>
    *
    * <pre>
    * final List&lt;PersonneDto&gt; v_personnes = findByCriteria(&quot;where grade_id &gt;= :grade_id&quot;,
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
   protected List<TypeDto> findByCriteria (final String p_queryCriteria,
            final Map<String, ? extends Object> p_map_value_by_name, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      return getMatch().findByCriteria(p_queryCriteria, p_map_value_by_name, p_nbLignesMax, p_nbLignesStart);
   }

   /**
    * Obtenir la liste des tuples à partir d'un TableCriteria (le critère sera généré automatiquement). <br/>
    * <h4>Exemple:</h4>
    *
    * <pre>
    * final TableCriteria v_tableCriteria = new TableCriteria();
    * v_tableCriteria.addCriteria(PersonneEntity_Itf.PersonneColumns_Enum.nom, Operator_Enum.contains, &quot;DUPONT&quot;);
    * v_tableCriteria.addOrderBy(PersonneEntity_Itf.PersonneColumns_Enum.gradeId, false);
    * v_tableCriteria.setNbLignesStart(100); // On démarre à la 100ème ligne
    * v_tableCriteria.setNbLignesMax(25); // On en lit 25 maximum
    * final List&lt;PersonneEntity_Itf&gt; v_personnes = findByCriteria(v_tableCriteria);
    * </pre>
    * @param p_tableCriteria
    *           (In)(*) Le paramètre 'tableCriteria' (obligatoire).
    * @return La liste des tuples.
    */
   protected List<TypeDto> findByCriteria (final TableCriteria<Columns_Enum> p_tableCriteria)
   {
      return getMatch().findByCriteria(p_tableCriteria);
   }
}
