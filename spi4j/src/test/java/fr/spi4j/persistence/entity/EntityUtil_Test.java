/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.exception.Spi4jValidationException;

/**
 * Classe de test de EntityUtil.
 * @author MINARM
 */
public class EntityUtil_Test
{
   /**
    * Entity de test.
    * @author MINARM
    */
   public static class TestEntity implements Entity_Itf<Long>
   {
      private static final long serialVersionUID = 1L;

      private final Long _id;

      private String _nom;

      /**
       * Constructeur.
       */
      public TestEntity ()
      {
         super();
         _id = -1L;
      }

      /**
       * Constructeur.
       * @param p_id
       *           Long
       */
      public TestEntity (final Long p_id)
      {
         super();
         _id = p_id;
         _nom = "Dupond"; // nom par défaut pour test
      }

      @Override
      public Long getId ()
      {
         return _id;
      }

      /**
       * @return Nom
       */
      public String getNom ()
      {
         return _nom;
      }

      /**
       * @return firstChild
       */
      public TestEntity getFirstChild ()
      {
         return new TestEntity(getFirstChildId());
      }

      /**
       * @return fistChildId
       */
      public Long getFirstChildId ()
      {
         return -1L;
      }

      /**
       * @param p_nom
       *           String
       */
      public void setNom (final String p_nom)
      {
         _nom = p_nom;
      }

      @Override
      public void setId (final Long p_id)
      {
         throw new IllegalStateException();
      }

      @Override
      public void validate () throws Spi4jValidationException
      {
         throw new IllegalStateException();
      }
   }

   /**
    * Attributs de TestEntity.
    * @author MINARM
    */
   public static enum TestColumn_Enum implements ColumnsNames_Itf
   {
      /** id. */
      id("id", "id", Long.class, true, -1, true),
      /** nom. */
      nom("nom", "nom", String.class, false, -1, false),
      /** first child. */
      firstChild("firstChild", "first child", TestEntity.class, false, -1, false);

      /**
       * Le nom physique de la table.
       */
      public static final String c_tableName = "PERSONNE";

      /** Le nom logique de la colonne. */
      private final String _logicalColumnName;

      /** Le nom physique de la colonne. */
      private final String _physicalColumnName;

      /** Le type associé à la colonne. */
      private final Class<?> _typeColumn;

      /** Est-ce que la saisie de la valeur est obligatoire pour cette colonne ? */
      private final boolean _mandatory;

      /** La taille de la colonne. */
      private final int _size;

      /** Est-ce que la colonne est la clé primaire? */
      private final boolean _id;

      /**
       * Constructeur permettant de spécifier le type de la colonne.
       * @param p_logicalColumnName
       *           (In)(*) Le nom logique de la colonne.
       * @param p_physicalColumnName
       *           (In)(*) Le nom physique de la colonne.
       * @param p_ClassType
       *           (In)(*) Le type de la colonne.
       * @param p_mandatory
       *           (In)(*) Est-ce que la saisie de la valeur est obligatoire pour cette colonne?
       * @param p_size
       *           (In)(*) La taille de la colonne
       * @param p_id
       *           (In)(*) Est-ce que la colonne est la clé primaire?
       */
      private TestColumn_Enum (final String p_logicalColumnName, final String p_physicalColumnName,
               final Class<?> p_ClassType, final boolean p_mandatory, final int p_size, final boolean p_id)
      {
         _logicalColumnName = p_logicalColumnName;
         _physicalColumnName = p_physicalColumnName;
         _typeColumn = p_ClassType;
         _mandatory = p_mandatory;
         _size = p_size;
         _id = p_id;
      }

      @Override
      public String getLogicalColumnName ()
      {
         return _logicalColumnName;
      }

      @Override
      public String getPhysicalColumnName ()
      {
         return _physicalColumnName;
      }

      @Override
      public boolean isMandatory ()
      {
         return _mandatory;
      }

      @Override
      public int getSize ()
      {
         return _size;
      }

      @Override
      public boolean isId ()
      {
         return _id;
      }

      @Override
      public Class<?> getTypeColumn ()
      {
         return _typeColumn;
      }

