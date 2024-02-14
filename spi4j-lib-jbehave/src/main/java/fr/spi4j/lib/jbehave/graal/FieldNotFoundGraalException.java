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
 * Exception lorsqu'un champ GRAAL n'a pas été trouvé.
 * @author MINARM
 */
public class FieldNotFoundGraalException extends GraalException
{

   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    * @param p_class
    *           la classe qui devrait contenir le champ
    * @param p_champ
    *           le nom du champ non trouvé
    */
   public FieldNotFoundGraalException (final Class<?> p_class, final String p_champ)
   {
      super("Aucune annotation nommée @Field(\"" + p_champ + "\") n'existe dans " + p_class.getName()
               + ". Veuillez ajouter l'annotation @Field(\"" + p_champ + "\") sur l'un des getter de la classe "
               + p_class.getName());
   }

   /**
    * Constructeur.
    * @param p_listPresenters
    *           la liste des présenteurs actifs
    * @param p_champ
    *           le nom du champ non trouvé
    */
   public FieldNotFoundGraalException (final Collection<Presenter_Abs<? extends View_Itf, ?>> p_listPresenters,
            final String p_champ)
   {
      super("Aucune annotation nommée @Field(\"" + p_champ + "\") n'existe dans les interfaces des vues actives : "
               + p_listPresenters + ".\nVeuillez ajouter l'annotation @Field(\"" + p_champ
               + "\") sur l'un des getter d'une classe de vue.");
   }

}
