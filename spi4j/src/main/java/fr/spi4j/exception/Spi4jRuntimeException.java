/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.exception;

/**
 * Exception lors de l'exécution de Spi4J.
 * @author MINARM
 */
public class Spi4jRuntimeException extends RuntimeException
{
   /**
    * SerialUid.
    */
   private static final long serialVersionUID = -3010160504882365661L;

   /** La solution potentielle de l'erreur. */
   private final String _msgSolution;

   /**
    * Constructeur avec une cause, un message d'erreur et une solution potentielle.
    * @param p_rootCause
    *           La cause
    * @param p_msgError
    *           Le message d'erreur
    * @param p_msgSolution
    *           La solution potentielle
    */
   public Spi4jRuntimeException (final Throwable p_rootCause, final String p_msgError, final String p_msgSolution)
   {
      // Partie héritée
      super(p_msgError, p_rootCause);
      // Partie spécifique
      _msgSolution = p_msgSolution;
   }

   /**
    * Constructeur avec un message d'erreur et une solution potentielle.
    * @param p_msgError
    *           Le message d'erreur
    * @param p_msgSolution
    *           La solution potentielle
    */
   public Spi4jRuntimeException (final String p_msgError, final String p_msgSolution)
   {
      this(null, p_msgError, p_msgSolution);
   }

   /**
    * Permet d'obtenir la solution potentielle de l'erreur.
    * @return La solution potentielle de l'erreur.
    */
   final String getMsgSolution ()
   {
      return _msgSolution;
   }

   @Override
   public String toString ()
   {
      return "\nErreur racine : " + getCause() + "\nErreur levée : " + getMessage() + "\nSolution potentielle : "
               + getMsgSolution();
   }
}
