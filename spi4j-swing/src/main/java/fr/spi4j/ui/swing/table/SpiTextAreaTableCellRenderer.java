/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Component;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * TableCellRenderer pour du texte multi-lignes avec wrapping et hauteur automatiques (alternative à SpiMultiLineTableCellRenderer}.
 * @author MINARM
 * @see JTextArea
 */
public class SpiTextAreaTableCellRenderer extends JTextArea implements TableCellRenderer
{
   private static final long serialVersionUID = 1L;

   private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

   // Column heights are placed in this Map
   private final Map<JTable, Map<Object, Map<Object, Integer>>> tablecellSizes = new HashMap<JTable, Map<Object, Map<Object, Integer>>>();

   /**
    * Constructeur.
    */
   public SpiTextAreaTableCellRenderer ()
   {
      super();
      setLineWrap(true);
      setWrapStyleWord(true);
   }

   @Override
   public Component getTableCellRendererComponent (final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column)
   {
      // set the Font, Color, etc.
      renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      setForeground(renderer.getForeground());
      setBackground(renderer.getBackground());
      setBorder(renderer.getBorder());
      setFont(renderer.getFont());
      setText(renderer.getText());

      final TableColumnModel columnModel = table.getColumnModel();
      setSize(columnModel.getColumn(column).getWidth(), 0);
      int heightWanted = (int) getPreferredSize().getHeight();
      addSize(table, row, column, heightWanted);
      heightWanted = findTotalMaximumRowSize(table, row);
      if (heightWanted != table.getRowHeight(row))
      {
         table.setRowHeight(row, heightWanted);
      }
      return this;
   }

   /**
    * @param table
    *           JTable
    * @param row
    *           int
    * @param column
    *           int
    * @param height
    *           int
    */
   private void addSize (final JTable table, final int row, final int column, final int height)
   {
      Map<Object, Map<Object, Integer>> rowsMap = tablecellSizes.get(table);
      if (rowsMap == null)
      {
         rowsMap = new HashMap<Object, Map<Object, Integer>>();
         tablecellSizes.put(table, rowsMap);
      }
      Map<Object, Integer> rowheightsMap = rowsMap.get(row);
      if (rowheightsMap == null)
      {
         rowheightsMap = new HashMap<Object, Integer>();
         rowsMap.put(row, rowheightsMap);
      }
      rowheightsMap.put(column, height);
   }

   /**
    * @param table
    *           JTable
    * @param row
    *           int
    * @return int
    */
   private int findTotalMaximumRowSize (final JTable table, final int row)
   {
      int maximumHeight = 0;
      final Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
      while (columns.hasMoreElements())
      {
         final TableColumn tc = columns.nextElement();
         final TableCellRenderer cellRenderer = tc.getCellRenderer();
         if (cellRenderer instanceof SpiTextAreaTableCellRenderer)
         {
            final SpiTextAreaTableCellRenderer tar = (SpiTextAreaTableCellRenderer) cellRenderer;
            maximumHeight = Math.max(maximumHeight, tar.findMaximumRowSize(table, row));
         }
      }
      return maximumHeight;
   }

   /**
    * @param table
    *           JTable
    * @param row
    *           int
    * @return int
    */
   private int findMaximumRowSize (final JTable table, final int row)
   {
      final Map<Object, Map<Object, Integer>> rows = tablecellSizes.get(table);
      if (rows == null)
      {
         return 0;
      }
      final Map<Object, Integer> rowheights = rows.get(row);
      if (rowheights == null)
      {
         return 0;
      }
      int maximumHeight = 0;
      for (final Map.Entry<Object, Integer> entry : rowheights.entrySet())
      {
         final int cellHeight = entry.getValue();
         maximumHeight = Math.max(maximumHeight, cellHeight);
      }
      return maximumHeight;
   }
}
