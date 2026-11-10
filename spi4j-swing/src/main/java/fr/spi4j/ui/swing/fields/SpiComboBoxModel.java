/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * Modèle de données pour une JComboBox basée sur une List.
 * @param <TypeValue>
 *           Type de la valeur
 * @author MINARM
 */
@SuppressWarnings("rawtypes")
class SpiComboBoxModel<TypeValue> extends AbstractListModel implements ComboBoxModel
{
   /**
    * Objet représentant la valeur nulle (rien sélectionné) dans la liste.
    */
   protected static final Object NULL_VALUE = new Serializable()
   {
      private static final long serialVersionUID = 1L;

      @Override
      public String toString ()
      {
         return "";
      }
   };

   private static final long serialVersionUID = 1L;

   // NULL_VALUE (index 0 et non -1) est sélectionné par défaut
   private Object selectedItem = NULL_VALUE;

   private Object unknownValue;

   private boolean nullValueDisplayed = true;

   private List<TypeValue> list = new ArrayList<TypeValue>(0);

   /**
    * Constructeur.
    */
   SpiComboBoxModel ()
   {
      super();
   }

   @Override
   public Object getElementAt (final int index)
   {
      // Cette méthode renvoie la valeur à afficher,
      // ou renvoie la constante NULL_VALUE pour la position 0.
      final int nullOffset = getNullOffset();
      if (index < nullOffset)
      {
         return NULL_VALUE;
      }
      // si index == nullOffset, c'est NULL_VALUE

      if (index > list.size() + nullOffset)
      {
         return "??"; // imprévu
      }

      Object object;
      if (index != list.size() + nullOffset)
      {
         object = list.get(index - nullOffset);
      }
      else
      {
         object = getUnknownValue();
      }
      // si index == getList().size() + nullOffset, c'est unknownValue

      if (object == null)
      {
         return NULL_VALUE;
      }
      // il n'est pas possible de renvoyer null, cela ferait planter JComboBox.getSelectedIndex
      return object;
   }

   @Override
   public Object getSelectedItem ()
   {
      return selectedItem;
   }

   @Override
   public int getSize ()
   {
      // Retourne le nombre d'éléments dans la liste
      // (+1 pour la valeur nulle et +1 si on a sélectionné un objet qui n'était pas dans la liste)
      return list.size() + getNullOffset() + (getUnknownValue() != null ? 1 : 0);
   }

   @Override
   public void setSelectedItem (final Object newSelectedItem)
   {
      this.selectedItem = newSelectedItem;
      fireContentsChanged(this, -1, -1);
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
         this.list = new ArrayList<TypeValue>(newList);
      }
      else
      {
         this.list = new ArrayList<TypeValue>(0);
      }

      // et on réinitialise unknownValue s'il y en avait une
      setUnknownValue(null);

      // si selectedItem est aussi dans la nouvelle liste,
      // on le laisse sélectionné (et on dit que la liste a changé),
      // sinon on sélectionne NULL_VALUE
      if (this.list.contains(selectedItem))
      {
         fireContentsChanged(this, -1, -1);
      }
      else if (!isNullValueDisplayed() && !list.isEmpty())
      {
         setSelectedItem(list.get(0));
      }
      else
      {
         setSelectedItem(NULL_VALUE);
      }
      // setSelectedItem lance aussi contentsChanged
   }

   /**
    * Retourne la valeur de la propriété unknownValue.
    * @return Object
    * @see #setUnknownValue
    */
   protected Object getUnknownValue ()
   {
      return unknownValue;
   }

   /**
    * Définit la valeur de la propriété unknownValue.
    * @param newUnknownValue
    *           Object
    * @see #getUnknownValue
    */
   protected void setUnknownValue (final Object newUnknownValue)
   {
      unknownValue = newUnknownValue;
   }

   /**
    * Retourne 1 si nullValueDisplayed == true, 0 sinon.
    * @return int
    */
   protected int getNullOffset ()
   {
      if (isNullValueDisplayed())
      {
         return 1;
      }
      return 0;
   }

   /**
    * Retourne la valeur de la propriété nullValueDisplayed (true par défaut).
    * @return boolean
    * @see #setNullValueDisplayed
    */
   public boolean isNullValueDisplayed ()
   {
      return nullValueDisplayed;
   }

   /**
    * Définit la valeur de la propriété nullValueDisplayed.
    * @param newNullValueDisplayed
    *           boolean
    * @see #isNullValueDisplayed
    */
   public void setNullValueDisplayed (final boolean newNullValueDisplayed)
   {
      nullValueDisplayed = newNullValueDisplayed;
   }
}
