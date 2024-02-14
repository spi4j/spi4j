/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import fr.spi4j.ui.mvp.View_Itf;

/**
 * Dialogue d'affichage d'exception.
 * @author MINARM
 */
public class SpiDialog extends JDialog
{
   private static final long serialVersionUID = 1L;

   // initialisé à false
   private static boolean notModal;

   /**
    * Construction privé d'une dialogue d'affichage d'erreur.
    * @param p_modal
    *           la modalité de la dialogue
    * @param p_owner
    *           la fenêtre parente
    */
   protected SpiDialog (final boolean p_modal, final Window p_owner)
   {
      // on utilise le constructeur avec une window et non le constructeur sans paramètre pour la position de la dialogue soit centrée sur la fenêtre parente,
      // en particulier lorsqu'il s'agit d'un multi-moniteur
      super(p_owner);
      setModal(p_modal && !notModal);

      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing (final WindowEvent e)
         {
            if (getContentPane() instanceof View_Itf)
            {
               ((View_Itf) getContentPane()).getPresenter().close();
            }
         }
      });
   }

   /**
    * Force les SpiDialogs à ne pas être modal, pour les tests JBehave.
    */
   public static void forceNotModal ()
   {
      notModal = true;
   }

   /**
    * Affiche la boîte de dialogue.
    */
   public void display ()
   {
      pack();
      setLocationRelativeTo(null);
      setVisible(true);
   }

   /**
    * Ouvre une dialogue modale avec une vue dedans, ayant pour parent la fenêtre active.
    * @param p_view
    *           la vue à afficher dans la dialogue
    */
   public static void open (final SpiViewPanel<?> p_view)
   {
      open(p_view, true);
   }

   /**
    * Ouvre une dialogue modale avec une vue dedans, ayant pour parent p_owner.
    * @param p_view
    *           la vue à afficher dans la dialogue
    * @param p_owner
    *           la fenêtre parente
    */
   public static void open (final SpiViewPanel<?> p_view, final Window p_owner)
   {
      open(p_view, true, p_owner);
   }

   /**
    * Ouvre une dialogue modale ou non avec une vue dedans, ayant pour parent la fenêtre active.
    * @param p_view
    *           la vue à afficher dans la dialogue
    * @param p_modal
    *           la modalité de la dialogue
    */
   public static void open (final SpiViewPanel<?> p_view, final boolean p_modal)
   {
      open(p_view, p_modal, SpiSwingUtilities.getFocusedWindow());
   }

   /**
    * Ouvre une dialogue modale ou non avec une vue dedans, ayant pour parent p_owner.
    * @param p_view
    *           la vue à afficher dans la dialogue
    * @param p_modal
    *           la modalité de la dialogue
    * @param p_owner
    *           la fenêtre parente
    */
   public static void open (final SpiViewPanel<?> p_view, final boolean p_modal, final Window p_owner)
   {
      final SpiDialog v_dialog = new SpiDialog(p_modal, p_owner);
      v_dialog.setContentPane(p_view);
      v_dialog.setTitle(p_view.getTitle());
      v_dialog.display();
   }

}
