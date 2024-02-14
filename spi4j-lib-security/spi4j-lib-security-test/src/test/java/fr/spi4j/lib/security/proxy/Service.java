/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.proxy;

import fr.spi4j.lib.security.exception.Spi4jSecurityException;

/**
 * Service de Test
 * @author MINARM
 */
public class Service implements Service_Itf
{
   @Override
   public void orTest ()
   {
      // ras
   }

   @Override
   public void andTest ()
   {
      // ras
   }

   @Override
   public void jeterExceptionAvecCause ()
   {
      throw new Spi4jSecurityException("nouvelle erreur");
   }

   @Override
   public void maMethode (final String p_param, final int p_param2)
   {
      // ras
   }

   @Override
   public void maMethode (final String p_param, final String p_param2)
   {
      // ras
   }

   @Override
   public int maMethode ()
   {
      return 0;
   }

   @Override
   public int maMethode (final String p_param)
   {
      return 0;
   }
}
