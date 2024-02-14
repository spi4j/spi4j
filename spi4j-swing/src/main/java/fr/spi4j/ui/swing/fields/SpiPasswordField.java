/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPasswordField;
import javax.swing.text.BadLocationException;

import fr.spi4j.ui.HasString_Itf;

/**
 * Champ de saisie d'un mot de passe.
 * @author MINARM
 */
public class SpiPasswordField extends JPasswordField implements HasString_Itf
{

   private static final long serialVersionUID = 1L;

   // ces gestionnaires d'événement sont statiques et non réinstanciés pour économie mémoire
   // l'instance du composant est déduite à partir de la source de l'événement
   private static final FocusHandler FOCUS_HANDLER = new FocusHandler();

   /**
    * FocusListener.
    * @author MINARM
    */
   private static class FocusHandler implements FocusListener
   {
      /**
       * Constructeur.
       */
      FocusHandler ()
      {
         super();
      }

      @Override
      public void focusGained (final FocusEvent focusEvent)
      {
         final Object source = focusEvent.getSource();
         if (source instanceof SpiPasswordField)
         {
            final SpiPasswordField v_field = (SpiPasswordField) source;
            if (SpiTextField.TEXT_SELECTION_ON_FOCUS_GAINED && v_field.isEditable())
            {
               v_field.selectAll();
            }
         }
      }

      @Override
      public void focusLost (final FocusEvent focusEvent)
      {
         // RAS
      }
   }

   /**
    * Constructeur par défaut.
    */
   public SpiPasswordField ()
   {
      super();
      addFocusListener(FOCUS_HANDLER);
   }

   @Override
   public String getValue ()
   {
      try
      {
         return getDocument().getText(0, getDocument().getLength());
      }
      catch (final BadLocationException e)
      {
         return new String(getPassword());
      }
   }

   @Override
   public void setValue (final String p_value)
   {
      setText(p_value);
   }

}
