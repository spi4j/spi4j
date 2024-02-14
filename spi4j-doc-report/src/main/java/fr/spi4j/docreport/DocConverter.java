/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.docreport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface des convertisseurs des documents (soit ODT, soit DOCX)
 * @author MINARM
 */
public interface DocConverter
{
   /**
    * Convertit le document en entrée vers un document PDF.
    * @param p_input
    *           Flux d'entrée
    * @param p_output
    *           Flux de sortie
    * @throws IOException
    *            e
    */
   void convertToPdf (final InputStream p_input, final OutputStream p_output) throws IOException;

   /**
    * Convertit le document en entrée vers un document HTML (mise en page non garantie).
    * @param p_input
    *           Flux d'entrée
    * @param p_output
    *           Flux de sortie
    * @throws IOException
    *            e
    */
   void convertToHtml (final InputStream p_input, final OutputStream p_output) throws IOException;
}
