/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import fr.spi4j.ui.mvp.View_Itf;

/**
 * Dialogue d'affichage d'exception.
 * @author MINARM
 */
public class SpiFrame extends JFrame
{
   private static final long serialVersionUID = 1L;

   /**
    * Construction privé d'une dialogue d'affichage d'erreur.
    */
   protected SpiFrame ()
   {
      super();

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
    * Affiche la dialogue d'erreur.
    */
   public void display ()
   {
      pack();
      setLocationRelativeTo(null);
      setVisible(true);
   }

   /**
    * Ouvre une dialogue avec une vue dedans.
    * @param p_view
    *           la vue à afficher dans la dialogue
    */
   public static void open (final SpiViewPanel<?> p_view)
   {
      final SpiFrame v_dialog = new SpiFrame();
      v_dialog.setContentPane(p_view);
      v_dialog.setTitle(p_view.getTitle());
      v_dialog.display();
   }

}
