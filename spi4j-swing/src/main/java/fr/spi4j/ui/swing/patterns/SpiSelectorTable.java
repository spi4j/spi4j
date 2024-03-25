/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.patterns;

import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import fr.spi4j.business.dto.DtoUtil;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.persistence.entity.EntityUtil;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ui.HasMultipleSelection_Itf;
import fr.spi4j.ui.swing.table.SpiTableModel;
import fr.spi4j.ui.swing.table.SpiTable_Abs;

/**
 * Composant de type table permettant de sélectionner certains éléments au moyen
 * d'une colonne cases à cocher.
 * <p>
 * C'est donc une alternative à une table en sélection multiple ou à un
 * composant de type mouvementeur.
 * 
 * @param <TypeValue> Type des valeurs de la liste
 * @author MINARM
 */
public class SpiSelectorTable<TypeValue> extends SpiTable_Abs<TypeValue>
		implements HasMultipleSelection_Itf<TypeValue> {
	private static final String SELECTION_TITLE = "Sél.";

	private static final long serialVersionUID = 1L;

	private static final MouseHandler MOUSE_HANDLER = new MouseHandler();

	private static final MouseMotionHandler MOUSE_MOTION_HANDLER = new MouseMotionHandler();

	// private static final TableCellEditor BOOLEAN_TABLE_CELL_EDITOR =
	// new DefaultCellEditor(new SpiCheckBox());

	private boolean pressed;

	private int lastRow = -1;

	/**
	 * TableModel gérant spécifiquement la colonne 0 pour les valeurs des
	 * sélections.
	 * 
	 * @param <TypeValue> Type des valeurs de la liste
	 * @author MINARM
	 */
	public static class SelectorTableModel<TypeValue> extends SpiTableModel<TypeValue> {
		private static final long serialVersionUID = 1L;

		private boolean[] selections = new boolean[0];

		/**
		 * Constructeur.
		 * 
		 * @param p_table SpiSelectorTable
		 */
		public SelectorTableModel(final SpiSelectorTable<TypeValue> p_table) {
			super(p_table);
		}

		@Override
		public String getColumnName(final int column) {
			if (column != 0) {
				return super.getColumnName(column);
			}
			return SELECTION_TITLE;
		}

		@Override
		public Class<?> getColumnClass(final int column) {
			if (column != 0) {
				return super.getColumnClass(column);
			}
			return Boolean.class;
		}

		@Override
		public boolean isCellEditable(final int row, final int column) {
			if (column != 0) {
				return super.isCellEditable(row, column);
			}
			return true;
		}

		@Override
		public Object getValueAt(final int row, final int column) {
			if (column != 0) {
				return super.getValueAt(row, column);
			}
			return Boolean.valueOf(row < selections.length && selections[row]);
		}

		@Override
		public void setValueAt(final Object value, final int row, final int column) {
			if (column != 0) {
				super.setValueAt(value, row, column);
			} else if (row < selections.length) {
				selections[row] = Boolean.TRUE.equals(value);
				fireTableCellUpdatedOnSelection(row, column);
			}
		}

		/**
		 * Méthode appelée lors de la sélection d'une case à cocher pour envoyer un
		 * évènement cellUpdated au modèle.
		 * <p>
		 * Cet appel sur le modèle provoque un tri du tableau (par ex. sur la colonne
		 * Sél.) et un réaffichage.
		 * <p>
		 * Mais si cela ne convient pas (par ex. si le réaffichage à chaque sélection ne
		 * convient pas), cette méthode peut être surchargée.
		 * 
		 * @param row    int
		 * @param column int
		 */
		@SuppressWarnings("unchecked")
		protected void fireTableCellUpdatedOnSelection(final int row, final int column) {
			// sans cela, certaines cases ne sont pas réafficher correctement sur un drag de
			// souris
			final SpiSelectorTable<TypeValue> table = (SpiSelectorTable<TypeValue>) getTable();
			if (table.getCellEditor() != null) {
				table.getCellEditor().cancelCellEditing();
			}

			fireTableCellUpdated(row, column);
		}
	}

	/**
	 * MouseHandler.
	 */
	private static class MouseHandler extends MouseAdapter {
		@Override
		public void mousePressed(final MouseEvent event) {
			final SpiSelectorTable<?> table = getInstance(event);
			if (table != null) {
				table.pressed = true;
				table.lastRow = -1;
				table.select(event);
			}
		}

		@Override
		public void mouseReleased(final MouseEvent event) {
			final SpiSelectorTable<?> table = getInstance(event);
			if (table != null) {
				table.pressed = false;
			}
		}
	}

	/**
	 * MouseMotionHandler.
	 */
	private static class MouseMotionHandler extends MouseMotionAdapter {
		@Override
		public void mouseDragged(final MouseEvent event) {
			final SpiSelectorTable<?> table = getInstance(event);
			if (table != null && table.pressed) {
				table.select(event);
			}
		}
	}

	/**
	 * MyTableColumnModel.
	 */
	private static class MyTableColumnModel extends DefaultTableColumnModel {
		private static final long serialVersionUID = 1L;

		@Override
		public void removeColumn(final TableColumn tableColumn) {
			// Cette méthode est redéfinie pour ne pas enlever la colonne de modelIndex == 0
			// (colonne de sélection).
			if (tableColumn.getModelIndex() != 0) {
				super.removeColumn(tableColumn);
			}
		}
	}

	/**
	 * Constructeur.
	 */
	public SpiSelectorTable() {
		super();
		setColumnSelectionAllowed(false);
		final TableColumn column = new TableColumn();
		column.setModelIndex(0);
		column.setHeaderValue(SELECTION_TITLE);
		column.setPreferredWidth(25);

		// BOOLEAN_TABLE_CELL_EDITOR ne fonctionne pas bien en Java 1.4 (sur les lignes
		// sélectionnées)
		// et en plus ce MOUSE_MOTION_HANDLER fonctionne encore mieux que ce dernier
		// (pas besoin de cliquer les lignes une par une)
		// column.setCellEditor(BOOLEAN_TABLE_CELL_EDITOR);
		addMouseListener(MOUSE_HANDLER);
		addMouseMotionListener(MOUSE_MOTION_HANDLER);

		addColumn(column);
	}

	/**
	 * Création du modèle de données par défaut.
	 * <p>
	 * Le modèle par défaut est un SpiTableModel spécifique pour la gestion de la
	 * colonne de sélection.
	 * 
	 * @return TableModel
	 */
	@Override
	protected TableModel createDefaultDataModel() {
		return new SelectorTableModel<TypeValue>(this);
	}

	/**
	 * Retourne la valeur de la propriété listTableModel.
	 * 
	 * @return SpiListTableModel
	 */
	@SuppressWarnings("unchecked")
	protected SelectorTableModel<TypeValue> getSelectorTableModel() {
		return (SelectorTableModel<TypeValue>) getModel();
	}

	/**
	 * Retourne l'instance de SpiSelectorTable père du composant source de
	 * l'événement spécifié.
	 * 
	 * @return SpiSelectorTable
	 * @param event ComponentEvent
	 */
	protected static SpiSelectorTable<?> getInstance(final ComponentEvent event) {
		return (SpiSelectorTable<?>) event.getComponent();
	}

	/**
	 * Retourne la liste des éléments sélectionnées.
	 * 
	 * @return List
	 * @see #setSelectionList
	 */
	public List<TypeValue> getSelectionList() {
		final boolean[] selections = getSelectorTableModel().selections;
		final List<TypeValue> selectionList = new ArrayList<TypeValue>();
		final List<TypeValue> list = getList();
		// Math.min nécessaire si réentrant sur getSelectionList avant setSelectionList
		// dans setList
		final int length = Math.min(selections.length, list.size());

		for (int i = 0; i < length; i++) {
			if (selections[i]) {
				selectionList.add(list.get(i));
			}
		}

		return selectionList;
	}

	/**
	 * Sélectionne une ligne.
	 * 
	 * @param event MouseEvent
	 */
	protected void select(final MouseEvent event) {
		if (!isEnabled()) {
			return;
		}
		final Point point = new Point(event.getX(), event.getY());
		final int modelColumn = convertColumnIndexToModel(columnAtPoint(point));
		final int viewRow = rowAtPoint(point);
		if (modelColumn == 0 && viewRow != -1 && viewRow != lastRow) {
			final int modelRow = convertRowIndexToModel(viewRow);
			final boolean cellEditable = getModel().isCellEditable(modelRow, modelColumn);
			if (cellEditable) {
				// colonne sélection
				Boolean value = (Boolean) getModel().getValueAt(modelRow, modelColumn);
				value = !value;
				getModel().setValueAt(value, modelRow, modelColumn);
			}
		}

		lastRow = viewRow;
	}

	@Override
	public void setList(final List<TypeValue> newList) {
		final List<TypeValue> selectionList = getSelectionList();
		super.setList(newList);
		setSelectionList(selectionList);
	}

	/**
	 * Définit la liste des éléments sélectionnés.
	 * 
	 * @param newSelectionList List
	 * @see #getSelectionList
	 */
	@SuppressWarnings("unchecked")
	public void setSelectionList(final List<TypeValue> newSelectionList) {
		final SelectorTableModel<TypeValue> selectorTableModel = getSelectorTableModel();
		final boolean[] selections = new boolean[selectorTableModel.getRowCount()];
		if (newSelectionList != null) {
			final List<TypeValue> list = getList();
			final int size = newSelectionList.size();
			int row;

			for (int i = 0; i < size; i++) {
				final TypeValue object = newSelectionList.get(i);
				row = list.indexOf(object);
				if (row == -1 && object instanceof Dto_Itf && ((Dto_Itf<?>) object).getId() != null) {
					// si l'objet à sélectionner n'a pas été trouvé et si c'est un DTO avec un id,
					// on le recherche par son identifiant
					// (car il peut arriver d'avoir deux instances du DTO avec le même identifiant,
					// y compris pour le "référentiel")
					final Dto_Itf<?> dto = DtoUtil.findInCollectionById((List<Dto_Itf<?>>) list, (Dto_Itf<?>) object);
					row = list.indexOf(dto);
				}
				if (row == -1 && object instanceof Entity_Itf && ((Entity_Itf<?>) object).getId() != null) {
					// si l'objet à sélectionner n'a pas été trouvé et si c'est un DTO avec un id,
					// on le recherche par son identifiant
					// (car il peut arriver d'avoir deux instances du DTO avec le même identifiant,
					// y compris pour le "référentiel")
					final Entity_Itf<?> entity = EntityUtil.findInCollectionById((List<Entity_Itf<?>>) list,
							(Entity_Itf<?>) object);
					row = list.indexOf(entity);
				}

				if (row >= 0) {
					selections[row] = true;
				}
			}
		}

		getSelectorTableModel().selections = selections;
		getSelectorTableModel().fireTableDataChanged();
	}

	@Override
	public void tableChanged(final TableModelEvent event) {
		// Cette surcharge est notamment appelée quand une ligne est ajoutée ou enlevée.
		final SelectorTableModel<TypeValue> selectorTableModel = getSelectorTableModel();
		if (selectorTableModel.getRowCount() != selectorTableModel.selections.length) {
			final boolean[] selections = new boolean[selectorTableModel.getRowCount()];
			System.arraycopy(selectorTableModel.selections, 0, selections, 0,
					Math.min(selectorTableModel.selections.length, selections.length));
			selectorTableModel.selections = selections;
		}
		super.tableChanged(event);
	}

	/**
	 * Création du modèle de données par défaut. Le modèle par défaut est
	 * particulier pour SpiSelectorTable.
	 * 
	 * @return TableColumnModel
	 */
	@Override
	protected TableColumnModel createDefaultColumnModel() {
		return new MyTableColumnModel();
	}

	@Override
	public List<TypeValue> getValue() {
		return getSelectionList();
	}

	@Override
	public void setValue(final List<TypeValue> p_listValue) {
		setSelectionList(p_listValue);
	}
}
