/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import java.util.Random;

import org.jbehave.core.annotations.When;

import fr.spi4j.lib.jbehave.graal.FieldNotFoundGraalException;
import fr.spi4j.lib.jbehave.graal.UserActionNotFoundGraalException;
import fr.spi4j.ui.graal.UserAction;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Liste des steps "When".
 * 
 * @author MINARM
 */
public class SpiStepsWhen extends SpiSteps_Abs {

	private final Random _random = new Random();

	/**
	 * Construit une classe de steps.
	 * 
	 * @param p_story la story en cours
	 */
	public SpiStepsWhen(final SpiStory_Abs p_story) {
		super(p_story);
	}

	/**
	 * Action de clic sur un bouton par l'utilisateur.
	 * 
	 * Ce step permet de simuler l’appui de l’utilisateur sur un bouton. Dans
	 * l’implémentation du MVP dans Spi4J, l’appui sur un bouton est équivalent à
	 * l’appel d’une méthode (sans paramètre) du presenter. Ce step va donc chercher
	 * parmi les presenters actifs celui qui possède une annotation
	 * <code>@UserAction(UserAction.c_BOUTON + bouton)</code>.
	 * 
	 * La méthode trouvée est alors exécutée. Si la méthode n’est pas trouvée ou
	 * qu’un problème survient lors de son exécution, le step est considéré comme
	 * dans l’état FAILED.
	 * 
	 * @param p_Bouton le nom du bouton cliqué
	 */
	@When("l'utilisateur clique sur le bouton \"$bouton\"")
	public void userClickButton(final String p_Bouton) {
		boolean v_actionDone = false;
		// On récupère la vue courante pour savoir a qui appartient le bouton
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				// On appel son action "Graal" qui doit etre "anotée"
				get_graalUtil().callUserAction(v_activePresenter, UserAction.c_BOUTON + p_Bouton);
				v_actionDone = true;
				break;
			} catch (final UserActionNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		if (!v_actionDone) {
			throw new UserActionNotFoundGraalException(getActivePresenters(), p_Bouton);
		}
	}

	/**
	 * Simulation d'un événement par l'utilisateur.
	 * 
	 * @param p_evenement le nom de l'événement à simuler
	 */
	@When("l'utilisateur simule l'événement \"$evenement\"")
	public void userSimuleEvenement(final String p_evenement) {
		boolean v_actionDone = false;
		// On récupère la vue courante pour savoir a qui appartient le bouton
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				// On appel son action "Graal" qui doit etre "anotée"
				get_graalUtil().callUserAction(v_activePresenter, UserAction.c_SIMULATION + p_evenement);
				v_actionDone = true;
				break;
			} catch (final UserActionNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		if (!v_actionDone) {
			throw new UserActionNotFoundGraalException(getActivePresenters(), p_evenement);
		}
	}

	/**
	 * L'utilisateur saisit une valeur dans un champ.
	 * 
	 * Ce step permet de remplir un champ de l’application (champ de saisie de
	 * texte, date, nombre, checkbox, …). Il va tout d’abord rechercher parmi les
	 * vues actives celle qui possède le champ souhaité.
	 * 
	 * Cela correspond à un getter dans l’interface de vue qui doit être annoté avec
	 * <code>@Field(champ)</code>. Ce champ doit être typé <code>HasValue<…></code>.
	 * 
	 * Une fois le champ trouvé, sa valeur est renseignée avec le paramètre du step.
	 * Si le champ n’a pas été trouvé ou si un problème survient lorsque la valeur
	 * est renseignée dans le champ, le step est considéré comme dans l’état FAILED.
	 * 
	 * @param p_valeur la valeur saisie
	 * @param p_champ  le nom du champ
	 */
	@When("l'utilisateur saisit la valeur \"$valeur\" dans le champ \"$champ\"")
	public void userSetField(final String p_valeur, final String p_champ) {
		boolean v_actionDone = false;
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				// récupère l'objet avec le nom graal "..." et passe la valeur à "..."
				get_graalUtil().setFieldValue(v_activePresenter, p_champ, p_valeur);
				v_actionDone = true;
				break;
			} catch (final FieldNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		if (!v_actionDone) {
			throw new FieldNotFoundGraalException(getActivePresenters(), p_champ);
		}
	}

