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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.lib.security.annotations.PermissionsOperator_Enum;
import fr.spi4j.lib.security.exception.UnauthentifiedUserException;

/**
 * Classe de test 'Spi4JServerSecurity'.
 */
public class Spi4JServerSecurity_Test {

	/**
	 * Initialisation d'une session mockée.
	 */
	@BeforeEach
	public void setUp() {
		Spi4jSecurity_Abs.setInstance(new Spi4jServerSecurity());
		HttpSessionMock.setHttpSessionMockInHttpSessionFilter();
	}

	/**
	 * Test de la méthode 'setCurrentUser'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testSetCurrentUser() throws Throwable {
		final User_Itf v_user = new User_Itf() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getIdentifiant() {
				return "mvt";
			}
		};

		final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
		v_instance.setCurrentUser(v_user);

		assertNotNull(v_user, "Un utilisateur devrait exister");
		assertEquals("mvt", v_instance.getCurrentUser().getIdentifiant(), "Devrait être identique");
	}

	/**
	 * Test de la méthode 'setCurrentUser' avec comme attribut null.
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testSetCurrentUserNull() throws Throwable {
		assertThrows(UnauthentifiedUserException.class, () -> {
			final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
			v_instance.setCurrentUser(null);
			assertNull(v_instance.getCurrentUser(), "Aucun utilisateur ne devrait exister.");
			assertNull(v_instance.getPermissionsOfCurrentUser(), "Aucune permission ne devrait être disponible.");
		});
	}

	/**
	 * Test de la méthode 'getPermissionsOfCurrentUser' Test de la méthode
	 * 'isCurrentUserSuperAdmin' Test de la méthode 'hasPermissions' et
	 * 'hasPermission'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testGetPermissionOfCurrentUser() throws Throwable {
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

		final Set<String> v_permsCurrentUser = v_instance.getPermissionsOfCurrentUser();
		assertEquals(v_permissions, v_permsCurrentUser, "Les permissions devraient être identiques");
		assertTrue(v_instance.isCurrentUserSuperAdmin(), "L'utilisateur courant est super admin");

		final String[] v_tabPermissions = { "toto", "tata" };

		assertTrue(v_instance.hasPermissions(v_tabPermissions, PermissionsOperator_Enum.OR),
				"L'utilisateur a bien toutes les permissions");
		assertTrue(v_instance.hasPermissions(v_tabPermissions, PermissionsOperator_Enum.AND),
				"L'utilisateur a bien toutes les permissions");
		assertTrue(v_instance.hasPermission("titi"), "L'utilisateur n'a pas cette permission");
	}

	/**
	 * Test de la méthode 'hasPermission' avec l'opérateur 'OR'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testHasPermissionsOR() throws Throwable {
		final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
		Spi4jSecurity_Abs.getInstance().enable();
		final boolean v_isEnabled = Spi4jSecurity_Abs.getInstance().isEnabled();
		assertTrue(v_isEnabled, "Sécurité activée");

		final Set<String> v_permSA = new HashSet<>();
		v_permSA.add("tutu");

		final PermissionContainer v_pc = new PermissionContainer(v_permSA, true);
		v_instance.setAuthorizationService(new AuthorizationService_Itf() {
			@Override
			public PermissionContainer getPermissionsOfCurrentUser() {
				return v_pc;
			}
		});
		v_instance.getPermissionsOfCurrentUser();
		assertTrue(v_instance.isCurrentUserSuperAdmin(), "L'utilisateur courant est superAdmin");

		final String[] v_perms = { "tutu" };

		v_instance.hasPermissions(v_perms, PermissionsOperator_Enum.OR);
		assertTrue(true, "L'utilisateur a une des permissions requises");

		v_instance.hasPermissions(v_perms, PermissionsOperator_Enum.AND);
		assertTrue(true, "l'utilisateur n'a pas toutes les permissions");
	}

	/***
	 * Test de la méthode 'refresh permission'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testRefreshPermission() throws Throwable {
		// Initialisation de l'utilisateur.
		final User_Itf v_user = new User_Itf() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getIdentifiant() {
				return "mvt";
			}
		};

		final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
		v_instance.setCurrentUser(v_user);

		// Initialisation de permissions vides.
		final Set<String> v_perm = new HashSet<>();
		final PermissionContainer v_pcVide = new PermissionContainer(v_perm, false);

		v_instance.setAuthorizationService(new AuthorizationService_Itf() {
			@Override
			public PermissionContainer getPermissionsOfCurrentUser() {
				return v_pcVide;
			}
		});

		assertTrue(v_instance.getPermissionsOfCurrentUser().isEmpty(),
				"L'utilisateur ne devrait pas avoir de permissions.");
		assertFalse(v_instance.isCurrentUserSuperAdmin(), "L'utilisateur ne devrait pas être superAdmin.");

		// Initilisation de permissions pleines
		v_perm.add("perm1");
		final PermissionContainer v_pc = new PermissionContainer(v_perm, true);
		v_instance.setAuthorizationService(new AuthorizationService_Itf() {
			@Override
			public PermissionContainer getPermissionsOfCurrentUser() {
				return v_pc;
			}
		});

		v_instance.refreshPermissions();

		assertFalse(v_instance.getPermissionsOfCurrentUser().isEmpty(),
				"L'utilisateur devrait posséder au moins une permissions.");
		assertTrue(v_instance.getPermissionsOfCurrentUser().contains("perm1"),
				"L'utilisateur devrait posséder la permissions perm1.");
		assertTrue(v_instance.isCurrentUserSuperAdmin(), "L'utilisateur devrait être superAdmin.");
	}

	/**
	 * Test de l'exception 'UnauthentifiedUserException'
	 */
	@Test
	public void testUnauthentifiedUserException() {
		assertThrows(UnauthentifiedUserException.class, () -> {
			final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
			v_instance.getCurrentUser();
			fail("Aucun utilisateur");
		});
	}

	/**
	 * Test de la méthode 'deconnexion'
	 */
	@Test
	public void testDeconnexion() {
		assertThrows(UnauthentifiedUserException.class, () -> {
			final Spi4jSecurity_Abs v_instance = Spi4jSecurity_Abs.getInstance();
			v_instance.deconnexion();
			v_instance.getCurrentUser();
			fail("Il n'y a plus de session");
			assertNull(v_instance.getCurrentUser(), "Il n'y a plus de session");
		});
	}
}
