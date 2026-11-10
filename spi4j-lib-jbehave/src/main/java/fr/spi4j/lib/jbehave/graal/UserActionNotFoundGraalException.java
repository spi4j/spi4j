/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.graal;

import java.util.Collection;

import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Exception lorsqu'une action GRAAL n'a pas été trouvé.
 * @author MINARM
 */
public class UserActionNotFoundGraalException extends GraalException
{

   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    * @param p_class
    *           la classe qui devrait contenir le champ
    * @param p_action
    *           le nom de l'action non trouvée
    */
   public UserActionNotFoundGraalException (final Class<? extends Presenter_Abs<? extends View_Itf, ?>> p_class,
            final String p_action)
   {
      super("Aucune annotation nommée @UserAction(\"" + p_action + "\") n'existe dans " + p_class.getName()
               + ". Veuillez ajouter l'annotation @UserAction(\"" + p_action + "\") sur une des méthodes de la classe "
               + p_class.getName());
   }

   /**
    * Constructeur.
    * @param p_listPresenters
    *           la liste des présenteurs actifs
    * @param p_action
    *           le nom de l'action non trouvée
    */
   public UserActionNotFoundGraalException (final Collection<Presenter_Abs<? extends View_Itf, ?>> p_listPresenters,
            final String p_action)
   {
      super("Aucune annotation nommée @UserAction(\"" + p_action
               + "\") n'existe dans les présenteurs des vues actives : " + p_listPresenters
               + ".\nVeuillez ajouter l'annotation @UserAction(\"" + p_action
               + "\") sur une des méthodes d'une classe de présenteur.");
   }

}
