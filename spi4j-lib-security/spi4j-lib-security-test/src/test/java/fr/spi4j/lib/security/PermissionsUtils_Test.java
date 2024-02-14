/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.lib.security.annotations.Permissions;
import fr.spi4j.lib.security.annotations.PermissionsOperator_Enum;
import fr.spi4j.lib.security.testapp.DtoService_Itf;
import fr.spi4j.lib.security.testapp.MyService_Itf;
import fr.spi4j.lib.security.testapp.MyUserBusiness;
import fr.spi4j.remoting.RemotingServlet;

/**
 * Tests de la classe PermissionsUtils.
 * 
 * @author MINARM
 */
public class PermissionsUtils_Test {

	/**
	 * Initialisation des tests : initialisation du UserBusiness.
	 */
	@BeforeEach
	public void setUp() {
		final MyUserBusiness v_myUserBusiness = MyUserBusiness.getSingleton();
		v_myUserBusiness.initBindings();
		RemotingServlet.setUserBusiness(v_myUserBusiness);
	}

	/**
	 * Test de la méthode utilitaire de récupération des permissions par opération
	 * de service.
	 * 
	 * @throws NoSuchMethodException méthode inconnue
	 */
	@Test
	public void testPermissionsByOperation() throws NoSuchMethodException {
		final Map<String, Permissions> v_permissions = PermissionsUtils.getMapPermissionsByOperation();
		assertNotNull(v_permissions, "Les permissions n'ont pas été trouvées");

		final String v_methodOperation1 = PermissionsUtils.getMethodId(MyService_Itf.class,
				MyService_Itf.class.getMethod("operation1"));
		assertNotNull(v_permissions.get(v_methodOperation1), "Des permissions devraient exister pour l'opération1");
		assertEquals(1, v_permissions.get(v_methodOperation1).value().length,
				"L'opération1 devrait avoir exactement 1 permission");
		assertEquals("perm1", v_permissions.get(v_methodOperation1).value()[0],
				"L'opération1 devrait avoir la permission perm1");
		assertEquals(PermissionsOperator_Enum.OR, v_permissions.get(v_methodOperation1).operator(),
				"L'opérateur des permissions de l'opération1 devrait être OR");

		final String v_methodOperation2 = PermissionsUtils.getMethodId(MyService_Itf.class,
				MyService_Itf.class.getMethod("operation2"));
		assertNull(v_permissions.get(v_methodOperation2), "Aucune permission ne devrait exister pour l'opération2");

		final String v_methodDtoOperation1 = PermissionsUtils.getMethodId(DtoService_Itf.class,
				DtoService_Itf.class.getMethod("operationSurDto1"));
		assertNotNull(v_permissions.get(v_methodDtoOperation1),
				"Des permissions devraient exister pour l'opérationSurDto1");
		assertEquals(1, v_permissions.get(v_methodDtoOperation1).value().length,
				"L'opérationSurDto1 devrait avoir exactement 1 permission");
		assertEquals("permDto1", v_permissions.get(v_methodDtoOperation1).value()[0],
				"L'opérationSurDto1 devrait avoir la permission permDto1");
		assertEquals(PermissionsOperator_Enum.OR, v_permissions.get(v_methodDtoOperation1).operator(),
				"L'opérateur des permissions de l'opérationSurDto1 devrait être OR");

		final String v_methodDtoFindById = PermissionsUtils.getMethodId(DtoService_Itf.class,
				DtoService_Itf.class.getMethod("findById", Long.class));
		assertNotNull(v_permissions.get(v_methodDtoFindById),
				"Des permissions devraient exister pour l'opérationDtoFindById");
		assertEquals(2, v_permissions.get(v_methodDtoFindById).value().length,
				"L'opérationDtoFindById devrait avoir exactement 2 permissions");
		assertEquals("permDto2", v_permissions.get(v_methodDtoFindById).value()[0],
				"L'opérationDtoFindById devrait avoir la permission permDto2");
		assertEquals("permDto3", v_permissions.get(v_methodDtoFindById).value()[1],
				"L'opérationDtoFindById devrait avoir la permission permDto3");
		assertEquals(PermissionsOperator_Enum.AND, v_permissions.get(v_methodDtoFindById).operator(),
				"L'opérateur des permissions de l'opérationDtoFindById devrait être AND");

		// comptage des méthodes existantes por Service_Itf :
		// save / findById / findAll / delete // operationSurDto1 / operationSurDto2
		int v_nbMethods = 0;
		for (final String v_key : v_permissions.keySet()) {
			if (v_key.contains(DtoService_Itf.class.getCanonicalName())) {
				v_nbMethods++;
			}
		}
		assertEquals(7, v_nbMethods, "Il devrait y avoir 7 méthodes pour Service_Itf");
	}

}
