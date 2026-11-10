/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.dao.Operator_Enum;
import fr.spi4j.persistence.dao.TableCriteria;
import fr.spi4j.persistence.dao.jdbc.DaoJdbc_Abs;
import fr.spi4j.persistence.entity.Spi4jLockException;
import fr.spi4j.testapp.MyParamPersistence;
import fr.spi4j.testapp.MyPersonneColumns_Enum;
import fr.spi4j.testapp.MyPersonneDao_Itf;
import fr.spi4j.testapp.MyPersonneEntity_Itf;
import fr.spi4j.testapp.MyUserPersistence;

/**
 * Classe de test du dao 'MyPersonneDao_Itf'.
 * @author MINARM
 */
public class MyPersonneDao_Test
{

   /** Le 'UserPersistence' de l'application. */
   private static MyUserPersistence userPersistence;

   /** Le 'MyPersonneDao_Itf' testé. */
   private static MyPersonneDao_Itf dao;

   /** L'id du 'MyPersonneEntity_Itf' stocké en base. */
   private static Long crudId;

   /**
    * Définition du crudId.
    * @param p_crudId
    *           le crudId
    */
   public static void setCrudId (final Long p_crudId)
   {
      crudId = p_crudId;
   }

   /**
    * Méthode d'initialisation de la classe de tests.
    */
   @BeforeAll
   public static void setUpBeforeClass ()
   {
      userPersistence = MyParamPersistence.getUserPersistence();
      dao = userPersistence.getInstanceOfPersonneDao();
      DatabaseInitialization.initDatabase();
   }

   /**
    * Méthode d'initialisation de tests.
    */
   @BeforeEach
   public void setUp ()
   {
      userPersistence.begin();
   }

   /**
    * Test de création de l'entity.
    * @throws Spi4jValidationException
    *            si erreur de création de l'entity.
    */
   @Test
   public void testCreate () throws Spi4jValidationException
   {
      final MyPersonneEntity_Itf v_entity = userPersistence.getInstanceOfPersonneEntity();

      v_entity.setNom("NOM1");
      v_entity.setPrenom("PRENOM1");
      v_entity.setCivil(false);

      dao.create(v_entity);

      setCrudId(v_entity.getId());

      assertNotNull(v_entity.getId(), "L'entity créée devrait avoir une clé primaire renseignée");

      assertEquals("NOM1", v_entity.getNom(), "Le nom de la personne est incorrect");
      assertEquals("PRENOM1", v_entity.getPrenom(), "Le prénom de la personne est incorrect");
   }

   /**
    * Test de recherche par identifiant de l'entity.
    * @throws Spi4jValidationException
    *            si erreur de création de l'entity.
    */
   @Test
   public void testFindById () throws Spi4jValidationException
   {
      testCreate();

      final MyPersonneEntity_Itf v_entity = dao.findById(crudId);
      assertNotNull(v_entity, "L'entity devrait exister dans le référentiel");
      assertNotNull(v_entity.getId(), "L'entity créée devrait avoir une clé primaire renseignée");
      assertNotNull(v_entity.toString(), "L'entity créée devrait avoir un toString");

      assertEquals("NOM1", v_entity.getNom(), "Le nom de la personne est incorrect");
      assertEquals("PRENOM1", v_entity.getPrenom(), "Le prénom de la personne est incorrect");
   }

   /**
    * Test de recherche par colonne.
    * @throws Spi4jValidationException
    *            si erreur de création de l'entity.
    */
   @Test
   public void testFindByColumn () throws Spi4jValidationException
   {
      testCreate();

      final List<MyPersonneEntity_Itf> v_entitys = dao.findByColumn(MyPersonneColumns_Enum.Personne_id, crudId);
      assertEquals(1, v_entitys.size(), "Il ne devrait exister qu'une entity");
      final MyPersonneEntity_Itf v_entity = v_entitys.get(0);
      assertNotNull(v_entity.getId(), "L'entity créée devrait avoir une clé primaire renseignée");

      assertEquals("NOM1", v_entity.getNom(), "Le nom de la personne est incorrect");
      assertEquals("PRENOM1", v_entity.getPrenom(), "Le prénom de la personne est incorrect");

      final List<MyPersonneEntity_Itf> v_entitys2 = dao.findByColumn(MyPersonneColumns_Enum.Personne_id,
               Collections.singletonList(crudId));
      assertEquals(v_entitys.size(), v_entitys2.size(), "Il ne devrait exister qu'une entity");
      assertEquals(v_entitys.get(0).getId(), v_entitys2.get(0).getId(), "Entity identique");

      final List<MyPersonneEntity_Itf> v_entitys3 = dao.findByColumn(MyPersonneColumns_Enum.Personne_id, null);
      assertEquals(0, v_entitys3.size(), "Il ne devrait exister aucune entity d'id null");
   }

