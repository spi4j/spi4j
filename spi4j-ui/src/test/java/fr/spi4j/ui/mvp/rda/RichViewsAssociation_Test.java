/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp.rda;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ui.Sample.HumainDto;
import fr.spi4j.ui.Sample.NoView;
import fr.spi4j.ui.Sample.SamplePresenter;
import fr.spi4j.ui.Sample.SamplePresenterWithoutIdGenerator;
import fr.spi4j.ui.Sample.SampleViewsAssociation;
import fr.spi4j.ui.graal.UserView;
import fr.spi4j.ui.mvp.MVPUtils;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.ViewManager;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Classe de tests de la classe {@link RichViewsAssociation}.
 * 
 * @author MINARM
 */
public class RichViewsAssociation_Test {

	private SampleViewsAssociation2 _association;

	/**
	 * Initialisation du test.
	 */
	@BeforeEach
	public void setUp() {
		_association = new SampleViewsAssociation2();
		MVPUtils.setViewManager(new ViewManager());
		MVPUtils.getInstance().getViewManager().setViewsAssociation(_association);
	}

	/**
	 * Actions à effectuer après chaque méthode de test.
	 */
	@AfterEach
	public void tearDown() {
		// fermeture de tous les présenteurs
		Collection<Presenter_Abs<?, ?>> v_presenters = MVPUtils.getInstance().getViewManager().getActivePresenters();
		while (!v_presenters.isEmpty()) {
			v_presenters.iterator().next().close();
			v_presenters = MVPUtils.getInstance().getViewManager().getActivePresenters();
		}
	}

	/**
	 * Test du cas nominal.
	 */
	@Test
	public void testNominal() {
		assertNotNull(_association.getViewForPresenter(new SamplePresenterWithoutIdGenerator(null, new HumainDto())),
				"La vue retournée aurait du être une NoView");
	}

	/**
	 * Test du cas avec présenteur inconnu.
	 */
	@Test
	public void testPresenteurInconnu() {
		try {
			final SamplePresenterExtended v_presenterInstance = new SamplePresenterExtended(null);
			assertNotNull(v_presenterInstance, "Une erreur aurait du survenir pour ce présenteur inconnu");
			fail("Une erreur aurait du survenir pour ce présenteur inconnu");
		} catch (final Spi4jRuntimeException v_e) {
			assertTrue(true, "Erreur attendue");
		}
	}

	/**
	 * Test des annotations Graal.
	 */
	@Test
	public void testAnnotationUserView() {
		final Presenter_Abs<?, ?> v_presenter = _association.getPresenterForAnnotatedView("NoView");
		assertTrue(v_presenter instanceof SamplePresenter || v_presenter instanceof SamplePresenterWithoutIdGenerator,
				"Le présenteur trouvé devrait être SamplePresenter ou SamplePresenterWithoutIdGenerator");
	}

	/**
	 * Test d'erreur pour annotations Graal.
	 */
	@Test
	public void testAnnotationEnDeuxiemePosition() {
		_association.addAssociation(SamplePresenterExtended.class, NoViewExtended.class);
		assertTrue(_association.getPresenterForAnnotatedView("NoViewExtended") instanceof SamplePresenterExtended,
				"Le présenteur trouvé devrait être SamplePresenterExtended");
	}

	/**
	 * Test d'erreur pour annotations Graal.
	 */
	@Test
	public void testAnnotationUserViewInconnue() {
		try {
			_association.getPresenterForAnnotatedView("UnknownView");
			fail("Aucun présenteur n'aurait du être trouvé pour la UserView UnknownView");
		} catch (final Spi4jRuntimeException v_e) {
			assertTrue(true, "Erreur attendue");
		}
	}

	/**
	 * SampleViewsAssociation2.
	 * 
	 * @author MINARM
	 */
	public static class SampleViewsAssociation2 extends SampleViewsAssociation {
		/**
		 * Constructeur.
		 */
		public SampleViewsAssociation2() {
			addAssociation(SamplePresenterWithoutIdGenerator.class, NoView.class);
		}

		@Override
		public <TypeView extends View_Itf> void addAssociation(
				final Class<? extends Presenter_Abs<TypeView, ?>> p_presenterClass,
				final Class<? extends TypeView> p_viewClass) {
			super.addAssociation(p_presenterClass, p_viewClass);
		}
	}

	/**
	 * Présenteur hérité.
	 * 
	 * @author MINARM
	 */
	public static class SamplePresenterExtended extends SamplePresenterWithoutIdGenerator {

		/**
		 * Constructeur par défaut. Constructeur utilisé par introspection dans les
		 * tests unitaires.
		 */
		public SamplePresenterExtended() {
			this(null);
		}

		/**
		 * Constructeur parent.
		 * 
		 * @param p_parent le présenteur parent
		 */
		public SamplePresenterExtended(final Presenter_Abs<? extends View_Itf, ?> p_parent) {
			super(p_parent);
		}
	}

	/**
	 * Vue héritée.
	 * 
	 * @author MINARM
	 */
	@UserView("NoViewExtended")
	public static class NoViewExtended extends NoView {
		// RAS
	}
}
