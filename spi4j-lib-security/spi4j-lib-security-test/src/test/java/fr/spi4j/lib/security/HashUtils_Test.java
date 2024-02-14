/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.lib.security.tools.HashUtils;

/**
 * Test de la classe HashUtils.
 * 
 * @author MINARM
 */
public class HashUtils_Test {
	/**
	 * Test de la méthode 'hashText' avec l'algo md5
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testHashTextWithMD5() throws Throwable {
		final String v_hashMD5 = HashUtils.hashText("test", HashUtils.c_md5);
		final String v_testHashMD5 = "098f6bcd4621d373cade4e832627b4f6";
		assertEquals(v_testHashMD5, v_hashMD5, "Les hashs devraient être identiques");
	}

	/**
	 * Test de la méthode 'hashText' avec l'algo sha-1
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testHashTextWithSHA1() throws Throwable {
		final String v_hashSHA1 = HashUtils.hashText("test", HashUtils.c_sha1);
		final String v_testHashSHA1 = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3";
		assertEquals(v_testHashSHA1, v_hashSHA1, "Les hashs devraient être identiques");
	}

	/**
	 * Test de la méthode 'hashText' avec l'algo sha-256
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testHashTextWithSHA256() throws Throwable {
		final String v_hashSHA256 = HashUtils.hashText("test", HashUtils.c_sha256);
		final String v_testHashSHA256 = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";
		assertEquals(v_testHashSHA256, v_hashSHA256, "Les hashs devraient être identiques");
	}

	/**
	 * Test de la méthode 'hashText' avec un algo inconnu
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testHashText() throws Throwable {
		assertThrows(Spi4jRuntimeException.class, () -> {
			HashUtils.hashText("test", "algo");
			fail("Algo inconnu");
		});

	}

	/**
	 * Test de la méthode 'hashText' avec un algo = null
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testHashTextWithoutAnyAlgo() throws Throwable {
		assertThrows(Spi4jRuntimeException.class, () -> {
			HashUtils.hashText("test", null);
			fail("Algo non défini");
		});
	}

	/**
	 * Test de la méthode 'hashText' avec un string = null
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testHashTextWithNullText() throws Throwable {
		assertThrows(IllegalArgumentException.class, () -> {
			HashUtils.hashText(null, HashUtils.c_md5);
			fail("Aucun texte à hasher");
		});
	}

	/**
	 * Test de la méthode 'hashText' avec une chaîne vide + md5
	 * 
	 * @throws Throwable exception
	 */
	@Test
	public void testHashTextWithoutText() throws Throwable {
		// HashUtils.hashText("", HashUtils.c_md5);
		assertNotNull(HashUtils.hashText("", HashUtils.c_md5));
	}
}
