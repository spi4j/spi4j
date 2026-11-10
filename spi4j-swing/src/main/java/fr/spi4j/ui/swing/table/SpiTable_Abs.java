/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import fr.spi4j.business.dto.AttributesNames_Itf;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.ui.swing.print.SpiClipboardPrinter;
import fr.spi4j.ui.swing.print.SpiPrinter;

/**
 * Composant Table, qui utilise une API typée pour la liste des valeurs et qui
 * utilise les AttributesNames_Itf (enum) pour définir les valeurs dans les
 * colonnes.
 * 
 * @param <TypeValue> Type des valeurs de la liste
 * @author MINARM
 */
abstract public class SpiTable_Abs<TypeValue> extends SpiListTable<TypeValue> {
	// TODO étudier l'intérêt d'utiliser JXTable de SwingX (malgré l'abandon du
	// projet par Oracle?)
	// et si oui remplacer cette implémentation

	static final Color BICOLOR_LINE = Color
			.decode(System.getProperty(SpiTable_Abs.class.getName() + ".bicolorLine", "#E7E7E7"));

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("deprecation")
	private static final KeyAdapter CLIPBOARD_KEY_LISTENER = new KeyAdapter() {
		@Override
		public void keyPressed(final KeyEvent event) {
			if (event.getSource() instanceof SpiTable_Abs) {
				final SpiTable_Abs<?> table = (SpiTable_Abs<?>) event.getSource();
				final int keyCode = event.getKeyCode();
				final int modifiers = event.getModifiers();
				if ((modifiers & java.awt.Event.CTRL_MASK) != 0 && keyCode == KeyEvent.VK_C) {
					event.consume();
					new SpiClipboardPrinter().print(table, null);
				}
			}
		}
	};

	private static final MouseListener POPUP_MENU_MOUSE_LISTENER = TablePopupMenu
			.createMouseListenerForTablePopupMenu();

	private boolean popupMenuDisabled;

	/**
	 * Constructeur.
	 */
	public SpiTable_Abs() {
		// on utilise le modèle par défaut créé par la méthode createDefaultDataModel()
		// ci-dessus
		this(null);
	}

	/**
	 * Constructeur.
	 * 
	 * @param dataModel Modèle pour les données
	 */
	protected SpiTable_Abs(final SpiListTableModel<TypeValue> dataModel) {
		super(dataModel);
		// la table ne crée pas automatiquement les colonnes à partir du dataModel,
		// et les colonnes seront définies en utilisant la méthode addColumn ci-dessous
		setAutoCreateColumnsFromModel(false);
		// le rowSorter sera automatiquement ajouté lors des appels à setModel (la
		// classe SpiListTableModel définit getColumnClass à partir des données)
		setAutoCreateRowSorter(true);
		// fond de couleur blanc (plutôt que gris en look and feel Nimbus ; utile pour
		// le rendu des cases à cocher dans le tableau)
		setBackground(Color.WHITE);
		// listener pour surcharger la copie dans presse-papier (format pour Excel et
		// non format par défaut de Swing)
		addKeyListener(CLIPBOARD_KEY_LISTENER);
		// listener pour afficher le popup menu
		addMouseListener(POPUP_MENU_MOUSE_LISTENER);

		// lors d'un focus lost, on stoppe l'édition de cellule s'il y en a une en cours
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}

	@Override
	protected TableModel createDefaultDataModel() {
		return new SpiTableModel<TypeValue>(this);
	}

	/**
	 * Ajoute une colonne dans la table.
	 * 
	 * @param p_attribute Attribut d'un DTO à afficher dans la colonne à partir de
	 *                    l'énumération des attributs correspondant aux DTOs de la
	 *                    liste affichée.
	 *                    <p>
	 *                    Par exemple, <code>PersonneAttributes_Enum.nom</code> pour
	 *                    une liste de PersonneDto.
	 * @param p_libelle   Libellé à afficher en entête de la colonne, ou null pour
	 *                    prendre à la place la description dans l'attribut
	 * @return this (fluent)
	 */
	public SpiTable_Abs<TypeValue> addColumn(final AttributesNames_Itf p_attribute, final String p_libelle) {
		// si le libellé est null, alors cela prendra la description de l'attribut comme
		// libellé dans l'entête de la colonne
		final AttributesNames_Itf[] v_attributesChain = { p_attribute };
		addColumn(v_attributesChain, p_libelle);
		return this;
	}

