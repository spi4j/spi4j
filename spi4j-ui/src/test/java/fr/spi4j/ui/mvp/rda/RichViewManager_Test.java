/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp.rda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ui.Sample.HumainDto;
import fr.spi4j.ui.Sample.SamplePresenter;
import fr.spi4j.ui.Sample.SamplePresenterWithBuggedIdGenerator;
import fr.spi4j.ui.Sample.SamplePresenterWithNonStaticIdGenerator;
import fr.spi4j.ui.Sample.SamplePresenterWithPrivateIdGenerator;
import fr.spi4j.ui.Sample.SamplePresenterWithoutIdGenerator;
import fr.spi4j.ui.Sample.SampleViewsAssociation;
import fr.spi4j.ui.mvp.MVPUtils;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.ViewAlreadyRegisteredException;
import fr.spi4j.ui.mvp.ViewManager;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Classe de tests unitaires pour le ViewManager.
 * 
 * @author MINARM
 */
public class RichViewManager_Test {

	/**
	 * Initialisation des tests.
	 */
	@BeforeEach
	public void setUp() {
		MVPUtils.setViewManager(new RichViewManager());
		MVPUtils.getInstance().getViewManager().setViewsAssociation(new SampleViewsAssociation());
	}

	/**
	 * Test de génération d'un id pour le présenteur avec un bon paramètre.
	 */
	@Test
	public void testGetPresenterIdOneParameter() {
		final HumainDto v_personneDto = new HumainDto();
		v_personneDto.setId(1L);
		assertEquals(1L, ((RichViewManager) MVPUtils.getInstance().getViewManager())
				.getPresenterId(SamplePresenter.class, v_personneDto), "Mauvais id généré");
	}

	/**
	 * Test de génération d'un id par défaut.
	 */
	@Test
	public void testGetDefaultPresenterId() {
		final HumainDto v_personneDto = new HumainDto();
		v_personneDto.setId(1L);
		assertEquals(MVPUtils.getInstance().getViewManager().getDefaultId(),
				((RichViewManager) MVPUtils.getInstance().getViewManager())
						.getPresenterId(SamplePresenterWithoutIdGenerator.class, v_personneDto),
				"Mauvais id généré");
	}

