/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.domain;

/**
 * Observer du jeu.
 * @author MINARM
 */
public interface GameObserver
{

   /**
    * NoOp GameObserver.
    */
   GameObserver c_NULL = new GameObserver()
   {

      @Override
      public void gridChanged (final Grid p_grid)
      {
         // Aucune action par défaut
      }

   };

   /**
    * Notification que la grille à changé.
    * @param p_grid
    *           la nouvelle grille
    */
   void gridChanged (Grid p_grid);

}
