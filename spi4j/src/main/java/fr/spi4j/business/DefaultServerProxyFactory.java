/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import fr.spi4j.ProxyFactory_Itf;
import fr.spi4j.ReflectUtil;
import fr.spi4j.persistence.TransactionProxy;
import fr.spi4j.persistence.resource.jdbc.JdbcResourceManager_Itf;
import net.bull.javamelody.MonitoringProxy;

/**
 * Implémentation par défaut de la factory de proxies pour les services côté
 * serveur.
 *
 * @author MINARM
 */
public class DefaultServerProxyFactory implements ProxyFactory_Itf {

	private final UserBusiness_Abs _userBusiness_Abs;

	/**
	 * Constructeur.
	 *
	 * @param p_userBusiness_Abs le UserBusiness de l'application
	 */
	public DefaultServerProxyFactory(final UserBusiness_Abs p_userBusiness_Abs) {
		_userBusiness_Abs = p_userBusiness_Abs;
	}

	@Override
	public <TypeService> TypeService getProxiedService(final Class<TypeService> p_interfaceService,
			final String p_serviceClassName) {
		// côté serveur, le service est son instance réelle. Il faut donc le créer.
		@SuppressWarnings("unchecked")
		TypeService v_Proxy = (TypeService) ReflectUtil.createInstance(p_serviceClassName);
		v_Proxy = getProxiedService(p_interfaceService, v_Proxy);
		return v_Proxy;
	}

	/**
	 * Ajoute des proxies sur le service côté serveur.
	 *
	 * @param <TypeService>      le type de service
	 * @param p_interfaceService l'interface du service
	 * @param p_serviceInstance  l'instance du service
	 * @return le service avec ses proxies positionnés
	 */
	protected <TypeService> TypeService getProxiedService(final Class<TypeService> p_interfaceService,
			final TypeService p_serviceInstance) {
		TypeService v_Proxy = p_serviceInstance;

		// ajout d'un proxy de transaction (seules les interfaces crud sont éligibles)
		if (p_serviceInstance instanceof Service_Itf) {
			v_Proxy = TransactionProxy.createProxy(_userBusiness_Abs.getUserPersistence(), v_Proxy);
		}

		// ajout d'un proxy de logging. Possibilité de demander la désactivation
		// complète de ce service.
		if (ServiceLogProxy.isEnabled() && ServiceLogProxy.isLogEnabled(p_interfaceService)) {
			ServiceLogProxy.setShowSQL(true); // sur le serveur on affiche les logs SQL
			v_Proxy = ServiceLogProxy.createProxy(p_interfaceService, v_Proxy);
		}

		// cela permet d'avoir accès aux statistiques des services à partir du
		// monitoring
		if (JdbcResourceManager_Itf.c_monitoring_enabled) {
			v_Proxy = MonitoringProxy.createProxy(v_Proxy, p_interfaceService.getSimpleName());
		}

		// ajout d'un proxy de cache si nécessaire
		if (p_serviceInstance instanceof ServiceReferentiel_Itf) {
			v_Proxy = ServiceCacheProxy.createProxy(p_interfaceService, v_Proxy);
		}

		return v_Proxy;
	}
}
