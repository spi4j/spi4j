/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.fetching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.testapp.MyPersonneAttributes_Enum;
import fr.spi4j.testapp.MyPersonneDto;
import fr.spi4j.testapp.MyPersonneFetchingStrategy;

/**
 * Test unitaire de la classe FetchingStrategyFetcher.
 * @author MINARM
 */
public class FetchingStrategyFetcher_Test
{
   private List<MyPersonneDto> _personnes;

   /**
    * Initialisation.
    */
   @BeforeEach
   public void setUp ()
   {
      _personnes = new ArrayList<>();
      final MyPersonneDto v_personne = new MyPersonneDto();
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
      final MyPersonneFetchingStrategy v_myPersonneFetchingStrategy = new MyPersonneFetchingStrategy();
      FetchingStrategyFetcher.applyFetchingStrategy(v_myPersonneFetchingStrategy, _personnes);
   }

   /**
    * noDto.
    */
   @Test
   public void noDto ()
   {
      final MyPersonneFetchingStrategy v_myPersonneFetchingStrategy = new MyPersonneFetchingStrategy()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public List<FetchingStrategy_Abs<Long, ? extends Dto_Itf<Long>>> getChildren ()
         {
            final List<FetchingStrategy_Abs<Long, ? extends Dto_Itf<Long>>> v_list = new ArrayList<>();
            v_list.add(new MyPersonneFetchingStrategy());
            return v_list;
         }
      };
      final List<MyPersonneDto> v_personnes = Collections.emptyList();
      FetchingStrategyFetcher.applyFetchingStrategy(v_myPersonneFetchingStrategy, v_personnes);
   }

   /**
    * notFetchedChild.
    */
   @Test
   public void notFetchedChild ()
   {
      final MyPersonneFetchingStrategy v_myPersonneFetchingStrategy = new MyPersonneFetchingStrategy()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public List<FetchingStrategy_Abs<Long, ? extends Dto_Itf<Long>>> getChildren ()
         {
            final List<FetchingStrategy_Abs<Long, ? extends Dto_Itf<Long>>> v_list = new ArrayList<>();
            final MyPersonneFetchingStrategy v_child = new MyPersonneFetchingStrategy();
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
   @Test
   public void fetchedChildren ()
   {
      final MyPersonneFetchingStrategy v_fsNeuveux = new MyPersonneFetchingStrategy()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public MyPersonneAttributes_Enum getAttribute ()
         {
            return MyPersonneAttributes_Enum.neuveux;
         }
      };

      final MyPersonneFetchingStrategy v_fsAncetre = new MyPersonneFetchingStrategy()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public MyPersonneAttributes_Enum getAttribute ()
         {
            return MyPersonneAttributes_Enum.ancetre;
         }

         // Test d'une fetching strategy avec une relation fetchée vers une liste de DTOs
         @Override
         public List<FetchingStrategy_Abs<Long, ? extends Dto_Itf<Long>>> getChildren ()
         {
            final List<FetchingStrategy_Abs<Long, ? extends Dto_Itf<Long>>> v_neuveuxList = new ArrayList<>();
            v_neuveuxList.add(v_fsNeuveux);
            return v_neuveuxList;
         }
      };

      v_fsNeuveux.setFetch(true);
      v_fsAncetre.setFetch(true);

      final MyPersonneFetchingStrategy v_myPersonneFetchingStrategy = new MyPersonneFetchingStrategy()
      {
         private static final long serialVersionUID = 1L;

         // Test d'une fetching strategy avec une relation fetchée vers un DTO non null
         @Override
         public List<FetchingStrategy_Abs<Long, ? extends Dto_Itf<Long>>> getChildren ()
         {
            final List<FetchingStrategy_Abs<Long, ? extends Dto_Itf<Long>>> v_ancetreList = new ArrayList<>();
            v_ancetreList.add(v_fsAncetre);
            return v_ancetreList;
         }
      };
      FetchingStrategyFetcher.applyFetchingStrategy(v_myPersonneFetchingStrategy, _personnes);
   }
}
