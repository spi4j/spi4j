/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * Champs liste déroulante avec auto-complétion. L'implémentation utilise SwingX
 * d'Oracle.
 * 
 * @param <TypeValue> Type de la valeur
 * @author MINARM
 */
public class SpiAutoCompleteComboBox<TypeValue> extends SpiComboBox<TypeValue> {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur.
	 */
	public SpiAutoCompleteComboBox() {
		super();
		AutoCompleteDecorator.decorate(this);
	}
}
