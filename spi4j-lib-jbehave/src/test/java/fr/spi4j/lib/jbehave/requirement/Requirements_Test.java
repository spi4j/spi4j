/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.requirement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.lib.jbehave.SpiStoryContextHandler;
import fr.spi4j.requirement.RequirementException;

/**
 * Test des requirements.
 * 
 * @author MINARM
 */
public class Requirements_Test {
	/**
	 * Test des requirements.
	 * 
	 * @throws NoSuchMethodException La méthode n'existe pas.
	 */
	@Test
	public void testRequirement() throws NoSuchMethodException {
		// Initialisation du contexte.
		SpiStoryContextHandler.start();

		// Initialisation du service.
		final MyService_Itf v_service = RequirementsInServiceProxy.createProxy(new MyService(),
				Requirement_AnnotationForTest.class);

		// La méthode methode n'a qu'une exigence : Requirement_Enum.REQ_FCT_PERS_01.
		v_service.methode();

		final RequirementContext v_context = SpiStoryContextHandler.get_currentRequirementContext(); 

		// Vérification initialisation du contexte.
		assertNotNull(v_context, "Le contexte d'enregistrement des exigences n'a pas été initialisé.");

		// Vérification que l'exigence Requirement_Enum.REQ_FCT_PERS_01 a été tirée.
		assertTrue(v_context.hasBeenUsed(Requirement_EnumForTest.REQ_FCT_PERS_01),
				"L'exigence " + Requirement_EnumForTest.REQ_FCT_PERS_01 + " n'a pas été testée.");

		// Vérification que l'exigence Requirement_Enum.REQ_TEC_PERS_02 n'a pas été
		// tirée.
		assertFalse(v_context.hasBeenUsed(Requirement_EnumForTest.REQ_TEC_PERS_02),
				"L'exigence " + Requirement_EnumForTest.REQ_TEC_PERS_02 + " n'aurait pas dû être testée.");

		// Vérification du nombre de méthodes portant l'exigence
		// Requirement_Enum.REQ_FCT_PERS_01.
		assertEquals(1, v_context.usagesFor(Requirement_EnumForTest.REQ_FCT_PERS_01).size(),
				"Il aurait dû y avoir une seule méthode portant l'exigence tirée.");

		// Vérification de la méthode ayant tirée l'exigence
		// Requirement_Enum.REQ_FCT_PERS_01.
		assertEquals(MyService_Itf.class.getMethod("methode"),
				v_context.usagesFor(Requirement_EnumForTest.REQ_FCT_PERS_01).get(0),
				"La méthode aurait dû tester l'exigence " + Requirement_EnumForTest.REQ_FCT_PERS_01 + '.');

		try {
			// Méthode qui jette une exception pour passer dans le
			// InvocationHandlerException.
			v_service.methodeJetantException();
			fail("Exception jetée");
		} catch (final Spi4jRuntimeException v_e) {
			assertTrue(true, "Exception jetée");
		}

		SpiStoryContextHandler.clearCurrentRequirementContext();
		assertFalse(SpiStoryContextHandler.get_currentRequirementContext().hasBeenUsed(Requirement_EnumForTest.REQ_FCT_PERS_01),
				"L'exigence " + Requirement_EnumForTest.REQ_FCT_PERS_01 + " n'aurait plus dû être testée.");

		SpiStoryContextHandler.destroy();
		assertNull(SpiStoryContextHandler.get_currentRequirementContext(),
				"Le contexte d'enregistrement des exigences ne doit plus être initialisé.");
	}

	/**
	 * Test d'une exigence sans context.
	 */
	@Test
	public void testRequirementWithoutContext() {
		// Initialisation du service.
		// Note : Aucun contexte n'a été initialisé.
		final MyService_Itf v_service = RequirementsInServiceProxy.createProxy(new MyService(),
				Requirement_AnnotationForTest.class);

		try {
			v_service.methode();
			fail("Une exception doit être jetée.");
		} catch (final Exception v_e) {
			// Vérification des messages d'erreurs lancés dus à l'absence de contexte.
			assertEquals(Spi4jRuntimeException.class, v_e.getClass(), "Les types d'erreurs devraient être les mêmes.");
			assertEquals("La session d'enregistrement des requirements n'a pas été initialisée", v_e.getMessage(),
					"Les messages d'erreurs devraient être les mêmes.");
		}
	}

	/**
	 * Test des requirements.
	 */
	@Test
	public void testRequirementException() {
		final RequirementException v_requirementException = new RequirementException(
				Requirement_EnumForTest.REQ_FCT_PERS_01, Requirement_EnumForTest.REQ_FCT_PERS_01.getName());
		assertEquals(Requirement_EnumForTest.REQ_FCT_PERS_01, v_requirementException.getRequirement(),
				"RequirementException");

		final RequirementException v_requirementException2 = new RequirementException(
				Requirement_EnumForTest.REQ_FCT_PERS_01, Requirement_EnumForTest.REQ_FCT_PERS_01.getName(),
				new Exception("dummy cause"));
		assertEquals("dummy cause", v_requirementException2.getCause().getMessage(), "RequirementException2");
	}
}
