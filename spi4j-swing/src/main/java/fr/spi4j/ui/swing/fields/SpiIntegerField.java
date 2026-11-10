/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.event.KeyEvent;

import javax.swing.SwingConstants;
import javax.swing.text.Document;

import fr.spi4j.ui.HasInteger_Itf;

/**
 * Champs de saisie d'un nombre entier (type Integer).
 * @author MINARM
 */
public class SpiIntegerField extends SpiTextField<Integer> implements HasInteger_Itf
{
   private static final long serialVersionUID = 1L;

   private static final Class<Document> DOCUMENT_TYPE;

   static
   {
      final String type = System.getProperty(SpiIntegerField.class.getName() + ".documentType");
      try
      {
         @SuppressWarnings("unchecked")
         final Class<Document> classe = type != null ? (Class<Document>) Class.forName(type) : null;
         DOCUMENT_TYPE = classe;
      }
      catch (final ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Constructeur.
    * @see #createDefaultModel
    */
   public SpiIntegerField ()
   {
      super();
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   @Override
   protected Document createDefaultModel ()
   {
      if (DOCUMENT_TYPE != null)
      {
         try
         {
            return DOCUMENT_TYPE.getDeclaredConstructor().newInstance();
         }
         catch (final Exception e)
         {
            throw new RuntimeException(e);
         }
      }

      return new SpiIntegerDocument();
   }

   @Override
   public Integer getValue ()
   {
      final String text = super.getText();
      return getIntegerDocument().parseInteger(text);
   }

   /**
    * Retourne la valeur de la propriété integerDocument.
    * @return SpiIntegerDocument
    */
   protected SpiIntegerDocument getIntegerDocument ()
   {
      return (SpiIntegerDocument) getDocument();
   }

   /**
    * Retourne la valeur de la propriété maximumValue.
    * @return int
    * @see #setMaximumValue
    */
   public int getMaximumValue ()
   {
      return (int) getIntegerDocument().getMaximumValue();
   }

   /**
    * Retourne la valeur de la propriété minimumValue.
    * @return int
    * @see #setMinimumValue
    */
   public int getMinimumValue ()
   {
      return (int) getIntegerDocument().getMinimumValue();
   }

   @Override
   public void setValue (final Integer newInteger)
   {
      super.setText(newInteger != null ? getIntegerDocument().formatInteger(newInteger) : null);
   }

   /**
    * Définit la valeur de la propriété maximumValue.
    * @param newMaximumValue
    *           int
    * @see #getMaximumValue
    */
   public void setMaximumValue (final int newMaximumValue)
   {
      getIntegerDocument().setMaximumValue(newMaximumValue);
   }

   /**
    * Définit la valeur de la propriété minimumValue.
    * @param newMinimumValue
    *           int
    * @see #getMinimumValue
    */
   public void setMinimumValue (final int newMinimumValue)
   {
      getIntegerDocument().setMinimumValue(newMinimumValue);
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
         final int newValue = getValue().intValue() + (event.getKeyCode() == KeyEvent.VK_UP ? 1 : -1);
         if (newValue >= getMinimumValue() && newValue <= getMaximumValue())
         {
            setValue(Integer.valueOf(newValue));
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
