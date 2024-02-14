/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import com.google.gwt.user.client.ui.PasswordTextBox;

import fr.spi4j.ui.HasString_Itf;

/**
 * Champs de saisie d'un texte court (type String sans \n).
 * @author MINARM
 */
public class SpiPasswordField extends PasswordTextBox implements HasString_Itf
{
   /**
    * Constructeur.
    */
   public SpiPasswordField ()
   {
      super();
   }
}
