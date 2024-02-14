/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp.test;

import fr.spi4j.persistence.H2DatabaseHelper;
import fr.spi4j.testapp.MyParamPersistence;

/**
 * Classe utilitaire pour les tests unitaires de l'application blanche pour utiliser une base H2.<br/>
 * Penser à lancer les tests en positionnant la propriété test.h2 : "-Dtest.h2=true" (dans Eclipse ou Maven)
 * @author MINARM
 */
public final class Spi4JDatabaseTestHelper extends H2DatabaseHelper
{

   private static final Spi4JDatabaseTestHelper c_instance = new Spi4JDatabaseTestHelper();

   /**
    * Constructeur.
    */
   private Spi4JDatabaseTestHelper ()
   {
      super(MyParamPersistence.getUserPersistence());
   }

   @Override
   protected void init ()
   {
      // create tables
      addScript("src/test/sql/create_tables_spi4j_H2.sql");
   }

   /**
    * Méthode d'initialisation de la base de données.
    */
   public static void initializeDatabase ()
   {
      c_instance.initDatabase();
   }
}
