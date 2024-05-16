/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

/**
 * Liste de l'ensemble des etats possibles pour les differents processus.
 * 
 * @author MINARM
 */
public enum TaskStateEnum {

	// Erreurs.
	error,
	// Etat initial.
	pending,
	// Etat initial.
	running,
	// Etat de traitement generique.
	reading,
	// Etat de traitement generique.
	processing,
	// Etat de traitement generique.
	writing,
	// Etat final.
	done,
	// Supprimé ou inexistant (id incorrect).
	unknown;

}
