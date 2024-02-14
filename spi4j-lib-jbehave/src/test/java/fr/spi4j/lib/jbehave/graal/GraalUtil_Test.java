/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.graal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;

import fr.spi4j.lib.jbehave.SaveDtoProxy;
import fr.spi4j.lib.jbehave.SpiStoryContextHandler;
import fr.spi4j.lib.jbehave.Story_Test;
import fr.spi4j.lib.jbehave.graal.Sample.SamplePresenter;
import fr.spi4j.ui.mvp.MVPUtils;
import fr.spi4j.ui.mvp.rda.RichViewManager;

/**
 * Test de GraalUtil.
 * 
 * @author MINARM
 */
public class GraalUtil_Test extends GraalTest_Abs {

	/**
	 * Test setFieldValue.
	 */
	@Test
	public void testSetFieldValue() {
		// final SamplePresenter v_presenter = new SamplePresenter(null);
		given.activateView("NoView");
		// assertNull("Le champ ne devrait pas avoir été renseigné",
		// v_presenter.getView().getChamp().getValue());
		then.fieldValueIs("Champ", null);
		then.fieldValueIs("Date", null);
		then.fieldValueIs("Int", null);
		then.fieldValueIs("Double", null);
		then.fieldValueIs("Boolean", null);

		// graalUtil.setFieldValue(v_presenter, GraalAnnotations.field("Champ"), "abc");
		when.userSetField("abc", "Champ");
		when.userSetField("01/01/2000", "Date");
		when.userSetField("3", "Int");
		when.userSetField("3754654.547", "Double");
		when.userSetField("1", "Boolean");
		// assertEquals("Le champ devrait avoir été renseigné", "abc",
		// graalUtil.getFieldValue(v_presenter, GraalAnnotations.field("Champ")));
		then.fieldValueIs("Champ", "abc");
		then.fieldValueIs("Date", "01/01/2000");
		then.fieldValueIs("Int", "3");
		then.fieldValueIs("Double", "3754654.547");
		then.fieldValueIs("Boolean", "true");
	}

	/**
	 * Test setSelectedValueInList.
	 */
	@Test
	public void testList() {
		// final SamplePresenter v_presenter = new SamplePresenter();
		given.activateView("NoView");
		// assertNotNull("Le liste devrait avoir des valeurs possibles",
		// v_presenter.getView().getListe().getList());
		then.listContainsValue("Liste", "abc");
		then.listContainsValues("Liste", "def;ghi");
		// assertNull("Le liste ne devrait pas avoir de valeur",
		// v_presenter.getView().getListe().getList());
		then.fieldValueIs("Liste", null);
		try {
			// graalUtil.setSelectedValueInList(v_presenter,
			// GraalAnnotations.field("Liste"), "ab");
			when.userChooseValueInList("ab", "Liste");
			fail("La liste n'aurait pas dû pouvoir recevoir la valeur ab");
		} catch (final AssertionError v_e) {
			assertNotNull(v_e, "Une erreur a dû être levée");
			final String v_valeursPossibles = Arrays.toString(new String[] { "abc", "def", "ghi" });
			assertTrue(v_e.getMessage()
					.startsWith("La liste \"Liste\" n'a pas pu être remplie avec la valeur ab (valeurs possibles : "
							+ v_valeursPossibles + ")"),
					"La valeur ab n'a pas pu être mise dans la liste");
		}
		// graalUtil.setSelectedValueInList(v_presenter,
		// GraalAnnotations.field("Liste"), "abc");
		when.userChooseValueInList("abc", "Liste");
		// assertEquals("La liste devrait avoir été renseignée", "abc",
		// v_presenter.getView().getListe().getValue());
		then.fieldValueIs("Liste", "abc");

		// assertFalse("La liste ne devrait pas contenir la valeur ab",
		// graalUtil.listContainsValue(v_presenter, GraalAnnotations.field("Liste"),
		// "ab"));
		then.listDoesntContainValue("Liste", "ab");
		then.listDoesntContainValues("Liste", "ab;cd");
	}

	/**
	 * Test UserView.
	 */
	@Test
	public void testUserView() {
		// final SamplePresenter v_presenter = new SamplePresenter();
		given.activateView("NoView");
		// assertEquals("La vue doit être NoView",
		// graalUtil.getUserViewName(v_presenter), "NoView");
		then.activeViewTitleIs("NoViewTitle");
		then.viewIsActive("NoView");
		then.activeViewIsNotModal();
		try {
			then.activeViewIsModal();
			fail("La vue ne devrait pas être modale");
		} catch (final AssertionError v_e) {
			assertNotNull(v_e, "La vue ne devrait pas être modale");
			assertTrue(v_e.getMessage().startsWith("La vue active devrait être modale"),
					"La vue ne devrait pas être modale");
		}
	}

	/**
	 * Test UserAction.
	 */
	@Test
	public void testUserAction() {
		// final SamplePresenter v_presenter = new SamplePresenter();
		given.activateView("NoView");

		final SamplePresenter v_presenter = ((RichViewManager) MVPUtils.getInstance().getViewManager())
				.getPresenter(SamplePresenter.class);
		assertFalse(v_presenter.isActionDone(), "L'action n'aurait pas dû être exécutée pour le moment");

		// graalUtil.callUserAction(v_presenter, GraalAnnotations.userAction("Action"));
		when.userClickButton("Action");
		assertTrue(v_presenter.isActionDone(), "L'action aurait dû être exécutée");
	}

