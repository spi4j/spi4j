/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

/**
 * Implementation d'une Tasklet. Il s'agit d'un batch simple qui ne necessite
 * pas de Reader, Processor, Writer.
 * 
 * @author MINARM
 */
public abstract class Tasklet extends StepAbstract {

	protected Tasklet(String p_id) {
		super(p_id);
	}
}
