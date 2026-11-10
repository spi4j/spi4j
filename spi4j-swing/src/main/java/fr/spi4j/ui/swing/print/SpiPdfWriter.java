/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.print;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.TableColumnModel;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

import fr.spi4j.ui.swing.table.SpiBasicTable;

/**
 * Objet d'impression/export pour Pdf (portrait ou paysage).
 * @author MINARM
 */
public class SpiPdfWriter extends SpiPrinter
{
   /**
    * Icône Adobe Reader.
    */
   public static final ImageIcon ADOBE_READER_ICON = new ImageIcon(
            SpiPdfWriter.class.getResource("/icons/adobe acrobat.png"));

   private final boolean landscape;

   /**
    * Objet d'impression/export pour Pdf paysage.
    * @author MINARM
    */
   public static class LandscapePdfWriter extends SpiPdfWriter
   {
      /** Constructeur. */
      public LandscapePdfWriter ()
      {
         super(true);
      }
   }

   /**
    * Constructeur.
    */
   public SpiPdfWriter ()
   {
      this(false);
   }

   /**
    * Constructeur.
    * @param myLandscape
    *           boolean
    */
   public SpiPdfWriter (final boolean myLandscape)
   {
      super();
      this.landscape = myLandscape;
   }

   /**
    * Retourne un booléen selon que l'export est en paysage ou en portrait.
    * @return boolean
    */
   public boolean isLandscape ()
   {
      return landscape;
   }

   /**
    * Lance l'impression (Méthode abstraite assurant le polymorphisme des instances.).
    * @param table
    *           SpiBasicTable
    * @param out
    *           OutputStream
    * @throws IOException
    *            Erreur disque
    */
   @Override
   public void print (final SpiBasicTable table, final OutputStream out) throws IOException
   {
      writePdf(table, out);
   }

   /**
    * Méthode abstraite : les instances doivent renvoyer l'extension du fichier exporté.
    * @return String
    */
   @Override
   public String getFileExtension ()
   {
      return "pdf";
   }

   /**
    * Méthode abstraite : les instances doivent renvoyer l'icône représentant le type.
    * @return Icon
    */
   @Override
   public Icon getIcon ()
   {
      return ADOBE_READER_ICON;
   }

   /**
    * Méthode abstraite : les instances doivent renvoyer leur nom.
    * @return String
    */
   @Override
   public String getName ()
   {
      return landscape ? "Exporter au format PDF paysage" : "Exporter au format PDF";
   }

   /**
    * Ecrit le pdf.
    * @param table
    *           SpiBasicTable
    * @param out
    *           OutputStream
    * @throws IOException
    *            e
    */
   protected void writePdf (final SpiBasicTable table, final OutputStream out) throws IOException
   {
      try
      {
         // step 1: creation of a document-object
         final Rectangle pageSize = landscape ? PageSize.A4.rotate() : PageSize.A4;
         final Document document = new Document(pageSize, 50, 50, 50, 50);
         // step 2: we create a writer that listens to the document and directs a PDF-stream to out
         createWriter(table, document, out);

         // we add some meta information to the document, and we open it
         document.addAuthor(System.getProperty("user.name"));
         document.addCreator("Spi4j-Swing");
         final String title = buildTitle(table);
         if (title != null)
         {
            document.addTitle(title);
         }
         document.open();

         // ouvre la boîte de dialogue Imprimer de Adobe Reader
         // if (writer instanceof PdfWriter) {
         // ((PdfWriter) writer).addJavaScript("this.print(true);", false);
         // }

         final TableColumnModel columnModel = table.getColumnModel();
         int printedColumnCount = 0;
         for (int i = 0; i < columnModel.getColumnCount(); i++)
         {
            if (!isColumnHidden(table, i))
            {
               printedColumnCount++;
            }
         }
         // table
         final Table datatable = new Table(printedColumnCount);
         datatable.setCellsFitPage(true);
         datatable.setPadding(4);
         datatable.setSpacing(0);

         // headers
         renderHeaders(table, datatable);

         // data rows
         renderList(table, datatable);

         document.add(datatable);

         // we close the document
         document.close();
      }
      catch (final DocumentException e)
      {
         throw new IOException(e);
      }
   }

