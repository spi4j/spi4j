/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;

import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

/**
 * Petit utilitaire pour gérer le curseur "sablier" de mise en attente en cas de traitement long.
 * @author MINARM
 */
public class SpiWaitCursor
{
   private static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

   private final Window window;

   private final Cursor oldWindowCursor;

   private final boolean windowGlassPaneVisible;

   /**
    * Constructeur : crée le WaitCursor et affiche le sablier.
    * @param comp
    *           Component
    */
   public SpiWaitCursor (final Component comp)
   {
      // Curseur de la frame ou dialog contenant le component
      window = SwingUtilities.windowForComponent(comp);
      windowGlassPaneVisible = window instanceof RootPaneContainer
               && ((RootPaneContainer) window).getGlassPane().isVisible();
      oldWindowCursor = window != null ? window.getCursor() : null;

      // On ne change pas le curseur du component car cela poserait problème en cas d'imbrication
      // pour le remettre à sa valeur initiale (Component.getCursor renvoyant le cursor de son parent si non défini)

      // On active le curseur d'attente
      // (l'utilisation du glassPane rend le curseur visible lors d'un double-clique sur une ligne par ex.
      // et l'utilisation de curseur de la frame rend celui-ci visible même si on sort de la fenêtre pour revenir)
      if (window instanceof RootPaneContainer)
      {
         final Component glassPane = ((RootPaneContainer) window).getGlassPane();
         glassPane.setVisible(true);
         glassPane.setCursor(WAIT_CURSOR);
      }
      if (window != null)
      {
         window.setCursor(WAIT_CURSOR);
      }
   }

   /**
    * Restore l'ancien curseur, en fin de traitement.
    */
   public void restore ()
   {
      if (window instanceof RootPaneContainer)
      {
         final Component glassPane = ((RootPaneContainer) window).getGlassPane();
         glassPane.setVisible(windowGlassPaneVisible);
         glassPane.setCursor(oldWindowCursor);
      }
      if (window != null)
      {
         window.setCursor(oldWindowCursor);
      }
   }
}
