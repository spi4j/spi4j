/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.persistence.dao.jdbc.DaoJdbc_Abs;

/**
 * Classe utilitaire pour gestion de base de données dans les tests.
 * @author MINARM
 */
public abstract class H2DatabaseHelper
{

   private static final Logger c_log = LogManager.getLogger(H2DatabaseHelper.class);

   private final UserPersistence_Abs _userPersistence;

   private final List<String> _scriptFilenames = new ArrayList<>();

   /**
    * Constructeur.
    * @param p_userPersistence
    *           le user persistence
    */
   protected H2DatabaseHelper (final UserPersistence_Abs p_userPersistence)
   {
      _userPersistence = p_userPersistence;
      init();
   }

   /**
    * Ajoute un script à exécuter lors de l'initialisation de la base.
    * @param p_filename
    *           le chemin du fichier
    */
   protected void addScript (final String p_filename)
   {
      _scriptFilenames.add(p_filename);
   }

   /**
    * Méthode d'initialisation appelée avant la construction de la base de données (faite pour être surchargée pour ajouter les scripts).
    */
   protected abstract void init ();

   /**
    * Initialisation de la base de données.
    */
   public void initDatabase ()
   {
      initDatabase(_userPersistence.getDefaultDao());
   }

   /**
    * Initialisation de la base de données.
    * @param p_dao
    *           Dao_Itf
    */
   public void initDatabase (final Dao_Itf<?, ?, ?> p_dao)
   {
      try
      {
         final Logger v_daoLogger = LogManager.getLogger(DaoJdbc_Abs.class);
         final Level v_level = v_daoLogger.getLevel();
         // on désactive les logs des requêtes SQL
         Configurator.setLevel(v_daoLogger.getName(), Level.INFO);
         try
         {
            for (final String v_filename : _scriptFilenames)
            {
               c_log.info("Initialisation de la base de données à partir du fichier : " + v_filename);
               if (_userPersistence == null)
               {
                  executeScript(v_filename, p_dao);
               }
               else
               {
                  _userPersistence.begin();
                  executeScript(v_filename, p_dao);
                  _userPersistence.commit();
               }
            }
         }
         finally
         {
            Configurator.setLevel(v_daoLogger.getName(), v_level);
         }
      }
      catch (final IOException v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Impossible d'initialiser la base de données",
                  "Vérifier la configuration du test");
      }
   }

   /**
    * Lit et exécute le contenu d'un fichier de script SQL.
    * @param p_filename
    *           le chemin du fichier
    * @param p_dao
    *           Dao_Itf
    * @throws IOException
    *            erreur de lecture
    */
   private void executeScript (final String p_filename, final Dao_Itf<?, ?, ?> p_dao) throws IOException
   {
      final StringBuilder v_sb = new StringBuilder();
      final Map<String, Object> v_parameters = Collections.emptyMap();

      // on cherche si le fichier est relatif à un fichier dans le classpath
      // et sinon on cherche le fichier sur le disque dur avec un chemin relatif au répertoire courant ou avec un chemin absolu
      InputStream v_input = getClass().getResourceAsStream(p_filename);
      if (v_input == null)
      {
         v_input = new FileInputStream(p_filename);
      }
      final BufferedReader v_reader = new BufferedReader(new InputStreamReader(v_input, "UTF-8"));
      try
      {
         String v_line = v_reader.readLine();
         while (v_line != null)
         {
            if (v_line.startsWith("/*"))
            {
               while (v_line != null && !v_line.endsWith("*/"))
               {
                  v_line = v_reader.readLine();
               }
            }
            else
            {
               final boolean v_lineIgnored = v_line.startsWith("--") || v_line.equalsIgnoreCase("SET DEFINE OFF;");
               if (!v_lineIgnored)
               {
                  v_sb.append(v_line);
                  if (v_line.endsWith(";"))
                  {
                     // on a une requête SQL, on l'exécute
                     final String v_sql = v_sb.toString().replace('?', '¿');
                     p_dao.executeUpdate(v_sql, v_parameters);
                     // après exécution, on vide le StringBuilder avant les lignes suivantes
                     v_sb.setLength(0);
                  }
               }
            }
            v_line = v_reader.readLine();
         }
      }
      finally
      {
         v_reader.close();
      }
   }
}
