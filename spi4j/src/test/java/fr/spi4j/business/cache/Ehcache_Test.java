/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test unitaire de la classe Ehcache.
 * @author MINARM
 */
public class Ehcache_Test
{
   /**
    * Test.
    */
   @Test
   public void test ()
   {
      final EhCache<String, String> v_ehcache = new EhCache<>("nom du cache");
      // taille du cache au départ
      assertEquals(0, v_ehcache.size());
      // put
      v_ehcache.put("key", "value");
      assertEquals(1, v_ehcache.size());
      // get
      assertEquals("value", v_ehcache.get("key"));
      // remove
      v_ehcache.remove("key");
      assertEquals(0, v_ehcache.size());
      // clear
      v_ehcache.put("key", "value");
      v_ehcache.clear();
      assertEquals(0, v_ehcache.size());
   }
}
