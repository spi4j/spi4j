/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import fr.spi4j.business.dto.DtoUtil;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.persistence.entity.EntityUtil;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ui.HasSelection_Itf;
import fr.spi4j.ui.swing.SpiSwingUtilities;

/**
 * Champs liste déroulante. <br/>
 * Les méthodes addItem, insertItemAt, removeItem, removeItemAt, removeAllItems ne peuvent être utilisées car le model n'est pas 'mutable'. <br/>
 * Il faut utiliser getList et setList pour définir les données affichées. <br/>
 * Et il faut utiliser la méthode setRenderer avec une implémentation héritant de SpiComboBoxRenderer_Abs ou de SpiDefaultListCellRenderer pour définir le rendu.<br/>
 * Les méthodes get/set pour selectedItem et selectedIndex ne peuvent être utilisées car elles sont appelées en interne Swing et qu'elle ne gère pas la valeur nulle. <br/>
 * Il faut utiliser getSelectedObject et setSelectedObject. <br/>
 * @param <TypeValue>
 *           Type de la valeur
 * @author MINARM
 */
@SuppressWarnings("rawtypes")
public class SpiComboBox<TypeValue> extends JComboBox implements HasSelection_Itf<TypeValue>
{
   static final JList DUMMY_JLIST = new JList();

   private static final long serialVersionUID = 1L;

   private static final DefaultListCellRenderer DEFAULT_RENDERER = new SpiDefaultListCellRenderer();

   private static final ItemListener ITEM_HANDLER = new ItemHandler();

   /**
    * ItemListener.
    * @author MINARM
    */
   private static class ItemHandler implements ItemListener
   {
      /**
       * Constructeur.
       */
      ItemHandler ()
      {
         super();
      }

      @SuppressWarnings("unchecked")
      @Override
      public void itemStateChanged (final ItemEvent itemEvent)
      {
         if (itemEvent.getSource() instanceof JComboBox)
         {
            final JComboBox comboBox = (JComboBox) itemEvent.getSource();
            final Object selectedItem = comboBox.getSelectedItem();
            final String toolTipText;
            if (selectedItem != null && SpiComboBoxModel.NULL_VALUE != selectedItem)
            {
               // si élément sélectionné non null
               final Component rendererComponent = comboBox.getRenderer().getListCellRendererComponent(DUMMY_JLIST,
                        selectedItem, comboBox.getSelectedIndex(), false, false);
               final String text = SpiSwingUtilities.getTextFromRendererComponent(rendererComponent);
               if (text != null)
               {
                  // s'il y a un renderer contenant un texte comme un label (cas extrêmement probable)
                  // alors le toolTip est le texte du renderer
                  // (par défaut sur les SpiComboBox, il y a un renderer de type label qui utilise le toString() sur l'élément)
                  toolTipText = text;
               }
               else
               {
                  // sinon on prend le toString() par défaut
                  toolTipText = selectedItem.toString();
               }
            }
            else
            {
               toolTipText = null;
            }
            comboBox.setToolTipText(toolTipText);
         }
      }
   }

   private boolean layingOut;

   /**
    * Constructeur.
    */
   @SuppressWarnings("unchecked")
   public SpiComboBox ()
   {
      super(new SpiComboBoxModel<TypeValue>());
      setSize(100, 21); // taille par défaut pour une ComboBox (21 et non 25)
      // on fixe la taille préférée pour éviter que les largeurs des comboBox et des écrans soient infiniment grandes selon les textes
      setPreferredSize(getSize());
      addItemListener(ITEM_HANDLER);
      setRenderer(DEFAULT_RENDERER);
      // ligne ci-dessous nécessaire sinon createDefaultKeySelectionManager() n'est pas appelé et le manager est celui dans UI
      setKeySelectionManager(createDefaultKeySelectionManager());
   }

   @Override
   protected JComboBox.KeySelectionManager createDefaultKeySelectionManager ()
   {
      return new SpiComboBoxKeySelectionManager(this);
   }

