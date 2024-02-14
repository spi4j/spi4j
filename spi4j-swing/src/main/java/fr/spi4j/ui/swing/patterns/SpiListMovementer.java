/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.patterns;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.TableColumn;

import fr.spi4j.business.dto.DtoUtil;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.persistence.entity.EntityUtil;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ui.HasMultipleSelection_Itf;
import fr.spi4j.ui.swing.table.SpiTable;

/**
 * Composant mouvementeur permettant de choisir certains éléments parmi une liste d'éléments disponibles et des les déplacer dans une liste d'éléments sélectionnés. <br/>
 * C'est donc une alternative facile à comprendre à la SpiSelectorTable.
 * @param <TypeValue>
 *           Type des valeurs de la liste
 * @author MINARM
 */
public class SpiListMovementer<TypeValue> extends JPanel implements ActionListener, HasMultipleSelection_Itf<TypeValue>
{
   /**
    * Implémentation du TransferHandler pour le drag and drop.
    */
   private final class ListMovementerTransferHandler extends TransferHandler
   {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean canImport (final TransferSupport support)
      {
         // on exclut le transfert à soi-même
         return !getTargetTableFromSupport(support).getList().containsAll(
                  getTransferDataFromTransferable(support.getTransferable()))
                  && support.isDataFlavorSupported(LIST_MOVEMENTER_FLAVOR);
      }

      /**
       * @param support
       *           TransferSupport
       * @return SpiTable
       */
      @SuppressWarnings("unchecked")
      private SpiTable<TypeValue> getTargetTableFromSupport (final TransferSupport support)
      {
         if (support.getComponent() instanceof SpiTable)
         {
            return (SpiTable<TypeValue>) support.getComponent();
         }
         else
         {
            // c'est le scrollPane dont on récupère la table
            return (SpiTable<TypeValue>) ((JScrollPane) support.getComponent()).getViewport().getView();
         }
      }

      /**
       * @param transferable
       *           Transferable
       * @return List
       */
      @SuppressWarnings("unchecked")
      private List<TypeValue> getTransferDataFromTransferable (final Transferable transferable)
      {
         try
         {
            return (List<TypeValue>) transferable.getTransferData(LIST_MOVEMENTER_FLAVOR);
         }
         catch (final UnsupportedFlavorException e)
         {
            throw new IllegalStateException(e);
         }
         catch (final IOException e)
         {
            throw new IllegalStateException(e);
         }
      }

      @Override
      public int getSourceActions (final JComponent c)
      {
         return TransferHandler.MOVE;
      }

      @SuppressWarnings("unchecked")
      @Override
      protected Transferable createTransferable (final JComponent c)
      {
         return createListMovementerTransferable((SpiTable<TypeValue>) c);
      }

      @Override
      public boolean importData (final TransferHandler.TransferSupport support)
      {
         if (!support.isDrop())
         {
            return false;
         }

         final List<TypeValue> transferData = getTransferDataFromTransferable(support.getTransferable());
         final SpiTable<TypeValue> targetTable = getTargetTableFromSupport(support);
         final List<TypeValue> list = new ArrayList<TypeValue>(targetTable.getList());
         list.addAll(transferData);
         targetTable.setList(list);

         return true;
      }

      @SuppressWarnings("unchecked")
      @Override
      protected void exportDone (final JComponent source, final Transferable data, final int action)
      {
         if (action == TransferHandler.MOVE)
         {
            final SpiTable<TypeValue> sourceTable = (SpiTable<TypeValue>) source;
            final List<TypeValue> list = new ArrayList<TypeValue>(sourceTable.getList());
            list.removeAll(getTransferDataFromTransferable(data));
            sourceTable.setList(list);
         }
      }
   }

   /**
    * Flavor du drag and drop.
    */
   public static final DataFlavor LIST_MOVEMENTER_FLAVOR = new DataFlavor("application/x-"
            + SpiListMovementer.class.getSimpleName().toLowerCase(), "Liste à déplacer");

   /** ActionCommand du bouton left. */
   protected static final String LEFT_ACTION = "left";

   /** ActionCommand du bouton right. */
   protected static final String RIGHT_ACTION = "right";

   /** ActionCommand du bouton leftleft. */
   protected static final String LEFTLEFT_ACTION = "leftLeft";

   /** ActionCommand du bouton rightright. */
   protected static final String RIGHTRIGHT_ACTION = "rightRight";

