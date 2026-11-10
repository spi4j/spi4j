/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.SwingConstants;
import javax.swing.text.Document;

import fr.spi4j.ui.HasDate_Itf;

/**
 * Champs de saisie d'une date (type java.util.Date).
 * 
 * @author MINARM
 */
public class SpiDateField extends SpiTextField<Date> implements HasDate_Itf {
	private static final long serialVersionUID = 1L;

	// Les instances maximumValue et minimumValue sont partagées pour tous les
	// champs de date
	private static Date maximumValue;

	private static Date minimumValue;

	private boolean endOfDay;

	static {
		init();
	}

	/**
	 * Constructeur.
	 * 
	 * @see #createDefaultModel
	 */
	public SpiDateField() {
		super();
		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	@Override
	protected Document createDefaultModel() {
		return new SpiDateDocument();
	}

	@Override
	public Date getValue() {
		final String text = super.getText();
		if (text == null || text.length() == 0) {
			return null;
		}
		// note : L'année 0 (et 000) sur un seul chiffre est interprétée par java comme
		// 0001 et non comme 2000.
		// (Si dateFormat était lenient false, il y aurait même une erreur).
		// Selon la valeur de minimumValue, elle est remplacée par 1900
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(getDateFormat().parse(text));
		} catch (final ParseException e) {
			try {
				calendar.setTime(SpiDateDocument.getAlternateDateFormat().parse(text));
			} catch (final ParseException e2) {
				beep();
				return null;
			}
		}

		calendar = validateMinMaxValue(calendar);

		// l'heure est soit 0h soit 23h59 selon endOfDay
		// (on ne garde pas l'heure courante mais seulement le jour)
		calendar.set(Calendar.HOUR_OF_DAY, isEndOfDay() ? 23 : 0);
		calendar.set(Calendar.MINUTE, isEndOfDay() ? 59 : 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Retourne la valeur de la propriété dateFormat interne.
	 * 
	 * @return SimpleDateFormat
	 * @see #setDateFormat
	 */
	protected static SimpleDateFormat getDateFormat() {
		return SpiDateDocument.getDateFormat();
	}

	/**
	 * Retourne la valeur de la propriété maximumValue.
	 * 
	 * @return Date
	 * @see #setMaximumValue
	 */
	public static Date getMaximumValue() {
		return maximumValue;
	}

	/**
	 * Retourne la valeur de la propriété minimumValue.
	 * 
	 * @return Date
	 * @see #setMinimumValue
	 */
	public static Date getMinimumValue() {
		return minimumValue;
	}

	/**
	 * Initialisation statique.
	 */
	protected static void init() {
		// Ces valeurs mini et maxi ne sont pas du tout limitées par Java
		// (Date accepte avant et après).
		// Ce sont juste des valeurs 'sensées' dans un contexte 'habituel'
		setMinimumValue(new GregorianCalendar(1900, 0, 1).getTime());
		setMaximumValue(new GregorianCalendar(2100, 0, 1).getTime());
	}

	/**
	 * Retourne la valeur de la propriété endOfDay.
	 * 
	 * @return boolean
	 * @see #setEndOfDay
	 */
	public boolean isEndOfDay() {
		return endOfDay;
	}

	@Override
	public void setValue(final Date newDate) {
		if (newDate != null) {
			super.setText(SpiDateDocument.getDisplayDateFormat().format(newDate));
		} else {
			super.setText(null);
		}
	}

	/**
	 * Définit la valeur de la propriété dateFormat interne.
	 * 
	 * @param newDateFormat SimpleDateFormat
	 * @see #getDateFormat
	 */
	protected static void setDateFormat(final SimpleDateFormat newDateFormat) {
		SpiDateDocument.setDateFormat(newDateFormat);
	}

	/**
	 * Définit la valeur de la propriété endOfDay.
	 * 
	 * @param newEndOfDay boolean
	 * @see #isEndOfDay
	 */
	public void setEndOfDay(final boolean newEndOfDay) {
		endOfDay = newEndOfDay;
	}

	/**
	 * Définit la valeur de la propriété maximumValue.
	 * 
	 * @param newMaximumValue Date
	 * @see #getMaximumValue
	 */
	public static void setMaximumValue(final Date newMaximumValue) {
		maximumValue = newMaximumValue;
	}

	/**
	 * Définit la valeur de la propriété minimumValue.
	 * 
	 * @param newMinimumValue Date
	 * @see #getMinimumValue
	 */
	public static void setMinimumValue(final Date newMinimumValue) {
		minimumValue = newMinimumValue;
	}

	/**
	 * Définit la valeur de la propriété date avec la date du jour.
	 * 
	 * @see #setValue
	 */
	public void setNow() {
		setValue(new Date());
	}

	@Override
	protected void keyEvent(final KeyEvent event) {
		// Ici, la touche Entrée valide la saisie
		// ou remplit le champ avec la date du jour si il est vide.
		// Et la flèche Haut incrémente la valeur de 1 jour,
		// et la flèche Bas la décrémente.
		final int keyCode = event.getKeyCode();
		if (isEditable() && event.getID() == KeyEvent.KEY_PRESSED && keyCode == KeyEvent.VK_ENTER
				&& (super.getText() == null || super.getText().length() == 0)) {
			setNow();
			event.consume();
		} else if (isEditable() && event.getID() == KeyEvent.KEY_PRESSED
				&& (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) && super.getText() != null
				&& super.getText().length() != 0) {
			final Date date = getValue();
			if (date != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_YEAR, keyCode == KeyEvent.VK_UP ? 1 : -1);
				calendar = validateMinMaxValue(calendar);
				setValue(calendar.getTime());
				event.consume();
			}
		} else {
			super.keyEvent(event);
		}
	}

	/**
	 * Validation interne d'une date selon minimumValue et maximumValue ou si date
	 * avant 01/01/0100.
	 * <p>
	 * Si elle n'est pas dans la période, renvoie une date replacée dans le siècle
	 * (même date avec l'année entre -80 ans et +20 ans par rapport à l'année
	 * courante).
	 * <p>
	 * Si cela ne suffit pas (min. 1900 ou max. 2100 ont été modifiés), renvoie min.
	 * ou max.
	 * 
	 * @return Calendar
	 * @param calendar Calendar
	 */
	protected static Calendar validateMinMaxValue(final Calendar calendar) {
		Calendar tmpCalendar = calendar;
		// pour gérer le cas de l'année 0x sur 2 chiffres,
		// l'année sur 2 chiffres est interprétée pour ajouter le siècle qui va bien
		// (en comparant à l'année courante + 20 ans, cela ajoute le siècle courant ou
		// celui d'avant)
		try {
			if (tmpCalendar.get(Calendar.YEAR) < 100 || tmpCalendar.before(getMinimumValue())
					|| tmpCalendar.after(getMaximumValue())) {

				tmpCalendar = (Calendar) tmpCalendar.clone();
				tmpCalendar.setTime(getDateFormat().parse(getDateFormat().format(calendar.getTime())));
				// getDateFormat() utilise normalement le pattern "dd/MM/yy"
				// sauf s'il a été redéfinit différemment
			}
		} catch (final ParseException e) {
			// impossible (on sait parser une date que l'on vient de formater)
			throw new IllegalStateException(e);
		}

		if (tmpCalendar.before(getMinimumValue())) {
			tmpCalendar = (Calendar) getMinimumValue().clone();
		} else if (tmpCalendar.after(getMaximumValue())) {
			tmpCalendar = (Calendar) getMaximumValue().clone();
		}

		return tmpCalendar;
	}
}
