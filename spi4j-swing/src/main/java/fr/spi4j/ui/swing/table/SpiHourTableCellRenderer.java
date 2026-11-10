/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.text.DateFormat;

/**
 * Définit un renderer pour représenter une heure (Date) dans une JTable.
 * @author MINARM
 */
public class SpiHourTableCellRenderer extends SpiDateTableCellRenderer
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public SpiHourTableCellRenderer ()
   {
      super();
      setDateFormat(DateFormat.getTimeInstance(DateFormat.SHORT));
   }
}
