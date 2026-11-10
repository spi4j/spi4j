/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report;

import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.model.api.IDesignEngine;

/**
 * Interface de gestion de la plateforme Birt (start/stop) et d'accès aux
 * engines.
 * <p>
 * Pour obtenir une instance, appeler BirtUtils.getInstance().
 * 
 * @author MINARM
 */
public interface BirtEngine_Itf {
	/**
	 * Permet de démarrer le moteur.
	 */
	void startPlatform();

	/**
	 * Permet d'arrêter le moteur.
	 */
	void stopPlatform();

	/**
	 * Obtenir le moteur de rapport (et si la plateforme n'a pas encore été
	 * démarrée, démarre celle-ci au besoin).
	 * 
	 * @return Le moteur de rapport.
	 */
	IReportEngine getReportEngine();

	/**
	 * Obtenir le moteur de design (et si la plateforme n'a pas encore été démarrée,
	 * démarre celle-ci au besoin).
	 * 
	 * @return Le moteur de design.
	 */
	IDesignEngine getDesignEngine();

}
