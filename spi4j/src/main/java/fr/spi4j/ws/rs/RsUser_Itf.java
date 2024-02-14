/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * User interface for login, specifically dedicated for REST services.
 *
 * @author MINARM.
 */
@SuppressWarnings("javadoc")
public interface RsUser_Itf {
	/**
	 * @return the user ...
	 */
	public String setIdentifiant(final String p_ident);
}
