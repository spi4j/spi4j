/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.testapp;

import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * DTO de test.
 * @author MINARM
 */
public class MyEntity implements Entity_Itf<Long>
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
