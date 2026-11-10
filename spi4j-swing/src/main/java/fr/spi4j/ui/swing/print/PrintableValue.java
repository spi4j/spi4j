/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.print;

/**
 * Interface permettant de customiser un texte à imprimer, quand le texte par
 * défaut n'est pas le même que celui à imprimer.
 * <p>
 * Actuellement pris en compte uniquement pour TableColumn.getHeaderValue().
 * 
 * @author MINARM
 */
public interface PrintableValue {
	/**
	 * @return String
	 */
	String getPrintableText();
}