	/**
	 * L'utilisateur coche un champ.
	 * 
	 * @param p_champ le nom du champ
	 */
	@When("l'utilisateur coche le champ \"$champ\"")
	public void userSetBooleanField(final String p_champ) {
		userSetField(Boolean.toString(true), p_champ);
	}

	/**
	 * L'utilisateur décoche un champ.
	 * 
	 * @param p_champ le nom du champ
	 */
	@When("l'utilisateur décoche le champ \"$champ\"")
	public void userUnsetBooleanField(final String p_champ) {
		userSetField(Boolean.toString(false), p_champ);
	}

	/**
	 * L'utilisateur saisit une valeur dynamique dans un champ (typée avec le numéro
	 * de thread et le numéro d'itération dans JMeter).
	 * 
	 * @param p_valeur la valeur saisie
	 * @param p_champ  le nom du champ
	 */
	@When("l'utilisateur saisit une valeur dynamique \"$valeur\" dans le champ \"$champ\"")
	public void userSetDynamicField(final String p_valeur, final String p_champ) {
		userSetField(p_valeur + getDynamicSuffix(), p_champ);
	}

	/**
	 * L'utilisateur choisit une valeur dans une liste.
	 * 
	 * Ce step permet de simuler le choix par l’utilisateur d’une valeur dans une
	 * liste (combo-box, liste de sélection simple). Il va tout d’abord rechercher
	 * parmi les vues actives celle qui possède la liste souhaitée.
	 * 
	 * Cela correspond à un getter dans l’interface de vue qui doit être annoté avec
	 * <code>@Field(liste)</code>. Ce champ correspond à une liste et doit donc être
	 * typé <code>HasSelection_Itf<…></code>.
	 * 
	 * Une fois le champ trouvé, les valeurs qu’il contient sont parcourues jusqu’à
	 * ce que l’élément courant corresponde avec celui souhaité par le step. Si le
	 * champ correspondant à la liste n’a pas été trouvé ou si l’élément désiré n’a
	 * pas été trouvé dans la liste, le step est considéré comme dans l’état FAILED.
	 * 
	 * Note : ce step se base sur la méthode toString() des éléments contenus dans
	 * la liste pour comparer avec la valeur passée en paramètre du step.
	 * 
	 * @param p_valeur la valeur choisie
	 * @param p_liste  le nom de la liste
	 */
	@When("l'utilisateur choisit la valeur \"$valeur\" dans la liste \"$liste\"")
	public void userChooseValueInList(final String p_valeur, final String p_liste) {
		userChooseInList(p_liste, p_valeur);
	}

	/**
	 * L'utilisateur choisit une liste de valeurs dans une liste.
	 * 
	 * @param p_valeurs les valeurs choisies (séparées par des ';')
	 * @param p_liste   le nom de la liste
	 */
	@When("l'utilisateur choisit la liste de valeurs \"$valeur\" dans la liste \"$liste\"")
	public void userChooseListValuesInList(final String p_valeurs, final String p_liste) {
		final String[] v_valeurs = splitParameter(p_valeurs);
		userChooseInList(p_liste, (Object[]) v_valeurs);
	}

	/**
	 * L'utilisateur choisit un objet dans une liste.
	 * 
	 * Ce step est similaire au précédent, mis à part le fait que le premier
	 * paramètre ne correspond pas au toString() de l’élément cherché, mais à un
	 * identifiant d’une valeur qui aurait été stocké précédemment dans la story.
	 * 
	 * On suppose donc que la méthode putData(objet, data) a déjà été appelée et que
	 * ce step fera appel à la méthode getData(objet) pour récupérer la valeur
	 * réelle de l’objet.
	 * 
	 * Le step parcourt les éléments de la liste et cherche si l’objet existe. Si le
	 * champ correspondant à la liste n’a pas été trouvé ou si l’élément désiré n’a
	 * pas été trouvé dans la liste, le step est considéré comme dans l’état FAILED.
	 * 
	 * @param p_objet l'objet choisie
	 * @param p_liste le nom de la liste
	 */
	@When("l'utilisateur choisit l'objet \"$objet\" dans la liste \"$liste\"")
	public void userChooseObjectInList(final String p_objet, final String p_liste) {
		final Object v_data = get_story().getData(p_objet);
		userChooseInList(p_liste, v_data);
	}

