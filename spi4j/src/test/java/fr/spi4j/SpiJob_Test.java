/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.persistence.UserPersistence_Abs;
import fr.spi4j.testapp.MyParamPersistence;

/**
 * Classe de test d'un job.
 * @author MINARM
 */
public class SpiJob_Test
{

   private SpiJobSample _job;

   /**
    * Initialisation du job.
    */
   @BeforeEach
   public void setUp ()
   {
      _job = new SpiJobSample();
   }

   /**
    * Test journalier du job.
    * @throws Throwable
    *            erreur
    */
   @Test
   public void testDaily () throws Throwable
   {
      final Calendar v_calendar = Calendar.getInstance();
      v_calendar.add(Calendar.MINUTE, 1);
      _job.scheduleDaily(v_calendar.get(Calendar.HOUR_OF_DAY), v_calendar.get(Calendar.MINUTE));
      final int v_attente = 62 - v_calendar.get(Calendar.SECOND);
      Thread.sleep(v_attente * 1000);
      assertEquals(1, _job._calledTime, "Le job aurait du être traité une fois");
   }

   /**
    * Test toutes les X secondes du job.
    * @throws Throwable
    *            erreur
    */
   @Test
   public void testEverySecond () throws Throwable
   {
      _job.scheduleEverySecond(5);
      Thread.sleep(12000);
      assertEquals(3, _job._calledTime, "Le job aurait du être traité trois fois");
   }

   /**
    * Test toutes les X secondes du job.
    * @throws Throwable
    *            erreur
    */
   @Test
   public void testNow () throws Throwable
   {
      _job.scheduleNow();
      Thread.sleep(2);
      assertEquals(1, _job._calledTime, "Le job aurait du être traité une fois");
   }

   /**
    * Test des exceptions.
    * @throws Throwable
    *            erreur
    */
   @Test
   public void testExceptions () throws Throwable
   {
      try
      {
         _job.scheduleDaily(-1);
         fail("Exception attendue");
      }
      catch (final IllegalArgumentException v_e)
      {
         assertNotNull(v_e, "Exception attendue");
      }
      try
      {
         _job.scheduleDaily(24);
         fail("Exception attendue");
      }
      catch (final IllegalArgumentException v_e)
      {
         assertNotNull(v_e, "Exception attendue");
      }
      try
      {
         _job.scheduleDaily(2, -1);
         fail("Exception attendue");
      }
      catch (final IllegalArgumentException v_e)
      {
         assertNotNull(v_e, "Exception attendue");
      }
      try
      {
         _job.scheduleDaily(2, 60);
         fail("Exception attendue");
      }
      catch (final IllegalArgumentException v_e)
      {
         assertNotNull(v_e, "Exception attendue");
      }
      try
      {
         _job.scheduleEverySecond(0);
         fail("Exception attendue");
      }
      catch (final IllegalArgumentException v_e)
      {
         assertNotNull(v_e, "Exception attendue");
      }
      try
      {
         _job.scheduleAt(null);
         fail("Exception attendue");
      }
      catch (final IllegalArgumentException v_e)
      {
         assertNotNull(v_e, "Exception attendue");
      }

      _job._throwException = true;
      _job.scheduleNow();

      Thread.sleep(1000);
   }

   /**
    * Annulation propre du job.
    */
   @AfterEach
   public void tearDown ()
   {
      _job.cancel();
   }

   /**
    * Job d'exemple.
    * @author MINARM
    */
   private class SpiJobSample extends SpiJob_Abs
   {

      private int _calledTime;

      private boolean _throwException;

      /**
       * Constructeur.
       */
      public SpiJobSample ()
      {
         super("Sample Job");
      }

      @Override
      protected void run () throws IOException
      {
         LogManager.getLogger(getClass()).info("Exécution du Job");
         _calledTime++;
         if (_throwException)
         {
            throw new IOException("Erreur de test");
         }
      }

      @Override
      protected void doOnException (final Throwable p_ex)
      {
         super.doOnException(p_ex);
         LogManager.getLogger(getClass()).info("Une erreur est intervenue : " + p_ex.toString());
      }

      @Override
      protected UserPersistence_Abs getUserPersistence ()
      {
         return MyParamPersistence.getUserPersistence();
      }
   }

}
