/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.docreport;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 * Classe pour utiliser les associations et l'imprimante par défaut du système.
 * @author MINARM
 */
public final class DesktopUtils
{
   /**
    * Pas d'instance.
    */
   private DesktopUtils ()
   {
      super();
   }

   /**
    * Imprime un fichier directement en utilisant l'imprimante par défaut du système et le logiciel adapté au fichier (Adobe Reader pour un fichier PDF par exemple). <br/>
    * Selon la configuration du système, cela peut ne pas fonctionner (par exemple, sur Linux en mode headless)
    * @param p_file
    *           File
    * @throws IOException
    *            e
    */
   public static void printFile (final File p_file) throws IOException
   {
      Desktop.getDesktop().print(p_file);
   }

   /**
    * Ouvre un fichier localement en utilisant le logiciel adapté au fichier (MS Word pour un fichier DOCX par exemple). <br/>
    * Selon la configuration du système, cela peut ne pas fonctionner (par exemple, sur Linux en mode headless)
    * @param p_file
    *           File
    * @throws IOException
    *            e
    */
   public static void openFile (final File p_file) throws IOException
   {
      Desktop.getDesktop().open(p_file);
   }
}