      @Override
      public String toString ()
      {
         return _physicalColumnName;
      }

      @Override
      public String getTableName ()
      {
         return c_tableName;
      }

      @Override
      public String getCompletePhysicalName ()
      {
         return getTableName() + '.' + getPhysicalColumnName();
      }
   }

   /**
    * Test findInCollectionById.
    */
   @Test
   public void testFindInCollectionById ()
   {
      final List<TestEntity> v_tests = Arrays.asList(new TestEntity(2L), new TestEntity(1L));
      assertNotNull(EntityUtil.findInCollectionById(v_tests, 1L));
      assertNotNull(EntityUtil.findInCollectionById(v_tests, new TestEntity(1L)));

      final List<TestEntity> v_tests2 = Arrays.asList(null, new TestEntity(2L), new TestEntity(1L));
      assertNotNull(EntityUtil.findInCollectionById(v_tests2, 1L));
      assertNotNull(EntityUtil.findInCollectionById(v_tests2, new TestEntity(1L)));
   }

   /**
    * Test findInCollectionById Scenario Alternatif 1 : non trouvé.
    */
   @Test
   public void testFindInCollectionById_SA1 ()
   {
      final List<TestEntity> v_tests = Arrays.asList(new TestEntity(1L), new TestEntity(2L));
      assertNull(EntityUtil.findInCollectionById(v_tests, 3L)); // retourne null car id 3L non trouvé
   }

   /**
    * Test findInCollectionById Scenario Exception 1 : id null.
    */
   @Test
   public void testFindInCollectionById_SE1 ()
   {
      assertThrows(IllegalArgumentException.class, () -> {
         final List<TestEntity> v_tests = Arrays.asList(new TestEntity(1L), new TestEntity(2L));
         EntityUtil.findInCollectionById(v_tests, new TestEntity(null)); // exception lancée ici
      });
   }

   /**
    * Test findInCollectionByListIds.
    */
   @Test
   public void testFindInCollectionByListIds ()
   {
      final List<TestEntity> v_tests = Arrays.asList(new TestEntity(1L), new TestEntity(2L));
      assertTrue(EntityUtil.findInCollectionByListIds(v_tests, Arrays.asList(1L)).size() == 1);
      assertTrue(EntityUtil.findInCollectionByListIds(v_tests, Arrays.asList(1L, 2L)).size() == 2);
      assertTrue(EntityUtil.findInCollectionByListIds(v_tests, new ArrayList<Long>()).size() == 0);
   }

   /**
    * Test findInCollectionByListIds Scenario Exception 1 : un id ne correspond pas à un Entity dans la liste.
    */
   @Test
   public void testFindInCollectionByListIds_SE1 ()
   {
      assertThrows(Spi4jRuntimeException.class, () -> {
         final List<TestEntity> v_tests = Arrays.asList(new TestEntity(1L), new TestEntity(2L));
         EntityUtil.findInCollectionByListIds(v_tests, Arrays.asList(-1L));
      });
   }

   /**
    * Test findInCollectionByAttribute.
    */
   @Test
   public void testFindInCollectionByAttribute ()
   {
      final List<TestEntity> v_tests = Arrays.asList(new TestEntity(1L), new TestEntity(2L));
      v_tests.get(0).setNom(null);
      assertTrue(EntityUtil.findInCollectionByAttribute(v_tests, TestColumn_Enum.id, 1L).size() == 1);
      assertTrue(EntityUtil.findInCollectionByAttribute(v_tests, TestColumn_Enum.nom, null).size() == 1);
      assertTrue(EntityUtil.findInCollectionByAttribute(v_tests, TestColumn_Enum.nom, "Durand").isEmpty());

      final List<TestEntity> v_tests2 = Arrays.asList(null, new TestEntity(1L), new TestEntity(2L));
      assertTrue(EntityUtil.findInCollectionByAttribute(v_tests2, TestColumn_Enum.id, 1L).size() == 1);
   }

