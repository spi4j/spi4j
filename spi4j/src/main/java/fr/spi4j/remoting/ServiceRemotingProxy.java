/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Proxy des interfaces de services pour faire des appels distants entre une
 * application cliente et un serveur (remoting sur http).
 * <p>
 * Les appels de services se font dans l'application cliente exactement comme
 * elle se ferait dans le serveur et un tunneling des appels se fait pour
 * exécuter les méthodes côté serveur.
 *
 * @param <TypeService> Type du service
 * @author MINARM
 */
public final class ServiceRemotingProxy<TypeService> implements InvocationHandler {
	static final NullResult c_nullResult = new NullResult();

	static final String c_exceptionMessageKey = "exception";

	static final String c_versionKey = "version";

	/**
	 * URL du serveur de la forme http://localhost:8080/myapp/remoting par exemple,
	 * où myapp est le nom du contexte de l'application appelée et où remoting est
	 * le chemin de la servlet appelée et où remoting est l'url-pattern défini dans
	 * web.xml pour la RemotingServlet.
	 */
	private static String serverUrl;

	private static String versionApplication;

	private static String cookie;

	private static final java.net.Proxy c_httpProxy = getProxyIfNeeded();

	private static SpiRemotingSerializer_Itf serializer = new JavaSerializer();

	private final Class<TypeService> _interfaceService;

	/**
	 * Classe interne définissant un résultat null de service (existe car null n'est
	 * pas sérialisable en tant que tel).
	 */
	static final class NullResult implements Serializable {
		private static final long serialVersionUID = 2663725802863043755L;

		/**
		 * Constructeur.
		 */
		private NullResult() {
			super();
		}
	}

	/**
	 * Classe interne définissant un résultat de type exception lancée par le
	 * service (existe pour que le client sache reconnaître sans équivoque que
	 * l'exception a été lancée par le service côté serveur).
	 */
	static class ExceptionResult extends Exception {
		private static final long serialVersionUID = -2282760048289127699L;

		private final Throwable _throwable;

		/**
		 * Constructeur.
		 *
		 * @param p_throwable Exception (ou Error)
		 */
		ExceptionResult(final Throwable p_throwable) {
			super();
			_throwable = p_throwable;
		}
	}

	/**
	 * Constructeur privé.
	 *
	 * @param p_interfaceService La classe de service qui aura du remoting
	 */
	private ServiceRemotingProxy(final Class<TypeService> p_interfaceService) {
		super();
		_interfaceService = p_interfaceService;
	}

	/**
	 * Factory de création du proxy de remoting pour le service.
	 *
	 * @param <TypeService>      Le type du service
	 * @param p_interfaceService La classe de service qui aura du remoting
	 * @return Le proxy du service avec le remoting
	 */
	@SuppressWarnings("unchecked")
	public static <TypeService> TypeService createProxy(final Class<TypeService> p_interfaceService) {
		final ServiceRemotingProxy<TypeService> v_serviceRemotingProxy = new ServiceRemotingProxy<>(p_interfaceService);
		final Class<?>[] v_interfaces = { p_interfaceService };
		return (TypeService) Proxy.newProxyInstance(p_interfaceService.getClassLoader(), v_interfaces,
				v_serviceRemotingProxy);
	}

	@Override
	public Object invoke(final Object p_proxy, final Method p_method, final Object[] p_args) throws Throwable {
		// instancie un objet Request pour indiquer au serveur quel service appelé et
		// quels sont les arguments
		final RemotingRequest v_request = new RemotingRequest(_interfaceService, p_method, p_args);

		try {
			// effectue l'appel vers le serveur
			final Object v_result = call(v_request);

			if (v_result instanceof ExceptionResult) {
				// il s'agit ici d'une exception lancée dans le service côté serveur
				// donc on relance l'exception à l'appelant du service côté client
				throw ((ExceptionResult) v_result)._throwable;
			} else if (v_result instanceof NullResult) {
				// il s'agit ici d'un résultat null (un objet null ou bien un résultat de type
				// void pour ce service)
				return null;
			}

			// sinon dans le cas général il s'agit de l'objet java en retour du service
			return v_result;
		} catch (final IOException v_ioe) {
			// si impossible de se connecter au serveur, envoyer un message clair
			throw new Spi4jRuntimeException(v_ioe, "Impossible de se connecter au serveur : " + serverUrl,
					"Vérifiez l'url du serveur");
		}
	}

