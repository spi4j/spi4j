/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * TableModel de SpiTable.
 * @param <TypeValue>
 *           Type des valeurs de la liste
 * @author MINARM
 */
public abstract class SpiListTableModel<TypeValue> extends AbstractTableModel
{
   private static final long serialVersionUID = 1L;

   private final JTable table;

   private List<TypeValue> list = new ArrayList<TypeValue>(0);

   /**
    * Constructeur.
    * @param p_table
    *           JTable
    */
   public SpiListTableModel (final JTable p_table)
   {
      super();
      this.table = p_table;
   }

   @Override
   public abstract Object getValueAt (final int rowIndex, final int columnIndex);

   /**
    * Retourne la table liée à ce modèle.
    * @return JTable
    */
   protected JTable getTable ()
   {
      return table;
   }

   @Override
   public Class<?> getColumnClass (final int column)
   {
      if (column >= 0 && column < getColumnCount())
      {
         // on parcours la colonne jusqu'à trouver un objet non null
         // pour trouver la classe même si la 1ère ligne est nulle
         final int rowCount = getRowCount();
         for (int i = 0; i < rowCount; i++)
         {
            final Object value = getValueAt(i, column);
            if (value != null)
            {
               return value.getClass();
            }
         }
      }

      // en dernier recours
      return Object.class;
   }

   /**
    * Ajoute un objet.
    * @param object
    *           TypeValue
    * @see #removeObject
    */
   public void addObject (final TypeValue object)
   {
      list.add(object);
      fireTableDataChanged();
   }

   /**
    * Enlève un objet.
    * @param object
    *           TypeValue
    * @see #addObject
    */
   public void removeObject (final TypeValue object)
   {
      list.remove(object);
      fireTableDataChanged();
   }

   /**
    * Retourne la valeur de la propriété list.
    * @return List
    * @see #setList
    */
   public List<TypeValue> getList ()
   {
      return new ArrayList<TypeValue>(list);
   }

   /**
    * Définit la valeur de la propriété list.
    * @param newList
    *           List
    * @see #getList
    */
   public void setList (final List<TypeValue> newList)
   {
      if (newList != null)
      {
         list = new ArrayList<TypeValue>(newList);
      }
      else
      {
         list = new ArrayList<TypeValue>(0);
      }
      fireTableDataChanged();
   }

   /**
    * Retourne l'objet à la position rowIndex.
    * @return Object
    * @param rowIndex
    *           int
    */
   public TypeValue getObjectAt (final int rowIndex)
   {
      if (rowIndex < 0 || rowIndex >= getRowCount())
      {
         // contournement du bug Sun 6894632
         // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6894632
         // qui s'est manifesté dans COSI pour la sélection des composants dans QuiOu d'une opération d'infrastructure
         // (Mantis 910, https://safran-forge-sid.sid.defense.gouv.fr/mantis/view.php?id=910)

         // (table.getSelectedRow() peut renvoyer une ligne qui n'est plus dans la table)
         return null;
      }
      return list.get(rowIndex);
   }

   @Override
   public int getRowCount ()
   {
      return list.size();
   }

   @Override
   public int getColumnCount ()
   {
      return table.getColumnCount();
   }

   /**
    * Retourne un booléen suivant que la cellule est éditable (false par défaut). <br/>
    * Ici, l'implémentation est faite selon que la propriété cellEditor de la TableColumn correspondante est nulle ou non.
    * @return boolean
    * @param row
    *           int
    * @param column
    *           int
    */
   @Override
   public boolean isCellEditable (final int row, final int column)
   {
      final int index = table.convertColumnIndexToView(column);
      return table.isEnabled() && table.getColumnModel().getColumn(index).getCellEditor() != null;
   }
}
