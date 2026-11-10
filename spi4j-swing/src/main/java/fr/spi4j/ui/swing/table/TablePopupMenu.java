/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.ui.swing.SpiButton;
import fr.spi4j.ui.swing.SpiMenuItem;
import fr.spi4j.ui.swing.print.SpiCsvLocalWriter;
import fr.spi4j.ui.swing.print.SpiHtmlWriter;
import fr.spi4j.ui.swing.print.SpiJavaPrinter;
import fr.spi4j.ui.swing.print.SpiPdfWriter;
import fr.spi4j.ui.swing.print.SpiPrinter;
import fr.spi4j.ui.swing.print.SpiRtfWriter;

/**
 * Popup menu des tables.
 * @author DSCID
 */
public class TablePopupMenu extends JPopupMenu
{
   private static final long serialVersionUID = 1L;

   private static final List<SpiPrinter> PRINTERS = new ArrayList<SpiPrinter>();

   /**
    * Retourne la liste des objets d'export / impression.
    * @return List
    */
   public static List<SpiPrinter> getPrinters ()
   {
      if (!PRINTERS.isEmpty())
      {
         return PRINTERS;
      }
      // ne sont pas inclus dans cette liste le printer "Clipboard" qui est utilisé directement avec Ctrl+C, les printers PDF/RTF paysages et le printer CSV US
      final List<SpiPrinter> printers = new ArrayList<SpiPrinter>();
      printers.add(new SpiCsvLocalWriter());
      try
      {
         Class.forName("com.lowagie.text.Document");
         printers.add(new SpiPdfWriter());
      }
      catch (final ClassNotFoundException e)
      {
         // l'export PDF ne sera pas disponible dans cette application
         LogManager.getLogger(TablePopupMenu.class).debug("Export PDF non disponible sans iText");
      }
      try
      {
         Class.forName("com.lowagie.text.rtf.RtfWriter2");
         printers.add(new SpiRtfWriter());
      }
      catch (final ClassNotFoundException e)
      {
         // l'export RTF ne sera pas disponible dans cette application
         LogManager.getLogger(TablePopupMenu.class).debug("Export RTF non disponible sans iText-RTF");
      }
      printers.add(new SpiHtmlWriter());
      printers.add(new SpiJavaPrinter());

      PRINTERS.addAll(printers);
      return PRINTERS;
   }

   /**
    * Initialise le menu contextuel d'une SpiTable pour afficher les impressions, mais sans laisser la sélection des colonnes.
    * @param table
    *           SpiTable_Abs
    */
   public static void initPopupForPrintingOnly (final SpiTable_Abs<?> table)
   {
      // on enlève le menu contextuel complet avec les impressions et la sélection des colonnes
      table.disablePopupMenu();
      // on remet le menu contextuel uniquement pour les impressions
      table.addMouseListener(createMouseListenerForPrintingOnly());
   }

   /**
    * Crée un MouseListener à ajouter sur une SpiTable pour afficher le menu contextuel des impressions (sans les sélections de colonnes).
    * @return MouseListener
    */
   private static MouseListener createMouseListenerForPrintingOnly ()
   {
      return new MouseAdapter()
      {
         @Override
         public void mouseReleased (final MouseEvent event)
         {
            if (event.isPopupTrigger() || SwingUtilities.isRightMouseButton(event)
                     && event.getComponent() instanceof SpiTable_Abs)
            {
               final SpiTable_Abs<?> table = (SpiTable_Abs<?>) event.getComponent();
               final TablePopupMenu popupMenu = new TablePopupMenu();
               popupMenu.initializePrintItems(table);
               popupMenu.show(table, event.getX(), event.getY());
            }
         }
      };
   }

   /**
    * Crée un MouseListener à ajouter sur une SpiTable pour afficher le menu contextuel complet.
    * @return MouseListener
    */
   static MouseListener createMouseListenerForTablePopupMenu ()
   {
      return new MouseAdapter()
      {
         @Override
         public void mouseReleased (final MouseEvent event)
         {
            if (event.isPopupTrigger() || SwingUtilities.isRightMouseButton(event)
                     && event.getComponent() instanceof SpiTable_Abs)
            {
               final SpiTable_Abs<?> table = (SpiTable_Abs<?>) event.getComponent();
               final TablePopupMenu popupMenu = new TablePopupMenu();
               popupMenu.initializeItems(table);
               popupMenu.show(table, event.getX(), event.getY());
            }
         }
      };
   }

