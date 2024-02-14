/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.persistence.UserPersistence_Abs;

/**
 * Classe abstraite de Job (Batch) pour exécuter un traitement de manière répétée et/ou programmée.
 * @author MINARM
 */
public abstract class SpiJob_Abs
{

   private final transient Timer _timer;

   private final String _name;

   /**
    * Constructeur avec initialisation du timer.
    * @param p_name
    *           le nom du job
    */
   protected SpiJob_Abs (final String p_name)
   {
      _name = p_name;
      _timer = new Timer(_name, true);
   }

   /**
    * @return Logger de ce job.
    */
   protected Logger getLogger ()
   {
      return LogManager.getLogger(getClass());
   }

   /**
    * Répète le traitement tous les jours à une heure donnée.
    * @param p_heure
    *           l'heure du traitement (0 --> 23)
    */
   public void scheduleDaily (final int p_heure)
   {
      scheduleDaily(p_heure, 0);
   }

   /**
    * Répète le traitement tous les jours à une heure donnée.
    * @param p_heure
    *           l'heure du traitement (0 --> 23)
    * @param p_minute
    *           la minute du traitement (0 --> 59)
    */
   public void scheduleDaily (final int p_heure, final int p_minute)
   {
      if (p_heure < 0 || p_heure >= 24)
      {
         throw new IllegalArgumentException("Heure incorrecte (doit être entre 0 et 23 inclus): " + p_heure);
      }
      if (p_minute < 0 || p_minute >= 60)
      {
         throw new IllegalArgumentException("Minute incorrecte (doit être entre 0 et 59 inclus): " + p_minute);
      }
      final Calendar v_calendar = Calendar.getInstance();
      v_calendar.set(Calendar.HOUR_OF_DAY, p_heure);
      v_calendar.set(Calendar.MINUTE, p_minute);
      v_calendar.set(Calendar.SECOND, 0);
      v_calendar.set(Calendar.MILLISECOND, 0);
      if (v_calendar.getTimeInMillis() < System.currentTimeMillis())
      {
         // si l'heure est déjà passé ce jour, alors la prochaine exécution est le lendemain
         // on utilise add et non roll pour ne pas tourner en boucle le 31/12
         v_calendar.add(Calendar.DAY_OF_YEAR, 1);
      }
      start(new JobConfig(true, JobConfig.c_dailyInterval, v_calendar.getTime()));
   }

   /**
    * Répète le traitement toutes les X secondes, à partir de maintenant.
    * @param p_secondes
    *           l'intervalle de temps en secondes (> 0)
    */
   public void scheduleEverySecond (final int p_secondes)
   {
      if (p_secondes <= 0)
      {
         throw new IllegalArgumentException("Intervalle de temps incorrect (doit être positif): " + p_secondes);
      }
      start(new JobConfig(true, p_secondes, new Date()));
   }

   /**
    * Programme le traitement immédiatement.
    */
   public void scheduleNow ()
   {
      scheduleAt(new Date());
   }

   /**
    * Programme le traitement à une date donnée (si la date est dans le passée, le traitement est exécuté immédiatement).
    * @param p_date
    *           la date à laquelle le traitement doit être exécuté
    */
   public void scheduleAt (final Date p_date)
   {
      if (p_date == null)
      {
         throw new IllegalArgumentException("La date du traitement ne doit pas être nulle");
      }
      start(new JobConfig(false, -1, p_date));
   }

   /**
    * Démarrage du traitement
    * @param p_config
    *           la configuration du traitement
    */
   private void start (final JobConfig p_config)
   {
      final TimerTask v_timerTask = createTimerTask(p_config);
      getLogger().info(_name + " : Prochain traitement : " + p_config._nextRun);
      _timer.schedule(v_timerTask, p_config._nextRun);
   }

