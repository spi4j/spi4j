/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.entity.fetching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.testapp.MyPersonneColumns_Enum;
import fr.spi4j.testapp.MyPersonneEntity;
import fr.spi4j.testapp.MyPersonneEntityFetchingStrategy;

/**
 * Test unitaire de la classe FetchingStrategyFetcher.
 * @author MINARM
 */
public class FetchingStrategyFetcher_Test
{
   private List<MyPersonneEntity> _personnes;

   /**
    * Initialisation.
    */
   @BeforeEach
   public void setUp ()
   {
      _personnes = new ArrayList<>();
      final MyPersonneEntity v_personne = new MyPersonneEntity();
      v_personne.setNom("Dupond");
      _personnes.add(v_personne);
      _personnes.add(v_personne);
   }

   /**
    * noChildren.
    */
   @Test
   public void noChildren ()
   {
      final MyPersonneEntityFetchingStrategy v_myPersonneFetchingStrategy = new MyPersonneEntityFetchingStrategy();
      FetchingStrategyFetcher.applyFetchingStrategy(v_myPersonneFetchingStrategy, _personnes);
   }

   /**
    * noEntity.
    */
   @Test
   public void noEntity ()
   {
      final MyPersonneEntityFetchingStrategy v_myPersonneFetchingStrategy = new MyPersonneEntityFetchingStrategy()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public List<FetchingStrategy_Abs<Long, ? extends Entity_Itf<Long>>> getChildren ()
         {
            final List<FetchingStrategy_Abs<Long, ? extends Entity_Itf<Long>>> v_list = new ArrayList<>();
            v_list.add(new MyPersonneEntityFetchingStrategy());
            return v_list;
         }
      };
      final List<MyPersonneEntity> v_personnes = Collections.emptyList();
      FetchingStrategyFetcher.applyFetchingStrategy(v_myPersonneFetchingStrategy, v_personnes);
   }

   /**
    * notFetchedChild.
    */
   @Test
   public void notFetchedChild ()
   {
      final MyPersonneEntityFetchingStrategy v_myPersonneFetchingStrategy = new MyPersonneEntityFetchingStrategy()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public List<FetchingStrategy_Abs<Long, ? extends Entity_Itf<Long>>> getChildren ()
         {
            final List<FetchingStrategy_Abs<Long, ? extends Entity_Itf<Long>>> v_list = new ArrayList<>();
            final MyPersonneEntityFetchingStrategy v_child = new MyPersonneEntityFetchingStrategy();
            v_child.setFetch(false);
            v_list.add(v_child);
            return v_list;
         }
      };
      FetchingStrategyFetcher.applyFetchingStrategy(v_myPersonneFetchingStrategy, _personnes);
   }

   /**
    * fetchedChildren.
    */

   // @Test
   public void fetchedChildren ()
   {
      final MyPersonneEntityFetchingStrategy v_fsNeuveux = new MyPersonneEntityFetchingStrategy()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public MyPersonneColumns_Enum getAttribute ()
         {
            return MyPersonneColumns_Enum.nom;
         }
      };

      final MyPersonneEntityFetchingStrategy v_fsAncetre = new MyPersonneEntityFetchingStrategy()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public MyPersonneColumns_Enum getAttribute ()
         {
            return MyPersonneColumns_Enum.nom;
         }

         // Test d'une fetching strategy avec une relation fetchée vers une liste de Entitys

         @Override
         public List<FetchingStrategy_Abs<Long, ? extends Entity_Itf<Long>>> getChildren ()
         {
            final List<FetchingStrategy_Abs<Long, ? extends Entity_Itf<Long>>> v_neuveuxList = new ArrayList<>();
            v_neuveuxList.add(v_fsNeuveux);
            return v_neuveuxList;
         }
      };

      v_fsNeuveux.setFetch(true);
      v_fsAncetre.setFetch(true);

      final MyPersonneEntityFetchingStrategy v_myPersonneFetchingStrategy = new MyPersonneEntityFetchingStrategy()
      {
         private static final long serialVersionUID = 1L;

         // Test d'une fetching strategy avec une relation fetchée vers une Entity non null

         @Override
         public List<FetchingStrategy_Abs<Long, ? extends Entity_Itf<Long>>> getChildren ()
         {
            final List<FetchingStrategy_Abs<Long, ? extends Entity_Itf<Long>>> v_ancetreList = new ArrayList<>();
            v_ancetreList.add(v_fsAncetre);
            return v_ancetreList;
         }
      };
      FetchingStrategyFetcher.applyFetchingStrategy(v_myPersonneFetchingStrategy, _personnes);
   }

}
