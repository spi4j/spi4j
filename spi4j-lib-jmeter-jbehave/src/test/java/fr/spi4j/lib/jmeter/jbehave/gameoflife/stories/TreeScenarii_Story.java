/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.stories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import fr.spi4j.lib.jbehave.SpiStoryException;
import fr.spi4j.lib.jmeter.jbehave.gameoflife.steps.GridStory;

/**
 * Story TreeScenarii.
 * 
 * @author MINARM
 */
public class TreeScenarii_Story extends GridStory {

	@Override
	public void run() {
		try {
			super.run();
			fail("Erreur attendue");
		} catch (final SpiStoryException v_e) {
			assertTrue("echec expres".equals(v_e.getMessage()), "Erreur attendue");
		}
	}

	@Override
	public int getThreadsToLaunch() {
		return 1;
	}
}
