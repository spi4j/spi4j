/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Un présenteur avec cet id existe déjà dans le ViewManager.
 * @author MINARM
 */
public class ViewAlreadyRegisteredException extends Spi4jRuntimeException
{

   private static final long serialVersionUID = 1L;

   /**
    * Constructeur avec message et solution.
    * @param p_msgError
    *           le message d'erreur
    * @param p_msgSolution
    *           la solution potentielle
    */
   public ViewAlreadyRegisteredException (final String p_msgError, final String p_msgSolution)
   {
      super(p_msgError, p_msgSolution);
   }

}
