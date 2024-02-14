/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 * Définit un renderer pour représenter un Double avec 2 décimales dans une JTable.
 * @author MINARM
 */
public class SpiDoubleTableCellRenderer extends SpiDefaultTableCellRenderer
{
   private static final long serialVersionUID = 1L;

   private NumberFormat numberFormat;

   /**
    * Constructeur.
    */
   public SpiDoubleTableCellRenderer ()
   {
      super();
      final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
      // symbols.setDecimalSeparator(','); (selon la Locale par défaut)
      final String pattern = "#,##0.00"; // max fraction digits 2, grouping used true, grouping size 3
      setNumberFormat(new DecimalFormat(pattern, symbols));
      setHorizontalAlignment(RIGHT);
   }

   /**
    * Retourne la valeur de la propriété numberFormat.
    * @return NumberFormat
    * @see #setNumberFormat
    */
   public NumberFormat getNumberFormat ()
   {
      return numberFormat;
   }

   /**
    * Définit la valeur de la propriété numberFormat.
    * @param newNumberFormat
    *           NumberFormat
    * @see #getNumberFormat
    */
   public void setNumberFormat (final NumberFormat newNumberFormat)
   {
      numberFormat = newNumberFormat;
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
         if (value instanceof Number)
         {
            // Number inclue Double, Float, Integer, BigDecimal ...
            this.setText(getNumberFormat().format(((Number) value).doubleValue()));
         }
         else
         {
            this.setText("??");
         }
      }
   }
}
