/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Le jeu.
 * @author MINARM
 */
public class Game
{

   private final int _width;

   private final int _height;

   private GameObserver _observer;

   private final Set<Cell> _cells = new HashSet<Cell>();

   /**
    * Constructeur
    * @param p_width
    *           la largeur du jeu
    * @param p_height
    *           la longueur du jeu
    */
   public Game (final int p_width, final int p_height)
   {
      this._width = p_width;
      this._height = p_height;
   }

   /**
    * Ajoute un observer à la partie.
    * @param p_observer
    *           l'observer
    */
   public void setObserver (final GameObserver p_observer)
   {
      this._observer = p_observer;
      notifyObserver();
   }

   /**
    * notifie l'observer.
    */
   private void notifyObserver ()
   {
      _observer.gridChanged(new Grid()
      {
         @Override
         public int getHeight ()
         {
            return _height;
         }

         @Override
         public int getWidth ()
         {
            return _width;
         }

         @Override
         public boolean hasLife (final int p_column, final int p_row)
         {
            return _cells.contains(new Cell(p_column, p_row));
         }

      });
   }

   /**
    * Change une cellule.
    * @param p_column
    *           la colonne de la cellule
    * @param p_row
    *           la ligne de la cellule
    */
   public void toggleCellAt (final int p_column, final int p_row)
   {
      final Cell v_toggled = new Cell(p_column, p_row);
      if (_cells.contains(v_toggled))
      {
         _cells.remove(v_toggled);
      }
      else
      {
         _cells.add(v_toggled);
      }
      notifyObserver();
   }

   /**
    * Génère.
    */
   public void nextGeneration ()
   {
      throw new UnsupportedOperationException("TO DO");
   }

}
