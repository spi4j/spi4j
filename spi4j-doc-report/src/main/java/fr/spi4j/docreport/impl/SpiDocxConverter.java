/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.docreport.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.spi4j.docreport.DocConverter;

/**
 * Convertisseur de documents DocX (MS Word) en PDF ou HTML.
 * @author MINARM
 */
public class SpiDocxConverter implements DocConverter
{
   @Override
   public void convertToPdf (final InputStream p_input, final OutputStream p_output) throws IOException
   {
      // 1) Load docx with POI XWPFDocument
      final XWPFDocument v_document = new XWPFDocument(p_input);

      // 2) Convert POI XWPFDocument to PDF with iText
      final PdfOptions v_options = PdfOptions.getDefault(); // PDFViaITextOptions.create().fontEncoding( "windows-1250" );
      PdfConverter.getInstance().convert(v_document, p_output, v_options);
   }

   @Override
   public void convertToHtml (final InputStream p_input, final OutputStream p_output) throws IOException
   {
      // 1) Load docx with POI XWPFDocument
      final XWPFDocument v_document = new XWPFDocument(p_input);

      // 2) Convert POI XWPFDocument to XHTML with iText
      XHTMLConverter.getInstance().convert(v_document, p_output, null);
   }
}
