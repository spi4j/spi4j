/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import fr.spi4j.lib.jbehave.requirement.RequirementContext;

/**
 * Gestion du contexte des storys JBehave.
 * @author MINARM
 */
public abstract class SpiStoryContextHandler {

	private static final ThreadLocal<RequirementContext> c_requirementContext = new ThreadLocal<RequirementContext>();

	private static final ThreadLocal<SpiStory_Abs> c_story = new ThreadLocal<>();

	private static SpiStory_Abs _story;

	private static RequirementContext _requirementContext;

	/**
	 * Initialise la story courante pour ce proxy. Selon que la story courante soit
	 * multithread ou non, on recherche dans le ThreadLocal.
	 * 
	 * @param p_story la story courante
	 */
	public static void start(final SpiStory_Abs p_story) {
		if (p_story.getThreadsToLaunch() > 1) {
			c_story.set(p_story);
			c_requirementContext.set(new RequirementContext());
		} else {
			_story = p_story;
			_requirementContext = new RequirementContext();
		}
	}

	/**
	 * 
	 */
	public static void start() {
		_requirementContext = new RequirementContext();
	}

	/**
	 * 
	 * @return
	 */
	public static SpiStory_Abs get_story() {
		return (null != _story) ? _story : c_story.get();
	}

	/**
	 * @return le context courant d'enregistrement des exigences.
	 */
	public static RequirementContext get_currentRequirementContext() {
		return (null != _requirementContext) ? _requirementContext : c_requirementContext.get();
	}

	/**
	 * Réinitialise le contexte courant
	 */
	public static void clearCurrentRequirementContext() {
		get_currentRequirementContext().clear();
	}

	/**
	 * 
	 */
	public static void destroy() {
		if (null != get_story())
			get_story().clearDataFile(); // utilité ?

		c_story.remove();
		c_requirementContext.remove();
		_requirementContext = null;
		_story = null;
	}

}
