/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.requirement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import fr.spi4j.lib.jbehave.SpiStoryContextHandler;
import fr.spi4j.lib.jbehave.Story_Test;
import fr.spi4j.lib.jbehave.graal.GraalTest_Abs;

/**
 * Classe de test pour requirements dans les stories.
 * 
 * @author MINARM
 */
public class RequirementInStory_Test extends GraalTest_Abs {

	/**
	 * Test qu'une exigence a bien été tirée.
	 */
	@Test
	public void testRequirement() {
		final Story_Test v_story = new Story_Test();
		setStory(v_story);

		// démarrage de la session d'enregistrement des requirements
		SpiStoryContextHandler.start(v_story);

		try {
			// l'exigence n'a pas encore été tirée
			then.requirementHasBeenUsed("REQ_" + Requirement_EnumForTest.REQ_FCT_PERS_01.getId());
			fail("Assertion en échec attendue");
		} catch (final AssertionError v_e) {
			// assertion en échec attendue
			assertTrue(v_e.getMessage().startsWith("L'exigence REQ_" + Requirement_EnumForTest.REQ_FCT_PERS_01.getId() + " n'a pas été testée") ,"Message d'erreur incorrect");

		}

		// appel du service et implicitement de l'exigence
		final MyService_Itf v_service = RequirementsInServiceProxy.createProxy(new MyService(),
				Requirement_AnnotationForTest.class);
		v_service.methode();

		// vérification que l'exigence a été tirée
		then.requirementHasBeenUsed("REQ_" + Requirement_EnumForTest.REQ_FCT_PERS_01.getId());

		// remise à zéro de la session d'enregistrement des requirements
		SpiStoryContextHandler.destroy();
	}

}
