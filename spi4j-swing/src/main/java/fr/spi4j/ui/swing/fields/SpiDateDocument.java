/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Document swing interne pour la saisie d'une date.
 * @author MINARM
 */
public class SpiDateDocument extends SpiTextDocument
{
   private static final long serialVersionUID = 1L;

   // Les instances de formats suivants sont partagées pour tous les champs de date.
   // Note : Ce n'est pas gênant même pour un DateFormat car les traitements Swing se font toujours sur un seul thread.

   // format de base et 1er format pour la validation
   private static SimpleDateFormat dateFormat;

   // format alternatif de validation (sans les '/')
   private static SimpleDateFormat alternateDateFormat;

   // 2ème format alternatif de validation (juste dd et MM)
   private static SimpleDateFormat alternateDateFormat2;

   // 3ème format alternatif de validation (juste dd)
   private static SimpleDateFormat alternateDateFormat3;

   // format d'affichage (avec 4 chiffres pour l'année)
   private static SimpleDateFormat displayDateFormat;

   /**
    * Constructeur.
    */
   public SpiDateDocument ()
   {
      super(10);
   }

   /**
    * Retourne la valeur de la propriété alternateDateFormat.
    * @return SimpleDateFormat
    */
   public static SimpleDateFormat getAlternateDateFormat ()
   {
      if (alternateDateFormat == null) 
      {
         // pour construire ce dateFormat on enlève les '/' et '.' du pattern de dateFormat
         // ie on peut saisir en France 251202 au lieu de 25/12/02 ou 25/12/2002

         final StringBuilder pattern = new StringBuilder(getDateFormat().toPattern());
         // note : il faut réévaluer pattern.length() chaque fois dans la condition
         // puisque la longueur diminue au fur et à mesure
         for (int i = 0; i < pattern.length(); i++)
         {
            if (!Character.isLetter(pattern.charAt(i)))
            {
               pattern.deleteCharAt(i);
            }
         }

         final SimpleDateFormat myAlternateDateFormat = new SimpleDateFormat(pattern.toString())
         {
            private static final long serialVersionUID = 1L;

            @Override
            public Date parse (final String text) throws ParseException
            {
               try
               {
                  return super.parse(text);
               }
               catch (final ParseException ex)
               {
                  final Calendar myCalendar = Calendar.getInstance();
                  final int year = myCalendar.get(Calendar.YEAR);
                  final int month = myCalendar.get(Calendar.MONTH);
                  try
                  {
                     myCalendar.setTime(alternateDateFormat2.parse(text));
                     myCalendar.set(Calendar.YEAR, year);
                     return myCalendar.getTime();
                  }
                  catch (final ParseException ex1)
                  {
                     try
                     {
                        myCalendar.setTime(alternateDateFormat3.parse(text));
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        return myCalendar.getTime();
                     }
                     catch (final ParseException ex2)
                     {
                        throw ex;
                     }
                  }
               }
            }
         };

         final SimpleDateFormat myAlternateDateFormat2 = new SimpleDateFormat(myAlternateDateFormat.toPattern()
                  .replaceAll("y", ""));
         final SimpleDateFormat myAlternateDateFormat3 = new SimpleDateFormat(myAlternateDateFormat2.toPattern()
                  .replaceAll("M", ""));
         // on n'accepte pas le 30/02 (qui serait alors le 02/03)
         myAlternateDateFormat.setLenient(false);
         myAlternateDateFormat2.setLenient(false);
         myAlternateDateFormat3.setLenient(false);

         alternateDateFormat = myAlternateDateFormat;
         alternateDateFormat2 = myAlternateDateFormat2;
         alternateDateFormat3 = myAlternateDateFormat3;
      }

      return alternateDateFormat;
   }

   /**
    * Retourne la valeur de la propriété dateFormat. <br/>
    * Ce dateFormat est le format court par défaut de java en fonction de la Locale par défaut (lenient est false).
    * @return SimpleDateFormat
    * @see #setDateFormat
    */
   public static SimpleDateFormat getDateFormat ()
   {
      if (dateFormat == null) 
      {
         // ce dateFormat est bien une instance de SimpleDateFormat selon le source DateFormat.get
         final SimpleDateFormat myDateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT);
         // on n'accepte pas le 30/02 (qui serait alors le 02/03)
         myDateFormat.setLenient(false);
         dateFormat = myDateFormat;
      }

      return dateFormat;
   }

   /**
    * Retourne la valeur de la propriété displayDateFormat.
    * @return SimpleDateFormat
    */
   public static SimpleDateFormat getDisplayDateFormat ()
   {
      if (displayDateFormat == null) 
      {
         String pattern = getDateFormat().toPattern();
         // si le pattern contient seulement 2 chiffres pour l'année on en met 4
         // parce que c'est plus clair à l'affichage
         // mais en saisie on peut toujours n'en mettre que deux qui seront alors interprétés (avec le siècle)
         if (pattern.indexOf("yy") != -1 && pattern.indexOf("yyyy") == -1)
         {
            pattern = new StringBuilder(pattern).insert(pattern.indexOf("yy"), "yy").toString();
         }

         displayDateFormat = new SimpleDateFormat(pattern);
      }

      return displayDateFormat;
   }

   @Override
   public void insertString (final int offset, final String string, final AttributeSet attributeSet)
            throws BadLocationException
   {
      if (string == null || string.length() == 0)
      {
         return;
      }

      // pour une date on n'accepte que les chiffres et '/' (et/ou '.', ' ', ':' selon pattern)
      char c;
      final String pattern = getDateFormat().toPattern();
      final int stringLength = string.length();
      for (int i = 0; i < stringLength; i++)
      {
         c = string.charAt(i);
         if (!Character.isDigit(c) && (Character.isLetter(c) || pattern.indexOf(c) == -1))
         {
            beep();
            return;
         }
      }

      // mais sinon on accepte tout, même un format incomplet
      super.insertString(offset, string, attributeSet);
   }

   /**
    * Définit la valeur de la propriété alternateDateFormat.
    * @param newAlternateDateFormat
    *           SimpleDateFormat
    * @see #getAlternateDateFormat
    */
   public static void setAlternateDateFormat (final SimpleDateFormat newAlternateDateFormat)
   {
      alternateDateFormat = newAlternateDateFormat;
   }

   /**
    * Définit la valeur de la propriété dateFormat (et réinitialise alternateDateFormat et displayDateFormat). <br/>
    * Il est possible notamment de fournir un format acceptant plusieurs syntaxes ou lançant une RuntimeException "Date invalide" pour l'afficher en rouge si la date n'est pas entre SpiDateField.getMinimumValue() et SpiDateField.getMaximumValue().
    * @param newDateFormat
    *           SimpleDateFormat
    * @see #getDateFormat
    */
   public static void setDateFormat (final SimpleDateFormat newDateFormat)
   {
      dateFormat = newDateFormat;
      // force la reconstruction des autres formats
      setAlternateDateFormat(null);
      setDisplayDateFormat(null);
   }

   /**
    * Définit la valeur de la propriété displayDateFormat.
    * @param newDisplayDateFormat
    *           SimpleDateFormat
    * @see #getDisplayDateFormat
    */
   public static void setDisplayDateFormat (final SimpleDateFormat newDisplayDateFormat)
   {
      displayDateFormat = newDisplayDateFormat;
   }
}