   private static final long serialVersionUID = 1L;

   private static final Icon LEFT_ICON = new ImageIcon(SpiListMovementer.class.getResource("/icons/Left16.gif"));

   private static final Icon RIGHT_ICON = new ImageIcon(SpiListMovementer.class.getResource("/icons/Right16.gif"));

   private static final Icon LEFTLEFT_ICON = new ImageIcon(SpiListMovementer.class.getResource("/icons/LeftLeft16.gif"));

   private static final Icon RIGHTRIGHT_ICON = new ImageIcon(
            SpiListMovementer.class.getResource("/icons/RightRight16.gif"));

   private static final GridLayout BUTTONS_GRID_LAYOUT = new GridLayout(0, 1, 0, 10);

   private JScrollPane selectionScrollPane;

   private JScrollPane selectableScrollPane;

   private SpiTable<TypeValue> selectionTable;

   private SpiTable<TypeValue> selectableTable;

   private JPanel buttonsPanel;

   /**
    * Constructeur.
    */
   public SpiListMovementer ()
   {
      super(new GridBagLayout());
      prepare();
      prepareDragAndDrop();
   }

   /**
    * Gestion de actions sur les boutons.
    * @param event
    *           ActionEvent
    */
   @Override
   public void actionPerformed (final ActionEvent event)
   {
      final String actionCommand = event.getActionCommand();
      List<TypeValue> selectables = getSelectableTable().getList();
      List<TypeValue> selections = getSelectionTable().getList();
      List<TypeValue> selectedSelectables = getSelectableTable().getSelectedList();
      List<TypeValue> selectedSelections = getSelectionTable().getSelectedList();

      if (RIGHT_ACTION.equals(actionCommand))
      {
         if (selectedSelectables.isEmpty())
         {
            return;
         }
         selections.addAll(selectedSelectables);
         selectables.removeAll(selectedSelectables);
         selectedSelections = selectedSelectables;
         selectedSelectables = null;
      }
      else if (LEFT_ACTION.equals(actionCommand))
      {
         if (selectedSelections.isEmpty())
         {
            return;
         }
         selections.removeAll(selectedSelections);
         selectables.addAll(selectedSelections);
         selectedSelectables = selectedSelections;
         selectedSelections = null;
      }
      else if (RIGHTRIGHT_ACTION.equals(actionCommand))
      {
         if (selectables.isEmpty())
         {
            return;
         }
         selections.addAll(selectables);
         selectedSelections = selectables;
         selectedSelectables = null;
         selectables = null;
      }
      else if (LEFTLEFT_ACTION.equals(actionCommand))
      {
         if (selections.isEmpty())
         {
            return;
         }
         selectables.addAll(selections);
         selectedSelections = null;
         selectedSelectables = selections;
         selections = null;
      }

      getSelectionTable().setList(selections);
      getSelectableTable().setList(selectables);
      getSelectionTable().setSelectedList(selectedSelections);
      getSelectableTable().setSelectedList(selectedSelectables);
   }

   /**
    * Retourne la valeur de la propriété buttonsPanel.
    * @return JPanel
    */
   protected JPanel getButtonsPanel ()
   {
      if (buttonsPanel == null)
      {
         buttonsPanel = new JPanel(BUTTONS_GRID_LAYOUT);
         // buttonsGridLayout est un poids mouche
         buttonsPanel.setOpaque(false);

         final JButton leftButton = new JButton(null, LEFT_ICON);
         final JButton rightButton = new JButton(null, RIGHT_ICON);
         final JButton leftLeftButton = new JButton(null, LEFTLEFT_ICON);
         final JButton rightRightButton = new JButton(null, RIGHTRIGHT_ICON);

         leftButton.setActionCommand(LEFT_ACTION);
         rightButton.setActionCommand(RIGHT_ACTION);
         leftLeftButton.setActionCommand(LEFTLEFT_ACTION);
         rightRightButton.setActionCommand(RIGHTRIGHT_ACTION);

         leftButton.addActionListener(this);
         rightButton.addActionListener(this);
         leftLeftButton.addActionListener(this);
         rightRightButton.addActionListener(this);

         buttonsPanel.add(rightButton);
         buttonsPanel.add(rightRightButton);
         buttonsPanel.add(leftButton);
         buttonsPanel.add(leftLeftButton);
      }
      return buttonsPanel;
   }

