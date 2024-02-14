/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.jbehave.core.model.Story;
import org.jbehave.core.parsers.RegexStoryParser;
import org.junit.jupiter.api.Test;

import fr.spi4j.lib.jmeter.jbehave.gameoflife.stories.TreeScenarii_Story;

/**
 * Test du filtre de scénario.
 * 
 * @author MINARM
 */
public class ScenarioFilteringStoryParserTest {

	/**
	 * Test du filtre de scénario.
	 * 
	 * @throws IOException erreur de lecture du fichier de story
	 */
	@Test
	public void testParseStoryString() throws IOException {
		final InputStream v_storyStream = TreeScenarii_Story.class.getResourceAsStream("TreeScenarii.story");
		assertNotNull(v_storyStream, "Impossible de lire la story");
		final String v_storyText = IOUtils.toString(v_storyStream, Charset.defaultCharset());
		final ScenarioFilteringStoryParser v_scenarioFilteringStoryParser = new ScenarioFilteringStoryParser(
				new RegexStoryParser(), "S1");
		final Story v_story = v_scenarioFilteringStoryParser.parseStory(v_storyText);
		assertEquals(1, v_story.getScenarios().size());
		assertEquals("S1", v_story.getScenarios().get(0).getTitle());
	}

}
