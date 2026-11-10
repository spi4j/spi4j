/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Chronometre simple se basant sur l'horloge de la JVM (en nanosecondes)
 * <p>
 * Particulierement utile pour mesurer le temps pris par une portion de code
 * java, Chronometre associe simplicite d'utilisation et lisibilite :
 * <blockquote>
 * 
 * <pre>
 * Chronometre chrono = new Chronometre();
 * chrono.start();
 * // .. code a mesurer
 * chono.pause();
 * // .. code a ne pas mesurer
 * chrono.release();
 * // .. code a mesurer
 * chrono.stop();
 * 
 * long duration = chrono.getTime();
 * </pre>
 * 
 * </blockquote>
 * 
 * @author MINARM
 * @see java.lang.System#currentTimeMillis()
 */
public final class Chronometre implements Serializable {

	// PS: n'utilise pas timeunit parce que a besoin de nombre à virgule.

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	/**
	 * La mis au format de nombres.
	 */
	private final DecimalFormat format;

	private long _start;
	private long _duree;

	private Date _startDate;
	private Date _stopDate;

	/**
	 * Constructeur
	 */
	public Chronometre() {
		super();

		this.format = new DecimalFormat("####00000.### ms");
		this.format.setRoundingMode(RoundingMode.HALF_EVEN);

		_duree = 0;
		_start = 0;

	}

	/**
	 * 'Demarre' le chronometre, mais fait aussi une remise à zero.
	 */
	public final void start() {
		_duree = 0;
		_start = System.nanoTime();
		_startDate = new Date();
	}

	/**
	 * 'Pause' le chronometre.
	 */
	public final void pause() {
		if (this.started()) {
			_duree += System.nanoTime() - _start;
		}
	}

	public final void release() {
		_start = System.nanoTime();
	}

	/**
	 * 'Arrete' le chronometre.
	 */
	public final void stop() {
		// permet de gerer les pauses.
		this.pause();
		_start = 0;
		_stopDate = new Date();
	}

	/**
	 * Indique la duree du timing precedent. Si le chronometre est en marche, arrete
	 * le chronometre au prealable.
	 * <p>
	 * Il est donc possible d'utiliser getTime() en lieu et place de stop();
	 * <blockquote>
	 * 
	 * <pre>
	 * Chronometre chrono = new Chronometre();
	 * chrono.start();
	 * // .. code à mesurer
	 * long duration = chrono.getTime();
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return la duree du precedent chrono en nanosecondes.
	 * @see Chronometre#start()
	 */
	public final long getTime() {
		if (this.started()) {
			stop();
		}
		return _duree;
	}

	public final double getMicroTime() {
		return getTime() / 1000.d;
	}

	public final double getMilliTime() {
		return getTime() / 1000000.d;
	}

	public final double peekMilliTime() {
		return peekTime() / 1000000.d;
	}

	public final String formatTimeMilli() {
		return this.format.format(this.getMilliTime());
	}

	public final String formatSimpleTimeMilli() {
		return Math.round(this.getMilliTime()) + " ms";
	}

	public final String formatSimpleTimeMilliMillieme() {
		return ((new Double(Math.round(this.getMilliTime() * 1000))) / 1000.d) + " ms";
	}

	public final String formatPeekTimeMilli() {
		return this.format.format(this.peekMilliTime());
	}

	/**
	 * Indique le temps imparti sans arreter le chronometre depuis le dernier start,
	 * ou depuis le dernier release();
	 * 
	 * @return la duree en nanosecondes, 0 si compteur arrete.
	 */
	public final long peekTime() {
		return (_start == 0) ? _start : System.nanoTime() - _start;
	}

	/**
	 * Indique le temps imparti sans arreter le chronometre depuis le dernier start,
	 * ou depuis les dernier release() cumule;
	 * 
	 * @return la duree en nanosecondes, 0 si compteur arrete.
	 */
	public final long peekTotalTime() {
		return (_start == 0) ? _start : _duree + (System.nanoTime() - _start);
	}

	/**
	 * Indique la date de depart du chronomètre en nanosecondes lorsque le compteur
	 * est en fonctionnement.
	 * 
	 * @return la date en nanosecondes, 0 si le chronomètre est arrete.
	 */
	public final long getStart() {
		return _start;
	}

	public final Date getLastStartDate() {
		return this._startDate;
	}

	public final Date getLastStopDate() {
		return this._stopDate;
	}

	public final boolean started() {
		return _start != 0;
	}

	/**
	 * Si chrono demarre, affiche le temps instantane. Si chrono arrete, affiche la
	 * dernière mesure.
	 */
	@Override
	public final String toString() {
		return this.started() ? this.formatPeekTimeMilli() : this.formatTimeMilli();
	}

	public void reset() {
		_duree = 0;
		_start = 0;
		_startDate = null;
		_stopDate = null;
	}
}
