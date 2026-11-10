/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.event.KeyEvent;

import javax.swing.SwingConstants;
import javax.swing.text.Document;

import fr.spi4j.ui.HasLong_Itf;

/**
 * Champs de saisie d'un nombre entier long (type Long).
 * @author MINARM
 */
public class SpiLongField extends SpiTextField<Long> implements HasLong_Itf
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    * @see #createDefaultModel
    */
   public SpiLongField ()
   {
      super();
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   @Override
   protected Document createDefaultModel ()
   {
      return new SpiLongDocument();
   }

   @Override
   public Long getValue ()
   {
      final String text = super.getText();
      return text != null && text.length() != 0 && !"-".equals(text) ? Long.valueOf(text) : null;
   }

   /**
    * Retourne la valeur de la propriété longDocument.
    * @return SpiLongDocument
    */
   protected SpiLongDocument getLongDocument ()
   {
      return (SpiLongDocument) getDocument();
   }

   /**
    * Retourne la valeur de la propriété maximumValue.
    * @return long
    * @see #setMaximumValue
    */
   public long getMaximumValue ()
   {
      return getLongDocument().getMaximumValue();
   }

   /**
    * Retourne la valeur de la propriété minimumValue.
    * @return long
    * @see #setMinimumValue
    */
   public long getMinimumValue ()
   {
      return getLongDocument().getMinimumValue();
   }

   @Override
   public void setValue (final Long newLong)
   {
      super.setText(newLong != null ? newLong.toString() : null);
   }

   /**
    * Définit la valeur de la propriété maximumValue.
    * @param newMaximumValue
    *           long
    * @see #getMaximumValue
    */
   public void setMaximumValue (final long newMaximumValue)
   {
      getLongDocument().setMaximumValue(newMaximumValue);
   }

   /**
    * Définit la valeur de la propriété minimumValue.
    * @param newMinimumValue
    *           long
    * @see #getMinimumValue
    */
   public void setMinimumValue (final long newMinimumValue)
   {
      getLongDocument().setMinimumValue(newMinimumValue);
   }

   @Override
   protected void keyEvent (final KeyEvent event)
   {
      // Ici, la flèche Haut incrémente la valeur de 1,
      // et la flèche Bas la décrémente.
      if (isEditable() && event.getID() == KeyEvent.KEY_PRESSED
               && (event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN)
               && super.getText() != null && super.getText().length() != 0)
      {
         final long newValue = getValue().longValue() + (event.getKeyCode() == KeyEvent.VK_UP ? 1 : -1);
         if (newValue >= getMinimumValue() && newValue <= getMaximumValue())
         {
            setValue(Long.valueOf(newValue));
            event.consume();
         }
         else
         {
            beep();
         }
      }
      else
      {
         super.keyEvent(event);
      }
   }
}
