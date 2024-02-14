/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.spi4j.ProxyFactory_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.UserPersistence_Abs;

/**
 * Classe abstraite de gestion des services.
 * @author MINARM
 */
abstract public class UserBusiness_Abs
{
   // il est préférable d'avoir ces proxys sous forme de singletons pour ne pas réinstancer les instances et les proxys à chaque récupération
   private final Map<String, ApplicationService_Itf> _bindings = new HashMap<>();

   private ProxyFactory_Itf _proxyFactory = new DefaultServerProxyFactory(this);

   /**
    * Méthode à implémenter pour initialiser les bindings des implémentations des services.
    */
   abstract public void initBindings ();

   /**
    * Initialisation de la factory de proxies pour les services côté serveur et initialisation des bindings.
    * @param p_proxyFactory
    *           la factory de proxies
    */
   public void setProxyFactory (final ProxyFactory_Itf p_proxyFactory)
   {
      _proxyFactory = p_proxyFactory;
      initBindings();
   }

   /**
    * Importe les services d'un UserBusiness, par exemple les services d'un UserBusinessReferentiel avec les services d'un UserBusinessAppwhite.<br />
    * Cet import est nécessaire côté serveur car la RemotingServlet ne connait qu'une seule instance de UserBusiness_Abs.
    * @param p_userBusiness
    *           UserBusiness_Abs
    */
   public void importBindings (final UserBusiness_Abs p_userBusiness)
   {
      _bindings.putAll(p_userBusiness._bindings);
   }

   /**
    * Initialisation des services dans une application cliente avec délégation de l'exécution des services à un serveur par remoting.
    * @param p_serverUrl
    *           URL du serveur de la forme http://localhost:8080/myapp/remoting par exemple, <br/>
    *           où myapp est le nom du contexte de l'application appelée et où remoting est le chemin de la servlet appelée <br/>
    *           et où remoting est l'url-pattern défini dans web.xml pour la RemotingServlet.
    */
   public void initImplClient (final String p_serverUrl)
   {
      // initialisation de l'url du serveur dans la classe servant au remoting par proxy
      setProxyFactory(new DefaultClientProxyFactory(p_serverUrl));
   }

   /**
    * Retourne le UserPersistence de l'application.
    * @return le UserPersistence de l'application.
    */
   abstract protected UserPersistence_Abs getUserPersistence ();

   /**
    * Retourne l'implémentation (ou un proxy) d'un service préalablement enregistré par la méthode bind.
    * @param <T>
    *           Type du service
    * @param p_serviceInterface
    *           Classe de l'interface implémentée par le service recherché
    * @return Service
    */
   @SuppressWarnings("unchecked")
   public <T> T getBinding (final Class<T> p_serviceInterface)
   {
      ApplicationService_Itf v_result = _bindings.get(p_serviceInterface.getName());
      if (v_result == null)
      {
         if (_bindings.isEmpty())
         {
            // s'il n'y a aucun binding de service défini, on initialise par défaut les bindings des implémentations "serveur"
            // (et non les implémentations proxys de remoting pour un "client")
            initBindings();
            v_result = _bindings.get(p_serviceInterface.getName());
         }
         if (v_result == null)
         {
            throw new Spi4jRuntimeException("Binding non défini pour la classe: " + p_serviceInterface,
                     "Vérifier l'initialisation de l'application. Il manque peut-être un appel à importBindings ?");
         }
      }
      return (T) v_result;
   }

   /**
    * Enregistre l'implémentation (ou un proxy) d'un service avec comme clé la classe de l'interface implémentée par le service.
    * @param p_serviceInterface
    *           Classe de l'interface
    * @param p_service
    *           Service (ou proxy du service)
    */
   protected void bind (final Class<?> p_serviceInterface, final ApplicationService_Itf p_service)
   {
      _bindings.put(p_serviceInterface.getName(), p_service);
   }

   /**
    * Encapsule un service dans des proxies :
    * <ul>
    * <li>Côté serveur : proxy de transaction, proxy de logging si le log est activé pour ce service, proxy de cache si ce service gère un référentiel.</li>
    * <li>Côté client : proxy de remoting, proxy de logging si le log est activé pour ce service, proxy de cache si ce service gère un référentiel.</li>
    * </ul>
    * Pour l'implémentation côté serveur, on a besoin de connaître l'instance du service.
    * @param <TypeService>
    *           le type de service à encapsuler
    * @param p_interfaceService
    *           (In)(*) la classe de l'interface du service à encapsuler
    * @param p_serviceClassName
    *           (In)(*) la classe de l'instance du service à encapsuler (si côté serveur)
    * @return le service encapsulé, typé
    */
   protected final <TypeService> TypeService wrapService (final Class<TypeService> p_interfaceService,
            final String p_serviceClassName)
   {
      return _proxyFactory.getProxiedService(p_interfaceService, p_serviceClassName);
   }

   /**
    * @return la liste des instances de services
    */
   public Collection<ApplicationService_Itf> getListServices ()
   {
      return _bindings.values();
   }
}
