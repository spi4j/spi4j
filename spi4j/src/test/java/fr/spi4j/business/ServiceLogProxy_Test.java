/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Test;

import fr.spi4j.persistence.dao.TableCriteria;
import fr.spi4j.testapp.MyPersonneColumns_Enum;
import fr.spi4j.testapp.MyPersonneDto;
import fr.spi4j.testapp.MyPersonneService;
import fr.spi4j.testapp.MyPersonneService_Itf;

/**
 * Test unitaire de la classe ServiceLogProxy.
 * @author MINARM
 */
public class ServiceLogProxy_Test
{
   /**
    * Test.
    */
   @Test
   @SuppressWarnings("unused")
   public void testInvoke ()
   {
      MyPersonneService_Itf v_service = new MyPersonneService();
      v_service = ServiceLogProxy.createProxy(MyPersonneService_Itf.class, v_service);
      // méthode sans paramètre
      v_service.findAll();
      // méthode avec un paramètre
      v_service.findAll(MyPersonneColumns_Enum.nom);
      // méthode avec deux paramètres
      v_service.findByColumn(MyPersonneColumns_Enum.nom, "qui a ce nom là ?");
      // méthode avec un paramètre null
      v_service.findByColumn(MyPersonneColumns_Enum.nom, null);
      // méthode avec un dto en paramètre
      v_service.changeNom(new MyPersonneDto());
      // méthode avec un paramètre dont le toString fait une erreur
      final TableCriteria<MyPersonneColumns_Enum> v_tableCriteria = new TableCriteria<>("desc")
      {
         @Override
         public String toString ()
         {
            throw new IllegalStateException("ce toString lance une exception mais le proxy de log ne doit pas planter");
         }
      };
      v_service.findByCriteria(v_tableCriteria);
      boolean v_ok = false;
      try
      {
         // méthode avec une exception
         v_service.methodeJetantException();
      }
      catch (final Exception v_ex)
      {
         v_ok = true;
      }
      // on vérifie que l'exception a bien été remontée par le proxy
      assertTrue(v_ok, "L'appel de cette méthode devrait lancer une exception");
   }

   /**
    * Test.
    */
   @Test
   public void testInvokeWithLogEnabled ()
   {
      final Logger v_logger = LogManager.getLogger(MyPersonneService_Itf.class.getSimpleName());
      final Level v_levelBefore = v_logger.getLevel();
      try
      {
         // avec log activé
         Configurator.setLevel(v_logger.getName(), Level.INFO);
         testInvoke();

         // avec log désactivé
         Configurator.setLevel(v_logger.getName(), Level.WARN);
         testInvoke();
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
   public void testIsLogEnabled ()
   {
      ServiceLogProxy.isLogEnabled(MyPersonneService_Itf.class);
   }
}
