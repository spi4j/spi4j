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

import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Classe de tests des mappers DTO <-> XTO.
 * @author MINARM
 */
public class Mapper_Test
{
   private static final Logger c_log = LogManager.getLogger(Mapper_Test.class);

   private static final Long c_id = 2L;

   private static final boolean c_bool = false;

   private static final String c_str = "Bonjour.";

   private static final Mapper_Abs<SousObjetDto, SousObjetXto> c_sousObjetMapper = new Mapper_Abs<>()
   {

      @Override
      public SousObjetXto convertDtoToXto (final SousObjetDto p_dto)
      {
         final SousObjetXto v_xto = super.convertDtoToXto(p_dto);

         // Start of user code convertDtoToXto
         if (p_dto.get_parent() != null)
         {
            final SimpleXto v_parent = new SimpleXto();
            v_parent.setId(p_dto.get_parent().getId());
            v_xto.set_parent(v_parent);
         }
         // End of user code

         return v_xto;
      }

      @Override
      public SousObjetDto convertXtoToDto (final SousObjetXto p_xto)
      {
         final SousObjetDto v_dto = super.convertXtoToDto(p_xto);

         // Start of user code convertDtoToXto
         if (p_xto.get_parent() != null)
         {
            final SimpleDto v_parent = new SimpleDto();
            v_parent.setId(p_xto.get_parent().getId());
            v_dto.set_parent(v_parent);
         }
         // End of user code

         return v_dto;
      }

      @Override
      protected SousObjetDto getInstanceOfDto ()
      {
         return new SousObjetDto();
      }

      @Override
      protected SousObjetXto getInstanceOfXto ()
      {
         return new SousObjetXto();
      }

      @Override
      protected Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> getSpecificMapper (final String p_fieldName)
      {
         if ("_parent".equals(p_fieldName))
         {
            return null;
         }

         return super.getSpecificMapper(p_fieldName);
      }

   };

   private static final Mapper_Abs<SimpleDto, SimpleXto> c_simpleMapper = new Mapper_Abs<>()
   {

      @Override
      protected SimpleDto getInstanceOfDto ()
      {
         return new SimpleDto();
      }

      @Override
      protected SimpleXto getInstanceOfXto ()
      {
         return new SimpleXto();
      }

      @Override
      protected Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> getSpecificMapper (final String p_fieldName)
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
    * Teste la conversion d'un XTO en DTO.
    */
   @Test
   public void testConvertXtoToDto ()
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

      final List<SimpleDto> v_dtoList = c_simpleMapper.convertListXtoToListDto(v_xtoList);

      checkConvertXtoToDto(v_xtoList, v_dtoList);

      // nulls et collections vides
      final List<SousObjetXto> v_tab_sousObjets2 = Collections.emptyList();
      final Map<Integer, SousObjetXto> v_map_intSousObjets2 = Collections.emptyMap();
      final Map<SousObjetXto, Long> v_map_sousObjetsLong2 = Collections.emptyMap();
      v_xto.set_sousObjet(null);
      v_xto.set_tab_sousObjets(v_tab_sousObjets2);
      v_xto.set_map_intSousObjets(v_map_intSousObjets2);
      v_xto.set_map_sousObjetsLong(v_map_sousObjetsLong2);
      final List<SimpleXto> v_xtoList2 = Collections.singletonList(v_xto);
      final List<SimpleDto> v_dtoList2 = c_simpleMapper.convertListXtoToListDto(v_xtoList2);
      checkConvertXtoToDto(v_xtoList2, v_dtoList2);

      v_xto.set_tab_sousObjets(null);
      v_xto.set_map_intSousObjets(null);
      v_xto.set_map_sousObjetsLong(null);
      final List<SimpleXto> v_xtoList3 = Collections.singletonList(v_xto);
      final List<SimpleDto> v_dtoList3 = c_simpleMapper.convertListXtoToListDto(v_xtoList3);
      checkConvertXtoToDto(v_xtoList3, v_dtoList3);

      assertNull(c_simpleMapper.convertListXtoToListDto(null), "null doit être converti en null");
   }

