/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.view.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fr.spi4j.lib.jmeter.jbehave.gameoflife.domain.GameObserver;
import fr.spi4j.lib.jmeter.jbehave.gameoflife.domain.Grid;

/**
 * Vue avec images du jeu.
 * @author MINARM
 */
public class ImageRenderer extends JPanel implements GameObserver
{

   private static final long serialVersionUID = 1L;

   private final int _width;

   private final int _height;

   private final int _scale;

   private Grid _cells = Grid.c_NULL;

   // This is here for testing purposes
   private GameObserver _piggyBack = GameObserver.c_NULL;

   /**
    * Constructeur.
    * @param p_width
    *           largeur du jeu
    * @param p_height
    *           hauteur du jeu
    * @param p_scale
    *           échelle des images
    */
   public ImageRenderer (final int p_width, final int p_height, final int p_scale)
   {
      this._width = p_width;
      this._height = p_height;
      this.setName("image.renderer");
      final Dimension v_size = new Dimension(p_width * p_scale, p_height * p_scale);
      this.setPreferredSize(v_size);
      this.setMaximumSize(v_size);
      this.setMinimumSize(v_size);
      this._scale = p_scale;
   }

   @Override
   public void gridChanged (final Grid p_grid)
   {
      this._cells = p_grid;
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run ()
         {
            ImageRenderer.this.repaint();
            _piggyBack.gridChanged(ImageRenderer.this._cells);
         }
      });
   }

   @Override
   public void paint (final Graphics p_g)
   {
      super.paint(p_g);
      for (int v_row = 0; v_row < _height; v_row++)
      {
         for (int v_column = 0; v_column < _width; v_column++)
         {
            final Color v_color;
            if (_cells.hasLife(v_column, v_row))
            {
               v_color = Color.BLACK;
            }
            else
            {
               v_color = Color.WHITE;
            }
            p_g.setColor(v_color);
            p_g.fillRect(v_column * _scale, v_row * _scale, _scale, _scale);
            p_g.setColor(Color.GRAY);
            p_g.drawRect(v_column * _scale, v_row * _scale, _scale, _scale);
         }
      }
   }

   /**
    * Affectation du listener.
    * @param p_piggyBack
    *           le listener
    */
   public void setPiggybackListener (final GameObserver p_piggyBack)
   {
      this._piggyBack = p_piggyBack;
      p_piggyBack.gridChanged(_cells);
   }
}
