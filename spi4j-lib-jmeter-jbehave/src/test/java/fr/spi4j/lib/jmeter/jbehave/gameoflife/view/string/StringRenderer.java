/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.view.string;

import fr.spi4j.lib.jmeter.jbehave.gameoflife.domain.GameObserver;
import fr.spi4j.lib.jmeter.jbehave.gameoflife.domain.Grid;

/**
 * Renderer pour la grille.
 * @author MINARM
 */
public class StringRenderer implements GameObserver
{

   private static final String c_NL = System.getProperty("line.separator");

   private Grid _grid = Grid.c_NULL;

   /**
    * Affiche la grille dans une string.
    * @return la grille sous forme de string
    */
   public String asString ()
   {
      final StringBuilder v_builder = new StringBuilder();
      for (int v_row = 0; v_row < _grid.getHeight(); v_row++)
      {
         for (int v_column = 0; v_column < _grid.getWidth(); v_column++)
         {
            if (_grid.hasLife(v_column, v_row))
            {
               v_builder.append("X");
            }
            else
            {
               v_builder.append(".");
            }
         }
         if (v_row < _grid.getHeight() - 1)
         {
            v_builder.append(c_NL);
         }
      }
      return v_builder.toString();
   }

   @Override
   public void gridChanged (final Grid p_grid)
   {
      this._grid = p_grid;
   }

}