	/**
	 * Ajoute une colonne dans la table.
	 * 
	 * @param p_column  Colonne d'une Entity à afficher dans la colonne à partir de
	 *                  l'énumération des colonnes correspondant aux Entitys de la
	 *                  liste affichée.
	 *                  <p>
	 *                  Par exemple, <code>PersonneColumns_Enum.nom</code> pour une
	 *                  liste de PersonneEntity.
	 * @param p_libelle Libellé à afficher en entête de la colonne, ou null pour
	 *                  prendre à la place la description dans la colonne
	 * @return this (fluent)
	 */
	public SpiTable_Abs<TypeValue> addColumn(final ColumnsNames_Itf p_column, final String p_libelle) {
		// si le libellé est null, alors cela prendra la description de l'attribut comme
		// libellé dans l'entête de la colonne
		final ColumnsNames_Itf[] v_columnsChain = { p_column };
		addColumn(v_columnsChain, p_libelle);
		return this;
	}

	/**
	 * Ajoute une colonne dans la table.
	 * 
	 * @param p_attributesChain Attributs chainés de DTOs dont le dernier est à
	 *                          afficher dans la colonne à partir de l'énumération
	 *                          des attributs correspondant aux DTOs de la liste
	 *                          affichée.
	 *                          <p>
	 *                          Par exemple,
	 *                          <code>new AttributesNames_Itf[] {PersonneAttributes_Enum.grade, GradeAttributes_Enum.trigramme}</code>
	 *                          pour afficher le trigramme du grade dans une liste
	 *                          de PersonneDto.
	 * @param p_libelle         Libellé à afficher en entête de la colonne, ou null
	 *                          pour prendre à la place la description du dernier
	 *                          attribut dans p_attributesChain
	 * @return this (fluent)
	 */
	public SpiTable_Abs<TypeValue> addColumn(final AttributesNames_Itf[] p_attributesChain, final String p_libelle) {
		final int v_modelIndex = getColumnCount();
		final TableColumn tableColumn = new TableColumn(v_modelIndex);
		// on met la liste des énumérations des attributs comme identifier dans le
		// TableColumn pour s'en servir dans SpiTableModel
		// (le type list a l'avantage d'implémenter equals, contrairement aux tableaux,
		// pour que la recherche de colonnes par identifier fonctionne)
		final List<AttributesNames_Itf> v_attributesChain = Arrays.asList(p_attributesChain);
		tableColumn.setIdentifier(v_attributesChain);
		if (p_libelle == null) {
			// on prend par défaut le "get_description()" de la dernière énumération du
			// tableau si le développeur n'a pas précisé de libellé,
			// ainsi la description de l'attribut dans le modèle peut être utilisée pour les
			// libellés IHM
			tableColumn.setHeaderValue(v_attributesChain.get(v_attributesChain.size() - 1).getDescription());
		} else {
			// le développeur a donné un libellé pour l'entête de cette colonne
			tableColumn.setHeaderValue(p_libelle);
		}
		// ajoute la colonne dans la table
		super.addColumn(tableColumn);
		return this;
	}

	/**
	 * Ajoute une colonne dans la table.
	 * 
	 * @param p_columnsChain Attributs chainés de Entitys dont le dernier est à
	 *                       afficher dans la colonne à partir de l'énumération des
	 *                       colonnes correspondant aux Entitys de la liste
	 *                       affichée.
	 *                       <p>
	 *                       Par exemple,
	 *                       <code>new ColumnsNames_Itf[] {PersonneColumns_Enum.grade, GradeColumns_Enum.trigramme}</code>
	 *                       pour afficher le trigramme du grade dans une liste de
	 *                       PersonneEntity.
	 * @param p_libelle      Libellé à afficher en entête de la colonne, ou null
	 *                       pour prendre à la place la description de la derniere
	 *                       colonne dans p_columnsChain
	 * @return this (fluent)
	 */
	public SpiTable_Abs<TypeValue> addColumn(final ColumnsNames_Itf[] p_columnsChain, final String p_libelle) {
		final int v_modelIndex = getColumnCount();
		final TableColumn tableColumn = new TableColumn(v_modelIndex);
		// on met la liste des énumérations des colonnes comme identifier dans le
		// TableColumn pour s'en servir dans SpiTableModel
		// (le type list a l'avantage d'implémenter equals, contrairement aux tableaux,
		// pour que la recherche de colonnes par identifier fonctionne)
		final List<ColumnsNames_Itf> v_columnsChain = Arrays.asList(p_columnsChain);
		tableColumn.setIdentifier(v_columnsChain);
		if (p_libelle == null) {
			// on prend par défaut le "get_description()" de la dernière énumération du
			// tableau si le développeur n'a pas précisé de libellé,
			// ainsi la description de l'attribut dans le modèle peut être utilisée pour les
			// libellés IHM
			tableColumn.setHeaderValue(v_columnsChain.get(v_columnsChain.size() - 1).getPhysicalColumnName());
		} else {
			// le développeur a donné un libellé pour l'entête de cette colonne
			tableColumn.setHeaderValue(p_libelle);
		}
		// ajoute la colonne dans la table
		super.addColumn(tableColumn);
		return this;
	}

