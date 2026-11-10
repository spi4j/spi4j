/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.requirement;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Exemple de service.
 * @author MINARM
 */
public class MyService implements MyService_Itf
{

   private final MyServiceRequirements _requirements = new MyServiceRequirements();

   @Override
   public void methode ()
   {
      _requirements.exigence1();
   }

   @Override
   public void methodeJetantException ()
   {
      throw new Spi4jRuntimeException("Exception", "Solution");
   }

}
