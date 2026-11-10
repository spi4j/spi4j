/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.io.Closeable;

/**
 * 
 * @param <IN>
 * 
 * @author MINARM
 */
public interface ItemReader_Itf<IN> extends Closeable {

	void init();

	IN read();
}
