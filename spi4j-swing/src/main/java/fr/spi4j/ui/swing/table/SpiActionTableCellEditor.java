/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * Classe abstraite pour les éditors avec bouton des cellules de JTables.
 * 
 * @author Santhosh Kumar T (MySwing)
 */
abstract class SpiActionTableCellEditor implements TableCellEditor, ActionListener {
	/** Bouton de l'éditeur de cellule. */
	protected final JButton customEditorButton = new JButton("\u2026"); // 3 petits points en unicode

	/** Editeur de cellule délégué. */
	protected final TableCellEditor editor;

	private JTable table;

	private int row;

	private int column;

	/**
	 * Constructeur.
	 * 
	 * @param myEditor TableCellEditor
	 */
	protected SpiActionTableCellEditor(final TableCellEditor myEditor) {
		this.editor = myEditor;
		customEditorButton.addActionListener(this);

		// ui-tweaking
		customEditorButton.setPreferredSize(new Dimension(14, 16)); // 14 pour laisser la bordure pressée s'afficher
		customEditorButton.setFocusable(false);
		customEditorButton.setFocusPainted(false);
		customEditorButton.setMargin(new Insets(0, 0, 0, 0));
	}

	@Override
	public Component getTableCellEditorComponent(final JTable myTable, final Object value, final boolean isSelected,
			final int myRow, final int myColumn) {
		final Component editorComponent = editor.getTableCellEditorComponent(myTable, value, isSelected, myRow,
				myColumn);
		final JPanel panel = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void addNotify() {
				super.addNotify();
				editorComponent.requestFocus();
			}

			@Override
			@SuppressWarnings("deprecation")
			protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition,
					final boolean pressed) {
				if (editorComponent instanceof JComponent) {
					final InputMap map = ((JComponent) editorComponent).getInputMap(condition);
					final ActionMap am = ((JComponent) editorComponent).getActionMap();

					if (map != null && am != null && isEnabled()) {
						final Object binding = map.get(ks);
						final Action action = binding == null ? null : am.get(binding);
						if (action != null) {
							return SwingUtilities.notifyAction(action, ks, e, editorComponent, e.getModifiers());
						}
					}
				}
				return false;
			}
		};

		panel.setRequestFocusEnabled(true);
		panel.add(editorComponent);
		panel.add(customEditorButton, BorderLayout.EAST);
		panel.doLayout();
		this.table = myTable;
		this.row = myRow;
		this.column = myColumn;
		return panel;
	}

	@Override
	public Object getCellEditorValue() {
		return editor.getCellEditorValue();
	}

	@Override
	public boolean isCellEditable(final EventObject anEvent) {
		return editor.isCellEditable(anEvent);
	}

	@Override
	public boolean shouldSelectCell(final EventObject anEvent) {
		return editor.shouldSelectCell(anEvent);
	}

	@Override
	public boolean stopCellEditing() {
		return editor.stopCellEditing();
	}

	@Override
	public void cancelCellEditing() {
		editor.cancelCellEditing();
	}

	@Override
	public void addCellEditorListener(final CellEditorListener l) {
		editor.addCellEditorListener(l);
	}

	@Override
	public void removeCellEditorListener(final CellEditorListener l) {
		editor.removeCellEditorListener(l);
	}

	@Override
	public final void actionPerformed(final ActionEvent event) {
		final Object partialValue = editor.getCellEditorValue();
		editor.cancelCellEditing();
		editCell(table, partialValue, row, column);
	}

	/**
	 * Appelle l'édition d'une cellule.
	 * 
	 * @param myTable      JTable
	 * @param partialValue Object
	 * @param myRow        int
	 * @param myColumn     int
	 */
	protected abstract void editCell(JTable myTable, Object partialValue, int myRow, int myColumn);
}
