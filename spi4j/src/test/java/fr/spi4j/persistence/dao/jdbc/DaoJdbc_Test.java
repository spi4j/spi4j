/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jEntityNotFoundException;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.ParamPersistence_Abs;
import fr.spi4j.persistence.dao.Cursor_Abs;
import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.testapp.MyGradeColumns_Enum;
import fr.spi4j.testapp.MyGradeDao_Itf;
import fr.spi4j.testapp.MyGradeEntity;
import fr.spi4j.testapp.MyGradeEntity_Itf;
import fr.spi4j.testapp.MyParamPersistence;
import fr.spi4j.testapp.MyPersonneColumns_Enum;
import fr.spi4j.testapp.MyPersonneDao_Itf;
import fr.spi4j.testapp.MyPersonneEntity_Itf;
import fr.spi4j.testapp.MyUserPersistence;
import fr.spi4j.testapp.MyWrongGradeColumns_Enum;
import fr.spi4j.testapp.MyWrongGradeDao_Itf;
import fr.spi4j.testapp.test.DatabaseInitialization;

/**
 * Classe de test de {@link DaoJdbc_Abs}
 * @author MINARM
 */
public class DaoJdbc_Test
{

   /** Le 'UserPersistence' de l'application. */
   private static MyUserPersistence userPersistence;

   private static Level initalLogLevel;

   /**
    * Méthode d'initialisation de la classe de tests.
    */
   @BeforeAll
   public static void setUpBeforeClass ()
   {
      userPersistence = MyParamPersistence.getUserPersistence();
      DatabaseInitialization.initDatabase();
      initalLogLevel = LogManager.getLogger(DaoJdbc_Abs.class).getLevel();
   }

   /**
    * Méthode d'initialisation de tests.
    */
   @BeforeEach
   public void setUp ()
   {
      userPersistence.begin();
      // activer les logs pour JdbcStatementPreparator
      // Logger.getLogger(DaoJdbc_Abs.class).setLevel(Level.DEBUG);
   }

   /**
    * Test du Dao.
    * @throws Spi4jValidationException
    *            e
    */
   @Test
   public void testDao () throws Spi4jValidationException
   {
      final MyGradeDao_Itf v_dao = userPersistence.getInstanceOfGradeDao();

      final MyGradeEntity_Itf v_grade = new MyGradeEntity(null, "Colonel", "COL", new Date(), false);
      v_dao.create(v_grade);
      v_dao.update(v_grade);
      v_dao.delete(v_grade);
      try
      {
         v_dao.delete(v_grade);
         fail("Erreur attendue");
      }
      catch (final Spi4jEntityNotFoundException v_e)
      {
         assertNotNull(v_e, "Erreur attendue");
      }

   }

   /**
    * Test du findByCriteria sans paramètres.
    */
   @Test
   public void testFindByCriteriaNoParameters ()
   {
      final MyGradeDao_Itf v_dao = userPersistence.getInstanceOfGradeDao();
      final List<MyGradeEntity_Itf> v_listeGrades = v_dao
               .findByCriteria("where " + MyGradeColumns_Enum.libelle + " is not null", null);
      assertNotNull(v_listeGrades, "Tous les grades devraient avoir un libellé");
   }

   /**
    * Test execute query et logs des paramètres.
    */
   @Test
   public void testExecuteQuery ()
   {
      final String v_query = "select 1 as VALUE where '1' = :boolean_true and '0' = :boolean_false and '1' = :null_value and '1' <> :non_null_value and 1 in :collection";
      final Dao_Itf<?, ?, ?> v_dao = MyParamPersistence.getUserPersistence().getDefaultDao();
      // les logs doivent être en debug comme mis dans setUp pour logguer les valeurs de ces paramètres
      final Map<String, Object> v_parameters = new HashMap<>();
      v_parameters.put("boolean_true", true);
      v_parameters.put("boolean_false", false);
      v_parameters.put("null_value", null);
      v_parameters.put("non_null_value", "Non null");
      v_parameters.put("collection", Arrays.asList(1, 2));

      final Cursor_Abs v_cursor2 = v_dao.executeQuery(v_query, v_parameters);
      v_cursor2.close();

      final Cursor_Abs v_cursor = v_dao.executeQuery(v_query, v_parameters, "commentaire1", "commentaire2");
      v_cursor.close();
   }

