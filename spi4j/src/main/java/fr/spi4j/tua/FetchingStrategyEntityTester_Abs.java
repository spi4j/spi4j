/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.tua;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.spi4j.entity.fetching.FetchingStrategy_Abs;

/**
 * Classe utilitaire à hériter par les tests unitaires de fetching strategies.
 * @author MINARM
 */
public abstract class FetchingStrategyEntityTester_Abs
{
   /**
    * assertFetched.
    * @param p_fs
    *           FetchingStrategy_Abs
    */
   protected void assertFetched (final FetchingStrategy_Abs<?, ?> p_fs)
   {
      assertTrue(p_fs.isFetch(), p_fs.path() + " should be fetched");
   }

   /**
    * assertNotFetched.
    * @param p_fs
    *           FetchingStrategy_Abs
    */
   protected void assertNotFetched (final FetchingStrategy_Abs<?, ?> p_fs)
   {
      assertFalse(p_fs.isFetch(), p_fs.path() + " should not be fetched");
   }
}
