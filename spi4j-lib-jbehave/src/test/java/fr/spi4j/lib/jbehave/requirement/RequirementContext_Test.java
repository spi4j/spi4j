/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.requirement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Classe de tests du contexte.
 * 
 * @author MINARM
 */
public class RequirementContext_Test {

	private static Method toStringMethod;

	private static Method hashCodeMethod;

	/**
	 * Initialisation des tests.
	 * 
	 * @throws NoSuchMethodException méthode inconnue
	 */
	@BeforeAll
	public static void setUpBeforeClass() throws NoSuchMethodException {
		toStringMethod = Object.class.getMethod("toString");
		hashCodeMethod = Object.class.getMethod("hashCode");
	}

	/**
	 * Vérification de l'état initial du contexte.
	 */
	@Test
	public void initialState() {
		final RequirementContext v_requirementContext = new RequirementContext();
		assertTrue(v_requirementContext.usagesFor(Requirement_EnumForTest.REQ_FCT_PERS_01).isEmpty(),
				"Aucune exigence ne doit avoir été tirée");
	}

	/**
	 * Tirage d'une exigence.
	 */
	@Test
	public void add1Method() {
		final RequirementContext v_requirementContext = new RequirementContext();
		v_requirementContext.used(Requirement_EnumForTest.REQ_FCT_PERS_01, toStringMethod);
		assertTrue(v_requirementContext.hasBeenUsed(Requirement_EnumForTest.REQ_FCT_PERS_01));
		assertThat(v_requirementContext.usagesFor(Requirement_EnumForTest.REQ_FCT_PERS_01),
				Matchers.hasItem(toStringMethod));
	}

	/**
	 * Tirage de 2 exigences.
	 */
	@Test
	public void add2Method() {
		final RequirementContext v_requirementContext = new RequirementContext();
		v_requirementContext.used(Requirement_EnumForTest.REQ_FCT_PERS_01, toStringMethod);
		v_requirementContext.used(Requirement_EnumForTest.REQ_FCT_PERS_01, hashCodeMethod);
		assertThat(v_requirementContext.usagesFor(Requirement_EnumForTest.REQ_FCT_PERS_01),
				Matchers.hasItems(toStringMethod, hashCodeMethod));
	}

	/**
	 * Tirage de 2 exigences sur la même méthode.
	 */
	@Test
	public void add1Method2Reqs() {
		final RequirementContext v_requirementContext = new RequirementContext();
		v_requirementContext.used(Requirement_EnumForTest.REQ_FCT_PERS_01, toStringMethod);
		v_requirementContext.used(Requirement_EnumForTest.REQ_TEC_PERS_02, toStringMethod);
		assertThat(v_requirementContext.usagesFor(Requirement_EnumForTest.REQ_FCT_PERS_01),
				Matchers.hasItems(toStringMethod));
		assertThat(v_requirementContext.usagesFor(Requirement_EnumForTest.REQ_TEC_PERS_02),
				Matchers.hasItems(toStringMethod));
	}

	/**
	 * Vérification de l'état final du contexte.
	 */
	@Test
	public void finalState() {
		final RequirementContext v_requirementContext = new RequirementContext();
		assertTrue(v_requirementContext.usagesFor(Requirement_EnumForTest.REQ_FCT_PERS_01).isEmpty(),
				"Aucune exigence ne doit avoir été tirée");
		v_requirementContext.used(Requirement_EnumForTest.REQ_FCT_PERS_01, toStringMethod);
		assertTrue(v_requirementContext.hasBeenUsed(Requirement_EnumForTest.REQ_FCT_PERS_01));
		v_requirementContext.clear();
		assertFalse(v_requirementContext.hasBeenUsed(Requirement_EnumForTest.REQ_FCT_PERS_01));
	}
}
