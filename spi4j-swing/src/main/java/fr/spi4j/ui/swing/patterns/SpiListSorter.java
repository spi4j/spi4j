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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import fr.spi4j.ui.HasValue_Itf;
import fr.spi4j.ui.swing.SpiPanel;
import fr.spi4j.ui.swing.table.SpiTable;

/**
 * Composant trieur permettant de donner un ordre à des éléments.
 * @param <TypeValue>
 *           Type des valeurs de la liste
 * @author MINARM
 */
public class SpiListSorter<TypeValue> extends JPanel implements ActionListener, HasValue_Itf<List<TypeValue>>
{
   /** ActionCommand du bouton up. */
   protected static final String UP_ACTION = "up";

   /** ActionCommand du bouton down. */
   protected static final String DOWN_ACTION = "down";

   /** ActionCommand du bouton upUp. */
   protected static final String UPUP_ACTION = "upUp";

   /** ActionCommand du bouton downDown. */
   protected static final String DOWNDOWN_ACTION = "downDown";

   private static final long serialVersionUID = 1L;

   private static final GridLayout BUTTONS_GRID_LAYOUT = new GridLayout(0, 1, 0, 10);

   private static final Icon upIcon = new ImageIcon(SpiListSorter.class.getResource("/icons/Up16.gif"));

   private static final Icon downIcon = new ImageIcon(SpiListSorter.class.getResource("/icons/Down16.gif"));

   private static final Icon upUpIcon = new ImageIcon(SpiListSorter.class.getResource("/icons/UpUp16.gif"));

   private static final Icon downDownIcon = new ImageIcon(SpiListSorter.class.getResource("/icons/DownDown16.gif"));

   private JScrollPane scrollPane;

   private SpiTable<TypeValue> table;

   private JPanel buttonsPanel;

   /**
    * Constructeur.
    */
   public SpiListSorter ()
   {
      super(new GridBagLayout());
      prepare();
   }

   /**
    * Constructeur.
    * @param p_table
    *           SpiTable
    */
   public SpiListSorter (final SpiTable<TypeValue> p_table)
   {
      super(new GridBagLayout());
      this.table = p_table;
      prepare();
   }

   @Override
   public void actionPerformed (final ActionEvent event)
   {
      // note : heureusement la liste n'est pas triable par le header !
      final List<TypeValue> selectedList = getTable().getSelectedList();
      if (selectedList.isEmpty())
      {
         return;
      }

      String actionCommand = event.getActionCommand();
      final List<TypeValue> list = new ArrayList<TypeValue>(getTable().getList());
      final int selectedSize = selectedList.size();
      final int size = list.size();
      boolean loop = false;
      int index = -1;
      int oldIndex;
      TypeValue object;

      // si actionCommand == UPUP, actionCommand = UP et loop = true
      // si actionCommand == DOWNDOWN, actionCommand = DOWN et loop = true
      // sinon loop == false
      if (UPUP_ACTION.equals(actionCommand))
      {
         actionCommand = UP_ACTION;
         loop = true;
      }
      else if (DOWNDOWN_ACTION.equals(actionCommand))
      {
         actionCommand = DOWN_ACTION;
         loop = true;
      }

      if (UP_ACTION.equals(actionCommand))
      {
         do
         {
            oldIndex = index;
            for (int i = 0; i < selectedSize; i++)
            {
               object = selectedList.get(i);
               index = list.indexOf(object);
               if (index > 0 && !selectedList.contains(list.get(index - 1)))
               {
                  list.remove(index);
                  list.add(index - 1, object);
               }
            }
         }
         while (loop && oldIndex != index);
         // si loop == true (UPUP), on teste si le dernier (le plus grand) index d'élément de la boucle précédente
         // est égale au dernier index d'élément de la boucle courante,
         // si oui on arrête sinon on continue à monter les retardataires
      }
      else if (DOWN_ACTION.equals(actionCommand))
      {
         do
         {
            oldIndex = index;
            for (int i = selectedSize - 1; i >= 0; i--)
            {
               object = selectedList.get(i);
               index = list.indexOf(object);
               if (index < size - 1 && !selectedList.contains(list.get(index + 1)))
               {
                  list.remove(index);
                  list.add(index + 1, object);
               }
            }
         }
         while (loop && oldIndex != index);
      }
      // si loop == true (DOWNDOWN), on teste si le dernier (le plus petit) index d'élément de la boucle précédente
      // est égale au dernier index d'élément de la boucle courante,
      // si oui on arrête sinon on continue à descendre les retardataires

      getTable().setList(list);
      getTable().setSelectedList(selectedList);
   }

