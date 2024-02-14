/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import com.google.gwt.user.client.ui.CheckBox;

import fr.spi4j.ui.HasBoolean_Itf;

/**
 * Champs case à cocher.
 * @author MINARM
 */
public class SpiCheckBox extends CheckBox implements HasBoolean_Itf
{
   /**
    * Constructeur.
    */
   public SpiCheckBox ()
   {
      super();
   }

   /**
    * Constructeur.
    * @param p_text
    *           String
    */
   public SpiCheckBox (final String p_text)
   {
      super(p_text);
   }

   /**
    * Précise si le champ est modifiable.
    * @param newEditable
    *           true si le champ est modifiable, false sinon
    */
   public void setEditable (final boolean newEditable)
   {
      super.setEnabled(newEditable);
   }
}
