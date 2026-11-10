/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import com.google.gwt.user.client.ui.TextBox;

import fr.spi4j.ui.HasMaxLength_Itf;
import fr.spi4j.ui.HasString_Itf;

/**
 * Champs de saisie d'un texte court (type String sans \n).
 * @author MINARM
 */
public class SpiStringField extends TextBox implements HasString_Itf, HasMaxLength_Itf
{

   /**
    * Constructeur.
    */
   public SpiStringField ()
   {
      super();
   }

}
