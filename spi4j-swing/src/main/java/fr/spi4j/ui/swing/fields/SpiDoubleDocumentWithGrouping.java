/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Document swing alternatif à SpiDoubleDocument pour la saisie d'un nombre décimal, et qui affiche et accepte en saisie des séparateurs de milliers. <br/>
 * Pour l'utiliser, appeler au démarrage de l'application :<br/>
 * System.setProperty("fr.spi4j.ui.swing.fields.SpiDoubleField.documentType", "fr.spi4j.ui.swing.fields.SpiDoubleDocumentWithGrouping");
 * @author MINARM
 */
public class SpiDoubleDocumentWithGrouping extends SpiDoubleDocument
{
   private static final long serialVersionUID = 1L;

   private static final Pattern NUMBER_PATTERN = Pattern.compile("[\\d\\s\\" + (char) 160 + ",\\.\\+\\-]*");

   private static final String pattern = "#,##0";

   private static final char DECIMAL_SEPARATOR = DecimalFormatSymbols.getInstance().getDecimalSeparator();

   // 2 décimales après la virgule
   private static final NumberFormat TWO_DIGITS_NUMBER_FORMAT = new DecimalFormat(pattern + ".00",
            DecimalFormatSymbols.getInstance());

   private NumberFormat numberFormat;

   /**
    * Constructeur.
    */
   public SpiDoubleDocumentWithGrouping ()
   {
      super();
   }

   @Override
   public NumberFormat getNumberFormat ()
   {
      if (numberFormat == null)
      {
         // le plus courant est mis en cache statique
         if (getFractionDigits() == 2)
         {
            numberFormat = getTwoDigitsNumberFormat();
         }
         else
         {
            final String lPattern = getFractionDigits() > 0 ? pattern + '.' : pattern;
            final DecimalFormatSymbols v_symbols = DecimalFormatSymbols.getInstance();
            numberFormat = new DecimalFormat(lPattern, v_symbols);
            numberFormat.setMinimumFractionDigits(getFractionDigits());
         }
      }
      return numberFormat;
   }

   /**
    * Retourne la valeur de la propriété twoDigitsNumberFormat.
    * @return NumberFormat
    */
   public static NumberFormat getTwoDigitsNumberFormat ()
   {
      return TWO_DIGITS_NUMBER_FORMAT;
   }

   /**
    * Définit la valeur de la propriété fractionDigits.
    * @param newFractionDigits
    *           int
    * @see #getFractionDigits
    */
   @Override
   public void setFractionDigits (final int newFractionDigits)
   {
      super.setFractionDigits(newFractionDigits);
      numberFormat = null; // force la reconstruction du format
   }

   @Override
   public void insertString (final int offset, final String string, final AttributeSet attributeSet)
            throws BadLocationException
   {
      if (string == null || string.length() == 0)
      {
         return;
      }

      String lString = string;
      int lOffset = offset;
      if ("-".equals(lString))
      {
         lOffset = 0;
      }
      else if ("+".equals(lString) && getText(0, getLength()).startsWith("-"))
      {
         remove(0, 1);
         return;
      }

      // même si l'utilisateur saisi un '.' ou un ' ', on les remplace par ',' et par l'espace insécable
      // qui sont séparateur décimal et séparateur de milliers en France
      lString = lString.replace('.', DECIMAL_SEPARATOR).replace(' ', (char) 160);

      final String text = new StringBuilder(getText(0, getLength())).insert(lOffset, lString).toString();
      double value = 0;
      boolean isOK = true;

      try
      {
         if (!"-".equals(text) || getMinimumValue() >= 0)
         {
            value = getNumberFormat().parse(text).doubleValue();
         }
      }
      catch (final ParseException v_e)
      {
         isOK = false;
      }

      isOK = isOK
               && value >= getMinimumValue()
               && value <= getMaximumValue()
               && (text.indexOf(DECIMAL_SEPARATOR) == -1 || (text.length() - text.indexOf(DECIMAL_SEPARATOR) - 2 < getFractionDigits() && getFractionDigits() > 0))
               && NUMBER_PATTERN.matcher(text).matches();

      if (isOK)
      {
         super.superInsertString(lOffset, lString, attributeSet);
      }
      else
      {
         beep();
      }
   }

   @Override
   public Double parseDouble (final String text)
   {
      if (text.length() != 0 && !"-".equals(text))
      {
         try
         {
            return getNumberFormat().parse(text).doubleValue();
         }
         catch (final ParseException v_e)
         {
            return null;
         }
      }
      return null;
   }
}