	/**
	 * Effectue l'appel http vers le serveur avec l'objet request décrivant le
	 * service demandé et ses arguments.
	 *
	 * @param p_request Objet request décrivant le service demandé et ses arguments
	 * @return Résultat de l'exécution du service (éventuellement de type NullResult
	 *         ou ExceptionResult)
	 * @throws IOException Si erreur d'entrée/sortie
	 */
	private static Object call(final RemotingRequest p_request) throws IOException {
		// ouverture de la connexion http en utilisant l'implémentation Java de base
		// (il serait possible sinon d'utiliser la librairie Apache HttpClient mais cela
		// ferait une dépendance à déployer en plus)
		final URLConnection v_connection = openConnection(p_request);
		// renseigne le cookie de ce client pour avoir la session http côté serveur et
		// pour assurer un suivi de session côté serveur s'il y a un load-balancer
		if (getCookie() != null) {
			v_connection.setRequestProperty("Cookie", getCookie());
		}
		clientWrite(p_request, v_connection);

		// effectue l'appel
		v_connection.connect();

		// lit le cookie éventuellement retourné par le serveur
		final String v_setCookie = v_connection.getHeaderField("Set-Cookie");
		if (v_setCookie != null) {
			updateCookie(v_setCookie);
		}

		// lit les données retournées par le serveur
		return clientRead(v_connection);
	}

	/**
	 * Ecriture de la requête du client vers le serveur
	 *
	 * @param p_request    la requête
	 * @param p_connection l'objet connexion
	 * @throws IOException erreur de communication
	 */
	private static void clientWrite(final RemotingRequest p_request, final URLConnection p_connection)
			throws IOException {
		OutputStream v_output = p_connection.getOutputStream();
		v_output = new BufferedOutputStream(v_output);
		// On compresse le flux envoyé en gzip
		v_output = new GZIPOutputStream(v_output);

		// écrit les données envoyées par le client
		serializer.write(p_request, v_output);
	}

	/**
	 * Lecture de la réponse du serveur au client
	 *
	 * @param p_connection l'objet connexion
	 * @return l'objet envoyé par le serveur
	 * @throws IOException erreur de communication
	 */
	private static Object clientRead(final URLConnection p_connection) throws IOException {
		InputStream v_input = p_connection.getInputStream();
		v_input = new BufferedInputStream(v_input);
		if ("gzip".equals(p_connection.getContentEncoding())) {
			// si la réponse du serveur est compressée en gzip, alors on la décompresse
			v_input = new GZIPInputStream(v_input);
		}

		try {
			try {
				return serializer.read(v_input);
			} catch (final ClassNotFoundException v_ex) {
				// Si le client RDA n'a pas la classe à désérialiser dans ses jars côté client,
				// alors il y a une ClassNotFoundException.
				// En général, il s'agit d'une exception d'une couche basse côté serveur
				// (par exemple, oracle.net.ns.NetException("The network...") du driver Oracle
				// quand la base de données Oracle n'est pas accessible pour cause de coupure
				// réseau).
				// Dans ce cas, on n'ajoute pas tous les jars du serveur dans les déploiements
				// des clients, mais pour autant on essaye d'afficher le message du serveur sans
				// la trace initiale.
				// Ce message d'exception a été ajouté dans une entête http de la réponse du
				// serveur (en plus de l'exception dans le flux) par RemotingServlet
				final String v_exceptionMessage = p_connection
						.getHeaderField(ServiceRemotingProxy.c_exceptionMessageKey);
				if (v_exceptionMessage != null) {
					throw new Spi4jRuntimeException(v_exceptionMessage, "");
				} else {
					throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
				}
			}
		} finally {
			// ce close doit être fait en finally
			// (http://java.sun.com/j2se/1.5.0/docs/guide/net/http-keepalive.html)
			v_input.close();

			if (p_connection instanceof HttpURLConnection) {
				final InputStream v_error = ((HttpURLConnection) p_connection).getErrorStream();
				if (v_error != null) {
					v_error.close();
				}
			}
		}
	}

