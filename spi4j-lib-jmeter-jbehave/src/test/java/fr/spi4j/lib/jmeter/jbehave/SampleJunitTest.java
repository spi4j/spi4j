/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import junit.framework.TestCase;

/**
 * Classe simple de test JUnit.
 * 
 * @author MINARM
 */
// Runner Junit4 pour ne pas faire d'exception pour la méthode testException
//@RunWith(JUnit4.class)
public class SampleJunitTest extends TestCase {

	/**
	 * Test basic.
	 */
	@Test
	public void testStuff() {
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException v_e) {
			// RAS
			fail(v_e.toString());
		}
	}

	/**
	 * Test avec exception.
	 */

	public void testException() {
		assertThrows(RuntimeException.class, () -> {
			throw new RuntimeException("normal fail");
		});
	}
}
