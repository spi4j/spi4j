package fr.spi4j.docreport;

/**
 * Enumération des types de document courants, permettant d'obtenir le mime-type (ou ContentType en http).
 * @author MINARM
 */
public enum DocType
{
   /**
    * OpenDocument Text (format de LibreOffice ou d'OpenOffice).
    */
   ODT("application/vnd.oasis.opendocument.text"),

   /**
    * OpenDocument Spreadsheet (format de LibreOffice ou d'OpenOffice).
    */
   ODS("application/vnd.oasis.opendocument.spreadsheet"),

   /**
    * Docx (format de MS Word).
    */
   DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),

   /**
    * Xlsx (format de MS Excel).
    */
   XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

   /**
    * PDF (format d'Adobe Reader par exemple).
    */
   PDF("application/pdf"),

   /**
    * Fichier html.
    */
   HTML("text/html");

   private final String _mimeType;

   /**
    * Constructeur.
    * @param p_mimeType
    *           Type mime
    */
   private DocType (final String p_mimeType)
   {
      this._mimeType = p_mimeType;
   }

   /**
    * @return Type mime. <br/>
    *         Par exemple, pour fournir un document PDF via http, il est possible d'indiquer son type : <code>httpResponse.setContentType(DocType.PDF.getMimeType())</code>
    */
   public String getMimeType ()
   {
      return _mimeType;
   }
}
