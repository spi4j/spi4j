/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.io.IOException;

import fr.spi4j.batch.exception.Spi4jTaskExecutionException;

/**
 * Implementation d'une Tasklet, un batch avec Reader, Processor, Writer.
 * 
 * @param <IN>  le type d'objet entree.
 * @param <OUT> le type d'objet en sortie.
 * 
 * @author MINARM
 */
public abstract class Chunk<IN, OUT> extends StepAbstract {

	/**
	 * Constructeur.
	 * 
	 * @param p_id : L'identifiant de l'etape.
	 */
	protected Chunk(String p_id) {
		super(p_id);
	}

	private ItemReader_Itf<IN> _reader;
	private ItemProcessor_Itf<IN, OUT> _processor;
	private ItemWriter_Itf<OUT> _writer;

	/**
	 * {@inheritDoc} <br>
	 * Doit être surcharge pour creer un environnement de connexion :
	 * 
	 * <pre>
	 * void run() {
	 * 	// avant (initialisation de la connexion)
	 * 	parent.run();
	 * 	// après (fermeture de la connexion)
	 * }
	 * </pre>
	 */
	@Override
	public void run() {

		IN v_in = null;
		_reader.init();

		boolean v_finished = false;
		while (!v_finished) {
			try {
				while ((v_in = _reader.read()) != null) {
					OUT v_out = _processor.process(v_in);
					_writer.write(v_out);
				}
				v_finished = true;
			} catch (Throwable p_e) {
				this.recordError(p_e);
			}
		}

		try {
			_reader.close();
		} catch (IOException p_e) {
			throw new Spi4jTaskExecutionException("Impossible de fermer correctement le reader.", null);
		}
	}

	public final void setReader(ItemReader_Itf<IN> p_reader) {
		_reader = p_reader;
	}

	public final void setProcessor(ItemProcessor_Itf<IN, OUT> p_processor) {
		_processor = p_processor;
	}

	public final void setWriter(ItemWriter_Itf<OUT> p_writer) {
		_writer = p_writer;
	}

	protected abstract void recordError(Throwable p_t);
}
