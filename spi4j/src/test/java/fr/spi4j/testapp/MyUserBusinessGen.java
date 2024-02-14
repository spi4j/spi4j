/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import fr.spi4j.business.UserBusiness_Abs;
import fr.spi4j.persistence.UserPersistence_Abs;

/**
 * Classe permettant de centraliser les factories business de l'application.
 * @author MINARM
 */
public final class MyUserBusinessGen extends UserBusiness_Abs
{

   /** Singleton. */
   private static MyUserBusinessGen singleton = new MyUserBusinessGen();

   /**
    * Constructeur privé.
    */
   private MyUserBusinessGen ()
   {
      super();
   }

   /**
    * Obtenir la façade de services 'MyPersonneService_Itf'.
    * @return L'instance désirée.
    */
   public static MyPersonneService_Itf getInstanceOfPersonneService ()
   {
      return singleton.getBinding(MyPersonneService_Itf.class);
   }

   /**
    * @return le singleton de cette factory
    */
   public static MyUserBusinessGen getSingleton ()
   {
      return singleton;
   }

   @Override
   protected UserPersistence_Abs getUserPersistence ()
   {
      return MyParamPersistence.getUserPersistence();
   }

   @Override
   public void initBindings ()
   {
      // dans cette factory de la partie commune entre un client et le serveur
      // on ne référence pas les classes d'implémentations "serveur" des services
      // pour ne pas avoir d'erreur de compilation (et pour ne pas avoir ClassNotFoundException à l'exécution)

      final MyPersonneService_Itf v_PersonneService = wrapService(MyPersonneService_Itf.class,
               "fr.spi4j.testapp.MyPersonneService");
      bind(MyPersonneService_Itf.class, v_PersonneService);
   }
}
