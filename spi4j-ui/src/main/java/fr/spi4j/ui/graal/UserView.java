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
 * Annotation sur une vue.
 * @author MINARM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE })
@Inherited
public @interface UserView
{
   /**
    * Le nom de la vue.
    */
   String value();
}
