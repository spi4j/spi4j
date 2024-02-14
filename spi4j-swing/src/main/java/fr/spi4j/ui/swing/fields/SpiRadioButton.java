/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import javax.swing.JRadioButton;

import fr.spi4j.ui.HasBoolean_Itf;

/**
 * Champs radio-bouton.
 * @author MINARM
 */
public class SpiRadioButton extends JRadioButton implements HasBoolean_Itf
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public SpiRadioButton ()
   {
      super();
      setOpaque(false);
   }

   /**
    * Constructeur.
    * @param text
    *           String
    */
   public SpiRadioButton (final String text)
   {
      super(text);
      setOpaque(false);
   }

   @Override
   public Boolean getValue ()
   {
      return isSelected();
   }

   @Override
   public void setValue (final Boolean p_value)
   {
      if (p_value == null)
      {
         setSelected(false);
      }
      else
      {
         setSelected(p_value);
      }
   }
}
