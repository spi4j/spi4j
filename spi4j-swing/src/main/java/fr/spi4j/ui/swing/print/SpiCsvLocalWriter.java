/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.print;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.Icon;

import fr.spi4j.ui.swing.table.SpiBasicTable;

/**
 * Writer CSV localisé.
 * @author MINARM
 */
public class SpiCsvLocalWriter extends SpiCsvWriter
{
   /** Caractère de séparation dans les fichiers csv (local). */
   public static final char CSV_LOCAL_SEPARATOR = ';';

   /**
    * Constructeur.
    */
   public SpiCsvLocalWriter ()
   {
      super();
   }

   /** {@inheritDoc} */
   @Override
   public void print (final SpiBasicTable table, final OutputStream out) throws IOException
   {
      writeCsvLocal(table, out, CSV_LOCAL_SEPARATOR);
      // le séparateur des .csv (';' à la française));
   }

   /** {@inheritDoc} */
   @Override
   public String getName ()
   {
      return "Exporter en excel (format CSV)";
   }

   /** {@inheritDoc} */
   @Override
   public Icon getIcon ()
   {
      return MSEXCEL_ICON;
   }

   /**
    * Exporte une JTable dans un fichier au format csv local pour Menu,Ouvrir. (séparateur ';', formats nombres et dates locaux).
    * @param table
    *           SpiBasicTable
    * @param outputStream
    *           OutputStream
    * @param csvSeparator
    *           char
    * @throws IOException
    *            Erreur disque
    */
   protected void writeCsvLocal (final SpiBasicTable table, final OutputStream outputStream, final char csvSeparator)
            throws IOException
   {
      writeCsvLocal(table, outputStream, csvSeparator, null);
   }

   /**
    * Exporte une JTable dans un fichier au format csv local pour Menu,Ouvrir. (séparateur ';', formats nombres et dates locaux).
    * @param table
    *           SpiBasicTable
    * @param outputStream
    *           OutputStream
    * @param csvSeparator
    *           char
    * @param dateFormat
    *           DateFormat
    * @throws IOException
    *            Erreur disque
    */
   protected void writeCsvLocal (final SpiBasicTable table, final OutputStream outputStream, final char csvSeparator,
            final DateFormat dateFormat) throws IOException
   {
      // récupération des informations utiles
      final int columnCount = table.getColumnModel().getColumnCount();
      final int rowCount = table.getRowCount();
      // charset local
      final Writer out = new OutputStreamWriter(outputStream, "Cp1252");

      final String eol = System.getProperty("line.separator");

      // titres des colonnes
      writeCsvHeader(table, out, csvSeparator);

      // les données proprement dites (ligne par ligne puis colonne par colonne)
      String text;
      Object value;
      for (int k = 0; k < rowCount; k++)
      {
         for (int i = 0; i < columnCount; i++)
         {
            if (isColumnHidden(table, i))
            {
               continue;
            }
            value = getValueAt(table, k, i);
            if (dateFormat != null && value instanceof Date)
            {
               text = dateFormat.format(value);
            }
            else
            {
               text = getTextAt(table, k, i);
               text = formatCsv(text, csvSeparator);
               if (value instanceof Number && text != null)
               {
                  // en France, le caractère de séparation des milliers est pour Java un espace insécable (0xA0)
                  // et non un espace standard (0x20), mais Excel XP reconnaît l'espace standard dans les nombres
                  // mais ne reconnaît pas l'espace insécable et interprète alors les nombres comme du texte
                  text = text.replace('\u00A0', '\u0020');
               }
            }

            out.write(text);
            if (i < columnCount - 1)
            {
               out.write(csvSeparator);
            }
         }
         if (k < rowCount - 1)
         {
            out.write(eol);
         }
      }
      out.flush();
   }
}
