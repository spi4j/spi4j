/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

/**
 * Interface dediee à la persistence des donnees resultantes.
 * 
 * @param <IN> Le type de donnees en entree du writer.
 * 
 * @author MINARM
 */
public interface ItemWriter_Itf<IN> extends Item_Itf {
	
	void write(IN p_in) throws Exception;
}
