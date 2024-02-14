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
import fr.spi4j.ui.SampleEntity.HumainEntity;
import fr.spi4j.ui.SampleEntity.HumainEntity_Itf;
import fr.spi4j.ui.SampleEntity.SamplePresenterEntity;
import fr.spi4j.ui.SampleEntity.SamplePresenterEntityWithBuggedIdGenerator;
import fr.spi4j.ui.SampleEntity.SamplePresenterEntityWithNonStaticIdGenerator;
import fr.spi4j.ui.SampleEntity.SamplePresenterEntityWithPrivateIdGenerator;
import fr.spi4j.ui.SampleEntity.SamplePresenterEntityWithoutIdGenerator;
import fr.spi4j.ui.SampleEntity.SampleViewsEntityAssociation;
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
public class RichViewEntityManager_Test {

	/**
	 * Initialisation des tests.
	 */
	@BeforeEach
	public void setUp() {
		MVPUtils.setViewManager(new RichViewManager());
		MVPUtils.getInstance().getViewManager().setViewsAssociation(new SampleViewsEntityAssociation());
	}

	/**
	 * Test de génération d'un id pour le présenteur avec un bon paramètre.
	 */
	@Test
	public void testGetPresenterIdOneParameter() {
		final HumainEntity_Itf v_entity = new HumainEntity();
		v_entity.setId(1L);
		assertEquals(1L, ((RichViewManager) MVPUtils.getInstance().getViewManager())
				.getPresenterId(SamplePresenterEntity.class, v_entity), "Mauvais id généré");
	}

	/**
	 * Test de génération d'un id par défaut.
	 */
	@Test
	public void testGetDefaultPresenterId() {
		final HumainEntity_Itf v_entity = new HumainEntity();
		v_entity.setId(1L);
		assertEquals(MVPUtils.getInstance().getViewManager().getDefaultId(),
				((RichViewManager) MVPUtils.getInstance().getViewManager())
						.getPresenterId(SamplePresenterEntityWithoutIdGenerator.class, v_entity),
				"Mauvais id généré");
	}

