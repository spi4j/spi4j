/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import fr.spi4j.ui.swing.SpiSwingUtilities;
import fr.spi4j.ui.swing.fields.SpiCheckBox;
import fr.spi4j.ui.swing.fields.SpiDateField;
import fr.spi4j.ui.swing.fields.SpiDoubleField;
import fr.spi4j.ui.swing.fields.SpiIntegerField;
import fr.spi4j.ui.swing.fields.SpiLongField;
import fr.spi4j.ui.swing.fields.SpiStringField;

/**
 * Composant Table basique servant de base à SpiListTable.
 * @author MINARM
 */
@SuppressWarnings("deprecation")
public class SpiBasicTable extends JTable
{
   private static final int ADJUST_COLUMN_WIDTHS_MAX_ROWS = 50;

   private static final long serialVersionUID = 1L;

   // Singleton statique pour renderers par défaut des cellules.
   private static final Map<Class<?>, TableCellRenderer> DEFAULT_RENDERERS = new HashMap<Class<?>, TableCellRenderer>(
            25);

   // Singleton statique pour editors par défaut des cellules.
   private static final Map<Class<?>, TableCellEditor> DEFAULT_EDITORS = new HashMap<Class<?>, TableCellEditor>(25);

   private static final KeyHandler KEY_HANDLER = new KeyHandler();

   /**
    * KeyAdapter.
    * @author MINARM
    */
   private static class KeyHandler extends KeyAdapter
   {
      /**
       * Constructeur.
       */
      KeyHandler ()
      {
         super();
      }

      @Override
      public void keyPressed (final KeyEvent event)
      {
         if (event.getSource() instanceof SpiBasicTable)
         {
            ((SpiBasicTable) event.getSource()).keyPressed(event);
         }
      }
   }

   /**
    * TableHeader.
    * @author MINARM
    */
   private static class TableHeader extends JTableHeader
   {
      private static final long serialVersionUID = 1L;

      /**
       * Constructeur.
       * @param columnModel
       *           TableColumnModel
       */
      TableHeader (final TableColumnModel columnModel)
      {
         super(columnModel);
      }

      @Override
      public String getToolTipText (final MouseEvent e)
      {
         final Point p = e.getPoint();
         final int index = columnModel.getColumnIndexAtX(p.x);
         final String tip;
         if (index != -1)
         {
            tip = String.valueOf(columnModel.getColumn(index).getHeaderValue());
         }
         else
         {
            tip = super.getToolTipText(e);
         }
         return tip;
      }
   }

   /**
    * Constructeur.
    * @param dataModel
    *           Modèle pour les données (par exemple, SpiTableModel)
    */
   public SpiBasicTable (final TableModel dataModel)
   {
      super(dataModel);
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      // setAutoCreateColumnsFromModel(false);
      // par défaut, on laisse AUTO_RESIZE_SUBSEQUENT_COLUMNS
      // setAutoResizeMode(AUTO_RESIZE_OFF);
      // setDragEnabled(true);
      addKeyListener(KEY_HANDLER);
   }

   /**
    * Adapte les largeurs des colonnes selon les données de cette table. <br/>
    * Pour chaque colonne la taille préférée est déterminée selon la valeur (et le renderer) du header et selon la valeur (et le renderer) de chaque cellule.
    */
   public void adjustColumnWidths ()
   {
      if (ADJUST_COLUMN_WIDTHS_MAX_ROWS > 0)
      {
         TableColumn tableColumn;
         TableCellRenderer renderer;
         Object value;
         Component component;
         final int columnCount = getColumnCount();
         final int rowCount = Math.min(getRowCount(), ADJUST_COLUMN_WIDTHS_MAX_ROWS);
         int columnWidth;
         final int maxWidth = 250; // taille ajustée maximum (15 minimum par défaut)

         // Boucle sur chaque colonne et chaque ligne.
         // Trouve le max de la largeur du header et de chaque cellule
         // et fixe la largeur de la colonne en fonction.
         for (int colNum = 0; colNum < columnCount; colNum++)
         {
            tableColumn = columnModel.getColumn(colNum);
            if (tableColumn.getMaxWidth() <= 0)
            {
               continue; // colonne invisible
            }

            if (getTableHeader() != null)
            {
               renderer = getTableHeader().getDefaultRenderer();
               value = tableColumn.getHeaderValue();
               component = renderer.getTableCellRendererComponent(this, value, false, false, -1, colNum);
               columnWidth = component.getPreferredSize().width + 10;
            }
            else
            {
               columnWidth = 10;
            }
            renderer = getCellRenderer(-1, colNum);

            for (int rowNum = 0; rowNum < rowCount; rowNum++)
            {
               value = getValueAt(rowNum, colNum);
               component = renderer.getTableCellRendererComponent(this, value, false, false, rowNum, colNum);
               columnWidth = Math.max(columnWidth, component.getPreferredSize().width);
            }
            columnWidth = Math.min(maxWidth, columnWidth);

            tableColumn.setPreferredWidth(columnWidth + getIntercellSpacing().width);
         }
      }
   }

