/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JButton;

import fr.spi4j.ui.HasState_Itf;

/**
 * Panel simple.
 * @author MINARM
 */
public class SpiButton extends JButton implements HasState_Itf
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public SpiButton ()
   {
      super();
   }

   /**
    * Constructeur avec texte.
    * @param p_text
    *           le texte du bouton
    */
   public SpiButton (final String p_text)
   {
      super(p_text);
   }

   /**
    * Constructeur avec texte.
    * @param p_text
    *           le texte du bouton
    * @param p_icon
    *           l'icône du bouton
    */
   public SpiButton (final String p_text, final Icon p_icon)
   {
      super(p_text, p_icon);
   }

   /**
    * Méthode interne pour notifier tous les listeners qui ont enregistré leur intérêt par button.addActionListener pour les évènements d'action sur ce bouton. Dans la surcharge de cette méthode, le curseur sablier est ici automatiquement affiché.
    * @param event
    *           ActionEvent
    */
   @Override
   protected void fireActionPerformed (final ActionEvent event)
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

   @Override
   public void setEnabled (final boolean enabled)
   {
      if (getAction() != null)
      {
         // si une action a été liée à ce bouton, l'état enabled du bouton a été défini selon l'état enabled de l'action,
         // ensuite si setEnabled est appelé sur le bouton alors on continue à tenir compte de l'état enabled de l'action
         super.setEnabled(enabled && getAction().isEnabled());
      }
      else
      {
         super.setEnabled(enabled);
      }
   }
}
