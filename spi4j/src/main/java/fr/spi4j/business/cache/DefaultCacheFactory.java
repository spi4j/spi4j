/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.cache;

/**
 * Implémentation par défaut de la factory de caches : utilisation de CacheSoft avec des SoftReferences.
 * @author MINARM
 */
public class DefaultCacheFactory implements CacheFactory_Itf
{
   @Override
   public <TypeService, K, V> Cache_Itf<K, V> getCache (final Class<TypeService> p_interfaceService,
            final TypeService p_service)
   {
      return new CacheSoft<>();
   }

   @Override
   public void clearAll ()
   {
      // RAS
   }

   @Override
   public void shutdown ()
   {
      // RAS
   }
}
