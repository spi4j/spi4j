/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.jsp;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for the messenger.
 * 
 * @author MINARM
 */
public class JspControllerMessenger implements JspControllerMessenger_Itf {

	/**
	 * List of messages to display.
	 */
	private List<String> _messages;

	/**
	 * The type for the message.For now it is not possible to mix different types.
	 * The type of the last message define the type for all previous messages.
	 */
	private MessageType _type;

	/**
	 * Constructor.
	 */
	JspControllerMessenger() {
		_messages = new ArrayList<>();
	}

	/**
	 * Add an message of type 'information'.
	 * 
	 * @param p_message : The specific message to display.
	 */
	public void addInfo(final String p_message, final Object... p_args) {
		_type = MessageType.INFO;
		addMessage(p_message, p_args);
	}

	/**
	 * Add an message of type 'success'.
	 * 
	 * @param p_message : The specific message to display.
	 */
	public void addSuccess(final String p_message, final Object... p_args) {
		_type = MessageType.SUCCESS;
		addMessage(p_message, p_args);
	}

	/**
	 * Add an message of type 'warning'.
	 * 
	 * @param p_message: The specific message to display.
	 */
	public void addWarning(final String p_message, final Object... p_args) {
		_type = MessageType.WARNING;
		addMessage(p_message, p_args);
	}

	/**
	 * Internal method for adding an error message.
	 * 
	 * @param p_message : The specific message to display.
	 */
	void addError(final String p_message, final Object... p_args) {
		_type = MessageType.ERROR;
		addMessage(p_message, p_args);
	}

	/**
	 * The centralized method for adding a specific message.
	 * 
	 * @param p_message : The specific message to display.
	 * @param p_args    : An optional list of arguments to inject in the message.
	 */
	private void addMessage(final String p_message, final Object... p_args) {

		String v_message = p_message;
		if (p_args.length > 0)
			v_message = MessageFormat.format(p_message, p_args);
		_messages.add(v_message);
	}

	/**
	 * Reset the list of messages.
	 */
	void reset() {
		_messages.clear();

	}

	/**
	 * Retrieve the list of messages.
	 * 
	 * @return The list of messages.
	 */
	public List<String> get_messages() {
		return _messages;
	}

	/**
	 * Retrieve the name of the specific class for messenger. (the class must be
	 * defined in the project)
	 * 
	 * @return The css class for message display.
	 */
	public String get_css() {
		return _type.get_css();
	}

	/**
	 * Retrieve the title for messenger.
	 * 
	 * @return The title for the messenger.
	 */
	public String get_title() {
		return _type.get_title();
	}

	/**
	 * Internal enumeration for types.
	 * 
	 * @author MINARM
	 */
	private enum MessageType {

		INFO("info", "Information"), SUCCESS("success", "Information"), WARNING("warning", "Avertissement"),
		ERROR("error", "Erreur");

		/**
		 * Constructor.
		 * 
		 * @param p_css   : The class for the css.
		 * @param p_title : The title to display.
		 */
		MessageType(final String p_css, final String p_title) {
			_css = p_css;
			_title = p_title;
		}

		/**
		 * The class for the css.
		 */
		private final String _css;

		/**
		 * The title to display.
		 */
		private final String _title;

		/**
		 * Retrieve the class for the css.
		 * 
		 * @return The class for the css.
		 */
		public String get_css() {
			return _css;
		}

		/**
		 * Retrieve the title to display.
		 * 
		 * @return The title to display.
		 */
		public String get_title() {
			return _title;
		}
	}
}
