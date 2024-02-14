/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.docreport.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.odftoolkit.odfdom.doc.OdfTextDocument;

import fr.opensagres.odfdom.converter.pdf.PdfConverter;
import fr.opensagres.odfdom.converter.pdf.PdfOptions;
import fr.opensagres.odfdom.converter.xhtml.XHTMLConverter;
import fr.spi4j.docreport.DocConverter;

/**
 * Convertisseur de documents ODF (OpenOffice/LibreOffice) en PDF ou HTML.
 * @author MINARM
 */
public class SpiOdtConverter implements DocConverter
{
   @Override
   public void convertToPdf (final InputStream p_input, final OutputStream p_output) throws IOException
   {
      // 1) Load odt with ODFDOM
      OdfTextDocument v_document;
      try
      {
         v_document = OdfTextDocument.loadDocument(p_input);
      }
      catch (final Exception v_e)
      {
         throw new IOException(v_e);
      }

      // 2) Convert ODFDOM OdfTextDocument to PDF with iText
      final PdfOptions v_options = PdfOptions.getDefault(); // PDFViaITextOptions.create().fontEncoding( "windows-1250" );
      PdfConverter.getInstance().convert(v_document, p_output, v_options);
   }

   @Override
   public void convertToHtml (final InputStream p_input, final OutputStream p_output) throws IOException
   {
      // 1) Load odt with ODFDOM
      OdfTextDocument v_document;
      try
      {
         v_document = OdfTextDocument.loadDocument(p_input);
      }
      catch (final Exception v_e)
      {
         throw new IOException(v_e);
      }

      // 2) Convert ODFDOM OdfTextDocument to HTML with iText
      XHTMLConverter.getInstance().convert(v_document, p_output, null);
   }
}