	/**
	 * Ouvre la connexion http vers le serveur sans effectuer l'appel.
	 *
	 * @param p_request Objet request décrivant le service demandé pour indiquer
	 *                  dans l'url http le nom du service
	 * @return URLConnection
	 * @throws IOException Si erreur d'entrée/sortie
	 */
	private static URLConnection openConnection(final RemotingRequest p_request) throws IOException {
		// ouvre une connexion http avec le proxy par défaut (configurable avec des
		// propriétés systèmes).
		// Remarque: même si on va écrire dans le flux envoyé l'objet "p_request" avec
		// l'interface et la méthode à appeler côté serveur,
		// on met quand même le nom simple du service et la méthode dans l'URL http afin
		// que l'on sache de quoi on parle
		// dans les logs des serveurs Apache ou Tomcat et dans un éventuel monitoring.
		final String v_serviceName;
		final int v_indexOfItf = p_request.getInterfaceServiceName().lastIndexOf("_Itf");
		if (v_indexOfItf != -1) {
			v_serviceName = p_request.getInterfaceServiceName().substring(0, v_indexOfItf);
		} else {
			v_serviceName = p_request.getInterfaceServiceName();
		}
		final URL v_url = new URL(getServerUrl() + '/' + v_serviceName + '/' + p_request.getMethodName());
		final URLConnection v_connection;
		if (c_httpProxy == null) {
			v_connection = v_url.openConnection();
		} else {
			// on aurait pu penser que openConnection() utilise automatiquement le proxy
			// http si les propriétés systèmes http.proxyHost et http.proxyPort sont
			// définies
			// mais apparemment non, c'est pourquoi on passe l'instance du proxy ici si
			// elles sont définies
			// (il est utile de les définir quand on veut enregistrer dans Apache JMeter les
			// requêtes http exécutées afin de réexécuter ces requêtes lors d'un test de
			// charge)
			v_connection = v_url.openConnection(c_httpProxy);
		}
		// method POST (et non GET)
		v_connection.setDoOutput(true);
		v_connection.setUseCaches(false);
		// connect timeout 5 minutes
		v_connection.setConnectTimeout(300000);
		// read timeout 10 minutes
		v_connection.setReadTimeout(600000);
		// type du contenu
		v_connection.setRequestProperty("Content-Type", serializer.getContentType());
		// contenu compressé en gzip
		v_connection.setRequestProperty("Content-Encoding", "gzip");
		// compression gzip acceptée
		v_connection.setRequestProperty("Accept-Encoding", "gzip");
		// la version de l'application cliente afin que le serveur puisse refuser la
		// requête d'un client non à jour
		if (versionApplication != null) {
			v_connection.setRequestProperty(c_versionKey, versionApplication);
		}
		return v_connection;
	}

	/**
	 * Retourne l'url du serveur définie pour les appels http.
	 *
	 * @return String
	 */
	public static String getServerUrl() {
		return serverUrl;
	}

	/**
	 * Définit l'URL du serveur de la forme http://localhost:8080/myapp/remoting par
	 * exemple, où myapp est le nom du contexte de l'application appelée et où
	 * remoting est le chemin de la servlet appelée et où remoting est l'url-pattern
	 * défini dans web.xml pour la RemotingServlet.
	 *
	 * @param p_serverUrl String
	 */
	public static void setServerUrl(final String p_serverUrl) {
		serverUrl = p_serverUrl;
	}

