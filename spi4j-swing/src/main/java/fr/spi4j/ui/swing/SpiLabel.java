/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import javax.swing.JLabel;

import fr.spi4j.ui.HasString_Itf;

/**
 * Label simple.
 * @author MINARM
 */
public class SpiLabel extends JLabel implements HasString_Itf
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public SpiLabel ()
   {
      super();
   }

   /**
    * Constructeur avec texte.
    * @param p_text
    *           le texte du bouton
    */
   public SpiLabel (final String p_text)
   {
      super(p_text);
   }

   @Override
   public void setValue (final String p_value)
   {
      setText(p_value);
   }

   @Override
   public String getValue ()
   {
      return getText();
   }
}
