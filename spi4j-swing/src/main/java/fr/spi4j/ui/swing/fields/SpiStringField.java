/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import javax.swing.text.Document;

import fr.spi4j.ui.HasMaxLength_Itf;
import fr.spi4j.ui.HasString_Itf;

/**
 * Champs de saisie d'un texte court (type String sans \n).
 * @author MINARM
 */
public class SpiStringField extends SpiTextField<String> implements HasString_Itf, HasMaxLength_Itf
{
   static final int STRING_FIELD_DEFAULT_MAX_LENGTH = 100;

   private static final long serialVersionUID = 1L;

   /**
    * Retourne la valeur de la propriété maxLength.
    * @return int
    * @see #setMaxLength
    */
   @Override
   public int getMaxLength ()
   {
      return getTextDocument().getMaxLength();
   }

   @Override
   public String getValue ()
   {
      String text = super.getText();
      if (text != null)
      {
         // on trime pour enlever les espaces
         text = text.trim();
         // si la chaine est vide alors elle devient null
         if (text.length() == 0)
         {
            text = null;
         }
      }
      return text;
   }

   /**
    * Définit la valeur de la propriété maxLength.
    * @param newMaxLength
    *           int
    * @see #getMaxLength
    */
   @Override
   public void setMaxLength (final int newMaxLength)
   {
      getTextDocument().setMaxLength(newMaxLength);
   }

   @Override
   public void setValue (final String newString)
   {
      // note: Swing remplace les '\n' par ' '
      super.setText(newString);
   }

   @Override
   protected Document createDefaultModel ()
   {
      return new SpiTextDocument(STRING_FIELD_DEFAULT_MAX_LENGTH);
   }
}
