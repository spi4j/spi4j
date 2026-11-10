/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.testapp;

import fr.spi4j.business.UserBusiness_Abs;
import fr.spi4j.persistence.UserPersistence_Abs;

/**
 * UserBusiness de test.
 * @author MINARM
 */
public final class MyUserBusiness extends UserBusiness_Abs
{

   /** Singleton. */
   private static MyUserBusiness singleton = new MyUserBusiness();

   /**
    * Constructeur privé.
    */
   private MyUserBusiness ()
   {
      super();
   }

   /**
    * Obtenir la façade de services 'PersonneService_Itf'.
    * @return L'instance désirée.
    */
   public static MyService_Itf getMyService ()
   {
      return singleton.getBinding(MyService_Itf.class);
   }

   /**
    * Obtenir la façade de services 'PersonneService_Itf'.
    * @return L'instance désirée.
    */
   public static DtoService_Itf getDtoService ()
   {
      return singleton.getBinding(DtoService_Itf.class);
   }

   /**
    * @return le singleton de cette factory
    */
   public static MyUserBusiness getSingleton ()
   {
      return singleton;
   }

   @Override
   public void initBindings ()
   {
      final MyService_Itf v_myService = wrapService(MyService_Itf.class, "fr.spi4j.lib.security.testapp.MyService");
      bind(MyService_Itf.class, v_myService);

      final DtoService_Itf v_dtoService = wrapService(DtoService_Itf.class, "fr.spi4j.lib.security.testapp.DtoService");
      bind(DtoService_Itf.class, v_dtoService);
   }

   @Override
   protected UserPersistence_Abs getUserPersistence ()
   {
      // non utilisé
      return null;
   }
}
