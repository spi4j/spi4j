/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.ElemParamPersistence;
import fr.spi4j.persistence.ElemResourceManager;
import fr.spi4j.persistence.ParamPersistence_Abs;
import fr.spi4j.persistence.dao.jdbc.DefaultJdbcDao;
import fr.spi4j.persistence.resource.DefaultResourcePhysical;
import fr.spi4j.persistence.resource.ResourcePhysical_Abs;
import fr.spi4j.persistence.resource.ResourceType_Enum;
import fr.spi4j.testapp.test.Spi4JDatabaseTestHelper;

/**
 * Implémentation permettant de centraliser le paramétrage de persistance de l'application.
 * @author MINARM
 */
public final class MyParamPersistence extends ParamPersistence_Abs implements MyParamAppli
{

   private static final Logger c_log = LogManager.getLogger(MyParamPersistence.class);

   /** Chemin de la base H2 pour les tests. */
   private static final String c_cheminBaseH2 = "target/testapp_db";

   /** La ressource physique SGBD par défaut. */
   private static final ResourcePhysical_Abs c_DefaultResourcePhysical = new DefaultResourcePhysical("jdbc:h2:"
            + ".\\" + c_cheminBaseH2, "ad", "ad", ResourceType_Enum.ressourceH2NonXA);

   /**
    * Le gestionnaire de connection comprenant des ResourcePhysique et renvoyant le ResourceManager correspondant au mode de fonctionnement de l'appli.
    */
   private final ElemResourceManager _ElemResourceManager = new ElemResourceManager(c_idAppli,
            c_DefaultResourcePhysical);

   /**
    * Constructeur.
    */
   protected MyParamPersistence ()
   {
      super(c_idAppli);
   }

   @Override
   protected void afterInit ()
   {
      // suppression de l'ancienne base si elle existe encore
      final File v_dbFile = new File(c_cheminBaseH2 + ".mv.db").getAbsoluteFile();
      if (v_dbFile.exists())
      {
         c_log.info("Suppression de la base existante : " + v_dbFile.getName());
         if (!v_dbFile.delete())
         {
            c_log.warn("La base existante n'a pas pu être supprimée : " + v_dbFile.getName());
         }
      }
      try
      {
         Spi4JDatabaseTestHelper.initializeDatabase();
      }
      catch (final Throwable v_t)
      {
         c_log.error(v_t.getMessage(), v_t);
         throw new Spi4jRuntimeException(v_t, "Impossible d'initialiser la base de données",
                  "Vérifier les scripts d'initialisation de la base de données");
      }
   }

   /**
    * Paramétrage de la persistance.
    */
   @Override
   protected void initElemParamPersistence ()
   {
      // Ajouter le paramétrage pour le DAO par défaut
      setDefaultDao(DefaultJdbcDao.class, _ElemResourceManager);

      // Ajouter le paramétrage pour l'entité "Personne"
      addElemParamPersistence(MyPersonneEntity_Itf.class, new ElemParamPersistence(MyVersionnedPersonneEntity.class,
               MyPersonneDao.class, _ElemResourceManager));

      // Ajouter le paramétrage pour l'entité "Grade"
      addElemParamPersistence(MyGradeEntity_Itf.class, new ElemParamPersistence(MyGradeEntity.class, MyGradeDao.class,
               _ElemResourceManager));

      // Ajouter le paramétrage pour l'entité "Grade"
      addElemParamPersistence(MyWrongGradeEntity_Itf.class, new ElemParamPersistence(MyGradeEntity.class,
               MyWrongGradeDao.class, _ElemResourceManager));
   }

   /**
    * Initialiser les instances du paramétrage de la couche persistance.
    */
   private static synchronized void initInstance ()
   {
      MyParamPersistence v_ParamPersistence = (MyParamPersistence) getParamPersistence(c_idAppli);
      // Si pas d'instance
      if (v_ParamPersistence == null)
      {
         // Instancier 'ParamPersistenceApp'
         v_ParamPersistence = new MyParamPersistence();
         // Mémoriser l'instance 'ParamPersistence'
         setParamPersistence(c_idAppli, v_ParamPersistence);

         // Instancier un 'UserPersistenceApp'
         final MyUserPersistence v_UserPersistence = new MyUserPersistence(v_ParamPersistence);
         // Mémoriser l'instance 'UserPersistence'
         setUserPersistence(c_idAppli, v_UserPersistence);

         // Initialiser les éléments du paramétrage
         v_ParamPersistence.initElemParamPersistence();
      }
   }

   /**
    * Permet d'obtenir le 'UserPersistence' de l'application.
    * @return Une instance de 'UserPersistenceGen_Abs'
    */
   public static MyUserPersistence getUserPersistence ()
   {
      return UserPersistenceStaticHolder.c_userPersistence;
   }

   /**
    * Design pattern "Static Holder": Classe pour initialiser au besoin (c'est-à-dire à la première demande)<br/>
    * le userPersistence de l'application sans nécessiter d'ajouter "synchronized" sur la méthode static getUserPersistence().<br/>
    * Ajouter "synchronized" pourrait devenir une contention car la méthode est static et est appelée très souvent dans l'application.<br/>
    * Le Static Holder permet d'initialiser l'attribut en étant automatiquement synchronisé par l'initialisation de la classe dans le ClassLoader.<br/>
    */
   private static final class UserPersistenceStaticHolder
   {
      /** Le 'UserPersistence' de l'application. */
      private static final MyUserPersistence c_userPersistence;

      static
      {
         // Initialiser la couche de persistance
         initInstance();
         // Obtenir le 'UserPersistence' de l'application
         c_userPersistence = (MyUserPersistence) getUserPersistence(c_idAppli);
         // finalise l'initialisation du ParamPersistence
         ((MyParamPersistence) getParamPersistence(c_idAppli)).afterInit();
      }

      /**
       * Constructeur.
       */
      private UserPersistenceStaticHolder ()
      {
         super();
      }
   }
}