   @Override
   protected void configureEnclosingScrollPane ()
   {
      // Si cette table est la viewportView d'un JScrollPane (la situation habituelle),
      // configure ce ScrollPane en positionnant la barre verticale à "always"
      // (et en installant le tableHeader comme columnHeaderView).
      super.configureEnclosingScrollPane();

      final Container parent = getParent();
      if (parent instanceof JViewport && parent.getParent() instanceof JScrollPane)
      {
         final JScrollPane scrollPane = (JScrollPane) parent.getParent();
         if (scrollPane.getVerticalScrollBar() != null)
         {
            scrollPane.getVerticalScrollBar().setFocusable(false);
         }
         if (scrollPane.getHorizontalScrollBar() != null)
         {
            scrollPane.getHorizontalScrollBar().setFocusable(false);
         }

         final JViewport viewport = scrollPane.getViewport();
         if (viewport == null || viewport.getView() != this)
         {
            return;
         }

         scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      }
   }

   @Override
   protected void createDefaultRenderers ()
   {
      final Map<Class<?>, TableCellRenderer> map = getDefaultTableCellRenderers();
      super.defaultRenderersByColumnClass = new Hashtable<>();
      super.defaultRenderersByColumnClass.putAll(map);
   }
   
   @Override
   protected void createDefaultEditors ()
   {
      final Map<Class<?>, TableCellEditor> map = getDefaultTableCellEditors();
      super.defaultEditorsByColumnClass = new Hashtable<>();
      super.defaultEditorsByColumnClass.putAll(map);
   }

   /**
    * Retourne la map par défaut des renderers de cellules avec pour clé le type des valeurs.
    * @return Map
    */
   private Map<Class<?>, TableCellRenderer> getDefaultTableCellRenderers ()
   {
      if (DEFAULT_RENDERERS.isEmpty())
      {
         DEFAULT_RENDERERS.put(Boolean.class, new SpiBooleanTableCellRenderer());
         DEFAULT_RENDERERS.put(Double.class, new SpiDoubleTableCellRenderer());
         DEFAULT_RENDERERS.put(Float.class, new SpiDoubleTableCellRenderer());
         DEFAULT_RENDERERS.put(java.math.BigDecimal.class, new SpiDoubleTableCellRenderer());
         DEFAULT_RENDERERS.put(Integer.class, new SpiIntegerTableCellRenderer());
         DEFAULT_RENDERERS.put(Long.class, new SpiIntegerTableCellRenderer());
         DEFAULT_RENDERERS.put(java.math.BigInteger.class, new SpiIntegerTableCellRenderer());
         DEFAULT_RENDERERS.put(Date.class, new SpiDateTableCellRenderer());
         DEFAULT_RENDERERS.put(java.sql.Date.class, new SpiDateTableCellRenderer());
         DEFAULT_RENDERERS.put(java.sql.Timestamp.class, new SpiDateTableCellRenderer());
         DEFAULT_RENDERERS.put(ArrayList.class, new SpiListTableCellRenderer()); 
         DEFAULT_RENDERERS.put(Object.class, new SpiDefaultTableCellRenderer());
         DEFAULT_RENDERERS.put(String.class, new SpiDefaultTableCellRenderer());
         DEFAULT_RENDERERS.put(ImageIcon.class, new SpiImageIconTableCellRenderer());
         DEFAULT_RENDERERS.put(Component.class, new SpiComponentTableCellRenderer());
      }
      return DEFAULT_RENDERERS;
   }

