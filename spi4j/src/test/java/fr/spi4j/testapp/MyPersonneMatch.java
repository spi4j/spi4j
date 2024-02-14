/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import fr.spi4j.matching.MatchHelper;
import fr.spi4j.matching.Match_Abs;

/**
 * Implémentation pour le Matcher (= persistance <-> business) sur le type 'Personne'.
 */
public class MyPersonneMatch extends Match_Abs<Long, MyPersonneDto, MyPersonneEntity_Itf, MyPersonneColumns_Enum>
         implements MyPersonneMatch_Itf
{
   /**
    * Obtenir le DAO associé au type 'Personne'.
    * @return L'instance désirée.
    */
   @Override
   protected MyPersonneDao_Itf getDao ()
   {
      final MyUserPersistence v_userPersistence = MyParamPersistence.getUserPersistence();
      return v_userPersistence.getInstanceOfPersonneDao();
   }

   @Override
   protected MyPersonneEntity_Itf convertDtoToEntity (final MyPersonneDto p_dto)
   {
      final MyUserPersistence v_userPersistence = MyParamPersistence.getUserPersistence();
      final MyPersonneEntity_Itf v_entity = v_userPersistence.getInstanceOfPersonneEntity();
      v_entity.setId(p_dto.getId());
      v_entity.setNom(p_dto.getNom());
      v_entity.setPrenom(p_dto.getPrenom());
      v_entity.setCivil(p_dto.getCivil());
      v_entity.setDateNaissance(p_dto.getDateNaissance());
      v_entity.setSalaire(p_dto.getSalaire());
      v_entity.setGradeId(p_dto.getGradeId());

      MatchHelper.setVersionValueInEntity(p_dto, v_entity);

      return v_entity;
   }

   @Override
   protected MyPersonneDto convertEntityToDto (final MyPersonneEntity_Itf p_entity)
   {
      final MyPersonneDto v_dto = new MyPersonneDto();
      refreshDtoFromEntity(p_entity, v_dto);
      return v_dto;
   }

   @Override
   protected void refreshDtoFromEntity (final MyPersonneEntity_Itf p_entity, final MyPersonneDto p_dto)
   {
      p_dto.setId(p_entity.getId());
      p_dto.setNom(p_entity.getNom());
      p_dto.setPrenom(p_entity.getPrenom());
      p_dto.setCivil(p_entity.getCivil());
      p_dto.setDateNaissance(p_entity.getDateNaissance());
      p_dto.setSalaire(p_entity.getSalaire());
      p_dto.setGradeId(p_entity.getGradeId());

      MatchHelper.setVersionValueInDto(p_entity, p_dto);
   }
}
