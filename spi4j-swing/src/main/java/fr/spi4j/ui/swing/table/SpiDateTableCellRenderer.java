/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.text.DateFormat;
import java.util.Date;

/**
 * Définit un renderer pour représenter une Date dans une JTable.
 * @author MINARM
 */
public class SpiDateTableCellRenderer extends SpiDefaultTableCellRenderer
{
   private static final long serialVersionUID = 1L;

   private DateFormat dateFormat;

   /**
    * Constructeur.
    */
   public SpiDateTableCellRenderer ()
   {
      super();
      setDateFormat(DateFormat.getDateInstance(DateFormat.SHORT));
      setHorizontalAlignment(RIGHT);
   }

   /**
    * Retourne la valeur de la propriété dateFormat.
    * @return DateFormat
    * @see #setDateFormat
    */
   public DateFormat getDateFormat ()
   {
      return dateFormat;
   }

   /**
    * Définit la valeur de la propriété dateFormat.
    * @param newDateFormat
    *           DateFormat
    * @see #getDateFormat
    */
   public void setDateFormat (final DateFormat newDateFormat)
   {
      dateFormat = newDateFormat;
   }

   @Override
   public void setValue (final Object value)
   {
      if (value == null)
      {
         this.setText(null);
      }
      else
      {
         if (value instanceof Date)
         {
            this.setText(getDateFormat().format((Date) value));
         }
         else
         {
            this.setText("??");
         }
      }
   }
}
