/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.requirement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import fr.spi4j.lib.jbehave.SpiStoryContextHandler;

/**
 * Test de tirage d'exigences.
 * 
 * @author MINARM
 */
public class RequirementsUtil_Test {

	/**
	 * Les exigences sont tirées.
	 */
	@Test
	public void callingMethodHavingRequirementAnnotationShouldReturnHasBeenUsed() {
		// démarrage de la session de requirements
		SpiStoryContextHandler.start();

		// création du service
		final MyService_Itf v_service = RequirementsInServiceProxy.createProxy(new MyService(),
				Requirement_AnnotationForTest.class);

		// les exigences ne sont pas tirées au démarrage
		assertFalse(SpiStoryContextHandler.get_currentRequirementContext().hasBeenUsed(Requirement_EnumForTest.REQ_FCT_PERS_01),
				"Exigence ne doit pas encore être tirée");
		assertFalse(SpiStoryContextHandler.get_currentRequirementContext().hasBeenUsed(Requirement_EnumForTest.REQ_TEC_PERS_02),
				"Exigence ne doit pas encore être tirée");

		// appel de la méthode
		v_service.methode();

		// une exigence a été tirée, mais pas l'autre
		assertTrue(SpiStoryContextHandler.get_currentRequirementContext().hasBeenUsed(Requirement_EnumForTest.REQ_FCT_PERS_01),
				"Exigence doit avoir été tirée");
		assertFalse(SpiStoryContextHandler.get_currentRequirementContext().hasBeenUsed(Requirement_EnumForTest.REQ_TEC_PERS_02),
				"Exigence ne doit pas avoir été tirée");
		SpiStoryContextHandler.destroy();
	}
}
