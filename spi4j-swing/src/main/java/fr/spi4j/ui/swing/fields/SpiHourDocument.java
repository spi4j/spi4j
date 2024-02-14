/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Document swing interne pour la saisie d'une heure.
 * @author MINARM
 */
public class SpiHourDocument extends SpiTextDocument
{
   private static final long serialVersionUID = 1L;

   // Les instances de formats suivants sont partagées pour tous les champs heures.
   // Note : Ce n'est pas gênant même pour un DateFormat car les traitements Swing se font toujours sur un seul thread.

   // format de base et 1er format pour la validation
   private static SimpleDateFormat dateFormat;

   // format alternatif de validation (sans ':')
   private static SimpleDateFormat alternateDateFormat;

   /**
    * Constructeur.
    */
   public SpiHourDocument ()
   {
      super(getDateFormat().toPattern().indexOf(' ') == -1 ? getDateFormat().toPattern().length() : 10);
   }

   /**
    * Retourne la valeur de la propriété alternateDateFormat.
    * @return SimpleDateFormat
    */
   public static SimpleDateFormat getAlternateDateFormat ()
   {
      if (alternateDateFormat == null) 
      {
         // pour construire ce dateFormat on enlève les ':', '.' du pattern de dateFormat
         // ie on peut saisir 1234 au lieu de 12:34

         final StringBuilder pattern = new StringBuilder(getDateFormat().toPattern());
         // note : il faut réévaluer pattern.length() chaque fois dans la condition
         // puisque la longueur diminue au fur et à mesure
         for (int i = 0; i < pattern.length(); i++)
         {
            if (!Character.isLetter(pattern.charAt(i)) && ' ' != pattern.charAt(i))
            { // on laisse ' ' pour "h:mm a" aux US
               pattern.deleteCharAt(i);
            }
         }

         final SimpleDateFormat myAlternateDateFormat = new SimpleDateFormat(pattern.toString());
         myAlternateDateFormat.setLenient(false);

         alternateDateFormat = myAlternateDateFormat;
      }

      return alternateDateFormat;
   }

   /**
    * Retourne la valeur de la propriété dateFormat (pattern hh:mm, lenient est false).
    * @return SimpleDateFormat
    * @see #setDateFormat
    */
   public static SimpleDateFormat getDateFormat ()
   {
      if (dateFormat == null) 
      {
         // ce dateFormat est bien une instance de SimpleDateFormat selon le source DateFormat.get
         final SimpleDateFormat myDateFormat = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT);
         myDateFormat.setLenient(false);
         dateFormat = myDateFormat;
      }

      return dateFormat;
   }

   @Override
   public void insertString (final int offset, final String string, final AttributeSet attributeSet)
            throws BadLocationException
   {
      if (string == null || string.length() == 0)
      {
         return;
      }

      // pour une heure on n'accepte que les chiffres et ':'

      // et tout si pattern contient ' '
      // ex : "h:mm a" pour am/pm aux US
      // ou "... z" si dateFormat défini avec fuseau horaire
      char c;
      final String pattern = getDateFormat().toPattern();
      final int stringLength = string.length();
      for (int i = 0; i < stringLength; i++)
      {
         c = string.charAt(i);
         if (!Character.isDigit(c) && pattern.indexOf(' ') == -1 && (Character.isLetter(c) || pattern.indexOf(c) == -1))
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
    * Définit la valeur de la propriété dateFormat (et réinitialise alternateDateFormat).
    * @param newDateFormat
    *           SimpleDateFormat
    * @see #getDateFormat
    */
   public static void setDateFormat (final SimpleDateFormat newDateFormat)
   {
      dateFormat = newDateFormat;
      // force la reconstruction des autres formats
      setAlternateDateFormat(null);
   }
}
