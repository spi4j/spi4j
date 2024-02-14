/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test de l'entité technique créée pour la gestion d'un DAO reliée à aucune entité.
 * @author MINARM
 */
public class NoEntity_Test
{

   /**
    * Test de l'entité creuse.
    * @throws Throwable
    *            exception
    */
   @Test
   public void testEntity () throws Throwable
   {
      final NoEntity v_e = new NoEntity();
      v_e.setId("abc");
      assertEquals("abc", v_e.getId(), "Id non ok");
      v_e.validate();
   }
}
