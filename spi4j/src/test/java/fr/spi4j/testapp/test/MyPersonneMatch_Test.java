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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.persistence.dao.Operator_Enum;
import fr.spi4j.persistence.dao.TableCriteria;
import fr.spi4j.testapp.MyParamPersistence;
import fr.spi4j.testapp.MyPersonneColumns_Enum;
import fr.spi4j.testapp.MyPersonneDto;
import fr.spi4j.testapp.MyPersonneMatch_Itf;
import fr.spi4j.testapp.MyUserMatching;
import fr.spi4j.testapp.MyUserPersistence;

/**
 * Classe de tests sur Match_Abs.
 * @author MINARM
 */
public class MyPersonneMatch_Test
{

   /** Le 'UserPersistence' de l'application. */
   private static MyUserPersistence userPersistence;

   private static MyPersonneMatch_Itf match;

   /**
    * Méthode d'initialisation des tests unitaires.
    */
   @BeforeAll
   public static void setUpbeforeClass ()
   {
      userPersistence = MyParamPersistence.getUserPersistence();
      match = MyUserMatching.getInstanceOfPersonneMatch();
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

      match.create(v_dto);

      assertNotNull(v_dto.getId(), "Le dto créé devrait avoir une clé primaire renseignée");

      assertEquals("NOM1", v_dto.getNom(), "Le nom de la personne est incorrect");
      assertEquals("PRENOM1", v_dto.getPrenom(), "Le prénom de la personne est incorrect");
   }

   /**
    * Test de suppression de toutes les entitys.
    * @throws Throwable
    *            exception
    */
   @Test
   public void testDeleteAll () throws Throwable
   {
      testCreate();
      testCreate();

      assertTrue(match.findAll().size() >= 2, "Il devrait y avoir deux objets créés.");

      match.deleteAll();

      assertTrue(match.findAll().isEmpty(), "Il ne devrait plus y avoir d'objets.");
   }

   /**
    * Test de la méthode 'findAll'.
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindAll () throws Throwable
   {
      testCreate();
      testCreate();

      final List<MyPersonneDto> v_listDto = match.findAll(MyPersonneColumns_Enum.prenom);

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
    * Test 1 des méthodes 'findByCriteria(TableCriteria<Columns_Enum> p_tableCriteria)'.
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindByCriteriaWithTableCriteria1 () throws Throwable
   {
      testCreate();
      testCreate();

      // On récupère les éléments qui ont comme attribut prenom "PRENOM1".
      // En principe, il y en a au moins deux.
      final TableCriteria<MyPersonneColumns_Enum> v_tableCriteria = new TableCriteria<>("Test 1");
      v_tableCriteria.addCriteria(MyPersonneColumns_Enum.prenom, Operator_Enum.equals, "PRENOM1");

      final List<MyPersonneDto> v_list = match.findByCriteria(v_tableCriteria);

      assertNotNull(v_list, "La liste devrait contenir au moins deux éléments.");
      assertTrue(v_list.size() >= 2, "La liste devrait contenir au moins deux éléments.");
   }

   /**
    * Test 2 des méthodes 'findByCriteria(TableCriteria<Columns_Enum> p_tableCriteria)'.
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindByCriteriaWithTableCriteria2 () throws Throwable
   {
      testCreate();
      testCreate();

      // On récupère les éléments qui ont comme attribut prenom "PRENOM".
      // En principe, il n'y en a aucun.
      final TableCriteria<MyPersonneColumns_Enum> v_tableCriteria = new TableCriteria<>("Test 2");
      v_tableCriteria.addCriteria(MyPersonneColumns_Enum.prenom, Operator_Enum.equals, "PRENOM");

      final List<MyPersonneDto> v_list = match.findByCriteria(v_tableCriteria);

      assertTrue(v_list.isEmpty(), "La liste devrait être vide.");
   }

   /**
    * Test 1 de la méthode 'findByCriteria' (sans pagination ni table criteria)
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindByCriteria1 () throws Throwable
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
      final List<MyPersonneDto> v_list = match.findByCriteria(v_query, v_map);

      assertNotNull(v_list, "La liste ne devrait pas être vide.");
      assertTrue(v_list.size() >= 4, "La liste devrait contenir au moins quatre éléments.");
   }

   /**
    * Test 2 de la méthode 'findByCriteria' (sans pagination ni table criteria)
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindByCriteria2 () throws Throwable
   {
      testCreate();
      testCreate();
      testCreate();
      testCreate();

      // On récupère les éléments qui ont comme attribut prenom "PRENOM1".
      // En principe, il n'y en a aucun.
      final String v_query = "where prenom = :prenom";
      final Map<String, String> v_map = Collections.singletonMap("prenom", "PRENOM");

      final List<MyPersonneDto> v_list = match.findByCriteria(v_query, v_map);

      assertNotNull(v_list, "La liste ne devrait pas être vide.");
      assertTrue(v_list.isEmpty(), "La liste devrait être vide.");
   }

   /**
    * Test 1 de la méthode 'findByCriteria' (avec pagination)
    * @throws Throwable
    *            Exception
    */
   @Test
   public void testFindByCriteriaWithPagination1 () throws Throwable
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
      final List<MyPersonneDto> v_list = match.findByCriteria(v_query, v_map, 2, 1);

      assertNotNull(v_list, "La liste ne devrait pas être vide.");
      assertTrue(v_list.size() == 2, "La liste devrait contenir deux éléments.");
   }

   /**
    * Test de la méthode 'findByColumn'.
    * @throws Throwable
    *            exception
    */
   @Test
   public void testFindByColumn () throws Throwable
   {
      testCreate();
      testCreate();

      final List<MyPersonneDto> v_list = match.findByColumn(MyPersonneColumns_Enum.prenom, "PRENOM1");

      assertNotNull(v_list, "La liste ne devrait pas être vide.");
      assertTrue(v_list.size() >= 2, "La liste devrait contenir au moins deux éléments.");
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
