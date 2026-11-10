/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import com.google.gwt.user.client.ui.IntegerBox;

import fr.spi4j.ui.HasInteger_Itf;

/**
 * Champs de saisie d'un nombre décimal (type Double).
 * @author MINARM
 */
public class SpiIntegerField extends IntegerBox implements HasInteger_Itf
{
   /**
    * Constructeur.
    */
   public SpiIntegerField ()
   {
      super();
   }
}
