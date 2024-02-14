/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.matching;

import java.lang.reflect.Field;

import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.jdbc.JdbcVersionHelper;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Classe de méthodes utilitaires pour la couche de matching.
 * @author MINARM
 */
public final class MatchHelper
{

   /** Le nom du champ dans le DTO qui contient la valeur de la version. */
   public static final String c_dtoVersionValueFieldName = "_versionValue";

   /**
    * Constructeur privé.
    */
   private MatchHelper ()
   {
      super();
   }

   /**
    * Positionne la version dans le DTO à partir de sa valeur dans l'entité.
    * @param p_entity
    *           l'entité
    * @param p_dto
    *           le DTO
    */
   public static void setVersionValueInDto (final Entity_Itf<?> p_entity, final Dto_Itf<?> p_dto)
   {
      // récupération du champ dans le DTO
      final Field v_dtoVersionField = getVersionFieldInDto(p_dto);

      // récupération du champ dans l'Entity
      final Field v_entityVersionField = getVersionFieldInJdbcEntity(p_entity);

      try
      {
         v_dtoVersionField.set(p_dto, v_entityVersionField.get(p_entity));
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Impossible de positionner la version dans le DTO",
                  "Vérifier le code généré");
      }
   }

   /**
    * Positionne la version dans l'entité à partir de sa valeur dans le DTO.
    * @param p_dto
    *           le DTO
    * @param p_entity
    *           l'entité
    */
   public static void setVersionValueInEntity (final Dto_Itf<?> p_dto, final Entity_Itf<?> p_entity)
   {
      // récupération du champ dans le DTO
      final Field v_dtoVersionField = getVersionFieldInDto(p_dto);

      // récupération du champ dans l'Entity
      final Field v_entityVersionField = getVersionFieldInJdbcEntity(p_entity);

      try
      {
         v_entityVersionField.set(p_entity, v_dtoVersionField.get(p_dto));
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Impossible de positionner la version dans l'entité",
                  "Vérifier le code généré");
      }
   }

   /**
    * Récupère le champ qui contient la version dans le DTO.
    * @param p_dto
    *           le DTO
    * @return le champ qui contient la version dans le DTO
    */
   private static Field getVersionFieldInDto (final Dto_Itf<?> p_dto)
   {
      try
      {
         final Field v_versionField = p_dto.getClass().getDeclaredField(c_dtoVersionValueFieldName);
         v_versionField.setAccessible(true);
         return v_versionField;
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Impossible de récupérer le champ de gestion de version dans le dto",
                  "Vérifier le code généré");
      }
   }

   /**
    * Récupère le champ qui contient la version dans l'entité JDBC.
    * @param p_entity
    *           l'entité
    * @return le champ qui contient la version dans l'entité
    */
   private static Field getVersionFieldInJdbcEntity (final Entity_Itf<?> p_entity)
   {
      // on suppose une implémentation JDBC
      return JdbcVersionHelper.getVersionField(p_entity);
   }

}