   /**
    * Configure le scrollPane de la table avec le bouton pour afficher / masquer des colonnes (en haut et à droite du tableau)
    * @param table
    *           SpiTable_Abs
    */
   static void configureEnclosingScrollPane (final SpiTable_Abs<?> table)
   {
      final Container parent = table.getParent();
      if (parent instanceof JViewport && parent.getParent() instanceof JScrollPane)
      {
         final JScrollPane scrollPane = (JScrollPane) parent.getParent();
         final SpiButton button = new SpiButton();
         scrollPane.setCorner(ScrollPaneConstants.UPPER_TRAILING_CORNER, button);

         button.setToolTipText("Colonnes");
         button.setIcon(ColumnIcon.INSTANCE);
         button.setBorder(null);
         button.setContentAreaFilled(false);
         button.setFocusable(false);
         button.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed (final ActionEvent event)
            {
               final TablePopupMenu popupMenu = new TablePopupMenu();
               TablePopupMenu.initializeColumnItems(table, popupMenu);
               final Dimension buttonSize = button.getSize();
               final int xPos = buttonSize.width - popupMenu.getPreferredSize().width;
               final int yPos = buttonSize.height;
               popupMenu.show(button, xPos, yPos);
            }
         });
      }
   }

   /**
    * Action inverse de configureEnclosingScrollPane.
    * @param table
    *           SpiTable_Abs
    */
   static void unconfigureEnclosingScrollPane (final SpiTable_Abs<?> table)
   {
      final Container parent = table.getParent();
      if (parent instanceof JViewport && parent.getParent() instanceof JScrollPane)
      {
         final JScrollPane scrollPane = (JScrollPane) parent.getParent();
         scrollPane.setCorner(ScrollPaneConstants.UPPER_TRAILING_CORNER, null);
      }
   }

   /**
    * Initialise les éléments de ce menu pour exporter / imprimer et pour afficher / masquer les colonnes dans un sous-menu.
    * @param table
    *           SpiTable_Abs
    */
   void initializeItems (final SpiTable_Abs<?> table)
   {
      // affiche les exports dans le menu
      initializePrintItems(table);

      // affiche le sous-menu "Colonnes"
      final JMenu subMenu = new JMenu("Colonnes");
      addSeparator();
      add(subMenu);
      initializeColumnItems(table, subMenu);
   }

   /**
    * @param table
    *           SpiTable_Abs
    */
   private void initializePrintItems (final SpiTable_Abs<?> table)
   {
      final List<SpiPrinter> printers = getPrinters();
      for (final SpiPrinter printer : printers)
      {
         final SpiMenuItem menuItem = new SpiMenuItem(printer.getName(), printer.getIcon());
         add(menuItem);
         menuItem.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed (final ActionEvent event)
            {
               try
               {
                  printer.print(table);
               }
               catch (final IOException e)
               {
                  throw new RuntimeException(e.getMessage(), e);
               }
            }
         });
      }
   }

   /**
    * @param table
    *           SpiTable_Abs
    * @param menu
    *           Menu où ajouter les éléments (JPopupMenu ou JMenu)
    */
   private static void initializeColumnItems (final SpiTable_Abs<?> table, final JComponent menu)
   {
      final List<TableColumn> columns = table.getColumns();
      for (final TableColumn column : columns)
      {
         final String headerValue = String.valueOf(column.getHeaderValue());
         // selon mantis 1104 on n'affiche pas dans la liste les colonnes qui n'ont pas d'entête, ce qui est logique
         if (!headerValue.isEmpty())
         {
            final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(headerValue);
            final boolean columnHidden = table.isColumnHidden(column);
            if (!columnHidden)
            {
               menuItem.setSelected(true);
            }
            menu.add(menuItem);
            menuItem.addActionListener(new ActionListener()
            {
               @Override
               public void actionPerformed (final ActionEvent event)
               {
                  table.setColumnHidden(column, !columnHidden);
               }
            });
         }
      }
   }
}
