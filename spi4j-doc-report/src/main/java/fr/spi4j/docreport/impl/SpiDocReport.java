/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.docreport.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.io.internal.ByteArrayOutputStream;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.spi4j.docreport.DocConverter;
import fr.spi4j.docreport.DocReport;

/**
 * Classe simplifiant l'utilisation de XDocReport, pour un usage simple.
 * @author MINARM
 */
public class SpiDocReport implements DocReport
{
   private static final Logger c_log = LogManager.getLogger(SpiDocReport.class);

   private TemplateEngineKind _templateEngineKind = TemplateEngineKind.Freemarker;

   @Override
   public String getTemplateEngineName ()
   {
      return _templateEngineKind.name();
   }

   @Override
   public void setTemplateEngineName (final String p_templateEngineName)
   {
      _templateEngineKind = TemplateEngineKind.valueOf(p_templateEngineName);
   }

   @Override
   public void process (final String p_filePathInClassPath, final Map<String, Object> p_context,
            final OutputStream p_output) throws IOException
   {
      checkParameters(p_filePathInClassPath, p_context, p_output);

      c_log.info("Génération d'une édition pour le template " + p_filePathInClassPath);
      final long v_start = System.currentTimeMillis();
      try
      {
         // 1) Get or load DOCX/ODT file with template engine and cache it to the registry
         final IXDocReport v_report = getXDocReport(p_filePathInClassPath);

         // 2) Generate report by merging Java model with the DOCX/ODT
         final OutputStream v_out = new BufferedOutputStream(p_output);
         try
         {
            v_report.process(p_context, v_out);
         }
         finally
         {
            v_out.close();
         }
      }
      catch (final XDocReportException v_e)
      {
         throw new IOException(v_e);
      }
      c_log.info("Génération effectuée d'une édition pour le template " + p_filePathInClassPath + " en "
               + (System.currentTimeMillis() - v_start) + " ms");
   }

   @Override
   public void processToPdf (final String p_filePathInClassPath, final Map<String, Object> p_context,
            final OutputStream p_output) throws IOException
   {
      // baos optimisé de xdocreport
      final ByteArrayOutputStream v_output = new ByteArrayOutputStream();
      process(p_filePathInClassPath, p_context, v_output);

      c_log.info("Conversion en PDF d'une édition pour le template " + p_filePathInClassPath);
      final long v_start = System.currentTimeMillis();
      final ByteArrayInputStream v_input = new ByteArrayInputStream(v_output.toByteArray());

      final DocConverter v_converter = getDocConverterForFile(p_filePathInClassPath);
      v_converter.convertToPdf(v_input, p_output);
      c_log.info("Conversion en PDF effectuée d'une édition pour le template " + p_filePathInClassPath + " en "
               + (System.currentTimeMillis() - v_start) + " ms");
   }

   @Override
   public void processToHtml (final String p_filePathInClassPath, final Map<String, Object> p_context,
            final OutputStream p_output) throws IOException
   {
      // baos optimisé de xdocreport
      final ByteArrayOutputStream v_output = new ByteArrayOutputStream();
      process(p_filePathInClassPath, p_context, v_output);

      c_log.info("Conversion en HTML d'une édition pour le template " + p_filePathInClassPath);
      final long v_start = System.currentTimeMillis();
      final ByteArrayInputStream v_input = new ByteArrayInputStream(v_output.toByteArray());

      final DocConverter v_converter = getDocConverterForFile(p_filePathInClassPath);
      v_converter.convertToHtml(v_input, p_output);
      c_log.info("Conversion en HTML effectuée d'une édition pour le template " + p_filePathInClassPath + " en "
               + (System.currentTimeMillis() - v_start) + " ms");
   }

   @Override
   public void loadTemplate (final InputStream p_inputStream, final String p_templateId) throws IOException
   {
      c_log.info("Chargement et mise en cache du template " + p_templateId);
      try
      {
         final String v_reportId = normalizeReportId(p_templateId);
         XDocReportRegistry.getRegistry().loadReport(p_inputStream, v_reportId, _templateEngineKind);
      }
      catch (final XDocReportException v_e)
      {
         throw new IOException(v_e);
      }
   }

   /**
    * Charge un template, ou le récupère depuis le cache s'il a déjà été chargé.
    * @param p_filePathInClassPath
    *           Chemin du template ODT ou DOCX dans le classpath (par exemple : /templates/courrier_annuaire.odt)
    * @return Instance du template.
    * @throws IOException
    *            e
    * @throws XDocReportException
    *            e
    */
   private IXDocReport getXDocReport (final String p_filePathInClassPath) throws IOException, XDocReportException
   {
      final String v_reportId = normalizeReportId(p_filePathInClassPath);
      IXDocReport v_report = XDocReportRegistry.getRegistry().getReport(v_reportId);
      if (v_report == null)
      {
         c_log.info("Chargement et mise en cache du template " + p_filePathInClassPath);
         final InputStream v_resourceAsStream = SpiDocReport.class.getResourceAsStream(p_filePathInClassPath);
         if (v_resourceAsStream == null)
         {
            throw new IllegalArgumentException("Le fichier du template n'a pas été trouvé dans les ressources : "
                     + p_filePathInClassPath);
         }
         final InputStream v_in = new BufferedInputStream(v_resourceAsStream);
         try
         {
            v_report = XDocReportRegistry.getRegistry().loadReport(v_in, v_reportId, _templateEngineKind);
         }
         finally
         {
            v_in.close();
         }
      }
      return v_report;
   }

   /**
    * Normalisation d'un reportId (pour enlever le '/' au début s'il y en a un).
    * @param p_filePathInClassPathOrTemplateId
    *           String
    * @return String, sans '/' au début
    */
   private String normalizeReportId (final String p_filePathInClassPathOrTemplateId)
   {
      String v_reportId = p_filePathInClassPathOrTemplateId;
      if (v_reportId.length() > 0 && v_reportId.charAt(0) == '/')
      {
         // si reportId commence par '/', Freemarker fait une exception "Template ... not found", donc on enlève ce '/'.
         v_reportId = v_reportId.substring(1);
      }
      return v_reportId;
   }

   @Override
   public DocConverter getDocConverterForFile (final String p_filePathInClassPath)
   {
      final String v_filePathLowerCase = p_filePathInClassPath.toLowerCase(Locale.FRENCH);
      if (v_filePathLowerCase.endsWith(".odt"))
      {
         return new SpiOdtConverter();
      }
      else if (v_filePathLowerCase.endsWith(".docx"))
      {
         return new SpiDocxConverter();
      }
      throw new IllegalArgumentException("Convertisseur non géré pour ce type de fichier : " + p_filePathInClassPath);
   }

   /**
    * Vérification des paramètres.
    * @param p_filePathInClassPath
    *           String
    * @param p_context
    *           Map
    * @param p_output
    *           OutputStream
    */
   private void checkParameters (final String p_filePathInClassPath, final Map<String, Object> p_context,
            final OutputStream p_output)
   {
      if (p_filePathInClassPath == null)
      {
         throw new IllegalArgumentException(
                  "Le paramètre p_filePathInClassPath ne doit pas être null (exemple : /templates/courrier_annuaire.odt)");
      }
      if (p_context == null)
      {
         throw new IllegalArgumentException(
                  "Le paramètre p_context ne doit pas être null et il doit contenir les valeurs nommées à mettre dans le document");
      }
      if (p_output == null)
      {
         throw new IllegalArgumentException(
                  "Le paramètre p_output ne doit pas être null : utiliser par exemple un FileOutputStream, un ServletOutputStream ou un ByteArrayOutputStream");
      }
   }
}
