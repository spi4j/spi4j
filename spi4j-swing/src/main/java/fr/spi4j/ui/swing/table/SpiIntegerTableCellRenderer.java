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
 * Définit un renderer pour représenter un Integer dans une JTable.
 * @author MINARM
 */
public class SpiIntegerTableCellRenderer extends SpiDefaultTableCellRenderer
{
   private static final long serialVersionUID = 1L;

   private NumberFormat numberFormat;

   /**
    * Constructeur.
    */
   public SpiIntegerTableCellRenderer ()
   {
      super();
      final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
      // symbols.setDecimalSeparator(','); (selon la Locale par défaut)
      final String pattern = "#,##0"; // max fraction digits 2, grouping used true, grouping size 3
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

   /** {@inheritDoc} */
   @Override
   public void setValue (final Object value)
   {
      if (value == null)
      {
         this.setText(null);
      }
      else
      {
         if (value instanceof Integer)
         {
            this.setText(getNumberFormat().format(value));
         }
         else if (value instanceof Number)
         {
            // Number inclue Integer, Long, BigInteger, Double, Float ...
            this.setText(getNumberFormat().format(((Number) value).longValue()));
         }
         else
         {
            this.setText("??");
         }
      }
   }
}
