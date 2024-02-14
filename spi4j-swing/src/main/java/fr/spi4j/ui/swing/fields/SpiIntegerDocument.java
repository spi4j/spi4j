/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

/**
 * Document swing interne pour la saisie d'un nombre entier.
 * @author MINARM
 */
public class SpiIntegerDocument extends SpiLongDocument
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    */
   public SpiIntegerDocument ()
   {
      super();
      setMaximumValue(Integer.MAX_VALUE);
   }

   /**
    * Parse un entier.
    * @param text
    *           String
    * @return Integer
    */
   public Integer parseInteger (final String text)
   {
      return text != null && text.length() != 0 && !"-".equals(text) ? Integer.valueOf(text) : null;
   }

   /**
    * Formate un entier.
    * @param integer
    *           Integer
    * @return String
    */
   public String formatInteger (final Integer integer)
   {
      return integer.toString();
   }
}
