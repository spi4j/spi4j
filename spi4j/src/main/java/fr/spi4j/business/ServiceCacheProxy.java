/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.spi4j.Identifiable_Itf;
import fr.spi4j.ReflectUtil;
import fr.spi4j.business.cache.CacheFactory_Itf;
import fr.spi4j.business.cache.Cache_Itf;
import fr.spi4j.business.cache.DefaultCacheFactory;

/**
 * Proxy des interfaces de services qui met en cache par JVM les résultats des appels de findById et de findAll.<br/>
 * (utile par exemple, pour limiter les appels du client sur le serveur sur des données du référentiel)
 * @param <TypeService>
 *           Type du service
 * @author MINARM
 */
public final class ServiceCacheProxy<TypeService> implements InvocationHandler
{
   // Caches dans la JVM de DTOs ou Entities de services "référentiel".

   // Par défaut, ces caches sont statiques en mémoire et non limités en taille,
   // et il est possible d'utiliser la librairie EhCache avec configuration possible dans un fichier ehcache.xml dont la taille :
   // ServiceCacheProxy.initCacheFactory(new EhcacheFactory());
   private static Map<Class<?>, Cache_Itf<Object, Identifiable_Itf<?>>> identifiableCacheByServiceClassAndByObjectId = new ConcurrentHashMap<>();

   private static Map<Class<?>, Cache_Itf<String, Object>> noArgsCacheByServiceClassAndByMethodName = new ConcurrentHashMap<>();

   private static CacheFactory_Itf cacheFactory = new DefaultCacheFactory();

   private final Class<TypeService> _interfaceService;

   private final TypeService _service;

   /**
    * Constructeur privé.
    * @param p_interfaceService
    *           La classe de service qui aura du cache
    * @param p_service
    *           Le service
    */
   ServiceCacheProxy (final Class<TypeService> p_interfaceService, final TypeService p_service)
   {
      super();
      _interfaceService = p_interfaceService;
      _service = p_service;
   }

   /**
    * Initialisation de la factory de caches.
    * @param p_cacheFactory
    *           la factory de caches
    */
   public static void initCacheFactory (final CacheFactory_Itf p_cacheFactory)
   {
      if (p_cacheFactory == null)
      {
         throw new IllegalArgumentException("Le paramètre 'cacheFactory' est obligatoire, il ne peut pas être 'null'\n");
      }
      cacheFactory = p_cacheFactory;
   }

   /**
    * Crée une instance d'un cache. <br/>
    * Le type de cache peut être configuré en injectant une instance de CacheFactory_Itf avec initCacheFactory.
    * @param <K>
    *           Type des clés
    * @param <V>
    *           Type des valeurs
    * @return Instance d'un cache
    */
   private <K, V> Cache_Itf<K, V> createCache ()
   {
      return cacheFactory.getCache(_interfaceService, _service);
   }

   /**
    * Libère les ressources : à appeler lors du undeploy de la webapp.
    */
   public static void shutdown ()
   {
      clearCaches();
      cacheFactory.shutdown();
   }

   /**
    * Factory de création du cache pour le service.
    * @param <TypeService>
    *           Le type du service
    * @param p_interfaceService
    *           La classe de service qui aura du cache
    * @param p_service
    *           Le service
    * @return Le proxy du service avec du cache
    */
   @SuppressWarnings("unchecked")
   public static <TypeService> TypeService createProxy (final Class<TypeService> p_interfaceService,
            final TypeService p_service)
   {
      final ServiceCacheProxy<TypeService> v_serviceCacheProxy = new ServiceCacheProxy<>(p_interfaceService, p_service);
      return (TypeService) Proxy.newProxyInstance(p_service.getClass().getClassLoader(), p_service.getClass()
               .getInterfaces(), v_serviceCacheProxy);
   }

   @Override
   public Object invoke (final Object p_proxy, final Method p_method, final Object[] p_args) throws Throwable
   {
      Object v_result = lookInCache(p_method, p_args);
      // si trouvé dans le cache, inutile d'appeler le service
      if (v_result == null)
      {
         // si non trouvé dans le cache, on appelle le service
         v_result = invokeService(p_method, p_args);

         // on met en cache
         putInCache(p_method, p_args, v_result);
      }

      return v_result;
   }

   /**
    * Interception de l'appel au service.
    * @param p_method
    *           la méthode appelée
    * @param p_args
    *           les arguments de la méthode
    * @return le résultat de la méthode
    * @throws Throwable
    *            si erreur d'invocation du service
    */
   private Object invokeService (final Method p_method, final Object[] p_args) throws Throwable
   {
      return ReflectUtil.invokeMethod(p_method, _service, p_args);
   }

