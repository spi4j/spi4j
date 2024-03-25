/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import fr.spi4j.ui.swing.fields.SpiDateField;
import fr.spi4j.ui.swing.fields.SpiDatePanel;

/**
 * Editor de dates avec calendrier pour les cellules des JTables.
 * <p>
 * Attention : sur cet éditeur, mettre clickCountToStart à 1 au lieu de 2 peut
 * entraîner des problèmes de focus.
 * 
 * @author MINARM
 */
public class SpiDateActionTableCellEditor extends SpiActionTableCellEditor {
	private static final ImageIcon c_calendarIcon = new ImageIcon(
			SpiDateActionTableCellEditor.class.getResource("/icons/Calendar16.gif"));

	private final SpiDatePanel datePanel = new SpiDatePanel();

	/**
	 * Constructeur.
	 */
	public SpiDateActionTableCellEditor() {
		this(new SpiDefaultCellEditor(new SpiDateField()));
	}

	/**
	 * Constructeur.
	 * 
	 * @param editor TableCellEditor
	 */
	public SpiDateActionTableCellEditor(final TableCellEditor editor) {
		super(editor);
		customEditorButton.setText(null);
		customEditorButton.setIcon(c_calendarIcon);
		customEditorButton.setPreferredSize(new Dimension(14, 16));
		customEditorButton.setBorder(null);
		customEditorButton.setContentAreaFilled(false);
		customEditorButton.setFocusable(false);
	}

	@Override
	protected void editCell(final JTable table, final Object partialValue, final int row, final int column) {
		// Object value = table.getValueAt(row, column);
		datePanel.setValue((Date) partialValue);

		// affiche la boîte de dialogue
		final JDialog dialog = datePanel.chooseDate();

		final WindowAdapter windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosed(final WindowEvent evt) {
				setValueAt(table, row, column);
			}
		};
		dialog.addWindowListener(windowAdapter);
	}

	/**
	 * Valide une valeur saisie.
	 * 
	 * @param table  JTable
	 * @param row    int
	 * @param column int
	 */
	protected void setValueAt(final JTable table, final int row, final int column) {
		table.setValueAt(datePanel.getValue(), row, column);
	}
}
