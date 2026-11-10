/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * Définit un renderer pour représenter un Component AWT ou Swing se dessinant lui-même dans une JTable (par ex. un JLabel avec icône, texte, toolTipText, fonte, foreground, horizontalAlignement ...).
 * @author MINARM
 */
public class SpiComponentTableCellRenderer extends SpiDefaultTableCellRenderer
{
   private static final long serialVersionUID = 1L;

   private static final String ERROR_TEXT = "??";

   private boolean error;

   @Override
   public Component getTableCellRendererComponent (final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column)
   {
      Component component;
      if (error || value == null)
      {
         setText(null);
         error = false;
         component = this;
      }

      if (value instanceof Component)
      {
         component = (Component) value;
      }
      else
      {
         this.setText(ERROR_TEXT);
         error = true;
         component = this;
      }

      if (component instanceof JLabel)
      {
         final Component superComponent = super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row,
                  column);
         if (superComponent instanceof JLabel)
         {
            final JLabel label = (JLabel) component;
            final JLabel superLabel = (JLabel) superComponent;
            label.setBackground(superLabel.getBackground());
            label.setBorder(superLabel.getBorder());
         }
      }

      return component;
   }
}