   /**
    * Test des paramètres dans un statement.
    */
   @Test
   public void testExecuteQuery2 ()
   {
      final String v_query = "select 1 as VALUE where '1' = :p0 and '1' = :p1 and '1' = :p2 and '1' = :p3 and '1' = :p4 and '1' = :p5 and '1' = :p6 and '1' = :p7 and '1' = :p8 and '1' = :p9 and '1' = :p10 and '1' = :p11 ";
      final Dao_Itf<?, ?, ?> v_dao = MyParamPersistence.getUserPersistence().getDefaultDao();
      // les logs doivent être en debug comme mis dans setUp pour logguer les valeurs de ces paramètres
      final Map<String, Object> v_parameters = new HashMap<>();
      v_parameters.put("p0", null);
      v_parameters.put("p1", true);
      v_parameters.put("p2", new Date());
      v_parameters.put("p3", Integer.valueOf(0));
      v_parameters.put("p4", Long.valueOf(0));
      v_parameters.put("p5", Float.valueOf(0));
      v_parameters.put("p6", Double.valueOf(0));
      v_parameters.put("p7", new BigDecimal("100"));
      v_parameters.put("p8", new BigInteger("100"));
      v_parameters.put("p9", Byte.valueOf((byte) 0));
      v_parameters.put("p10", MyGradeColumns_Enum.trigramme);
      v_parameters.put("p11", Arrays.asList(1, 2));

      final Cursor_Abs v_cursor2 = v_dao.executeQuery(v_query, v_parameters);
      v_cursor2.close();
   }

   /**
    * Test du nom de table dans DAO.
    */
   @Test
   public void testExecuteUpdate ()
   {
      final String v_query = "delete from " + MyPersonneColumns_Enum.c_tableName + " where 0 = 1";
      final MyPersonneDao_Itf v_dao = MyParamPersistence.getUserPersistence().getInstanceOfPersonneDao();

      v_dao.executeUpdate(v_query, null);

      v_dao.executeUpdate(v_query, null, "commentaire1", "commentaire2");
   }

