package fr.spi4j.ui.mvp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ui.Sample.Flow1;
import fr.spi4j.ui.Sample.NoView;
import fr.spi4j.ui.Sample.SamplePresenter;

/**
 * Tests de gestion des flows.
 * 
 * @author MINARM
 */
public class Flow_Test {

	/**
	 * Initialisation de la classe de tests.
	 */
	@BeforeEach
	public void setUp() {
		MVPUtils.setViewManager(new ViewManager());
		MVPUtils.getInstance().getViewManager().setViewsAssociation(new ViewsAssociation() {
			@SuppressWarnings("unchecked")
			@Override
			public <TypeView extends View_Itf> TypeView getViewForPresenter(
					final Presenter_Abs<TypeView, ?> p_presenter) {
				return (TypeView) new NoView();
			}

			@Override
			public Presenter_Abs<? extends View_Itf, ?> getPresenterForAnnotatedView(final String p_userView) {
				return null;
			}
		});
	}

	/**
	 * Test de désenregistrer un flow qui a déjà été terminé.
	 */
	@Test
	public void testUnregisterFlow2Fois() {
		assertThrows(Spi4jRuntimeException.class, () -> {
			final Flow1 v_flow1 = new Flow1();
			v_flow1.start();
			v_flow1.stop();
			// exception attendue
			v_flow1.abort();
			fail("Exception attendue");
		});
	}

	/**
	 * Test d'enregitrer un présenteur sans flow
	 */
	@Test
	public void testRegisterPresenterSansFlow() {
		assertThrows(Spi4jRuntimeException.class, () -> {
			final Flow1 v_flow1 = new Flow1();
			v_flow1.start();
			v_flow1.stop();
			// exception attendue
			@SuppressWarnings("unused")
			final SamplePresenter v_presenter = new SamplePresenter();
			fail("Exception attendue");
		});
	}

	/**
	 * Test d'enregitrer un présenteur sans flow
	 */
	@Test
	public void testRegisterPresenterSansFlowNormal() {
		// on attend pas d'excpetion car aucun flow n'est connu
		final SamplePresenter v_presenter = new SamplePresenter();
		assertNull(v_presenter.getCurrentFlowManager(), "le présenteur ne devrait pas avoir de flow");
	}

	/**
	 * Test d'enregitrer un présenteur sans flow
	 */
	@Test
	public void testRegisterPresenterAvecFlow() {
		final Flow1 v_flow1 = new Flow1();
		v_flow1.start();
		final SamplePresenter v_presenter = new SamplePresenter();
		assertNotNull(v_presenter.getCurrentFlowManager(), "le présenteur devrait avoir un flow");
		assertEquals(v_flow1, v_presenter.getCurrentFlowManager(), "le présenteur devrait avoir le bon flow");
		v_flow1.stop();
	}

}
