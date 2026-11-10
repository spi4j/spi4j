/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.Document;

import fr.spi4j.ui.HasMandatory_Itf;
import fr.spi4j.ui.HasValue_Itf;

/**
 * Classe abstraite servant de base aux champs de saisie. Cette classe hérite de
 * JTextField.
 * <p>
 * Pour désactiver globalement, le feedback des champs obligatoires ajouter la
 * propriété système
 * -Dfr.spi4j.ui.swing.fields.SpiTextField.mandatoryFieldsFeedbackDisabled=true
 * <p>
 * Pour désactiver globalement, la sélection du texte lors du gain de focus,
 * ajouter la propriété système
 * -Dfr.spi4j.ui.swing.fields.SpiTextField.textSelectionOnFocusGained=false
 * 
 * @param <TypeValue> Type de la valeur saisie
 * @author MINARM
 */
public abstract class SpiTextField<TypeValue> extends JTextField implements HasValue_Itf<TypeValue>, HasMandatory_Itf {
	static final boolean TEXT_SELECTION_ON_FOCUS_GAINED = Boolean
			.parseBoolean(System.getProperty(SpiTextField.class.getName() + ".textSelectionOnFocusGained", "true"));

	private static final boolean MANDATORY_FIELDS_FEEDBACK_DISABLED = Boolean
			.parseBoolean(System.getProperty(SpiTextField.class.getName() + ".mandatoryFieldsFeedbackDisabled"));

	private static final Color INVALID_COLOR = Color.RED;

	private static final long serialVersionUID = 1L;

	// ces gestionnaires d'événement sont statiques et non réinstanciés pour
	// économie mémoire
	// l'instance du composant est déduite à partir de la source de l'événement
	private static final FocusHandler FOCUS_HANDLER = new FocusHandler();

	private static final KeyHandler KEY_HANDLER = new KeyHandler();

	static {
		final String lookAndFeelName = UIManager.getLookAndFeel().getName();
		if (!lookAndFeelName.startsWith("Windows") && !lookAndFeelName.startsWith("Metal")) {
			// nécessaire pour Looks and Feel Nimbus
			UIManager.put("TextField.inactiveBackground", new Color(240, 240, 240));
		}
	}

	/**
	 * FocusListener.
	 * 
	 * @author MINARM
	 */
	private static class FocusHandler implements FocusListener {
		/**
		 * Constructeur.
		 */
		FocusHandler() {
			super();
		}

		@Override
		public void focusGained(final FocusEvent focusEvent) {
			final Object source = focusEvent.getSource();
			if (source instanceof SpiTextField) {
				final SpiTextField<?> v_field = (SpiTextField<?>) source;
				if (TEXT_SELECTION_ON_FOCUS_GAINED && v_field.isEditable()) {
					v_field.selectAll();
				}
			}
		}

		@Override
		public void focusLost(final FocusEvent focusEvent) {
			final Object source = focusEvent.getSource();
			if (source instanceof SpiTextField) {
				@SuppressWarnings("unchecked")
				final SpiTextField<Object> v_field = (SpiTextField<Object>) source;
				final Object value = v_field.getValue();
				// v_field.setValue réinitialise v_field.invalidNull, donc il doit être avant
				// "v_field.invalidNull = invalidNull"
				v_field.setValue(value);
				if (!MANDATORY_FIELDS_FEEDBACK_DISABLED && v_field.isMandatory() && v_field.isEditable()) {
					final boolean invalidNull = value == null && v_field.isMandatory();
					v_field.invalidNull = invalidNull;
					v_field.repaint();
				}
			}
		}
	}

	/**
	 * KeyListener.
	 * 
	 * @author MINARM
	 */
	private static class KeyHandler implements KeyListener {
		/**
		 * Constructeur.
		 */
		KeyHandler() {
			super();
		}

		@Override
		public void keyPressed(final KeyEvent keyEvent) {
			final Object source = keyEvent.getSource();
			if (source instanceof SpiTextField) {
				((SpiTextField<?>) source).keyEvent(keyEvent);
			}
		}

		@Override
		public void keyReleased(final KeyEvent keyEvent) {
			final Object source = keyEvent.getSource();
			if (source instanceof SpiTextField) {
				((SpiTextField<?>) source).keyEvent(keyEvent);
			}
		}

