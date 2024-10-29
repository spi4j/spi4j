/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

/**
 * Interface de tous les processus executables par {@link TaskManager}. Les
 * processus sont executes par des FutureTask.
 *
 * @param <RESULT> Indique la valeur de retour pour le process asynchrone.
 * 
 * @author MINARM
 */
public interface Task_Itf<RESULT> extends Runnable {
	/**
	 * @return L'état.
	 */
	TaskStateEnum getState();

	/**
	 * @return L'avancement du traitement de 0 à 100.
	 */
	int getProgression();

	/**
	 * @return Le résultat, null si non prêt.
	 */
	RESULT getResult();

	/**
	 * @return L'id du process.
	 */
	String getId();

	/**
	 * Marque le process comme déjà insérée dans un {@link TaskManager}.
	 * 
	 * @return l'uuid
	 * @throws ExpliciteTechniqueException quand exécutée plus d'une fois.
	 */
	String queue();
}
