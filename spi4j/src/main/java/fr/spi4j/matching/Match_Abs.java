/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.persistence.dao.TableCriteria;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Classe abstraite de Matching entre le DTO et l'entity.
 * @author MINARM
 * @param <TypeId>
 *           Type de l'identifiant du DTO
 * @param <TypeDto>
 *           Type de DTO
 * @param <TypeEntity>
 *           Type d'entity
 * @param <Columns_Enum>
 *           Type de l'énumération des colonnes
 */
public abstract class Match_Abs<TypeId, TypeDto extends Dto_Itf<TypeId>, TypeEntity extends Entity_Itf<TypeId>, Columns_Enum extends ColumnsNames_Itf>
         implements Match_Itf<TypeId, TypeDto, TypeEntity, Columns_Enum>
{
   /**
    * @return Une instance du dao typée pour correspondre à l'instance du matching.
    */
   protected abstract Dao_Itf<TypeId, TypeEntity, Columns_Enum> getDao ();

   @Override
   public void create (final TypeDto p_dto) throws Spi4jValidationException
   {
      final TypeEntity v_entity = convertDtoToEntity(p_dto);
      getDao().create(v_entity);
      refreshDtoFromEntity(v_entity, p_dto);
   }

   @Override
   public TypeDto findById (final TypeId p_id)
   {
      final TypeEntity v_found = getDao().findById(p_id);
      return convertEntityToDto(v_found);
   }

   @Override
   public List<TypeDto> findAll ()
   {
      final List<TypeEntity> v_tab_entity = getDao().findAll();
      return convertListEntityToListDto(v_tab_entity);
   }

   @Override
   public List<TypeDto> findAll (final Columns_Enum p_orderByColumn)
   {
      final List<TypeEntity> v_tab_entity = getDao().findAll(p_orderByColumn);
      return convertListEntityToListDto(v_tab_entity);
   }

   @Override
   public void update (final TypeDto p_dto) throws Spi4jValidationException
   {
      final TypeEntity v_entity = convertDtoToEntity(p_dto);
      getDao().update(v_entity);
      refreshDtoFromEntity(v_entity, p_dto);
   }

   @Override
   public void delete (final TypeDto p_dto) throws Spi4jValidationException
   {
      final TypeEntity v_entity = convertDtoToEntity(p_dto);
      getDao().delete(v_entity);
      refreshDtoFromEntity(v_entity, p_dto);
   }

   @Override
   public int deleteAll ()
   {
      return getDao().deleteAll();
   }

   @Override
   public int deleteByCriteria (final TableCriteria<Columns_Enum> p_tableCriteria)
   {
      return getDao().deleteByCriteria(p_tableCriteria);
   }

   @Override
   public List<TypeDto> findByColumn (final Columns_Enum p_column, final Object p_value)
   {
      final List<TypeEntity> v_tab_entity = getDao().findByColumn(p_column, p_value);
      return convertListEntityToListDto(v_tab_entity);
   }

   @Override
   public List<TypeDto> findByCriteria (final String p_sqlCriteria,
            final Map<String, ? extends Object> p_map_value_by_name)
   {
      final List<TypeEntity> v_tab_entity = getDao().findByCriteria(p_sqlCriteria, p_map_value_by_name);
      return convertListEntityToListDto(v_tab_entity);
   }

   @Override
   public List<TypeDto> findByCriteria (final String p_sqlCriteria,
            final Map<String, ? extends Object> p_map_value_by_name, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      final List<TypeEntity> v_tab_entity = getDao().findByCriteria(p_sqlCriteria, p_map_value_by_name, p_nbLignesMax,
               p_nbLignesStart);
      return convertListEntityToListDto(v_tab_entity);
   }

   @Override
   public List<TypeDto> findByCriteria (final TableCriteria<Columns_Enum> p_tableCriteria)
   {
      final List<TypeEntity> v_tab_entity = getDao().findByCriteria(p_tableCriteria);
      return convertListEntityToListDto(v_tab_entity);
   }

   @Override
   public List<TypeDto> convertListEntityToListDto (final List<TypeEntity> p_tab_entity)
   {
      final List<TypeDto> v_tab_dto = new ArrayList<>(p_tab_entity.size());
      for (final TypeEntity v_entity : p_tab_entity)
      {
         final TypeDto v_dto = convertEntityToDto(v_entity);
         v_tab_dto.add(v_dto);
      }
      return v_tab_dto;
   }

   /**
    * Convertit un DTO en Entité.
    * @param p_dto
    *           (In)(*) Le DTO à convertir en entité
    * @return l'entité correspondant à p_dto
    */
   protected abstract TypeEntity convertDtoToEntity (final TypeDto p_dto);

   /**
    * Convertit une entité en DTO.
    * @param p_entity
    *           (In)(*) L'entité à convertir en DTO
    * @return le DTO correspondant à p_entity
    */
   protected abstract TypeDto convertEntityToDto (final TypeEntity p_entity);

   /**
    * Rafraichit le DTO à partir de l'entité.
    * @param p_entity
    *           (In)(*) l'entité source
    * @param p_dto
    *           (In/Out)(*) le DTO qui sera rafraichit à partir de p_entity
    */
   protected abstract void refreshDtoFromEntity (final TypeEntity p_entity, final TypeDto p_dto);

   /**
    * Vérification de la conversion dans la couche de matching.
    * @param p_clazz
    *           les types convertis par la couche de matching
    */
   protected void checkAttributeTypes (final Class<?>... p_clazz)
   {
      for (final Class<?> v_clazz : p_clazz)
      {
         if (v_clazz == null)
         {
            throw new Spi4jRuntimeException("Problème de matching", "Vérifier le matching dans " + getClass().getName());
         }
      }
   }
}
