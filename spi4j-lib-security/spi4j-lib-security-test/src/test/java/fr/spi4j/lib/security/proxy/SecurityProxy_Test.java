/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.proxy;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import fr.spi4j.lib.security.AuthorizationService_Itf;
import fr.spi4j.lib.security.HttpSessionMock;
import fr.spi4j.lib.security.PermissionContainer;
import fr.spi4j.lib.security.SecurityProxy;
import fr.spi4j.lib.security.Spi4jSecurity_Abs;
import fr.spi4j.lib.security.Spi4jServerSecurity;
import fr.spi4j.lib.security.User_Itf;
import fr.spi4j.lib.security.exception.Spi4jSecurityException;
import fr.spi4j.lib.security.exception.UnauthorizedActionException;

/**
 * Test du SecurityProxy
 * 
 * @author MINARM
 */
public class SecurityProxy_Test {
	/**
	 * Test de SecurityProxy
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testProxy() throws Throwable {
		Spi4jSecurity_Abs.setInstance(new Spi4jServerSecurity());
		Spi4jSecurity_Abs.getInstance().enable();
		HttpSessionMock.setHttpSessionMockInHttpSessionFilter();
		Spi4jSecurity_Abs.getInstance().setAuthorizationService(new AuthorizationService_Itf() {

			@Override
			public PermissionContainer getPermissionsOfCurrentUser() {
				final Set<String> v_permissions = new HashSet<>();
				v_permissions.add("toto");
				v_permissions.add("tata");

				final boolean v_isSuperAdminF = false;
				final PermissionContainer v_pc = new PermissionContainer(v_permissions, v_isSuperAdminF);

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

		Spi4jSecurity_Abs.getInstance().setCurrentUser(v_user);

		Service_Itf v_monTest = new Service();
		v_monTest = SecurityProxy.createProxy(v_monTest);

		try {
			v_monTest.orTest();
			fail("L'utilisateur n'a aucune des permissions");
		} catch (final Exception v_ex) {
			assertTrue(true, "L'utilisateur n'a aucune des permissions");
		}

		try {
			v_monTest.andTest();
			fail("L'utilisateur n'a aucune des permissions");
		} catch (final UnauthorizedActionException v_e) {
			assertTrue(true, "L'utilisateur n'a aucune des permissions");
		}

		v_monTest.maMethode();

		v_monTest.maMethode("plop", "plop");

		v_monTest.maMethode("plop", 2);

		v_monTest.maMethode("toto");

		try {
			v_monTest.jeterExceptionAvecCause();
			fail("Exception jetée");
		} catch (final Spi4jSecurityException v_e) {
			assertTrue(true, "Exception jetée");
		}
	}
}