   /**
    * Test findOneInCollectionByAttribute.
    */
   @Test
   public void testFindOneInCollectionByAttribute ()
   {
      final Entity_Itf<?> v_TestEntity1 = new TestEntity(1L);
      final Entity_Itf<?> v_TestEntity2 = new TestEntity(2L);

      final List<Entity_Itf<?>> v_tests = Arrays.asList(v_TestEntity1, v_TestEntity2);
      for (final Entity_Itf<?> v_entity : v_tests)
      {
         ((TestEntity) v_entity).setNom(null);
      }
      assertNotNull(EntityUtil.findOneInCollectionByAttribute(v_tests, TestColumn_Enum.id, 1L));
      try
      {
         EntityUtil.findOneInCollectionByAttribute(v_tests, TestColumn_Enum.nom, null);
         // pas ok
         fail();
      }
      catch (final Exception v_e)
      {
         // ok (plusieurs trouvés)
         assertNotNull(v_e);
      }

      try
      {
         EntityUtil.findOneInCollectionByAttribute(v_tests, TestColumn_Enum.nom, "Durand");
         // pas ok
         fail();
      }
      catch (final Exception v_e)
      {
         // ok (aucun trouvé)
         assertNotNull(v_e);
      }
   }

   /**
    * Test createMapById
    */
   @Test
   public void testCreateListIds ()
   {
      final List<TestEntity> v_tests = Arrays.asList(new TestEntity(1L), new TestEntity(2L));
      final List<Long> v_list = EntityUtil.createListIds(v_tests);
      assertEquals(2, v_list.size());
      assertEquals(1L, v_list.get(0).longValue());
      assertEquals(2L, v_list.get(1).longValue());
   }

   /**
    * Test createMapById
    */
   @Test
   public void testCreateMapById ()
   {
      final List<TestEntity> v_tests = Arrays.asList(new TestEntity(1L), new TestEntity(2L));
      final Map<Long, TestEntity> v_map = EntityUtil.createMapById(v_tests);
      assertNotNull(v_map.get(1L));
      assertNotNull(v_map.get(2L));
      assertEquals(2, v_map.size());
   }

   /**
    * Test createSortedMapById
    */
   @Test
   public void testCreateSortedMapById ()
   {
      final List<TestEntity> v_tests = Arrays.asList(new TestEntity(1L), new TestEntity(2L));
      final Map<Long, TestEntity> v_map = EntityUtil.createSortedMapById(v_tests);
      assertNotNull(v_map.get(1L));
      assertNotNull(v_map.get(2L));
      assertEquals(2, v_map.size());

      // on vérifie l'ordre
      assertEquals(1L, v_map.values().iterator().next().getId().longValue());
      assertEquals(1L, v_map.keySet().iterator().next().longValue());
   }

   /**
    * Test sortListByAttribute
    */
   @Test
   public void testSortListByAttribute ()
   {
      final List<TestEntity> v_tests = Arrays.asList(new TestEntity(1L), new TestEntity(2L));

      Collections.reverse(v_tests);
      EntityUtil.sortListByAttribute(v_tests, TestColumn_Enum.id);
      assertEquals(1L, v_tests.get(0).getId().longValue());

      EntityUtil.sortListByAttribute(v_tests, TestColumn_Enum.nom);

      EntityUtil.sortListByAttribute(v_tests, TestColumn_Enum.id, false, true);
      assertEquals(2L, v_tests.get(0).getId().longValue());

      EntityUtil.sortListByAttribute(v_tests, TestColumn_Enum.nom, true, false);
   }

   /**
    * Test sortListByAttribute : Scenario Alternatif 1 (comparaison de String)
    */
   @Test
   public void testSortListByAttribute_SA1 ()
   {
      final TestEntity v_TestEntity1 = new TestEntity(1L);
      final TestEntity v_TestEntity2 = new TestEntity(2L);
      v_TestEntity2.setNom("Toto");
      final List<TestEntity> v_tests = Arrays.asList(v_TestEntity1, v_TestEntity2);
      Collections.reverse(v_tests);
      EntityUtil.sortListByAttribute(v_tests, TestColumn_Enum.nom, false, true);
      assertEquals("Toto", v_tests.get(0).getNom());
      EntityUtil.sortListByAttribute(v_tests, TestColumn_Enum.nom, true, false);
      assertEquals("Toto", v_tests.get(1).getNom());
   }

