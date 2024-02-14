/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * Définit un renderer pour représenter une Icon dans une JTable.
 * @author MINARM
 */
public class SpiImageIconTableCellRenderer extends SpiDefaultTableCellRenderer
{
   private static final long serialVersionUID = 1L;

   private static final String ERROR_TEXT = "??";

   private boolean error;

   /**
    * Constructeur.
    */
   public SpiImageIconTableCellRenderer ()
   {
      super();
      setHorizontalAlignment(SwingConstants.CENTER);
   }

   @Override
   public void setValue (final Object value)
   {
      if (error)
      {
         setText(null);
         error = false;
      }

      if (value == null)
      {
         setIcon(null);
      }
      else
      {
         if (value instanceof Icon)
         {
            setIcon((Icon) value);
         }
         else
         {
            this.setText(ERROR_TEXT);
            error = true;
         }
      }
   }
}
