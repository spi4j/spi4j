/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Classe de tests des mappers Entity <-> XTO.
 * @author MINARM
 */
public class EntityMapper_Test
{
   private static final Logger c_log = LogManager.getLogger(EntityMapper_Test.class);

   private static final Long c_id = 2L;

   private static final boolean c_bool = false;

   private static final String c_str = "Bonjour.";

   private static final EntityMapper_Abs<SousObjetEntity, SousObjetXto> c_sousObjetMapper = new EntityMapper_Abs<>()
   {

      @Override
      public SousObjetXto convertEntityToXto (final SousObjetEntity p_entity)
      {
         final SousObjetXto v_xto = super.convertEntityToXto(p_entity);

         // Start of user code convertEntityToXto
         if (p_entity.get_parent() != null)
         {
            final SimpleXto v_parent = new SimpleXto();
            v_parent.setId(p_entity.get_parent().getId());
            v_xto.set_parent(v_parent);
         }
         // End of user code

         return v_xto;
      }

      @Override
      public SousObjetEntity convertXtoToEntity (final SousObjetXto p_xto)
      {
         final SousObjetEntity v_entity = super.convertXtoToEntity(p_xto);

         // Start of user code convertEntityToXto
         if (p_xto.get_parent() != null)
         {
            final SimpleEntity v_parent = new SimpleEntity();
            v_parent.setId(p_xto.get_parent().getId());
            v_entity.set_parent(v_parent);
         }
         // End of user code

         return v_entity;
      }

      @Override
      protected SousObjetEntity getInstanceOfEntity ()
      {
         return new SousObjetEntity();
      }

      @Override
      protected SousObjetXto getInstanceOfXto ()
      {
         return new SousObjetXto();
      }

      @Override
      protected EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> getSpecificMapper (
               final String p_fieldName)
      {
         if ("_parent".equals(p_fieldName))
         {
            return null;
         }

         return super.getSpecificMapper(p_fieldName);
      }

   };

   private static final EntityMapper_Abs<SimpleEntity, SimpleXto> c_simpleMapper = new EntityMapper_Abs<>()
   {

      @Override
      protected SimpleEntity getInstanceOfEntity ()
      {
         return new SimpleEntity();
      }

      @Override
      protected SimpleXto getInstanceOfXto ()
      {
         return new SimpleXto();
      }

      @Override
      protected EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> getSpecificMapper (
               final String p_fieldName)
      {
         if ("_sousObjet".equals(p_fieldName))
         {
            return c_sousObjetMapper;
         }
         if ("_tab_sousObjets".equals(p_fieldName))
         {
            return c_sousObjetMapper;
         }
         if ("_map_intSousObjets".equals(p_fieldName))
         {
            return c_sousObjetMapper;
         }
         if ("_map_sousObjetsLong".equals(p_fieldName))
         {
            return c_sousObjetMapper;
         }

         return super.getSpecificMapper(p_fieldName);
      }
   };

   /**
    * Recherche la (première) clé pour une valeur dans une map.
    * @param <Key>
    *           le type de clés
    * @param <Value>
    *           le type de valeurs
    * @param p_map
    *           la map
    * @param p_value
    *           la valeur
    * @return la première clé pour cette valeur dans la map
    */
   private <Key, Value> Key findKeyForValueInMap (final Map<Key, Value> p_map, final Value p_value)
   {
      for (final Entry<Key, Value> v_entry : p_map.entrySet())
      {
         if ((p_value == null && v_entry.getValue() == null) || (p_value != null && p_value.equals(v_entry.getValue())))
         {
            return v_entry.getKey();
         }
      }
      return null;
   }

