/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.print;

import java.io.OutputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.field.RtfPageNumber;
import com.lowagie.text.rtf.field.RtfTotalPageNumber;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;

import fr.spi4j.ui.swing.table.SpiBasicTable;

/**
 * Objet d'impression/export pour Rtf (portrait ou paysage).
 * @author MINARM
 */
public class SpiRtfWriter extends SpiPdfWriter
{
   /**
    * Icône Word.
    */
   public static final ImageIcon MSWORD_ICON = new ImageIcon(SpiRtfWriter.class.getResource("/icons/ms word.png"));

   /**
    * Objet d'impression/export pour Rtf paysage.
    * @author MINARM
    */
   public static class LandscapeRtfWriter extends SpiRtfWriter
   {
      /** Constructeur. */
      public LandscapeRtfWriter ()
      {
         super(true);
      }
   }

   /**
    * Constructeur.
    */
   public SpiRtfWriter ()
   {
      this(false);
   }

   /**
    * Constructeur avec booléen selon que format Portrait (false) ou Paysage (true).
    * @param landscape
    *           boolean
    */
   public SpiRtfWriter (final boolean landscape)
   {
      super(landscape);
   }

   /**
    * Méthode abstraite : les instances doivent renvoyer l'extension du fichier exporté.
    * @return String
    */
   @Override
   public String getFileExtension ()
   {
      return "rtf";
   }

   /**
    * Implémentation de méthode abstraite : renvoye l'icône représentant le type.
    * @return Icon
    */
   @Override
   public Icon getIcon ()
   {
      return MSWORD_ICON;
   }

   /**
    * Implémentation de méthode abstraite : renvoie le nom.
    * @return String
    */
   @Override
   public String getName ()
   {
      return isLandscape() ? "Exporter en word paysage (format RTF)" : "Exporter en word (format RTF)";
   }

   /**
    * We create a writer that listens to the document and directs a RTF-stream to out
    * @param table
    *           SpiBasicTable
    * @param document
    *           Document
    * @param out
    *           OutputStream
    * @return DocWriter
    */
   @Override
   protected DocWriter createWriter (final SpiBasicTable table, final Document document, final OutputStream out)
   {
      final RtfWriter2 writer = RtfWriter2.getInstance(document, out);

      // title
      final String title = buildTitle(table);
      if (title != null)
      {
         final HeaderFooter header = new RtfHeaderFooter(new Paragraph(title));
         header.setAlignment(Element.ALIGN_LEFT);
         header.setBorder(Rectangle.NO_BORDER);
         document.setHeader(header);
         document.addTitle(title);
      }

      // advanced page numbers : x/y
      final Paragraph footerParagraph = new Paragraph();
      final Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.NORMAL);
      footerParagraph.add(new RtfPageNumber(font));
      footerParagraph.add(new Phrase(" / ", font));
      footerParagraph.add(new RtfTotalPageNumber(font));
      footerParagraph.setAlignment(Element.ALIGN_CENTER);
      final HeaderFooter footer = new RtfHeaderFooter(footerParagraph);
      footer.setBorder(Rectangle.TOP);
      document.setFooter(footer);

      return writer;
   }

   // width rtf plus nécessaire
   // /**
   // * Effectue le rendu des headers.
   // * @param table JTable
   // * @param datatable Table
   // * @throws BadElementException
   // */
   // @Override protected void renderHeaders(JTable table, Table datatable) throws BadElementException {
   // super.renderHeaders(table, datatable);
   // datatable.setWidth(90f); // 90 au lieu de 100 pour avoir une marge dans le RTF
   // }
}
