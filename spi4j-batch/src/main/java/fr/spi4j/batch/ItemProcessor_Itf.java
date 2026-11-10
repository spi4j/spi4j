/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

/**
 * Traite un item de type IN et le transforme en un item de type OUT.
 * 
 * @param IN  Le type de donnée en entrée.
 * @param OU> Le type de données en sortie.
 * 
 * @author MINARM
 */
public interface ItemProcessor_Itf<IN, OUT> {
	
	OUT process(IN p_in);
}
