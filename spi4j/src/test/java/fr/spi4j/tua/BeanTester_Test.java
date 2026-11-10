/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.tua;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Classe de test du {@link BeanTester_Abs}.
 * @author MINARM
 */
public class BeanTester_Test
{

   /**
    * Test le random.
    */
   @Test
   public void testRandom ()
   {
      final BeanTester_Abs v_beanTester = new BeanTester_Abs()
      {
         // pas de méthodes abstraites
      };
      assertNotNull(v_beanTester.getRandomBoolean(), "Génération nulle");
      assertNotNull(v_beanTester.getRandomDate(), "Génération nulle");
      assertNotNull(v_beanTester.getRandomDouble(), "Génération nulle");
      assertNotNull(v_beanTester.getRandomFloat(), "Génération nulle");
      assertNotNull(v_beanTester.getRandomInteger(), "Génération nulle");
      assertNotNull(v_beanTester.getRandomInteger(3), "Génération nulle");
      assertNotNull(v_beanTester.getRandomLong(), "Génération nulle");
      assertNotNull(v_beanTester.getRandomLong(15), "Génération nulle");
      assertNotNull(v_beanTester.getRandomString(), "Génération nulle");
      assertNotNull(v_beanTester.getRandomString(5), "Génération nulle");
      assertNotNull(v_beanTester.getRandomBinary(), "Génération nulle");
      assertNotNull(v_beanTester.getRandomTimestamp(), "Génération nulle");
      assertNotNull(v_beanTester.getRandomTime(), "Génération nulle");
      assertTrue(v_beanTester.getRandomString(5).length() <= 5, "Génération de chaine trop grande");
   }

}
