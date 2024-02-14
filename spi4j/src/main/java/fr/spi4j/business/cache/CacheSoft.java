/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestion de cache par défaut. <br/>
 * Les références en cache sont conservées en fonction de la place mémoire disponible.
 * @param <K>
 *           Type des clés
 * @param <V>
 *           Type des valeurs
 */
class CacheSoft<K, V> implements Cache_Itf<K, V>
{
   /** The internal Map that will hold the SoftReference. */
   private final Map<K, SoftValue<K, V>> _map = new ConcurrentHashMap<>();

   /** Reference _queue for cleared SoftReference objects. */
   private final ReferenceQueue<V> _queue = new ReferenceQueue<>();

   @Override
   public Object get (final K p_key)
   {
      final Object v_result;
      // We get the SoftReference represented by that _key
      final SoftReference<V> v_softRef = _map.get(p_key);
      if (v_softRef != null)
      {
         // From the SoftReference we get the value, which can be
         // null if it was not in the map, or it was removed in
         // the processQueue() method defined below
         v_result = v_softRef.get();
         if (v_result == null)
         {
            // If the value has been garbage collected, remove the
            // entry from the HashMap.
            _map.remove(p_key);
         }
      }
      else
      {
         v_result = null;
      }
      return v_result;
   }

   /**
    * We define our own subclass of SoftReference which contains not only the value but also the _key to make it easier to find the entry in the Map after it's been garbage collected.
    */
   private static final class SoftValue<K, V> extends SoftReference<V>
   {
      private final K _key; // always make data member final

      /**
       * Constructor.
       * @param p_value
       *           V
       * @param p_key
       *           K
       * @param p_queue
       *           ReferenceQueue
       */
      SoftValue (final V p_value, final K p_key, final ReferenceQueue<V> p_queue)
      {
         super(p_value, p_queue);
         this._key = p_key;
      }
   }

   /**
    * Here we go through the ReferenceQueue and remove garbage collected SoftValue objects from the HashMap by looking them up using the SoftValue._key data member.
    */
   @SuppressWarnings("unchecked")
   private void processQueue ()
   {
      SoftValue<K, V> v_sv = (SoftValue<K, V>) _queue.poll();
      while (v_sv != null)
      {
         _map.remove(v_sv._key); // we can access private data!
         v_sv = (SoftValue<K, V>) _queue.poll();
      }
   }

   @Override
   public void put (final K p_key, final V p_value)
   {
      processQueue(); // throw out garbage collected values first
      _map.put(p_key, new SoftValue<>(p_value, p_key, _queue));
   }

   @Override
   public void remove (final K p_key)
   {
      processQueue(); // throw out garbage collected values first
      _map.remove(p_key);
   }

   @Override
   public void clear ()
   {
      processQueue(); // throw out garbage collected values
      _map.clear();
   }

   @Override
   public int size ()
   {
      return _map.size();
   }
}