   /**
    * Retourne la valeur de la propriété selectableList.
    * @return List
    * @see #setSelectableList
    */
   public List<TypeValue> getSelectableList ()
   {
      // clone
      return new ArrayList<TypeValue>(getSelectableTable().getList());
   }

   /**
    * Retourne la valeur de la propriété selectableScrollPane.
    * @return JScrollPane
    */
   protected JScrollPane getSelectableScrollPane ()
   {
      if (selectableScrollPane == null)
      {
         selectableScrollPane = new JScrollPane(getSelectableTable());
      }

      return selectableScrollPane;
   }

   /**
    * Retourne la valeur de la propriété selectableTable.
    * @return SpiTable
    * @see #setSelectableTable
    */
   public SpiTable<TypeValue> getSelectableTable ()
   {
      if (selectableTable == null)
      {
         selectableTable = new SpiTable<TypeValue>();
         final TableColumn tableColumn = new TableColumn(0);
         tableColumn.setIdentifier("selectable");
         tableColumn.setHeaderValue("Disponibles");
         // ajoute la colonne dans la table
         selectableTable.addColumn(tableColumn);
         selectableTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      }
      return selectableTable;
   }

   /**
    * Retourne la valeur de la propriété selectionList.
    * @return List
    * @see #setSelectionList
    */
   public List<TypeValue> getSelectionList ()
   {
      // clone
      return new ArrayList<TypeValue>(getSelectionTable().getList());
   }

   /**
    * Retourne la valeur de la propriété selectionScrollPane.
    * @return JScrollPane
    */
   protected JScrollPane getSelectionScrollPane ()
   {
      if (selectionScrollPane == null)
      {
         selectionScrollPane = new JScrollPane(getSelectionTable());
      }

      return selectionScrollPane;
   }

   /**
    * Retourne la valeur de la propriété selectionTable.
    * @return SpiTable
    * @see #setSelectionTable
    */
   public SpiTable<TypeValue> getSelectionTable ()
   {
      if (selectionTable == null)
      {
         selectionTable = new SpiTable<TypeValue>();
         final TableColumn tableColumn = new TableColumn(0);
         tableColumn.setIdentifier("selected");
         tableColumn.setHeaderValue("Sélectionnés");
         // ajoute la colonne dans la table
         selectionTable.addColumn(tableColumn);
         selectionTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      }
      return selectionTable;
   }

   /**
    * Initialisation.
    */
   protected void prepare ()
   {
      setOpaque(false);
      final int margin = 10;
      final Insets insets = new Insets(margin, margin, margin, margin);
      final int center = GridBagConstraints.CENTER;
      final int both = GridBagConstraints.BOTH;
      final int horizontal = GridBagConstraints.HORIZONTAL;
      GridBagConstraints constraints;

      constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, center, both, insets, 0, 0);
      add(getSelectableScrollPane(), constraints);

