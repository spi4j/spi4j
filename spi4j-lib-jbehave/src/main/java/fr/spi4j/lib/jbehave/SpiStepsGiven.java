/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import org.jbehave.core.annotations.Given;

/**
 * Liste des steps "Given".
 * 
 * @author MINARM
 */
public class SpiStepsGiven extends SpiSteps_Abs {

	/**
	 * Construit une classe de steps.
	 * 
	 * @param p_story la story en cours
	 */
	public SpiStepsGiven(final SpiStory_Abs p_story) {
		super(p_story);
	}

	/**
	 * Affiche une vue d'après son nom. Ce step permet de démarrer un écran. Il
	 * cherche parmi toutes les vues connues celle dont l’interface est annotée avec
	 * <code>@UserView(name)</code>.
	 * 
	 * Le presenter associé à la vue trouvée est alors instancié (sans paramètres).
	 * A partir de cet instant, l’écran est considéré comme démarré et actif. Si un
	 * problème a lieu lors de l’instanciation du presenter ou si aucune vue ne
	 * porte l’annotation @UserView cherchée, le step est considéré comme dans
	 * l’état FAILED.
	 * 
	 * @param p_Name le nom de la vue
	 */
	@Given("la vue \"$name\" est active")
	public void activateView(final String p_Name) {
		getViewManager().openView(p_Name);
	}

	/**
	 * Déterminer l'identifiant et le type du prochain DTO à sauvegarder.
	 * 
	 * @param p_type        le type de l'identifiant
	 * @param p_identifiant l'identifiant
	 */
	@Given("le prochain DTO de type \"$type\" sera sauvegardé sous le nom \"$identifiant\"")
	public void nextIdentifiantWithTypeToSave(final String p_type, final String p_identifiant) {
		SaveDtoProxy.setNextTypeToSave(p_identifiant, p_type);
	}
}
