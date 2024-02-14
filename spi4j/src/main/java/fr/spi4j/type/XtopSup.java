/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.type;

import java.io.Serializable;

import fr.spi4j.persistence.DatabaseLineStatus_Enum;

/**
 * Type specifique pour le statut de la ligne. Définir ce type permet de
 * travailler avec les valeurs de l'enumeration et de se fondre dans le
 * fonctionnement genral au niveau du processus de generation.
 *
 * @author MINARM
 */
public class XtopSup implements Serializable {

	/**
	 * Numero de serie auto genere pour la serialisation.
	 */
	private static final long serialVersionUID = 3960447253989916607L;

	DatabaseLineStatus_Enum _xtopSup;

	/**
	 * Constructeur avec forcage du statut de la ligne dans la base de donnees.
	 *
	 * @param p_lineStatus : le statut de la ligne sous forme d'enumeration.
	 */
	public XtopSup(final DatabaseLineStatus_Enum p_lineStatus) {
		_xtopSup = p_lineStatus;
	}

	/**
	 * Constructeur avec forcage du statut de la ligne dans la base de donnees.
	 *
	 * @param p_lineStatus : le statut de la ligne en sortie de base de donnees sous
	 *                     forme de String.
	 */
	public XtopSup(final String p_lineStatus) {
		_xtopSup = DatabaseLineStatus_Enum.fromStringValue(p_lineStatus);
	}

	/**
	 * Retourne le statut de la ligne sous forme de chaine de caractere.
	 *
	 * @return la valeur de l'enumeration au format chaine de caractere.
	 */
	public String get_value() {
		return _xtopSup.get_value();
	}

	/**
	 * Postionne le statut de la ligne.
	 *
	 * @param p_xtopSup La valeur de la ligne sous forme d'enumeration.
	 */
	public void set_value(final DatabaseLineStatus_Enum p_xtopSup) {
		_xtopSup = p_xtopSup;
	}
}
