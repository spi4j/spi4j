/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.print;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;

import fr.spi4j.ui.swing.table.SpiBasicTable;

/**
 * Printer Java.
 * @author MINARM
 */
public class SpiJavaPrinter extends SpiPrinter
{
   /**
    * Icône Imprimante.
    */
   public static final ImageIcon PRINT_ICON = new ImageIcon(SpiJavaPrinter.class.getResource("/icons/Print16.gif"));

   /**
    * Constructeur.
    */
   public SpiJavaPrinter ()
   {
      super();
   }

   /** {@inheritDoc} */
   @Override
   public void print (final SpiBasicTable table, final OutputStream out)
   {
      // rien ici
   }

   /** {@inheritDoc} */
   @Override
   public File getFile (final JTable table) throws IOException
   {
      // impression directe, pas de fichier
      // (Remarque : le code pour exporter et / ou imprimer en pdf est disponible, mais cela nécessiterait de déployer la dépendance iText sur le poste client)
      try
      {
         table.print();
      }
      catch (final PrinterException e)
      {
         throw new IOException(e);
      }
      return null;
   }

   /** {@inheritDoc} */
   @Override
   public String getName ()
   {
      return "Imprimer";
   }

   /** {@inheritDoc} */
   @Override
   public String getFileExtension ()
   {
      return null;
   }

   /** {@inheritDoc} */
   @Override
   public Icon getIcon ()
   {
      return PRINT_ICON;
   }
}
