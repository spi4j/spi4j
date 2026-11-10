/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test de la classe Login
 * 
 * @author MINARM
 */
public class Login_Test {

	/**
	 * Test de la classe login avec toutes ses méthodes
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testLogin() throws Throwable {
		final Login v_login = new Login("identifiant", "mdp");
		assertNotNull(v_login, "L'objet a été instancié");

		if (v_login.getIdentifiant() != null && v_login.getMotDePasse() != null) {
			v_login.toString();
		}
		assertEquals("identifiant", v_login.getIdentifiant(), "L'identifiant est incorrect");
		assertEquals("mdp", v_login.getMotDePasse(), "Le mot de passe est incorrect");
	}
}