   /**
    * Assertions sur le résultat du convertListXtoToListDto
    * @param p_xtoList
    *           Avant conversion
    * @param p_dtoList
    *           Après conversion
    */
   private void checkConvertXtoToDto (final List<SimpleXto> p_xtoList, final List<SimpleDto> p_dtoList)
   {
      final SimpleXto v_xto = p_xtoList.get(0);

      assertNotNull(p_dtoList, "La liste doit avoir été convertie");
      assertEquals(1, p_dtoList.size(), "La liste devrait avoir 1 seul élément");
      final SimpleDto v_dto = p_dtoList.get(0);

      c_log.info("XTO : " + v_xto);
      c_log.info("DTO : " + v_dto);

      assertEquals(c_id, v_dto.getId(), "Problème dans la conversion de la clé primaire");
      assertEquals(c_bool, v_dto.get_bool(), "Problème dans la conversion du booléen");
      assertEquals(c_str, v_dto.get_str(), "Problème dans la conversion de la String");
      if (v_xto.get_sousObjet() != null)
      {
         assertNotNull(v_dto.get_sousObjet(), "Aucune conversion du sous objet");
         assertEquals(v_xto.get_sousObjet().get_value(), v_dto.get_sousObjet().get_value(),
                  "Problème dans la conversion du sous objet");
      }
      else
      {
         assertNull(v_dto.get_sousObjet(), "Aucune conversion du sous objet");
      }

      if (v_xto.get_tab_sousObjets() != null)
      {
         assertNotNull(v_dto.get_tab_sousObjets(), "Aucune conversion de la liste");
         for (final Object v_sousObjetDtoObjet : v_dto.get_tab_sousObjets())
         {
            assertTrue(v_sousObjetDtoObjet == null || v_sousObjetDtoObjet instanceof SousObjetDto,
                     "Mauvais type de sous objet dans la liste");
         }
      }
      else
      {
         assertNull(v_dto.get_tab_sousObjets(), "Aucune conversion de la liste");
      }

      checkConvertXtoToDtoMapSousObjets(v_xto, v_dto);
   }

   /**
    * @param p_xto
    *           Xto
    * @param p_dto
    *           Dto
    */
   private void checkConvertXtoToDtoMapSousObjets (final SimpleXto p_xto, final SimpleDto p_dto)
   {
      if (p_xto.get_map_intSousObjets() != null)
      {
         assertNotNull(p_dto.get_map_intSousObjets(), "Aucune conversion de la map avec xto en valeurs");
         for (final Entry<Integer, SousObjetXto> v_xtoEntry : p_xto.get_map_intSousObjets().entrySet())
         {
            final Object v_sousObjetDtoObject = p_dto.get_map_intSousObjets().get(v_xtoEntry.getKey());
            assertTrue(v_sousObjetDtoObject == null || v_sousObjetDtoObject instanceof SousObjetDto,
                     "Mauvais type de sous objet dans la map avec xto en valeurs");
            if (v_sousObjetDtoObject == null)
            {
               assertNull(v_xtoEntry.getValue(), "Problème dans la conversion de la map avec xto en valeurs");
            }
            else
            {
               final SousObjetDto v_sousObjetDto = (SousObjetDto) v_sousObjetDtoObject;
               assertEquals(v_xtoEntry.getValue().get_value(), v_sousObjetDto.get_value(),
                        "Problème dans la conversion de la map avec xto en valeurs");
            }
         }
      }
      else
      {
         assertNull(p_dto.get_map_intSousObjets(), "Aucune conversion de la map avec xto en valeurs");
      }

      if (p_xto.get_map_sousObjetsLong() != null)
      {
         assertNotNull(p_dto.get_map_sousObjetsLong(), "Aucune conversion de la map avec xto en clés");
         for (final Entry<SousObjetDto, Long> v_dtoEntry : p_dto.get_map_sousObjetsLong().entrySet())
         {
            // recherche le XTO correspondant à la valeur de l'entrée pour comparer avec le DTO
            final Object v_xtoKeyObject = findKeyForValueInMap(p_xto.get_map_sousObjetsLong(), v_dtoEntry.getValue());
            assertTrue(v_xtoKeyObject == null || v_xtoKeyObject instanceof SousObjetXto,
                     "Mauvais type de sous objet dans la map avec xto en clés");
            if (v_xtoKeyObject == null)
            {
               assertNull(v_dtoEntry.getKey(), "Problème dans la conversion de la map avec xto en clés");
            }
            else
            {
               final SousObjetXto v_sousObjetXto = (SousObjetXto) v_xtoKeyObject;
               assertEquals(v_dtoEntry.getKey().get_value(), v_sousObjetXto.get_value(),
                        "Problème dans la conversion de la map avec xto en clés");
            }
         }
      }
      else
      {
         assertNull(p_dto.get_map_sousObjetsLong(), "Aucune conversion de la map avec xto en clés");
      }
   }

