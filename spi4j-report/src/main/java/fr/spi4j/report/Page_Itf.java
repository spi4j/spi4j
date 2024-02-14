/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report;

import java.io.OutputStream;

/**
 * Interface de création/gestion d'une page de l'édition.
 * @author MINARM
 */
public interface Page_Itf
{
   /**
    * Permet la numérotation des pages PDF.
    * @param p_firstPageNumber
    *           Le numéro de la première page.
    * @param p_totalPagesNumber
    *           Le total des pages du document.
    * @return Le nombre de pages.
    */
   int addPageNumbers (int p_firstPageNumber, int p_totalPagesNumber);

   /**
    * Permet l'édition de la page PDF : le résultat s'obtient ensuite avec get_tab_byte().
    */
   void doReport ();

   /**
    * Permet l'édition de la page PDF : alternative à doReport pour écrire directement dans un flux sans stocker en mémoire.
    * @param p_outputStream
    *           OutputStream
    */
   void writeReport (OutputStream p_outputStream);

   /**
    * Retourne le nombre de page PDF dans cette page (si si ;-).
    * @return int
    */
   int getNumberOfPages ();

   /**
    * Retourne le flux binaire du PDF.
    * @return Le flux binaire du PDF
    */
   byte[] get_tab_byte ();

}
