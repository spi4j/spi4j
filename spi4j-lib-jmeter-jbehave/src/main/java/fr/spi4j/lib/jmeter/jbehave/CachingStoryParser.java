/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbehave.core.model.Story;
import org.jbehave.core.parsers.StoryParser;

/**
 * Parseur de story vérifiant que la story n'est pas déjà en cache.
 * @author MINARM
 */
public class CachingStoryParser implements StoryParser
{

   private final StoryParser _delegate;

   private ConcurrentMap<String, Story> _cache = null;

   private static final Logger c_log = LogManager.getLogger(CachingStoryParser.class);

   /**
    * Constructeur.
    * @param p_storyParser
    *           le parseur de story
    */
   public CachingStoryParser (final StoryParser p_storyParser)
   {
      this._delegate = p_storyParser;
      reset();
   }

   @Override
   public Story parseStory (final String p_storyAsText)
   {
      Story v_story = _cache.get(p_storyAsText);
      if (v_story == null)
      {
         c_log.debug("Mise en cache de la story");
         v_story = _delegate.parseStory(p_storyAsText);
         _cache.put(p_storyAsText, v_story);
      }
      return v_story;
   }

   @Override
   public Story parseStory (final String p_storyAsText, final String p_storyPath)
   {
      Story v_story = _cache.get(p_storyAsText);
      if (v_story == null)
      {
         c_log.debug("Mise en cache de la story : " + p_storyPath);
         v_story = _delegate.parseStory(p_storyAsText, p_storyPath);
         _cache.put(p_storyAsText, v_story);
      }
      return v_story;
   }

   /**
    * Initialise le cache.
    */
   public void reset ()
   {
      _cache = new ConcurrentHashMap<String, Story>();
   }

}
