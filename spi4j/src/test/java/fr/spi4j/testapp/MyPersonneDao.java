/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.spi4j.persistence.dao.jdbc.DaoJdbc_Abs;

/**
 * Implémentation JDBC du DAO Personne.
 * @author MINARM
 */
public class MyPersonneDao extends DaoJdbc_Abs<MyPersonneEntity_Itf, MyPersonneColumns_Enum> implements
         MyPersonneDao_Itf
{
   /**
    * Constructeur par défaut.
    */
   public MyPersonneDao ()
   {
      super(MyPersonneColumns_Enum.c_tableName, MyPersonneColumns_Enum.values());
   }

   @Override
   protected Map<String, Object> getMapValueByLogicalNameFromEntity (final MyPersonneEntity_Itf p_Entity)
   {
      final Map<String, Object> v_map_valueByColumnName = new LinkedHashMap<>();
      v_map_valueByColumnName.put(MyPersonneColumns_Enum.Personne_id.getLogicalColumnName(), p_Entity.getId());
      v_map_valueByColumnName.put(MyPersonneColumns_Enum.nom.getLogicalColumnName(), p_Entity.getNom());
      v_map_valueByColumnName.put(MyPersonneColumns_Enum.prenom.getLogicalColumnName(), p_Entity.getPrenom());
      v_map_valueByColumnName.put(MyPersonneColumns_Enum.civil.getLogicalColumnName(), p_Entity.getCivil());
      v_map_valueByColumnName.put(MyPersonneColumns_Enum.dateNaissance.getLogicalColumnName(),
               p_Entity.getDateNaissance());
      v_map_valueByColumnName.put(MyPersonneColumns_Enum.salaire.getLogicalColumnName(), p_Entity.getSalaire());
      v_map_valueByColumnName.put(MyPersonneColumns_Enum.version.getLogicalColumnName(), p_Entity.getVersion());
      v_map_valueByColumnName.put(MyPersonneColumns_Enum.grade_id.getLogicalColumnName(), p_Entity.getGradeId());
      return v_map_valueByColumnName;
   }

   @Override
   protected MyPersonneEntity_Itf getEntityFromMapValueByLogicalName (final Map<String, Object> p_map_valueByColumnName)
   {
      final MyPersonneEntity_Itf v_entity = MyParamPersistence.getUserPersistence().getInstanceOfPersonneEntity();
      v_entity.setId((Long) p_map_valueByColumnName.get(MyPersonneColumns_Enum.Personne_id.getLogicalColumnName()));
      v_entity.setNom((String) p_map_valueByColumnName.get(MyPersonneColumns_Enum.nom.getLogicalColumnName()));
      v_entity.setPrenom((String) p_map_valueByColumnName.get(MyPersonneColumns_Enum.prenom.getLogicalColumnName()));
      v_entity.setCivil((Boolean) p_map_valueByColumnName.get(MyPersonneColumns_Enum.civil.getLogicalColumnName()));
      v_entity.setDateNaissance((Date) p_map_valueByColumnName.get(MyPersonneColumns_Enum.dateNaissance
               .getLogicalColumnName()));
      v_entity.setSalaire((Double) p_map_valueByColumnName.get(MyPersonneColumns_Enum.salaire.getLogicalColumnName()));
      v_entity.setVersion((Date) p_map_valueByColumnName.get(MyPersonneColumns_Enum.version.getLogicalColumnName()));
      v_entity.setGradeId((Long) p_map_valueByColumnName.get(MyPersonneColumns_Enum.grade_id.getLogicalColumnName()));
      return v_entity;
   }
}
