/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Classe de test 'Spi4JSecurity'.
 */
public class Spi4jSecurity_Test {

	/**
	 * Initialisation d'une session mockée.
	 */
	@BeforeEach
	public void setUp() {
		Spi4jSecurity_Abs.setInstance(new Spi4jServerSecurity());
		HttpSessionMock.setHttpSessionMockInHttpSessionFilter();
	}

	/**
	 * Test de l'opération 'testGetInstance'.
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testGetInstance() throws Throwable {
		final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
		assertNotNull(v_instance, "L'instance ne devrait pas être null");
	}

	/**
	 * Test de l'opération 'testEnable'.
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testEnable() throws Throwable {
		final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
		if (!v_instance.isEnabled()) {
			v_instance.enable();
		}

		final boolean v_isEnabled = v_instance.isEnabled();

		assertTrue(v_isEnabled, "Devrait être vrai");
	}

	/**
	 * Test simple de stockage d'un utilisateur.
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testSetUser() throws Throwable {
		final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
		v_instance.setCurrentUser(new User_Itf() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getIdentifiant() {
				return "abc";
			}
		});
		assertNotNull(v_instance.getCurrentUser());
	}

}
