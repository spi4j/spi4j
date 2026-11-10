/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.SwingConstants;
import javax.swing.text.Document;

import fr.spi4j.ui.HasDate_Itf;

/**
 * Champs de saisie d'une heure (type Date).
 * @author MINARM
 */
public class SpiHourField extends SpiTextField<Date> implements HasDate_Itf
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    * @see #createDefaultModel
    */
   public SpiHourField ()
   {
      super();
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   @Override
   protected Document createDefaultModel ()
   {
      return new SpiHourDocument();
   }

   @Override
   public Date getValue ()
   {
      final String text = super.getText();
      if (text == null || text.length() == 0)
      {
         return null;
      }

      try
      {
         return getDateFormat().parse(text);
      }
      catch (final ParseException e)
      {
         try
         {
            return SpiHourDocument.getAlternateDateFormat().parse(text);
         }
         catch (final ParseException e2)
         {
            beep();
            return null;
         }
      }
   }

   /**
    * Retourne la valeur de la propriété dateFormat interne.
    * @return SimpleDateFormat
    * @see #setDateFormat
    */
   protected static SimpleDateFormat getDateFormat ()
   {
      return SpiHourDocument.getDateFormat();
   }

   @Override
   protected void keyEvent (final KeyEvent event)
   {
      // Ici, la touche Entrée valide la saisie
      // ou remplit le champ avec l'heure courante si il est vide.
      // Et la flèche Haut incrémente la valeur de 1 heure,
      // et la flèche Bas la décrémente.
      final int keyCode = event.getKeyCode();
      if (isEditable() && event.getID() == KeyEvent.KEY_PRESSED
               && (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) && super.getText() != null
               && super.getText().length() != 0)
      {
         final Date date = getValue();
         if (date != null)
         {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, keyCode == KeyEvent.VK_UP ? 1 : -1);
            setValue(calendar.getTime());
            event.consume();
         }
      }
      else if (isEditable() && event.getID() == KeyEvent.KEY_PRESSED && keyCode == KeyEvent.VK_ENTER
               && (super.getText() == null || super.getText().length() == 0))
      {
         setNow();
         event.consume();
      }
      else
      {
         super.keyEvent(event);
      }
   }

   @Override
   public void setValue (final Date newDate)
   {
      if (newDate != null)
      {
         super.setText(getDateFormat().format(newDate));
      }
      else
      {
         super.setText(null);
      }
   }

   /**
    * Définit la valeur de la propriété dateFormat interne.
    * @param newDateFormat
    *           SimpleDateFormat
    * @see #getDateFormat
    */
   protected static void setDateFormat (final SimpleDateFormat newDateFormat)
   {
      SpiHourDocument.setDateFormat(newDateFormat);
   }

   /**
    * Définit la valeur de la propriété date avec l'heure courante.
    * @see #setValue
    */
   public void setNow ()
   {
      setValue(new Date());
   }
}