   /**
    * Teste la conversion d'un DTO en XTO.
    */
   @Test
   public void testConvertDtoToXto ()
   {
      final SimpleDto v_dto = new SimpleDto();

      final SousObjetDto v_sousObjetA = new SousObjetDto();
      v_sousObjetA.set_value("sousObj A");
      v_sousObjetA.set_parent(v_dto);
      final SousObjetDto v_sousObjetB = new SousObjetDto();
      v_sousObjetB.set_value("sousObj B");
      v_sousObjetB.set_parent(v_dto);

      v_dto.setId(c_id);
      v_dto.set_bool(c_bool);
      v_dto.set_str(c_str);
      v_dto.set_sousObjet(v_sousObjetA);
      final List<SousObjetDto> v_tab_sousObjets = new ArrayList<>();
      v_tab_sousObjets.add(null);
      v_tab_sousObjets.add(v_sousObjetA);
      v_tab_sousObjets.add(v_sousObjetB);
      v_dto.set_tab_sousObjets(v_tab_sousObjets);
      final Map<Integer, SousObjetDto> v_map_intSousObjets = new HashMap<>();
      v_map_intSousObjets.put(0, null);
      v_map_intSousObjets.put(null, v_sousObjetA);
      v_map_intSousObjets.put(1, v_sousObjetB);
      v_dto.set_map_intSousObjets(v_map_intSousObjets);
      final Map<SousObjetDto, Long> v_map_sousObjetsLong = new HashMap<>();
      v_map_sousObjetsLong.put(v_sousObjetA, null);
      v_map_sousObjetsLong.put(null, 10L);
      v_map_sousObjetsLong.put(v_sousObjetB, 20L);
      v_dto.set_map_sousObjetsLong(v_map_sousObjetsLong);

      final List<SimpleDto> v_dtoList = Collections.singletonList(v_dto);

      final List<SimpleXto> v_xtoList = c_simpleMapper.convertListDtoToListXto(v_dtoList);

      checkConvertDtoToXto(v_dtoList, v_xtoList);

      // nulls et collections vides
      final List<SousObjetDto> v_tab_sousObjets2 = Collections.emptyList();
      final Map<Integer, SousObjetDto> v_map_intSousObjets2 = Collections.emptyMap();
      final Map<SousObjetDto, Long> v_map_sousObjetsLong2 = Collections.emptyMap();
      v_dto.set_sousObjet(null);
      v_dto.set_tab_sousObjets(v_tab_sousObjets2);
      v_dto.set_map_intSousObjets(v_map_intSousObjets2);
      v_dto.set_map_sousObjetsLong(v_map_sousObjetsLong2);
      final List<SimpleDto> v_dtoList2 = Collections.singletonList(v_dto);
      final List<SimpleXto> v_xtoList2 = c_simpleMapper.convertListDtoToListXto(v_dtoList2);
      checkConvertDtoToXto(v_dtoList2, v_xtoList2);

      v_dto.set_tab_sousObjets(null);
      v_dto.set_map_intSousObjets(null);
      v_dto.set_map_sousObjetsLong(null);
      final List<SimpleDto> v_dtoList3 = Collections.singletonList(v_dto);
      final List<SimpleXto> v_xtoList3 = c_simpleMapper.convertListDtoToListXto(v_dtoList3);
      checkConvertDtoToXto(v_dtoList3, v_xtoList3);

      assertNull(c_simpleMapper.convertListDtoToListXto(null), "null doit être converti en null");
   }

   /**
    * Assertions sur le résultat du convertListDtoToListXto
    * @param p_dtoList
    *           Avant conversion
    * @param p_xtoList
    *           Après conversion
    */
   private void checkConvertDtoToXto (final List<SimpleDto> p_dtoList, final List<SimpleXto> p_xtoList)
   {
      final SimpleDto v_dto = p_dtoList.get(0);

      assertNotNull(p_xtoList, "La liste doit avoir été convertie");
      assertEquals(1, p_xtoList.size(), "La liste devrait avoir 1 seul élément");
      final SimpleXto v_xto = p_xtoList.get(0);

      c_log.info("DTO : " + v_dto);
      c_log.info("XTO : " + v_xto);

      assertEquals(c_id, v_xto.getId(), "Problème dans la conversion de la clé primaire");
      assertEquals(c_bool, v_xto.get_bool(), "Problème dans la conversion du booléen");
      assertEquals(c_str, v_xto.get_str(), "Problème dans la conversion de la String");
      if (v_dto.get_sousObjet() != null)
      {
         assertNotNull(v_xto.get_sousObjet(), "Aucune conversion du sous objet");
         assertEquals(v_dto.get_sousObjet().get_value(), v_xto.get_sousObjet().get_value(),
                  "Problème dans la conversion du sous objet");
      }
      else
      {
         assertNull(v_xto.get_sousObjet(), "Aucune conversion du sous objet");
      }

      if (v_dto.get_tab_sousObjets() != null)
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

      checkConvertDtoToXtoMapSousObjets(v_dto, v_xto);
   }