   /**
    * Création de la tâche exécutée par le Timer.
    * @param p_config
    *           la config du traitement
    * @return la tâche qui sera exécutée par le Timer
    */
   protected TimerTask createTimerTask (final JobConfig p_config)
   {
      final TimerTask v_timerTask = new TimerTask()
      {
         @Override
         public void run ()
         {
            if (hasTransaction())
            {
               // Début de transaction
               getUserPersistence().begin();
            }
            final long v_start = System.currentTimeMillis();
            final Logger v_logger = getLogger();
            try
            {
               v_logger.info(_name + " : Début du traitement");
               // Exécution du traitement
               SpiJob_Abs.this.run();
               v_logger.info(_name + " : Fin du traitement en " + (System.currentTimeMillis() - v_start) + " ms");
               if (hasTransaction())
               {
                  // Commit de transaction
                  getUserPersistence().commit();
                  v_logger.info(_name + " : Transaction committée pour ce traitement");
               }
            }
            catch (final Throwable v_ex)
            {
               v_logger.warn(_name + " : Erreur lors du traitement après " + (System.currentTimeMillis() - v_start)
                        + " ms, " + v_ex.toString());
               if (hasTransaction())
               {
                  // Rollback de transaction
                  getUserPersistence().rollback();
                  v_logger.info(_name + " : Transaction rollbackée pour ce traitement");
               }
               // Gestion de l'erreur
               doOnException(v_ex);
            }
            finally
            {
               // Préparation du prochain lancement de traitement
               if (p_config._repeat)
               {
                  start(p_config.prepareNext());
               }
            }
         }
      };
      return v_timerTask;
   }

   /**
    * Méthode de gestion des erreurs
    * @param p_ex
    *           l'erreur lancée par le job
    */
   protected void doOnException (final Throwable p_ex)
   {
      getLogger().warn(p_ex.toString(), p_ex);
   }

   /**
    * Méthode abstraite du traitement.
    * @throws Throwable
    *            exception qui peut être interceptée par la méthode {@link #doOnException(Throwable)}
    */
   protected abstract void run () throws Throwable;

   /**
    * Annule les traitements. Arrête le Timer.
    */
   public void cancel ()
   {
      if (_timer != null)
      {
         // cancel sur le timer indispensable pour éviter les fuites mémoires
         _timer.cancel();
         getLogger().info(_name + " : Traitements interrompus");
      }
   }

   /**
    * Exemple : return ParamPersistenceGen_Abs.getUserPersistence();
    * @return le UserPersistence de l'application pour gérer une transaction globale sur le job
    */
   protected abstract UserPersistence_Abs getUserPersistence ();

   /**
    * @return true si une transaction doit être gérée par la tache (par défaut), ou false sinon
    */
   protected boolean hasTransaction ()
   {
      return true;
   }

   /**
    * Classe de configuration d'un Job
    * @author MINARM
    */
   private static class JobConfig
   {
      private static final int c_dailyInterval = 24 * 60 * 60;

      private final boolean _repeat;

      private final int _intervalInSeconds;

      private Date _nextRun;

      /**
       * Constructeur.
       * @param p_repeat
       *           le job doit-il se répéter ?
       * @param p_intervalInSeconds
       *           combien de secondes séparent 2 jobs (si répétition) ?
       * @param p_nextRun
       *           à quelle date doit s'exécuter le prochain traitement ?
       */
      JobConfig (final boolean p_repeat, final int p_intervalInSeconds, final Date p_nextRun)
      {
         _repeat = p_repeat;
         _intervalInSeconds = p_intervalInSeconds;
         _nextRun = p_nextRun;
      }

      /**
       * Dans le cas d'un job à répétition, prépare la configuration pour le prochain traitement
       * @return l'instance courante de configuration, mise à jour
       */
      public JobConfig prepareNext ()
      {
         if (!_repeat)
         {
            throw new IllegalStateException("Répétition non activée pour ce job");
         }
         if (_intervalInSeconds < 0)
         {
            throw new IllegalStateException("Intervalle de répétition invalide : " + _intervalInSeconds);
         }
         final Calendar v_calendar = Calendar.getInstance();
         v_calendar.setTime(_nextRun);
         // on ajoute l'intervalle défini
         // on utilise add et non roll pour ne pas tourner en boucle le 31/12
         v_calendar.add(Calendar.SECOND, _intervalInSeconds);
         _nextRun = v_calendar.getTime();
         return this;
      }
   }
}
