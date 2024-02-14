/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.util.Collection;
import java.util.Iterator;

/**
 * Définit un renderer pour représenter une List dans une JTable.
 * @author MINARM
 */
public class SpiListTableCellRenderer extends SpiDefaultTableCellRenderer
{
   private static final long serialVersionUID = 1L;

   @Override
   public void setValue (final Object value)
   {
      if (value == null)
      {
         this.setText(null);
      }
      else if (value instanceof Collection)
      {
         // Collection inclue ArrayList, HashMap, Vector, Hashtable ...

         // extrait de AbstractCollection.toString pour éviter substring pour enlever []
         final Collection<?> collection = (Collection<?>) value;
         final StringBuilder sb = new StringBuilder();
         final Iterator<?> iterator = collection.iterator();
         final int maxIndex = collection.size() - 1;
         for (int i = 0; i <= maxIndex; i++)
         {
            sb.append(String.valueOf(iterator.next()));
            if (i < maxIndex)
            {
               sb.append(", ");
            }
         }
         this.setText(sb.toString());
      }
      else
      {
         this.setText("??");
      }
   }
}
