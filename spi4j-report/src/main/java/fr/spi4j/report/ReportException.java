/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report;

/**
 * Classe mère des exceptions d'export.
 * @author MINARM
 */
public class ReportException extends RuntimeException
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur parent.
    * @param p_message
    *           le message
    */
   public ReportException (final String p_message)
   {
      this(p_message, null);
   }

   /**
    * Constructeur parent surchargé.
    * @param p_message
    *           le message
    * @param p_cause
    *           la cause
    */
   public ReportException (final String p_message, final Throwable p_cause)
   {
      super(p_message, p_cause);
   }

}
