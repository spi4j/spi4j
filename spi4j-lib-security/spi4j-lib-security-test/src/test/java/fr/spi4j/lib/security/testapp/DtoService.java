/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.testapp;

import fr.spi4j.business.Service_Abs;
import fr.spi4j.matching.Match_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Service 'DTO' de test.
 * @author MINARM
 */
public class DtoService extends Service_Abs<Long, MyDto, DtoColumns_Enum> implements DtoService_Itf
{
   @Override
   public void operationSurDto1 ()
   {
      //
   }

   @Override
   public void operationSurDto2 ()
   {
      //
   }

   @Override
   protected Match_Itf<Long, MyDto, ? extends Entity_Itf<Long>, DtoColumns_Enum> getMatch ()
   {
      return null;
   }
}
