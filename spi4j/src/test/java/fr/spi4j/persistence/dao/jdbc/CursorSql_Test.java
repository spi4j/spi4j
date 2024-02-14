/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.Cursor_Abs;
import fr.spi4j.testapp.MyParamPersistence;
import fr.spi4j.testapp.MyPersonneDao_Itf;
import fr.spi4j.testapp.test.DatabaseInitialization;

/**
 * Test unitaire de la classe CursorSql.
 * @author MINARM
 */
public class CursorSql_Test
{
   private static final Logger c_log = LogManager.getLogger(CursorSql_Test.class);

   private MyPersonneDao_Itf _dao;

   /**
    * Méthode d'initialisation de la classe de tests.
    */
   @BeforeAll
   public static void setUpBeforeClass ()
   {
      DatabaseInitialization.initDatabase();
   }

   /**
    * Méthode d'initialisation de chaque test.
    */
   @BeforeEach
   public void setUp ()
   {
      MyParamPersistence.getUserPersistence().begin();
      _dao = MyParamPersistence.getUserPersistence().getInstanceOfPersonneDao();
   }

   /**
    * Méthode de fin de test : rollback.
    */
   @AfterEach
   public void tearDown ()
   {
      // MyParamPersistence.getUserPersistence().rollback();
   }

   /**
    * Test.
    */
   @Test
   public void testGetQueryAndColumnNames ()
   {
      final String v_query = "select 1 as VALUE";
      final Cursor_Abs v_cursor = _dao.executeQuery(v_query, null);
      try
      {
         assertEquals(v_query, v_cursor.getQuery());
         assertEquals(Arrays.asList("VALUE"), v_cursor.getColumnNames());
         assertEquals(Arrays.asList("VALUE"), v_cursor.getColumnNames());
         assertFalse(v_cursor.isClosed());
      }
      finally
      {
         v_cursor.close();
         assertTrue(v_cursor.isClosed());
      }
   }

   /**
    * Test.
    */
   @Test
   public void testPagination ()
   {
      final String v_query = "select 1 as VALUE";
      // remarque: avec la base H2 de test, ces paginations sont optimisées par DaoJdbc_Abs en utilisant limit et offset
      // donc il est normal que ces tests ne couvre pas le fonctionnement de la pagination dans CursorSql qui n'est lui que le mode dégradé
      final Cursor_Abs v_cursor = _dao.executeQuery(v_query, null, -1, 0);
      try
      {
         assertTrue(v_cursor.next());
         assertFalse(v_cursor.next());
      }
      finally
      {
         v_cursor.close();
      }

      final Cursor_Abs v_cursor2 = _dao.executeQuery(v_query, null, -1, 1);
      try
      {
         assertFalse(v_cursor2.next());
      }
      finally
      {
         v_cursor2.close();
      }

      final Cursor_Abs v_cursor3 = _dao.executeQuery(v_query, null, -1, 2);
      try
      {
         assertFalse(v_cursor3.next());
      }
      finally
      {
         v_cursor3.close();
      }

      final Cursor_Abs v_cursor4 = _dao.executeQuery(v_query, null, 0, 0);
      try
      {
         assertFalse(v_cursor4.next());
      }
      finally
      {
         v_cursor4.close();
      }

      final Cursor_Abs v_cursor5 = _dao.executeQuery(v_query, null, 1, 0);
      try
      {
         assertTrue(v_cursor5.next());
      }
      finally
      {
         v_cursor5.close();
      }
   }

   /**
    * Test.
    */
   @Test
   public void testGetXxx ()
   {
      final Cursor_Abs v_cursor = _dao.executeQuery(
               "select 1 as VALUE, null as VALUE2, current_date() as date, current_timestamp() as timestamp", null);
      v_cursor.next();
      try
      {
         v_cursor.getLong("value");
         v_cursor.getInteger("value");
         v_cursor.getDouble("value");
         v_cursor.getFloat("value");
         v_cursor.getBigDecimal("value");
         v_cursor.getByte("value");
         v_cursor.getBoolean("value");
         v_cursor.getString("value");

         v_cursor.getDate("date");
         v_cursor.getTimestamp("timestamp");
         v_cursor.getTime("timestamp");

         v_cursor.getLong("value2");
         v_cursor.getInteger("value2");
         v_cursor.getDouble("value2");
         v_cursor.getFloat("value2");
         v_cursor.getBigDecimal("value2");
         v_cursor.getByte("value2");
         v_cursor.getBoolean("value2");
         v_cursor.getString("value2");

         v_cursor.getDate("value2");
         v_cursor.getTimestamp("value2");
         v_cursor.getTime("value2");
         v_cursor.getBlob("value2");
      }
      finally
      {
         v_cursor.close();
      }
   }

   /**
    * Test.
    */
   @Test
   public void testGetXxxException1 ()
   {
      final Cursor_Abs v_cursor = _dao.executeQuery("select 1", null);
      v_cursor.next();
      try
      {
         try
         {
            v_cursor.getLong("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getInteger("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getDouble("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getFloat("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getBigDecimal("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getByte("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getBoolean("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
      }
      finally
      {
         v_cursor.close();
      }
   }

   /**
    * Test.
    */
   @SuppressWarnings("deprecation")
   @Test
   public void testGetXxxLobException2 ()
   {
      final Cursor_Abs v_cursor = _dao.executeQuery("select 1", null);
      v_cursor.next();
      try
      {
         try
         {
            v_cursor.getString("unknown");

         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getDate("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getTimestamp("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getTime("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getBlob("unknown");
         }
         catch (final Spi4jRuntimeException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
         try
         {
            v_cursor.getClob("unknown");
         }
         catch (final UnsupportedOperationException v_ex)
         {
            // ok
            c_log.trace(v_ex.toString());
         }
      }
      finally
      {
         v_cursor.close();
      }
   }
}
