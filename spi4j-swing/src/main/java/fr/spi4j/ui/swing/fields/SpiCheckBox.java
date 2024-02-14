/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import javax.swing.JCheckBox;

import fr.spi4j.ui.HasBoolean_Itf;

/**
 * Champs case à cocher.
 * @author MINARM
 */
public class SpiCheckBox extends JCheckBox implements HasBoolean_Itf
{
   /**
    * serialVersionUID.
    */
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public SpiCheckBox ()
   {
      super();
      setOpaque(false);
   }

   /**
    * Constructeur.
    * @param p_text
    *           String
    */
   public SpiCheckBox (final String p_text)
   {
      super(p_text);
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
         setSelected(false); // false par défaut
      }
      else
      {
         setSelected(p_value);
      }
   }
}
