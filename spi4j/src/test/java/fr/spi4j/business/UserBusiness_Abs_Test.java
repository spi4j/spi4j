/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import fr.spi4j.testapp.MyPersonneService;
import fr.spi4j.testapp.MyPersonneService_Itf;
import fr.spi4j.testapp.MyUserBusinessGen;

/**
 * Test unitaire de la classe UserBusiness_Abs.
 * @author MINARM
 */
public class UserBusiness_Abs_Test
{
   /**
    * Test.
    */
   @Test
   public void test ()
   {
      // ce premier getInstance initialise les bindings des services "server" si cela n'a pas déjà été fait avant
      assertNotNull(MyUserBusinessGen.getInstanceOfPersonneService());

      final MyUserBusinessGen v_userBusiness = MyUserBusinessGen.getSingleton();
      assertFalse(v_userBusiness.getListServices().isEmpty());

      v_userBusiness.importBindings(v_userBusiness);
      v_userBusiness.setProxyFactory(new DefaultServerProxyFactory(v_userBusiness));
   }

   /**
    * Test.
    */
   @Test
   public void testWraps ()
   {
      final MyUserBusinessGen v_userBusiness = MyUserBusinessGen.getSingleton();
      assertNotNull(v_userBusiness.wrapService(MyPersonneService_Itf.class, MyPersonneService.class.getName()));
   }

   /**
    * Test.
    */
   @Test
   public void testUnknownBinding ()
   {
      assertThrows(Exception.class, () -> {
         MyUserBusinessGen.getSingleton().getBinding(Object.class);
      });
   }
}
