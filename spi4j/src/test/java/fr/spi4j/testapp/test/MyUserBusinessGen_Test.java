/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import fr.spi4j.ProxyFactory_Itf;
import fr.spi4j.ReflectUtil;
import fr.spi4j.persistence.TransactionProxy;
import fr.spi4j.testapp.MyParamPersistence;
import fr.spi4j.testapp.MyUserPersistence;

/**
 * Classe de test de 'MyUserPersistenceGen_Abs'.
 * @author MINARM
 */
public class MyUserBusinessGen_Test
{
   /**
    * Test de la proxyFactory des DAOs.
    */
   @Test
   public void testDaoProxyFactory ()
   {
      final MyUserPersistence v_userPersistence = MyParamPersistence.getUserPersistence();
      // initilisation d'une factory
      v_userPersistence.initDaoProxyFactory(new ProxyFactory_Itf()
      {
         @Override
         public <TypeDao> TypeDao getProxiedService (final Class<TypeDao> p_interfaceDao, final String p_daoClassName)
         {
            @SuppressWarnings("unchecked")
            TypeDao v_proxiedDao = (TypeDao) ReflectUtil.createInstance(p_daoClassName);
            v_proxiedDao = TransactionProxy.createProxy(v_userPersistence, v_proxiedDao);
            return v_proxiedDao;
         }
      });

      // utilisation de la factory
      assertNotNull(v_userPersistence.getInstanceOfPersonneDao());
      assertNotNull(v_userPersistence.getInstanceOfGradeDao());

      v_userPersistence.initDaoProxyFactory(null);
   }
}
