/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.graal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.lib.jbehave.graal.Sample.SamplePresenter2;

/**
 * Classe de tests des exceptions.
 * 
 * @author MINARM
 */
public class Exceptions_Test extends GraalTest_Abs {

	/**
	 * Test FieldNotFound.
	 */
	@Test
	public void testFieldNotFound() {
		given.activateView("NoView");
		try {
			when.userSetField("abc", "XXX");
			fail("Une exception doit arriver car le champ n'existe pas");
		} catch (final GraalException v_e) {
			assertNotNull(v_e, "Une exception doit arriver");
			assertTrue(v_e.getMessage().startsWith("Aucune annotation nommée @Field(\"XXX\")"),
					"Le message d'erreur est incorrect");
		}
	}

	/**
	 * Test UserActionNotFound.
	 */
	@Test
	public void testUserActionNotFound() {
		given.activateView("NoView");
		try {
			when.userClickButton("XXX");
			fail("Une exception doit arriver car l'action n'existe pas");
		} catch (final GraalException v_e) {
			assertNotNull(v_e, "Une exception doit arriver");
			assertTrue(v_e.getMessage().startsWith("Aucune annotation nommée @UserAction(\"XXX\")"),
					"Le message d'erreur est incorrect");
		}
	}

	/**
	 * Test UserViewNotFound.
	 */
	@Test
	public void testUserViewNotFound() {
		final SamplePresenter2 v_presenter = new SamplePresenter2();
		assertNotNull(v_presenter, "non null");
		try {
			then.viewIsActive("XXX");
			fail("Une exception doit arriver car la vue n'a pas d'annotation UserView");
		} catch (final AssertionError v_e) {
			assertNotNull(v_e, "Une exception doit arriver");
			assertTrue(v_e.getMessage().startsWith("La vue XXX aurait dû être active."),
					"Le message d'erreur est incorrect");
		}
	}

	/**
	 * Test exception chooseValueInList sur champ normal.
	 */
	@Test
	public void testCastException1() {
		given.activateView("NoView");
		try {
			when.userChooseValueInList("abc", "Champ");
			fail("Une exception doit arriver car le champ n'est pas un HasSelection_Itf");
		} catch (final GraalException v_e) {
			assertNotNull(v_e, "Une exception doit arriver");
			assertEquals("Le champ \"Champ\" n'est pas un HasList_Itf", v_e.getMessage(),
					"Le message d'erreur est incorrect");
		}
	}

	/**
	 * Test exception set value sur un champ non HasValue
	 */
	@Test
	public void testCastException2() {
		given.activateView("NoView");
		try {
			when.userSetField("abc", "WrongField");
			fail("Une exception doit arriver car le champ n'est pas un HasValue_Itf");
		} catch (final GraalException v_e) {
			assertNotNull(v_e, "Une exception doit arriver");
			assertEquals("Le champ \"WrongField\" n'est pas un HasValue_Itf", v_e.getMessage(),
					"Le message d'erreur est incorrect");
		}
	}

	/**
	 * Test exception listContainsValue sur champ normal.
	 */
	@Test
	public void testCastException3() {
		given.activateView("NoView");
		try {
			then.listContainsValue("Champ", "abc");
			fail("Une exception doit arriver car le champ n'est pas un HasSelection_Itf");
		} catch (final GraalException v_e) {
			assertNotNull(v_e, "Une exception doit arriver");
			assertEquals("Le champ \"Champ\" n'est pas un HasList_Itf", v_e.getMessage(),
					"Le message d'erreur est incorrect");
		}
	}

	/**
	 * Test exception set int mauvais format.
	 */
	@Test
	public void testFormatException1() {
		given.activateView("NoView");
		try {
			when.userSetField("abc", "Int");
			fail("Une exception doit arriver car la valeur saisie n'est pas un entier");
		} catch (final Spi4jRuntimeException v_e) {
			assertNotNull(v_e, "Une exception doit arriver");
			assertEquals("Impossible de parser l'entier abc pour le champ \"Int\"", v_e.getMessage(),
					"Le message d'erreur est incorrect");
		}
	}

	/**
	 * Test exception set date mauvais format.
	 */
	@Test
	public void testFormatException2() {
		given.activateView("NoView");
		try {
			when.userSetField("abc", "Date");
			fail("Une exception doit arriver car la valeur saisie n'est pas une date");
		} catch (final Spi4jRuntimeException v_e) {
			assertNotNull(v_e, "Une exception doit arriver");
			assertEquals("Impossible de parser la date abc pour le champ \"Date\"", v_e.getMessage(),
					"Le message d'erreur est incorrect");
		}
	}

	/**
	 * Test exception set date mauvais format.
	 */
	@Test
	public void testFormatException3() {
		given.activateView("NoView");
		try {
			when.userSetField("abc", "Double");
			fail("Une exception doit arriver car la valeur saisie n'est pas un double");
		} catch (final Spi4jRuntimeException v_e) {
			assertNotNull(v_e, "Une exception doit arriver");
			assertEquals("Impossible de parser le double abc pour le champ \"Double\"", v_e.getMessage(),
					"Le message d'erreur est incorrect");
		}
	}
}
