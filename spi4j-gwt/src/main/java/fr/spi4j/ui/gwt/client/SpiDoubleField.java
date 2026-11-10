/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import com.google.gwt.user.client.ui.DoubleBox;

import fr.spi4j.ui.HasDouble_Itf;

/**
 * Champs de saisie d'un nombre décimal (type Double).
 * @author MINARM
 */
public class SpiDoubleField extends DoubleBox implements HasDouble_Itf
{
   /**
    * Constructeur.
    */
   public SpiDoubleField ()
   {
      super();
   }
}
