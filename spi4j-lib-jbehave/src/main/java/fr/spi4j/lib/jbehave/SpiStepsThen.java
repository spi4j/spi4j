/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jbehave.core.annotations.Then;

import fr.spi4j.lib.jbehave.graal.FieldNotFoundGraalException;
import fr.spi4j.lib.jbehave.graal.UserViewNotFoundGraalException;
import fr.spi4j.lib.jbehave.requirement.RequirementContext;
import fr.spi4j.requirement.Requirement_Itf;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Liste des steps "Then".
 * 
 * @author MINARM
 */
public class SpiStepsThen extends SpiSteps_Abs {

	/**
	 * Construit une classe de steps.
	 * 
	 * @param p_story la story en cours
	 */
	public SpiStepsThen(final SpiStory_Abs p_story) {
		super(p_story);
	}

	/**
	 * Vérifie que l'exigence a été testée.
	 * 
	 * @param p_req l'exigence
	 */
	@Then("l'exigence \"$exigence\" a été testée")
	public void requirementHasBeenUsed(final String p_req) {
		if (get_story().shouldCheckRequirements()) {
			final Requirement_Itf v_req = get_story().findRequirement(p_req);
			assertNotNull(v_req, "Exigence inconnue : " + p_req);

			final RequirementContext v_context = SpiStoryContextHandler.get_currentRequirementContext();
			assertNotNull(v_context, "Le contexte d'enregistrement des exigences n'a pas été initialisé");

			assertTrue(v_context.hasBeenUsed(v_req), "L'exigence " + p_req + " n'a pas été testée");
		}
	}

	/**
	 * Vérifie qu'une vue est affichée.
	 * 
	 * Ce step liste les vues actives et vérifie qu’il y en a une dont l’interface
	 * porte l’annotation <code>@UserView(name)</code>. Si aucune interface parmi
	 * les vues actives ne porte cette annotation, le step est considéré comme dans
	 * l’état FAILED.
	 * 
	 * @param p_name le nom de la vue affichée attendu
	 */
	@Then("la vue \"$name\" est active")
	public void viewIsActive(final String p_name) {
		final List<String> v_activeUserViewNames = new ArrayList<>();
		boolean v_found = false;
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				final String v_userViewName = get_graalUtil().getUserViewName(v_activePresenter);
				v_activeUserViewNames.add(v_userViewName);
				if (v_userViewName.equals(p_name)) {
					v_found = true;
					break;
				}
			} catch (final UserViewNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		assertTrue(v_found, "La vue " + p_name + " aurait dû être active. Les vues suivantes sont actives : "
				+ v_activeUserViewNames);
	}

