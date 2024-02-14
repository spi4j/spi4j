/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.ReflectUtil;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Proxy des interfaces de services qui log tous les appels de services (par
 * exemple, du client sur le serveur).
 *
 * @param <TypeService> Type du service
 * @author MINARM
 */
public final class ServiceLogProxy<TypeService> implements InvocationHandler {
	private static final ThreadLocal<RequestsCounter> c_threadLocal = new ThreadLocal<>();

	private static final char[] c_padding = "        ".toCharArray();

	private static final char[] c_padding_sql = "  ".toCharArray();

	private static boolean enabled = true;

	private static boolean showSQL;

	private final String _interfaceSimpleName;

	private final Logger _logger;

	private final TypeService _service;

	/**
	 * Contexte de gestion du nombre de requêtes.
	 *
	 * @author MINARM
	 */
	private static final class RequestsCounter {
		/**
		 * indique s'il s'agit d'un compteur de service interne ou non (false par
		 * défaut)
		 */
		private boolean _inService;

		/** compteur de requêtes (initialisé à 0 par défaut) */
		private int _counter;

		/**
		 * Sette l'indicateur pour définir s'il s'agit d'un compteur de service interne.
		 *
		 * @param p_isInService true s'il s'agit d'un compteur de service interne; false
		 *                      sinon
		 */
		public void setInService(final boolean p_isInService) {
			_inService = p_isInService;
		}

		/**
		 * Indique s'il s'agit d'un compteur de service interne ou non
		 *
		 * @return true s'il s'agit d'un compteur de service interne; false sinon
		 */
		public boolean isInService() {
			return _inService;
		}

		/**
		 * Incrémente d'une unité le compteur de requêtes.
		 */
		public void increment() {
			_counter++;
		}

		/**
		 * Retourne la valeur du compteur de requêtes.
		 *
		 * @return la valeur du compteur de requêtes.
		 */
		public int getCounterValue() {
			return _counter;
		}
	}

	/**
	 * Constructeur privé.
	 *
	 * @param p_interfaceService La classe de service qui aura des logs
	 * @param p_service          Le service
	 * @param p_loggerName       Nom du logger qui sera utilisé pour le service
	 */
	ServiceLogProxy(final Class<TypeService> p_interfaceService, final TypeService p_service,
			final String p_loggerName) {
		super();
		_interfaceSimpleName = p_interfaceService.getSimpleName();
		_logger = LogManager.getLogger(p_loggerName);
		_service = p_service;
	}

	/**
	 * Factory de création des logs pour le service (le nom du logger utilisé sera
	 * le nom simple de p_interfaceService).
	 *
	 * @param <TypeService>      Le type du service
	 * @param p_interfaceService La classe de service qui aura des logs
	 * @param p_service          Le service
	 * @return Le proxy du service avec des logs
	 */
	public static <TypeService> TypeService createProxy(final Class<TypeService> p_interfaceService,
			final TypeService p_service) {
		return createProxy(p_interfaceService, p_service, p_interfaceService.getSimpleName());
	}

	/**
	 * Factory de création des logs pour le service.
	 *
	 * @param <TypeService>      Le type du service
	 * @param p_interfaceService La classe de service qui aura des logs
	 * @param p_service          Le service
	 * @param p_loggerName       Nom du logger qui sera utilisé pour le service
	 * @return Le proxy du service avec des logs
	 */
	@SuppressWarnings("unchecked")
	public static <TypeService> TypeService createProxy(final Class<TypeService> p_interfaceService,
			final TypeService p_service, final String p_loggerName) {
		final ServiceLogProxy<TypeService> v_serviceLogProxy = new ServiceLogProxy<>(p_interfaceService, p_service,
				p_loggerName);
		return (TypeService) Proxy.newProxyInstance(p_service.getClass().getClassLoader(),
				p_service.getClass().getInterfaces(), v_serviceLogProxy);
	}

