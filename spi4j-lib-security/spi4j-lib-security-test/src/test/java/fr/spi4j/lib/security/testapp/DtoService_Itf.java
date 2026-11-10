/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.testapp;

import fr.spi4j.business.Service_Itf;
import fr.spi4j.lib.security.annotations.Permissions;
import fr.spi4j.lib.security.annotations.PermissionsOperator_Enum;

/**
 * Service 'DTO' de test.
 * @author MINARM
 */
public interface DtoService_Itf extends Service_Itf<Long, MyDto>
{

   /**
    * Opération sur Dto 1.
    */
   @Permissions("permDto1")
   public void operationSurDto1 ();

   /**
    * Opération sur Dto 2.
    */
   public void operationSurDto2 ();

   @Override
   @Permissions(value =
   {"permDto2", "permDto3" }, operator = PermissionsOperator_Enum.AND)
   public MyDto findById (Long p_id);

}
