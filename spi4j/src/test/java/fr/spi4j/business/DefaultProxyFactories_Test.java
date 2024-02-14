/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Test;

import fr.spi4j.testapp.MyPersonneService;
import fr.spi4j.testapp.MyPersonneService_Itf;
import fr.spi4j.testapp.MyUserBusinessGen;

/**
 * Test unitaire des classes DefaultClientProxyFactory et DefaultServerProxyFactory.
 * @author MINARM
 */
public class DefaultProxyFactories_Test
{
   private final DefaultServerProxyFactory _defaultServerProxyFactory = new DefaultServerProxyFactory(
            MyUserBusinessGen.getSingleton());

   /**
    * Service implémentant ServiceReferentiel_Itf.
    * @author MINARM
    */
   private static class MyReferentielService extends MyPersonneService implements ServiceReferentiel_Itf
   {
      // RAS
   }

   /**
    * Test.
    */
   @Test
   public void testServerWithLogEnabled ()
   {
      final Logger v_logger = LogManager.getLogger(MyPersonneService_Itf.class.getSimpleName());
      final Level v_levelBefore = v_logger.getLevel();
      try
      {
         // avec log activé
         Configurator.setLevel(v_logger.getName(), Level.INFO);
         _defaultServerProxyFactory.getProxiedService(MyPersonneService_Itf.class, new MyPersonneService());

         // avec log désactivé
         Configurator.setLevel(v_logger.getName(), Level.WARN);
         _defaultServerProxyFactory.getProxiedService(MyPersonneService_Itf.class, new MyPersonneService());
      }
      finally
      {
         Configurator.setLevel(v_logger.getName(), v_levelBefore);
      }
   }

   /**
    * Test.
    */
   @Test
   public void testServerWithServiceReferentiel ()
   {
      // sans service référentiel
      _defaultServerProxyFactory.getProxiedService(MyPersonneService_Itf.class, new MyPersonneService());
      // avec service référentiel
      _defaultServerProxyFactory.getProxiedService(ServiceReferentiel_Itf.class, new MyReferentielService());
   }

}
