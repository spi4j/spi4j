/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Document swing alternatif à SpiIntegerDocument pour la saisie d'un nombre
 * entier, et qui affiche et accepte en saisie des séparateurs de milliers.
 * <p>
 * Pour l'utiliser, appeler au démarrage de l'application :
 * System.setProperty("fr.spi4j.ui.swing.fields.SpiIntegerField.documentType",
 * "fr.spi4j.ui.swing.fields.SpiIntegerDocumentWithGrouping");
 * 
 * @author MINARM
 */
public class SpiIntegerDocumentWithGrouping extends SpiIntegerDocument {
	private static final long serialVersionUID = 1L;

	private static final Pattern NUMBER_PATTERN = Pattern.compile("[\\d\\s\\" + (char) 160 + ",\\.\\+\\-]*");

	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance();

	/**
	 * Constructeur.
	 */
	public SpiIntegerDocumentWithGrouping() {
		super();
	}

	@Override
	public void insertString(final int offset, final String string, final AttributeSet attributeSet)
			throws BadLocationException {
		if (string == null || string.length() == 0) {
			return;
		}

		int lOffset = offset;
		if ("-".equals(string)) {
			lOffset = 0;
		} else if ("+".equals(string) && getText(0, getLength()).startsWith("-")) {
			remove(0, 1);
			return;
		}

		// même si l'utilisateur saisi un ' ', on le par l'espace insécable
		// qui est séparateur de milliers en France
		final String lString = string.replace(' ', (char) 160);

		final String text = new StringBuilder(getText(0, getLength())).insert(lOffset, lString).toString();
		long value = 0;
		boolean isOK = true;
		try {
			if (!"-".equals(text) || getMinimumValue() >= 0) {
				value = NUMBER_FORMAT.parse(text).intValue();
			}
		} catch (final ParseException e) {
			isOK = false;
		}
		isOK = isOK && value >= getMinimumValue() && value <= getMaximumValue()
				&& NUMBER_PATTERN.matcher(text).matches();

		if (isOK) {
			superInsertString(lOffset, lString, attributeSet);
		} else {
			beep();
		}
	}

	@Override
	public Integer parseInteger(final String text) {
		if (text != null && text.length() != 0 && !"-".equals(text)) {
			try {
				return NUMBER_FORMAT.parse(text).intValue();
			} catch (final ParseException v_e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public String formatInteger(final Integer integer) {
		return NUMBER_FORMAT.format(integer);
	}
}