	/**
	 * Test d'erreur de génération d'un id pour le présenteur avec générateur privé.
	 */
	@Test
	public void testGetPresenterIdPrivate() {
		final HumainEntity_Itf v_entity = new HumainEntity();
		v_entity.setId(1L);
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager())
					.getPresenterId(SamplePresenterEntityWithPrivateIdGenerator.class, v_entity);
			fail("Une exception était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals(
					"Le générateur d'id pour le présenteur "
							+ SamplePresenterEntityWithPrivateIdGenerator.class.getName() + " n'est pas accessible",
					v_erreur.getMessage(), "Générateur inaccessible");
		}
	}

	/**
	 * Test d'erreur de génération d'un id pour le présenteur avec un générateur
	 * buggé.
	 */
	@Test
	public void testGetPresenterIdBugged() {
		final HumainEntity_Itf v_entity = new HumainEntity();
		v_entity.setId(1L);
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager())
					.getPresenterId(SamplePresenterEntityWithBuggedIdGenerator.class, v_entity);
			fail("Une exception était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals(
					"Erreur lors de la génération d'id du présenteur "
							+ SamplePresenterEntityWithBuggedIdGenerator.class.getName(),
					v_erreur.getMessage(), "Générateur inaccessible");
		}
	}

	/**
	 * Test d'erreur de génération d'un id pour le présenteur avec un générateur non
	 * static.
	 */
	@Test
	public void testGetPresenterIdNonStatic() {
		final HumainEntity_Itf v_entity = new HumainEntity();
		v_entity.setId(1L);
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager())
					.getPresenterId(SamplePresenterEntityWithNonStaticIdGenerator.class, v_entity);
			fail("Une exception était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals(
					"Le générateur d'id pour le présenteur "
							+ SamplePresenterEntityWithNonStaticIdGenerator.class.getName() + " n'est pas static",
					v_erreur.getMessage(), "Générateur inaccessible");
		}
	}

	/**
	 * Test d'erreur de génération d'un id pour le présenteur sans paramètres.
	 */
	@Test
	public void testGetPresenterMissingParameter() {
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager()).getPresenterId(SamplePresenterEntity.class);
			fail("Une exception était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals(
					"Aucun générateur d'id trouvé dans le présenteur " + SamplePresenterEntity.class.getName()
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
			((RichViewManager) MVPUtils.getInstance().getViewManager()).getPresenterId(SamplePresenterEntity.class,
					"toto");
			fail("L'exception IllegalArgumentException était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals(
					"Aucun générateur d'id trouvé dans le présenteur " + SamplePresenterEntity.class.getName()
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
		final HumainEntity_Itf v_entity = new HumainEntity();
		v_entity.setId(1L);
		try {
			((RichViewManager) MVPUtils.getInstance().getViewManager()).getPresenterId(SamplePresenterEntity.class,
					v_entity, "toto");
			fail("L'exception IllegalArgumentException était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals(
					"Aucun générateur d'id trouvé dans le présenteur " + SamplePresenterEntity.class.getName()
							+ " pour les paramètres [HumainEntity, String]",
					v_erreur.getMessage(), "Mauvais paramètres pour le générateur");
		}
	}

	/**
	 * Test de récupération d'un présenteur.
	 */
	@Test
	public void testGetPresenter() {
		final HumainEntity_Itf v_entity = new HumainEntity();
		v_entity.setId(1L);
		final SamplePresenterEntity v_presenter = new SamplePresenterEntity(null, v_entity);
		assertNotNull(((RichViewManager) MVPUtils.getInstance().getViewManager())
				.getPresenter(SamplePresenterEntity.class, v_entity), "Presenter non trouvé");
		assertEquals(v_presenter, ((RichViewManager) MVPUtils.getInstance().getViewManager())
				.getPresenter(SamplePresenterEntity.class, v_entity), "Presenter non trouvé");
	}

	/**
	 * Test de création d'un présenteur déjà instancié.
	 */
	@Test
	public void testRegisterAlreadyRegisteredPresenter() {
		final HumainEntity_Itf v_entity = new HumainEntity();
		v_entity.setId(1L);
		@SuppressWarnings("unused")
		final SamplePresenterEntity v_presenter = new SamplePresenterEntity(null, v_entity);
		try {
			@SuppressWarnings("unused")
			final SamplePresenterEntity v_presenter2 = new SamplePresenterEntity(null, v_entity);
			fail("L'exception ViewAlreadyRegisteredException était attendue");
		} catch (final ViewAlreadyRegisteredException v_erreur) {
			assertEquals(

					"Un présenteur avec cet identifiant existe déjà dans le ViewManager : "
							+ SamplePresenterEntity.class.getName() + '/'
							+ ((RichViewManager) MVPUtils.getInstance().getViewManager())
									.getPresenterId(SamplePresenterEntity.class, v_entity),
					v_erreur.getMessage(), "Présenteur déjà existant");
		}
	}

	/**
	 * Test d'erreur lorsque aucune association de vues n'a été définie.
	 */
	@Test
	public void testNoViewsAssociation() {
		MVPUtils.setViewManager(new ViewManager());
		final HumainEntity_Itf v_entity = new HumainEntity();
		v_entity.setId(1L);
		try {
			@SuppressWarnings("unused")
			final SamplePresenterEntity v_presenter = new SamplePresenterEntity(null, v_entity);
			fail("Une exception était attendue");
		} catch (final Spi4jRuntimeException v_erreur) {
			assertEquals("Aucune vue associée au présenteur " + SamplePresenterEntity.class.getName(),
					v_erreur.getMessage(), "Aucune association de vues");
		}
	}

	/**
	 * Test de désenregistrement d'un présenteur lorsqu'il a un parent.
	 */
	@Test
	public void testUnregisterWithParent() {
		final HumainEntity_Itf v_entity = new HumainEntity();
		v_entity.setId(1L);
		final SamplePresenterEntity v_presenterParent = new SamplePresenterEntity(null, v_entity);
		final SamplePresenterEntityWithoutIdGenerator v_presenterFils = new SamplePresenterEntityWithoutIdGenerator(
				v_presenterParent);
		MVPUtils.getInstance().getViewManager().unregisterPresenter(v_presenterFils, true);
	}

	/**
	 * Test de désenregistrement d'un présenteur lorsqu'il a un parent.
	 */
	// @Test
	public void testOpenViewWithGraal() {
		((RichViewManager) MVPUtils.getInstance().getViewManager()).openView("NoViewEntity");
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