   /**
    * Teste la conversion d'un XTO en Entity.
    */
   @Test
   public void testConvertXtoToEntity ()
   {
      final SimpleXto v_xto = new SimpleXto();

      final SousObjetXto v_sousObjet1 = new SousObjetXto();
      v_sousObjet1.set_value("sousObj 1");
      v_sousObjet1.set_parent(v_xto);
      final SousObjetXto v_sousObjet2 = new SousObjetXto();
      v_sousObjet2.set_value("sousObj 2");
      v_sousObjet2.set_parent(v_xto);

      v_xto.setId(c_id);
      v_xto.set_bool(c_bool);
      v_xto.set_str(c_str);
      v_xto.set_sousObjet(v_sousObjet1);
      final List<SousObjetXto> v_tab_sousObjets = new ArrayList<>();
      v_tab_sousObjets.add(v_sousObjet1);
      v_tab_sousObjets.add(null);
      v_tab_sousObjets.add(v_sousObjet2);
      v_xto.set_tab_sousObjets(v_tab_sousObjets);
      final Map<Integer, SousObjetXto> v_map_intSousObjets = new HashMap<>();
      v_map_intSousObjets.put(null, v_sousObjet1);
      v_map_intSousObjets.put(0, null);
      v_map_intSousObjets.put(1, v_sousObjet2);
      v_xto.set_map_intSousObjets(v_map_intSousObjets);
      final Map<SousObjetXto, Long> v_map_sousObjetsLong = new HashMap<>();
      v_map_sousObjetsLong.put(null, 10L);
      v_map_sousObjetsLong.put(v_sousObjet1, null);
      v_map_sousObjetsLong.put(v_sousObjet2, 20L);
      v_xto.set_map_sousObjetsLong(v_map_sousObjetsLong);

      // Création d'une liste contenant le xto
      final List<SimpleXto> v_xtoList = Collections.singletonList(v_xto);

      final List<SimpleEntity> v_entityList = c_simpleMapper.convertListXtoToListEntity(v_xtoList);

      checkConvertXtoToEntity(v_xtoList, v_entityList);

      // nulls et collections vides
      final List<SousObjetXto> v_tab_sousObjets2 = Collections.emptyList();
      final Map<Integer, SousObjetXto> v_map_intSousObjets2 = Collections.emptyMap();
      final Map<SousObjetXto, Long> v_map_sousObjetsLong2 = Collections.emptyMap();
      v_xto.set_sousObjet(null);
      v_xto.set_tab_sousObjets(v_tab_sousObjets2);
      v_xto.set_map_intSousObjets(v_map_intSousObjets2);
      v_xto.set_map_sousObjetsLong(v_map_sousObjetsLong2);
      final List<SimpleXto> v_xtoList2 = Collections.singletonList(v_xto);
      final List<SimpleEntity> v_entityList2 = c_simpleMapper.convertListXtoToListEntity(v_xtoList2);
      checkConvertXtoToEntity(v_xtoList2, v_entityList2);

      v_xto.set_tab_sousObjets(null);
      v_xto.set_map_intSousObjets(null);
      v_xto.set_map_sousObjetsLong(null);
      final List<SimpleXto> v_xtoList3 = Collections.singletonList(v_xto);
      final List<SimpleEntity> v_entityList3 = c_simpleMapper.convertListXtoToListEntity(v_xtoList3);
      checkConvertXtoToEntity(v_xtoList3, v_entityList3);

      assertNull(c_simpleMapper.convertListXtoToListEntity(null), "null doit être converti en null");
   }

   /**
    * Assertions sur le résultat du convertListXtoToListEntity
    * @param p_xtoList
    *           Avant conversion
    * @param p_entityList
    *           Après conversion
    */
   private void checkConvertXtoToEntity (final List<SimpleXto> p_xtoList, final List<SimpleEntity> p_entityList)
   {
      final SimpleXto v_xto = p_xtoList.get(0);

      assertNotNull(p_entityList, "La liste doit avoir été convertie");
      assertEquals(1, p_entityList.size(), "La liste devrait avoir 1 seul élément");
      final SimpleEntity v_entity = p_entityList.get(0);

      c_log.info("XTO : " + v_xto);
      c_log.info("ENTITY : " + v_entity);

      assertEquals(c_id, v_entity.getId(), "Problème dans la conversion de la clé primaire");
      assertEquals(c_bool, v_entity.get_bool(), "Problème dans la conversion du booléen");
      assertEquals(c_str, v_entity.get_str(), "Problème dans la conversion de la String");
      if (v_xto.get_sousObjet() != null)
      {
         assertNotNull(v_entity.get_sousObjet(), "Aucune conversion du sous objet");
         assertEquals(v_xto.get_sousObjet().get_value(), v_entity.get_sousObjet().get_value(),
                  "Problème dans la conversion du sous objet");
      }
      else
      {
         assertNull(v_entity.get_sousObjet(), "Aucune conversion du sous objet");
      }

      if (v_xto.get_tab_sousObjets() != null)
      {
         assertNotNull(v_entity.get_tab_sousObjets(), "Aucune conversion de la liste");
         for (final Object v_sousObjetEntityObjet : v_entity.get_tab_sousObjets())
         {
            assertTrue(v_sousObjetEntityObjet == null || v_sousObjetEntityObjet instanceof SousObjetEntity,
                     "Mauvais type de sous objet dans la liste");
         }
      }
      else
      {
         assertNull(v_entity.get_tab_sousObjets(), "Aucune conversion de la liste");
      }

      checkConvertXtoToEntityMapSousObjets(v_xto, v_entity);
   }

