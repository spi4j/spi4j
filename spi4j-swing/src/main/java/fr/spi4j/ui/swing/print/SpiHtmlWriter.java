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
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;

import fr.spi4j.ui.swing.table.SpiBasicTable;

/**
 * Writer HTML.
 * @author MINARM
 */
public class SpiHtmlWriter extends SpiPrinter
{
   /**
    * Icône MS IE.
    */
   public static final ImageIcon MSIE_ICON = new ImageIcon(SpiCsvWriter.class.getResource("/icons/ms ie.png"));

   /**
    * Constructeur.
    */
   public SpiHtmlWriter ()
   {
      super();
   }

   /** {@inheritDoc} */
   @Override
   public void print (final SpiBasicTable table, final OutputStream out) throws IOException
   {
      writeHtml(table, out, false);
   }

   /** {@inheritDoc} */
   @Override
   public String getName ()
   {
      return "Exporter au format HTML";
   }

   /** {@inheritDoc} */
   @Override
   public String getFileExtension ()
   {
      return "html";
   }

   /** {@inheritDoc} */
   @Override
   public Icon getIcon ()
   {
      return MSIE_ICON;
   }

   /**
    * Encode un texte au format html.
    * @return String
    * @param text
    *           String
    */
   protected String formatHtml (final String text)
   {
      return SpiHtmlEncoder.encodeString(text);
   }

   /**
    * Exporte une JTable dans un fichier au format html.
    * @param table
    *           SpiBasicTable
    * @param outputStream
    *           OutputStream
    * @param isSelection
    *           boolean
    * @throws IOException
    *            Erreur disque
    */
   protected void writeHtml (final SpiBasicTable table, final OutputStream outputStream, final boolean isSelection)
            throws IOException
   {
      final int rowCount = table.getRowCount();
      final int columnCount = table.getColumnCount();
      final Writer out = new OutputStreamWriter(outputStream, "UTF-8");

      final String eol = isSelection ? "\n" : System.getProperty("line.separator");
      // eol = "\n" si sélection, "\r\n" sinon pour un fichier windows et "\n" pour un fichier unix

      out.write("<!-- Fichier genere par ");
      out.write(System.getProperty("user.name"));
      out.write(" le ");
      out.write(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()));
      out.write(" -->");
      out.write(eol);

      out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
      out.write("<html>");
      out.write(eol);
      out.write("<head>");
      out.write(eol);
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
      out.write(eol);
      out.write("<title>");
      final String title = buildTitle(table);
      if (title != null)
      {
         out.write(title);
      }
      out.write("</title>");
      out.write(eol);
      out.write("<style type=\"text/css\"><!--.smallFont {  font-size: smaller}--></style>");
      out.write(eol);
      out.write("</head>");
      out.write(eol);
      out.write("<body>");
      out.write(eol);
      if (title != null)
      {
         out.write("<h3>");
         out.write(title);
         out.write("</h3>");
      }
      out.write(eol);

      out.write("<table width=\"100%\" border=\"1\" cellspacing=\"0\" bordercolor=\"#000000\" cellpadding=\"2\">");
      out.write(eol);
      out.write(eol);
      out.write("  <tr align=\"center\" class=\"smallFont\">");
      out.write(eol);

      // titres des colonnes
      for (int i = 0; i < columnCount; i++)
      {
         if (isColumnHidden(table, i))
         {
            continue;
         }
         out.write("    <th id=");
         out.write(String.valueOf(i));
         out.write("> ");
         String text = getColumnHeaderAt(table, i);
         text = formatHtml(text);
         out.write(text);
         out.write(" </th>");
         out.write(eol);
      }
      out.write("  </tr>");
      out.write(eol);

      // les données proprement dites (ligne par ligne puis colonne par colonne)
      final ListSelectionModel selectionModel = table.getSelectionModel();
      for (int k = 0; k < rowCount; k++)
      {
         if (isSelection && !selectionModel.isSelectedIndex(k))
         {
            continue;
         }

         out.write(eol);
         out.write("  <tr id=");
         out.write(String.valueOf(k));
         out.write(" class=\"smallFont\">");
         out.write(eol);
         for (int i = 0; i < columnCount; i++)
         {
            if (isColumnHidden(table, i))
            {
               continue;
            }
            final Object value = getValueAt(table, k, i);
            out.write("    <td");
            if (value instanceof Number || value instanceof Date)
            {
               out.write(" align=\"right\"");
            }
            else if (value instanceof Boolean)
            {
               out.write(" align=\"center\"");
            }
            out.write("> ");

            String text = getTextAt(table, k, i);
            text = formatHtml(text);
            out.write(text != null && text.trim().length() != 0 ? text : "&nbsp;"); 
            out.write(" </td>");
            out.write(eol);
         }
         out.write("  </tr>");
         out.write(eol);
      }

      out.write(eol);
      out.write("</table>");
      out.write(eol);
      out.write("</body>");
      out.write(eol);
      out.write("</html>");
      out.flush();
   }
}