   /**
    * We create a writer that listens to the document and directs a PDF-stream to out
    * @param table
    *           SpiBasicTable
    * @param document
    *           Document
    * @param out
    *           OutputStream
    * @return DocWriter
    * @throws DocumentException
    *            e
    */
   protected DocWriter createWriter (final SpiBasicTable table, final Document document, final OutputStream out)
            throws DocumentException
   {
      final PdfWriter writer = PdfWriter.getInstance(document, out);
      // writer.setViewerPreferences(PdfWriter.PageLayoutTwoColumnLeft);

      // title
      final String title = buildTitle(table);
      if (title != null)
      {
         final HeaderFooter header = new HeaderFooter(new Phrase(title), false);
         header.setAlignment(Element.ALIGN_LEFT);
         header.setBorder(Rectangle.NO_BORDER);
         document.setHeader(header);
         document.addTitle(title);
      }

      // simple page numbers : x
      // HeaderFooter footer = new HeaderFooter(new Phrase(), true);
      // footer.setAlignment(Element.ALIGN_RIGHT);
      // footer.setBorder(Rectangle.TOP);
      // document.setFooter(footer);

      // add the event handler for advanced page numbers : x/y
      writer.setPageEvent(new AdvancedPageNumberEvents());

      return writer;
   }

   /**
    * Effectue le rendu des headers.
    * @param table
    *           SpiBasicTable
    * @param datatable
    *           Table
    * @throws BadElementException
    *            e
    */
   protected void renderHeaders (final SpiBasicTable table, final Table datatable) throws BadElementException
   {
      final int columnCount = table.getColumnCount();
      final TableColumnModel columnModel = table.getColumnModel();
      // size of columns
      float totalWidth = 0;
      for (int i = 0; i < columnCount; i++)
      {
         if (isColumnHidden(table, i))
         {
            continue;
         }
         totalWidth += columnModel.getColumn(i).getWidth();
      }
      final float[] headerwidths = new float[datatable.getColumns()];
      int printedColumn = 0;
      for (int i = 0; i < columnCount; i++)
      {
         if (isColumnHidden(table, i))
         {
            continue;
         }
         headerwidths[printedColumn] = 100f * columnModel.getColumn(i).getWidth() / totalWidth;
         printedColumn++;
      }
      datatable.setWidths(headerwidths);
      datatable.setWidth(100f);

      // table header
      final com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
      datatable.getDefaultCell().setBorderWidth(2);
      datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
      // datatable.setDefaultCellGrayFill(0.75f);

      for (int i = 0; i < columnCount; i++)
      {
         if (isColumnHidden(table, i))
         {
            continue;
         }
         final String text = getColumnHeaderAt(table, i);
         datatable.addCell(new Phrase(text, font));
      }
      // end of the table header
      datatable.endHeaders();
   }

   /**
    * Effectue le rendu de la liste.
    * @param table
    *           SpiBasicTable
    * @param datatable
    *           Table
    * @throws BadElementException
    *            e
    */
   protected void renderList (final SpiBasicTable table, final Table datatable) throws BadElementException
   {
      final int columnCount = table.getColumnCount();
      final int rowCount = table.getRowCount();
      // data rows
      final Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
      datatable.getDefaultCell().setBorderWidth(1);
      datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
      // datatable.setDefaultCellGrayFill(0);
      Object value;
      String text;
      int horizontalAlignment;
      for (int k = 0; k < rowCount; k++)
      {
         for (int i = 0; i < columnCount; i++)
         {
            if (isColumnHidden(table, i))
            {
               continue;
            }
            value = getValueAt(table, k, i);
            if (value instanceof Number || value instanceof Date)
            {
               horizontalAlignment = Element.ALIGN_RIGHT;
            }
            else if (value instanceof Boolean)
            {
               horizontalAlignment = Element.ALIGN_CENTER;
            }
            else
            {
               horizontalAlignment = Element.ALIGN_LEFT;
            }
            datatable.getDefaultCell().setHorizontalAlignment(horizontalAlignment);
            text = getTextAt(table, k, i);
            datatable.addCell(new Phrase(8, text != null ? text : "", font));
         }
      }
   }
}
