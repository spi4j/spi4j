/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.print;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.swing.JTable;

import fr.spi4j.ui.swing.table.SpiBasicTable;

/**
 * Printer de table copiant les données dans le presse-papiers.
 * @author MINARM
 */
public class SpiClipboardPrinter extends SpiHtmlWriter
{
   /** {@inheritDoc} */
   @Override
   public void print (final SpiBasicTable table, final OutputStream out)
   {
      copySelectionToClipboard(table);
   }

   /** {@inheritDoc} */
   @Override
   public File getFile (final JTable table)
   {
      return null; // pas de fichier
   }

   /** {@inheritDoc} */
   @Override
   public String getName ()
   {
      return "Clipboard";
   }

   /**
    * Copie la sélection d'une table dans le presse-papiers (au format html pour Excel par exemple).
    * @param table
    *           JTable
    */
   protected void copySelectionToClipboard (final SpiBasicTable table)
   {
      if (table.getSelectionModel().isSelectionEmpty())
      {
         return;
      }

      final Toolkit toolkit = table.getToolkit();
      final Clipboard clipboard = toolkit.getSystemClipboard();
      final ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream(2048);
      try
      {
         writeHtml(table, byteArrayOut, true);
      }
      catch (final IOException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      try
      {
         final String charset = System.getProperty("file.encoding");
         final StringSelection contents = new StringSelection(byteArrayOut.toString(charset));
         clipboard.setContents(contents, contents);
      }
      catch (final UnsupportedEncodingException e)
      {
         throw new IllegalStateException(e);
      }
   }
}
