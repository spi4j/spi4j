/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import javax.swing.text.Document;

/**
 * Champs de saisie d'un texte court (type String sans \n) à casse fixée (majuscules ou minuscules).
 * @author MINARM
 */
public class SpiFixedCaseStringField extends SpiStringField
{
   private static final long serialVersionUID = 1L;

   @Override
   protected Document createDefaultModel ()
   {
      return new SpiFixedCaseTextDocument(SpiStringField.STRING_FIELD_DEFAULT_MAX_LENGTH);
   }

   /**
    * Retourne la valeur de la propriété fixedCaseTextDocument.
    * @return SpiFixedCaseTextDocument
    */
   protected SpiFixedCaseTextDocument getFixedCaseTextDocument ()
   {
      return (SpiFixedCaseTextDocument) getDocument();
   }

   /**
    * Retourne la valeur de la propriété upperCase.
    * @return boolean
    * @see #setUpperCase
    */
   public boolean isUpperCase ()
   {
      return getFixedCaseTextDocument().isUpperCase();
   }

   /**
    * Définit la valeur de la propriété upperCase.
    * @param newUpperCase
    *           boolean
    * @see #isUpperCase
    */
   public void setUpperCase (final boolean newUpperCase)
   {
      getFixedCaseTextDocument().setUpperCase(newUpperCase);
   }
}
