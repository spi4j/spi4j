/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.graal;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import fr.spi4j.lib.jbehave.SpiStepsGiven;
import fr.spi4j.lib.jbehave.SpiStepsThen;
import fr.spi4j.lib.jbehave.SpiStepsWhen;
import fr.spi4j.lib.jbehave.SpiStory_Abs;
import fr.spi4j.lib.jbehave.graal.Sample.SampleViewsAssociation;
import fr.spi4j.ui.mvp.MVPUtils;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;
import fr.spi4j.ui.mvp.rda.RichViewManager;

/**
 * Classe abstraite de tests pour Graal, avec initialisation des steps.
 * @author MINARM
 */
public class GraalTest_Abs
{

   /**
    * Steps given.
    */
   protected static SpiStepsGiven given;

   /**
    * Steps when.
    */
   protected static SpiStepsWhen when;

   /**
    * Steps then.
    */
   protected static SpiStepsThen then;

   /**
    * Initialisation des tests.
    */
   @BeforeEach
   public void setUp ()
   {
      MVPUtils.reinit();
      MVPUtils.setViewManager(new RichViewManager());
      MVPUtils.getInstance().getViewManager().setViewsAssociation(new SampleViewsAssociation());
      given = new SpiStepsGiven(null);
      when = new SpiStepsWhen(null);
      then = new SpiStepsThen(null);
   }

   /**
    * Finalisation d'un test : suppression des présenteurs actifs.
    */
   @AfterEach
   public void tearDown ()
   {
      Collection<Presenter_Abs<? extends View_Itf, ?>> v_presenters = MVPUtils.getInstance().getViewManager()
               .getActivePresenters();
      while (!v_presenters.isEmpty())
      {
         v_presenters.iterator().next().close();
         v_presenters = MVPUtils.getInstance().getViewManager().getActivePresenters();
      }
   }

   /**
    * Affecte une story au test (pour que les steps puissent connaitre la story courante).
    * @param p_story
    *           la story
    */
   protected void setStory (final SpiStory_Abs p_story)
   {
      given = new SpiStepsGiven(p_story);
      when = new SpiStepsWhen(p_story);
      then = new SpiStepsThen(p_story);
   }

}
