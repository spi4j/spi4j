/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.graal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation sur les actions utilisateurs.
 * @author MINARM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface UserAction
{
   /** "Cliquer sur bouton " */
   String c_BOUTON = "Cliquer sur bouton ";

   /** "Simulation d'un événement " */
   String c_SIMULATION = "Simulation événement ";

   /**
    * Le nom de l'action.
    */
   String value();
}
