/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.stories;

import fr.spi4j.lib.jmeter.jbehave.gameoflife.steps.GridStory;

/**
 * Story ICanToggleACell.
 * 
 * @author MINARM
 */
public class ICanToggleACell_Story extends GridStory {
	@Override
	public int getThreadsToLaunch() {
		return 1;
	}
}
