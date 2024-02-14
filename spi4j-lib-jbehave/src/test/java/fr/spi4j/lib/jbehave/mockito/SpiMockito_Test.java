/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import fr.spi4j.lib.jbehave.mockito.SpiMockito_LoginSample_Test.SampleConvention1;
import fr.spi4j.ui.HasString_Itf;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Classe de test de la classe SpiMockito.
 * 
 * @author MINARM
 */
public class SpiMockito_Test {

	/**
	 * Interface d'une vue de Login
	 * 
	 * @author MINARM
	 */
	public interface Login_Itf extends View_Itf {
		/**
		 * @return login
		 */
		public String get_login();

		/**
		 * Affecte login.
		 * 
		 * @param p_login login
		 */
		public void set_login(String p_login);

		/**
		 * @return password
		 */
		public String get_password();

		/**
		 * Affecte password.
		 * 
		 * @param p_password password
		 */
		public void set_password(String p_password);

		/**
		 * @return interface de widget title
		 */
		public HasString_Itf get_title();
	}

	/**
	 * Test de la vue mockée en bean.
	 */
	@Test
	public void testLoginPopupJavaBeanMock() {
		final Login_Itf v_sample = SpiMockito.mockBean(Login_Itf.class);
		assertNull(v_sample.get_login(), "Valeur non présente pour le moment");
		v_sample.set_login("toto");
		assertEquals("toto", v_sample.get_login(), "Valeur présente");
	}

	/**
	 * Test du widget HasString title mocké.
	 */
	@Test
	public void testSmartJavaBeanNullsReturnsJavaBeanForNullNonPrimitives() {
		final Login_Itf v_sample = SpiMockito.mockBean(Login_Itf.class, true);
		assertNotNull(v_sample.get_title(), "Un titre a été initialisé");
		final HasString_Itf v_title = v_sample.get_title();
		assertNull(v_title.getValue(), "Valeur non présente pour le moment");
		v_title.setValue("Mon titre");
		assertEquals("Mon titre", v_title.getValue(), "Une valeur a été mise dans le titre");
	}

	/**
	 * Test d'une vue mockée en bean n'a pas de valeurs dans ses widgets.
	 */
	@Test
	public void testSmartJavaBeanNullsReturnsNullForNullPrimitives() {
		final Login_Itf v_sample = SpiMockito.mockBean(Login_Itf.class, true);
		assertNull(v_sample.get_login(), "Valeur non présente");
	}

	/**
	 * Test vue mockée en JavaBeanView.
	 */
	@Test
	public void testJavaBeanView() {
		@SuppressWarnings("unchecked")
		final Presenter_Abs<Login_Itf, Object> v_presenter = Mockito.mock(Presenter_Abs.class);
		final Login_Itf v_sample = SpiMockito.mockViewBean(Login_Itf.class, v_presenter, true);
		assertNotNull(v_sample.get_title(), "Un titre a été initialisé");
		final HasString_Itf v_title = v_sample.get_title();
		assertNull(v_title.getValue(), "Valeur non présente pour le moment");
		v_title.setValue("Mon titre");
		assertEquals("Mon titre", v_title.getValue(), "La valeur a été insérée");

		// verify(v_presenter).fieldUpdated(v_title);
	}

	/**
	 * Test to String sur un bean mocké.
	 */
	@Test
	public void toStringShouldReturnJavaBeanWithClassName() {
		assertEquals("JavaBean mock for " + SampleConvention1.class.getName(),
				SpiMockito.mockBean(SampleConvention1.class).toString(), "Test toString");
	}
}
