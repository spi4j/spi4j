/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dbpopulate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.DaoUtils;
import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Classe centralisant la répartition des données : surcharge des clés étrangères (= FK) d'une table.
 * @author MINARM
 */
public final class RepartitionPopulateData
{
   private static final Logger c_log = LogManager.getLogger(RepartitionPopulateData.class);

   /**
    * Constructeur par défaut.
    */
   private RepartitionPopulateData ()
   {
      super();
   }

   /**
    * @param <EntityId>
    *           le type de l'identifiant de l'entité
    * @param <TypeEntity>
    *           type
    * @param <Type11>
    *           type
    * @param <ColumnsNames>
    *           type colonne
    * @param p_min
    *           Occurence min
    * @param p_max
    *           Occurence max
    * @param p_GradeDao
    *           Le Dao grade
    * @param p_PersonneDao
    *           Le Dao de personne
    * @param p_colonneJointure
    *           String = "setGrade"
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    * @throws ImpossibleCombDatabaseException
    *            Impossible de construire une base de données selon cette combinaison 0N - 01
    */
   public static <EntityId, TypeEntity extends Entity_Itf<EntityId>, Type11 extends Entity_Itf<?>, ColumnsNames extends ColumnsNames_Itf> void repartition_1N_11 (
            final int p_min, final int p_max, final Dao_Itf<?, Type11, ?> p_GradeDao,
            final Dao_Itf<EntityId, TypeEntity, ColumnsNames> p_PersonneDao, final ColumnsNames p_colonneJointure,
            final boolean p_joinWithReferentiel) throws ImpossibleCombDatabaseException
   {
      // Le nombre total d'entités côté 1
      final int v_nbGrades = DaoUtils.getCount(p_GradeDao);
      // Le nombre total d'entités côté N
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);

      checkMax1N(v_nbPersonnes, v_nbGrades, p_max, p_PersonneDao, p_GradeDao);
      checkMin1N(v_nbPersonnes, v_nbGrades, p_min, p_PersonneDao, p_GradeDao);

