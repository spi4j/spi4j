/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

/**
 * Interface for any class (component) that needs to watch another class and be
 * notified when that component changes.
 *
 * @author MinArm
 */
@SuppressWarnings("javadoc")
public interface ComponentObserver {

	/**
	 * This method is called by the observable component in case of any change
	 * (event) for the observable class. It allow for the observer component to be
	 * warned and to execute any necessary action. If needed, the observer can
	 * retrieve some parameters from the observable and adjust its behavior
	 * depending on the value of these parameters
	 *
	 * @param p_params an optional list of parameters issued from the observable
	 *                 component.
	 */
	void onObservableEvent(Object... p_params);

	/**
	 * Return any parameter needed by the observable component.
	 *
	 * @return any necessary parameter for the observable component. It can also be
	 *         a collection.
	 */
	<T> T getObserverParams();
}
