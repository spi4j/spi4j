/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.tua;

import java.io.ByteArrayInputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.spi4j.Parameters;
import fr.spi4j.persistence.DatabaseLineStatus_Enum;
import fr.spi4j.persistence.dao.Binary;
import fr.spi4j.type.XtopSup;

/**
 * Classe utilitaire à hériter par les tests unitaires de beans.
 *
 * @author MINARM
 */
public abstract class BeanTester_Abs {

	private final Random _random = new Random();

	/**
	 * Positionne le paramPersistence sur la base H2 de test. <br>
	 * Comme les tests sont lancés en Junit, la JVM stoppe dès la fin des tests<br>
	 * La map des propriétés est donc automatiquemebt supprimee.
	 */
	static {
		Parameters.setH2Database();
	}

	/**
	 * @return un Integer aléatoire
	 */
	protected Integer getRandomInteger() {
		return _random.nextInt();
	}

	/**
	 * @param p_sizeLimit le nombre limite de digit pour cet entier
	 * @return un Integer aléatoire
	 */
	protected Integer getRandomInteger(final int p_sizeLimit) {
		return _random.nextInt((int) Math.pow(10, p_sizeLimit));
	}

	/**
	 * @return un Long aléatoire
	 */
	protected Long getRandomLong() {
		return _random.nextLong();
	}

	/**
	 * @param p_sizeLimit le nombre limite de digit pour cet entier
	 * @return un Long aléatoire
	 */
	protected Long getRandomLong(final int p_sizeLimit) {
		return (long) (_random.nextLong() % Math.pow(10, p_sizeLimit));
	}

	/**
	 * @return un Float aléatoire
	 */
	protected Float getRandomFloat() {
		return _random.nextFloat();
	}

	/**
	 * @return un Double aléatoire
	 */
	protected Double getRandomDouble() {
		return _random.nextDouble();
	}

	/**
	 * @return un Boolean aléatoire
	 */
	protected Boolean getRandomBoolean() {
		return _random.nextBoolean();
	}

	/**
	 * @return une Date aléatoire
	 */
	protected Date getRandomDate() {
		final Calendar v_cal = Calendar.getInstance();
		v_cal.set(_random.nextInt(120) + 1900, _random.nextInt(12), _random.nextInt(27) + 1);
		return v_cal.getTime();
	}

	/**
	 * @return un Timestamp aléatoire
	 */
	protected Timestamp getRandomTimestamp() {
		final Calendar v_cal = Calendar.getInstance();
		v_cal.set(_random.nextInt(120) + 1900, _random.nextInt(12), _random.nextInt(27) + 1, _random.nextInt(23),
				_random.nextInt(59), _random.nextInt(59));
		return new Timestamp(v_cal.getTimeInMillis());
	}

	/**
	 * @return un XtopSup aléatoire.
	 */
	protected XtopSup getRandomXtopSup() {
		final List<DatabaseLineStatus_Enum> v_enums = new ArrayList<>();
		v_enums.add(DatabaseLineStatus_Enum.active);
		v_enums.add(DatabaseLineStatus_Enum.deletedForAll);
		v_enums.add(DatabaseLineStatus_Enum.deletedForNewReference);
		v_enums.add(DatabaseLineStatus_Enum.deletedForTrash);
		return new XtopSup(v_enums.get(new Random().nextInt(v_enums.size())));
	}

	/**
	 * @return un Time aléatoire
	 */
	protected Time getRandomTime() {
		final Calendar v_cal = Calendar.getInstance();
		v_cal.set(_random.nextInt(120) + 1900, _random.nextInt(12), _random.nextInt(27) + 1, _random.nextInt(23),
				_random.nextInt(59), _random.nextInt(59));
		return new Time(v_cal.getTimeInMillis());
	}

	/**
	 * @return une String aléatoire
	 */
	protected String getRandomString() {
		return getRandomString(20);
	}

	/**
	 * @param p_sizeLimit la taille limite de la String
	 * @return une String aléatoire
	 */
	protected String getRandomString(final int p_sizeLimit) {
		// caractères lisibles de 48 ('0') à 122 ('z')
		final int v_size = _random.nextInt(p_sizeLimit) + 1;
		final StringBuilder v_str = new StringBuilder(v_size);
		for (int v_i = 0; v_i < v_size; v_i++) {
			final char v_c = Character.valueOf((char) (_random.nextInt('z' - '0') + '0'));
			v_str.append(v_c);
		}
		return v_str.toString();
	}

	/**
	 * @param <T> Type de la liste
	 * @return Liste vide
	 */
	protected <T> List<T> getRandomList() {
		return new ArrayList<>();
	}

	/**
	 * @return un Binary contenant un tableau de Byte
	 */
	protected Binary getRandomBinary() {
		final byte[] v_arrBytes = new byte[1];
		v_arrBytes[0] = (byte) 0xA1;

		final Binary v_binary = new Binary(new ByteArrayInputStream(v_arrBytes));
		return v_binary;
	}

}
