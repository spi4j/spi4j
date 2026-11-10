/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Document swing interne pour la saisie d'un nombre entier long.
 * @author MINARM
 */
public class SpiLongDocument extends SpiTextDocument
{
   private static final long serialVersionUID = 1L;

   private long maximumValue = Long.MAX_VALUE;

   private long minimumValue;

   /**
    * Constructeur.
    */
   public SpiLongDocument ()
   {
      super(-1);
   }

   /**
    * Retourne la valeur de la propriété maximumValue.
    * @return long
    * @see #setMaximumValue
    */
   public long getMaximumValue ()
   {
      return maximumValue;
   }

   /**
    * Retourne la valeur de la propriété minimumValue.
    * @return long
    * @see #setMinimumValue
    */
   public long getMinimumValue ()
   {
      return minimumValue;
   }

   @Override
   public void insertString (final int offset, final String string, final AttributeSet attributeSet)
            throws BadLocationException
   {
      if (string == null || string.length() == 0)
      {
         return;
      }

      int lOffset = offset;
      if ("-".equals(string))
      {
         lOffset = 0;
      }
      else if ("+".equals(string) && getText(0, getLength()).startsWith("-"))
      {
         remove(0, 1);
         return;
      }

      final String text = new StringBuilder(getText(0, getLength())).insert(lOffset, string).toString();
      long value = 0;
      boolean isOK = true;
      try
      {
         if (!"-".equals(text) || getMinimumValue() >= 0)
         {
            value = Long.parseLong(text);
         }
      }
      catch (final NumberFormatException e)
      {
         isOK = false;
      }
      isOK = isOK && value >= getMinimumValue() && value <= getMaximumValue();

      if (isOK)
      {
         super.insertString(lOffset, string, attributeSet);
      }
      else
      {
         beep();
      }
   }

   /**
    * Appelle la méthode insertString de la super-classe SpiTextDocument sans passer par insertString de cette classe.
    * @param offset
    *           int
    * @param string
    *           String
    * @param attributeSet
    *           AttributeSet
    * @throws BadLocationException
    *            e
    */
   public void superInsertString (final int offset, final String string, final AttributeSet attributeSet)
            throws BadLocationException
   {
      super.insertString(offset, string, attributeSet);
   }

   /**
    * Définit la valeur de la propriété maximumValue.
    * @param newMaximumValue
    *           long
    * @see #getMaximumValue
    */
   public void setMaximumValue (final long newMaximumValue)
   {
      maximumValue = newMaximumValue;
   }

   /**
    * Définit la valeur de la propriété minimumValue.
    * @param newMinimumValue
    *           long
    * @see #getMinimumValue
    */
   public void setMinimumValue (final long newMinimumValue)
   {
      minimumValue = newMinimumValue;
   }
}
