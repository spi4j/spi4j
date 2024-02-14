/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JToolBar;

/**
 * Toolbar.
 * @author adamar, DCSID
 */
public class SpiToolBar extends JToolBar
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public SpiToolBar ()
   {
      super();
   }

   @Override
   public void setEnabled (final boolean newEnabled)
   {
      // Permet d'activer/désactiver tous les boutons.
      super.setEnabled(newEnabled);
      for (final Component component : getComponents())
      {
         component.setEnabled(newEnabled);
      }
   }

   /**
    * Redéfinition pour créer un {@link SpiButton} à partir d'une action
    * @param p_action
    *           L'action
    * @return un objet de type <code>SpiButton</code>
    */
   @Override
   protected SpiButton createActionComponent (final Action p_action)
   {
      final SpiButton v_button = new SpiButton()
      {
         private static final long serialVersionUID = 1L;

         @Override
         protected PropertyChangeListener createActionPropertyChangeListener (final Action p_a)
         {
            PropertyChangeListener v_pcl = createActionChangeListener(this);
            if (v_pcl == null)
            {
               v_pcl = super.createActionPropertyChangeListener(p_a);
            }
            return v_pcl;
         }
      };
      // Désactivation du texte si la barre est verticale et qu'il existe une icone
      if (p_action != null && getOrientation() == VERTICAL
               && (p_action.getValue(Action.SMALL_ICON) != null || p_action.getValue(Action.LARGE_ICON_KEY) != null))
      {
         v_button.setHideActionText(true);
      }
      v_button.setOpaque(false);
      v_button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      return v_button;
   }
}