   /**
    * Retourne la valeur de la propriété comboBoxModel.
    * @return SpiComboBoxModel
    */
   @SuppressWarnings("unchecked")
   protected SpiComboBoxModel<TypeValue> getComboBoxModel ()
   {
      return (SpiComboBoxModel<TypeValue>) getModel();
   }

   /**
    * Retourne la valeur de la propriété list.
    * @return List
    * @see #setList
    */
   @Override
   public List<TypeValue> getList ()
   {
      return getComboBoxModel().getList();
   }

   /**
    * Définit la valeur de la propriété list.
    * @param newList
    *           List
    * @see #getList
    */
   @Override
   public void setList (final List<TypeValue> newList)
   {
      getComboBoxModel().setList(newList);
   }

   /**
    * Retourne l'objet sélectionné.
    * @return TypeValue
    * @see #setSelectedObject
    */
   @SuppressWarnings("unchecked")
   public TypeValue getSelectedObject ()
   {
      final Object selectedItem = super.getSelectedItem();
      if (selectedItem == SpiComboBoxModel.NULL_VALUE)
      {
         return null;
      }
      return (TypeValue) selectedItem;
   }

   /**
    * Sélectionne un objet. <br/>
    * L'objet peut être null et il peut ne pas être dans la liste. Si il n'est pas dans la liste, il est ajouté temporairement à la fin de la liste et enlevé dès qu'un autre objet est sélectionné (par le programme, pas par l'utilisateur). <br/>
    * L'objet 'null' est affiché et sélectionnable au début de la liste.
    * @param newSelectedObject
    *           TypeValue
    * @see #getSelectedObject
    */
   @SuppressWarnings("unchecked")
   public void setSelectedObject (final TypeValue newSelectedObject)
   {
      final SpiComboBoxModel<TypeValue> model = getComboBoxModel();
      final List<TypeValue> list = getList();
      int index = list.indexOf(newSelectedObject);
      if (index == -1 && newSelectedObject instanceof Dto_Itf)
      {
         // si l'objet à sélectionner n'a pas été trouvé et si c'est un DTO, on le recherche par son identifiant
         // (car il peut arriver d'avoir deux instances du DTO avec le même identifiant, y compris pour le "référentiel")
         final Dto_Itf<?> dto = DtoUtil.findInCollectionById((List<Dto_Itf<?>>) list, (Dto_Itf<?>) newSelectedObject);
         index = list.indexOf(dto);
      }
      if (index == -1 && newSelectedObject instanceof Entity_Itf)
      {
         // si l'objet à sélectionner n'a pas été trouvé et si c'est un Entity, on le recherche par son identifiant
         // (car il peut arriver d'avoir deux instances du Entity avec le même identifiant, y compris pour le "référentiel")
         final Entity_Itf<?> entity = EntityUtil.findInCollectionById((List<Entity_Itf<?>>) list,
                  (Entity_Itf<?>) newSelectedObject);
         index = list.indexOf(entity);
      }

      if (newSelectedObject == null || index >= 0)
      {
         model.setUnknownValue(null);
         index = newSelectedObject == null ? 0 : index + model.getNullOffset();
         // si le selectedObjet est null ou est dans la liste,
         // on met unknowValue à null pour annuler un unknownValue précédent
      }
      else
      {
         model.setUnknownValue(newSelectedObject);
         // et si le selectedObjet n'est pas dans la liste, on le met dans unknowValue,
         // il apparaîtra à la fin (voir SpiComboBoxModel.getSize et SpiComboBoxModel.getElementAt)
         index = model.getSize() - 1; // model.size == model.list.size + 1 (nullValue) + 1 (unknownValue)
      }
      super.setSelectedIndex(index);
   }

   /**
    * Retourne la valeur de la propriété nullValueDisplayed.
    * @return boolean
    * @see #setNullValueDisplayed
    */
   public boolean isNullValueDisplayed ()
   {
      return getComboBoxModel().isNullValueDisplayed();
   }

   /**
    * Définit la valeur de la propriété nullValueDisplayed.
    * @param newNullValueDisplayed
    *           boolean
    * @see #isNullValueDisplayed
    */
   public void setNullValueDisplayed (final boolean newNullValueDisplayed)
   {
      getComboBoxModel().setNullValueDisplayed(newNullValueDisplayed);
   }

