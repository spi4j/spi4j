/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.exception;

import java.util.Arrays;

/**
 * Classe d'exception pour la validation des objets métier.
 * @author MINARM
 */
public class Spi4jValidationException extends Exception
{

   /**
    * SerialUid.
    */
   private static final long serialVersionUID = 4793201612441957811L;

   /**
    * Constructeur sans paramètres pour sérialisation GWT.
    */
   protected Spi4jValidationException ()
   {
      // RAS
   }

   /**
    * Constructeur pour un champ invalide dans un objet.
    * @param p_instance
    *           l'instance qui n'a pas pu être validée
    * @param p_fields
    *           les champs invalides dans l'instance
    */
   public Spi4jValidationException (final Object p_instance, final String... p_fields)
   {
      this(p_instance, null, p_fields);
   }

   /**
    * Constructeur pour un champ invalide dans un objet, avec une cause.
    * @param p_instance
    *           l'instance qui n'a pas pu être validée
    * @param p_fields
    *           le champ invalide dans l'instance
    * @param p_cause
    *           la cause de la non validité
    */
   public Spi4jValidationException (final Object p_instance, final Throwable p_cause, final String... p_fields)
   {
      super("Champ(s) " + Arrays.toString(p_fields) + " invalide(s) pour l'objet " + p_instance, p_cause);
   }

}