	/**
	 * Vérifie si un service peut être loggé.
	 *
	 * @param p_interfaceService La classe du service
	 * @return true si le service peut être loggé, false sinon
	 */
	public static boolean isLogEnabled(final Class<?> p_interfaceService) {
		return LogManager.getLogger(p_interfaceService.getSimpleName()).isInfoEnabled();
	}

	@Override
	public Object invoke(final Object p_proxy, final Method p_method, final Object[] p_args) throws Throwable {
		// on n'utilise pas les nano-secondes car ce serait beaucoup plus coûteux
		final long v_start = System.currentTimeMillis();
		final int v_requestsCounterStart = getRequestsCounterValueForCurrentThread();
		Throwable v_throwable = null;
		final boolean v_inService = isInService();
		try {
			if (!v_inService) {
				// on flag le thread pour les services imbriqués
				setInService();
			}
			return ReflectUtil.invokeMethod(p_method, _service, p_args);
		} catch (final Throwable v_ex) {
			v_throwable = v_ex;
			throw v_ex;
		} finally {
			logMessage(p_method, p_args, v_start, v_requestsCounterStart, v_throwable);
			if (!v_inService) {
				// permet de libérer le thread
				unsetInService();
			}
		}
	}

	/**
	 * Log l'appel de la méthode avec le nom, les paramètres, le temps d'exécution
	 * et l'exception éventuelle
	 *
	 * @param p_method               Method
	 * @param p_args                 Object[]
	 * @param p_start                long
	 * @param p_requestsCounterStart le nombre de requêtes du compteur sql avant
	 *                               appel de la méthode
	 * @param p_throwable            Throwable
	 */
	void logMessage(final Method p_method, final Object[] p_args, final long p_start, final int p_requestsCounterStart,
			final Throwable p_throwable) {
		// perf: on reteste ici si le niveau de log est suffisant pour écrire ce log au
		// cas où ce niveau ait été changé à la volée après création du proxy
		if (_logger.isInfoEnabled()) {
			final StringBuilder v_msg = buildMessage(p_method, p_args, System.currentTimeMillis() - p_start,
					getRequestsCounterValueForCurrentThread() - p_requestsCounterStart, p_throwable);
			// on ne met pas l'exception complète dans cette trace, pour ne pas logguer
			// plusieurs fois la trace de l'exception
			// (la trace sera logguée ailleurs)
			_logger.info(v_msg);
		}
	}

	/**
	 * Construit le message pour le log.
	 *
	 * @param p_method        Méthode
	 * @param p_args          Arguments de la méthode
	 * @param p_duration      Durée de l'exécution de la méthode en millisecondes
	 * @param p_totalRequests le nombre total de requêtes pour le service
	 * @param p_throwable     Si non null, exception lancée par la méthode
	 * @return StringBuilder
	 */
	private StringBuilder buildMessage(final Method p_method, final Object[] p_args, final long p_duration,
			final int p_totalRequests, final Throwable p_throwable) {
		// on met la durée d'exécution dans la trace
		final String v_duration = String.valueOf(p_duration);
		final StringBuilder v_msg = new StringBuilder();
		if (v_duration.length() < 7) {
			v_msg.append(c_padding, 0, 7 - v_duration.length());
		}
		v_msg.append(v_duration).append(" ms");
		// traitement réalisé seulement si on affiche les logs sql
		if (showSQL) {
			v_msg.append(" - ");
			final String v_totalRequests = String.valueOf(p_totalRequests);
			if (v_totalRequests.length() < 2) {
				v_msg.append(c_padding_sql, 0, 2 - v_totalRequests.length());
			}
			v_msg.append(v_totalRequests).append(" SQL");
		}
		v_msg.append("; ");

		// on ajoute le nom du composant (nom simple de l'interface) et le nom de la
		// méthode
		v_msg.append(_interfaceSimpleName).append('.').append(p_method.getName());

		// on ajoute les valeurs des paramètres utilisées pour l'exécution de la méthode
		v_msg.append('(');
		if (p_args != null) {
			final int v_length = p_args.length;
			for (int v_i = 0; v_i < v_length; v_i++) {
				if (v_i != 0) {
					v_msg.append(", ");
				}
				final Object v_arg = p_args[v_i];
				if (v_arg instanceof Dto_Itf) {
					v_msg.append(v_arg.getClass().getSimpleName()).append('[').append(((Dto_Itf<?>) v_arg).getId())
							.append(']');
				} else if (v_arg instanceof Entity_Itf) {
					v_msg.append(v_arg.getClass().getSimpleName()).append('[').append(((Entity_Itf<?>) v_arg).getId())
							.append(']');
				} else if (v_arg == null) {
					v_msg.append("null");
				} else {
					try {
						v_msg.append(v_arg.toString());
					} catch (final Throwable v_e) {
						// si le toString de l'argument lance une exception, cela ne doit pas
						// interrompre le traitement dans l'application
						v_msg.append("??");
					}
				}
			}
		}
		v_msg.append(')');

		// si l'exécution de la méthode a lancée une exception, on ajoute le nom simple
		// de l'exception et son message
		if (p_throwable != null) {
			v_msg.append(" throws ").append(p_throwable.getClass().getSimpleName()).append(": ")
					.append(p_throwable.getMessage());
		}
		return v_msg;
	}

