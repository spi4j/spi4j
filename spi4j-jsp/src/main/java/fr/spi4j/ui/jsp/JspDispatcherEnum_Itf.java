/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.jsp;

/**
 * Interface for the application dispatcher. The dispatcher is called by the
 * main jsp controller.
 * 
 * @author MINARM
 */
public interface JspDispatcherEnum_Itf {

	/**
	 * Retrieve the dispatch string.
	 * 
	 * @return the dispatch string.
	 */
	public String dispatch();

	/**
	 * Retrieve the redirection indicator.
	 * 
	 * @return 'true' if the dispatch is a redirect.
	 */
	public boolean redirect();

	/**
	 * Retrieve the 'from menu' indicator.
	 * 
	 * @return 'true' if the dispatch is from a menu.
	 */
	public boolean menu();
}
