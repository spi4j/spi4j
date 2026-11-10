/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.persistence.dao.Operator_Enum;
import fr.spi4j.persistence.dao.TableCriteria;
import fr.spi4j.requirement.RequirementException;
import fr.spi4j.testapp.MyParamPersistence;
import fr.spi4j.testapp.MyPersonneColumns_Enum;
import fr.spi4j.testapp.MyPersonneDto;
import fr.spi4j.testapp.MyPersonneService_Itf;
import fr.spi4j.testapp.MyRequirement_Enum;
import fr.spi4j.testapp.MyUserBusinessGen;
import fr.spi4j.testapp.MyUserPersistence;

/**
 * Classe de test du service 'MyPersonneService_Itf'.
 */
public class MyPersonneService_Test
{

   /** Le 'UserPersistence' de l'application. */
   private static MyUserPersistence userPersistence;

   /** Le 'MyPersonneService_Itf' testé. */
   private static MyPersonneService_Itf service;

   /** L'id du 'MyPersonneDto' stocké en base. */
   private static Long crudId;

   /**
    * Définition du crudId.
    * @param p_crudId
    *           le crudId
    */
   public static void setCrudId (final Long p_crudId)
   {
      MyPersonneService_Test.crudId = p_crudId;
   }

   /**
    * Méthode d'initialisation de la classe de tests.
    */
   @BeforeAll
   public static void setUpBeforeClass ()
   {
      userPersistence = MyParamPersistence.getUserPersistence();
      service = MyUserBusinessGen.getInstanceOfPersonneService();
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
    * @throws Throwable
    *            exception
    */
   @Test
   public void testCreate () throws Throwable
   {
      final MyPersonneDto v_dto = new MyPersonneDto();

      v_dto.setNom("NOM1");
      v_dto.setPrenom("PRENOM1");
      v_dto.setCivil(true);

      final MyPersonneDto v_createdDto = service.save(v_dto);

      setCrudId(v_createdDto.getId());

      assertNotNull(v_createdDto.getId(), "Le dto créé devrait avoir une clé primaire renseignée");
      assertEquals("NOM1", v_createdDto.getNom(), "Le nom de la personne est incorrect");
      assertEquals("PRENOM1", v_createdDto.getPrenom(), "Le prénom de la personne est incorrect");
   }

   /**
    * Test de recherche par identifiant de l'entity.
    * @throws Throwable
    *            exception
    */
   @Test
   public void testFindById () throws Throwable
   {
      testCreate();

      final MyPersonneDto v_dto = service.findById(crudId);
      assertNotNull(v_dto, "Le dto devrait exister dans le référentiel");
      assertNotNull(v_dto.getId(), "Le dto créé devrait avoir une clé primaire renseignée");
      assertNotNull(v_dto.toString(), "Le dto créé devrait avoir un toString");

      assertEquals("NOM1", v_dto.getNom(), "Le nom de la personne est incorrect");
      assertEquals("PRENOM1", v_dto.getPrenom(), "Le prénom de la personne est incorrect");
   }

   /**
    * Test de mise à jour de l'entity.
    * @throws Throwable
    *            exception
    */
   @Test
   public void testUpdate () throws Throwable
   {
      testCreate();

      final MyPersonneDto v_dto = service.findById(crudId);

      v_dto.setPrenom("PRENOM2");

      final MyPersonneDto v_updatedDto = service.save(v_dto);

      assertNotNull(v_updatedDto.getId(), "Le dto mis à jour devrait avoir une clé primaire renseignée");

      assertEquals("NOM1", v_updatedDto.getNom(), "Le nom de la personne est incorrect");
      assertEquals("PRENOM2", v_updatedDto.getPrenom(), "Le prénom de la personne est incorrect");
   }

   /**
    * Test de recherche de toutes les entitys.
    * @throws Throwable
    *            exception
    */
   @Test
   public void testFindAll () throws Throwable
   {
      testCreate();

      final List<MyPersonneDto> v_all = service.findAll();
      boolean v_existe = false;
      for (final MyPersonneDto v_dto : v_all)
      {
         if (v_dto.getId().equals(crudId))
         {
            v_existe = true;
         }
      }
      assertTrue(v_existe, "Le dto n'a pas été trouvé dans la liste de tous les dto");
   }

   /**
    * Test de suppression de l'entity.
    * @throws Throwable
    *            exception
    */
   @Test
   public void testDelete () throws Throwable
   {
      testCreate();

      final MyPersonneDto v_dto = service.findById(crudId);

      service.delete(v_dto);
      final List<MyPersonneDto> v_all = service.findAll();
      boolean v_existe = false;
      for (final MyPersonneDto v_oneDto : v_all)
      {
         if (v_oneDto.getId().equals(crudId))
         {
            v_existe = true;
         }
      }
      assertFalse(v_existe, "Le dto ne devrait plus exister dans la liste de tous les dto");
   }

   /**
    * Test de suppression de l'entity.
    * @throws Throwable
    *            exception
    */
   @Test
   public void testDeleteByCriteria () throws Throwable
   {
      testCreate();

      final TableCriteria<MyPersonneColumns_Enum> v_tableCriteria = new TableCriteria<>("testDeleteByCriteria");
      v_tableCriteria.addCriteria(MyPersonneColumns_Enum.Personne_id, Operator_Enum.equals, crudId);

      assertEquals(1, service.deleteByCriteria(v_tableCriteria));
   }

   /**
    * Test de la méthode 'findByCriteria' avec comme paramètre un TableCriteria.
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindByCriteriaWithTableCriteria () throws Throwable
   {
      testCreate();
      testCreate();

      final TableCriteria<MyPersonneColumns_Enum> v_tableCriteria = new TableCriteria<>("Test 2");
      v_tableCriteria.addCriteria(MyPersonneColumns_Enum.prenom, Operator_Enum.equals, "PRENOM");

      final List<MyPersonneDto> v_list = service.findByCriteria(v_tableCriteria);

      assertTrue(v_list.isEmpty(), "La liste devrait être vide.");
   }

   /**
    * Test de la méthode 'findByCriteria' (sans pagination ni table criteria)
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindByCriteria () throws Throwable
   {
      testCreate();
      testCreate();
      testCreate();
      testCreate();

      // On récupère les éléments qui ont comme attribut prenom "PRENOM1".
      // En principe, il y en a au moins quatre.
      final String v_query = "where prenom = :prenom";
      final Map<String, String> v_map = Collections.singletonMap("prenom", "PRENOM1");

      // Test de la méthode findByCriteria (String p_queryCriteria, Map<String, ? extends Object> p_map_value_by_name)
      final List<MyPersonneDto> v_list = service.findByCriteria(v_query, v_map);

      assertNotNull(v_list, "La liste ne devrait pas être vide.");
      assertTrue(v_list.size() >= 4, "La liste devrait contenir au moins quatre éléments.");
   }

   /**
    * Test de la méthode 'findByCriteria' (avec pagination)
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindByCriteriaWithPagination () throws Throwable
   {
      testCreate();
      testCreate();
      testCreate();
      testCreate();

      // On récupère les éléments qui ont comme attribut prenom "PRENOM1".
      // En principe, il y en a au moins quatre.
      final String v_query = "where prenom = :prenom";
      final Map<String, String> v_map = Collections.singletonMap("prenom", "PRENOM1");

      // Test de la méthode findByCriteria (String p_queryCriteria,
      // Map<String, ? extends Object> p_map_value_by_name, int p_nbLignesMax, int p_nbLignesStart)
      final List<MyPersonneDto> v_list = service.findByCriteria(v_query, v_map, 2, 1);

      assertNotNull(v_list, "La liste ne devrait pas être vide.");
      assertTrue(v_list.size() == 2, "La liste devrait contenir deux éléments.");
   }

   /**
    * Test de la méthode 'findByColumn'
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindByColumn () throws Throwable
   {
      testCreate();
      testCreate();

      final List<MyPersonneDto> v_list = service.findByColumn(MyPersonneColumns_Enum.prenom, "PRENOM1");

      assertNotNull(v_list, "La liste ne devrait pas être vide.");
      assertTrue(v_list.size() >= 2, "La liste devrait contenir au moins deux éléments.");
   }

   /**
    * Test de la méthode 'findAll' avec pour paramètre une énumération
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindAllWithColumnEnum () throws Throwable
   {
      testCreate();
      testCreate();

      final List<MyPersonneDto> v_listDto = service.findAll(MyPersonneColumns_Enum.prenom);

      assertFalse(v_listDto.isEmpty(), "La liste devrait contenir au moins un élément.");
      assertTrue(v_listDto.size() >= 1, "La liste devrait contenir au moins un élément.");

      int v_prenom1Found = 0;
      for (final MyPersonneDto v_myPersonneDto : v_listDto)
      {
         if (v_myPersonneDto.getPrenom().equals("PRENOM1"))
         {
            v_prenom1Found++;
         }
      }
      assertTrue(v_prenom1Found >= 2, "Il devrait y avoir au moins 2 fois PRENOM1 (créés au début)");
   }

   /**
    * Test d'une exigence fonctionnelle.
    */
   @Test
   public void testExigenceFonctionnelle ()
   {
      try
      {
         service.methodeJetantExceptionExigenceFonctionnelle();
         fail("Erreur attendue");
      }
      catch (final RequirementException v_e)
      {
         assertTrue(v_e.getRequirement() == MyRequirement_Enum.REQ_FCT_PERS_01, "Erreur attendue");
      }
   }

   /**
    * Test d'une exigence technique.
    */
   @Test
   public void testExigenceTechnique ()
   {
      try
      {
         service.methodeJetantExceptionExigenceTechnique();
         fail("Erreur attendue");
      }
      catch (final RequirementException v_e)
      {
         assertTrue(v_e.getRequirement() == MyRequirement_Enum.REQ_TEC_PERS_02, "Erreur attendue");
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