   /**
    * Retourne la valeur de l'item à la position donnée.
    * @return Object
    * @param index
    *           int
    * @deprecated Utiliser getList ou getSelectedObject
    */
   @Deprecated
   @Override
   public Object getItemAt (final int index)
   {
      return super.getItemAt(index);
   }

   /**
    * Retourne le nombre d'items.
    * @return int
    * @deprecated Utiliser getList
    */
   @Deprecated
   @Override
   public int getItemCount ()
   {
      return super.getItemCount();
   }

   /**
    * Retourne la valeur de la propriété selectedIndex.
    * @return int
    * @deprecated Utiliser getSelectedObject
    */
   @Deprecated
   @Override
   public int getSelectedIndex ()
   {
      return super.getSelectedIndex();
   }

   /**
    * Retourne la valeur de la propriété selectedItem.
    * @return Object
    * @deprecated Utiliser getSelectedObject
    */
   @Deprecated
   @Override
   public Object getSelectedItem ()
   {
      return super.getSelectedItem();
   }

   /**
    * Définit la valeur de la propriété selectedIndex.
    * @param newSelectedIndex
    *           int
    * @deprecated Utiliser setSelectedObject
    */
   @Deprecated
   @Override
   public void setSelectedIndex (final int newSelectedIndex)
   {
      super.setSelectedIndex(newSelectedIndex);
   }

   /**
    * Définit la valeur de la propriété selectedItem.
    * @param newSelectedItem
    *           Object
    * @deprecated Utiliser setSelectedObject
    */
   @Deprecated
   @Override
   public void setSelectedItem (final Object newSelectedItem)
   {
      super.setSelectedItem(newSelectedItem);
   }

   @Override
   public TypeValue getValue ()
   {
      return getSelectedObject();
   }

   @Override
   public void setValue (final TypeValue p_value)
   {
      setSelectedObject(p_value);
   }

   /**
    * Définit le renderer de la comboBox.
    * @param renderer
    *           SpiComboBoxRenderer_Abs
    */
   @SuppressWarnings("unchecked")
   public void setRenderer (final SpiComboBoxRenderer_Abs<TypeValue> renderer)
   {
      super.setRenderer(renderer);
   }

   @Override
   public void doLayout ()
   {
      // Causes this container to lay out its components
      try
      {
         layingOut = true;
         super.doLayout();
      }
      finally
      {
         layingOut = false;
      }
   }

   @Override
   public Dimension getSize ()
   {
      // Returns the size of this component in the form of a Dimension object (workaround RFE 4618607)
      final Dimension dim = super.getSize();
      if (!layingOut)
      {
         // calcul de la taille si affichage de la popup déroulante (pour que la popup soit plus grande si besoin selon les textes)
         Dimension preferredSize = null;
         if (ui != null)
         {
            // on préfère ui.getPreferredSize, plutôt que getPreferredSize qui a été définie dans le constructeur
            // (getPreferredSize n'est plus une valeur dépendante du texte mais seulement une valeur raisonnable pour la comboBox sans popup)
            preferredSize = ui.getPreferredSize(this);
         }
         if (preferredSize == null)
         {
            // au pire
            preferredSize = getPreferredSize();
         }

         dim.width = Math.max(dim.width, preferredSize.width);
         // quel que soit le texte on ne dépasse pas l'écran
         dim.width = Math.min(dim.width, getToolkit().getScreenSize().width);
      }
      return dim;
   }

   /**
    * Vérifie si une valeur est nulle (en testant en plus l'objet <code>{@link SpiComboBoxModel}.NULL_VALUE</code>)
    * @param p_value
    *           la valeur saisie dans la SpiTable
    * @return true si la valeur saisie dans la table est nulle (peut être la valeur nulle par défaut d'une combobox)
    */
   public static boolean isNullValue (final Object p_value)
   {
      return p_value == null || p_value == SpiComboBoxModel.NULL_VALUE;
   }
}
