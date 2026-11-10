/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.jsp;

/**
 * Interface for the messenger. It was taken as a decision to not write 'error'
 * type messages directly but to recover them only through exceptions. See at
 * usage time if it is useful.
 * 
 * @author MINARM
 */
public interface JspControllerMessenger_Itf {

	/**
	 * Add a message for information.
	 * 
	 * @param p_message : The specific message to display.
	 * @param p_args    : Some optional arguments to insert in the message.
	 */
	void addInfo(final String p_message, final Object... p_args);

	/**
	 * Add a message for success information.
	 * 
	 * @param p_message : The specific message to display.
	 * @param p_args    : Some optional arguments to insert in the message.
	 */
	void addSuccess(final String p_message, final Object... p_args);

	/**
	 * Add a message for warning.
	 * 
	 * @param p_message : The specific message to display.
	 * @param p_args    : Some optional arguments to insert in the message.
	 */
	void addWarning(final String p_message, final Object... p_args);

}
