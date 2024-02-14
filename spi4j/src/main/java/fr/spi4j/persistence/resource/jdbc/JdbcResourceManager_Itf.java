/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.transaction.SystemException;

/**
 * Interface commune aux JdbcResourceManager (XA et non XA)
 * @author MINARM
 */
public interface JdbcResourceManager_Itf
{
   /**
    * Booléen selon que le monitoring JavaMelody est activé.
    */
   boolean c_monitoring_enabled = Init.isMonitoringEnabled();

   /**
    * Initialisation.
    * @author MINARM
    */
   static final class Init
   {
      /**
       * Constructeur.
       */
      private Init ()
      {
         super();
      }

      /**
       * Est-ce que le monitoring est activé ? (Oui si la dépendance javamelody est présente dans le classpath)
       * @return boolean
       */
      static boolean isMonitoringEnabled ()
      {
         boolean v_return;
         try
         {
            Class.forName("net.bull.javamelody.MonitoringFilter");
            v_return = true;
         }
         catch (final Throwable v_ex)
         {
            v_return = false;
         }
         return v_return;
      }
   }

   /**
    * Permet d'obtenir la connexion JDBC du thread courant (non null). La ressource est automatiquement associée à la transaction du thread local.
    * @return La connection JDBC
    * @throws SystemException
    *            e
    */
   Connection getCurrentConnection () throws SystemException;

   /**
    * Ferme la connexion jdbc du thread courant si elle existe.
    * @throws SQLException
    *            e
    */
   void closeCurrentConnection () throws SQLException;
}
