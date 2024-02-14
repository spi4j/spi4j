/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.cache;

/**
 * Interface des implémentations de cache. <br/>
 * @param <K>
 *           Type des clés
 * @param <V>
 *           Type des valeurs
 */
public interface Cache_Itf<K, V>
{
   /**
    * Récupération d'un objet mis en cache
    * @param p_key
    *           clé de l'objet
    * @return objet mis en cache, null si l'objet n'est pas en cache
    */
   Object get (final K p_key);

   /**
    * Mise en cache d'un objet identifié par sa clé.
    * @param p_key
    *           clé de l'objet
    * @param p_value
    *           objet mis en cache
    */
   void put (final K p_key, final V p_value);

   /**
    * Suppression d'un objet du cache
    * @param p_key
    *           clé de l'objet
    */
   void remove (final K p_key);

   /**
    * Purge du cache
    */
   void clear ();

   /**
    * @return Taille du cache en nombre de valeurs.
    */
   int size ();
}