   /**
    * Test d'une requête d'insertion avec un $.
    * @throws Spi4jValidationException
    *            entité non valide
    */
   @Test
   public void testExecuteInsertDollar () throws Spi4jValidationException
   {
      final Level v_level = LogManager.getLogger(DaoJdbc_Abs.class).getLevel();
      Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), Level.DEBUG);
      final MyPersonneDao_Itf v_dao = MyParamPersistence.getUserPersistence().getInstanceOfPersonneDao();
      final MyPersonneEntity_Itf v_entity = MyParamPersistence.getUserPersistence().getInstanceOfPersonneEntity();
      v_entity.setNom("sh\\kdfh$sdfkj");
      v_entity.setCivil(true);
      v_dao.create(v_entity);
      Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), v_level);
   }

   /**
    * Test d'une requête d'insertion avec utilisation de séquence.
    * @throws Spi4jValidationException
    *            entité non valide
    */
   @Test
   public void testExecuteInsertAvecSequence () throws Spi4jValidationException
   {
      final Level v_level = LogManager.getLogger(DaoJdbc_Abs.class).getLevel();
      Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), Level.DEBUG);
      try
      {
         final MyPersonneDao_Itf v_dao = MyParamPersistence.getUserPersistence().getInstanceOfPersonneDao();
         final MyPersonneEntity_Itf v_entity = MyParamPersistence.getUserPersistence().getInstanceOfPersonneEntity();
         v_entity.setNom("Dupont");
         v_entity.setCivil(true);
         v_dao.create(v_entity);
      }
      finally
      {
         Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), v_level);
      }
   }

   /**
    * Test d'une requête d'insertion sans utilisation de séquence.
    * @throws Spi4jValidationException
    *            entité non valide
    */
   @Test
   public void testExecuteInsertSansSequence () throws Spi4jValidationException
   {
      final Level v_level = LogManager.getLogger(DaoJdbc_Abs.class).getLevel();
      Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), Level.DEBUG);
      ParamPersistence_Abs.enableInsertWithId(true);
      try
      {
         final MyPersonneDao_Itf v_dao = MyParamPersistence.getUserPersistence().getInstanceOfPersonneDao();
         final MyPersonneEntity_Itf v_entity = MyParamPersistence.getUserPersistence().getInstanceOfPersonneEntity();
         v_entity.setId(-1L);
         v_entity.setNom("Dupont");
         v_entity.setCivil(true);
         v_dao.create(v_entity);
      }
      finally
      {
         ParamPersistence_Abs.enableInsertWithId(false);
         Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), v_level);
      }
   }

   /**
    * Test d'une requête d'insertion sans utilisation de séquence.
    * @throws Spi4jValidationException
    *            entité non valide
    */
   @Test
   public void testExecuteInsertAvecIdErreur () throws Spi4jValidationException
   {
      final Level v_level = LogManager.getLogger(DaoJdbc_Abs.class).getLevel();
      Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), Level.DEBUG);
      try
      {
         final MyPersonneDao_Itf v_dao = MyParamPersistence.getUserPersistence().getInstanceOfPersonneDao();
         final MyPersonneEntity_Itf v_entity = MyParamPersistence.getUserPersistence().getInstanceOfPersonneEntity();
         v_entity.setId(-1L);
         v_entity.setNom("Dupont");
         v_entity.setCivil(true);
         v_dao.create(v_entity);
         fail("Erreur attendue");
      }
      catch (final Spi4jRuntimeException v_e)
      {
         assertNotNull(v_e, "Erreur attendue");
         assertEquals(v_e.getMessage(), "L'entité insérée possède déjà un identifiant.", "Erreur attendue");
      }
      finally
      {
         Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), v_level);
      }
   }

   /**
    * Test du nom de table dans DAO.
    */
   @Test
   public void testTableName ()
   {
      final MyGradeDao_Itf v_dao = MyParamPersistence.getUserPersistence().getInstanceOfGradeDao();

      assertEquals(MyGradeColumns_Enum.c_tableName, v_dao.getTableName(), "Test du nom de table");
   }

   /**
    * Lancement d'une requête avec erreur de select car colonnes incorrectes avec la base.
    * @throws Spi4jValidationException
    *            entité non valide
    */
   @Test
   public void testWrongExecuteQuery () throws Spi4jValidationException
   {
      final MyWrongGradeDao_Itf v_wrongDao = MyParamPersistence.getUserPersistence().getInstanceOfWrongGradeDao();
      final MyGradeDao_Itf v_dao = MyParamPersistence.getUserPersistence().getInstanceOfGradeDao();

      final MyGradeEntity_Itf v_entity = new MyGradeEntity(null, "Colonel", "COL", new Date(), false);
      v_dao.create(v_entity);
      try
      {
         v_wrongDao.findById(v_entity.getId());
         fail("Erreur attendue");
      }
      catch (final Spi4jRuntimeException v_e)
      {
         // les paramètres doivent être visibles dans le message d'erreur
         assertTrue(v_e.toString().contains(MyWrongGradeColumns_Enum.colonne_inconnue.toString()));
      }
   }

   /**
    * Lancement d'une requête avec erreur de mise à jour car colonnes incorrectes avec la base.
    * @throws Spi4jValidationException
    *            entité non valide
    */
   @Test
   public void testWrongExecuteUpdate () throws Spi4jValidationException
   {
      final MyWrongGradeDao_Itf v_wrongDao = MyParamPersistence.getUserPersistence().getInstanceOfWrongGradeDao();
      final MyGradeDao_Itf v_dao = MyParamPersistence.getUserPersistence().getInstanceOfGradeDao();

      final MyGradeEntity_Itf v_entity = new MyGradeEntity(null, "Colonel", "COL", new Date(), false);
      v_dao.create(v_entity);
      v_entity.set_libelle("Mon Colonel");
      try
      {
         v_wrongDao.update(v_entity);
         fail("Erreur attendue");
      }
      catch (final Spi4jRuntimeException v_e)
      {
         // les paramètres doivent être visibles dans le message d'erreur
         assertTrue(v_e.toString().contains("Mon Colonel"));
      }
   }

   /**
    * Méthode de fin de test : rollback.
    */
   @AfterEach
   public void tearDown ()
   {
      userPersistence.rollback();
   }

   /**
    * Finalisation du test.
    */
   @AfterAll
   public static void tearDownAfterClass ()
   {
      Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), initalLogLevel);
   }

}