	/**
	 * Méthode typée pour définir un renderer spécifique pour les cellules d'une
	 * colonne dans la table.
	 * 
	 * @param p_attribute    Attribut d'un DTO pour une colonne existante
	 * @param p_cellRenderer Renderer des cellules dans cette colonne
	 */
	public void setColumnCellRenderer(final AttributesNames_Itf p_attribute, final TableCellRenderer p_cellRenderer) {
		final AttributesNames_Itf[] v_attributesChain = { p_attribute };
		setColumnCellRenderer(v_attributesChain, p_cellRenderer);
	}

	/**
	 * Méthode typée pour définir un renderer spécifique pour les cellules d'une
	 * colonne dans la table.
	 * 
	 * @param p_column       Attribut d'une Entity pour une colonne existante
	 * @param p_cellRenderer Renderer des cellules dans cette colonne
	 */
	public void setColumnCellRenderer(final ColumnsNames_Itf p_column, final TableCellRenderer p_cellRenderer) {
		final ColumnsNames_Itf[] v_columnsChain = { p_column };
		setColumnCellRenderer(v_columnsChain, p_cellRenderer);
	}

	/**
	 * Méthode typée pour définir un renderer spécifique pour les cellules d'une
	 * colonne dans la table.
	 * 
	 * @param p_attributesChain Attributs d'un DTO pour une colonne existante
	 * @param p_cellRenderer    Renderer des cellules dans cette colonne
	 */
	public void setColumnCellRenderer(final AttributesNames_Itf[] p_attributesChain,
			final TableCellRenderer p_cellRenderer) {
		getColumn(p_attributesChain).setCellRenderer(p_cellRenderer);
	}

	/**
	 * Méthode typée pour définir un renderer spécifique pour les cellules d'une
	 * colonne dans la table.
	 * 
	 * @param p_columnsChain Attributs d'une Entity pour une colonne existante
	 * @param p_cellRenderer Renderer des cellules dans cette colonne
	 */
	public void setColumnCellRenderer(final ColumnsNames_Itf[] p_columnsChain, final TableCellRenderer p_cellRenderer) {
		getColumn(p_columnsChain).setCellRenderer(p_cellRenderer);
	}

	/**
	 * Méthode typée pour définir un editor spécifique pour les cellules d'une
	 * colonne dans la table.
	 * 
	 * @param p_attribute  Attribut d'un DTO pour une colonne existante
	 * @param p_cellEditor Editor des cellules dans cette colonne
	 */
	public void setColumnCellEditor(final AttributesNames_Itf p_attribute, final TableCellEditor p_cellEditor) {
		final AttributesNames_Itf[] v_attributesChain = { p_attribute };
		setColumnCellEditor(v_attributesChain, p_cellEditor);
	}

	/**
	 * Méthode typée pour définir un editor spécifique pour les cellules d'une
	 * colonne dans la table.
	 * 
	 * @param p_column     Colonne d'une entity pour une colonne existante
	 * @param p_cellEditor Editor des cellules dans cette colonne
	 */
	public void setColumnCellEditor(final ColumnsNames_Itf p_column, final TableCellEditor p_cellEditor) {
		final ColumnsNames_Itf[] v_columnsChain = { p_column };
		setColumnCellEditor(v_columnsChain, p_cellEditor);
	}

	/**
	 * Méthode typée pour définir un editor spécifique pour les cellules d'une
	 * colonne dans la table.
	 * 
	 * @param p_attributesChain Attributs d'un DTO pour une colonne existante
	 * @param p_cellEditor      Editor des cellules dans cette colonne
	 */
	public void setColumnCellEditor(final AttributesNames_Itf[] p_attributesChain, final TableCellEditor p_cellEditor) {
		getColumn(p_attributesChain).setCellEditor(p_cellEditor);
	}

