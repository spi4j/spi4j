/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JUnitSampler;
import org.apache.jmeter.protocol.java.sampler.JavaSampler;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.jupiter.api.Test;

import fr.spi4j.lib.jmeter.jbehave.gameoflife.stories.ICanToggleACell_Story;

/**
 * Classe de test du Sampler JBehave.
 * 
 * @author MINARM
 */
public class SpiStorySamplerTest {

	/**
	 * Teste l'appel à un scénario.
	 */
	@Test
	public void sampleJBehaveScenario() {
		final JavaSampler v_javaSampler = new JavaSampler();
		final String v_className = SpiStorySampler.class.getName();
		v_javaSampler.setClassname(v_className);
		v_javaSampler.setName(v_className);
		final Arguments v_args = v_javaSampler.getArguments();
		v_args.addArgument(SpiStorySampler.c_ARG_EMBEDDABLE_CLASS, ICanToggleACell_Story.class.getName());
		v_args.addArgument(SpiStorySampler.c_ARG_SCENARIO_TITLE, "Toggle 3 cells");
		v_args.addArgument(SpiStorySampler.c_ARG_THREAD_NUMBER, "1");
		final SampleResult v_sample = v_javaSampler.sample(null);
		assertTrue(v_sample.isSuccessful(), "Le sampling a échoué");
	}

	/**
	 * Test d'un appel JUnit standard.
	 */
	@Test
	public void sampleJunitTest() {
		final String v_classname = SampleJunitTest.class.getName();
		final JUnitSampler v_sampler = junitSampler(v_classname, "testStuff");
		final SampleResult v_sample = v_sampler.sample(null);
		assertTrue(v_sample.isSuccessful());
	}

	/**
	 * Test d'un appel JUnit avec exception.
	 */
	@Test
	public void sampleJunitTestException() {
		final String v_classname = SampleJunitTest.class.getName();
		final JUnitSampler v_sampler = junitSampler(v_classname, "testException");
		final SampleResult v_sample = v_sampler.sample(null);
		assertTrue(v_sample.isSuccessful());
	}

	/**
	 * Prépare le lancement d'une méthode de JUnit pour les tests.
	 * 
	 * @param p_classname le nom de la classe
	 * @param p_method    le nom de la méthode
	 * @return le sampler qui va lancer le test
	 */
	protected JUnitSampler junitSampler(final String p_classname, final String p_method) {
		final JUnitSampler v_sampler = new JUnitSampler();
		v_sampler.setClassname(p_classname);
		v_sampler.setName(p_classname);
		v_sampler.setMethod(p_method);
		v_sampler.threadStarted();
		v_sampler.threadFinished();
		return v_sampler;
	}

}
