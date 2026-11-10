/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.business.cache.DefaultCacheFactory;
import fr.spi4j.business.cache.EhcacheFactory;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.testapp.MyParamPersistence;
import fr.spi4j.testapp.MyPersonneDto;
import fr.spi4j.testapp.MyPersonneEntityService;
import fr.spi4j.testapp.MyPersonneEntityService_Itf;
import fr.spi4j.testapp.MyPersonneEntity_Itf;
import fr.spi4j.testapp.MyPersonneService;
import fr.spi4j.testapp.MyPersonneService_Itf;
import fr.spi4j.testapp.MyVersionnedPersonneEntity;
import fr.spi4j.testapp.test.DatabaseInitialization;

/**
 * Test unitaire de la classe ServiceCacheProxy.
 * @author MINARM
 */
public class ServiceCacheProxy_Test
{
   /**
    * Méthode d'initialisation de la classe de tests.
    */
   @BeforeAll
   public static void setUpBeforeClass ()
   {
      DatabaseInitialization.initDatabase();
   }

   /**
    * Méthode d'initialisation de tests.
    */
   @BeforeEach
   public void setUp ()
   {
      MyParamPersistence.getUserPersistence().begin();
   }

   /**
    * Méthode de fin de test : rollback.
    */
   @AfterEach
   public void tearDown ()
   {
      MyParamPersistence.getUserPersistence().rollback();
   }

   /**
    * Test. Architecture classique avec Dto
    * @throws Spi4jValidationException
    *            e
    */
   @Test
   public void testCacheDto () throws Spi4jValidationException
   {
      MyPersonneService_Itf v_service = new MyPersonneService();
      v_service = ServiceCacheProxy.createProxy(MyPersonneService_Itf.class, v_service);
      MyPersonneDto v_personne = new MyPersonneDto();
      v_personne.setNom("Dupond");
      v_personne.setCivil(true);
      v_personne = v_service.save(v_personne);

      // mise en cache findById
      v_service.findById(v_personne.getId());
      // findById depuuis le cache
      v_service.findById(v_personne.getId());

      // mise en cache du findAll
      v_service.findAll();
      // findAll depuis le cache
      v_service.findAll();

      // mise en cache d'une méthode sans paramètre
      v_service.methode();
      // même méthode depuis le cache
      v_service.methode();
      // 2ème méthode sans paramètre depuis le même service
      v_service.methode2();
      v_service.methode2();

      // méthode qui remonte une exception
      boolean v_ok = false;
      try
      {
         v_service.methodeJetantException();
      }
      catch (final Exception v_ex)
      {
         v_ok = true;
      }
      assertTrue(v_ok, "L'exception aurait du être remontée au travers du proxy");

      // purge des caches
      ServiceCacheProxy.clearCaches();
   }

   /**
    * Test. Architecture classique avec dto
    * @throws Spi4jValidationException
    *            e
    */
   @Test
   public void testEhcacheDto () throws Spi4jValidationException
   {
      ServiceCacheProxy.initCacheFactory(new EhcacheFactory());
      try
      {
         testCacheDto();
      }
      finally
      {
         ServiceCacheProxy.initCacheFactory(new DefaultCacheFactory());
      }
   }

   /**
    * Test.
    * @throws Spi4jValidationException
    *            e
    */
   @Test
   public void testCacheEntity () throws Spi4jValidationException
   {
      MyPersonneEntityService_Itf v_service = new MyPersonneEntityService();
      v_service = ServiceCacheProxy.createProxy(MyPersonneEntityService_Itf.class, v_service);
      MyPersonneEntity_Itf v_personne = new MyVersionnedPersonneEntity();
      v_personne.setNom("Dupond");
      v_personne.setCivil(true);
      v_personne = v_service.save(v_personne);

      // mise en cache findById
      v_service.findById(v_personne.getId());
      // findById depuuis le cache
      v_service.findById(v_personne.getId());

      // mise en cache du findAll
      v_service.findAll();
      // findAll depuis le cache
      v_service.findAll();

      // mise en cache d'une méthode sans paramètre
      v_service.methode();
      // même méthode depuis le cache
      v_service.methode();
      // 2ème méthode sans paramètre depuis le même service
      v_service.methode2();
      v_service.methode2();

      // méthode qui remonte une exception
      boolean v_ok = false;
      try
      {
         v_service.methodeJetantException();
      }
      catch (final Exception v_ex)
      {
         v_ok = true;
      }
      assertTrue(v_ok, "L'exception aurait du être remontée au travers du proxy");

      // purge des caches
      ServiceCacheProxy.clearCaches();
   }

   /**
    * Test.
    * @throws Spi4jValidationException
    *            e
    */
   @Test
   public void testEhcacheEntity () throws Spi4jValidationException
   {
      ServiceCacheProxy.initCacheFactory(new EhcacheFactory());
      try
      {
         testCacheEntity();
      }
      finally
      {
         ServiceCacheProxy.initCacheFactory(new DefaultCacheFactory());
      }
   }

}
