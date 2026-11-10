/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import com.google.gwt.user.client.ui.Button;

import fr.spi4j.ui.HasString_Itf;

/**
 * Bouton
 * @author MINARM
 */
public class SpiButton extends Button implements HasString_Itf
{

   /**
    * Constructeur.
    */
   public SpiButton ()
   {
      super();
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
