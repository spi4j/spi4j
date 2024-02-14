/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.entity.fetching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import fr.spi4j.testapp.MyPersonneColumns_Enum;
import fr.spi4j.testapp.MyPersonneEntity_Itf;
import fr.spi4j.tua.FetchingStrategyEntityTester_Abs;

/**
 * Test unitaire de la classe FetchingStrategy_Abs.
 * @author MINARM
 */
public class FetchingStrategy_Test extends FetchingStrategyEntityTester_Abs
{
   /**
    * FetchingStrategy de test.
    * @author MINARM
    */
   public static class TestFetchingStrategy extends FetchingStrategy_Abs<Long, MyPersonneEntity_Itf>
   {
      private static final long serialVersionUID = 1L;

      /**
       * Constructeur.
       */
      TestFetchingStrategy ()
      {
         super();
      }

      /**
       * Constructeur.
       * @param p_name
       *           Nom
       * @param p_parent
       *           Parent
       */
      TestFetchingStrategy (final MyPersonneColumns_Enum p_name, final FetchingStrategy_Abs<?, ?> p_parent)
      {
         super(p_name, p_parent);
      }
   }

   /**
    * oneLevelPath.
    */
   @Test
   public void oneLevelPath ()
   {
      assertEquals("TestFetchingStrategy", new TestFetchingStrategy().path());
   }

   /**
    * twoLevelPath.
    */
   @Test
   public void twoLevelPath ()
   {
      assertEquals("TestFetchingStrategy.nom",
               new TestFetchingStrategy(MyPersonneColumns_Enum.nom, new TestFetchingStrategy()).path());
   }

   /**
    * name.
    */
   @Test
   public void name ()
   {
      assertEquals(null, new TestFetchingStrategy().getAttribute());
      assertEquals("NOM", new TestFetchingStrategy(MyPersonneColumns_Enum.nom, new TestFetchingStrategy())
               .getAttribute().toString());
      assertEquals(MyPersonneColumns_Enum.nom,
               new TestFetchingStrategy(MyPersonneColumns_Enum.nom, new TestFetchingStrategy()).getAttribute());
   }

   /**
    * parent.
    */
   @Test
   public void parent ()
   {
      assertEquals(null, new TestFetchingStrategy().getParent());
      final TestFetchingStrategy v_parent = new TestFetchingStrategy();
      assertEquals(v_parent, new TestFetchingStrategy(MyPersonneColumns_Enum.nom, v_parent).getParent());
   }

   /**
    * children.
    */
   @Test
   public void children ()
   {
      assertTrue(new TestFetchingStrategy().getChildren().isEmpty());
   }

   /**
    * fetch.
    */
   @Test
   public void fetch ()
   {
      // Fetching strategy sans parent a fetch==true par défaut
      final TestFetchingStrategy v_fsWithoutParent = new TestFetchingStrategy();
      assertFetched(v_fsWithoutParent);
      v_fsWithoutParent.setFetch(true);
      assertFetched(v_fsWithoutParent);

      final TestFetchingStrategy v_fsWithParent = new TestFetchingStrategy(MyPersonneColumns_Enum.nom,
               v_fsWithoutParent);
      // Fetching strategy sans parent a fetch==false par défaut
      assertNotFetched(v_fsWithParent);
      v_fsWithParent.setFetch(true);
      assertFetched(v_fsWithParent);
   }
}
