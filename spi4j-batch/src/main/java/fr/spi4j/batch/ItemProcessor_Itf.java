/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.io.IOException;

/**
 * Traite un item de type IN et le transforme en un item de type OUT.
 * 
 * @param IN  Le type de donnée en entrée.
 * @param OUT> Le type de données en sortie.
 * 
 * @author MINARM
 */
public interface ItemProcessor_Itf<IN, OUT> extends Item_Itf {
	
	OUT process(IN p_in) throws Exception;;
	
	default void close() throws IOException {}

	default void init() throws Exception {}
}
