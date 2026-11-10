/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test de la classe PageITEXT
 * @author MINARM
 */
public class PageItext_Test
{
   /**
    * test de l'instance simple
    */
   @Test
   public void dummy ()
   {
      final PageITEXT v_PageITEXT = new PageITEXT();
      assertNotNull(v_PageITEXT);
   }
}
