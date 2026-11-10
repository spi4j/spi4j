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
 * The method annotated is dedicated to refresh token.
 *
 * @author MINARM
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface RsRefreshToken {
	/**
	 * @return The authentication protocol.
	 */
	String protocol();
}