   /**
    * @param p_xto
    *           Xto
    * @param p_entity
    *           Entity
    */
   private void checkConvertXtoToEntityMapSousObjets (final SimpleXto p_xto, final SimpleEntity p_entity)
   {
      if (p_xto.get_map_intSousObjets() != null)
      {
         assertNotNull(p_entity.get_map_intSousObjets(), "Aucune conversion de la map avec xto en valeurs");
         for (final Entry<Integer, SousObjetXto> v_xtoEntry : p_xto.get_map_intSousObjets().entrySet())
         {
            final Object v_sousObjetEntityObject = p_entity.get_map_intSousObjets().get(v_xtoEntry.getKey());
            assertTrue(v_sousObjetEntityObject == null || v_sousObjetEntityObject instanceof SousObjetEntity,
                     "Mauvais type de sous objet dans la map avec xto en valeurs");
            if (v_sousObjetEntityObject == null)
            {
               assertNull(v_xtoEntry.getValue(), "Problème dans la conversion de la map avec xto en valeurs");
            }
            else
            {
               final SousObjetEntity v_sousObjetEntity = (SousObjetEntity) v_sousObjetEntityObject;
               assertEquals(v_xtoEntry.getValue().get_value(), v_sousObjetEntity.get_value(),
                        "Problème dans la conversion de la map avec xto en valeurs");
            }
         }
      }
      else
      {
         assertNull(p_entity.get_map_intSousObjets(), "Aucune conversion de la map avec xto en valeurs");
      }

      if (p_xto.get_map_sousObjetsLong() != null)
      {
         assertNotNull(p_entity.get_map_sousObjetsLong(), "Aucune conversion de la map avec xto en clés");
         for (final Entry<SousObjetEntity, Long> v_entityEntry : p_entity.get_map_sousObjetsLong().entrySet())
         {
            // recherche le XTO correspondant à la valeur de l'entrée pour comparer avec le entity
            final Object v_xtoKeyObject = findKeyForValueInMap(p_xto.get_map_sousObjetsLong(),
                     v_entityEntry.getValue());
            assertTrue(v_xtoKeyObject == null || v_xtoKeyObject instanceof SousObjetXto,
                     "Mauvais type de sous objet dans la map avec xto en clés");
            if (v_xtoKeyObject == null)
            {
               assertNull(v_entityEntry.getKey(), "Problème dans la conversion de la map avec xto en clés");
            }
            else
            {
               final SousObjetXto v_sousObjetXto = (SousObjetXto) v_xtoKeyObject;
               assertEquals(v_entityEntry.getKey().get_value(), v_sousObjetXto.get_value(),
                        "Problème dans la conversion de la map avec xto en clés");
            }
         }
      }
      else
      {
         assertNull(p_entity.get_map_sousObjetsLong(), "Aucune conversion de la map avec xto en clés");
      }
   }

