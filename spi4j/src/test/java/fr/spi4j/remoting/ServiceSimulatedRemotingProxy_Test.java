/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Test;

import fr.spi4j.testapp.MyPersonneDto;
import fr.spi4j.testapp.MyPersonneService;
import fr.spi4j.testapp.MyPersonneService_Itf;

/**
 * Test unitaire de la classe ServiceSimulatedRemotingProxy.
 * @author MINARM
 */
public class ServiceSimulatedRemotingProxy_Test
{
   /**
    * Test.
    */
   @Test
   public void test ()
   {
      MyPersonneService_Itf v_myPersonneService = new MyPersonneService();
      v_myPersonneService = ServiceSimulatedRemotingProxy.createProxy(MyPersonneService_Itf.class, v_myPersonneService);

      final MyPersonneDto v_personne = new MyPersonneDto();
      v_personne.setNom("mon nom");
      v_myPersonneService.changeNom(v_personne);

      // on vérifie que le nom changé dans le DTO à l'intérieur du service n'a pas changé dans le DTO ci-dessus
      // c'est-à-dire que l'instance du DTO n'est pas la même entre celle-ci et celle dans le service (deep clone)

      assertEquals(v_personne.getNom(), "mon nom");

      boolean v_ok = false;
      try
      {
         v_myPersonneService.methodeJetantException();
      }
      catch (final Exception v_ex)
      {
         v_ok = true;
      }
      // on vérifie que l'exception dans le service remonte bien au travers du proxy
      assertTrue(v_ok, "methodeJetantException");

      // test avec log fin
      final Logger v_logger = LogManager.getLogger("SIMULATED_REMOTING");
      final Level v_initialLevel = v_logger.getLevel();

      Configurator.setLevel(v_logger.getName(), Level.TRACE);
      try
      {
         v_myPersonneService.changeNom(v_personne);
      }
      finally
      {
         Configurator.setLevel(v_logger.getName(), v_initialLevel);
      }
   }
}
