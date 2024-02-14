/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.cache;

/**
 * Implémentation optionnelle de la factory de caches : utilisation de la librairie Ehcache.
 * @author MINARM
 */
public class EhcacheFactory implements CacheFactory_Itf
{
   @Override
   public <TypeService, K, V> Cache_Itf<K, V> getCache (final Class<TypeService> p_interfaceService,
            final TypeService p_service)
   {
      return new EhCache<>(p_interfaceService.getName());
   }

   @Override
   public void clearAll ()
   {
      EhCache.clearAll();
   }

   @Override
   public void shutdown ()
   {
      EhCache.shutdown();
   }
}
