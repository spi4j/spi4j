/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Document swing interne pour la saisie d'un texte court (sans \n) ou long (avec \n).
 * @author MINARM
 */
public class SpiTextDocument extends PlainDocument
{
   private static final boolean BEEP_ON_INVALID = true;

   private static final long serialVersionUID = 1L;

   private int maxLength = -1;

   private boolean editable = true;

   /**
    * Constructeur.
    * @param myMaxLength
    *           int
    */
   public SpiTextDocument (final int myMaxLength)
   {
      super();
      setMaxLength(myMaxLength);
   }

   /**
    * Retourne la valeur de la propriété maxLength.
    * @return int
    * @see #setMaxLength
    */
   public int getMaxLength ()
   {
      return maxLength;
   }

   @Override
   public void insertString (final int offset, final String string, final AttributeSet attributeSet)
            throws BadLocationException
   {
      if (string == null || string.length() == 0)
      {
         return;
      }
      if (!isEditable())
      {
         beep();
         return;
      }

      if (getMaxLength() <= 0)
      {
         super.insertString(offset, string, attributeSet);
      }
      else
      {
         final int freeLength = getMaxLength() - getLength();

         // Si la longueur restante est supérieure à zéro
         if (freeLength >= 0)
         {
            if (string.length() <= freeLength)
            {
               super.insertString(offset, string, attributeSet);
            }
            else
            {
               super.insertString(offset, string.substring(0, freeLength), attributeSet);
               beep();
            }
         }
         else
         {
            beep();
         }
      }
   }

   @Override
   public void remove (final int offset, final int length) throws BadLocationException
   {
      if (!isEditable())
      {
         return;
      }

      super.remove(offset, length);
   }

   /**
    * Définit la valeur de la propriété maxLength.
    * @param newMaxLength
    *           int
    * @see #getMaxLength
    */
   public void setMaxLength (final int newMaxLength)
   {
      maxLength = newMaxLength;
   }

   /**
    * Retourne la valeur de la propriété editable.
    * @return boolean
    * @see #setEditable
    */
   public boolean isEditable ()
   {
      return editable;
   }

   /**
    * Définit la valeur de la propriété editable.
    * @param newEditable
    *           boolean
    * @see #isEditable
    */
   public void setEditable (final boolean newEditable)
   {
      editable = newEditable;
   }

   /**
    * Cette méthode appelée en cas de saisie invalide émet un beep.
    */
   protected void beep ()
   {
      if (isEditable() && BEEP_ON_INVALID)
      {
         Toolkit.getDefaultToolkit().beep();
      }
   }
}