      constraints = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, center, horizontal, insets, 0, 0);
      add(getButtonsPanel(), constraints);

      constraints = new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, center, both, insets, 0, 0);
      add(getSelectionScrollPane(), constraints);

      // gestion des double-clics
      getSelectableTable().addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked (final MouseEvent e)
         {
            if (e.getClickCount() == 2)
            {
               // seul le paramètre "command" est important
               actionPerformed(new ActionEvent(getSelectableTable(), -1, RIGHT_ACTION));
            }
         }
      });
      getSelectionTable().addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked (final MouseEvent e)
         {
            if (e.getClickCount() == 2)
            {
               // seul le paramètre "command" est important
               actionPerformed(new ActionEvent(getSelectableTable(), -1, LEFT_ACTION));
            }
         }
      });
   }

   /**
    * Initialisation.
    */
   private void prepareDragAndDrop ()
   {
      final TransferHandler transferHandler = new ListMovementerTransferHandler();
      getSelectableTable().setDragEnabled(true);
      getSelectableTable().setTransferHandler(transferHandler);
      getSelectableScrollPane().setTransferHandler(transferHandler);

      getSelectionTable().setDragEnabled(true);
      getSelectionTable().setTransferHandler(transferHandler);
      getSelectionScrollPane().setTransferHandler(transferHandler);
   }

   /**
    * Définit la valeur de la propriété selectableList. <br/>
    * Doit être appelée avant setSelectedList.
    * @param newSelectableList
    *           List
    * @see #getSelectableList
    */
   public void setSelectableList (final List<TypeValue> newSelectableList)
   {
      // clone
      getSelectableTable().setList(
               newSelectableList != null ? new ArrayList<TypeValue>(newSelectableList) : new ArrayList<TypeValue>(0));
      if (!getSelectionTable().getList().isEmpty())
      {
         getSelectionTable().setList(null);
      }
   }

   /**
    * Définit la valeur de la propriété selectableTable.
    * @param newSelectableTable
    *           SpiTable
    * @see #getSelectableTable
    */
   public void setSelectableTable (final SpiTable<TypeValue> newSelectableTable)
   {
      selectableTable = newSelectableTable;

      getSelectableScrollPane().setViewportView(getSelectableTable());
   }

   /**
    * Définit la valeur de la propriété selectionList. <br/>
    * Doit être appelée après setSelectableList.
    * @param newSelectionList
    *           List
    * @see #getSelectionList
    */
   @SuppressWarnings("unchecked")
   public void setSelectionList (final List<TypeValue> newSelectionList)
   {
      if (newSelectionList == null)
      {
         getSelectionTable().setList(new ArrayList<TypeValue>(0));
      }
      else
      {
         // clone
         getSelectionTable().setList(new ArrayList<TypeValue>(newSelectionList));

         final List<TypeValue> selectableList = getSelectableList();
         for (final TypeValue object : newSelectionList)
         {
            final boolean removed = selectableList.remove(object);

            if (!removed && object instanceof Dto_Itf)
            {
               // si l'objet à sélectionner n'a pas été trouvé et si c'est un DTO, on le recherche par son identifiant
               // (car il peut arriver d'avoir deux instances du DTO avec le même identifiant, y compris pour le "référentiel")
               final Dto_Itf<?> dto = DtoUtil.findInCollectionById((List<Dto_Itf<?>>) selectableList,
                        (Dto_Itf<?>) object);
               selectableList.remove(dto);
            }

            if (!removed && object instanceof Entity_Itf)
            {
               // si l'objet à sélectionner n'a pas été trouvé et si c'est un Entity, on le recherche par son identifiant
               // (car il peut arriver d'avoir deux instances du Entity avec le même identifiant, y compris pour le "référentiel")
               final Entity_Itf<?> entity = EntityUtil.findInCollectionById((List<Entity_Itf<?>>) selectableList,
                        (Entity_Itf<?>) object);
               selectableList.remove(entity);
            }

         }
         // enlève tous les objets contenus dans newSelectionList de selectableList s'il y en a
         getSelectableTable().setList(selectableList);
      }
   }

   /**
    * Définit la valeur de la propriété selectionTable.
    * @param newSelectionTable
    *           SpiTable
    * @see #getSelectionTable
    */
   public void setSelectionTable (final SpiTable<TypeValue> newSelectionTable)
   {
      selectionTable = newSelectionTable;

      getSelectionScrollPane().setViewportView(getSelectionTable());
   }

   @Override
   public List<TypeValue> getValue ()
   {
      return getSelectionList();
   }

   @Override
   public void setValue (final List<TypeValue> p_selectionList)
   {
      setSelectionList(p_selectionList);
   }

   @Override
   public List<TypeValue> getList ()
   {
      return getSelectableList();
   }

   @Override
   public void setList (final List<TypeValue> p_selectableList)
   {
      setSelectableList(p_selectableList);
   }

   /**
    * Implémentation du Transferable de drag and drop pour une table
    * @param table
    *           SpiTable
    * @return Transferable
    */
   private Transferable createListMovementerTransferable (final SpiTable<TypeValue> table)
   {
      final List<TypeValue> v_list = table.getSelectedList();

      return new Transferable()
      {
         @Override
         public boolean isDataFlavorSupported (final DataFlavor flavor)
         {
            return LIST_MOVEMENTER_FLAVOR.equals(flavor);
         }

         @Override
         public DataFlavor[] getTransferDataFlavors ()
         {
            return new DataFlavor[]
            {LIST_MOVEMENTER_FLAVOR };
         }

         @Override
         public Object getTransferData (final DataFlavor flavor) throws UnsupportedFlavorException, IOException
         {
            if (!isDataFlavorSupported(flavor))
            {
               throw new UnsupportedFlavorException(flavor);
            }
            return v_list;
         }
      };
   }
}
