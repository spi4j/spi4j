/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.exception.Spi4jValidationException;

/**
 * Classe de test de DtoUtil.
 * @author MINARM
 */
public class DtoUtil_Test
{
   /**
    * DTO de test.
    * @author MINARM
    */
   public static class TestDto implements Dto_Itf<Long>
   {
      private static final long serialVersionUID = 1L;

      private final Long _id;

      private String _nom;

      /**
       * Constructeur.
       */
      public TestDto ()
      {
         super();
         _id = -1L;
      }

      /**
       * Constructeur.
       * @param p_id
       *           Long
       */
      public TestDto (final Long p_id)
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
      public TestDto getFirstChild ()
      {
         return new TestDto(getFirstChildId());
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
    * Attributs de TestDTO.
    * @author MINARM
    */
   public static enum TestAttributes_Enum implements AttributesNames_Itf
   {
      /** id. */
      id("id", "id desc", Long.class, true, -1),
      /** nom. */
      nom("nom", "nom desc", String.class, false, -1),
      /** first child. */
      firstChild("firstChild", "first child desc", TestDto.class, false, -1);

      /** Le nom de l'attribut. */
      private final String _name;

      /** La description de l'attribut. */
      private final String _description;

      /** Le type associé à l'attribut. */
      private final Class<?> _type;

      /** Est-ce que la saisie de la valeur est obligatoire pour cet attribut ? */
      private final boolean _mandatory;

      /** La taille de l'attribut. */
      private final int _size;

      /** La méthode du getter. */
      private Method _getterMethod;

      /** La méthode du setter. */
      private Method _setterMethod;

      /**
       * Constructeur.
       * @param p_name
       *           (In)(*) Le nom de l'attribut.
       * @param p_description
       *           (In)(*) La description de l'attribut.
       * @param p_ClassType
       *           (In)(*) Le type de l'attribut.
       * @param p_mandatory
       *           (In)(*) Est-ce que la saisie de la valeur est obligatoire pour cette colonne?
       * @param p_size
       *           (In)(*) La taille de la colonne
       */
      private TestAttributes_Enum (final String p_name, final String p_description, final Class<?> p_ClassType,
               final boolean p_mandatory, final int p_size)
      {
         _name = p_name;
         _description = p_description;
         _type = p_ClassType;
         _mandatory = p_mandatory;
         _size = p_size;
      }

      @Override
      public String getName ()
      {
         return _name;
      }

      @Override
      public String getDescription ()
      {
         return _description;
      }

      @Override
      public Class<?> getType ()
      {
         return _type;
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
      public String toString ()
      {
         return _description;
      }

      @Override
      public Method getGetterMethod ()
      {
         if (_getterMethod == null)
         {
            _getterMethod = DtoAttributeHelper.getInstance().getGetterMethodForAttribute(TestDto.class, getName());
         }
         return _getterMethod;
      }

      @Override
      public Method getSetterMethod ()
      {
         if (_setterMethod == null)
         {
            _setterMethod = DtoAttributeHelper.getInstance().getSetterMethodForAttribute(TestDto.class, getName(),
                     getType());
         }
         return _setterMethod;
      }
   }

   /**
    * Test findInCollectionById.
    */
   @Test
   public void testFindInCollectionById ()
   {
      final List<TestDto> v_tests = Arrays.asList(new TestDto(2L), new TestDto(1L));
      assertNotNull(DtoUtil.findInCollectionById(v_tests, 1L));
      assertNotNull(DtoUtil.findInCollectionById(v_tests, new TestDto(1L)));

      final List<TestDto> v_tests2 = Arrays.asList(null, new TestDto(2L), new TestDto(1L));
      assertNotNull(DtoUtil.findInCollectionById(v_tests2, 1L));
      assertNotNull(DtoUtil.findInCollectionById(v_tests2, new TestDto(1L)));
   }

   /**
    * Test findInCollectionById Scenario Alternatif 1 : non trouvé.
    */
   @Test
   public void testFindInCollectionById_SA1 ()
   {
      final List<TestDto> v_tests = Arrays.asList(new TestDto(1L), new TestDto(2L));
      assertNull(DtoUtil.findInCollectionById(v_tests, 3L)); // retourne null car id 3L non trouvé
   }

   /**
    * Test findInCollectionById Scenario Exception 1 : id null.
    */
   @Test
   public void testFindInCollectionById_SE1 ()
   {
      assertThrows(IllegalArgumentException.class, () -> {
         final List<TestDto> v_tests = Arrays.asList(new TestDto(1L), new TestDto(2L));
         DtoUtil.findInCollectionById(v_tests, new TestDto(null)); // exception lancée ici
      });
   }

   /**
    * Test findInCollectionByListIds.
    */
   @Test
   public void testFindInCollectionByListIds ()
   {
      final List<TestDto> v_tests = Arrays.asList(new TestDto(1L), new TestDto(2L));
      assertTrue(DtoUtil.findInCollectionByListIds(v_tests, Arrays.asList(1L)).size() == 1);
      assertTrue(DtoUtil.findInCollectionByListIds(v_tests, Arrays.asList(1L, 2L)).size() == 2);
      assertTrue(DtoUtil.findInCollectionByListIds(v_tests, new ArrayList<Long>()).size() == 0);
   }

   /**
    * Test findInCollectionByListIds Scenario Exception 1 : un id ne correspond pas à un DTO dans la liste.
    */
   @Test
   public void testFindInCollectionByListIds_SE1 ()
   {
      assertThrows(Spi4jRuntimeException.class, () -> {
         final List<TestDto> v_tests = Arrays.asList(new TestDto(1L), new TestDto(2L));
         DtoUtil.findInCollectionByListIds(v_tests, Arrays.asList(-1L));
      });
   }

   /**
    * Test findInCollectionByAttribute.
    */
   @Test
   public void testFindInCollectionByAttribute ()
   {
      final List<TestDto> v_tests = Arrays.asList(new TestDto(1L), new TestDto(2L));
      v_tests.get(0).setNom(null);
      assertTrue(DtoUtil.findInCollectionByAttribute(v_tests, TestAttributes_Enum.id, 1L).size() == 1);
      assertTrue(DtoUtil.findInCollectionByAttribute(v_tests, TestAttributes_Enum.nom, null).size() == 1);
      assertTrue(DtoUtil.findInCollectionByAttribute(v_tests, TestAttributes_Enum.nom, "Durand").isEmpty());

      final List<TestDto> v_tests2 = Arrays.asList(null, new TestDto(1L), new TestDto(2L));
      assertTrue(DtoUtil.findInCollectionByAttribute(v_tests2, TestAttributes_Enum.id, 1L).size() == 1);
   }

   /**
    * Test findOneInCollectionByAttribute.
    */
   @Test
   public void testFindOneInCollectionByAttribute ()
   {
      final Dto_Itf<?> v_testDto1 = new TestDto(1L);
      final Dto_Itf<?> v_testDto2 = new TestDto(2L);

      final List<Dto_Itf<?>> v_tests = Arrays.asList(v_testDto1, v_testDto2);
      for (final Dto_Itf<?> v_dto : v_tests)
      {
         ((TestDto) v_dto).setNom(null);
      }
      assertNotNull(DtoUtil.findOneInCollectionByAttribute(v_tests, TestAttributes_Enum.id, 1L));
      try
      {
         DtoUtil.findOneInCollectionByAttribute(v_tests, TestAttributes_Enum.nom, null);
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
         DtoUtil.findOneInCollectionByAttribute(v_tests, TestAttributes_Enum.nom, "Durand");
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
      final List<TestDto> v_tests = Arrays.asList(new TestDto(1L), new TestDto(2L));
      final List<Long> v_list = DtoUtil.createListIds(v_tests);
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
      final List<TestDto> v_tests = Arrays.asList(new TestDto(1L), new TestDto(2L));
      final Map<Long, TestDto> v_map = DtoUtil.createMapById(v_tests);
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
      final List<TestDto> v_tests = Arrays.asList(new TestDto(1L), new TestDto(2L));
      final Map<Long, TestDto> v_map = DtoUtil.createSortedMapById(v_tests);
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
      final List<TestDto> v_tests = Arrays.asList(new TestDto(1L), new TestDto(2L));

      Collections.reverse(v_tests);
      DtoUtil.sortListByAttribute(v_tests, TestAttributes_Enum.id);
      assertEquals(1L, v_tests.get(0).getId().longValue());

      DtoUtil.sortListByAttribute(v_tests, TestAttributes_Enum.nom);

      DtoUtil.sortListByAttribute(v_tests, TestAttributes_Enum.id, false, true);
      assertEquals(2L, v_tests.get(0).getId().longValue());

      DtoUtil.sortListByAttribute(v_tests, TestAttributes_Enum.nom, true, false);
   }

   /**
    * Test sortListByAttribute : Scenario Alternatif 1 (comparaison de String)
    */
   @Test
   public void testSortListByAttribute_SA1 ()
   {
      final TestDto v_testDto1 = new TestDto(1L);
      final TestDto v_testDto2 = new TestDto(2L);
      v_testDto2.setNom("Toto");
      final List<TestDto> v_tests = Arrays.asList(v_testDto1, v_testDto2);
      Collections.reverse(v_tests);
      DtoUtil.sortListByAttribute(v_tests, TestAttributes_Enum.nom, false, true);
      assertEquals("Toto", v_tests.get(0).getNom());
      DtoUtil.sortListByAttribute(v_tests, TestAttributes_Enum.nom, true, false);
      assertEquals("Toto", v_tests.get(1).getNom());
   }

   /**
    * Test sortListByAttribute : Scenario Alternatif 2 (comparaison avec 1 null)
    */
   @Test
   public void testSortListByAttribute_SA2 ()
   {
      final TestDto v_testDto1 = new TestDto(1L);
      final TestDto v_testDto2 = new TestDto(2L);
      v_testDto1.setNom(null);
      final List<TestDto> v_tests = Arrays.asList(v_testDto1, v_testDto2);
      assertNull(v_testDto1.getNom());
      Collections.reverse(v_tests);
      DtoUtil.sortListByAttribute(v_tests, TestAttributes_Enum.nom);
      assertEquals(1L, v_tests.get(0).getId().longValue());
      assertNull(v_tests.get(0).getNom());
   }

   /**
    * Test sortListByAttribute : Scenario Alternatif 3 (comparaison avec 1 null)
    */
   @Test
   public void testSortListByAttribute_SA3 ()
   {
      final TestDto v_testDto1 = new TestDto(1L);
      final TestDto v_testDto2 = new TestDto(2L);
      v_testDto2.setNom(null);
      final List<TestDto> v_tests = Arrays.asList(v_testDto1, v_testDto2);
      assertNull(v_testDto2.getNom());
      Collections.reverse(v_tests);
      DtoUtil.sortListByAttribute(v_tests, TestAttributes_Enum.nom);
      assertEquals(2L, v_tests.get(0).getId().longValue());
      assertNull(v_tests.get(0).getNom());
   }

   /**
    * Test sortListByAttribute : Scenario Alternatif 3 (comparaison avec 1 null)
    */
   @Test
   public void testSortListByAttribute_SA4 ()
   {
      final TestDto v_testDto1 = new TestDto(1L);
      final TestDto v_testDto2 = new TestDto(2L);
      final List<TestDto> v_tests = Arrays.asList(v_testDto1, v_testDto2);
      DtoUtil.sortListByAttribute(v_tests, TestAttributes_Enum.firstChild, true, true);
      DtoUtil.sortListByAttribute(v_tests, TestAttributes_Enum.firstChild, false, false);
   }

   /**
    * Test getAttributeValue.
    */
   @Test
   public void testGetAttributeValue ()
   {
      final Dto_Itf<?> v_testDto = new TestDto(1L);
      assertEquals(1L, DtoUtil.getAttributeValue(v_testDto, TestAttributes_Enum.id));
   }

   /**
    * Test setAttributeValue.
    */
   @Test
   public void testSetAttributeValue ()
   {
      final Dto_Itf<?> v_testDto = new TestDto(1L);
      DtoUtil.setAttributeValue(v_testDto, TestAttributes_Enum.nom, "test");
   }

   /**
    * Test deepClone.
    */
   @Test
   public void deepClone ()
   {
      final Dto_Itf<?> v_testDto = new TestDto(1L);
      assertEquals(1L, DtoUtil.deepClone(v_testDto).getId());
   }

   /**
    * Test clone.
    */
   @Test
   public void testClone ()
   {
      final Dto_Itf<?> v_testDto = new TestDto(1L);

      assertEquals(1L, DtoUtil.clone(v_testDto).getId());

   }

   /**
    * Test deepClone.
    */
   @Test
   public void testSpi4jValidationException ()
   {
      final Spi4jValidationException v_spi4jValidationException = new Spi4jValidationException(new TestDto(1L), "nom");
      assertNotNull(Spi4jValidationException.class.getSimpleName(), v_spi4jValidationException.getMessage());
      final Spi4jValidationException v_spi4jValidationException2 = new Spi4jValidationException(new TestDto(1L),
               new Exception("dummy cause"), "nom");
      assertNotNull(Spi4jValidationException.class.getSimpleName(), v_spi4jValidationException2.getMessage());
   }
}
