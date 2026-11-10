/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

/**
 * Interface dediee à la persistence des donnees resultantes.
 * 
 * @param <OUT> Le type de donnees en sortie du writer.
 * 
 * @author MINARM
 */
public interface ItemWriter_Itf<OUT> {

	void write(OUT p_out);
}
