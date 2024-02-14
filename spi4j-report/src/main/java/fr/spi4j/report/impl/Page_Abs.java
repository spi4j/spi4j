/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import fr.spi4j.report.Page_Itf;
import fr.spi4j.report.ReportException;

/**
 * Classe abstraite permettant de gérer et retourner le flux binaire du pdf.
 * @author MINARM
 */
public abstract class Page_Abs implements Page_Itf
{
   private final ByteArrayOutputStream _byteArrayOutputStream;

   /**
    * Constructeur.
    */
   public Page_Abs ()
   {
      super();
      _byteArrayOutputStream = new ByteArrayOutputStream();
   }

   /**
    * Obtenir un byteArrayOuptutStream.
    * @return un byteArrayOuptutStream.
    */
   public ByteArrayOutputStream get_byteArrayOutputStream ()
   {
      return _byteArrayOutputStream;
   }

   @Override
   public byte[] get_tab_byte ()
   {
      return get_byteArrayOutputStream().toByteArray();
   }

   @Override
   public int addPageNumbers (final int p_firstPageNumber, final int p_totalPagesNumber)
   {
      final ByteArrayOutputStream v_byteArrayOutputStream = get_byteArrayOutputStream();
      if (v_byteArrayOutputStream.size() == 0)
      {
         throw new IllegalStateException("doReport() doit être appelée avant addPageNumbers");
      }

      int v_nbPageCour = 0;
      try
      {
         // Instancier un 'PdfReader' sur les donnees binaires
         final PdfReader v_pdfReader = new PdfReader(get_tab_byte());
         // réinitialisation du flux de sortie
         // (sinon les données du PDF avec les numéros de page s'ajoute à la suite des données du PDF sans les numéros)
         v_byteArrayOutputStream.reset();
         // Instancier et ouvrir le PdfStamper
         final PdfStamper v_pdfStamper = new PdfStamper(v_pdfReader, v_byteArrayOutputStream);
         try
         {
            // Creer un instance de font de base
            final BaseFont v_baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

            // Obtenir le nombre de pages total
            final int v_nbPageTot = v_pdfReader.getNumberOfPages();
            // Parcourir les pages du PDF
            while (v_nbPageCour < v_nbPageTot)
            {
               final PdfContentByte v_PdfContentByte = v_pdfStamper.getOverContent(v_nbPageCour + 1);
               v_PdfContentByte.beginText();
               v_PdfContentByte.setFontAndSize(v_baseFont, 12);
               v_PdfContentByte.showTextAligned(PdfContentByte.ALIGN_CENTER, "Page "
                        + (p_firstPageNumber + v_nbPageCour) + " / " + p_totalPagesNumber, 50, 10, 0);
               v_PdfContentByte.endText();
               v_nbPageCour++;
            }
         }
         finally
         {
            // Fermeture
            v_pdfStamper.close();
            v_pdfReader.close();
         }
      }
      catch (final IOException v_e)
      {
         throw new ReportException("Problème d'I/O avec p_firstPageNumber=" + p_firstPageNumber
                  + " - p_totalPagesNumber=" + p_totalPagesNumber, v_e);
      }
      catch (final DocumentException v_e)
      {
         throw new ReportException("Problème de document avec p_firstPageNumber=" + p_firstPageNumber
                  + " - p_totalPagesNumber=" + p_totalPagesNumber, v_e);
      }
      return p_firstPageNumber + v_nbPageCour;
   }

   @Override
   public void doReport ()
   {
      final ByteArrayOutputStream v_byteArrayOutputStream = get_byteArrayOutputStream();
      v_byteArrayOutputStream.reset();
      writeReport(v_byteArrayOutputStream);
   }

   @Override
   public abstract void writeReport (OutputStream p_outputStream);

   @Override
   public int getNumberOfPages ()
   {
      if (get_byteArrayOutputStream().size() == 0)
      {
         throw new IllegalStateException("doReport() doit être appelée avant getNumberOfPages");
      }
      try
      {
         // Instancier un 'PdfReader' sur les donnees binaires
         final PdfReader v_PdfReader = new PdfReader(get_tab_byte());
         try
         {
            return v_PdfReader.getNumberOfPages();
         }
         finally
         {
            v_PdfReader.close();
         }
      }
      catch (final IOException v_e)
      {
         throw new ReportException("Problème pour obtenir le nombre de page(s)", v_e);
      }
   }
}
