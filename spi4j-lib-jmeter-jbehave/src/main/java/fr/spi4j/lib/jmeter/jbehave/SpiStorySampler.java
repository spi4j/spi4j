/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbehave.core.Embeddable;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.io.StoryPathResolver;
import org.jbehave.core.reporters.StoryReporterBuilder;

import fr.spi4j.lib.jbehave.SpiStory_Abs;

/**
 * Sampler JBehave pour JMeter.
 * @author MINARM
 */
public class SpiStorySampler implements JavaSamplerClient
{

   /**
    * c_ARG_EMBEDDABLE_CLASS
    */
   public static final String c_ARG_EMBEDDABLE_CLASS = "embeddableClass";

   /**
    * c_ARG_SCENARIO_TITLE
    */
   public static final String c_ARG_SCENARIO_TITLE = "scenarioTitle";

   /**
    * c_ARG_THREAD_NUMBER
    */
   public static final String c_ARG_THREAD_NUMBER = "threadNumber";

   private static final Logger c_log = LogManager.getRootLogger();

   private Embeddable _embeddable;

   private String _scenarioTitle;

   private SpiStory_Abs _spiStory;

   private Embedder _configuredEmbedder;

   private CachingStoryParser _storyParser;

   private CachingStoryLoader _storyLoader;

   private int _threadNum;

   @Override
   public void setupTest (final JavaSamplerContext p_context)
   {
      final String v_embeddableClassName = p_context.getParameter(c_ARG_EMBEDDABLE_CLASS);
      _scenarioTitle = p_context.getParameter(c_ARG_SCENARIO_TITLE);

      final String v_threadNumStr = p_context.getParameter(c_ARG_THREAD_NUMBER);
      try
      {
         _threadNum = Integer.parseInt(v_threadNumStr);
         c_log.info(whoAmI() + "\tNumero de thread trouvé: " + _threadNum);
      }
      catch (final NumberFormatException v_e)
      {
         _threadNum = 0;
         c_log.error(whoAmI() + "\tNumero de thread non valide: " + v_threadNumStr, v_e);
      }

      _embeddable = createEmbeddable(v_embeddableClassName);
      c_log.debug("embeddable : " + _embeddable.getClass().getName());
      if (_embeddable instanceof SpiStory_Abs)
      {
         _spiStory = (SpiStory_Abs) _embeddable;

         _spiStory.setThreadNum(_threadNum);

         _configuredEmbedder = _spiStory.configuredEmbedder();
         final EmbedderControls v_embedderControls = _configuredEmbedder.embedderControls();
         v_embedderControls.doGenerateViewAfterStories(false);

         final Configuration v_configuration = _spiStory.configuration();
         _storyLoader = new CachingStoryLoader(v_configuration.storyLoader());
         v_configuration.useStoryLoader(_storyLoader);
         if (_scenarioTitle != null && !"".equals(_scenarioTitle.trim()))
         {
            v_configuration.useStoryParser(new ScenarioFilteringStoryParser(v_configuration.storyParser(),
                     _scenarioTitle));
         }
         _storyParser = new CachingStoryParser(v_configuration.storyParser());
         v_configuration.useStoryParser(_storyParser);

         // deactivate reporting
         v_configuration.useStoryReporterBuilder(new StoryReporterBuilder());
      }
   }

   /**
    * Execute la story JBehave.
    * @param p_result
    *           l'objet encapsulant le résultat
    * @throws Throwable
    *            si une erreur est levée
    */
   protected void doRun (final SampleResult p_result) throws Throwable
   {
      c_log.info("Exécution de la story : " + _spiStory.getClass().getSimpleName());
      final StoryPathResolver v_pathResolver = _spiStory.configuration().storyPathResolver();
      final String v_storyPath = v_pathResolver.resolve(_embeddable.getClass());

      // cache story
      final String v_storyAsText = _storyLoader.loadStoryAsText(v_storyPath);
      _storyParser.parseStory(v_storyAsText, v_storyPath);

      p_result.sampleStart();
      _spiStory.setUp();
      try
      {
         _configuredEmbedder.runStoriesAsPaths(Arrays.asList(v_storyPath));
      }
      finally
      {
         _spiStory.tearDown();
         p_result.sampleEnd();
      }
   }

   @Override
   public void teardownTest (final JavaSamplerContext p_context)
   {
      c_log.debug(getClass().getName() + ": teardownTest");
   }

   /**
    * Provide a list of parameters which this test supports. Any parameter names and associated values returned by this method will appear in the GUI by default so the user doesn't have to remember the exact names. The user can add other parameters which are not listed here. If this method returns null then no parameters will be listed. If the value for some parameter is null then that parameter will be listed in the GUI with an empty value.
    * @return a specification of the parameters used by this test which should be listed in the GUI, or null if no parameters should be listed.
    */
   @Override
   public Arguments getDefaultParameters ()
   {
      final Arguments v_params = new Arguments();
      v_params.addArgument(c_ARG_EMBEDDABLE_CLASS, "");
      v_params.addArgument(c_ARG_SCENARIO_TITLE, "");
      v_params.addArgument(c_ARG_THREAD_NUMBER, "${__threadNum}");
      return v_params;
   }

   @Override
   public SampleResult runTest (final JavaSamplerContext p_ctx)
   {
      final SampleResult v_result = new SampleResult();
      try
      {
         doRun(v_result);
         v_result.setSuccessful(true);
      }
      catch (final Throwable v_e)
      {
         c_log.error(v_e.toString(), v_e);
         v_result.setSuccessful(false);
         final ByteArrayOutputStream v_baos = new ByteArrayOutputStream();
         try
         {
            final PrintStream v_printer = new PrintStream(v_baos, true, "UTF-8");
            v_e.printStackTrace(v_printer);
            v_result.setResponseMessage(v_baos.toString("UTF-8"));
         }
         catch (final UnsupportedEncodingException v_uee)
         {
            c_log.error(v_uee.toString(), v_uee);
         }
      }
      return v_result;
   }

   /**
    * Lance le test.
    * @throws Throwable
    *            Erreur dans le test
    */
   public void run () throws Throwable
   {
      runTest(null);
   }

   /**
    * Créée la classe JUnit qui correspond à la story.
    * @param p_classname
    *           le nom de la classe
    * @return la classe qui sert à lancer la story
    */
   private Embeddable createEmbeddable (final String p_classname)
   {
      Embeddable v_embeddable = null;
      try
      {
         final Class<?> v_javaClass = Class.forName(p_classname.trim(), false, Thread.currentThread()
                  .getContextClassLoader());
         v_embeddable = (Embeddable) v_javaClass.getDeclaredConstructor().newInstance();

         if (c_log.isDebugEnabled())
         {
            c_log.debug(whoAmI() + "\tCreated:\t" + p_classname + "@" + Integer.toHexString(v_embeddable.hashCode()));
         }
      }
      catch (final Exception v_e)
      {
         c_log.error(whoAmI() + "\tException creating: " + p_classname, v_e);
      }
      return v_embeddable;
   }

   /**
    * Generate a String identifier of this instance for debugging purposes.
    * @return a String identifier for this sampler instance
    */
   private String whoAmI ()
   {
      final StringBuilder v_sb = new StringBuilder();
      v_sb.append(Thread.currentThread().getName());
      v_sb.append("@");
      v_sb.append(Integer.toHexString(hashCode()));
      return v_sb.toString();
   }

}
