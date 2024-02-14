/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author MINARM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Permissions
{
   /**
    * Les permissions requises pour cette opération.
    */
   String[] value();

   /**
    * Un opérateur pour les permissions (défaut = OR)
    */
   PermissionsOperator_Enum operator() default PermissionsOperator_Enum.OR;
}
