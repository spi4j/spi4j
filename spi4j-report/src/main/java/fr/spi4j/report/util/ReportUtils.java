/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import fr.spi4j.report.DataContainer;
import fr.spi4j.report.ReportException;

/**
 * Classe utilitaire pour la génération de rapports.
 * @author MINARM
 */
public final class ReportUtils
{

   /**
    * Constructeur privé.
    */
   private ReportUtils ()
   {
      super();
   }

   /**
    * Permet de convertir un fichier Image en tableau d'octets.
    * @param p_imageFileName
    *           Le nom du fichier de l'image.
    * @return L'image sous forme de tableau d'octets.
    */
   public static byte[] transformPictureToByte (final String p_imageFileName)
   {
      // Creation d'une InputStream
      final InputStream v_stream = ReportUtils.class.getResourceAsStream(p_imageFileName);
      // Bufferisation de l'image en InputStream
      final InputStream v_inputStream = new BufferedInputStream(v_stream);

      // Ajout de l'inputStream au Data
      try
      {
         // Le tableau de byte
         final byte[] v_tableauByte = new byte[v_inputStream.available()];
         // Lecture du data
         v_inputStream.read(v_tableauByte);

         // Retour des donnees contenant l'image sous forme de tableau de byte
         return v_tableauByte;
      }
      catch (final IOException v_e)
      {
         throw new ReportException("Problème d'I/O dans la conversion de l'image \"" + p_imageFileName + "\"", v_e);
      }
      finally
      {
         try
         {
            v_inputStream.close();
         }
         catch (final IOException v_e)
         {
            // ne peut pas arriver
            throw new RuntimeException(v_e);
         }
      }
   }

   /**
    * Retourne une liste de DataContainer contenant la liste des colonnes permettant de remplir le Data Set.
    * @param p_class
    *           La classe du bean.
    * @param p_nomCol
    *           Le nom de la colonne à ajouter dans le DataContainer.
    * @return La liste des colonnes.
    */
   public static List<DataContainer> createListDataContainer (final Class<?> p_class, final String... p_nomCol)
   {
      final List<DataContainer> v_tab_dataSetList = new ArrayList<>();

      // Parcourir les noms de colonnes spécifiés
      for (final String v_nomCol : p_nomCol)
      {
         try
         {
            // Recuperation du getter associé à 'v_nomCol'
            final Method v_Method = p_class.getMethod("get" + v_nomCol);

            // Obtenir le nom sans le get
            final String v_nameWihoutGet = v_Method.getName().substring("get".length());
            // Obtenir le type retournée
            final String v_typeReturn = v_Method.getReturnType().getSimpleName().toLowerCase();
            // Remplissage de la liste de DataContainer avec le nom et le type des attributs de la class
            v_tab_dataSetList.add(new DataContainer(v_nameWihoutGet, v_typeReturn));
         }
         catch (final Exception v_e)
         {
            throw new ReportException("Problème pour constituer le DataContainer avec la colonne '" + v_nomCol + "'",
                     v_e);
         }
      }
      return v_tab_dataSetList;
   }

   /**
    * Retourne une liste de DataContainer contenant la liste des colonnes permettant de remplir le Data Set.
    * @param p_class
    *           La classe du bean.
    * @return La liste des colonnes.
    */
   public static List<DataContainer> createListDataContainer (final Class<?> p_class)
   {
      final List<DataContainer> v_tab_dataSetList = new ArrayList<>();
      // Recuperation des getters
      final Method[] v_tab_methods = p_class.getMethods();
      for (final Method v_method : v_tab_methods)
      {
         if (v_method.getName().startsWith("get") && v_method.getParameterTypes().length == 0
                  && !v_method.getName().equals("getClass"))
         {
            // Remplissage de la liste de DataContainer avec le nom et le type des attributs de la class
            v_tab_dataSetList.add(new DataContainer(v_method.getName().substring("get".length()), v_method
                     .getReturnType().getSimpleName().toLowerCase()));
         }
      }

      return v_tab_dataSetList;
   }
}
