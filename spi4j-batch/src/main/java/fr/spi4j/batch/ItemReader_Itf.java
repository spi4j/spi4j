/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

/**
 * Interface dediee à la persistence des donnees resultantes.
 * 
 * @param <OUT> Le type de donnees en sortie du reader.
 * 
 * @author MINARM
 */
public interface ItemReader_Itf<OUT> extends Item_Itf {

	OUT read();
}
