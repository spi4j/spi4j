/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * Définit un renderer pour représenter un Boolean à l'aide d'une checkbox dans une JTable.
 * @author MINARM
 */
public class SpiBooleanTableCellRenderer extends JCheckBox implements TableCellRenderer
{
   private static final long serialVersionUID = 1L;

   private static final Border BORDER = UIManager.getBorder("Table.focusCellHighlightBorder");

   /**
    * Constructeur.
    */
   public SpiBooleanTableCellRenderer ()
   {
      super();
      setHorizontalAlignment(CENTER);
      setOpaque(true);
      setEnabled(false);
      setBorderPainted(true);
   }

   @Override
   public Component getTableCellRendererComponent (final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column)
   {

      if (isSelected)
      {
         super.setForeground(table.getSelectionForeground());
         super.setBackground(table.getSelectionBackground());
      }
      else
      {
         super.setForeground(table.getForeground());
         if (SpiTable_Abs.BICOLOR_LINE != null && row % 2 == 0)
         {
            super.setBackground(SpiTable_Abs.BICOLOR_LINE);
         }
         else
         {
            super.setBackground(table.getBackground());
         }
      }

      if (hasFocus)
      {
         setBorder(BORDER);
      }
      else
      {
         setBorder(null);
      }

      setEnabled(table.isCellEditable(row, column));

      if (value instanceof Boolean)
      {
         final boolean selected = ((Boolean) value).booleanValue();
         this.setSelected(selected);
         // this.setToolTipText(selected ? "vrai" : "false");
         return this;
      }
      final JLabel label = (JLabel) table.getDefaultRenderer(String.class).getTableCellRendererComponent(table, null,
               isSelected, hasFocus, row, column);
      if (value == null)
      {
         label.setText(null);
      }
      else
      {
         label.setText("??");
      }
      return label;
   }
}
