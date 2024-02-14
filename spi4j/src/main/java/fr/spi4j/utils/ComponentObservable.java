/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

/**
 * Interface for any class (component) that needs to be watched by another class
 * and has to notify the observer on any change. For now only one method to
 * register the observer component and thus be able to call it in case of
 * modification.
 *
 * @author MinArm
 */
public interface ComponentObservable {

	/**
	 *
	 * @param p_observer the class (component) to register to register with the
	 *                   observable class.
	 */
	public void registerObserver(ComponentObserver p_observer);
}
