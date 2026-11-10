/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.testapp;

import fr.spi4j.business.ApplicationService_Itf;
import fr.spi4j.lib.security.annotations.Permissions;

/**
 * Service 'Applicatif' de test.
 * @author MINARM
 */
public interface MyService_Itf extends ApplicationService_Itf
{

   /**
    * Opération 1.
    */
   @Permissions("perm1")
   public void operation1 ();

   /**
    * Opération 2.
    */
   public void operation2 ();

}
