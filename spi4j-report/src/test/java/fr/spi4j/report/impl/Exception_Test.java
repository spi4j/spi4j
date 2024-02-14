/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import fr.spi4j.report.ReportException;

/**
 * Test de la classe ReportException
 * 
 * @author MINARM
 */
public class Exception_Test {
	/**
	 * Test.
	 */
	@Test
	public void test() {
		final String v_message = "test";
		assertNotNull(new ReportException(v_message));
		assertNotNull(new ReportException(v_message, new ReportException(v_message)));
		assertNotNull(new ReportException("Problème simulé pour les tests", new ReportException(v_message)));
	}
}
