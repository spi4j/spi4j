/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.view.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import fr.spi4j.lib.jmeter.jbehave.gameoflife.domain.Game;
import fr.spi4j.lib.jmeter.jbehave.gameoflife.domain.GameObserver;
import fr.spi4j.lib.jmeter.jbehave.gameoflife.domain.Grid;

/**
 * Vue swing du jeu.
 * @author MINARM
 */
public class GameFrame extends JFrame implements GameObserver
{

   private static final int c_HEIGHT = 20;

   private static final int c_WIDTH = 40;

   private static final int c_SCALE = 20;

   private static final long serialVersionUID = 1L;

   private final Game _game;

   private JButton _nextStep;

   private GameObserver _delegateListener = GameObserver.c_NULL;

   /**
    * Constructeur.
    * @param p_game
    *           modèle du jeu
    */
   public GameFrame (final Game p_game)
   {
      this._game = p_game;

      setUpFrame();
      setUpGrid();

      createNextStepButton();

      this.pack();
   }

   /**
    * initialisation de la frame.
    */
   private void setUpFrame ()
   {
      this.setName("game.frame");
      this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

   }

   /**
    * initialisation de la grille.
    */
   private void setUpGrid ()
   {
      this.getContentPane().setLayout(new BorderLayout());
      final ImageRenderer v_imageRenderer = new ImageRenderer(c_WIDTH, c_HEIGHT, c_SCALE);
      v_imageRenderer.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mousePressed (final MouseEvent p_e)
         {
            final int v_column = p_e.getX() / c_SCALE;
            final int v_row = p_e.getY() / c_SCALE;
            _game.toggleCellAt(v_column, v_row);
         }
      });
      _game.setObserver(v_imageRenderer);
      this._delegateListener = v_imageRenderer;
      this.getContentPane().add(v_imageRenderer, BorderLayout.CENTER);
   }

   /**
    * initialisation du bouton de génération.
    */
   private void createNextStepButton ()
   {
      _nextStep = new JButton("Next Step");
      _nextStep.setName("next.step");
      _nextStep.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed (final ActionEvent p_event)
         {
            _game.nextGeneration();
         }
      });
      this.add(_nextStep, BorderLayout.SOUTH);
   }

   @Override
   public void gridChanged (final Grid p_grid)
   {
      _delegateListener.gridChanged(p_grid);
   }

   /**
    * Lanceur.
    * @param p_args
    *           args
    */
   public static void main (final String[] p_args)
   {
      new GameFrame(new Game(40, 30)).setVisible(true);
   }
}