	/**
	 * L'utilisateur choisit une liste d'objets dans une liste.
	 * 
	 * @param p_identifiants les identifiants choisies (séparées par des ';')
	 * @param p_liste        le nom de la liste
	 */
	@When("l'utilisateur choisit la liste d'objets \"$valeurs\" dans la liste \"$liste\"")
	public void userChooseListObjectsInList(final String p_identifiants, final String p_liste) {
		final String[] v_dataKeys = splitParameter(p_identifiants);
		final Object[] v_objects = new Object[v_dataKeys.length];
		for (int v_i = 0; v_i < v_dataKeys.length; v_i++) {
			v_objects[v_i] = get_story().getData(v_dataKeys[v_i]);
		}
		userChooseInList(p_liste, v_objects);
	}

	/**
	 * L'utilisateur choisit une liste de valeurs dans une liste.
	 * 
	 * @param p_liste   la liste (nom GRAAL)
	 * @param p_valeurs les valeurs à sélectionner dans la liste
	 */
	private void userChooseInList(final String p_liste, final Object... p_valeurs) {
		boolean v_actionDone = false;
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				// récupère l'objet (liste) avec le nom graal "..." et passe la valeur à "..."
				get_graalUtil().setSelectedValueInList(v_activePresenter, p_liste, p_valeurs);
				v_actionDone = true;
				break;
			} catch (final FieldNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		if (!v_actionDone) {
			throw new FieldNotFoundGraalException(getActivePresenters(), p_liste);
		}
	}

	/**
	 * L'utilisateur saisit une valeur aléatoire dans un champ.
	 * 
	 * @param p_taille la taille saisie
	 * @param p_champ  le nom du champ
	 */
	@When("l'utilisateur saisit une chaine aléatoire($taille) dans le champ \"$champ\"")
	public void userSetFieldWithRandomString(final int p_taille, final String p_champ) {

		// caractères lisibles de 48 ('0') à 122 ('z')
		final int v_size = _random.nextInt(p_taille) + 1;
		final StringBuilder v_str = new StringBuilder(v_size);
		for (int v_i = 0; v_i < v_size; v_i++) {
			final char v_c = Character.valueOf((char) (_random.nextInt('z' - '0') + '0'));
			v_str.append(v_c);
		}
		userSetField(v_str.toString(), p_champ);
	}

	/**
	 * L'utilisateur choisit la valeur vide dans une liste
	 * 
	 * @param p_liste le nom de la liste
	 */
	@When("l'utilisateur choisit la valeur vide dans la liste \"$liste\"")
	public void userChooseNoValueInList(final String p_liste) {
		boolean v_actionDone = false;
		for (final Presenter_Abs<? extends View_Itf, ?> v_activePresenter : getActivePresenters()) {
			try {
				// récupère l'objet (liste) avec le nom graal "..." et passe la valeur à "..."
				get_graalUtil().unsetValueInList(v_activePresenter, p_liste);
				v_actionDone = true;
				break;
			} catch (final FieldNotFoundGraalException v_e) {
				// l'action n'a pas été trouvée dans cette vue, on essaye dans les autres vues
				continue;
			}
		}
		if (!v_actionDone) {
			throw new FieldNotFoundGraalException(getActivePresenters(), p_liste);
		}
	}

	/**
	 * L'utilisateur choisit une valeur dynamique dans une liste
	 * 
	 * @param p_valeur la valeur
	 * @param p_liste  la liste
	 */
	@When("l'utilisateur choisit la valeur dynamique \"$valeur\" dans la liste \"$liste\"")
	public void userChooseDynamicValueInList(final String p_valeur, final String p_liste) {
		userChooseInList(p_liste, p_valeur + getDynamicSuffix());
	}

}
