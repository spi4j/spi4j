/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer par défaut pour les cellules des JTables.
 * <p>
 * La super classe contient un certain nombre d'optimisation de performances
 * pour le rendu.
 * 
 * @author MINARM
 */
public class SpiDefaultTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	protected void setValue(final Object value) {
		// méthode à surcharger pour changer le rendu via setText(String) ou
		// setIcon(Icon) en fonction de la valeur (par exemple, formatage)
		super.setValue(value);
	}

	/**
	 * Ajustement de la hauteur de cette ligne en fonction de la taille du renderer
	 * (ex: la taille d'une icône ou d'un label html).
	 * 
	 * @param table     JTable
	 * @param component Component
	 * @param row       int
	 */
	protected void adjustRowHeight(final JTable table, final Component component, final int row) {
		// Ajustement de la hauteur de cette ligne en fonction de la taille du renderer
		final int cellHeight = table.getRowHeight(row);
		final int rendererHeight = component.getPreferredSize().height;
		if (cellHeight < rendererHeight - 2) {
			// dans le cas normal, cellHeight est à 16 et rendererHeight est à 18
			table.setRowHeight(row, rendererHeight);
		}
	}
}
