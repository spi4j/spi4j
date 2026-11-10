/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.jbehave.core.annotations.When;

/**
 * Exemples de steps.
 * 
 * @author MINARM
 */
public class StorySteps extends SpiSteps_Abs {
	/**
	 * Constructeur parent.
	 * 
	 * @param p_story la story
	 */
	public StorySteps(final SpiStory_Abs p_story) {
		super(p_story);
	}

	/**
	 * Step ok.
	 */
	@When("Step ok")
	public void stepOk() {
		assertNotNull(get_story(), "La story devrait être connue");
	}

	/**
	 * Step qui fera une erreur.
	 * 
	 * @param p_message le message d'erreur
	 */
	@When("Step fail \"$message\"")
	public void stepFail(final String p_message) {
		fail(p_message);
	}
}
