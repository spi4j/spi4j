/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

/**
 * Définit un renderer pour représenter un texte centré dans une JTable.
 * @author MINARM
 */
public class SpiCenteredTextTableCellRenderer extends SpiDefaultTableCellRenderer
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public SpiCenteredTextTableCellRenderer ()
   {
      super();
      setHorizontalAlignment(CENTER);
   }
}
