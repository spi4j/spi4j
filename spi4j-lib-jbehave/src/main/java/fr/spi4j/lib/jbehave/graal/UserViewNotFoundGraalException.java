/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.graal;

import fr.spi4j.ui.mvp.View_Itf;

/**
 * Exception lorsqu'une vue GRAAL n'a pas été trouvé.
 * @author MINARM
 */
public class UserViewNotFoundGraalException extends GraalException
{

   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    * @param p_class
    *           la classe qui devrait contenir le champ
    */
   public UserViewNotFoundGraalException (final Class<? extends View_Itf> p_class)
   {
      super("Aucune annotation nommée \"@UserView\" n'existe dans " + p_class.getName()
               + ". Veuillez ajouter l'annotation \"@UserView\" sur la classe " + p_class.getName());
   }

}
