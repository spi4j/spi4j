/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.domain;

/**
 * Cellule du jeu.
 * @author MINARM
 */
public class Cell
{

   private final int _column;

   private final int _row;

   /**
    * Constructeur.
    * @param p_column
    *           la colonne
    * @param p_row
    *           la ligne
    */
   public Cell (final int p_column, final int p_row)
   {
      this._column = p_column;
      this._row = p_row;
   }

   @Override
   public int hashCode ()
   {
      final int v_prime = 31;
      int v_result = 1;
      v_result = v_prime * v_result + _column;
      v_result = v_prime * v_result + _row;
      return v_result;
   }

   @Override
   public boolean equals (final Object p_obj)
   {
      final Cell v_other = (Cell) p_obj;
      return v_other._column == this._column && v_other._row == this._row;
   }

}