	/**
	 * Retourne le compteur de requêtes du thread courant en l'initialisatn si
	 * besoin.
	 *
	 * @return le compteur de requêtes du thread courant.
	 */
	private static RequestsCounter getRequestsCounter() {
		if (c_threadLocal.get() == null) {
			c_threadLocal.set(new RequestsCounter());
		}
		return c_threadLocal.get();
	}

	/**
	 * Retourne la valeur du compteur de requêtes du thread local.
	 *
	 * @return la valeur du compteur de requêtes (ou -1 si on n'affiche pas les logs
	 *         SQL).
	 */
	static int getRequestsCounterValueForCurrentThread() {
		// traitement réalisé seulement si on affiche les logs sql (permet de ne pas
		// instancier un thread local inutilement)
		if (showSQL) {
			return getRequestsCounter().getCounterValue();
		}
		return -1;
	}

	/**
	 * Incrémente le compteur de requêtes lié au thread local.
	 */
	public static void incrementRequestsCounterForCurrentThread() {
		// traitement réalisé seulement si on affiche les logs sql (permet de ne pas
		// instancier un thread local inutilement)
		if (showSQL) {
			getRequestsCounter().increment();
		}
	}

	/**
	 * Indique s'il faut afficher les logs SQL ou non.
	 *
	 * @param p_showSQL true pour afficher les logs SQL; false sinon
	 */
	public static void setShowSQL(final boolean p_showSQL) {
		showSQL = p_showSQL;
	}

	/**
	 * Est-ce que l'on est déjà entré dans un service pour ce thread ?
	 *
	 * @return boolean
	 */
	private static boolean isInService() {
		return getRequestsCounter().isInService();
	}

	/**
	 * Flag ce thread comme étant maintenant dans un service.
	 */
	private static void setInService() {
		getRequestsCounter().setInService(true);
	}

	/**
	 * Flag ce thread comme n'étant plus dans un service.
	 */
	private static void unsetInService() {
		c_threadLocal.remove();
	}

	/**
	 * Le proxy de log pour les services doit il être activé ?
	 *
	 * @return 'true' si le service est actif 'false' sinon.
	 */
	public static boolean isEnabled() {
		return enabled;
	}

	/**
	 * Positionne l'indicateur déctivité pour le proxy de log des services.
	 *
	 * @param enabled 'true' si le service est actif 'false' si on demande à le
	 *                désactiver.
	 */
	public static void setEnabled(final boolean enabled) {
		ServiceLogProxy.enabled = enabled;
	}
}
