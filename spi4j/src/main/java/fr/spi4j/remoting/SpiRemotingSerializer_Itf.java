/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Interface des sérializers pour le remoting.
 *
 * @author MINARM
 */
public interface SpiRemotingSerializer_Itf {

	/**
	 * Ecrit dans le flux de la requête http l'objet request du service demandé avec
	 * ses arguments.
	 *
	 * @param p_request Objet request
	 * @param p_output  Flux de sortie
	 * @throws IOException Si erreur d'entrée/sortie
	 */
	void write(final Serializable p_request, final OutputStream p_output) throws IOException;

	/**
	 * Lit en retour dans le flux de la réponse http le résultat de l'exécution du
	 * service.
	 * <p>
	 * Dans les faits, c'est cette méthode qui lance l'appel http et l'exécution du
	 * service côté serveur.
	 *
	 * @param p_input Flux d'entrée
	 * @return Résultat de l'exécution du service
	 * @throws IOException            Si erreur d'entrée/sortie
	 * @throws ClassNotFoundException classe inconnue
	 */
	Object read(final InputStream p_input) throws IOException, ClassNotFoundException;

	/**
	 * @return le type de contenu (text/xml par exemple)
	 */
	String getContentType();
}
