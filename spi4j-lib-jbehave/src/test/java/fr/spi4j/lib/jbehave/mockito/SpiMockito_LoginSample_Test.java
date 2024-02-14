/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Classe de test d'un mock JavaBean
 * 
 * @author MINARM
 */
public class SpiMockito_LoginSample_Test {

	/**
	 * Interface de test 1.
	 * 
	 * @author MINARM
	 */
	public interface SampleConvention1 {
		/**
		 * @return name
		 */
		public String getName();

		/**
		 * Affecte name.
		 * 
		 * @param p_name name
		 */
		public void setName(String p_name);

		/**
		 * @return major
		 */
		public boolean isMajor();

		/**
		 * Affecte major.
		 * 
		 * @param p_major major
		 */
		public void setMajor(boolean p_major);

		/**
		 * @return minor
		 */
		public Boolean getMinor();

		/**
		 * Affecte minor.
		 * 
		 * @param p_minor minor
		 */
		public void setMinor(Boolean p_minor);
	}

	/**
	 * Interface de test 2.
	 * 
	 * @author MINARM
	 */
	public interface SampleConvention2 {
		/**
		 * @return name
		 */
		public String get_name();

		/**
		 * Affecte name.
		 * 
		 * @param p_name name
		 */
		public void set_name(String p_name);

		/**
		 * @return major
		 */
		public boolean is_major();

		/**
		 * Affecte major
		 * 
		 * @param p_major major.
		 */
		public void set_major(boolean p_major);

		/**
		 * @return minor
		 */
		public Boolean get_minor();

		/**
		 * Affecte minor.
		 * 
		 * @param p_minor minor
		 */
		public void set_minor(Boolean p_minor);

	}

	/**
	 * Interface de test 3. Mauvaise convention : noms pas correspondants dans le
	 * getter et le setter.
	 * 
	 * @author MINARM
	 */
	public interface SampleConvention3 {
		/**
		 * @return name
		 */
		public String get_name();

		/**
		 * Affecte name.
		 * 
		 * @param p_name name
		 */
		public void setName(String p_name);
	}

	/**
	 * Test de la convention 1.
	 */
	@Test
	public void testMockBeanConvention1() {
		final SampleConvention1 v_sample = SpiMockito.mockBean(SampleConvention1.class);
		assertNull(v_sample.getName(), "Valeur non affectée pour le moment");
		v_sample.setName("toto");
		assertEquals("toto", v_sample.getName(), "Valeur affectée");

		assertFalse(v_sample.isMajor(), "Valeur non affectée pour le moment");
		v_sample.setMajor(true);
		assertTrue(v_sample.isMajor(), "Valeur affectée");

		assertNull(v_sample.getMinor(), "Valeur non affectée pour le moment");
		v_sample.setMinor(true);
		assertTrue(v_sample.getMinor(), "Valeur affectée");

	}

	/**
	 * Test de la convention 2.
	 */
	@Test
	public void testMockBeanConvention2() {
		final SampleConvention2 v_sample = SpiMockito.mockBean(SampleConvention2.class);
		assertNull(v_sample.get_name(), "Valeur non affectée pour le moment");
		v_sample.set_name("toto");
		assertEquals("toto", v_sample.get_name(), "Valeur affectée");

		assertFalse(v_sample.is_major(), "Valeur non affectée pour le moment");
		v_sample.set_major(true);
		assertTrue(v_sample.is_major(), "Valeur affectée");

		assertNull(v_sample.get_minor(), "Valeur non affectée pour le moment");
		v_sample.set_minor(true);
		assertTrue(v_sample.get_minor(), "Valeur affectée");

	}

	/**
	 * Test de la convention 3
	 */
	@Test
	public void testMockBeanConvention3ReturnsNull() {
		final SampleConvention3 v_sample = SpiMockito.mockBean(SampleConvention3.class);
		assertNull(v_sample.get_name(), "Valeur non affectée pour le moment");
		v_sample.setName("toto");
		assertNull(v_sample.get_name(), "Valeur toujours pas affectée car mauvaise convention");
	}

}
