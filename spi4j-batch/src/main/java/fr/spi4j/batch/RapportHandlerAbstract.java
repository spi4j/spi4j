/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Aurelien
 */
public abstract class RapportHandlerAbstract {

   private BufferedWriter bufferedWriter;
   private FileWriter fileWriter;
   private String file;
   private StringBuilder content;

   public RapportHandlerAbstract(String directory, String rapportFileName) {

      this.file = directory + File.separator + rapportFileName;
      try {
         this.fileWriter = new FileWriter(this.file);
         this.bufferedWriter = new BufferedWriter(this.fileWriter);
      }
      catch (IOException e) {
         throw new RuntimeException("Impossible de créer le fichier de rapport {}", e);
      }

      this.content = new StringBuilder();
   }

   public void write(String ligne) {
      if (null != this.bufferedWriter) {
         try {
            this.bufferedWriter.write(ligne);
            this.bufferedWriter.newLine();
         }
         catch (IOException e) {
            throw new RuntimeException("Impossible d'écrire dans le fichier de rapport {}", e);
         }
      }

      this.content.append(ligne).append("\n");
   }

   public void close() {
      if (null != this.bufferedWriter) {
         try {
            this.bufferedWriter.close();
         }
         catch (IOException e) {
            throw new RuntimeException("Impossible de fermer le fichier  de rapport {}", e);
         }
      }

      if (null != this.fileWriter) {
         try {
            this.fileWriter.close();
         }
         catch (IOException e) {
            throw new RuntimeException("Impossible de fermer le fichier  de rapport {}", e);
         }
      }

   }

   public String read() {

      if (null != this.content) {
         return this.content.toString();
      }

      return null;
   }

}