	/**
	 * Retourne le cookie éventuellement retourné par le serveur pour définir la
	 * session http associé à ce client.
	 *
	 * @return String
	 */
	private static String getCookie() {
		return cookie;
	}

	/**
	 * Définit le cookie éventuellement retourné par le serveur pour définir la
	 * session http associé à ce client.
	 *
	 * @param p_cookie String
	 */
	private static void setCookie(final String p_cookie) {
		cookie = p_cookie;
	}

	/**
	 * Met à jour le cookie qui sera utilisé par le remoting dans les appels
	 * suivants
	 *
	 * @param p_setCookie Cookie envoyé par le serveur
	 */
	private static void updateCookie(final String p_setCookie) {
		final Map<String, HttpCookie> v_cookiesByName = new LinkedHashMap<>();
		if (getCookie() != null) {
			// s'il y avait des cookies déjà on les garde s'ils n'ont pas le même nom que
			// les nouveaux
			// et ils seront écrasés par les nouveaux s'ils ont le même nom
			final List<HttpCookie> v_cookies = HttpCookie.parse(getCookie());
			for (final HttpCookie v_cookie : v_cookies) {
				v_cookiesByName.put(v_cookie.getName(), v_cookie);
			}
		}
		// "replace" pour contournement du bug Oracle 6790677 du JDK 1.6 (quelque part
		// avant 1.6.0_23)
		// http://bugs.sun.com/view_bug.do?bug_id=6790677
		final String v_setCookie = p_setCookie.replace(" HttpOnly", "");
		final List<HttpCookie> v_cookies = HttpCookie.parse(v_setCookie);
		for (final HttpCookie v_cookie : v_cookies) {
			v_cookiesByName.put(v_cookie.getName(), v_cookie);
		}
		final StringBuilder v_sb = new StringBuilder();
		for (final HttpCookie v_cookie : v_cookiesByName.values()) {
			if (v_sb.length() != 0) {
				v_sb.append("; ");
			}
			v_sb.append(v_cookie.getName()).append('=').append(v_cookie.getValue());
		}

		setCookie(v_sb.toString());
	}

	/**
	 * Définit la version de l'application.
	 *
	 * @param p_versionApplication Version de l'application
	 */
	public static void setVersionApplication(final String p_versionApplication) {
		versionApplication = p_versionApplication;
	}

	/**
	 * Si les propriétés systèmes http.proxyHost et éventuellement http.proxyPort
	 * sont définies, alors retourne l'instance de java.net.Proxy à utiliser pour
	 * les requêtes http, ou null si la propriété système http.proxyHost n'est pas
	 * défini.
	 * <p>
	 * (il est utile de les définir quand on veut enregistrer dans Apache JMeter les
	 * requêtes http exécutées afin de réexécuter ces requêtes lors d'un test de
	 * charge)
	 *
	 * @return java.net.Proxy
	 */
	private static java.net.Proxy getProxyIfNeeded() {
		final java.net.Proxy v_result;
		final String v_proxyHost = System.getProperty("http.proxyHost");
		if (v_proxyHost != null) {
			final int v_port;
			final String v_proxyPort = System.getProperty("http.proxyPort");
			if (v_proxyPort == null) {
				v_port = 80; // port 80 par défaut
			} else {
				v_port = Integer.parseInt(v_proxyPort);
			}
			v_result = new java.net.Proxy(Type.HTTP, new InetSocketAddress(v_proxyHost, v_port));
		} else {
			v_result = null;
		}
		return v_result;
	}

	/**
	 * Utilise un serializer pour transférer les données via HTTP (Défaut :
	 * JavaSerializer. Alternative : XmlSerializer)
	 *
	 * @param p_serializer le serializer
	 */
	public static void setSerializer(final SpiRemotingSerializer_Itf p_serializer) {
		serializer = p_serializer;
	}
}
