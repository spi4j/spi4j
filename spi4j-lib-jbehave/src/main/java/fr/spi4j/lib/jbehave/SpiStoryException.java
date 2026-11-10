/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

/**
 * Classe de recherche des .story.
 * 
 * @author MINARM
 */
public class SpiStoryException extends RuntimeException {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur
	 * 
	 * @param p_msg : le message a afficher pour l'exception.
	 */
	SpiStoryException(final String p_msg) {
		super(p_msg);
	}
}
