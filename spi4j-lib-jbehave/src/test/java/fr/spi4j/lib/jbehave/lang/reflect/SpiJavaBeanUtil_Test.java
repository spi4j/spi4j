/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.lang.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Classe de tests de la classe utilitaire {@link SpiJavaBeanUtil}.
 * 
 * @author MINARM
 */
public class SpiJavaBeanUtil_Test {

	/**
	 * Vérifie que la propriété est bien récupérée selon le nom de la méthode.
	 */
	@Test
	public void getPropertyName() {
		assertEquals("Name", SpiJavaBeanUtil.getPropertyName("getName"), "La propriété est incorrecte");
		assertEquals("Name", SpiJavaBeanUtil.getPropertyName("setName"), "La propriété est incorrecte");
		assertEquals("name", SpiJavaBeanUtil.getPropertyName("get_name"), "La propriété est incorrecte");
		assertEquals("name", SpiJavaBeanUtil.getPropertyName("set_name"), "La propriété est incorrecte");
		assertEquals("Name", SpiJavaBeanUtil.getPropertyName("get_Name"), "La propriété est incorrecte");
		assertEquals("Name", SpiJavaBeanUtil.getPropertyName("set_Name"), "La propriété est incorrecte");
		assertEquals("name", SpiJavaBeanUtil.getPropertyName("is_name"), "La propriété est incorrecte");
		assertEquals("Name", SpiJavaBeanUtil.getPropertyName("is_Name"), "La propriété est incorrecte");
		assertEquals("Name", SpiJavaBeanUtil.getPropertyName("isName"), "La propriété est incorrecte");
	}

}
