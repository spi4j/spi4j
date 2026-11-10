/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Component;

import javax.swing.JTable;

/**
 * Définit un renderer pour représenter une cellule avec des retours chariots,
 * de hauteur variable, dans une JTable.
 * 
 * @author MINARM
 */
public class SpiMultiLineTableCellRenderer extends SpiDefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public void setValue(final Object value) {
		if (value == null) {
			setToolTipText(null);
			setText("");
		} else {
			final String text = value.toString();
			if (text.indexOf('\n') == -1) {
				setToolTipText(null);
				setText(text);
			} else {
				final StringBuilder sb = new StringBuilder(text.length() + text.length() / 4);
				sb.append("<html>");
				if (getHorizontalAlignment() == CENTER) {
					sb.append("<center>");
				}
				// les espaces doivent être remplacés par nbsp (espace insécable) sinon il y a
				// des retours chariots ajoutés selon la largeur de la colonne (wrapping)
				// et dans ce cas la hauteur de la ligne ne serait plus correcte
				sb.append(text.replace("\n", "<br/>").replace(" ", "&nbsp;"));
				final String string = sb.toString();
				setToolTipText(string);
				setText(string);
			}
		}
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		// Surcharge pour appeler adjustRowHeight.
		final Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				column);
		adjustRowHeight(table, component, row);
		return component;
	}
}
