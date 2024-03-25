/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.lib.security.client.Spi4jClientSecurity;

/**
 * Classe de test 'Spi4jClientSecurity'.
 * 
 * @author MINARM
 */
public class Spi4jClientSecurity_Test {
	/**
	 * Initialisation d'une session mockée.
	 */
	@BeforeEach
	public void setUp() {
		Spi4jSecurity_Abs.setInstance(new Spi4jClientSecurity());
		HttpSessionMock.setHttpSessionMockInHttpSessionFilter();
	}

	/**
	 * Test de la méthode 'setCurrentUser' avec un utilisateur et des permissions.
	 * <p>
	 * Test des méthodes 'getCurrentUser', 'isCurrentUserSuperAdmin' et
	 * 'getPermissionsOfCurrentUser'.
	 * <p>
	 * Test de la méthode 'refreshPermission'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testSetCurrentUserNotNull() throws Throwable {
		final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();

		final Set<String> v_permissions = new HashSet<>();
		v_permissions.add("toto");
		v_permissions.add("tata");
		final PermissionContainer v_pc = new PermissionContainer(v_permissions, true);

		v_instance.setAuthorizationService(new AuthorizationService_Itf() {
			@Override
			public PermissionContainer getPermissionsOfCurrentUser() {
				return v_pc;
			}
		});

		final User_Itf v_user = new User_Itf() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getIdentifiant() {
				return "mvt";
			}
		};

		v_instance.setCurrentUser(v_user);

		assertNotNull(v_user, "Un utilisateur devrait exister");
		assertEquals(v_user.getIdentifiant(), v_instance.getCurrentUser().getIdentifiant(),
				"L'identifiant est incorrect");
		assertTrue(v_instance.isCurrentUserSuperAdmin(), "L'utilisateur est super admin");
		assertEquals(v_permissions, v_instance.getPermissionsOfCurrentUser(), "Les permissions devrait être les mêmes");

	}

	/**
	 * Test de la méthode 'setCurrentUser' avec un utilisateur null.
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testSetCurrentUserNull() throws Throwable {
		final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
		v_instance.setCurrentUser(null);

		assertNull(v_instance.getCurrentUser(), "Aucun utilisateur");
	}

	/**
	 * Test de la méthode 'deconnexion'
	 */
	@Test
	public void testDeconnexion() {
		final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
		v_instance.deconnexion();

		v_instance.getCurrentUser();
		assertNull(v_instance.getCurrentUser(), "L'utilisateur est null");
	}

	/**
	 * Test de la méthode 'refreshPermission'.
	 */
	@Test
	public void testRefreshPermission() {
		final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();

		// Initialisation de l'utilisateur
		final User_Itf v_user = new User_Itf() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getIdentifiant() {
				return "mvt";
			}
		};

		v_instance.setCurrentUser(v_user);

		// Initialisation de permissions vides.
		final Set<String> v_permissions = new HashSet<>();
		final PermissionContainer v_pcVide = new PermissionContainer(v_permissions, false);

		v_instance.setAuthorizationService(new AuthorizationService_Itf() {
			@Override
			public PermissionContainer getPermissionsOfCurrentUser() {
				return v_pcVide;
			}
		});

		assertTrue(v_instance.getPermissionsOfCurrentUser().isEmpty(),
				"L'utilisateur ne devrait pas avoir de permissions.");
		assertFalse(v_instance.isCurrentUserSuperAdmin(), "L'utilisateur ne devrait pas être superAdmin.");

		// Initialisation de permissions remplies.
		v_permissions.add("perm1");
		final PermissionContainer v_pc = new PermissionContainer(v_permissions, true);

		v_instance.setAuthorizationService(new AuthorizationService_Itf() {
			@Override
			public PermissionContainer getPermissionsOfCurrentUser() {
				return v_pc;
			}
		});

		v_instance.refreshPermissions();

		assertFalse(v_instance.getPermissionsOfCurrentUser().isEmpty(),
				"L'utilisateur devrait avoir au moins une permission.");
		assertTrue(v_instance.getPermissionsOfCurrentUser().contains("perm1"),
				"L'utilisateur devrait posséder la permissions perm1.");
		assertTrue(v_instance.isCurrentUserSuperAdmin(), "L'utilisateur devrait être superAdmin.");

	}

}