   /**
    * @param p_dto
    *           Dto
    * @param p_xto
    *           Xto
    */
   private void checkConvertDtoToXtoMapSousObjets (final SimpleDto p_dto, final SimpleXto p_xto)
   {
      if (p_dto.get_map_intSousObjets() != null)
      {
         assertNotNull(p_xto.get_map_intSousObjets(), "Aucune conversion de la map avec dto en valeurs");
         for (final Entry<Integer, SousObjetDto> v_dtoEntry : p_dto.get_map_intSousObjets().entrySet())
         {
            final Object v_sousObjetXtoObject = p_xto.get_map_intSousObjets().get(v_dtoEntry.getKey());
            assertTrue(v_sousObjetXtoObject == null || v_sousObjetXtoObject instanceof SousObjetXto,
                     "Mauvais type de sous objet dans la map avec dto en valeurs");
            if (v_sousObjetXtoObject == null)
            {
               assertNull(v_dtoEntry.getValue(), "Problème dans la conversion de la map avec dto en valeurs");
            }
            else
            {
               final SousObjetXto v_sousObjetXto = (SousObjetXto) v_sousObjetXtoObject;
               assertEquals(v_dtoEntry.getValue().get_value(), v_sousObjetXto.get_value(),
                        "Problème dans la conversion de la map avec dto en valeurs");
            }
         }
      }
      else
      {
         assertNull(p_xto.get_map_intSousObjets(), "Aucune conversion de la map avec dto en valeurs");
      }

      if (p_dto.get_map_sousObjetsLong() != null)
      {
         assertNotNull(p_xto.get_map_sousObjetsLong(), "Aucune conversion de la map avec dto en clés");
         for (final Entry<SousObjetXto, Long> v_xtoEntry : p_xto.get_map_sousObjetsLong().entrySet())
         {
            // recherche le DTO correspondant à la valeur de l'entrée pour comparer avec le XTO
            final Object v_dtoKeyObject = findKeyForValueInMap(p_dto.get_map_sousObjetsLong(), v_xtoEntry.getValue());
            assertTrue(v_dtoKeyObject == null || v_dtoKeyObject instanceof SousObjetDto,
                     "Mauvais type de sous objet dans la map avec dto en clés");
            if (v_dtoKeyObject == null)
            {
               assertNull(v_xtoEntry.getKey(), "Problème dans la conversion de la map avec dto en clés");
            }
            else
            {
               final SousObjetDto v_sousObjetDto = (SousObjetDto) v_dtoKeyObject;
               assertEquals(v_xtoEntry.getKey().get_value(), v_sousObjetDto.get_value(),
                        "Problème dans la conversion de la map avec dto en clés");
            }
         }
      }
      else
      {
         assertNull(p_xto.get_map_sousObjetsLong(), "Aucune conversion de la map avec dto en clés");
      }
   }

   /**
    * Test d'exception si un mapper spécifique n'a pas été défini sur un champ.
    */
   @Test
   public void testExceptionIfNoSpecificMapper ()
   {
      final Mapper_Abs<SimpleDto, SimpleXto> v_mapper = new Mapper_Abs<>()
      {

         @Override
         protected SimpleXto getInstanceOfXto ()
         {
            return new SimpleXto();
         }

         @Override
         protected SimpleDto getInstanceOfDto ()
         {
            return new SimpleDto();
         }
      };
      final SimpleDto v_dto = new SimpleDto();
      final SousObjetDto v_sousObjet = new SousObjetDto();
      v_dto.set_sousObjet(v_sousObjet);
      try
      {
         v_mapper.convertDtoToXto(v_dto);
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
         v_mapper.convertXtoToDto(v_xto);
         fail("Erreur attendue");
      }
      catch (final Spi4jRuntimeException v_erreur)
      {
         assertEquals("Un mapper a été demandé pour le champ _sousObjet mais aucun n'a été renseigné dans "
                  + v_mapper.getClass(), v_erreur.getCause().getMessage(), "Erreur attendue");
      }
   }

}