	/**
	 * Méthode typée pour définir un editor spécifique pour les cellules d'une
	 * colonne dans la table.
	 * 
	 * @param p_columnsChain Colonnes d'une Entity pour une colonne existante
	 * @param p_cellEditor   Editor des cellules dans cette colonne
	 */
	public void setColumnCellEditor(final ColumnsNames_Itf[] p_columnsChain, final TableCellEditor p_cellEditor) {
		getColumn(p_columnsChain).setCellEditor(p_cellEditor);
	}

	/**
	 * Définit si une colonne est éditable. Note: En général, il faut augmenter la
	 * hauteur des lignes pour l'édition, par exemple : table.setRowHeight(22).
	 * <p>
	 * Le type de l'éditeur est déterminé selon columnClass du model et donc selon
	 * les données dans la liste ou, s'il n'y en a pas, selon le type déclaré dans
	 * l'attribut.
	 * 
	 * @param p_attribute Attribut d'un DTO pour une colonne existante
	 * @param editable    boolean
	 */
	public void setColumnEditable(final AttributesNames_Itf p_attribute, final boolean editable) {
		final AttributesNames_Itf[] v_attributesChain = { p_attribute };
		setColumnEditable(v_attributesChain, editable);
	}

	/**
	 * Définit si une colonne est éditable. Note: En général, il faut augmenter la
	 * hauteur des lignes pour l'édition, par exemple : table.setRowHeight(22).
	 * <p>
	 * Le type de l'éditeur est déterminé selon columnClass du model et donc selon
	 * les données dans la liste ou, s'il n'y en a pas, selon le type déclaré dans
	 * l'attribut.
	 * 
	 * @param p_column Colonne d'une Entity pour une colonne existante
	 * @param editable boolean
	 */
	public void setColumnEditable(final ColumnsNames_Itf p_column, final boolean editable) {
		final ColumnsNames_Itf[] v_columnsChain = { p_column };
		setColumnEditable(v_columnsChain, editable);
	}

	/**
	 * Définit si une colonne est éditable. Note: En général, il faut augmenter la
	 * hauteur des lignes pour l'édition, par exemple : table.setRowHeight(22).
	 * <p>
	 * Le type de l'éditeur est déterminé selon columnClass du model et donc selon
	 * les données dans la liste ou, s'il n'y en a pas, selon le type déclaré dans
	 * l'attribut.
	 * 
	 * @param p_attributesChain Attributs d'un DTO pour une colonne existante
	 * @param editable          boolean
	 */
	public void setColumnEditable(final AttributesNames_Itf[] p_attributesChain, final boolean editable) {
		final TableColumn tableColumn = getColumn(p_attributesChain);
		if (editable) {
			final Class<?> typeClass = getModel().getColumnClass(tableColumn.getModelIndex());
			final TableCellEditor tableCellEditor = getDefaultEditor(typeClass);
			if (tableCellEditor == null) {
				throw new IllegalStateException("Aucun editor défini pour le type " + typeClass);
			}
			tableColumn.setCellEditor(tableCellEditor);
		} else {
			tableColumn.setCellEditor(null);
		}
	}

	/**
	 * Définit si une colonne est éditable. Note: En général, il faut augmenter la
	 * hauteur des lignes pour l'édition, par exemple : table.setRowHeight(22).
	 * <p>
	 * Le type de l'éditeur est déterminé selon columnClass du model et donc selon
	 * les données dans la liste ou, s'il n'y en a pas, selon le type déclaré dans
	 * l'attribut.
	 * 
	 * @param p_columnsChain Colonnes d'une Entity pour une colonne existante
	 * @param editable       boolean
	 */
	public void setColumnEditable(final ColumnsNames_Itf[] p_columnsChain, final boolean editable) {
		final TableColumn tableColumn = getColumn(p_columnsChain);
		if (editable) {
			final Class<?> typeClass = getModel().getColumnClass(tableColumn.getModelIndex());
			final TableCellEditor tableCellEditor = getDefaultEditor(typeClass);
			if (tableCellEditor == null) {
				throw new IllegalStateException("Aucun editor défini pour le type " + typeClass);
			}
			tableColumn.setCellEditor(tableCellEditor);
		} else {
			tableColumn.setCellEditor(null);
		}
	}

