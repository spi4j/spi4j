/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * Gestion de cache optionnelle utilisant la librarie Ehcache. <br/>
 * @param <K>
 *           Type des clés
 * @param <V>
 *           Type des valeurs
 */
class EhCache<K, V> implements Cache_Itf<K, V>
{
   /**
    * Gestionnaire Ehcache.
    */
   private static final CacheManager c_cacheManager = CacheManager.create();

   private final Ehcache _cache;

   /**
    * Constructeur.
    * @param p_cacheName
    *           Nom unique du cache
    */
   EhCache (final String p_cacheName)
   {
      super();
      // la configuration du cache sera déduite du fichier ehcache.xml présent à côté des classes
      // et selon son nom si paramétré dans ehcache.xml ou selon la configuration par défaut paramétrée dans ehcache.xml
      _cache = c_cacheManager.addCacheIfAbsent(p_cacheName);
   }

   /**
    * Purge les caches.
    */
   public static void clearAll ()
   {
      c_cacheManager.clearAll();
   }

   /**
    * Arrête définitivement tous les caches et les ressources associées.
    */
   public static void shutdown ()
   {
      c_cacheManager.shutdown();
   }

   @Override
   public Object get (final K p_key)
   {
      final Element v_element = _cache.get(p_key);
      if (v_element != null)
      {
         return v_element.getObjectValue();
      }
      return null;
   }

   @Override
   public void put (final K p_key, final V p_value)
   {
      _cache.put(new Element(p_key, p_value));
   }

   @Override
   public void remove (final K p_key)
   {
      _cache.remove(p_key);
   }

   @Override
   public void clear ()
   {
      _cache.removeAll();
   }

   @Override
   public int size ()
   {
      return _cache.getSize();
   }
}
