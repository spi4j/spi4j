/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.text.NumberFormat;

import javax.swing.SwingConstants;
import javax.swing.text.Document;

import fr.spi4j.ui.HasDouble_Itf;

/**
 * Champs de saisie d'un nombre décimal (type Double).
 * @author MINARM
 */
public class SpiDoubleField extends SpiTextField<Double> implements HasDouble_Itf
{
   private static final long serialVersionUID = 1L;

   private static final Class<Document> DOCUMENT_TYPE;

   static
   {
      final String type = System.getProperty(SpiDoubleField.class.getName() + ".documentType");
      try
      {
         @SuppressWarnings("unchecked")
         final Class<Document> classe = type != null ? (Class<Document>) Class.forName(type) : null;
         DOCUMENT_TYPE = classe;
      }
      catch (final ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Constructeur.
    * @see #createDefaultModel
    */
   public SpiDoubleField ()
   {
      super();
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   @Override
   protected Document createDefaultModel ()
   {
      if (DOCUMENT_TYPE != null)
      {
         try
         {
            return DOCUMENT_TYPE.getDeclaredConstructor().newInstance();
         }
         catch (final Exception e)
         {
            throw new RuntimeException(e);
         }
      }

      return new SpiDoubleDocument();
   }

   @Override
   public Double getValue ()
   {
      final String text = super.getText();
      return getDoubleDocument().parseDouble(text);
   }

   /**
    * Retourne la valeur de la propriété doubleDocument.
    * @return SpiDoubleDocument
    */
   protected SpiDoubleDocument getDoubleDocument ()
   {
      return (SpiDoubleDocument) getDocument();
   }

   /**
    * Retourne la valeur de la propriété fractionDigits.
    * @return int
    * @see #setFractionDigits
    */
   public int getFractionDigits ()
   {
      return getDoubleDocument().getFractionDigits();
   }

   /**
    * Retourne la valeur de la propriété maximumValue.
    * @return double
    * @see #setMaximumValue
    */
   public double getMaximumValue ()
   {
      return getDoubleDocument().getMaximumValue();
   }

   /**
    * Retourne la valeur de la propriété minimumValue.
    * @return double
    * @see #setMinimumValue
    */
   public double getMinimumValue ()
   {
      return getDoubleDocument().getMinimumValue();
   }

   /**
    * Retourne la valeur de la propriété numberFormat interne.
    * @return NumberFormat
    * @see #setNumberFormat
    */
   protected NumberFormat getNumberFormat ()
   {
      return getDoubleDocument().getNumberFormat();
   }

   @Override
   public void setValue (final Double newDouble)
   {
      super.setText(newDouble != null ? getNumberFormat().format(newDouble) : null);
   }

   /**
    * Définit la valeur de la propriété fractionDigits.
    * @param newFractionDigits
    *           int
    * @see #getFractionDigits
    */
   public void setFractionDigits (final int newFractionDigits)
   {
      getDoubleDocument().setFractionDigits(newFractionDigits);
      // setValue pour réaffichage avec les décimales
      setValue(getValue());
   }

   /**
    * Définit la valeur de la propriété maximumValue.
    * @param newMaximumValue
    *           double
    * @see #getMaximumValue
    */
   public void setMaximumValue (final double newMaximumValue)
   {
      getDoubleDocument().setMaximumValue(newMaximumValue);
   }

   /**
    * Définit la valeur de la propriété minimumValue.
    * @param newMinimumValue
    *           double
    * @see #getMinimumValue
    */
   public void setMinimumValue (final double newMinimumValue)
   {
      getDoubleDocument().setMinimumValue(newMinimumValue);
   }

   /**
    * Définit la valeur de la propriété numberFormat interne.
    * @param newNumberFormat
    *           NumberFormat
    * @see #getNumberFormat
    */
   protected void setNumberFormat (final NumberFormat newNumberFormat)
   {
      getDoubleDocument().setNumberFormat(newNumberFormat);
   }
}
