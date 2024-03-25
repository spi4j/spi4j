package fr.spi4j.ui.swing.table;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultRowSorter;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Renderer d'entête de colonne avec case à cocher "Sélectionner tout" à
 * utiliser en haut d'une colonne de cases à cocher.
 * <p>
 * Utilisation : new CheckBoxHeaderRenderer(table, column).registerRenderer();
 * <p>
 * Remarque : cela ne désactive pas le tri sur les lignes lors du clique sur
 * l'entête de colonne, si jamais le tableau est triable.
 * 
 * @author MINARM
 */
public class CheckBoxHeaderRenderer extends JCheckBox implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	private final JTable table;

	private final TableColumn tableColumn;

	/**
	 * Si les cases à cocher dans les lignes deviennent toutes cochées ou pas, alors
	 * on met à jour le header.
	 * 
	 * @author MINARM
	 */
	private class ModelAndSorterListener implements TableModelListener, RowSorterListener {
		@Override
		public void tableChanged(final TableModelEvent e) {
			refreshHeader();
		}

		@Override
		public void sorterChanged(final RowSorterEvent p_e) {
			refreshHeader();
		}

		/**
		 * Si les cases à cocher dans les lignes deviennent toutes cochées ou pas, alors
		 * on met à jour le header.
		 */
		private void refreshHeader() {
			boolean allChecked = true;
			final TableModel tableModel = table.getModel();
			final int column = tableColumn.getModelIndex();
			// les lignes peuvent éventuellement être filtrées,
			// donc on regarde seulement les lignes affichées dans la vue et pas toutes les
			// lignes du modèle
			for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
				final int modelRow = table.convertRowIndexToModel(viewRow);
				final boolean value = ((Boolean) tableModel.getValueAt(modelRow, column)).booleanValue();
				if (!value) {
					allChecked = false;
					break;
				}
			}
			if (allChecked != isSelected()) {
				setSelected(allChecked);
				table.getTableHeader().repaint();
			}
		}
	}

	/**
	 * Quand on (dé)coche l'entête de la colonne, alors toutes les lignes sont
	 * (dé)cochées.
	 * 
	 * @author MINARM
	 */
	class HeaderMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(final MouseEvent mouseEvent) {
			final int viewColumn = table.getColumnModel().getColumnIndexAtX(mouseEvent.getX());
			final int modelColumn = table.convertColumnIndexToModel(viewColumn);
			if (modelColumn == tableColumn.getModelIndex()) {
				setSelected(!isSelected());
				final Boolean checked = isSelected();
				final TableModel tableModel = table.getModel();
				final int column = tableColumn.getModelIndex();
				// les lignes peuvent éventuellement être filtrées,
				// donc on regarde seulement les lignes affichées dans la vue et pas toutes les
				// lignes du modèle
				for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
					final int modelRow = table.convertRowIndexToModel(viewRow);
					tableModel.setValueAt(checked, modelRow, column);
				}
				table.getTableHeader().repaint();
				mouseEvent.consume();
			}
		}
	}

	/**
	 * Constructeur.
	 * 
	 * @param pTable       Le tableau
	 * @param pTableColumn La colonne contenant des booléens (cases à cocher
	 *                     modifiables par exemple)
	 */
	public CheckBoxHeaderRenderer(final JTable pTable, final TableColumn pTableColumn) {
		super();
		this.table = pTable;
		this.tableColumn = pTableColumn;
	}

	/**
	 * Enregistre ce header sur la colonne.
	 */
	public void registerRenderer() {
		tableColumn.setHeaderRenderer(this);
		setText(String.valueOf(tableColumn.getHeaderValue()));
		final JTableHeader header = table.getTableHeader();
		header.addMouseListener(new HeaderMouseListener());
		setForeground(header.getForeground());
		setBackground(header.getBackground());
		setFont(header.getFont());
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));

		table.getModel().addTableModelListener(new ModelAndSorterListener());
		if (table.getRowSorter() != null) {
			table.getRowSorter().addRowSorterListener(new ModelAndSorterListener());
		}

		// Si la table a un RowSorter, alors le clic sur le header de la colonne
		// déclenche l'action toggleSort ce qui n'est pas souhaité,
		// avant que l'évènement mouseClicked n'arrive dans le listener de cette classe.
		// Pour éviter une action de tri en plus de "(Dé)Sélectionner tout", on rend
		// cette colonne non triable.
		if (table.getRowSorter() instanceof DefaultRowSorter<?, ?>) {
			// si des colonnes sont ajoutées sur la table après l'appel à setSortable,
			// alors ArrayIndexOutOfBoundsException dans la méthode
			// DefaultRowSorter.isSortable,
			// donc pour tenter d'éviter ce cas, setSortable est appelée en invokeLater
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					((DefaultRowSorter<?, ?>) table.getRowSorter()).setSortable(tableColumn.getModelIndex(), false);
				}
			});
		}
	}

	@Override
	public Component getTableCellRendererComponent(final JTable pTable, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		return this;
	}

	/**
	 * Main de Test.
	 * 
	 * @param args String[]
	 */
	public static void main(final String[] args) {
		final Object[] colNames = { "", "String", "String" };
		final DefaultTableModel dtm = new DefaultTableModel(null, colNames);
		final JTable table = new JTable(dtm);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		for (int x = 0; x < 5; x++) {
			dtm.addRow(new Object[] { Boolean.FALSE, "Row " + (x + 1) + " Col 2", "Row " + (x + 1) + " Col 3" });
		}
		final JScrollPane sp = new JScrollPane(table);
		final TableColumn tc = table.getColumnModel().getColumn(0);
		tc.setCellEditor(table.getDefaultEditor(Boolean.class));
		tc.setCellRenderer(table.getDefaultRenderer(Boolean.class));
		tc.setHeaderValue("Check All");
		new CheckBoxHeaderRenderer(table, tc).registerRenderer();
		final JFrame f = new JFrame();
		f.getContentPane().add(sp);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
