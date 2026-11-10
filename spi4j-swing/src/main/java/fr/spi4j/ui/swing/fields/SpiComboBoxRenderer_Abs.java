/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.Component;

import javax.swing.JList;

/**
 * Renderer abstrait pour les éléments de liste déroulante (SpiComboBox).
 * @param <TypeValue>
 *           Type de l'objet affiché
 * @author MINARM
 */
public abstract class SpiComboBoxRenderer_Abs<TypeValue> extends SpiDefaultListCellRenderer
{
   private static final long serialVersionUID = 1L;

   /**
    * Méthode à implémenter par héritage pour le rendu (transforme un objet en String pour affichage).
    * @param value
    *           TypeValue
    * @return String
    */
   public abstract String valueAsString (final TypeValue value);

   @SuppressWarnings(
   {"unchecked", "rawtypes" })
   @Override
   public Component getListCellRendererComponent (final JList list, final Object value, final int index,
            final boolean isSelected, final boolean cellHasFocus)
   {
      final Object myValue;
      // dans WindowsComboBoxUI.getBaseline, il peut arriver que value soit " " au lieu d'être du bon type
      if (value == null || value == SpiComboBoxModel.NULL_VALUE || " ".equals(value))
      {
         myValue = value;
      }
      else
      {
         myValue = valueAsString((TypeValue) value);
      }
      return super.getListCellRendererComponent(list, myValue, index, isSelected, cellHasFocus);
   }
}
