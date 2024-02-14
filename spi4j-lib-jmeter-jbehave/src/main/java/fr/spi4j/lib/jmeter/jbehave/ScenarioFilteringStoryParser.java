/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.jbehave.core.model.Description;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Narrative;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.parsers.StoryParser;

/**
 * Filtre de scénario par story.
 * @author MINARM
 */
public class ScenarioFilteringStoryParser implements StoryParser
{

   private final StoryParser _delegate;

   private final String _title;

   /**
    * Constructeur.
    * @param p_delegate
    *           le parser
    * @param p_title
    *           le titre du scénario
    */
   public ScenarioFilteringStoryParser (final StoryParser p_delegate, final String p_title)
   {
      super();
      this._delegate = p_delegate;
      this._title = p_title;
   }

   @Override
   public Story parseStory (final String p_storyAsText)
   {
      final Story v_story = _delegate.parseStory(p_storyAsText);
      return filter(v_story);
   }

   @Override
   public Story parseStory (final String p_storyAsText, final String p_storyPath)
   {
      final Story v_story = _delegate.parseStory(p_storyAsText, p_storyPath);
      return filter(v_story);
   }

   /**
    * Filtre sur une story.
    * @param p_story
    *           la story
    * @return la story filtrée
    */
   protected Story filter (final Story p_story)
   {
      return new FilteredStory(p_story);
   }

   /**
    * Story filtrée.
    * @author MINARM
    */
   final class FilteredStory extends Story
   {

      private final Story _delegateStory;

      /**
       * Constructeur.
       * @param p_delegateStory
       *           la story initiale
       */
      FilteredStory (final Story p_delegateStory)
      {
         super();
         this._delegateStory = p_delegateStory;
      }

      @Override
      public Description getDescription ()
      {
         return _delegateStory.getDescription();
      }

      @Override
      public Meta getMeta ()
      {
         return _delegateStory.getMeta();
      }

      @Override
      public Narrative getNarrative ()
      {
         return _delegateStory.getNarrative();
      }

      @Override
      public int hashCode ()
      {
         return _delegateStory.hashCode();
      }

      @Override
      public String getName ()
      {
         return _delegateStory.getName();
      }

      @Override
      public void namedAs (final String p_name)
      {
         _delegateStory.namedAs(p_name);
      }

      @Override
      public String getPath ()
      {
         return _delegateStory.getPath();
      }

      @Override
      public String toString ()
      {
         return _delegateStory.toString();
      }

      @Override
      public boolean equals (final Object p_obj)
      {
         return _delegateStory.equals(p_obj);
      }

      @Override
      public List<Scenario> getScenarios ()
      {
         final List<Scenario> v_scenarios = _delegateStory.getScenarios();
         Scenario v_match = null;
         if (_title != null)
         {
            for (final Iterator<Scenario> v_i = v_scenarios.iterator(); v_i.hasNext() && v_match == null;)
            {
               final Scenario v_scenario = v_i.next();
               if (v_scenario.getTitle() != null && _title.trim().equals(v_scenario.getTitle().trim()))
               {
                  v_match = v_scenario;
               }
            }
         }
         List<Scenario> v_result;
         if (v_match != null)
         {
            v_result = Collections.singletonList(v_match);
         }
         else
         {
            LogManager.getLogger(getClass()).warn("Aucun scénario nommé \"" + _title + "\"");
            v_result = new ArrayList<Scenario>();
         }
         return v_result;
      }
   }

}
