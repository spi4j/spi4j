/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.domain;

/**
 * La grille de jeu.
 * @author MINARM
 */
public interface Grid
{

   /**
    * NoOp Grid.
    */
   Grid c_NULL = new Grid()
   {

      @Override
      public int getHeight ()
      {
         return 0;
      }

      @Override
      public int getWidth ()
      {
         return 0;
      }

      @Override
      public boolean hasLife (final int p_column, final int p_row)
      {
         return false;
      }
   };

   /**
    * @return la largeur du jeu
    */
   int getWidth ();

   /**
    * @return la longueur du jeu
    */
   int getHeight ();

   /**
    * Cherche si une cellule a été cochée.
    * @param p_column
    *           la colonne de la cellule
    * @param p_row
    *           la ligne de la cellule
    * @return true si la cellule a été cochée et false sinon
    */
   boolean hasLife (int p_column, int p_row);

}
