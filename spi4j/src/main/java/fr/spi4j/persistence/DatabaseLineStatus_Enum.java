/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Enumération des différents états pour la suppression logique en base de
 * données.
 *
 * @author MINARM
 */
public enum DatabaseLineStatus_Enum {
	/**
	 * La ligne est active.
	 */
	active("0"),

	/**
	 * La ligne est référencée pour l'existant mais supprimée pour les insertions
	 * qui peuvent référencer ligne. (cas des applications comptable par exemple).
	 */
	deletedForNewReference("1"),

	/**
	 * La ligne est conservée mais considérée comme supprimée.
	 */
	deletedForAll("3"),

	/**
	 * La ligne est à supprimer physiquement de la base de données.
	 */
	deletedForTrash("9");

	/**
	 * L'état (statut de la ligne). L'état est sous forme String pour la
	 * compatibilité avec l'ensemble des Bdd.
	 */
	private String _value;

	/**
	 * L'état de la ligne sous forme numérique.
	 */
	private short _shortValue;

	/**
	 * Constructeur.
	 *
	 * @param p_value : L'état de la ligne.
	 */
	DatabaseLineStatus_Enum(final String p_value) {
		_value = p_value;
		_shortValue = Short.parseShort(_value);
	}

	/**
	 * Retourne l'état (ou statut) de la ligne.
	 *
	 * @return L'état de la ligne.
	 */
	public String get_value() {
		return _value;
	}

	/**
	 * Vérifie si la valeur de la ligne permet la visibilité en selection.
	 * <p>
	 * Laisse courir l'exception si problème de cast pour l'entier.
	 *
	 * @param p_value : La valeur de l'énumération à tester.
	 * @return 'True' si la ligne est visible pour selection.
	 */
	public static boolean forSelect(final String p_value) {
		if (null != p_value && Integer.parseInt(p_value) < deletedForAll._shortValue) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	/**
	 * Vérifie si la valeur de la ligne permet la suppression.
	 * <p>
	 * Laisse courir l'exception si problème de cast pour l'entier.
	 *
	 * @param p_value : La valeur de l'énumération à tester.
	 * @return 'True' si la ligne est visible pour suppression.
	 */
	public static boolean forDelete(final String p_value) {
		if (null != p_value && Integer.parseInt(p_value) < deletedForAll._shortValue) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	/**
	 * Vérifie si la valeur de la ligne permet la mise à jour.
	 *
	 * @param p_value : La valeur de l'énumération à tester.
	 * @return 'True' si la ligne est visible pour mise à jour.
	 */
	public static boolean forUpdate(final String p_value) {

		if (active.get_value().equals(p_value)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	/**
	 * Récupère l'énumération à partir de la valeur stockée dans la base de données.
	 *
	 * @param p_value : La valeur de l'énumération sous forme de String, e,
	 *                provenance de la base de données.
	 * @return Le statut de la lignes sous forme d'enumeration.
	 */
	public static DatabaseLineStatus_Enum fromStringValue(final String p_value) {
		for (final DatabaseLineStatus_Enum v_lineStatusEnum : DatabaseLineStatus_Enum.values()) {
			if (v_lineStatusEnum.get_value().equals(p_value)) {
				return v_lineStatusEnum;
			}
		}
		throw new Spi4jRuntimeException(
				"XTOPSUP : Impossible de retrouver le statut de la ligne au niveau de l'énumération ",
				"Vérifier le statut de la ligne dans la abse de données.");
	}
}