   /**
    * Retourne la map par défaut des editors de cellules avec pour clé le type des valeurs.
    * @return Map
    */
   private Map<Class<?>, TableCellEditor> getDefaultTableCellEditors ()
   {
      if (DEFAULT_EDITORS.isEmpty())
      {
         DEFAULT_EDITORS.put(Boolean.class, new SpiDefaultCellEditor(new SpiCheckBox()));
         DEFAULT_EDITORS.put(Double.class, new SpiDefaultCellEditor(new SpiDoubleField()));
         DEFAULT_EDITORS.put(Integer.class, new SpiDefaultCellEditor(new SpiIntegerField()));
         DEFAULT_EDITORS.put(Long.class, new SpiDefaultCellEditor(new SpiLongField()));
         DEFAULT_EDITORS
                  .put(Date.class, new SpiDateActionTableCellEditor(new SpiDefaultCellEditor(new SpiDateField())));
         DEFAULT_EDITORS.put(String.class, new SpiDefaultCellEditor(new SpiStringField()));
         DEFAULT_EDITORS.put(Object.class, new SpiDefaultCellEditor(new SpiStringField()));
      }
      return DEFAULT_EDITORS;
   }

   @Override
   public TableCellRenderer getDefaultRenderer (final Class<?> columnClass)
   {
      // si c'est une instance de Collection on prend le renderer de ArrayList
      // (car par ex., le résultat de Collections.unmodifiableList est une interface de List
      // sans classe "connue" donc son renderer par défaut n'est pas paramétrable)
      if (columnClass != null && Collection.class.isAssignableFrom(columnClass))
      {
         return super.getDefaultRenderer(ArrayList.class); 
      }
      return super.getDefaultRenderer(columnClass);
   }

   @Override
   protected JTableHeader createDefaultTableHeader ()
   {
      return new TableHeader(columnModel);
   }

   @Override
   public String getToolTipText (final MouseEvent event)
   {
      final int row = rowAtPoint(event.getPoint());
      final int column = columnAtPoint(event.getPoint());
      if (row != -1 && column != -1)
      {
         String tip = super.getToolTipText(event);
         if (tip == null)
         {
            tip = getTextAt(row, column);
            if (tip == null || tip.length() == 0)
            {
               tip = super.getToolTipText();
            }
         }
         return tip;
      }
      return super.getToolTipText();
   }

   /**
    * Renvoie le texte affiché à la position demandée.
    * @return String
    * @param row
    *           int
    * @param column
    *           int
    */
   public String getTextAt (final int row, final int column)
   {
      final Object value = getValueAt(row, column);

      String text = "";
      if (value != null)
      {
         final TableCellRenderer renderer = getCellRenderer(row, column);
         final Component rendererComponent = renderer.getTableCellRendererComponent(this, value, false, false, row,
                  column);
         // text depuis JLabel, JTextComponent ou JCheckBox
         text = SpiSwingUtilities.getTextFromRendererComponent(rendererComponent);
         if (text == null)
         {
            // text par défaut
            text = value.toString();
         }
      }
      return text;
   }

   /**
    * Gestion des événements clavier sur cette table.
    * @param event
    *           KeyEvent
    */
   protected void keyPressed (final KeyEvent event)
   {
      final int keyCode = event.getKeyCode();
      final int modifiers = event.getModifiers();
      if ((modifiers & Event.CTRL_MASK) != 0 && keyCode == KeyEvent.VK_ADD)
      {
         adjustColumnWidths();
      }
      // else if (modifiers == 0)
      // {
      // final int selectedColumn = getSelectedColumn() != -1 ? getSelectedColumn() : 0;
      // final int selectedRow = getSelectedRow() != -1 ? getSelectedRow() : 0;
      // final int rowCount = getRowCount();
      // if (isCellEditable(selectedRow, selectedColumn) || rowCount == 0)
      // {
      // return;
      // }
      // final String keyChar = String.valueOf(event.getKeyChar());
      // String text;
      // for (int i = selectedRow + 1; i < rowCount; i++)
      // {
      // text = getTextAt(i, selectedColumn);
      // if (text != null && text.regionMatches(true, 0, keyChar, 0, 1))
      // {
      // setRowSelectionInterval(i, i);
      // setColumnSelectionInterval(selectedColumn, selectedColumn);
      // scrollRectToVisible(getCellRect(i, selectedColumn, true));
      // return;
      // }
      // }
      // for (int i = 0; i <= selectedRow; i++)
      // {
      // text = getTextAt(i, selectedColumn);
      // if (text != null && text.regionMatches(true, 0, keyChar, 0, 1))
      // {
      // setRowSelectionInterval(i, i);
      // setColumnSelectionInterval(selectedColumn, selectedColumn);
      // scrollRectToVisible(getCellRect(i, selectedColumn, true));
      // return;
      // }
      // }
      // }
   }
}
