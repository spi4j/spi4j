/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Document swing interne pour la saisie d'un nombre décimal.
 * 
 * @author MINARM
 */
public class SpiDoubleDocument extends SpiTextDocument {
	private static final long serialVersionUID = 1L;

	private static final char DECIMAL_SEPARATOR = DecimalFormatSymbols.getInstance().getDecimalSeparator();

	// 2 décimales après la virgule
	private static final NumberFormat TWO_DIGITS_NUMBER_FORMAT = new DecimalFormat("0.00",
			DecimalFormatSymbols.getInstance());

	private NumberFormat numberFormat;

	private double maximumValue = Integer.MAX_VALUE;

	private double minimumValue;

	private int fractionDigits = 2;

	/**
	 * Constructeur.
	 */
	public SpiDoubleDocument() {
		super(-1);
	}

	/**
	 * Retourne la valeur de la propriété decimalSeparator.
	 * 
	 * @return char
	 */
	public char getDecimalSeparator() {
		return DECIMAL_SEPARATOR;
	}

	/**
	 * Retourne la valeur de la propriété fractionDigits.
	 * 
	 * @return int
	 * @see #setFractionDigits
	 */
	public int getFractionDigits() {
		return fractionDigits;
	}

	/**
	 * Retourne la valeur de la propriété maximumValue.
	 * 
	 * @return double
	 * @see #setMaximumValue
	 */
	public double getMaximumValue() {
		return maximumValue;
	}

	/**
	 * Retourne la valeur de la propriété minimumValue.
	 * 
	 * @return double
	 * @see #setMinimumValue
	 */
	public double getMinimumValue() {
		return minimumValue;
	}

	/**
	 * Retourne la valeur de la propriété numberFormat.
	 * 
	 * @return NumberFormat
	 * @see #setNumberFormat
	 */
	public NumberFormat getNumberFormat() {
		if (numberFormat == null) {
			// le plus courant est mis en cache statique
			if (getFractionDigits() == 2) {
				numberFormat = getTwoDigitsNumberFormat();
			} else {
				final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
				final String pattern = getFractionDigits() > 0 ? "0." : "0";
				numberFormat = new DecimalFormat(pattern, symbols);
				numberFormat.setMinimumFractionDigits(getFractionDigits());
			}
		}
		return numberFormat;
	}

	/**
	 * Retourne la valeur de la propriété twoDigitsNumberFormat.
	 * 
	 * @return NumberFormat
	 */
	public static NumberFormat getTwoDigitsNumberFormat() {
		return TWO_DIGITS_NUMBER_FORMAT;
	}

	@Override
	public void insertString(final int offset, final String string, final AttributeSet attributeSet)
			throws BadLocationException {
		if (string == null || string.length() == 0) {
			return;
		}

		String lString = string;
		int lOffset = offset;
		if ("-".equals(lString)) {
			lOffset = 0;
		} else if ("+".equals(lString) && getText(0, getLength()).startsWith("-")) {
			remove(0, 1);
			return;
		}

		lString = lString.replace('.', DECIMAL_SEPARATOR);

		String text = new StringBuilder(getText(0, getLength())).insert(lOffset, lString).toString();
		double value = 0;
		boolean isOK = true;
		// on remplace la ',' par un '.' pour le parsing
		text = text.replace(DECIMAL_SEPARATOR, '.');

		try {
			if (!"-".equals(text) || getMinimumValue() >= 0) {
				value = Double.parseDouble(text);
			}
		} catch (final NumberFormatException e) {
			isOK = false;
		}

		isOK = isOK && value >= getMinimumValue() && value <= getMaximumValue() && (text.indexOf('.') == -1
				|| (text.length() - text.indexOf('.') - 2 < getFractionDigits() && getFractionDigits() > 0));

		if (isOK) {
			super.insertString(lOffset, lString, attributeSet);
		} else {
			beep();
		}
	}

	/**
	 * Appelle la méthode insertString de la super-classe SpiTextDocument sans
	 * passer par insertString de cette classe.
	 * 
	 * @param offset       int
	 * @param string       String
	 * @param attributeSet AttributeSet
	 * @throws BadLocationException e
	 */
	public void superInsertString(final int offset, final String string, final AttributeSet attributeSet)
			throws BadLocationException {
		super.insertString(offset, string, attributeSet);
	}

	/**
	 * Parse un décimal.
	 * 
	 * @param text String
	 * @return Double
	 */
	public Double parseDouble(final String text) {
		// on remplace la ',' par un '.' pour le parsing
		final String myText = text.replace(getDecimalSeparator(), '.');
		return myText.length() != 0 && !"-".equals(myText) ? Double.parseDouble(myText) : null;
	}

	/**
	 * Définit la valeur de la propriété fractionDigits.
	 * 
	 * @param newFractionDigits int
	 * @see #getFractionDigits
	 */
	public void setFractionDigits(final int newFractionDigits) {
		fractionDigits = newFractionDigits;
		numberFormat = null; // force la reconstruction du format
	}

	/**
	 * Définit la valeur de la propriété maximumValue.
	 * 
	 * @param newMaximumValue double
	 * @see #getMaximumValue
	 */
	public void setMaximumValue(final double newMaximumValue) {
		maximumValue = newMaximumValue;
	}

	/**
	 * Définit la valeur de la propriété minimumValue.
	 * 
	 * @param newMinimumValue double
	 * @see #getMinimumValue
	 */
	public void setMinimumValue(final double newMinimumValue) {
		minimumValue = newMinimumValue;
	}

	/**
	 * Définit la valeur de la propriété numberFormat.
	 * 
	 * @param newNumberFormat NumberFormat
	 * @see #getNumberFormat
	 */
	public void setNumberFormat(final NumberFormat newNumberFormat) {
		numberFormat = newNumberFormat;
	}
}