   /**
    * Teste la conversion d'un Entity en XTO.
    */
   @Test
   public void testConvertEntityToXto ()
   {
      final SimpleEntity v_entity = new SimpleEntity();

      final SousObjetEntity v_sousObjetA = new SousObjetEntity();
      v_sousObjetA.set_value("sousObj A");
      v_sousObjetA.set_parent(v_entity);
      final SousObjetEntity v_sousObjetB = new SousObjetEntity();
      v_sousObjetB.set_value("sousObj B");
      v_sousObjetB.set_parent(v_entity);

      v_entity.setId(c_id);
      v_entity.set_bool(c_bool);
      v_entity.set_str(c_str);
      v_entity.set_sousObjet(v_sousObjetA);
      final List<SousObjetEntity> v_tab_sousObjets = new ArrayList<>();
      v_tab_sousObjets.add(null);
      v_tab_sousObjets.add(v_sousObjetA);
      v_tab_sousObjets.add(v_sousObjetB);
      v_entity.set_tab_sousObjets(v_tab_sousObjets);
      final Map<Integer, SousObjetEntity> v_map_intSousObjets = new HashMap<>();
      v_map_intSousObjets.put(0, null);
      v_map_intSousObjets.put(null, v_sousObjetA);
      v_map_intSousObjets.put(1, v_sousObjetB);
      v_entity.set_map_intSousObjets(v_map_intSousObjets);
      final Map<SousObjetEntity, Long> v_map_sousObjetsLong = new HashMap<>();
      v_map_sousObjetsLong.put(v_sousObjetA, null);
      v_map_sousObjetsLong.put(null, 10L);
      v_map_sousObjetsLong.put(v_sousObjetB, 20L);
      v_entity.set_map_sousObjetsLong(v_map_sousObjetsLong);

      final List<SimpleEntity> v_entityList = Collections.singletonList(v_entity);

      final List<SimpleXto> v_xtoList = c_simpleMapper.convertListEntityToListXto(v_entityList);

      checkConvertEntityToXto(v_entityList, v_xtoList);

      // nulls et collections vides
      final List<SousObjetEntity> v_tab_sousObjets2 = Collections.emptyList();
      final Map<Integer, SousObjetEntity> v_map_intSousObjets2 = Collections.emptyMap();
      final Map<SousObjetEntity, Long> v_map_sousObjetsLong2 = Collections.emptyMap();
      v_entity.set_sousObjet(null);
      v_entity.set_tab_sousObjets(v_tab_sousObjets2);
      v_entity.set_map_intSousObjets(v_map_intSousObjets2);
      v_entity.set_map_sousObjetsLong(v_map_sousObjetsLong2);
      final List<SimpleEntity> v_entityList2 = Collections.singletonList(v_entity);
      final List<SimpleXto> v_xtoList2 = c_simpleMapper.convertListEntityToListXto(v_entityList2);
      checkConvertEntityToXto(v_entityList2, v_xtoList2);

      v_entity.set_tab_sousObjets(null);
      v_entity.set_map_intSousObjets(null);
      v_entity.set_map_sousObjetsLong(null);
      final List<SimpleEntity> v_entityList3 = Collections.singletonList(v_entity);
      final List<SimpleXto> v_xtoList3 = c_simpleMapper.convertListEntityToListXto(v_entityList3);
      checkConvertEntityToXto(v_entityList3, v_xtoList3);

      assertNull(c_simpleMapper.convertListEntityToListXto(null), "null doit être converti en null");
   }

   /**
    * Assertions sur le résultat du convertListEntityToListXto
    * @param p_entityList
    *           Avant conversion
    * @param p_xtoList
    *           Après conversion
    */
   private void checkConvertEntityToXto (final List<SimpleEntity> p_entityList, final List<SimpleXto> p_xtoList)
   {
      final SimpleEntity v_entity = p_entityList.get(0);

      assertNotNull(p_xtoList, "La liste doit avoir été convertie");
      assertEquals(1, p_xtoList.size(), "La liste devrait avoir 1 seul élément");
      final SimpleXto v_xto = p_xtoList.get(0);

      c_log.info("Entity : " + v_entity);
      c_log.info("XTO : " + v_xto);

      assertEquals(c_id, v_xto.getId(), "Problème dans la conversion de la clé primaire");
      assertEquals(c_bool, v_xto.get_bool(), "Problème dans la conversion du booléen");
      assertEquals(c_str, v_xto.get_str(), "Problème dans la conversion de la String");
      if (v_entity.get_sousObjet() != null)
      {
         assertNotNull(v_xto.get_sousObjet(), "Aucune conversion du sous objet");
         assertEquals(v_entity.get_sousObjet().get_value(), v_xto.get_sousObjet().get_value(),
                  "Problème dans la conversion du sous objet");
      }
      else
      {
         assertNull(v_xto.get_sousObjet(), "Aucune conversion du sous objet");
      }

      if (v_entity.get_tab_sousObjets() != null)
      {
         assertNotNull(v_xto.get_tab_sousObjets(), "Aucune conversion de la liste");
         for (final Object v_sousObjetXtoObjet : v_xto.get_tab_sousObjets())
         {
            assertTrue(v_sousObjetXtoObjet == null || v_sousObjetXtoObjet instanceof SousObjetXto,
                     "Mauvais type de sous objet dans la liste");
         }
      }
      else
      {
         assertNull(v_xto.get_tab_sousObjets(), "Aucune conversion de la liste");
      }

      checkConvertEntityToXtoMapSousObjets(v_entity, v_xto);
   }

