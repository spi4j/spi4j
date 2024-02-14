/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp.rda;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation sur les méthodes de génération d'id dans les présenteurs.
 * @author MINARM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{ElementType.METHOD })
@Inherited
public @interface PresenterId
{
   // pas de méthode
}
