/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import fr.spi4j.ProxyFactory_Itf;
import fr.spi4j.remoting.ServiceRemotingProxy;

/**
 * Implémentation par défaut de la factory de proxies pour les services côté
 * client.
 *
 * @author MINARM
 */
public class DefaultClientProxyFactory implements ProxyFactory_Itf {
	/**
	 * Factory de proxy pour le client, avec en paramètre l'url du serveur distant.
	 * Exemple de paramètre serverUrl :
	 * http://localhost:8080/appwhite1-webapp/remoting
	 *
	 * @param p_server_url l'url d'accès au remoting
	 */
	public DefaultClientProxyFactory(final String p_server_url) {
		ServiceRemotingProxy.setServerUrl(p_server_url);
	}

	@Override
	public <TypeService> TypeService getProxiedService(final Class<TypeService> p_interfaceService,
			final String p_serviceClassName) {
		// côté client, le service est appelé via le mécanisme de remoting. On
		// positionne donc un premier proxy pour gérer cela.
		TypeService v_Proxy = ServiceRemotingProxy.createProxy(p_interfaceService);
		v_Proxy = getProxiedService(p_interfaceService, v_Proxy);
		return v_Proxy;
	}

	/**
	 * Ajoute des proxies sur le service côté client.
	 *
	 * @param <TypeService>      le type de service
	 * @param p_interfaceService l'interface du service
	 * @param p_serviceInstance  l'instance du service
	 * @return le service avec ses proxies positionnés
	 */
	protected <TypeService> TypeService getProxiedService(final Class<TypeService> p_interfaceService,
			final TypeService p_serviceInstance) {
		TypeService v_Proxy = p_serviceInstance;
		if (ServiceLogProxy.isEnabled() && ServiceLogProxy.isLogEnabled(p_interfaceService)) {
			v_Proxy = ServiceLogProxy.createProxy(p_interfaceService, v_Proxy);
		}

		// ajout d'un proxy de cache si nécessaire (note : p_service est null ici)
		if (ServiceReferentiel_Itf.class.isAssignableFrom(p_interfaceService)) {
			v_Proxy = ServiceCacheProxy.createProxy(p_interfaceService, v_Proxy);
		}

		return v_Proxy;
	}
}