   /**
    * Retourne la valeur de la propriété buttonsPanel.
    * @return JPanel
    */
   protected JPanel getButtonsPanel ()
   {
      if (buttonsPanel == null)
      {
         buttonsPanel = new SpiPanel(BUTTONS_GRID_LAYOUT);
         // buttonsGridLayout est un poids mouche
         buttonsPanel.setOpaque(false);

         final JButton upButton = new JButton(null, upIcon);
         final JButton downButton = new JButton(null, downIcon);
         final JButton upUpButton = new JButton(null, upUpIcon);
         final JButton downDownButton = new JButton(null, downDownIcon);

         upButton.setActionCommand(UP_ACTION);
         downButton.setActionCommand(DOWN_ACTION);
         upUpButton.setActionCommand(UPUP_ACTION);
         downDownButton.setActionCommand(DOWNDOWN_ACTION);

         upButton.addActionListener(this);
         downButton.addActionListener(this);
         upUpButton.addActionListener(this);
         downDownButton.addActionListener(this);

         buttonsPanel.add(upButton);
         buttonsPanel.add(downButton);
         buttonsPanel.add(upUpButton);
         buttonsPanel.add(downDownButton);
      }
      return buttonsPanel;
   }

   /**
    * Retourne la valeur de la propriété list.
    * @return List
    * @see #setList
    */
   public List<TypeValue> getList ()
   {
      // clone
      return new ArrayList<TypeValue>(getTable().getList());
   }

   /**
    * Définit la valeur de la propriété list.
    * @param newList
    *           List
    * @see #getList
    */
   public void setList (final List<TypeValue> newList)
   {
      // clone
      getTable().setList(newList != null ? newList : new ArrayList<TypeValue>(0));
   }

   /**
    * Retourne la valeur de la propriété scrollPane.
    * @return JScrollPane
    */
   protected JScrollPane getScrollPane ()
   {
      if (scrollPane == null)
      {
         scrollPane = new JScrollPane(getTable());
      }

      return scrollPane;
   }

   /**
    * Retourne la valeur de la propriété table.
    * @return SpiTable
    */
   public SpiTable<TypeValue> getTable ()
   {
      if (table == null)
      {
         table = new SpiTable<TypeValue>();
         final TableColumn tableColumn = new TableColumn(0);
         tableColumn.setIdentifier("this");
         tableColumn.setHeaderValue("Eléments");
         // ajoute la colonne dans la table
         table.addColumn(tableColumn);
      }
      return table;
   }

   /**
    * Initialisation.
    */
   protected void prepare ()
   {
      setOpaque(false);
      final int margin = 10;
      final Insets insets = new Insets(margin, 0, 0, margin);
      final int center = GridBagConstraints.CENTER;
      final int both = GridBagConstraints.BOTH;
      final int horizontal = GridBagConstraints.HORIZONTAL;
      GridBagConstraints constraints;

      constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, center, both, insets, 0, 0);
      add(getScrollPane(), constraints);

      constraints = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, center, horizontal, insets, 0, 0);
      add(getButtonsPanel(), constraints);

      final SpiTable<TypeValue> v_table = getTable();
      v_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      // on désactive le tri sur header, puisque il y a un ordre métier
      v_table.setRowSorter(null);
   }

   @Override
   public void setEnabled (final boolean enabled)
   {
      // Surcharge de setEnabled pour les boutons.
      super.setEnabled(enabled);
      getButtonsPanel().setEnabled(enabled);
   }

   @Override
   public List<TypeValue> getValue ()
   {
      return getList();
   }

   @Override
   public void setValue (final List<TypeValue> p_value)
   {
      setList(p_value);
   }
}
