/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import fr.spi4j.business.dto.DtoUtil;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.entity.EntityUtil;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Composant Table typé servant de base à SpiTable.
 * @param <TypeValue>
 *           Type des valeurs de la liste
 * @author MINARM
 */
public class SpiListTable<TypeValue> extends SpiBasicTable
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    * @param dataModel
    *           Modèle pour les données (par exemple, SpiTableModel)
    */
   public SpiListTable (final SpiListTableModel<TypeValue> dataModel)
   {
      super(dataModel);
   }

   @Override
   public void setModel (final TableModel tableModel)
   {
      if (!(tableModel instanceof SpiListTableModel))
      {
         throw new IllegalArgumentException("model doit être instance de " + SpiListTableModel.class.getName());
      }
      super.setModel(tableModel);
   }

   /**
    * Retourne la valeur de la propriété listTableModel.
    * @return SpiListTableModel
    */
   @SuppressWarnings("unchecked")
   protected SpiListTableModel<TypeValue> getListTableModel ()
   {
      return (SpiListTableModel<TypeValue>) getModel();
   }

   /**
    * Retourne la valeur de la propriété list.
    * @return List
    * @see #setList
    */
   public List<TypeValue> getList ()
   {
      return getListTableModel().getList();
   }

   /**
    * Définit la valeur de la propriété list. <BR>
    * Cette méthode adaptent automatiquement les largeurs des colonnes selon les données.
    * @param newList
    *           List
    * @see #getList
    */
   public void setList (final List<TypeValue> newList)
   {
      getListTableModel().setList(newList);

      adjustColumnWidths();

      // Réinitialise les hauteurs des lignes qui pourraient avoir été particularisées
      // avec setRowHeight(row, rowHeight).
      setRowHeight(getRowHeight());

      // remarque perf: on pourrait parcourir les colonnes pour affecter des renderers aux colonnes n'en ayant pas selon getDefaultRenderer(getColumnClass(i))
      // pour éliminer les appels à getDefaultRenderer pour chaque cellule
   }

   /**
    * Retourne la liste d'objets sélectionnés.
    * @return List
    * @see #setSelectedList
    */
   public List<TypeValue> getSelectedList ()
   {
      final int[] selectedRows = getSelectedRows();
      int selectedRow;
      final int rowCount = getRowCount();
      final int length = selectedRows.length;
      final List<TypeValue> selectedList = new ArrayList<TypeValue>(length);
      for (int i = 0; i < length; i++)
      {
         selectedRow = selectedRows[i];
         // getSelectedRows peut renvoyer des lignes qui ne sont plus
         // dans la table si ce sont les denières sélectionnées
         if (selectedRow >= 0 && selectedRow < rowCount)
         {
            final TypeValue objectAt = getObjectAt(selectedRow);
            if (objectAt != null)
            {
               selectedList.add(objectAt);
            }
         }
      }

      return selectedList;
   }

   /**
    * Retourne l'objet sélectionné.
    * @return TypeValue
    * @see #setSelectedObject
    */
   public TypeValue getSelectedObject ()
   {
      if (getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_SELECTION)
      {
         throw new Spi4jRuntimeException("Appel à getSelectedObject() invalide pour une table en sélection multiple",
                  "Utiliser getSelectedList() ou bien mettre la table en sélection simple");
      }
      return getObjectAt(getSelectedRow());
   }

   /**
    * Définit la liste d'objets sélectionnés.
    * @param newSelectedList
    *           List
    * @see #getSelectedList
    */
   @SuppressWarnings("unchecked")
   public void setSelectedList (final List<TypeValue> newSelectedList)
   {
      clearSelection();
      if (newSelectedList == null || newSelectedList.isEmpty())
      {
         return;
      }

      final ListSelectionModel listSelectionModel = getSelectionModel();
      final List<TypeValue> list = getList();

      for (final TypeValue object : newSelectedList)
      {
         int rowIndex = list.indexOf(object);
         if (rowIndex == -1 && object instanceof Dto_Itf)
         {
            // si l'objet à sélectionner n'a pas été trouvé et si c'est un DTO, on le recherche par son identifiant
            // (car il peut arriver d'avoir deux instances du DTO avec le même identifiant, y compris pour le "référentiel")
            final Dto_Itf<?> dto = DtoUtil.findInCollectionById((List<Dto_Itf<?>>) list, (Dto_Itf<?>) object);
            rowIndex = list.indexOf(dto);
         }
         if (rowIndex == -1 && object instanceof Entity_Itf)
         {
            // si l'objet à sélectionner n'a pas été trouvé et si c'est un Entity, on le recherche par son identifiant
            // (car il peut arriver d'avoir deux instances du Entity avec le même identifiant, y compris pour le "référentiel")
            final Entity_Itf<?> dto = EntityUtil.findInCollectionById((List<Entity_Itf<?>>) list,
                     (Entity_Itf<?>) object);
            rowIndex = list.indexOf(dto);
         }

         if (rowIndex > -1)
         {
            rowIndex = convertRowIndexToView(rowIndex);
            listSelectionModel.addSelectionInterval(rowIndex, rowIndex);
         }
      }

      // scrolle pour afficher la première ligne sélectionnée
      final int firstIndex = getSelectionModel().getMinSelectionIndex();
      final Rectangle cellRect = getCellRect(firstIndex, 0, true);
      scrollRectToVisible(cellRect);
   }

   /**
    * Définit l'objet sélectionné. L'objet peut être null pour ne rien sélectionné.
    * @param newSelectedObject
    *           TypeValue
    * @see #getSelectedObject
    */
   public void setSelectedObject (final TypeValue newSelectedObject)
   {
      if (newSelectedObject != null)
      {
         final List<TypeValue> newSelectedList = new ArrayList<TypeValue>(1);
         newSelectedList.add(newSelectedObject);
         setSelectedList(newSelectedList);
      }
      else
      {
         clearSelection();
      }
   }

   /**
    * Renvoie l'objet à la position demandée. Attention rowIndex est l'index vu de la JTable avec les index, et non pas vu du ListTableModel.
    * @return Object
    * @param rowIndex
    *           int
    */
   TypeValue getObjectAt (final int rowIndex)
   {
      // getSelectedRow peut renvoyer une ligne qui n'est plus dans la table
      // si c'est la dernière sélectionnée
      if (rowIndex < 0 || rowIndex >= getRowCount())
      {
         return null;
      }

      final int row = convertRowIndexToModel(rowIndex);

      return getListTableModel().getObjectAt(row);
   }
}
