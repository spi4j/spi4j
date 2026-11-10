/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.datepicker.client.DateBox;

import fr.spi4j.ui.HasDate_Itf;

/**
 * Champs de saisie d'une date (type java.util.Date).
 * 
 * @author MINARM
 */
public class SpiDateField extends DateBox implements HasDate_Itf {
	// Les instances maximumValue et minimumValue sont partagées pour tous les
	// champs de date
	private static Date maximumValue;

	private static Date minimumValue;

	private static DateTimeFormat _dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);

	private boolean endOfDay;

	// static
	// {
	// init();
	// }

	/**
	 * Constructeur.
	 */
	public SpiDateField() {
		super();
		addStyleName("spi-date-box");
	}

	/**
	 * Constructeur.
	 * 
	 * @param p_format le format d'affichage de la date
	 */
	public SpiDateField(final Format p_format) {
		super();
		setFormat(p_format);
		addStyleName("spi-date-box");
	}

	@SuppressWarnings("deprecation")
	@Override
	public Date getValue() {
		final Date v_date = super.getValue();
		if (v_date == null) {
			return null;
		}

		// v_date = validateMinMaxValue(v_date);

		// l'heure est soit 0h soit 23h59 selon endOfDay
		// (on ne garde pas l'heure courante mais seulement le jour)
		v_date.setHours(isEndOfDay() ? 23 : 0);
		v_date.setMinutes(isEndOfDay() ? 59 : 0);
		v_date.setSeconds(0);

		return v_date;
	}

	/**
	 * Retourne la valeur de la propriété dateFormat interne.
	 * 
	 * @return SimpleDateFormat
	 * @see #setDateFormat
	 */
	protected static DateTimeFormat getDateFormat() {
		return _dateFormat;
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

	// /**
	// * Initialisation statique.
	// */
	// protected static void init ()
	// {
	// // Ces valeurs mini et maxi ne sont pas du tout limitées par Java
	// // (Date accepte avant et après).
	// // Ce sont juste des valeurs 'sensées' dans un contexte 'habituel'
	// setMinimumValue(new GregorianCalendar(1900, 0, 1).getTime());
	// setMaximumValue(new GregorianCalendar(2100, 0, 1).getTime());
	// }

	/**
	 * Retourne la valeur de la propriété endOfDay.
	 * 
	 * @return boolean
	 * @see #setEndOfDay
	 */
	public boolean isEndOfDay() {
		return endOfDay;
	}

	/**
	 * Définit la valeur de la propriété dateFormat interne.
	 * 
	 * @param newDateFormat SimpleDateFormat
	 * @see #getDateFormat
	 */
	protected static void setDateFormat(final DateTimeFormat newDateFormat) {
		_dateFormat = newDateFormat;
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

	/**
	 * Précise si le composant est modifiable ou non.
	 * 
	 * @param newEditable true si le composant doit être modifiable, false sinon
	 */
	public void setEditable(final boolean newEditable) {
		setEnabled(newEditable);
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
	 * @return Date
	 * @param p_date date
	 */
	// protected static Date validateMinMaxValue (final Date p_date)
	// {
	// Date tmpDate = p_date;
	// // pour gérer le cas de l'année 0x sur 2 chiffres,
	// // l'année sur 2 chiffres est interprétée pour ajouter le siècle qui va bien
	// // (en comparant à l'année courante + 20 ans, cela ajoute le siècle courant
	// ou celui d'avant)
	// try
	// {
	// if (tmpCalendar.get(Calendar.YEAR) < 100 ||
	// tmpCalendar.before(getMinimumValue())
	// || tmpCalendar.after(getMaximumValue()))
	// {
	//
	// tmpCalendar = (Calendar) tmpCalendar.clone();
	// tmpCalendar.setTime(getDateFormat().parse(getDateFormat().format(calendar.getTime())));
	// // getDateFormat() utilise normalement le pattern "dd/MM/yy"
	// // sauf s'il a été redéfinit différemment
	// }
	// }
	// catch (final IllegalArgumentException e)
	// {
	// // impossible (on sait parser une date que l'on vient de formater)
	// throw new IllegalStateException(e);
	// }
	//
	// if (tmpCalendar.before(getMinimumValue()))
	// {
	// tmpCalendar = (Calendar) getMinimumValue().clone();
	// }
	// else if (tmpCalendar.after(getMaximumValue()))
	// {
	// tmpCalendar = (Calendar) getMaximumValue().clone();
	// }
	//
	// return tmpCalendar;
	// }
}
