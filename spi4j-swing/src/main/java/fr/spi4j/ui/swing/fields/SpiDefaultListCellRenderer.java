/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import javax.swing.DefaultListCellRenderer;

/**
 * Renderer par défaut pour les éléments de liste déroulante (SpiComboBox).
 * @author MINARM
 */
public class SpiDefaultListCellRenderer extends DefaultListCellRenderer
{
   private static final long serialVersionUID = 1L;

   /**
    * Définit le texte. Surcharge pour le tooltip.
    * @param text
    *           String
    */
   @Override
   public void setText (final String text)
   {
      if (text != null && text.length() > 0)
      {
         super.setText(text);
         setToolTipText(text);
      }
      else
      {
         super.setText(" ");
         setToolTipText(null);
      }
   }
}