	/**
	 * Vérifie le titre de la vue active.
	 * 
	 * Ce step liste les vues actives et vérifie qu’il y en a une qui porte le bon
	 * titre. Si aucune vue parmi les vues actives ne porte le bon titre, le step
	 * est considéré comme dans l’état FAILED.
	 * 
	 * @param p_title le titre de la vue active attendu
	 */
	@Then("le titre de la vue active est \"$title\"")
	public void activeViewTitleIs(final String p_title) {

		final List<String> v_activeTitles = new ArrayList<>();
		boolean v_found = false;
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			final String v_title = v_activePresenter.getView().getTitle();
			v_activeTitles.add(v_title);
			if (v_title.equals(p_title)) {
				v_found = true;
				break;
			}
		}
		assertTrue(v_found, "La vue active aurait dû avoir pour titre " + p_title
				+ ". Les vues suivantes sont actives : " + v_activeTitles);
	}

	/**
	 * Vérifie qu'une liste contient une certaine valeur.
	 * 
	 * Ce step vérifie qu’une liste contient la valeur souhaitée. Il va tout d’abord
	 * rechercher parmi les vues actives celle qui possède la liste souhaitée. Cela
	 * correspond à un getter dans l’interface de vue qui doit être annoté avec
	 * <code>@Field(liste)</code>.
	 * 
	 * Ce champ doit être une liste, donc doit être typé
	 * <code>HasSelection_Itf<…></code>. Une fois le champ trouvé, on parcourt les
	 * valeurs possibles dans la liste et on vérifie que le paramètre du step en
	 * fait partie (en utilisant la méthode toString()). Si la liste n’a pas été
	 * trouvée parmi les vues actives ou si la liste ne contient pas la valeur
	 * attendue, le step est considéré comme dans l’état FAILED.
	 * 
	 * @param p_liste  la liste
	 * @param p_valeur la valeur
	 */
	@Then("la liste \"$liste\" contient la valeur \"$valeur\"")
	public void listContainsValue(final String p_liste, final String p_valeur) {
		listContains(p_liste, p_valeur);
	}

	/**
	 * Vérifie qu'une liste contient certaines valeurs.
	 * 
	 * Ce step est similaire au précédent, mais il permet en plus de vérifier qu’une
	 * liste contient plusieurs valeurs. Les valeurs doivent être séparées par un
	 * point virgule (;).
	 * 
	 * @param p_liste   la liste
	 * @param p_valeurs les valeurs
	 */
	@Then("la liste \"$liste\" contient les valeurs \"$valeurs\"")
	public void listContainsValues(final String p_liste, final String p_valeurs) {
		listContains(p_liste, (Object[]) splitParameter(p_valeurs));
	}

	/**
	 * Vérifie qu'une liste contient une certaine valeur.
	 * 
	 * @param p_liste       la liste
	 * @param p_identifiant l'identifiant de l'objet
	 */
	@Then("la liste \"$liste\" contient l'objet \"$identifiant\"")
	public void listContainsObject(final String p_liste, final String p_identifiant) {
		listContains(p_liste, get_story().getData(p_identifiant));
	}

	/**
	 * Vérifie qu'une liste contient certaines valeurs.
	 * 
	 * @param p_liste        la liste
	 * @param p_identifiants les identifiants des objets
	 */
	@Then("la liste \"$liste\" contient les objets \"$valeurs\"")
	public void listContainsObjects(final String p_liste, final String p_identifiants) {
		final String[] v_dataKeys = splitParameter(p_identifiants);
		final Object[] v_objects = new Object[v_dataKeys.length];
		for (int v_i = 0; v_i < v_dataKeys.length; v_i++) {
			v_objects[v_i] = get_story().getData(v_dataKeys[v_i]);
		}
		listContains(p_liste, v_objects);
	}

	/**
	 * Vérifie qu'une liste contient certaines valeurs.
	 * 
	 * @param p_liste   la liste
	 * @param p_objects les valeurs
	 */
	private void listContains(final String p_liste, final Object... p_objects) {
		boolean v_testDone = false;
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				assertTrue(get_graalUtil().listContainsObjects(v_activePresenter, p_liste, p_objects),
						"La liste " + p_liste + " devrait contenir les valeurs " + Arrays.toString(p_objects));
				v_testDone = true;
				break;
			} catch (final FieldNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		assertTrue(v_testDone,
				"La liste " + p_liste + " n'a pas été trouvée dans les présenteurs actifs " + getActivePresenters());
	}

	/**
	 * Vérifie qu'une liste ne contient pas une certaine valeur.
	 * 
	 * Ce step est l’opposé des deux précédents et sert à vérifier qu’une liste ne
	 * contient pas une certaine valeur.
	 * 
	 * @param p_liste  la liste
	 * @param p_valeur la valeur
	 */
	@Then("la liste \"$liste\" ne contient pas la valeur \"$valeur\"")
	public void listDoesntContainValue(final String p_liste, final String p_valeur) {
		listDoesntContain(p_liste, p_valeur);
	}

	/**
	 * Vérifie qu'une liste ne contient pas un certain objet enregistré dans la
	 * story.
	 * 
	 * @param p_liste       la liste
	 * @param p_identifiant l'identifiant de l'objet
	 */
	@Then("la liste \"$liste\" ne contient pas l'objet \"$identifiant\"")
	public void listDoesntContainObject(final String p_liste, final String p_identifiant) {
		final Object v_data = get_story().getData(p_identifiant);
		listDoesntContain(p_liste, v_data);
	}

	/**
	 * Vérifie qu'une liste contient certaines valeurs.
	 * 
	 * @param p_liste   la liste
	 * @param p_valeurs les valeurs
	 */
	@Then("la liste \"$liste\" ne contient pas les valeurs \"$valeurs\"")
	public void listDoesntContainValues(final String p_liste, final String p_valeurs) {
		listDoesntContain(p_liste, (Object[]) splitParameter(p_valeurs));
	}

	/**
	 * Vérifie qu'une liste contient certaines valeurs.
	 * 
	 * @param p_liste        la liste
	 * @param p_identifiants les identifiants des objets
	 */
	@Then("la liste \"$liste\" ne contient pas les objets \"$valeurs\"")
	public void listDoesntContainObjects(final String p_liste, final String p_identifiants) {
		final String[] v_dataKeys = splitParameter(p_identifiants);
		final Object[] v_objects = new Object[v_dataKeys.length];
		for (int v_i = 0; v_i < v_dataKeys.length; v_i++) {
			v_objects[v_i] = get_story().getData(v_dataKeys[v_i]);
		}
		listDoesntContain(p_liste, v_objects);
	}

	/**
	 * Vérifie qu'une liste ne contient pas une valeur.
	 * 
	 * @param p_liste   la liste
	 * @param p_objects les objets
	 */
	private void listDoesntContain(final String p_liste, final Object... p_objects) {
		boolean v_testDone = false;
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				assertFalse(

						get_graalUtil().listContainsObjects(v_activePresenter, p_liste, p_objects),
						"La liste " + p_liste + " ne devrait pas contenir les valeurs " + Arrays.toString(p_objects));
				v_testDone = true;
				break;
			} catch (final FieldNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		assertTrue(v_testDone,
				"La liste " + p_liste + " n'a pas été trouvée dans les présenteurs actifs " + getActivePresenters());
	}

	/**
	 * Vérifie qu'un champ contient une certaine valeur.
	 * 
	 * Ce step vérifie qu’un champ contient une certaine valeur. Il va tout d’abord
	 * rechercher parmi les vues actives celle qui possède le champ souhaité.
	 * 
	 * Cela correspond à un getter dans l’interface de vue qui doit être annoté
	 * <code>@Field(champ)</code>. Ce champ doit être un champ de saisie (texte,
	 * date, nombre, checkbox, liste, …) et doit donc être typé HasValue<…>.
	 * 
	 * Une fois le champ trouvé, on vérifie que sa valeur correspond au paramètre du
	 * step (en utilisant la méthode toString()). Si le champ n’a pas été trouvé
	 * parmi les vues actives ou si la valeur du champ n’est pas celle attendue, le
	 * step est considéré comme dans l’était FAILED.
	 * 
	 * @param p_champ  le champ
	 * @param p_valeur la valeur
	 */
	@Then("le champ \"$champ\" contient la valeur \"$valeur\"")
	public void fieldValueIs(final String p_champ, final String p_valeur) {
		boolean v_testDone = false;
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				final Object v_value = get_graalUtil().getFieldValue(v_activePresenter, p_champ);
				if (v_value == null) {
					if (p_valeur != null) {
						fail("Le champ " + p_champ + " devrait contenir la valeur " + p_valeur
								+ " mais ne contient rien");
					}
				} else {
					assertTrue(get_graalUtil().doComparaison(p_valeur, v_value),
							"Le champ " + p_champ + " devrait contenir la valeur \"" + p_valeur
									+ "\" mais contient la valeur \"" + v_value + "\"");
				}
				v_testDone = true;
				break;
			} catch (final FieldNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		assertTrue(v_testDone,
				"Le champ " + p_champ + " n'a pas été trouvé dans les présenteurs actifs " + getActivePresenters());
	}

	/**
	 * Vérifie que la fenêtre active est modale.
	 */
	@Then("la vue active est modale")
	public void activeViewIsModal() {
		assertTrue(getActivePresenters().iterator().next().getView().isModal(), "La vue active devrait être modale");
	}

	/**
	 * Vérifie que la fenêtre active n'est pas modale.
	 */
	@Then("la vue active n'est pas modale")
	public void activeViewIsNotModal() {
		assertFalse(getActivePresenters().iterator().next().getView().isModal(),
				"La vue active ne devrait pas être modale");
	}

	/**
	 * @param p_champ Le champ dans à tester
	 */
	@Then("le champ \"$champ\" ne contient rien")
	public void fieldValueIsEmptyOrNull(final String p_champ) {
		boolean v_testDone = false;
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				final Object v_value = get_graalUtil().getFieldValue(v_activePresenter, p_champ);
				if (v_value != null) {
					if (v_value instanceof String) {
						assertTrue(((String) v_value).isEmpty(), "Le champ " + p_champ + " contient la valeur "
								+ v_value + " mais ne devrait rien contenir");
					} else {
						fail("Le champ " + p_champ + " contient la valeur " + v_value
								+ " mais ne devrait rien contenir");
					}
				}
				v_testDone = true;
				break;
			} catch (final FieldNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		assertTrue(v_testDone,
				"Le champ " + p_champ + " n'a pas été trouvé dans les présenteurs actifs " + getActivePresenters());
	}

	/**
	 * Vérifie qu'une liste contient la valeur vide.
	 * 
	 * @param p_liste la liste
	 */
	@Then("la liste \"$liste\" contient la valeur vide")
	public void listContainsNoValue(final String p_liste) {
		boolean v_testDone = false;
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				assertTrue(get_graalUtil().listContainsNoValue(v_activePresenter, p_liste),
						"La liste " + p_liste + " devrait contenir la valeur vide");
				v_testDone = true;
				break;
			} catch (final FieldNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		assertTrue(v_testDone,
				"La liste " + p_liste + " n'a pas été trouvée dans les présenteurs actifs " + getActivePresenters());
	}
}
