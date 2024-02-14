/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test unitaire de la classe CacheSoft.
 * @author MINARM
 */
public class CacheSoft_Test
{
   /**
    * Test.
    */
   @Test
   public void test ()
   {
      final CacheSoft<String, String> v_cacheSoft = new CacheSoft<>();
      // taille du cache au départ
      assertEquals(0, v_cacheSoft.size());
      // put
      v_cacheSoft.put("key", "value");
      assertEquals(1, v_cacheSoft.size());
      // get
      assertEquals("value", v_cacheSoft.get("key"));
      // remove
      v_cacheSoft.remove("key");
      assertEquals(0, v_cacheSoft.size());
      // clear
      v_cacheSoft.put("key", "value");
      v_cacheSoft.clear();
      assertEquals(0, v_cacheSoft.size());
   }
}
