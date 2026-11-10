/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Entité vide utilisée pour le DAO par défaut.
 * @author MINARM
 */
class NoEntity implements Entity_Itf<Object>
{

   private static final long serialVersionUID = 1L;

   private Object _id;

   /**
    * Constructeur masqué.
    */
   NoEntity ()
   {
      super();
   }

   @Override
   public Object getId ()
   {
      return _id;
   }

   @Override
   public void setId (final Object p_id)
   {
      _id = p_id;
   }

   @Override
   public void validate () throws Spi4jValidationException
   {
      // RAS
   }

}