      doRepartition_N_1(false, p_min, p_max, p_PersonneDao, p_colonneJointure, p_GradeDao, p_joinWithReferentiel);
   }

   /**
    * @param <EntityId>
    *           le type de l'identifiant de l'entité
    * @param <TypeEntity>
    *           type
    * @param <Type11>
    *           type
    * @param <ColumnsNames>
    *           type colonne
    * @param p_min
    *           Occurence min
    * @param p_max
    *           Occurence max
    * @param p_GradeDao
    *           Dao grade
    * @param p_PersonneDao
    *           Dao personne
    * @param p_colonneJointure
    *           : String
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    * @throws ImpossibleCombDatabaseException
    *            Impossible de construire une base de données selon cette combinaison
    */
   public static <EntityId, TypeEntity extends Entity_Itf<EntityId>, Type11 extends Entity_Itf<?>, ColumnsNames extends ColumnsNames_Itf> void repartition_1N_01 (
            final int p_min, final int p_max, final Dao_Itf<?, Type11, ?> p_GradeDao,
            final Dao_Itf<EntityId, TypeEntity, ColumnsNames> p_PersonneDao, final ColumnsNames p_colonneJointure,
            final boolean p_joinWithReferentiel) throws ImpossibleCombDatabaseException
   {
      // Le nombre total d'entités côté 1
      final int v_nbGrades = DaoUtils.getCount(p_GradeDao);
      // Le nombre total d'entités côté N
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);

      checkMin1N(v_nbPersonnes, v_nbGrades, p_min, p_PersonneDao, p_GradeDao);
      doRepartition_N_1(true, p_min, p_max, p_PersonneDao, p_colonneJointure, p_GradeDao, p_joinWithReferentiel);
   }

   /**
    * @param <EntityId>
    *           le type de l'identifiant de l'entité
    * @param <TypeEntity>
    *           type
    * @param <Type11>
    *           type
    * @param <ColumnsNames>
    *           type colonne
    * @param p_min
    *           Occurence min
    * @param p_max
    *           Occurence max
    * @param p_PersonneDao
    *           Dao personne
    * @param p_colonneJointure
    *           : String
    * @param p_GradeDao
    *           Le Dao grade
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    * @throws ImpossibleCombDatabaseException
    *            Impossible de construire une base de données selon cette combinaison 0N - 01
    */
   public static <EntityId, TypeEntity extends Entity_Itf<EntityId>, Type11 extends Entity_Itf<?>, ColumnsNames extends ColumnsNames_Itf> void repartition_0N_11 (
            final int p_min, final int p_max, final Dao_Itf<?, Type11, ?> p_GradeDao,
            final Dao_Itf<EntityId, TypeEntity, ColumnsNames> p_PersonneDao, final ColumnsNames p_colonneJointure,
            final boolean p_joinWithReferentiel) throws ImpossibleCombDatabaseException
   {
      // Le nombre total d'entités côté 1
      final int v_nbGrades = DaoUtils.getCount(p_GradeDao);
      // Le nombre total d'entités côté N
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);

      checkMax1N(v_nbPersonnes, v_nbGrades, p_max, p_PersonneDao, p_GradeDao);
      doRepartition_N_1(false, p_min, p_max, p_PersonneDao, p_colonneJointure, p_GradeDao, p_joinWithReferentiel);
   }

   /**
    * @param <EntityId>
    *           le type de l'identifiant de l'entité
    * @param <TypeEntity>
    *           type
    * @param <Type11>
    *           type
    * @param <ColumnsNames>
    *           type colonne
    * @param p_min
    *           Occurence min
    * @param p_max
    *           Occurence max
    * @param p_GradeDao
    *           Dao grade
    * @param p_PersonneDao
    *           Dao personne
    * @param p_colonneJointure
    *           methode set_grade_id
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    */
   public static <EntityId, TypeEntity extends Entity_Itf<EntityId>, Type11 extends Entity_Itf<?>, ColumnsNames extends ColumnsNames_Itf> void repartition_0N_01 (
            final int p_min, final int p_max, final Dao_Itf<?, Type11, ?> p_GradeDao,
            final Dao_Itf<EntityId, TypeEntity, ColumnsNames> p_PersonneDao, final ColumnsNames p_colonneJointure,
            final boolean p_joinWithReferentiel)
   {
      doRepartition_N_1(true, p_min, p_max, p_PersonneDao, p_colonneJointure, p_GradeDao, p_joinWithReferentiel);
   }

   /**
    * @param <IdEntity>
    *           type de l'identifiant de l'entité
    * @param <TypeEntity>
    *           type entité
    * @param <Columns_Enum>
    *           type de colonnes
    * @param p_min
    *           Occurence min
    * @param p_max
    *           Occurence max
    * @param p_PersonneDao
    *           Dao perosnne
    * @param p_AdressesDao
    *           Dao adresse
    * @param p_columnJointure
    *           colonne de jointure
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    * @throws ImpossibleCombDatabaseException
    *            Impossible de construire une base de données selon cette combinaison 1N - 1N
    */
   public static <IdEntity, TypeEntity extends Entity_Itf<IdEntity>, Columns_Enum extends ColumnsNames_Itf> void repartition_11_1N (
            final int p_min, final int p_max, final Dao_Itf<?, ?, ?> p_PersonneDao,
            final Dao_Itf<IdEntity, TypeEntity, Columns_Enum> p_AdressesDao, final Columns_Enum p_columnJointure,
            final boolean p_joinWithReferentiel) throws ImpossibleCombDatabaseException
   {
      final int v_nbAdresses = DaoUtils.getCount(p_AdressesDao);
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);

      checkMax1N(v_nbAdresses, v_nbPersonnes, p_max, p_AdressesDao, p_PersonneDao);
      checkMin1N(v_nbAdresses, v_nbPersonnes, p_min, p_AdressesDao, p_PersonneDao);
      doRepartition_N_1(false, p_min, p_max, p_AdressesDao, p_columnJointure, p_PersonneDao, p_joinWithReferentiel);
   }

   /**
    * @param <IdEntity>
    *           type de l'identifiant de l'entité
    * @param <TypeEntity>
    *           type
    * @param <Columns_Enum>
    *           colonne
    * @param p_min
    *           Occurence min
    * @param p_max
    *           Occurence max
    * @param p_PersonneDao
    *           Dao personne
    * @param p_AdressesDao
    *           Dao adresse
    * @param p_columnJointure
    *           colonne de jointure
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    * @throws ImpossibleCombDatabaseException
    *            Impossible de construire une base de données selon cette combinaison 01 - 1N
    */
   public static <IdEntity, TypeEntity extends Entity_Itf<IdEntity>, Columns_Enum extends ColumnsNames_Itf> void repartition_01_1N (
            final int p_min, final int p_max, final Dao_Itf<?, ?, ?> p_PersonneDao,
            final Dao_Itf<IdEntity, TypeEntity, Columns_Enum> p_AdressesDao, final Columns_Enum p_columnJointure,
            final boolean p_joinWithReferentiel) throws ImpossibleCombDatabaseException
   {
      final int v_nbAdresses = DaoUtils.getCount(p_AdressesDao);
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);

      checkMax1N(v_nbAdresses, v_nbPersonnes, p_max, p_AdressesDao, p_PersonneDao);
      doRepartition_N_1(true, p_min, p_max, p_AdressesDao, p_columnJointure, p_PersonneDao, p_joinWithReferentiel);
   }

   /**
    * @param <IdEntity>
    *           type de l'identifiant de l'entité
    * @param <TypeEntity>
    *           type
    * @param <Columns_Enum>
    *           colonne
    * @param p_min
    *           Occurence min
    * @param p_max
    *           Occurence max
    * @param p_PersonneDao
    *           Dao personne
    * @param p_AdressesDao
    *           Dao adresse
    * @param p_columnJointure
    *           colonne de jointure
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    * @throws ImpossibleCombDatabaseException
    *            Impossible de construire une base de données selon cette combinaison 1N - 0N
    */
   public static <IdEntity, TypeEntity extends Entity_Itf<IdEntity>, Columns_Enum extends ColumnsNames_Itf> void repartition_11_0N (
            final int p_min, final int p_max, final Dao_Itf<?, ?, ?> p_PersonneDao,
            final Dao_Itf<IdEntity, TypeEntity, Columns_Enum> p_AdressesDao, final Columns_Enum p_columnJointure,
            final boolean p_joinWithReferentiel) throws ImpossibleCombDatabaseException
   {
      final int v_nbAdresses = DaoUtils.getCount(p_AdressesDao);
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);

      checkMin1N(v_nbAdresses, v_nbPersonnes, p_min, p_AdressesDao, p_PersonneDao);
      doRepartition_N_1(false, p_min, p_max, p_AdressesDao, p_columnJointure, p_PersonneDao, p_joinWithReferentiel);
   }

   /**
    * @param <IdEntity>
    *           type de l'identifiant de l'entité
    * @param <TypeEntity>
    *           type
    * @param <Columns_Enum>
    *           colonne
    * @param p_min
    *           Occurence min
    * @param p_max
    *           Occurence max
    * @param p_PersonneDao
    *           Dao personne
    * @param p_AdressesDao
    *           Dao adresse
    * @param p_columnJointure
    *           colonne de jointure
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    */
   public static <IdEntity, TypeEntity extends Entity_Itf<IdEntity>, Columns_Enum extends ColumnsNames_Itf> void repartition_01_0N (
            final int p_min, final int p_max, final Dao_Itf<?, ?, ?> p_PersonneDao,
            final Dao_Itf<IdEntity, TypeEntity, Columns_Enum> p_AdressesDao, final Columns_Enum p_columnJointure,
            final boolean p_joinWithReferentiel)
   {
      // aucune contrainte de volumétrie
      doRepartition_N_1(true, p_min, p_max, p_AdressesDao, p_columnJointure, p_PersonneDao, p_joinWithReferentiel);
   }

   /**
    * Lève une exception si <code>p_nbPersonnes > p_nbGrades * p_max</code>
    * @param p_nbPersonnes
    *           le nombre de personnes
    * @param p_nbGrades
    *           le nombre de grades
    * @param p_max
    *           le max
    * @param p_PersonneDao
    *           dao sur personne
    * @param p_GradeDao
    *           dao sur grade
    * @throws ImpossibleCombDatabaseException
    *            Impossible de construire une base de données selon cette combinaison 0N - 01
    */
   private static void checkMax1N (final int p_nbPersonnes, final int p_nbGrades, final int p_max,
            final Dao_Itf<?, ?, ?> p_PersonneDao, final Dao_Itf<?, ?, ?> p_GradeDao)
            throws ImpossibleCombDatabaseException
   {
      if (p_nbPersonnes > p_nbGrades * p_max)
      {
         throw new ImpossibleCombDatabaseException(
                  "C'est impossible de construire une base de données selon cette combinaison'\n"
                           + "Pensez a augmenter le nb de " + p_PersonneDao.getTableName() + " max ayant un "
                           + p_GradeDao.getTableName() + " ou a diminuer le nb de " + p_PersonneDao.getTableName());
      }
   }

   /**
    * Lève une exception si <code>p_nbPersonnes < p_nbGrades * p_min</code>
    * @param p_nbPersonnes
    *           le nombre de personnes
    * @param p_nbGrades
    *           le nombre de grades
    * @param p_min
    *           le min
    * @param p_PersonneDao
    *           dao sur personne
    * @param p_GradeDao
    *           dao sur grade
    * @throws ImpossibleCombDatabaseException
    *            Impossible de construire une base de données selon cette combinaison 0N - 01
    */
   private static void checkMin1N (final int p_nbPersonnes, final int p_nbGrades, final int p_min,
            final Dao_Itf<?, ?, ?> p_PersonneDao, final Dao_Itf<?, ?, ?> p_GradeDao)
            throws ImpossibleCombDatabaseException
   {
      if (p_nbPersonnes < p_nbGrades * p_min)
      {
         throw new ImpossibleCombDatabaseException(
                  "C'est impossible de construire une base de données selon cette combinaison'\n"
                           + "Pensez a augmenter le nb de " + p_PersonneDao.getTableName()
                           + " ou a diminuer le nb min de " + p_PersonneDao.getTableName() + " ayant un "
                           + p_GradeDao.getTableName());
      }

   }

   /**
    * @param <EntityId>
    *           le type de l'identifiant de l'entité
    * @param <TypeEntity>
    *           type
    * @param <ColumnsNames>
    *           le type de la colonne de jointure
    * @param <Type11>
    *           type
    * @param p_isDestination_01
    *           'true' C'est un cas 0..1
    * @param p_min
    *           min
    * @param p_max
    *           max
    * @param p_PersonneDao
    *           Dao personne
    * @param p_columnJointure
    *           la colonne de jointure
    * @param p_GradeDao
    *           Dao grade
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    */
   private static <EntityId, TypeEntity extends Entity_Itf<EntityId>, Type11 extends Entity_Itf<?>, ColumnsNames extends ColumnsNames_Itf> void doRepartition_N_1 (
            final boolean p_isDestination_01, final int p_min, final int p_max,
            final Dao_Itf<EntityId, TypeEntity, ColumnsNames> p_PersonneDao, final ColumnsNames p_columnJointure,
            final Dao_Itf<?, Type11, ?> p_GradeDao, final boolean p_joinWithReferentiel)
   {
      // Le nombre total d'entités côté 1
      final int v_nbGrades = DaoUtils.getCount(p_GradeDao);
      final List<Type11> v_listGrades;
      if (p_joinWithReferentiel)
      {
         v_listGrades = p_GradeDao.findAll();
      }
      else
      {
         v_listGrades = Collections.emptyList();
      }
      // Le nombre total d'entités côté N
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);
      // Le nombre de lignes dans l'entité côté N à mettre avec un null (par défaut 0)
      long v_nbPersonnesSansGrade = 0;

      // L'identifiant de l'entité côté N
      long v_indexPersonne = 0;
      // L'identifiant de l'entité côté 1
      long v_cptGrade = 0;
      long v_indexGrade = 0;

      // Si des entités côté N peuvent ne pas avoir d'entité 1 (clé étrangère nullable)
      if (p_isDestination_01)
      {
         // Combien d'entités côté N va-t-on mettre avec une clé étrangère nulle ? Par défaut : 10%
         v_nbPersonnesSansGrade = v_nbGrades * 10 / 100;
         // Cependant, si après avoir choisi ces 10% on se rend compte qu'il ne reste plus assez d'entité côté N, on en prend moins
         while ((v_nbGrades - v_nbPersonnesSansGrade) * p_max < v_nbPersonnes)
         {
            v_nbPersonnesSansGrade--;
         }
         // Si la table jointe est une table de référentielle, alors il faut aller chercher l'entité à l'index v_cptGrade
         if (p_joinWithReferentiel)
         {
            v_indexGrade = (Long) v_listGrades.get((int) v_cptGrade).getId();
         }
         else
         {
            v_indexGrade = v_cptGrade;
         }
         // Pour toutes les entités côté N sélectionnées pour avoir une clé étrangère nulle, on met à jour les lignes
         updateForeignKey(p_PersonneDao, p_columnJointure, v_indexGrade, v_indexPersonne, v_indexPersonne
                  + v_nbPersonnesSansGrade - 1);
         v_indexPersonne = v_indexPersonne + v_nbPersonnesSansGrade;
      }

      final Counter_Itf<Integer> v_counter = new Repartition1NCounter(p_min, p_max, v_nbPersonnes, v_nbGrades
               - v_nbPersonnesSansGrade, p_isDestination_01);

      int v_nbRowsToUpdate = v_counter.getNextValue();
      while (v_nbRowsToUpdate >= 0)
      {
         // Si la table jointe est une table de référentielle, alors il faut aller chercher l'entité à l'index v_cptGrade
         if (p_joinWithReferentiel)
         {
            v_indexGrade = (Long) v_listGrades.get((int) v_cptGrade).getId();
         }
         else
         {
            v_indexGrade = v_cptGrade;
         }
         c_log.debug("Affecter " + v_nbRowsToUpdate + " " + p_PersonneDao.getTableName() + " pour l'instance de "
                  + p_GradeDao.getTableName() + " " + v_indexGrade);
         // A ce niveau : on peut encore associer des personnes à ce grade
         updateForeignKey(p_PersonneDao, p_columnJointure, v_indexGrade, v_indexPersonne, v_indexPersonne
                  + v_nbRowsToUpdate - 1);
         v_indexPersonne = v_indexPersonne + v_nbRowsToUpdate;

         // Passser au grade suivant
         v_cptGrade++;
         v_nbRowsToUpdate = v_counter.getNextValue();
      }
   }

   /**
    * @param <TypeEntity>
    *           le type d'entité source
    * @param p_minDestination
    *           la volumétrie minimum sur la relation
    * @param p_maxDestination
    *           la volumétrie maximum sur la relation
    * @param p_minSource
    *           la volumétrie minimum sur la relation inverse
    * @param p_maxSource
    *           la volumétrie maximum sur la relation inverse
    * @param p_PersonneDao
    *           dao personne
    * @param p_CompetencesDao
    *           dao compétences
    * @param p_tableJointure
    *           le nom de la table de jointure
    * @param p_colonneJointure1
    *           le nom de la colonne correspondant à l'id de la personne
    * @param p_colonneJointure2
    *           le nom de la colonne correspondant à l'id de la compétence
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    * @throws ImpossibleCombDatabaseException
    *            Les données en entrée ne permettent pas de faire la répartition pour la montée en charge
    */
   public static <TypeEntity extends Entity_Itf<?>> void repartition_1N_1N (final int p_minDestination,
            final int p_maxDestination, final int p_minSource, final int p_maxSource,
            final Dao_Itf<?, TypeEntity, ?> p_PersonneDao, final Dao_Itf<?, ?, ?> p_CompetencesDao,
            final String p_tableJointure, final String p_colonneJointure1, final String p_colonneJointure2,
            final boolean p_joinWithReferentiel) throws ImpossibleCombDatabaseException
   {
      final int v_nbCompetences = DaoUtils.getCount(p_CompetencesDao);
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);

      // Contraintes de volumétrie
      checkMaxNNDestination(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      checkMinNNDestination(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      checkMaxNNSource(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      checkMinNNSource(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      doRepartition_N_N(false, false, p_minDestination, p_maxDestination, p_minSource, p_maxSource, p_PersonneDao,
               p_CompetencesDao, p_tableJointure, p_colonneJointure1, p_colonneJointure2, p_joinWithReferentiel);
   }

   /**
    * @param <TypeEntity>
    *           le type d'entité source
    * @param p_minDestination
    *           la volumétrie minimum sur la relation
    * @param p_maxDestination
    *           la volumétrie maximum sur la relation
    * @param p_minSource
    *           la volumétrie minimum sur la relation inverse
    * @param p_maxSource
    *           la volumétrie maximum sur la relation inverse
    * @param p_PersonneDao
    *           dao personne
    * @param p_CompetencesDao
    *           dao compétences
    * @param p_tableJointure
    *           le nom de la table de jointure
    * @param p_colonneJointure1
    *           le nom de la colonne correspondant à l'id de la personne
    * @param p_colonneJointure2
    *           le nom de la colonne correspondant à l'id de la compétence
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    * @throws ImpossibleCombDatabaseException
    *            Les données en entrée ne permettent pas de faire la répartition pour la montée en charge
    */
   public static <TypeEntity extends Entity_Itf<?>> void repartition_0N_1N (final int p_minDestination,
            final int p_maxDestination, final int p_minSource, final int p_maxSource,
            final Dao_Itf<?, TypeEntity, ?> p_PersonneDao, final Dao_Itf<?, ?, ?> p_CompetencesDao,
            final String p_tableJointure, final String p_colonneJointure1, final String p_colonneJointure2,
            final boolean p_joinWithReferentiel) throws ImpossibleCombDatabaseException
   {
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);
      final int v_nbCompetences = DaoUtils.getCount(p_CompetencesDao);

      // Contraintes de volumétrie comme la précédente à part le fait que la contrainte de mininum de personne pour une compétence n'est plus nécessaire
      checkMaxNNDestination(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      checkMinNNDestination(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      checkMaxNNSource(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      doRepartition_N_N(true, false, p_minDestination, p_maxDestination, p_minSource, p_maxSource, p_PersonneDao,
               p_CompetencesDao, p_tableJointure, p_colonneJointure1, p_colonneJointure2, p_joinWithReferentiel);
   }

   /**
    * @param <TypeEntity>
    *           le type d'entité source
    * @param p_minDestination
    *           la volumétrie minimum sur la relation
    * @param p_maxDestination
    *           la volumétrie maximum sur la relation
    * @param p_minSource
    *           la volumétrie minimum sur la relation inverse
    * @param p_maxSource
    *           la volumétrie maximum sur la relation inverse
    * @param p_PersonneDao
    *           dao personne
    * @param p_CompetencesDao
    *           dao compétences
    * @param p_tableJointure
    *           le nom de la table de jointure
    * @param p_colonneJointure1
    *           le nom de la colonne correspondant à l'id de la personne
    * @param p_colonneJointure2
    *           le nom de la colonne correspondant à l'id de la compétence
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    * @throws ImpossibleCombDatabaseException
    *            Les données en entrée ne permettent pas de faire la répartition pour la montée en charge
    */
   public static <TypeEntity extends Entity_Itf<?>> void repartition_1N_0N (final int p_minDestination,
            final int p_maxDestination, final int p_minSource, final int p_maxSource,
            final Dao_Itf<?, TypeEntity, ?> p_PersonneDao, final Dao_Itf<?, ?, ?> p_CompetencesDao,
            final String p_tableJointure, final String p_colonneJointure1, final String p_colonneJointure2,
            final boolean p_joinWithReferentiel) throws ImpossibleCombDatabaseException
   {
      final int v_nbCompetences = DaoUtils.getCount(p_CompetencesDao);
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);

      checkMaxNNDestination(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      checkMaxNNSource(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      checkMinNNSource(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      doRepartition_N_N(false, true, p_minDestination, p_maxDestination, p_minSource, p_maxSource, p_PersonneDao,
               p_CompetencesDao, p_tableJointure, p_colonneJointure1, p_colonneJointure2, p_joinWithReferentiel);
   }

   /**
    * @param <TypeEntity>
    *           le type d'entité source
    * @param p_minDestination
    *           la volumétrie minimum sur la relation
    * @param p_maxDestination
    *           la volumétrie maximum sur la relation
    * @param p_minSource
    *           la volumétrie minimum sur la relation inverse
    * @param p_maxSource
    *           la volumétrie maximum sur la relation inverse
    * @param p_PersonneDao
    *           dao personne
    * @param p_CompetencesDao
    *           dao compétences
    * @param p_tableJointure
    *           le nom de la table de jointure
    * @param p_colonneJointure1
    *           le nom de la colonne correspondant à l'id de la personne
    * @param p_colonneJointure2
    *           le nom de la colonne correspondant à l'id de la compétence
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    * @throws ImpossibleCombDatabaseException
    *            Les données en entrée ne permettent pas de faire la répartition pour la montée en charge
    */
   public static <TypeEntity extends Entity_Itf<?>> void repartition_0N_0N (final int p_minDestination,
            final int p_maxDestination, final int p_minSource, final int p_maxSource,
            final Dao_Itf<?, TypeEntity, ?> p_PersonneDao, final Dao_Itf<?, ?, ?> p_CompetencesDao,
            final String p_tableJointure, final String p_colonneJointure1, final String p_colonneJointure2,
            final boolean p_joinWithReferentiel) throws ImpossibleCombDatabaseException
   {
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);
      final int v_nbCompetences = DaoUtils.getCount(p_CompetencesDao);

      checkMaxNNDestination(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      checkMaxNNSource(v_nbPersonnes, v_nbCompetences, p_maxDestination, p_PersonneDao, p_CompetencesDao);
      doRepartition_N_N(true, true, p_minDestination, p_maxDestination, p_minSource, p_maxSource, p_PersonneDao,
               p_CompetencesDao, p_tableJointure, p_colonneJointure1, p_colonneJointure2, p_joinWithReferentiel);
   }

   /**
    * Lève une exception si <code>p_nbPersonnes > p_nbCompetences * p_maxDestination</code>
    * @param p_nbPersonnes
    *           le nombre de personnes
    * @param p_nbCompetences
    *           le nombre de compétences
    * @param p_maxDestination
    *           le max
    * @param p_PersonneDao
    *           dao sur personne
    * @param p_CompetencesDao
    *           dao sur compétence
    * @throws ImpossibleCombDatabaseException
    *            Les données en entrée ne permettent pas de faire la répartition pour la montée en charge
    */
   private static void checkMaxNNDestination (final int p_nbPersonnes, final int p_nbCompetences,
            final int p_maxDestination, final Dao_Itf<?, ?, ?> p_PersonneDao, final Dao_Itf<?, ?, ?> p_CompetencesDao)
            throws ImpossibleCombDatabaseException
   {
      if (p_nbPersonnes > p_nbCompetences * p_maxDestination)
      {
         throw new ImpossibleCombDatabaseException(
                  "C'est impossible de construire une base de données selon cette combinaison : Si on venait a affecter a chaque "
                           + p_PersonneDao.getTableName() + " son nb max de " + p_CompetencesDao.getTableName() + " ,"
                           + " le nb de " + p_PersonneDao.getTableName()
                           + " reste supérieur et on se retrouvera avec des " + p_PersonneDao.getTableName() + " sans "
                           + p_CompetencesDao.getTableName() + "\n" + "Pensez à diminuer le nb de "
                           + p_PersonneDao.getTableName() + " ou à augmenter le nb max de "
                           + p_CompetencesDao.getTableName() + "/" + p_PersonneDao.getTableName());
      }
   }

   /**
    * Lève une exception si <code>p_nbPersonnes < p_nbCompetences * p_minDestination</code>
    * @param p_nbPersonnes
    *           le nombre de personnes
    * @param p_nbCompetences
    *           le nombre de compétences
    * @param p_minDestination
    *           le min
    * @param p_PersonneDao
    *           dao sur personne
    * @param p_CompetencesDao
    *           dao sur compétence
    * @throws ImpossibleCombDatabaseException
    *            Les données en entrée ne permettent pas de faire la répartition pour la montée en charge
    */
   private static void checkMinNNDestination (final int p_nbPersonnes, final int p_nbCompetences,
            final int p_minDestination, final Dao_Itf<?, ?, ?> p_PersonneDao, final Dao_Itf<?, ?, ?> p_CompetencesDao)
            throws ImpossibleCombDatabaseException
   {
      if (p_nbPersonnes < p_nbCompetences * p_minDestination)
      {
         throw new ImpossibleCombDatabaseException(
                  "C'est impossible de construire une base de données selon cette combinaison : le nb minimal de "
                           + p_CompetencesDao.getTableName() + " que peut avoir une " + p_PersonneDao.getTableName()
                           + " est grand par rapport au nb de " + p_PersonneDao.getTableName() + ",\n "
                           + ".ie. on se retrouvera avec des " + p_CompetencesDao.getTableName() + " sans "
                           + p_PersonneDao.getTableName() + "\n" + "Pensez à augmenter le nb de "
                           + p_PersonneDao.getTableName() + " ou à diminuer le nb de "
                           + p_CompetencesDao.getTableName() + "/" + p_PersonneDao.getTableName());
      }
   }

   /**
    * Lève une exception si <code>p_nbPersonnes > p_nbCompetences * p_maxSource</code>
    * @param p_nbPersonnes
    *           le nombre de personnes
    * @param p_nbCompetences
    *           le nombre de compétences
    * @param p_maxSource
    *           le max
    * @param p_PersonneDao
    *           dao sur personne
    * @param p_CompetencesDao
    *           dao sur compétence
    * @throws ImpossibleCombDatabaseException
    *            Les données en entrée ne permettent pas de faire la répartition pour la montée en charge
    */
   private static void checkMaxNNSource (final int p_nbPersonnes, final int p_nbCompetences, final int p_maxSource,
            final Dao_Itf<?, ?, ?> p_PersonneDao, final Dao_Itf<?, ?, ?> p_CompetencesDao)
            throws ImpossibleCombDatabaseException
   {
      if (p_nbPersonnes > p_nbCompetences * p_maxSource)
      {
         throw new ImpossibleCombDatabaseException(
                  "C'est impossible de construire une base de données selon cette combinaison :  Si on venait a affecter a chaque "
                           + p_CompetencesDao.getTableName() + " son nb max de " + p_PersonneDao.getTableName() + " ,"
                           + " le nb de " + p_PersonneDao.getTableName()
                           + " reste supérieur \n et on se retrouvera avec des " + p_PersonneDao.getTableName()
                           + " sans " + p_CompetencesDao.getTableName() + "\n" + "Pensez à augmenter le nb max de "
                           + p_PersonneDao.getTableName() + "/" + p_CompetencesDao.getTableName()
                           + " ou a diminuer le nb de " + p_PersonneDao.getTableName());

      }
   }

   /**
    * Lève une exception si <code>p_nbPersonnes < p_nbCompetences * p_minSource</code>
    * @param p_nbPersonnes
    *           le nombre de personnes
    * @param p_nbCompetences
    *           le nombre de compétences
    * @param p_minSource
    *           le min
    * @param p_PersonneDao
    *           dao sur personne
    * @param p_CompetencesDao
    *           dao sur compétence
    * @throws ImpossibleCombDatabaseException
    *            Les données en entrée ne permettent pas de faire la répartition pour la montée en charge
    */
   private static void checkMinNNSource (final int p_nbPersonnes, final int p_nbCompetences, final int p_minSource,
            final Dao_Itf<?, ?, ?> p_PersonneDao, final Dao_Itf<?, ?, ?> p_CompetencesDao)
            throws ImpossibleCombDatabaseException
   {
      if (p_nbPersonnes < p_nbCompetences * p_minSource)
      {
         throw new ImpossibleCombDatabaseException(
                  "C'est impossible de construire une base de données selon cette combinaison :\n"
                           + " le nb minimal de " + p_PersonneDao.getTableName() + " que peut avoir une "
                           + p_CompetencesDao.getTableName() + " est grand par rapport au nb de "
                           + p_PersonneDao.getTableName() + "\n" + "Pensez à diminuer le nb min de "
                           + p_PersonneDao.getTableName() + "/" + p_CompetencesDao.getTableName()
                           + " ,ou a augmenter le nb de " + p_PersonneDao.getTableName());
      }
   }

   /**
    * @param <TypeEntity>
    *           le type d'entité source
    * @param <TypeEntityN>
    *           le type d'entité destination
    * @param p_isSource_0_N
    *           'true' Si la cardinalité se commence par 0..N
    * @param p_isDestination_0_N
    *           'true' Si la cardinalité se termine par 0..N
    * @param p_minDestination
    *           la volumétrie minimum sur la relation
    * @param p_maxDestination
    *           la volumétrie maximum sur la relation
    * @param p_minSource
    *           la volumétrie minimum sur la relation inverse
    * @param p_maxSource
    *           la volumétrie maximum sur la relation inverse
    * @param p_PersonneDao
    *           dao personne
    * @param p_CompetencesDao
    *           dao compétences
    * @param p_tableJointure
    *           le nom de la table de jointure
    * @param p_colonneJointure1
    *           le nom de la colonne correspondant à l'id de la personne
    * @param p_colonneJointure2
    *           le nom de la colonne correspondant à l'id de la compétence
    * @param p_joinWithReferentiel
    *           flag indiquant si la table pointée par cette relation est une table du référentiel ou non
    */
   private static <TypeEntity extends Entity_Itf<?>, TypeEntityN extends Entity_Itf<?>> void doRepartition_N_N (
            final boolean p_isSource_0_N, final boolean p_isDestination_0_N, final int p_minDestination,
            final int p_maxDestination, final int p_minSource, final int p_maxSource,
            final Dao_Itf<?, TypeEntity, ?> p_PersonneDao, final Dao_Itf<?, TypeEntityN, ?> p_CompetencesDao,
            final String p_tableJointure, final String p_colonneJointure1, final String p_colonneJointure2,
            final boolean p_joinWithReferentiel)
   {
      c_log.debug("Les paramètres p_isSource_0_N, p_minSource et p_maxSource ne sont pas utilisés pour le moment : "
               + p_isSource_0_N + ", " + p_minSource + ", " + p_maxSource);

      final int v_nbCompetences = DaoUtils.getCount(p_CompetencesDao);
      final int v_nbPersonnes = DaoUtils.getCount(p_PersonneDao);

      final List<TypeEntityN> v_listCompetences;
      if (p_joinWithReferentiel)
      {
         v_listCompetences = p_CompetencesDao.findAll();
      }
      else
      {
         v_listCompetences = Collections.emptyList();
      }

      // Le nombre de lignes dans l'entité source à mettre sans entité destination (par défaut 0)
      long v_nbPersonnesSansCompetence = 0;

      // L'identifiant de l'entité côté 1
      int v_indexPersonne = 0;
      // L'identifiant de l'entité côté table de jointure
      long v_cptJointure = 0;
      long v_indexJointure = 0;

      // Si des entités source peuvent ne pas avoir d'entité destination
      if (p_isDestination_0_N)
      {
         // Combien d'entités source va-t-on mettre avec aucune correspondance dans la table de jointure ? Par défaut : 10%
         v_nbPersonnesSansCompetence = v_nbCompetences * 10 / 100;
         // Cependant, si après avoir choisi ces 10% on se rend compte qu'il ne reste plus assez d'entité source, on en prend moins
         while ((v_nbCompetences - v_nbPersonnesSansCompetence) * p_maxDestination < v_nbPersonnes)
         {
            v_nbPersonnesSansCompetence--;
         }
         // Pour toutes les entités source sélectionnées pour n'avoir aucune correspondance dans la table de jointure, on ne fait rien
         for (int v_cptPersonneSansGrade = 0; v_cptPersonneSansGrade < v_nbPersonnesSansCompetence; v_cptPersonneSansGrade++)
         {
            v_indexPersonne++;
         }
      }

      final Counter_Itf<Integer> v_counter = new Repartition1NCounter(p_minDestination, p_maxDestination, v_nbPersonnes
               - v_nbPersonnesSansCompetence, v_nbCompetences, p_isDestination_0_N);

      int v_nbRowsToUpdate = v_counter.getNextValue();
      while (v_nbRowsToUpdate >= 0)
      {
         c_log.info("Affecter " + v_nbRowsToUpdate + " " + p_CompetencesDao.getTableName() + " pour l'instance de "
                  + p_PersonneDao.getTableName() + " " + v_indexPersonne);
         for (int v_cptRowUpdating = 0; v_cptRowUpdating < v_nbRowsToUpdate; v_cptRowUpdating++)
         {
            // Si la table jointe est une table de référentielle, alors il faut aller chercher l'entité à l'index v_cptGrade
            if (p_joinWithReferentiel)
            {
               v_indexJointure = (Long) v_listCompetences.get((int) (v_cptJointure % v_nbCompetences)).getId();
            }
            else
            {
               v_indexJointure = v_cptJointure % v_nbCompetences;
            }
            // A ce niveau : on peut encore associer des compétences à cette personne
            // Remplir le tuple de 'Personne'
            insertInNN(p_PersonneDao, p_tableJointure, p_colonneJointure1, p_colonneJointure2, v_indexPersonne,
                     v_indexJointure);
            v_cptJointure++;
         } // FIN for

         // Passser au grade suivant
         v_indexPersonne++;
         v_nbRowsToUpdate = v_counter.getNextValue(v_nbPersonnes - v_indexPersonne);
      }

   }

   /**
    * Insertion d'une ligne dans une table de jointure.
    * @param p_dao
    *           le dao qui exécutera la requête (peu importe son type)
    * @param p_tableJointure
    *           le nom de la table de jointure
    * @param p_colonneJointure1
    *           le nom de la colonne 1
    * @param p_colonneJointure2
    *           le nom de la colonne 2
    * @param p_indexPersonne
    *           la valeur à mettre dans la colonne 1
    * @param p_indexCompetence
    *           la valeur à mettre dans la colonne 2
    */
   private static void insertInNN (final Dao_Itf<?, ?, ?> p_dao, final String p_tableJointure,
            final String p_colonneJointure1, final String p_colonneJointure2, final long p_indexPersonne,
            final long p_indexCompetence)
   {
      final String v_updateRequete = "INSERT into " + p_tableJointure + " (" + p_colonneJointure1 + ", "
               + p_colonneJointure2 + ") values (:value1, :value2)";
      final Map<String, Object> v_mapParametres = new HashMap<>(2);
      v_mapParametres.put("value1", p_indexPersonne);
      v_mapParametres.put("value2", p_indexCompetence);
      final int v_result = p_dao.executeUpdate(v_updateRequete, v_mapParametres);
      // si la requête n'a pas retourné de résultat (ou plus d'un)
      if (v_result != 1)
      {
         throw new Spi4jRuntimeException("Erreur lors de l'insertion dans la table de jointure : " + p_tableJointure,
                  "Vérifier la requête " + v_updateRequete + " (paramètres : " + v_mapParametres + ")");
      }
   }

   /**
    * Met à jour une plage d'entités (voir p_idEntityStart et p_idEntityEnd inclus).
    * @param <TypeId>
    *           type id
    * @param <IdEntity>
    *           type id
    * @param <TypeEntity>
    *           type entité
    * @param <ColumnsNames>
    *           type colonnes
    * @param p_dao
    *           dao
    * @param p_colonneJointure
    *           colonne de jointure
    * @param p_foreignKeyValue
    *           valeur à mettre à jour dans la colonne
    * @param p_idEntityStart
    *           id de l'entité de début à mettre à jour
    * @param p_idEntityEnd
    *           id de l'entité de fin à mettre à jour
    * @example updateForeignKey(p_PersonneDao, p_columnJointure, v_indexGrade, v_Personne.getId(), v_Personne.getId() + 2);
    */
   private static <TypeId, IdEntity, TypeEntity extends Entity_Itf<IdEntity>, ColumnsNames extends ColumnsNames_Itf> void updateForeignKey (
            final Dao_Itf<IdEntity, TypeEntity, ColumnsNames> p_dao, final ColumnsNames p_colonneJointure,
            final TypeId p_foreignKeyValue, final Long p_idEntityStart, final Long p_idEntityEnd)
   {
      for (long v_i = p_idEntityStart; v_i <= p_idEntityEnd; v_i++)
      {
         final String v_updateRequete = "UPDATE " + p_colonneJointure.getTableName() + " SET "
                  + p_colonneJointure.getCompletePhysicalName() + "=:value" + " WHERE "
                  + p_dao.getColumnId().getCompletePhysicalName() + "=:id";
         final Map<String, Object> v_mapParametres = new HashMap<>(2);
         v_mapParametres.put("value", p_foreignKeyValue);
         v_mapParametres.put("id", v_i);
         final int v_result = p_dao.executeUpdate(v_updateRequete, v_mapParametres);
         // si la requête n'a pas retourné de résultat (ou plus d'un)
         if (v_result != 1)
         {
            throw new Spi4jRuntimeException("Erreur lors de la mise à jour d'une clé étrangère : "
                     + p_colonneJointure.getCompletePhysicalName(), "Vérifier la requête " + v_updateRequete
                     + " (paramètres : " + v_mapParametres + ")");
         }
      }
      // FIXME : Requête de mise à jour ensembliste
      // final String v_updateRequete = "UPDATE " + p_colonneJointure.getTableName() + " SET "
      // + p_colonneJointure.getPhysicalColumnName() + "=:value" + " WHERE "
      // + p_dao.getColumnId().getPhysicalColumnName() + " between :idStart AND :idEnd";
      // // + p_dao.getColumnId().getPhysicalColumnName() + " <= ";
      // final Map<String, Object> v_mapParametres = new HashMap<String, Object>(3);
      // v_mapParametres.put("value", p_foreignKeyValue);
      // v_mapParametres.put("idStart", p_idEntityStart);
      // v_mapParametres.put("idEnd", p_idEntityEnd);
      // final int v_result = p_dao.executeUpdate(v_updateRequete, v_mapParametres);
      // // si la requête n'a pas retourné de résultat (ou plus d'un)
      // if (v_result != 1)
      // {
      // throw new Spi4jRuntimeException("Erreur lors de la mise à jour d'une clé étrangère : "
      // + p_colonneJointure.getCompletePhysicalName(), "Vérifier la requête " + v_updateRequete
      // + " (paramètres : " + v_mapParametres + ")");
      // }
   }

}
