/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report;

import com.lowagie.text.pdf.PdfWriter;

/**
 * Enumération des permissions d'un document PDF.
 * @author MINARM
 */
public enum Permission_Enum
{

   /**
    * Permission pour :<br>
    * - copier le contenu <br>
    * - copier le contenu pour accessibilité<br>
    * - ajouter des commentaires<br>
    * - remplir des champs de formulaire
    */
   ALLOW_COPY(PdfWriter.ALLOW_COPY,
            "Permission pour copier le contenu, ajouter des commentaires, remplir des champs de formulaire"),

   /**
    * Permission de faire une "impression assemblée".
    */
   ALLOW_ASSEMBLY(PdfWriter.ALLOW_ASSEMBLY, "Permission de faire une impression assemblée"),

   /**
    * Permission de faire une "impression brouillon".
    */
   ALLOW_DEGRADED_PRINTING(PdfWriter.ALLOW_DEGRADED_PRINTING, "Permission de faire une impression brouillon"),

   /**
    * Permission de remplir les formulaires.
    */
   ALLOW_FILL_IN(PdfWriter.ALLOW_FILL_IN, "Permission de remplir les formulaires"),

   /**
    * Permission d'écrire des annotations (commentaires et notes)
    */
   ALLOW_MODIFY_ANNOTATIONS(PdfWriter.ALLOW_MODIFY_ANNOTATIONS,
            "Permission d'écrire des annotations (commentaires et notes)"),

   /**
    * Permission de modifier le contenu du PDF.
    */
   ALLOW_MODIFY_CONTENTS(PdfWriter.ALLOW_MODIFY_CONTENTS, "Permission de modifier le contenu du PDF"),

   /**
    * Permission d'imprimer.
    */
   ALLOW_PRINTING(PdfWriter.ALLOW_PRINTING, "Permission d'imprimer"),

   /**
    * Permission de faire une copie d'écran.
    */
   ALLOW_SCREENREADERS(PdfWriter.ALLOW_SCREENREADERS, "Permission de faire une copie d'écran");

   private final int _pdfPermission;

   private final String _libellePermission;

   /**
    * Constructeur.
    * @param p_pdfPermission
    *           .
    * @param p_libellePermission
    *           Libellé
    */
   Permission_Enum (final int p_pdfPermission, final String p_libellePermission)
   {
      _pdfPermission = p_pdfPermission;
      _libellePermission = p_libellePermission;
   }

   /**
    * @return la valeur de la permission au sens d'IText.
    */
   public int getPdfPermission ()
   {
      return _pdfPermission;
   }

   @Override
   public String toString ()
   {
      return _libellePermission + " - " + super.toString();
   }
}
