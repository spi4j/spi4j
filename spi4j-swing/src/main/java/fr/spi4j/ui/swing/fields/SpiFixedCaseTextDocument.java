/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Document swing interne pour la saisie d'un texte à casse fixée (majuscules ou minuscules).
 * @author MINARM
 */
public class SpiFixedCaseTextDocument extends SpiTextDocument
{
   private static final long serialVersionUID = 1L;

   private boolean upperCase = true; // majuscules par défaut

   /**
    * Constructeur.
    * @param maxLength
    *           int
    */
   public SpiFixedCaseTextDocument (final int maxLength)
   {
      super(maxLength);
   }

   @Override
   public void insertString (final int offset, final String string, final AttributeSet attributeSet)
            throws BadLocationException
   {
      if (string == null || string.length() == 0)
      {
         return;
      }
      super.insertString(offset, upperCase ? string.toUpperCase() : string.toLowerCase(), attributeSet);
   }

   /**
    * Retourne la valeur de la propriété upperCase.
    * @return boolean
    * @see #setUpperCase
    */
   public boolean isUpperCase ()
   {
      return upperCase;
   }

   /**
    * Définit la valeur de la propriété upperCase.
    * @param newUpperCase
    *           boolean
    * @see #isUpperCase
    */
   public void setUpperCase (final boolean newUpperCase)
   {
      upperCase = newUpperCase;
   }
}
