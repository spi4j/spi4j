/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.spi4j.ui.swing.fields.SpiStringArea;

/**
 * Panel simple.
 * @author MINARM
 */
public class SpiPanel extends JPanel
{
   private static final long serialVersionUID = 1L;

   private static final ContainerHandler CONTAINER_HANDLER = new ContainerHandler();

   /**
    * ContainerAdapter.
    * @author MINARM
    */
   private static class ContainerHandler extends ContainerAdapter
   {
      /**
       * Constructeur.
       */
      ContainerHandler ()
      {
         super();
      }

      @Override
      public void componentAdded (final ContainerEvent containerEvent)
      {
         final SpiPanel panel = (SpiPanel) containerEvent.getContainer();
         final Component child = containerEvent.getChild();
         // si le panel est enabled false, les composants ajoutés le seront aussi
         // si le panel est enabled true, on laisse les composants à enabled false pour ceux qui veulent
         if (!panel.isEnabled())
         {
            child.setEnabled(false);
            if (child instanceof JScrollPane && ((JScrollPane) child).getViewport().getView() != null)
            {
               ((JScrollPane) child).getViewport().getView().setEnabled(false);
            }
         }
      }
   }

   /**
    * Constructeur.
    */
   public SpiPanel ()
   {
      super();
      addContainerListener(CONTAINER_HANDLER);
   }

   /**
    * Constructeur.
    * @param layout
    *           LayoutManager
    */
   public SpiPanel (final LayoutManager layout)
   {
      super(layout);
      addContainerListener(CONTAINER_HANDLER);
   }

   @Override
   public void setEnabled (final boolean newEnabled)
   {
      // Permet d'activer/désactiver tous les composants de ce panel.
      super.setEnabled(newEnabled);
      for (final Component component : getComponents())
      {
         component.setEnabled(newEnabled);
         // ici on ne veut pas appeler setEnabled sur le JTextArea à l'intérieur d'un SpiStringArea qui hérite de JScrollPane
         // car c'est uniquement le SpiStringArea qui gère enabled (et editable du JTextArea)
         if (component instanceof JScrollPane && ((JScrollPane) component).getViewport().getView() != null
                  && !(component instanceof SpiStringArea))
         {
            ((JScrollPane) component).getViewport().getView().setEnabled(newEnabled);
         }
      }
   }

   /**
    * Fermer la fenêtre (type Window).
    */
   public void disposeWindow ()
   {
      final Window v_WindowContainer = SpiSwingUtilities.getAncestorOfClass(Window.class, this);
      if (v_WindowContainer != null)
      {
         v_WindowContainer.dispose();
      }
   }

   /**
    * Fermer la fenêtre (type Window).
    */
   public void bringWindowToFront ()
   {
      final Window v_WindowContainer = SpiSwingUtilities.getAncestorOfClass(Window.class, this);
      v_WindowContainer.toFront();
      if (v_WindowContainer instanceof Frame)
      {
         final Frame v_frame = (Frame) v_WindowContainer;
         if (v_frame.getState() == Frame.ICONIFIED)
         {
            v_frame.setState(Frame.NORMAL);
         }
      }
   }
}
