/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.steps;

import java.util.List;

import fr.spi4j.lib.jbehave.SpiStory_Abs;
import fr.spi4j.requirement.Requirement_Itf;
import fr.spi4j.ui.mvp.ViewsAssociation;

/**
 * Story autour d'une grille de jeu.
 * 
 * @author MINARM
 */
public abstract class GridStory extends SpiStory_Abs {

	@Override
	public List<Object> getStepsClassesInstances() {
		final List<Object> v_classesInstances = super.getStepsClassesInstances();
		v_classesInstances.add(new GridSteps());
		return v_classesInstances;
	}

	@Override
	public ViewsAssociation getViewsAssociation() {
		return null;
	}

	@Override
	public Requirement_Itf findRequirement(final String p_req) {
		return null;
	}

	@Override
	public boolean shouldCheckRequirements() {
		return false;
	}

	@Override
	public boolean shouldGenerateViewAfterStories() {
		return false;
	}
}
