/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * MenuItem.
 * @author MINARM
 */
public class SpiMenuItem extends JMenuItem
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public SpiMenuItem ()
   {
      this(null);
   }

   /**
    * Constructeur.
    * @param text
    *           String
    */
   public SpiMenuItem (final String text)
   {
      super(text);
   }

   /**
    * Constructeur.
    * @param text
    *           String
    * @param icon
    *           Icon
    */
   public SpiMenuItem (final String text, final Icon icon)
   {
      super(text, icon);
   }

   /**
    * Méthode interne pour notifier tous les listeners qui ont enregistré leur intérêt par menuItem.addActionListener pour les évènements d'action sur cet item. Dans la surcharge de cette méthode, le curseur sablier est ici automatiquement affiché.
    * @param event
    *           ActionEvent
    */
   @Override
   protected void fireActionPerformed (final java.awt.event.ActionEvent event)
   {
      final SpiWaitCursor waitCursor = new SpiWaitCursor(this);
      try
      {
         super.fireActionPerformed(event);
      }
      finally
      {
         waitCursor.restore();
      }
   }
}
