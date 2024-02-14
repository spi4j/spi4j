/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test de story non trouvée.
 * 
 * @author MINARM
 */
public class StoryNotFound_Test extends Story_Test {

	@Override
	public void run() {
		try {
			super.run();
			fail("La story ne devrait pas exister");
		} catch (final SpiStoryException v_e) {
			assertTrue(v_e.getMessage().startsWith("Impossible de trouver la Story JBehave."), "Story trouvée");
		}
	}
}
