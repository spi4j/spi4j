package fr.spi4j.persistence.dbpopulate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Classe de tests unitaires pour le compteur de montée en charge : {@link Repartition1NCounter}.
 * @author MINARM
 */
public class Repartition1NCounter_Test
{

   /**
    * Test du compteur de montée en charge pour une relation 1 -> N
    */
   @Test
   public void testRepartition1NCounter ()
   {
      final int v_nbValeurs = 3000;

      final Counter_Itf<Integer> v_counter = new Repartition1NCounter(10, 30, v_nbValeurs, 100, false);

      final Map<Integer, Integer> v_repartitionStats = new HashMap<>();
      int v_nbValeursUtilisees = 0;

      int v_value = v_counter.getNextValue();
      while (v_value > 0)
      {
         v_nbValeursUtilisees += v_value;
         if (v_repartitionStats.containsKey(v_value))
         {
            v_repartitionStats.put(v_value, v_repartitionStats.get(v_value) + 1);
         }
         else
         {
            v_repartitionStats.put(v_value, 1);
         }
         v_value = v_counter.getNextValue();
      }

      assertEquals(v_nbValeurs, v_nbValeursUtilisees);
   }

   /**
    * Test du compteur de montée en charge pour une relation 1 -> N
    */
   @Test
   public void testRepartition1NCounterNullable ()
   {
      final int v_nbValeurs = 1100;

      final Counter_Itf<Integer> v_counter = new Repartition1NCounter(5, 10, v_nbValeurs, 200, true);

      final Map<Integer, Integer> v_repartitionStats = new HashMap<>();
      int v_nbValeursUtilisees = 0;

      int v_value = v_counter.getNextValue();
      while (v_value > 0)
      {
         v_nbValeursUtilisees += v_value;
         if (v_repartitionStats.containsKey(v_value))
         {
            v_repartitionStats.put(v_value, v_repartitionStats.get(v_value) + 1);
         }
         else
         {
            v_repartitionStats.put(v_value, 1);
         }
         v_value = v_counter.getNextValue();
      }

      assertTrue(v_nbValeurs <= v_nbValeursUtilisees);
   }

}
