/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import java.util.List;
import java.util.Map;

import fr.spi4j.entity.Service_Itf;
import fr.spi4j.persistence.dao.TableCriteria;

/**
 * Définit le contrat de services spécifiques pour un type 'Personne'.
 */
public interface MyPersonneEntityService_Itf extends Service_Itf<Long, MyPersonneEntity_Itf>
{
   /**
    * Méthode de test pour les requirements et le proxy de cache.
    * @return 0.
    */
   public int methode ();

   /**
    * Méthode 2 de test pour le proxy de cache.
    * @return 0.
    */
   public int methode2 ();

   /**
    * Méthode jetant une exception de test pour les requirements, le proxy de cache et le proxy de log.
    */
   public void methodeJetantException ();

   /**
    * Méthode jetant une exception pour exigence fonctionnelle.
    */
   public void methodeJetantExceptionExigenceFonctionnelle ();

   /**
    * Méthode jetant une exception pour exigence technique.
    */
   public void methodeJetantExceptionExigenceTechnique ();

   /**
    * Méthode de test avec paramètre pour ServiceSimulatedRemotingProxy.
    * @param p_personne
    *           MyPersonneEntity.
    */
   public void changeNom (final MyPersonneEntity p_personne);

   /**
    * Méthode findByCriteria
    * @param p_tableCriteria
    *           Table Criteria
    * @return La liste des personnes.
    */
   public List<MyPersonneEntity_Itf> findByCriteria (final TableCriteria<MyPersonneColumns_Enum> p_tableCriteria);

   /**
    * Méthode findByCriteria
    * @param p_queryCriteria
    *           La requête SQL définissant le critère.
    * @param p_map_value_by_name
    *           Map contenant la valeur des paramètres.
    * @return La liste des personnes.
    */
   public List<MyPersonneEntity_Itf> findByCriteria (final String p_queryCriteria,
            final Map<String, ? extends Object> p_map_value_by_name);

   /**
    * Méthode findByCriteria
    * @param p_queryCriteria
    *           La requête SQL définissant le critère.
    * @param p_map_value_by_name
    *           Map contenant la valeur des paramètres.
    * @param p_nbLignesMax
    *           Le nombre de lignes retenues.
    * @param p_nbLignesStart
    *           Le nombre de ligne à partir de laquelle on commence.
    * @return La liste des personnes.
    */
   public List<MyPersonneEntity_Itf> findByCriteria (final String p_queryCriteria,
            final Map<String, ? extends Object> p_map_value_by_name, final int p_nbLignesMax, final int p_nbLignesStart);

   /**
    * Méthode deleteByCriteria
    * @param p_tableCriteria
    *           Table Criteria
    * @return Nombre de lignes supprimées.
    */
   public int deleteByCriteria (final TableCriteria<MyPersonneColumns_Enum> p_tableCriteria);

   /**
    * Méthode findByColumn
    * @param p_column
    *           La colonne dans laquelle rechercher les éléments.
    * @param p_value
    *           Les valeurs recherchées dans la colonne.
    * @return La liste des personnes.
    */
   public List<MyPersonneEntity_Itf> findByColumn (final MyPersonneColumns_Enum p_column, final Object p_value);

   /**
    * Méthode findAll
    * @param p_orderByColumn
    *           Colonne pour le tri
    * @return La liste des personnes.
    */
   public List<MyPersonneEntity_Itf> findAll (final MyPersonneColumns_Enum p_orderByColumn);
}
