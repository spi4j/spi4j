/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.util.Map;

import org.json.JSONObject;

/**
 * Connector interface. All custom connectors must implements this interface.
 * The main goal of this interface is to allow the connection between all
 * different parameters of an initial request (in order to obtain token(s)) and
 * the helper class for the token(s) recovery from the authentication server.
 *
 * @author MINARM
 */
public interface RsTokenConnector_Itf {

	/**
	 * Retrieve all properties (from GET or POST) from the intial query.
	 *
	 * @return The map for query properties.
	 */
	Map<String, String> get_params();

	/**
	 * Retrieve the header parameters (depending the authentication flow).
	 *
	 * @return The map for header properties.
	 */
	Map<String, String> get_headerParams();

	/**
	 * Retrieve the current token configuration.
	 *
	 * @return The token configuration.
	 */
	RsToken get_tokenConfig();

	/**
	 * Retrieve the tokens with possibly new properties.
	 *
	 * @param p_tokens : The token(s).
	 * @return The token(s) with added properties or a new applicative token (or
	 *         anything else). By default return the token(s).
	 */
	JSONObject get_tokens(final JSONObject p_tokens);
}