	/**
	 * Test Given Save DTO.
	 */
	@Test
	public void testGivenSaveDto() {
		final MonService_Itf v_service = SaveDtoProxy.createProxy(new MonService());
		final Story_Test v_story = new Story_Test();
		SpiStoryContextHandler.start(v_story);
		setStory(v_story);

		assertNull(v_story.getData("mon objet"), "L'objet \"mon objet\"");

		given.nextIdentifiantWithTypeToSave("String", "mon objet");

		final String v_obj = "abc";
		v_service.save(v_obj);

		assertNotNull(v_story.getData("mon objet"), "L'objet \"mon objet\"");
		assertEquals(v_obj, v_story.getData("mon objet"), "L'objet \"mon objet\"");

		SpiStoryContextHandler.destroy();
	}

	/**
	 * Test chooseObjectInList.
	 */
	@Test
	public void testChooseObjectInList() {
		given.activateView("NoView");

		final MonService_Itf v_service = SaveDtoProxy.createProxy(new MonService());
		final Story_Test v_story = new Story_Test();
		SpiStoryContextHandler.start(v_story);
		setStory(v_story);

		assertNull(v_story.getData("mon objet"), "L'objet \"mon objet\"");

		given.nextIdentifiantWithTypeToSave("String", "mon objet");

		final String v_obj = "abc";
		v_service.save(v_obj);

		assertNotNull(v_story.getData("mon objet"), "L'objet \"mon objet\"");
		assertEquals(v_obj, v_story.getData("mon objet"), "L'objet \"mon objet\"");

		then.fieldValueIs("Liste", null);
		when.userChooseObjectInList("mon objet", "Liste");
		then.listContainsObject("Liste", "mon objet");
		then.listContainsObjects("Liste", "mon objet");

		given.nextIdentifiantWithTypeToSave("String", "objet inutilise");
		final String v_objInutilise = "vide";
		v_service.save(v_objInutilise);

		given.nextIdentifiantWithTypeToSave("String", "objet inutilise 2");
		final String v_objInutilise2 = "vide2";
		v_service.save(v_objInutilise2);

		then.listDoesntContainObjects("Liste", "objet inutilise;objet inutilise 2");

		SpiStoryContextHandler.destroy();
	}

	/**
	 * Test chooseObjectInListMulti.
	 */
	@Test
	public void testChooseObjectInListeMulti() {
		given.activateView("NoView");

		final MonService_Itf v_service = SaveDtoProxy.createProxy(new MonService());
		final Story_Test v_story = new Story_Test();
		SpiStoryContextHandler.start(v_story);
		setStory(v_story);

		assertNull(v_story.getData("mon objet"), "L'objet \"mon objet\"");

		given.nextIdentifiantWithTypeToSave("String", "mon objet");
		final String v_obj = "1";
		v_service.save(v_obj);

		given.nextIdentifiantWithTypeToSave("String", "mon objet 2");
		final String v_obj2 = "2";
		v_service.save(v_obj2);

		assertNotNull(v_story.getData("mon objet"), "L'objet \"mon objet\"");
		assertEquals(v_obj, v_story.getData("mon objet"), "L'objet \"mon objet\"");

		then.fieldValueIs("ListeMulti", null);
		try {
			when.userChooseListObjectsInList("mon objet;mon objet 2", "Liste");
		} catch (final GraalException v_e) {
			assertNotNull(v_e,
					"Il aurait du être impossible de sélectionner plusieurs valeurs dans une liste à sélection unique");
		}
		when.userChooseListObjectsInList("mon objet;mon objet 2", "ListeMulti");
		then.listContainsObject("ListeMulti", "mon objet");
		then.listContainsObjects("ListeMulti", "mon objet;mon objet 2");

		given.nextIdentifiantWithTypeToSave("String", "mon objet 3");
		final String v_objInutilise = "3";
		v_service.save(v_objInutilise);

		then.listDoesntContainObject("Liste", "mon objet 3");

		SpiStoryContextHandler.destroy();
	}

	/**
	 * Test setFieldWithRandomString.
	 */
	@Test
	public void testSetFieldWithRandomString() {
		given.activateView("NoView");
		then.fieldValueIs("Champ", null);
		when.userSetFieldWithRandomString(10, "Champ");
	}

	/**
	 * Test doComparaison.
	 */
	@Test
	public void testDoComparaison() {
		final GraalUtil v_graalUtil = new GraalUtil();
		final Date v_date = new Date();
		v_date.setTime(new GregorianCalendar(2012, 04, 26).getTimeInMillis());
		assertTrue(v_graalUtil.doComparaison("26/05/2012", v_date), "");
	}

	/**
	 * Interface de service de test.
	 * 
	 * @author MINARM
	 */
	public static interface MonService_Itf {
		/**
		 * Sauvegarde l'objet.
		 * 
		 * @param p_obj l'objet à sauvegarder
		 * @return l'objet sauvegardé
		 */
		Object save(Object p_obj);
	}

	/**
	 * Implémentation de service de test.
	 * 
	 * @author MINARM
	 */
	public static class MonService implements MonService_Itf {
		@Override
		public Object save(final Object p_obj) {
			return p_obj;
		}
	}

}
