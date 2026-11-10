/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import fr.spi4j.persistence.dao.DaoJpa_Abs;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.testapp.MyPersonneColumns_Enum;
import fr.spi4j.testapp.MyPersonneEntity;

/**
 * Classe de test bateau de {@link DaoJpa_Abs}
 * @author MINARM
 */
public class DaoJpa_Test
{
   /**
    * Dao.
    * @author MINARM
    */
   private static class MyPersonneDaoJpa extends DaoJpa_Abs<Long, MyPersonneEntity, MyPersonneColumns_Enum>
   {
      /**
       * Constructeur.
       */
      public MyPersonneDaoJpa ()
      {
         super(MyPersonneColumns_Enum.c_tableName, MyPersonneEntity.class);
      }

      @Override
      public ColumnsNames_Itf getColumnId ()
      {
         return MyPersonneColumns_Enum.Personne_id;
      }
   }

   /**
    * test.
    */
   @Test
   public void test ()
   {
      final MyPersonneDaoJpa v_dao = new MyPersonneDaoJpa();
      assertEquals(MyPersonneColumns_Enum.c_tableName, v_dao.getTableName());
   }
}
