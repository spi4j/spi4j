/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import fr.spi4j.ui.swing.SpiSwingUtilities;

/**
 * Ce KeySelectionManager sélectionne les éléments d'une comboBox selon une chaîne de caractères au lieu d'un seul caractère. <br/>
 * La chaîne se constitue par les caractères se suivant à moins de 500 ms, et se réinitialise au delà.
 * @author MINARM (et javaalmanac)
 */
@SuppressWarnings("rawtypes")
class SpiComboBoxKeySelectionManager implements JComboBox.KeySelectionManager
{
   private static final long DELAY = 500;

   private long lastKeyTime;

   private String pattern = "";

   private final JComboBox comboBox;

   /**
    * Constructeur.
    * @param p_comboBox
    *           JComboBox
    */
   SpiComboBoxKeySelectionManager (final JComboBox p_comboBox)
   {
      super();
      this.comboBox = p_comboBox;
   }

   @Override
   public int selectionForKey (final char aKey, final ComboBoxModel model)
   {
      // Recherche selectedIndex
      int selectedIndex = 1;
      final Object selectedItem = model.getSelectedItem();
      final int modelSize = model.getSize();
      if (selectedItem != null)
      {
         for (int i = 0; i < modelSize; i++)
         {
            if (selectedItem.equals(model.getElementAt(i)))
            {
               selectedIndex = i;
               break;
            }
         }
      }

      final long curTime = System.currentTimeMillis();

      // Si le dernier caractère a été tapé il y a moins de 500 ms,
      // on l'ajoute au pattern courant, sinon il devient le pattern courant
      if (curTime - lastKeyTime < DELAY)
      {
         pattern += Character.toLowerCase(aKey);
      }
      else
      {
         pattern = String.valueOf(Character.toLowerCase(aKey));
      }

      // Enregistre l'heure
      lastKeyTime = curTime;

      String s;
      Object elementAt;
      final int patternLength = pattern.length();
      // Recherche depuis la sélection courante
      for (int i = selectedIndex + 1; i < modelSize; i++)
      {
         elementAt = model.getElementAt(i);
         if (elementAt != null)
         {
            s = getTextForElement(elementAt, i);
            if (s.regionMatches(true, 0, pattern, 0, patternLength))
            {
               return i;
            }
         }
      }

      // Sinon, recherche depuis le début jusqu'à la sélection courante
      for (int i = 0; i < selectedIndex; i++)
      {
         elementAt = model.getElementAt(i);
         if (elementAt != null)
         {
            s = getTextForElement(elementAt, i);
            if (s.regionMatches(true, 0, pattern, 0, patternLength))
            {
               return i;
            }
         }
      }
      return -1;
   }

   /**
    * Texte correspondant à un élément.
    * @param element
    *           Object
    * @param index
    *           int
    * @return String
    */
   @SuppressWarnings("unchecked")
   private String getTextForElement (final Object element, final int index)
   {
      final Component rendererComponent = comboBox.getRenderer().getListCellRendererComponent(SpiComboBox.DUMMY_JLIST,
               element, index, false, false);
      // s'il y a un renderer contenant un texte comme un label (cas extrêmement probable)
      // alors le toolTip est le texte du renderer
      // (par défaut sur les SpiComboBox, il y a un renderer de type label qui utilise le toString() sur l'élément)
      String text = SpiSwingUtilities.getTextFromRendererComponent(rendererComponent);
      if (text == null)
      {
         // sinon on prend le toString() par défaut
         text = element.toString();
      }
      return text;
   }
}
