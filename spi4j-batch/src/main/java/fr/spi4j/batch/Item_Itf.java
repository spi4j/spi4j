/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */

package fr.spi4j.batch;

import java.io.Closeable;

public interface Item_Itf extends Closeable {

	void init() throws Exception;
}
