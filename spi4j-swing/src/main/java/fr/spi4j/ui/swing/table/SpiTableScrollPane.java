/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import javax.swing.JScrollPane;

/**
 * ScrollPane avec une table directement intégrée.
 * @param <TypeValue>
 *           Type des valeurs de la liste
 * @author MINARM
 */
public class SpiTableScrollPane<TypeValue> extends JScrollPane
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public SpiTableScrollPane ()
   {
      super();
      setTable(createTable());
   }

   /**
    * Constructeur.
    * @param table
    *           SpiTable_Abs
    */
   public SpiTableScrollPane (final SpiTable_Abs<TypeValue> table)
   {
      super();
      if (table == null)
      {
         throw new NullPointerException();
      }
      setTable(table);
   }

   /**
    * Crée la table incluse (SpiTable par défaut). Cette méthode peut être surchargée pour créer une table d'un type différent.
    * @return SpiTable
    */
   protected SpiTable<TypeValue> createTable ()
   {
      return new SpiTable<TypeValue>();
   }

   /**
    * Retourne la table incluse.
    * @return SpiTable
    */
   @SuppressWarnings("unchecked")
   public SpiTable<TypeValue> getTable ()
   {
      // remarque: ici on retourne un type SpiTable car c'est cette classe qui implémente HasSelection_Itf,
      // cette méthode getTable() n'est donc pas utilisable pour un autre type de SpiTable_Abs,
      // par exemple pour un SpiSelectorTable
      return (SpiTable<TypeValue>) getViewport().getView();
   }

   /**
    * Redéfinit la table incluse.
    * @param table
    *           SpiTable_Abs
    */
   public void setTable (final SpiTable_Abs<TypeValue> table)
   {
      setViewportView(table);
   }
}