		@Override
		public void keyTyped(final KeyEvent keyEvent) {
			final Object source = keyEvent.getSource();
			if (source instanceof SpiTextField) {
				((SpiTextField<?>) source).keyEvent(keyEvent);
			}
		}
	}

	private boolean mandatory;

	private boolean invalidNull;

	/**
	 * Constructeur.
	 */
	protected SpiTextField() {
		this(null);
	}

	/**
	 * Constructeur.
	 * 
	 * @param document text.Document
	 */
	protected SpiTextField(final Document document) {
		super(document, null, 0);
		addFocusListener(FOCUS_HANDLER);
		addKeyListener(KEY_HANDLER);

		// setDragEnabled(true);
	}

	/**
	 * Cette méthode appelée en cas de saisie invalide émet un beep.
	 */
	protected void beep() {
		getTextDocument().beep();
	}

	/**
	 * Crée le modèle de document par défaut.
	 * <p>
	 * Pour cette classe renvoie une instance de SpiTextDocument, elle sera
	 * surchargée dans les classes filles.
	 * 
	 * @return text.Document
	 */
	@Override
	protected Document createDefaultModel() {
		return new SpiTextDocument(-1);
	}

	/**
	 * Retourne la valeur de la propriété textDocument.
	 * 
	 * @return SpiTextDocument
	 */
	protected SpiTextDocument getTextDocument() {
		return (SpiTextDocument) getDocument();
	}

	/**
	 * Gestion des événements claviers.
	 * <p>
	 * La touche Entrée valide la saisie comme à la perte du focus.
	 * <p>
	 * Cette méthode est surchargée dans SpiDateField pour la date du jour, dans
	 * SpiHourField pour l'heure courante et dans SpiIntegerField pour +1/-1.
	 * 
	 * @param event KeyEvent
	 */
	protected void keyEvent(final KeyEvent event) {
		try {
			if (event.getID() == KeyEvent.KEY_PRESSED && event.getKeyCode() == KeyEvent.VK_ENTER && isEditable()) {
				// voir aussi focusEvent
				setValue(getValue());
			}
		} catch (final Exception e) {
			beep();
		}
	}

	@Override
	public void setEditable(final boolean newEditable) {
		// note : le document n'est pas encore défini lors de l'appel à setEditable dans
		// le constructeur de JTextComponent
		if (getDocument() != null) {
			getTextDocument().setEditable(newEditable);
		}
		super.setEditable(newEditable);
		// appel de updateBackground car sans cela la couleur de fond du JTextField non
		// éditable reste blanche et non grisée si le look and feel est Nimbus (à base
		// de Synth)
		SpiStringArea.updateBackground(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Utiliser setEditable
	 */
	@Deprecated
	@Override
	public void setEnabled(final boolean newEnabled) {
		// Surcharge de setEnabled pour appeler setEditable.
		setEditable(newEnabled);
	}

	/**
	 * Définit la valeur de la propriété text.
	 * <p>
	 * Cette méthode est utilisée en interne par les classes filles, elle n'a pas de
	 * raison d'être utilisée autrement.
	 * 
	 * @param newText String
	 */
	@Override
	public void setText(final String newText) {
		// si le texte est le même, on évite de lancer un DocumentEvent inutile
		if (!getText().equals(newText)) {
			// le document doit être un SpiTextDocument
			final SpiTextDocument document = getTextDocument();
			final boolean oldEditable = document.isEditable();
			document.setEditable(true);
			super.setText(newText);
			setCaretPosition(0); // on scrolle au début
			document.setEditable(oldEditable);
		}
		invalidNull = false;
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);
		if (invalidNull) {
			// Contrôle de surface selon les attributs de DTO dans le modèle:
			// si le champ est éditable, avec un mapping mandatory et que l'utilisateur est
			// passé sur le champ
			// sans saisir une valeur, on lui donne un feedback pour lui rappeler de saisir
			// ce champ obligatoire
			final Graphics g2 = g.create();
			g2.setColor(INVALID_COLOR);
			final Insets insets = getBorder().getBorderInsets(this);
			g2.drawRect(insets.left - 3, insets.top - 3, getWidth() - insets.right - insets.left + 5,
					getHeight() - insets.bottom - insets.top + 5);
			g2.dispose();
		}
	}

	@Override
	public boolean isMandatory() {
		return mandatory;
	}

	@Override
	public void setMandatory(final boolean p_mandatory) {
		this.mandatory = p_mandatory;
	}
}