   /**
    * Recherche dans le cache.
    * @param p_method
    *           La méthode appelée
    * @param p_args
    *           Les arguments de la méthode
    * @return L'objet trouvé dans le cache s'il existe, ou null si le résultat de cette méthode n'a pas été trouvé dans le cache
    */
   Object lookInCache (final Method p_method, final Object[] p_args)
   {
      // si la méthode est findById ou findAll ou une méthode sans paramètre,
      // alors on regarde dans le cache si on a le résultat (sinon on fera l'appel au service distant)
      // remarque: on pourrait généraliser ce cache à toutes les méthodes avec paramètres,
      // mais les méthodes find avec paramètres ne sont pas forcément utiles comme cache en général)
      if (p_args == null || p_args.length == 0)
      {
         // résultat de la méthode si trouvé ou null sinon
         return getNoArgsResultFromCache(p_method);
      }
      else if (p_args.length == 1 && "findById".equals(p_method.getName()))
      {
         final Object v_id = p_args[0];
         // dto si trouvé ou null sinon
         return getIdentifiableFromCache(v_id);
      }
      // non trouvé dans le cache
      return null;
   }

   /**
    * Recherche un Identifiable (Dto ou entity) dans le cache.
    * @param p_id
    *           Identifiant de l'objet
    * @return Identifiable ou null si non trouvé
    */
   private Identifiable_Itf<?> getIdentifiableFromCache (final Object p_id)
   {
      final Cache_Itf<Object, Identifiable_Itf<?>> v_cacheById = identifiableCacheByServiceClassAndByObjectId
               .get(_interfaceService);
      if (v_cacheById != null)
      {
         final Object v_result = v_cacheById.get(p_id);
         // trouvé dans le cache ou null sinon
         return (Identifiable_Itf<?>) v_result;
      }
      return null;
   }

   /**
    * Retourne le résultat d'une méthode sans paramètre depuis le cache.
    * @param p_method
    *           Méthode
    * @return Résultat ou null si le résultat n'est pas connu dans le cache
    */
   private Object getNoArgsResultFromCache (final Method p_method)
   {
      final Cache_Itf<String, Object> v_cacheByMethodName = noArgsCacheByServiceClassAndByMethodName
               .get(_interfaceService);
      if (v_cacheByMethodName != null)
      {
         final Object v_result = v_cacheByMethodName.get(p_method.getName());
         // trouvé dans le cache ou null sinon
         return v_result;
      }
      return null;
   }

   /**
    * Ajoute le résultat d'un appel dans le cache.
    * @param p_method
    *           La méthode appelée
    * @param p_args
    *           Les arguments de la méthode
    * @param p_resultOfInvoke
    *           Le résultat de la méthode
    */
   void putInCache (final Method p_method, final Object[] p_args, final Object p_resultOfInvoke)
   {
      if (p_args == null || p_args.length == 0)
      {
         // On met en cache le résultat d'une méthode sans argument
         putNoArgsResultInCache(p_method, p_resultOfInvoke);
      }
      else if ("findById".equals(p_method.getName()) && p_resultOfInvoke instanceof Identifiable_Itf)
      {
         // on met en cache l identifiable d'un findById
         putIdentifiableInCacheIfAbsent((Identifiable_Itf<?>) p_resultOfInvoke);
      }
   }

   /**
    * Ajoute un objet identifiable (Dto ou entity) en cache.
    * @param p_identifiable
    *           L'objet à rajouter en cache
    */
   private void putIdentifiableInCacheIfAbsent (final Identifiable_Itf<?> p_identifiable)
   {
      Cache_Itf<Object, Identifiable_Itf<?>> v_cacheById = identifiableCacheByServiceClassAndByObjectId
               .get(_interfaceService);
      if (v_cacheById == null)
      {
         v_cacheById = createCache();
         identifiableCacheByServiceClassAndByObjectId.put(_interfaceService, v_cacheById);
      }
      if (v_cacheById.get(p_identifiable.getId()) == null)
      {
         v_cacheById.put(p_identifiable.getId(), p_identifiable);
      }
   }

   /**
    * Ajoute en cache le résultat d'une méthode sans paramètre.
    * @param p_method
    *           Méthode
    * @param p_resultOfInvoke
    *           Résultat de la méthode
    */
   @SuppressWarnings("unchecked")
   private void putNoArgsResultInCache (final Method p_method, final Object p_resultOfInvoke)
   {
      Cache_Itf<String, Object> v_cacheByMethodName = noArgsCacheByServiceClassAndByMethodName.get(_interfaceService);
      if (v_cacheByMethodName == null)
      {
         v_cacheByMethodName = createCache();
         noArgsCacheByServiceClassAndByMethodName.put(_interfaceService, v_cacheByMethodName);
      }
      v_cacheByMethodName.put(p_method.getName(), p_resultOfInvoke);

      if ("findAll".equals(p_method.getName()) && p_resultOfInvoke instanceof Collection)
      {
         // si on a la liste des objets identifiables après findAll, autant mettre chacun des obetss dans le cache pour findById
         for (final Object v_object : (Collection<Object>) p_resultOfInvoke)
         {
            if (v_object instanceof Identifiable_Itf<?>)
            {
               putIdentifiableInCacheIfAbsent((Identifiable_Itf<?>) v_object);
            }
         }
      }
   }

   /**
    * Purge tous les caches.
    */
   public static void clearCaches ()
   {
      cacheFactory.clearAll();
      identifiableCacheByServiceClassAndByObjectId.clear();
      noArgsCacheByServiceClassAndByMethodName.clear();
   }
}
