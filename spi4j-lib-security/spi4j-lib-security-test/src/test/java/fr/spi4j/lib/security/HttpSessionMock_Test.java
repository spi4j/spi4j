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

import org.junit.jupiter.api.Test;

/**
 * Test de la classe 'HttpMockSession'
 * 
 * @author MINARM
 */
public class HttpSessionMock_Test {
	private final HttpSessionMock _hSM = new HttpSessionMock();

	/**
	 * Test de la méthode 'getCreationTime'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testGetCreationTime() throws Throwable {
		assertEquals(0, _hSM.getCreationTime(), "retourne 0");
	}

	/**
	 * Test de la méthode 'getId'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testGetId() throws Throwable {
		assertNull(_hSM.getId(), "retourne null");
	}

	/**
	 * Test de la méthode 'getLastAccessedTime'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testGetLastAccessedTime() throws Throwable {
		assertEquals(0, _hSM.getLastAccessedTime(), "retourne 0");
	}

	/**
	 * Test de la méthode 'getServletContext'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testGetServletContext() throws Throwable {
		assertNull(_hSM.getServletContext(), "retourne null");
	}

	/**
	 * Test de la méthode 'setMaxInactiveInterval'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testSetMaxInactiveInterval() throws Throwable {
		_hSM.setMaxInactiveInterval(0);
		assertTrue(true);
	}

	/**
	 * Test de la méthode 'getMaxInactiveInterval'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testGetMaxInactiveInterval() throws Throwable {
		assertEquals(0, _hSM.getMaxInactiveInterval(), "retourne 0");
	}

	/**
	 * Test de la méthode 'getSessionContext'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testGetSessionContext() throws Throwable {
		assertNull(_hSM.getSessionContext(), "retourne null");
	}

	/**
	 * Test des méthodes relatives aux attributs
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testAttribute() throws Throwable {
		final Object v_newObject = new Object();
		_hSM.setAttribute("attribut", v_newObject);

		assertEquals(v_newObject, _hSM.getAttribute("attribut"), "Les deux objets devraient être identiques");
		assertNotNull(_hSM.getAttributeNames(), "il devrait y avoir au moins un attribut");

		_hSM.removeAttribute("attribut");
		assertNull(_hSM.getAttribute("attribut"), "il n'y a plus d'attribut nommé 'attribut'");

		_hSM.putValue("attribut", v_newObject);
		_hSM.removeValue("attribut");
	}

	/**
	 * Test de la méthode 'getValueNames'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testGetValueNames() throws Throwable {
		assertNull(_hSM.getValueNames(), "retourne null");
	}

	/**
	 * Test de la méthode 'isNew'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testIsNew() throws Throwable {
		assertFalse(_hSM.isNew(), "retourne false");
	}

	/**
	 * Test de la méthode 'getValue'
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testGetValue() throws Throwable {
		assertNull(_hSM.getValue("test"), "retourne null");
	}

}