   /**
    * Test sortListByAttribute : Scenario Alternatif 2 (comparaison avec 1 null)
    */
   @Test
   public void testSortListByAttribute_SA2 ()
   {
      final TestEntity v_TestEntity1 = new TestEntity(1L);
      final TestEntity v_TestEntity2 = new TestEntity(2L);
      v_TestEntity1.setNom(null);
      final List<TestEntity> v_tests = Arrays.asList(v_TestEntity1, v_TestEntity2);
      assertNull(v_TestEntity1.getNom());
      Collections.reverse(v_tests);
      EntityUtil.sortListByAttribute(v_tests, TestColumn_Enum.nom);
      assertEquals(1L, v_tests.get(0).getId().longValue());
      assertNull(v_tests.get(0).getNom());
   }

   /**
    * Test sortListByAttribute : Scenario Alternatif 3 (comparaison avec 1 null)
    */
   @Test
   public void testSortListByAttribute_SA3 ()
   {
      final TestEntity v_TestEntity1 = new TestEntity(1L);
      final TestEntity v_TestEntity2 = new TestEntity(2L);
      v_TestEntity2.setNom(null);
      final List<TestEntity> v_tests = Arrays.asList(v_TestEntity1, v_TestEntity2);
      assertNull(v_TestEntity2.getNom());
      Collections.reverse(v_tests);
      EntityUtil.sortListByAttribute(v_tests, TestColumn_Enum.nom);
      assertEquals(2L, v_tests.get(0).getId().longValue());
      assertNull(v_tests.get(0).getNom());
   }

   /**
    * Test sortListByAttribute : Scenario Alternatif 3 (comparaison avec 1 null)
    */
   @Test
   public void testSortListByAttribute_SA4 ()
   {
      final TestEntity v_TestEntity1 = new TestEntity(1L);
      final TestEntity v_TestEntity2 = new TestEntity(2L);
      final List<TestEntity> v_tests = Arrays.asList(v_TestEntity1, v_TestEntity2);
      EntityUtil.sortListByAttribute(v_tests, TestColumn_Enum.firstChild, true, true);
      EntityUtil.sortListByAttribute(v_tests, TestColumn_Enum.firstChild, false, false);
   }

   /**
    * Test getAttributeValue.
    */
   @Test
   public void testGetAttributeValue ()
   {
      final Entity_Itf<?> v_TestEntity = new TestEntity(1L);
      assertEquals(1L, EntityUtil.getAttributeValue(v_TestEntity, TestColumn_Enum.id));
   }

   /**
    * Test setAttributeValue.
    */
   @Test
   public void testSetAttributeValue ()
   {
      final Entity_Itf<?> v_TestEntity = new TestEntity(1L);
      EntityUtil.setAttributeValue(v_TestEntity, TestColumn_Enum.nom, "test");
   }

   /**
    * Test deepClone.
    */
   @Test
   public void deepClone ()
   {
      final Entity_Itf<?> v_TestEntity = new TestEntity(1L);
      assertEquals(1L, EntityUtil.deepClone(v_TestEntity).getId());
   }

   /**
    * Test clone.
    */
   @Test
   public void testClone ()
   {
      final Entity_Itf<?> v_TestEntity = new TestEntity(1L);

      assertEquals(1L, EntityUtil.clone(v_TestEntity).getId());

   }

   /**
    * Test deepClone.
    */
   @Test
   public void testSpi4jValidationException ()
   {
      final Spi4jValidationException v_spi4jValidationException = new Spi4jValidationException(new TestEntity(1L),
               "nom");
      assertNotNull(Spi4jValidationException.class.getSimpleName(), v_spi4jValidationException.getMessage());
      final Spi4jValidationException v_spi4jValidationException2 = new Spi4jValidationException(new TestEntity(1L),
               new Exception("dummy cause"), "nom");
      assertNotNull(Spi4jValidationException.class.getSimpleName(), v_spi4jValidationException2.getMessage());
   }
}
