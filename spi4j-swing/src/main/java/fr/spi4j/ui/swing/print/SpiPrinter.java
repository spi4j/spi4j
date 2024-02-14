/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.print;

import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JTable;

import fr.spi4j.ui.swing.table.SpiBasicTable;

/**
 * Classe d'impression et d'export. <br/>
 * Cette classe s'utilise avec la méthode statique printOrExport. <br/>
 * @author MINARM
 */
public abstract class SpiPrinter
{
   // instance du fileChooser (pour export)
   private static JFileChooser fileChooser;

   /**
    * Constructeur.
    */
   protected SpiPrinter ()
   {
      super();
   }

   /**
    * Construit le titre à inclure dans l'impression/export.
    * @param component
    *           java.awt.Component
    * @return String
    */
   protected String buildTitle (final Component component)
   {
      final StringBuilder title = new StringBuilder();
      if (component != null && component.getName() != null)
      {
         title.append(component.getName());
      }
      // sur l'application pilote, ce calcule du titre selon JDialog, JFrame et JTabbedPane n'est pas fiable
      // Component current = component;
      // while (current != null)
      // {
      // if (current instanceof JTabbedPane)
      // {
      // final JTabbedPane tabbedPane = (JTabbedPane) current;
      // final String localTitle = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
      // if (localTitle != null && localTitle.length() != 0)
      // {
      // if (title.length() != 0)
      // {
      // title.insert(0, " - ");
      // }
      // title.insert(0, localTitle);
      // }
      // }
      // else if (current instanceof JFrame)
      // {
      // if (title.length() != 0)
      // {
      // title.insert(0, " - ");
      // }
      // title.insert(0, ((JFrame) current).getTitle());
      // }
      // else if (current instanceof JDialog)
      // {
      // if (title.length() != 0)
      // {
      // title.insert(0, " - ");
      // }
      // title.insert(0, ((JDialog) current).getTitle());
      // }
      // current = current.getParent();
      // }

      return title.length() != 0 ? title.toString() : null;
   }

   /**
    * Retourne true si la colonne est masquée, c'est-à-dire si maxWidth <= 0.
    * @param table
    *           SpiBasicTable
    * @param columnIndex
    *           int
    * @return boolean
    */
   protected boolean isColumnHidden (final SpiBasicTable table, final int columnIndex)
   {
      return table.getColumnModel().getColumn(columnIndex).getMaxWidth() <= 0;
   }

   /**
    * Renvoie le texte de l'entête de cette JTable pour la colonne spécifiée.
    * @return String
    * @param table
    *           SpiBasicTable
    * @param column
    *           int
    */
   protected String getColumnHeaderAt (final SpiBasicTable table, final int column)
   {
      Object headerValue = table.getColumnModel().getColumn(column).getHeaderValue();
      if (headerValue instanceof PrintableValue)
      {
         headerValue = ((PrintableValue) headerValue).getPrintableText();
      }
      final String text = headerValue != null ? headerValue.toString() : "";
      return replaceHtml(text);
   }

   /**
    * Renvoie le texte de la cellule de cette JTable à la ligne et la colonne spécifiée.
    * @return String
    * @param table
    *           SpiBasicTable
    * @param row
    *           int
    * @param column
    *           int
    */
   protected String getTextAt (final SpiBasicTable table, final int row, final int column)
   {
      final String text = table.getTextAt(row, column);
      // suppression de tags éventuellements présents (par ex. avec un MultiLineTableCellRenderer)
      return replaceHtml(text);
   }

   /**
    * Enlève les balises html d'un texte.
    * @param text
    *           String
    * @return String
    */
   private String replaceHtml (final String text)
   {
      if (text != null && text.startsWith("<html>"))
      {
         String result = text;
         result = result.replaceFirst("<html>", "");
         result = result.replaceFirst("</html>", ""); // mettre </html> à la fin n'est pas nécessaire, mais au cas où on l'enlève
         result = result.replaceFirst("<center>", "");
         result = result.replaceAll("<br/>", "\n");
         result = result.replaceAll("<br>", "\n");
         result = result.replaceAll("<nobr>", "");
         result = result.replaceAll("&nbsp;", " ");
         result = result.replaceAll("<b>", "").replaceAll("</b>", "");
         result = result.replaceFirst("<font color='#[0-9]*+'>", "");
         return result;
      }
      return text;
   }

