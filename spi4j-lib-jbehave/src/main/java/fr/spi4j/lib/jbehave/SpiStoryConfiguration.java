/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import java.text.SimpleDateFormat;
import java.util.Properties;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.ParameterConverters.DateConverter;
import org.jbehave.core.steps.ParameterConverters.ExamplesTableConverter;

/**
 * Configuration des storys JBehave.
 * @author MINARM
 */
public class SpiStoryConfiguration extends MostUsefulConfiguration
{

   /**
    * Constructeur.
    * @param p_embeddableClass
    *           la classe qui contient les tests
    */
   public SpiStoryConfiguration (final Class<?> p_embeddableClass)
   {
      final Properties v_viewResources = new Properties();
      v_viewResources.put("decorateNonHtml", "true");
      // allows loading examples tables from external resources
      final ExamplesTableFactory v_examplesTableFactory = new ExamplesTableFactory(new LoadFromClasspath(
               p_embeddableClass), null);
      useStoryLoader(new LoadFromClasspath(p_embeddableClass))
               .useStoryParser(new RegexStoryParser(v_examplesTableFactory))
               .useStoryPathResolver(new SpiStoryPathResolver())
               .useStoryReporterBuilder(
                        new StoryReporterBuilder()
                                 .withCodeLocation(CodeLocations.codeLocationFromClass(p_embeddableClass))
                                 .withDefaultFormats().withViewResources(v_viewResources)
                                 .withFormats(Format.CONSOLE, Format.HTML))
               .useParameterConverters(
                        new ParameterConverters().addConverters(new DateConverter(new SimpleDateFormat("yyyy-MM-dd")), // use custom date pattern
                                 new ExamplesTableConverter(v_examplesTableFactory)));
   }

}
