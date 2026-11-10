/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

/**
 * Implementation de base pour un step avec la gestion de son identifiant.
 * 
 * @author MINARM
 */
abstract class StepAbstract implements Step_Itf {

	private String _id;

	public String getId() {
		return _id;
	}

	protected StepAbstract(final String p_id) {
		_id = p_id;
	}
}
