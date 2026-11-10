/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import fr.spi4j.ui.HasSelection_Itf;

/**
 * Composant Table, qui utilise une API typée pour la liste des valeurs et qui utilise les AttributesNames_Itf (enum) pour définir les valeurs dans les colonnes.
 * @param <TypeValue>
 *           Type des valeurs de la liste
 * @author MINARM
 */
public class SpiTable<TypeValue> extends SpiTable_Abs<TypeValue> implements HasSelection_Itf<TypeValue>
{
   private static final long serialVersionUID = 1L;

   @Override
   public TypeValue getValue ()
   {
      // pour respecter l'interface HasSelection_Itf qui est typée génériquement avec TypeValue pour une SpiListTable, on retourne getSelectedObject() et non getSelectedList
      // (le typage générique de cette SpiListTable ne dépend pas dynamiquement de getSelectionModel().getSelectionMode() et donc d'une éventuelle sélection multiple)
      return getSelectedObject();
   }

   @Override
   public void setValue (final TypeValue p_value)
   {
      setSelectedObject(p_value);
   }
}
