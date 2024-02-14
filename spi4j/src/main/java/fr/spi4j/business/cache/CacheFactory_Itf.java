/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.cache;

/**
 * Factory de création de caches, qui sera utilisée dans ServiceCacheProxy.
 * @author MINARM
 */
public interface CacheFactory_Itf
{
   /**
    * @param <TypeService>
    *           le type de service
    * @param <K>
    *           Type des clés
    * @param <V>
    *           Type des valeurs
    * @param p_interfaceService
    *           l'interface du service
    * @param p_service
    *           l'insance du service
    * @return le service proxysé
    */
   <TypeService, K, V> Cache_Itf<K, V> getCache (final Class<TypeService> p_interfaceService,
            final TypeService p_service);

   /**
    * Purge les caches.
    */
   void clearAll ();

   /**
    * Libère les ressources.
    */
   void shutdown ();
}
