/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report;

import fr.spi4j.report.impl.DocumentPdf;
import fr.spi4j.report.impl.PageBIRT;
import fr.spi4j.report.impl.PageITEXT;
import fr.spi4j.report.impl.PageOrientation_Enum;

/**
 * Création/gestion d'une page de l'édition. Cette page s'appuie sur PageITEXT() ou PageBIRT().
 * @author MINARM.
 */
public final class ReportFactory
{

   /**
    * Constructeur privé (factory).
    */
   private ReportFactory ()
   {
      // ras
   }

   /**
    * Méthode de création d'un document Birt ou IText.
    * @return Le document créé.
    */
   public static Document_Itf createDocument ()
   {
      return new DocumentPdf();
   }

   /**
    * Méthode de création d'une page IText ou Birt selon l'extension du paramètre.
    * @param p_reportName
    *           Le nom du rapport.
    * @return La page créée.
    */
   public static PageDesign_Itf createPageBirt (final String p_reportName)
   {
      return createPageBirt(p_reportName, PageOrientation_Enum.PageOrientationAuto);
   }

   /**
    * Méthode de création d'une page IText ou Birt selon l'extension du paramètre.
    * @param p_reportName
    *           Le nom du rapport.
    * @param p_PageOrientation_Enum
    *           L'orientation de la page.
    * @return La page créée.
    */
   public static PageDesign_Itf createPageBirt (final String p_reportName,
            final PageOrientation_Enum p_PageOrientation_Enum)
   {
      if (!p_reportName.endsWith(".rptdesign"))
      {
         throw new IllegalArgumentException("Extension incorrecte (attendu : .rptdesign) : " + p_reportName);
      }
      return new PageBIRT(p_reportName, p_PageOrientation_Enum);
   }

   /**
    * Méthode de création d'une page IText ou Birt selon l'extension du paramètre.
    * @param p_reportName
    *           Le nom du rapport.
    * @param p_MasterPageName
    *           Le nom de la MasterPage à utiliser comme modèle.
    * @param p_PageOrientation_Enum
    *           L'orientation de la page.
    * @return La page créée.
    */
   public static PageDesign_Itf createPageBirt (final String p_reportName, final String p_MasterPageName,
            final PageOrientation_Enum p_PageOrientation_Enum)
   {
      if (!p_reportName.endsWith(".rptdesign"))
      {
         throw new IllegalArgumentException("Extension incorrecte (attendu : .rptdesign) : " + p_reportName);
      }
      return new PageBIRT(p_reportName, p_MasterPageName, p_PageOrientation_Enum);
   }

   /**
    * Méthode surchargée de création de page.
    * @return La page IText créée.
    */
   public static Page_Itf createPageItext ()
   {
      return new PageITEXT();
   }
}
