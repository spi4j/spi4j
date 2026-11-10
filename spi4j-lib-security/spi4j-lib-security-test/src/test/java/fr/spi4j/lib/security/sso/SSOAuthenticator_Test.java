/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.sso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import fr.spi4j.lib.security.exception.Spi4jSecurityException;
import fr.spi4j.lib.security.sso.SSOUserAttributes.Role;

/**
 * Test de l'authentification par SSO.
 * 
 * @author MINARM
 */
@Disabled
// cette classe de test est mise en Ignore pour JUnit car il dépend d'un serveur du SID sur l'intradef
public class SSOAuthenticator_Test {

	private static final SSOAuthenticator_Itf c_auth = SSOAuthenticatorMock
			.getSSOAuthenticator("http://pp-sso-sid.intradef.gouv.fr/opensso/");

	/**
	 * Test de connexion.
	 */
	@Test
	public void testComplet() {
		// Mot de passe d'Erwan Garel
		c_auth.setConnectionTimeout(10000);
		c_auth.setTimeout(10000);
		final SSOCookies v_cookies = c_auth.login(SSOAuthenticatorMock.c_userGood, SSOAuthenticatorMock.c_passwordGood);

		final List<String> v_listCookies = c_auth.searchCookiesToForward();

		assertNotNull(v_listCookies, "La liste des cookies ne devrait pas être vide.");

		assertTrue(c_auth.checkTokenValidity(v_cookies), "Le token devrait être valide");

		final SSOUserAttributes v_attributes = c_auth.getAttributes(v_cookies);
		assertNotNull(v_attributes, "Les attributs LDAP doivent exister");
		assertEquals(SSOAuthenticatorMock.c_userGood, v_attributes.getAttributeListByName("uid").get(0),
				"Mauvais uid dans les attributs ldap");
		assertEquals("GAREL Erwan", v_attributes.getAttributeListByName("cn").get(0),
				"Mauvais cn dans les attributs ldap");

		assertNotNull(v_attributes.getAttributes(), "Les attributs doivent exister");
		assertNotNull(v_attributes.getTokenId(), "Le token ne devrait pas être vide");
		assertNotNull(v_attributes.getTokenId(), "Le token devrait avoir un id");

		assertNotNull(v_attributes.toString(), "L'objet devrait être rempli");

		assertNotNull(v_attributes.getRoles(), "L'utilisateur devrait posséder des roles");
		assertFalse(v_attributes.getRoles().isEmpty(), "L'utilisateur devrait posséder des roles");

		final Role v_role = v_attributes.getRoles().get(0);
		assertNotNull(v_role.getFullString(), "La chaîne complète du rôle");
		assertEquals(v_role.getFullString(), "Les rôles devraient correspondre",
				"id=ugrp.cosi,ou=group,dc=opensso,dc=java,dc=net");

		assertNotNull(v_role.getRoot(), "Le dernier élément du rôle");
		assertEquals("ugrp.cosi", v_role.getRoot(), "Les rôles devraient correspondre");

		assertNotNull(v_role.toString(), "L'objet devrait être rempli");
		assertEquals("ugrp.cosi", v_role.toString(), "Les rôles devraient correspondre");

		c_auth.logout(v_cookies);

		assertFalse(c_auth.checkTokenValidity(v_cookies), "Le token ne devrait plus être valide");
	}

	/**
	 * Test mauvais mot de passe
	 */
	@Test
	public void testWrongPassword() {
		assertThrows(Spi4jSecurityException.class, () -> {
			c_auth.login(SSOAuthenticatorMock.c_userGood, SSOAuthenticatorMock.c_passwordBad);
		});
	}

	/**
	 * Test mauvais utilisateur
	 */
	@Test
	public void testWrongLogin() {
		assertThrows(Spi4jSecurityException.class, () -> {
			c_auth.login(SSOAuthenticatorMock.c_userBad, SSOAuthenticatorMock.c_passwordBad);
		});
	}

	/**
	 * Test mauvaise URL
	 */
	@Test
	public void testMauvaiseURL() {
		assertThrows(Spi4jSecurityException.class, () -> {
			final SSOAuthenticator_Itf v_auth = new SSOAuthenticator("http://url");
			v_auth.login(SSOAuthenticatorMock.c_userBad, SSOAuthenticatorMock.c_passwordBad);
		});
	}
}
