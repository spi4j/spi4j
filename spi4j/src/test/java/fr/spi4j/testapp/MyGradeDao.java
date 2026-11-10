/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import java.util.LinkedHashMap;
import java.util.Map;

import fr.spi4j.persistence.dao.jdbc.DaoJdbc_Abs;

/**
 * Implémentation JDBC du DAO Grade.
 * @author MINARM
 */
public class MyGradeDao extends DaoJdbc_Abs<MyGradeEntity_Itf, MyGradeColumns_Enum> implements MyGradeDao_Itf
{
   /**
    * Constructeur par défaut.
    */
   public MyGradeDao ()
   {
      super(MyGradeColumns_Enum.c_tableName, MyGradeColumns_Enum.values());
   }

   @Override
   protected Map<String, Object> getMapValueByLogicalNameFromEntity (final MyGradeEntity_Itf p_Entity)
   {
      final Map<String, Object> v_map_valueByColumnName = new LinkedHashMap<>();
      v_map_valueByColumnName.put(MyGradeColumns_Enum.Grade_id.getLogicalColumnName(), p_Entity.getId());
      v_map_valueByColumnName.put(MyGradeColumns_Enum.libelle.getLogicalColumnName(), p_Entity.get_libelle());
      v_map_valueByColumnName.put(MyGradeColumns_Enum.trigramme.getLogicalColumnName(), p_Entity.get_trigramme());
      return v_map_valueByColumnName;
   }

   @Override
   protected MyGradeEntity_Itf getEntityFromMapValueByLogicalName (final Map<String, Object> p_map_valueByColumnName)
   {
      final MyGradeEntity_Itf v_entity = MyParamPersistence.getUserPersistence().getInstanceOfGradeEntity();
      v_entity.setId((Long) p_map_valueByColumnName.get(MyGradeColumns_Enum.Grade_id.getLogicalColumnName()));
      v_entity.set_libelle((String) p_map_valueByColumnName.get(MyGradeColumns_Enum.libelle.getLogicalColumnName()));
      v_entity.set_trigramme((String) p_map_valueByColumnName.get(MyGradeColumns_Enum.trigramme.getLogicalColumnName()));
      return v_entity;
   }
}
