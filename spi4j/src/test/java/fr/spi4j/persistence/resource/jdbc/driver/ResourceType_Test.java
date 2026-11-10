/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource.jdbc.driver;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import fr.spi4j.persistence.resource.ResourceType_Enum;

/**
 * Test unitaire de la classe ResourceType_Enum.
 * @author MINARM
 */
public class ResourceType_Test
{

   /**
    * Test.
    */
   @Test
   public void test ()
   {
      for (final ResourceType_Enum v_resourceType : ResourceType_Enum.values())
      {
         assertTrue(v_resourceType == ResourceType_Enum.fromRmId(v_resourceType.getRmId()));
         assertNotNull(v_resourceType.toString());
      }
   }
}
