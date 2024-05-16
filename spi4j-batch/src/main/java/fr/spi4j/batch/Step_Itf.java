/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

/**
 * Répresente une etape d'un Job.
 * 
 * @author MINARM
 */
public interface Step_Itf extends Runnable {

	/**
	 * @return L'identifiant du Step.
	 */
	String getId();
}
