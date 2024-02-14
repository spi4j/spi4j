/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.testapp.MyParamPersistence;

/**
 * Test de l'implémentation DAO JDBC non reliée à une entité.
 * @author MINARM
 */
public class DefaultJdbcDao_Test
{

   private static Dao_Itf<?, ?, ?> dao = MyParamPersistence.getUserPersistence().getDefaultDao();

   /**
    * Test de la méthode get_tableName.
    */

   @Test
   public void get_tableName ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.getTableName();
      });
   }

   /**
    * Test de la méthode create.
    */
   @Test
   public void create ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.create(null);
      });
   }

   /**
    * Test de la méthode findById.
    */
   @Test
   public void findById ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.findById(null);
      });
   }

   /**
    * Test de la méthode findAll.
    */
   @Test
   public void findAll1 ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.findAll();
      });
   }

   /**
    * Test de la méthode findAll.
    */
   @Test
   public void findAll2 ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.findAll(null);
      });
   }

   /**
    * Test de la méthode update.
    */
   @Test
   public void update ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.update(null);
      });
   }

   /**
    * Test de la méthode delete.
    */
   @Test
   public void delete ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.delete(null);
      });
   }

   /**
    * Test de la méthode delete.
    */
   @Test
   public void deleteByCriteria ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.deleteByCriteria(null);
      });
   }

   /**
    * Test de la méthode deleteAll.
    */
   @Test
   public void deleteAll ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.deleteAll();
      });
   }

   /**
    * Test de la méthode findByColumn.
    */
   @Test
   public void findByColumn ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.findByColumn(null, null);
      });
   }

   /**
    * Test de la méthode findByCriteria.
    */
   @Test
   public void findByCriteria1 ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.findByCriteria(null, null);
      });
   }

   /**
    * Test de la méthode findByCriteria.
    */
   @Test
   public void findByCriteria2 ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.findByCriteria(null, null, 0, 0);
      });
   }

   /**
    * Test de la méthode findByCriteria.
    */
   @Test
   public void findByCriteria3 ()
   {
      assertThrows(UnsupportedOperationException.class, () -> {
         dao.findByCriteria(null);
      });
   }
}