	/**
	 * Test d'erreur de génération d'un id pour le présenteur avec générateur privé.
	 */
	@Test
	public void testGetPresenterIdPrivate() {
		final HumainDto v_personneDto = new HumainDto();
		v_personneDto.setId(1L);
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager())
					.getPresenterId(SamplePresenterWithPrivateIdGenerator.class, v_personneDto);
			fail("Une exception était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals("Le générateur d'id pour le présenteur "
					+ SamplePresenterWithPrivateIdGenerator.class.getName() + " n'est pas accessible",
					v_erreur.getMessage(), "Générateur inaccessible");
		}
	}

	/**
	 * Test d'erreur de génération d'un id pour le présenteur avec un générateur
	 * buggé.
	 */
	@Test
	public void testGetPresenterIdBugged() {
		final HumainDto v_personneDto = new HumainDto();
		v_personneDto.setId(1L);
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager())
					.getPresenterId(SamplePresenterWithBuggedIdGenerator.class, v_personneDto);
			fail("Une exception était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals(
					"Erreur lors de la génération d'id du présenteur "
							+ SamplePresenterWithBuggedIdGenerator.class.getName(),
					v_erreur.getMessage(), "Générateur inaccessible");
		}
	}

	/**
	 * Test d'erreur de génération d'un id pour le présenteur avec un générateur non
	 * static.
	 */
	@Test
	public void testGetPresenterIdNonStatic() {
		final HumainDto v_personneDto = new HumainDto();
		v_personneDto.setId(1L);
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager())
					.getPresenterId(SamplePresenterWithNonStaticIdGenerator.class, v_personneDto);
			fail("Une exception était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals("Le générateur d'id pour le présenteur "
					+ SamplePresenterWithNonStaticIdGenerator.class.getName() + " n'est pas static",
					v_erreur.getMessage(), "Générateur inaccessible");
		}
	}

	/**
	 * Test d'erreur de génération d'un id pour le présenteur sans paramètres.
	 */
	@Test
	public void testGetPresenterMissingParameter() {
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager()).getPresenterId(SamplePresenter.class);
			fail("Une exception était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals(
					"Aucun générateur d'id trouvé dans le présenteur " + SamplePresenter.class.getName()
							+ " pour les paramètres []",
					v_erreur.getMessage(), "Mauvais paramètres pour le générateur");
		}
	}

	/**
	 * Test d'erreur de génération d'un id pour le présenteur avec un mauvais
	 * paramètre.
	 */
	@Test
	public void testGetPresenterWrongParameter() {
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager()).getPresenterId(SamplePresenter.class, "toto");
			fail("L'exception IllegalArgumentException était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals(
					"Aucun générateur d'id trouvé dans le présenteur " + SamplePresenter.class.getName()
							+ " pour les paramètres [String]",
					v_erreur.getMessage(), "Mauvais paramètres pour le générateur");
		}
	}

	/**
	 * Test d'erreur de génération d'un id pour le présenteur avec trop de
	 * paramètres.
	 */
	@Test
	public void testGetPresenterTooManyParameters() {
		final HumainDto v_personneDto = new HumainDto();
		v_personneDto.setId(1L);
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager()).getPresenterId(SamplePresenter.class,
					v_personneDto, "toto");
			fail("L'exception IllegalArgumentException était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals(
					"Aucun générateur d'id trouvé dans le présenteur " + SamplePresenter.class.getName()
							+ " pour les paramètres [HumainDto, String]",
					v_erreur.getMessage(), "Mauvais paramètres pour le générateur");
		}
	}

	/**
	 * Test de récupération d'un présenteur.
	 */
	@Test
	public void testGetPresenter() {
		final HumainDto v_personneDto = new HumainDto();
		v_personneDto.setId(1L);
		final SamplePresenter v_presenter = new SamplePresenter(null, v_personneDto);
		assertNotNull(((RichViewManager) MVPUtils.getInstance().getViewManager()).getPresenter(SamplePresenter.class,
				v_personneDto), "Presenter non trouvé");
		assertEquals(v_presenter, ((RichViewManager) MVPUtils.getInstance().getViewManager())
				.getPresenter(SamplePresenter.class, v_personneDto), "Presenter non trouvé");
	}

	/**
	 * Test de création d'un présenteur déjà instancié.
	 */
	@Test
	public void testRegisterAlreadyRegisteredPresenter() {
		final HumainDto v_personneDto = new HumainDto();
		v_personneDto.setId(1L);
		@SuppressWarnings("unused")
		final SamplePresenter v_presenter = new SamplePresenter(null, v_personneDto);
		try {
			@SuppressWarnings("unused")
			final SamplePresenter v_presenter2 = new SamplePresenter(null, v_personneDto);
			fail("L'exception ViewAlreadyRegisteredException était attendue");
		} catch (final ViewAlreadyRegisteredException v_erreur) {
			assertEquals(
					"Un présenteur avec cet identifiant existe déjà dans le ViewManager : "
							+ SamplePresenter.class.getName() + '/'
							+ ((RichViewManager) MVPUtils.getInstance().getViewManager())
									.getPresenterId(SamplePresenter.class, v_personneDto),
					v_erreur.getMessage(), "Présenteur déjà existant");
		}
	}

	/**
	 * Test d'erreur lorsque aucune association de vues n'a été définie.
	 */
	@Test
	public void testNoViewsAssociation() {
		MVPUtils.setViewManager(new ViewManager());
		final HumainDto v_personneDto = new HumainDto();
		v_personneDto.setId(1L);
		try {
			@SuppressWarnings("unused")
			final SamplePresenter v_presenter = new SamplePresenter(null, v_personneDto);
			fail("Une exception était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals("Aucune vue associée au présenteur " + SamplePresenter.class.getName(), v_erreur.getMessage(),
					"Aucune association de vues");
		}
	}

	/**
	 * Test de désenregistrement d'un présenteur lorsqu'il a un parent.
	 */
	@Test
	public void testUnregisterWithParent() {
		final HumainDto v_personneDto = new HumainDto();
		v_personneDto.setId(1L);
		final SamplePresenter v_presenterParent = new SamplePresenter(null, v_personneDto);
		final SamplePresenterWithoutIdGenerator v_presenterFils = new SamplePresenterWithoutIdGenerator(
				v_presenterParent);
		MVPUtils.getInstance().getViewManager().unregisterPresenter(v_presenterFils, true);
	}

	/**
	 * Test de désenregistrement d'un présenteur lorsqu'il a un parent.
	 */
	@Test
	public void testOpenViewWithGraal() {
		((RichViewManager) MVPUtils.getInstance().getViewManager()).openView("NoView");
	}

	/**
	 * Finalisation d'un test : vider le ViewManager.
	 */
	@AfterEach
	public void tearDown() {
		Collection<Presenter_Abs<? extends View_Itf, ?>> v_presenters = MVPUtils.getInstance().getViewManager()
				.getActivePresenters();
		while (!v_presenters.isEmpty()) {
			v_presenters.iterator().next().close();
			v_presenters = MVPUtils.getInstance().getViewManager().getActivePresenters();
		}
	}

}
