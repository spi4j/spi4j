/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.testapp;

import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jValidationException;

/**
 * DTO de test.
 * @author MINARM
 */
public class MyDto implements Dto_Itf<Long>
{

   private static final long serialVersionUID = 1L;

   @Override
   public Long getId ()
   {
      return null;
   }

   @Override
   public void setId (final Long p_id)
   {
      //
   }

   @Override
   public void validate () throws Spi4jValidationException
   {
      //
   }

}