   /**
    * Test de recherche par critère.
    * @throws Spi4jValidationException
    *            si erreur de création de l'entity.
    */
   @Test
   public void testFindByCriteria () throws Spi4jValidationException
   {
      testCreate();

      final TableCriteria<MyPersonneColumns_Enum> v_table = new TableCriteria<>(
               "Test 'find by criteria' en cherchant sur l'id");
      v_table.addCriteria(MyPersonneColumns_Enum.Personne_id, Operator_Enum.equals, crudId);
      v_table.addOrderByDesc(MyPersonneColumns_Enum.Personne_id);
      final List<MyPersonneEntity_Itf> v_entitys = dao.findByCriteria(v_table);
      assertEquals(1, v_entitys.size(), "Il ne devrait exister qu'une entity");
      final MyPersonneEntity_Itf v_entity = v_entitys.get(0);
      assertNotNull(v_entity.getId(), "L'entity créée devrait avoir une clé primaire renseignée");

      assertEquals("NOM1", v_entity.getNom(), "Le nom de la personne est incorrect");
      assertEquals("PRENOM1", v_entity.getPrenom(), "Le prénom de la personne est incorrect");

      // TableCriteria avec un Identifiable_Itf
      // description vide pour le TableCriteria pour tester l'absence de log
      final TableCriteria<MyPersonneColumns_Enum> v_table2 = new TableCriteria<>("");
      v_table2.addCriteria(MyPersonneColumns_Enum.Personne_id, Operator_Enum.equals, v_entity);
      final List<MyPersonneEntity_Itf> v_entitys2 = dao.findByCriteria(v_table2);
      assertEquals(1, v_entitys2.size(), "Il ne devrait exister qu'une entity");

      // TableCriteria avec une collection d'id
      final TableCriteria<MyPersonneColumns_Enum> v_table3 = new TableCriteria<>(
               "Test 'find by criteria' en cherchant sur une collection d'id");
      v_table3.addCriteria(MyPersonneColumns_Enum.Personne_id, Operator_Enum.in,
               Collections.singletonList(v_entity.getId()));
      final List<MyPersonneEntity_Itf> v_entitys3 = dao.findByCriteria(v_table3);
      assertEquals(1, v_entitys3.size(), "Il ne devrait exister qu'une entity");

      // TableCriteria avec une collection Identifiable_Itf
      final TableCriteria<MyPersonneColumns_Enum> v_table4 = new TableCriteria<>(
               "Test 'find by criteria' en cherchant sur une collection d'identifiable");
      v_table4.addCriteria(MyPersonneColumns_Enum.Personne_id, Operator_Enum.in, Collections.singletonList(v_entity));
      final List<MyPersonneEntity_Itf> v_entitys4 = dao.findByCriteria(v_table4);
      assertEquals(1, v_entitys4.size(), "Il ne devrait exister qu'une entity");

      final Level v_initalLogLevel = LogManager.getLogger(DaoJdbc_Abs.class).getLevel();
      // activer les logs pour JdbcStatementPreparator
      Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), Level.DEBUG);
      try
      {
         dao.findByCriteria(v_table3);
         dao.findByCriteria(v_table4);
      }
      finally
      {
         Configurator.setLevel(LogManager.getLogger(DaoJdbc_Abs.class).getName(), v_initalLogLevel);
      }
   }

   /**
    * Test de mise à jour de l'entity.
    * @throws Spi4jValidationException
    *            si erreur de mise à jour de l'entity.
    */
   @Test
   public void testUpdate () throws Spi4jValidationException
   {
      testCreate();

      final MyPersonneEntity_Itf v_entity = dao.findById(crudId);

      v_entity.setPrenom("PRENOM2");

      dao.update(v_entity);

      assertNotNull(v_entity.getId(), "L'entity mise à jour devrait avoir une clé primaire renseignée");

      assertEquals("NOM1", v_entity.getNom(), "Le nom de la personne est incorrect");
      assertEquals("PRENOM2", v_entity.getPrenom(), "Le prénom de la personne est incorrect");
   }

   /**
    * Test de recherche de toutes les entitys.
    * @throws Spi4jValidationException
    *            si erreur de création de l'entity.
    */
   @Test
   public void testFindAll () throws Spi4jValidationException
   {
      testCreate();

      final List<MyPersonneEntity_Itf> v_all = dao.findAll();
      boolean v_existe = false;
      for (final MyPersonneEntity_Itf v_entity : v_all)
      {
         if (v_entity.getId().equals(crudId))
         {
            v_existe = true;
         }
      }
      assertTrue(v_existe, "L'entity n'a pas été trouvée dans la liste de toutes les entitys");

      // findAll avec order by sur une colonne
      final List<MyPersonneEntity_Itf> v_all2 = dao.findAll(MyPersonneColumns_Enum.nom);
      boolean v_existe2 = false;
      for (final MyPersonneEntity_Itf v_entity : v_all2)
      {
         if (v_entity.getId().equals(crudId))
         {
            v_existe2 = true;
         }
      }
      assertTrue(v_existe2, "L'entity n'a pas été trouvée dans la liste de toutes les entitys");
   }

   /**
    * Test de suppression de l'entity.
    * @throws Spi4jValidationException
    *            si erreur de création de l'entity.
    */
   @Test
   public void testDelete () throws Spi4jValidationException
   {
      testCreate();

      final MyPersonneEntity_Itf v_entity = dao.findById(crudId);

      dao.delete(v_entity);
      final List<MyPersonneEntity_Itf> v_all = dao.findAll();
      boolean v_existe = false;
      for (final MyPersonneEntity_Itf v_oneEntity : v_all)
      {
         if (v_oneEntity.getId().equals(crudId))
         {
            v_existe = true;
         }
      }
      assertFalse(v_existe, "L'entity ne devrait plus exister dans la liste de toutes les entitys");
   }

   /**
    * Test de suppression par critère.
    * @throws Spi4jValidationException
    *            si erreur de création de l'entity.
    */
   @Test
   public void testDeleteByCriteria () throws Spi4jValidationException
   {
      testCreate();

      // description vide pour le TableCriteria pour tester l'absence de log
      final TableCriteria<MyPersonneColumns_Enum> v_table = new TableCriteria<>("");
      v_table.addCriteria(MyPersonneColumns_Enum.Personne_id, Operator_Enum.equals, crudId);
      final int v_nbRowsDeleted = dao.deleteByCriteria(v_table);
      assertEquals(1, v_nbRowsDeleted, "Il ne devait y avoir qu'une entity à supprimer");
   }

   /**
    * Méthode de test du Lock.
    * @throws Spi4jValidationException
    *            e
    */
   @Test
   public void testLockSurUpdate () throws Spi4jValidationException
   {
      testCreate();
      final MyPersonneEntity_Itf v_personne1 = dao.findById(crudId);
      final MyPersonneEntity_Itf v_personne2 = dao.findById(crudId);

      try
      {
         // attente d'une seconde entre la création et la modification de l'objet de test
         Thread.sleep(1000);
      }
      catch (final InterruptedException v_e)
      {
         // RAS
      }

      v_personne1.setNom("Personne1");
      dao.update(v_personne1);

      assertEquals("Personne1", dao.findById(crudId).getNom(),
               "La personne n'a pas pu être enregistrée comme attendue");
      assertEquals(v_personne1.getVersion().getTime(), dao.findById(crudId).getVersion().getTime(),
               "Version a été mis à jour après update");
      assertNotSame(v_personne2.getVersion().getTime(), dao.findById(crudId).getVersion().getTime(),
               "Version a été mis à jour après update");

      v_personne2.setNom("Personne2");
      try
      {
         dao.update(v_personne2);
         fail("Erreur de lock attendue");
      }
      catch (final Spi4jLockException v_e)
      {
         assertNotNull(v_e, "Erreur de lock attendue");
      }
   }

   /**
    * Méthode de test du Lock.
    * @throws Spi4jValidationException
    *            e
    */
   @Test
   public void testLockSurDelete () throws Spi4jValidationException
   {
      testCreate();
      final MyPersonneEntity_Itf v_personne1 = dao.findById(crudId);
      final MyPersonneEntity_Itf v_personne2 = dao.findById(crudId);

      try
      {
         // attente d'une seconde entre la création et la modification de l'objet de test
         Thread.sleep(1000);
      }
      catch (final InterruptedException v_e)
      {
         // RAS
      }

      v_personne1.setNom("Personne1");
      dao.update(v_personne1);

      assertEquals("Personne1", dao.findById(crudId).getNom(),
               "La personne n'a pas pu être enregistrée comme attendue");
      assertEquals(v_personne1.getVersion().getTime(), dao.findById(crudId).getVersion().getTime(),
               "Version a été mis à jour après update");
      assertNotSame(v_personne2.getVersion().getTime(), dao.findById(crudId).getVersion().getTime(),
               "Version a été mis à jour après update");

      try
      {
         dao.delete(v_personne2);
         fail("Erreur de lock attendue");
      }
      catch (final Spi4jLockException v_e)
      {
         assertNotNull(v_e, "Erreur de lock attendue");
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
}
