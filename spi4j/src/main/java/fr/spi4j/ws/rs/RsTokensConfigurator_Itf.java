/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * @author MINARM.
 */
public interface RsTokensConfigurator_Itf {

	/**
	 * Retrieve the container for all tokens definitions.
	 *
	 * @param p_tokensContainer : The container for all defined and required tokens.
	 */
	public void defineRequiredTokens(final RsTokensContainer p_tokensContainer);
}
