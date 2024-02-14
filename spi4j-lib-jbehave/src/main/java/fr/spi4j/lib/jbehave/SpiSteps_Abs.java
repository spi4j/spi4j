/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import java.util.Collection;

import fr.spi4j.lib.jbehave.graal.GraalUtil;
import fr.spi4j.ui.mvp.MVPUtils;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.ViewManager;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Classe ancêtre pour la définition des steps JBehave.
 * 
 * @author MINARM
 */
public abstract class SpiSteps_Abs {

	// protected static final Logger c_log = Logger.getLogger(SpiSteps.class);

	private final GraalUtil _graalUtil = new GraalUtil();

	private final SpiStory_Abs _story;

	/**
	 * Constructeur de classe de steps.
	 * 
	 * @param p_story la story en cours
	 */
	public SpiSteps_Abs(final SpiStory_Abs p_story) {
		_story = p_story;
	}

	/**
	 * @return la story en cours.
	 */
	public SpiStory_Abs get_story() {
		return _story;
	}

	/**
	 * @return l'instance du ViewManager
	 */
	public ViewManager getViewManager() {
		return MVPUtils.getInstance().getViewManager();
	}

	/**
	 * @return le présenteur actif
	 */
	public Collection<Presenter_Abs<? extends View_Itf, ?>> getActivePresenters() {
		return getViewManager().getActivePresenters();
	}

	/**
	 * @return la classe utilitaire Graal
	 */
	public GraalUtil get_graalUtil() {
		return _graalUtil;
	}

	/**
	 * Sépare les valeurs d'un paramètre, en utilisant le séparateur ';'.
	 * 
	 * @param p_parameter le paramètre du step
	 * @return le tableau de chaines de caractères
	 */
	protected String[] splitParameter(final String p_parameter) {
		String v_escapingString = "###";
		if (p_parameter.contains(v_escapingString)) {
			v_escapingString += "#";
		}
		final String v_escapedString = p_parameter.replaceAll("\\\\;", v_escapingString);
		final String[] v_valeurs = v_escapedString.split(";");
		for (int v_i = 0; v_i < v_valeurs.length; v_i++) {
			if (v_valeurs[v_i].contains(v_escapingString)) {
				v_valeurs[v_i] = v_valeurs[v_i].replaceAll(v_escapingString, ";");
			}
		}
		return v_valeurs;
	}

	/**
	 * @return le suffixe d'une valeur dynamique (numéro de thread et numéro
	 *         d'itération)
	 */
	protected String getDynamicSuffix() {
		return "/" + get_story().getThreadNum() + "/" + get_story().getNumIteration();
	}

}