	/**
	 * Retourne la colonne correspondant à un attribut dans cette table.
	 * 
	 * @param p_attribute Attribut d'une colonne existante dans cette table
	 * @return TableColumn
	 */
	public TableColumn getColumn(final AttributesNames_Itf p_attribute) {
		final AttributesNames_Itf[] v_attributesChain = { p_attribute };
		return getColumn(v_attributesChain);
	}

	/**
	 * Retourne la colonne correspondant à un attribut dans cette table.
	 * 
	 * @param p_column Colonne d'une colonne existante dans cette table
	 * @return TableColumn
	 */
	public TableColumn getColumn(final ColumnsNames_Itf p_column) {
		final ColumnsNames_Itf[] v_columnsChain = { p_column };
		return getColumn(v_columnsChain);
	}

	/**
	 * Retourne la colonne correspondant aux attributs dans cette table.
	 * 
	 * @param p_attributesChain Attribut d'une colonne existante dans cette table
	 * @return TableColumn
	 */
	public TableColumn getColumn(final AttributesNames_Itf[] p_attributesChain) {
		return super.getColumn(Arrays.asList(p_attributesChain));
	}

	/**
	 * Retourne la colonne correspondant aux attributs dans cette table.
	 * 
	 * @param p_columnsChain Colonne d'une colonne existante dans cette table
	 * @return TableColumn
	 */
	public TableColumn getColumn(final ColumnsNames_Itf[] p_columnsChain) {
		return super.getColumn(Arrays.asList(p_columnsChain));
	}

	/**
	 * @return Liste de toutes les colonnes dans le ColumnModel.
	 */
	public List<TableColumn> getColumns() {
		return Collections.unmodifiableList(Collections.list(getColumnModel().getColumns()));
	}

	/**
	 * @return Liste des colonnes masquées.
	 */
	public List<TableColumn> getHiddenColumns() {
		final List<TableColumn> columns = getColumns();
		final List<TableColumn> result = new ArrayList<TableColumn>();
		for (final TableColumn column : columns) {
			if (isColumnHidden(column)) {
				result.add(column);
			}
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Retourne true si la colonne est masquée, c'est-à-dire si column.getMaxWidth()
	 * <= 0.
	 * 
	 * @param column TableColumn
	 * @return boolean
	 */
	public boolean isColumnHidden(final TableColumn column) {
		// une colonne est considérée comme masquée selon l'état de son attribut
		// maxWidth
		// (cela sera pris en compte à l'affichage et aussi dans la méthode
		// SpiBasicTable#adjustColumnWidths)
		return column.getMaxWidth() <= 0;
	}

	/**
	 * Définit si une colonne est masquée ou non.
	 * 
	 * @param column TableColumn
	 * @param hidden boolean
	 */
	public void setColumnHidden(final TableColumn column, final boolean hidden) {
		if (hidden) {
			// masque la colonne (minWidth doit être remis à 0 s'il ne l'est pas sinon
			// maxWidth n'est pas réellement mis à 0)
			column.setMinWidth(0);
			column.setMaxWidth(0);
		} else {
			// réaffiche une colonne
			column.setMaxWidth(Integer.MAX_VALUE);
			// ajuste la largeur de la colonne par rapport aux données
			adjustColumnWidths();
		}
		invalidate();
	}

	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();

		if (!popupMenuDisabled) {
			TablePopupMenu.configureEnclosingScrollPane(this);
		}
	}

	/**
	 * Désactive le menu contextuel de ce tableau ainsi que le bouton de sélection
	 * des colonnes sur le scrollPane.
	 */
	public void disablePopupMenu() {
		// listener pour afficher le popup menu
		removeMouseListener(POPUP_MENU_MOUSE_LISTENER);
		TablePopupMenu.unconfigureEnclosingScrollPane(this);
		popupMenuDisabled = true;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int rowIndex, final int vColIndex) {
		// Surcharge pour la gestion des lignes de couleurs alternées.
		final Component component = super.prepareRenderer(renderer, rowIndex, vColIndex);
		if (BICOLOR_LINE != null && !isRowSelected(rowIndex)) {
			if (rowIndex % 2 == 0) {
				component.setBackground(BICOLOR_LINE);
			} else {
				component.setBackground(getBackground());
			}
		}

		return component;
	}

	/**
	 * Affichage de la boîte de dialogue d'export ou d'impression pour le choix de
	 * printer en paramètre (csv, html, java...)
	 * 
	 * @param printer SpiPrinter
	 * @throws IOException e
	 */
	public void showPrintExportDialog(final SpiPrinter printer) throws IOException {
		printer.print(this);
	}
}
