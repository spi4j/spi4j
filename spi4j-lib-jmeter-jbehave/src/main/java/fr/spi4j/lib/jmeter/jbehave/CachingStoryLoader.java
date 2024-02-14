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
import org.jbehave.core.io.StoryLoader;

/**
 * Chargeur de story vérifiant que la story n'est pas déjà en cache.
 * 
 * @author MINARM
 */
public class CachingStoryLoader implements StoryLoader {

	private final StoryLoader _delegate;

	private ConcurrentMap<String, String> _cache = null;

	private static final Logger c_log = LogManager.getLogger(CachingStoryLoader.class);

	/**
	 * Constructeur.
	 * 
	 * @param p_storyLoader le chargeur de story
	 */
	public CachingStoryLoader(final StoryLoader p_storyLoader) {
		this._delegate = p_storyLoader;
		reset();
	}

	@Override
	public String loadStoryAsText(final String p_storyPath) {
		String v_story = _cache.get(p_storyPath);
		if (v_story == null) {
			c_log.debug("Mise en cache du contenu de la story : " + p_storyPath);
			v_story = this._delegate.loadStoryAsText(p_storyPath);
			_cache.putIfAbsent(p_storyPath, v_story);
			c_log.debug(v_story);
		}
		return v_story;
	}

	/**
	 * Initialise le cache.
	 */
	public void reset() {
		_cache = new ConcurrentHashMap<String, String>();
	}

	// Ajout suite a montee de versiion JBehave en 5.0.
	@Override
	public String loadResourceAsText(String p_resourcePath) {
		return null;
	}
}
