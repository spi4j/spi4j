/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.ws.rs.NameBinding;

/**
 * Each method annotated must be secured with an access token.
 *
 * @author MINARM
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface RsSecured {
	/**
	 * @return The associated token(s).
	 */
	String tokens();

	/**
	 *
	 * @return The associated scope(s).
	 */
	String scopes();
}
