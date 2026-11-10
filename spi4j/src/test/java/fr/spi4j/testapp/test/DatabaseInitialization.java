/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp.test;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.testapp.MyParamPersistence;

/**
 * Classe d'initialisation de la base de test.
 * @author MINARM
 */
public final class DatabaseInitialization
{

   /**
    * Constructeur privé.
    */
   private DatabaseInitialization ()
   {
      super();
   }

   /**
    * Initialisation de la base (H2) pour les tests unitaires.
    */
   public static void initDatabase ()
   {
      final Dao_Itf<?, ?, ?> v_dao = MyParamPersistence.getUserPersistence().getDefaultDao();
      try
      {
         v_dao.executeUpdate("drop sequence AW_PERSONNE_SEQ", null);
      }
      catch (final Exception v_ex)
      {
         LogManager.getRootLogger().info(v_ex.toString());
      }
      try
      {
         v_dao.executeUpdate("drop table AW_PERSONNE", null);
      }
      catch (final Exception v_ex)
      {
         LogManager.getRootLogger().info(v_ex.toString());
      }
      v_dao.executeUpdate("create sequence AW_PERSONNE_SEQ start with 1000", null);
      final String v_createTablePersonne = "create table AW_PERSONNE"
               + " (PERSONNE_ID NUMBER(19) not null, NOM VARCHAR(100) not null, PRENOM VARCHAR(100), CIVIL NUMBER(1) not null, DATE_NAISSANCE DATE, SALAIRE NUMBER(14,2), VERSION TIMESTAMP not null, GRADE_ID NUMBER(19))";
      v_dao.executeUpdate(v_createTablePersonne, null);

      try
      {
         v_dao.executeUpdate("drop sequence AW_GRADE_SEQ", null);
      }
      catch (final Exception v_ex)
      {
         LogManager.getRootLogger().info(v_ex.toString());
      }
      try
      {
         v_dao.executeUpdate("drop table AW_GRADE", null);
      }
      catch (final Exception v_ex)
      {
         LogManager.getRootLogger().info(v_ex.toString());
      }

      v_dao.executeUpdate("create sequence AW_GRADE_SEQ start with 1000", null);
      final String v_createTableGrade = "create table AW_GRADE"
               + " (GRADE_ID NUMBER(19) not null, LIBELLE VARCHAR(100) not null, TRIGRAMME VARCHAR(3) not null)";
      v_dao.executeUpdate(v_createTableGrade, null);
   }
}