   /**
    * @param p_entity
    *           Entity
    * @param p_xto
    *           Xto
    */
   private void checkConvertEntityToXtoMapSousObjets (final SimpleEntity p_entity, final SimpleXto p_xto)
   {
      if (p_entity.get_map_intSousObjets() != null)
      {
         assertNotNull(p_xto.get_map_intSousObjets(), "Aucune conversion de la map avec entity en valeurs");
         for (final Entry<Integer, SousObjetEntity> v_entityEntry : p_entity.get_map_intSousObjets().entrySet())
         {
            final Object v_sousObjetXtoObject = p_xto.get_map_intSousObjets().get(v_entityEntry.getKey());
            assertTrue(v_sousObjetXtoObject == null || v_sousObjetXtoObject instanceof SousObjetXto,
                     "Mauvais type de sous objet dans la map avec entity en valeurs");
            if (v_sousObjetXtoObject == null)
            {
               assertNull(v_entityEntry.getValue(), "Problème dans la conversion de la map avec entity en valeurs");
            }
            else
            {
               final SousObjetXto v_sousObjetXto = (SousObjetXto) v_sousObjetXtoObject;
               assertEquals(v_entityEntry.getValue().get_value(), v_sousObjetXto.get_value(),
                        "Problème dans la conversion de la map avec entity en valeurs");
            }
         }
      }
      else
      {
         assertNull(p_xto.get_map_intSousObjets(), "Aucune conversion de la map avec entity en valeurs");
      }

      if (p_entity.get_map_sousObjetsLong() != null)
      {
         assertNotNull(p_xto.get_map_sousObjetsLong(), "Aucune conversion de la map avec entity en clés");
         for (final Entry<SousObjetXto, Long> v_xtoEntry : p_xto.get_map_sousObjetsLong().entrySet())
         {
            // recherche le Entity correspondant à la valeur de l'entrée pour comparer avec le XTO
            final Object v_entityKeyObject = findKeyForValueInMap(p_entity.get_map_sousObjetsLong(),
                     v_xtoEntry.getValue());
            assertTrue(v_entityKeyObject == null || v_entityKeyObject instanceof SousObjetEntity,
                     "Mauvais type de sous objet dans la map avec entity en clés");
            if (v_entityKeyObject == null)
            {
               assertNull(v_xtoEntry.getKey(), "Problème dans la conversion de la map avec entity en clés");
            }
            else
            {
               final SousObjetEntity v_sousObjetEntity = (SousObjetEntity) v_entityKeyObject;
               assertEquals(v_xtoEntry.getKey().get_value(), v_sousObjetEntity.get_value(),
                        "Problème dans la conversion de la map avec entity en clés");
            }
         }
      }
      else
      {
         assertNull(p_xto.get_map_sousObjetsLong(), "Aucune conversion de la map avec entity en clés");
      }
   }

   /**
    * Test d'exception si un mapper spécifique n'a pas été défini sur un champ.
    */
   @Test
   public void testExceptionIfNoSpecificMapper ()
   {
      final EntityMapper_Abs<SimpleEntity, SimpleXto> v_mapper = new EntityMapper_Abs<>()
      {

         @Override
         protected SimpleXto getInstanceOfXto ()
         {
            return new SimpleXto();
         }

         @Override
         protected SimpleEntity getInstanceOfEntity ()
         {
            return new SimpleEntity();
         }
      };
      final SimpleEntity v_entity = new SimpleEntity();
      final SousObjetEntity v_sousObjet = new SousObjetEntity();
      v_entity.set_sousObjet(v_sousObjet);
      try
      {
         v_mapper.convertEntityToXto(v_entity);
         fail("Erreur attendue");
      }
      catch (final Spi4jRuntimeException v_erreur)
      {
         assertEquals("Un mapper a été demandé pour le champ _sousObjet mais aucun n'a été renseigné dans "
                  + v_mapper.getClass(), v_erreur.getCause().getMessage(), "Erreur attendue");
      }

      final SimpleXto v_xto = new SimpleXto();
      final SousObjetXto v_sousObjetXto = new SousObjetXto();
      v_xto.set_sousObjet(v_sousObjetXto);
      try
      {
         v_mapper.convertXtoToEntity(v_xto);
         fail("Erreur attendue");
      }
      catch (final Spi4jRuntimeException v_erreur)
      {
         assertEquals("Un mapper a été demandé pour le champ _sousObjet mais aucun n'a été renseigné dans "
                  + v_mapper.getClass(), v_erreur.getCause().getMessage(), "Erreur attendue");
      }
   }
}