   /**
    * Renvoie la valeur de la cellule de cette JTable à la ligne et la colonne spécifiée.
    * @return String
    * @param table
    *           SpiBasicTable
    * @param row
    *           int
    * @param column
    *           int
    */
   protected Object getValueAt (final SpiBasicTable table, final int row, final int column)
   {
      return table.getModel().getValueAt(row, column);
   }

   /**
    * Renvoie la boîte de dialogue swing de choix du fichier d'export. (Initialisée pour s'ouvrir sur le répertoire courant user.dir).
    * @return JFileChooser
    */
   public static synchronized JFileChooser getFileChooser ()
   {
      if (fileChooser == null)
      {
         final String currentDirectory = System.getProperty("user.dir");
         fileChooser = new JFileChooser(currentDirectory);
      }
      return fileChooser;
   }

   /**
    * Choix du fichier pour un export.
    * @return File
    * @param table
    *           JTable
    * @param extension
    *           String
    * @throws IOException
    *            Erreur disque
    */
   protected File chooseFile (final JTable table, final String extension) throws IOException
   {
      final JFileChooser myFileChooser = getFileChooser();

      final SpiExtensionFileFilter filter = new SpiExtensionFileFilter(extension);
      myFileChooser.addChoosableFileFilter(filter);

      String title = buildTitle(table);
      if (title != null)
      {
         // on remplace par des espaces les caractères interdits dans les noms de fichiers : \ / : * ? " < > |
         final String notAllowed = "\\/:*?\"<>|";
         final int notAllowedLength = notAllowed.length();
         for (int i = 0; i < notAllowedLength; i++)
         {
            title = title.replace(notAllowed.charAt(i), ' ');
         }
         myFileChooser.setSelectedFile(new File(title));
      }
      // l'extension sera ajoutée ci-dessous au nom du fichier

      try
      {
         final Component parent = table.getParent() != null ? table.getParent() : table;
         if (myFileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)
         {
            String fileName = myFileChooser.getSelectedFile().getCanonicalPath();
            if (!fileName.endsWith('.' + extension))
            {
               fileName += '.' + extension;
            }

            return new File(fileName);
         }
         else
         {
            return null;
         }
      }
      finally
      {
         myFileChooser.removeChoosableFileFilter(filter);
      }
   }

   /**
    * Retourne le fichier de sortie, par défaut on ouvre la boîte de dialogue de choix du fichier.
    * @return File
    * @param table
    *           JTable
    * @throws IOException
    *            Erreur disque
    */
   protected File getFile (final JTable table) throws IOException
   {
      return chooseFile(table, getFileExtension());
   }

   /**
    * Méthode abstraite assurant le polymorphisme des instances.
    * @param table
    *           SpiBasicTable
    * @param outputStream
    *           OutputStream
    * @throws IOException
    *            Erreur disque
    */
   public abstract void print (SpiBasicTable table, OutputStream outputStream) throws IOException;

   /**
    * Méthode abstraite : les instances doivent renvoyer leur nom.
    * @return String
    */
   public abstract String getName ();

   /**
    * Méthode abstraite : les instances doivent renvoyer l'extension du fichier exporté.
    * @return String
    */
   public abstract String getFileExtension ();

   /**
    * Méthode abstraite : les instances doivent renvoyer l'icône représentant le type.
    * @return Icon
    */
   public abstract Icon getIcon ();

   /**
    * Impression/export de la table.
    * @param table
    *           SpiBasicTable
    * @throws IOException
    *            Erreur disque
    */
   public void print (final SpiBasicTable table) throws IOException
   {
      final File file = getFile(table);
      if (file != null)
      {
         final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
         try
         {
            print(table, outputStream);
         }
         finally
         {
            outputStream.close();
         }

         showDocument(file);
      }
   }

   /**
    * Affiche le document. <br/>
    * Les implémentations peuvent surcharger cette méthode.
    * @param targetFile
    *           File
    * @throws IOException
    *            Erreur disque
    */
   protected void showDocument (final File targetFile) throws IOException
   {
      Desktop.getDesktop().open(targetFile);

      // on pourrait imprimer le fichier directement (par exemple CSV avec Excel) en supposant que Desktop.getDesktop().isDesktopSupported()
      // et Desktop.getDesktop().isSupported(Desktop.Action.PRINT) retournent true
      // ce qui est le cas en Windows XP SP2 (et Java 1.6)
      // Desktop.getDesktop().print(targetFile);
   }

   /**
    * Retourne le nom pour affichage.
    * @return String
    */
   @Override
   public String toString ()
   {
      return getName();
   }
}
