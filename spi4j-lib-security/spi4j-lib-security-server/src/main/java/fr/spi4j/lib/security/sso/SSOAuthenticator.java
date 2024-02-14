/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.sso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.spi4j.lib.security.exception.Spi4jSecurityException;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * Connecteur pour SSO.
 * 
 * @author MINARM
 */
public class SSOAuthenticator implements SSOAuthenticator_Itf {

	// private static final String c_SET_COOKIE_HEADER = "Set-Cookie";

	// private static final String c_COOKIE_HEADER = "Cookie";

	// private static final String c_METHOD_POST = "POST";

	// private static final Logger c_LOG = Logger.getLogger(SSOAuthenticator.class);

	private final String _ssoUrl;

	@SuppressWarnings("unused")
	private String _tokenCookie;

	@SuppressWarnings("unused")
	private int _timeout;

	@SuppressWarnings("unused")
	private int _connectionTimeout;

	private String _charset;

	// private final List<String> _cookiesToForward;

	/**
	 * Constructeur.
	 * 
	 * @param p_ssoUrl l'url du serveur SSO
	 */
	public SSOAuthenticator(final String p_ssoUrl) {
		_ssoUrl = p_ssoUrl;

		// timeout de 10 secondes
		_timeout = 10000;
		_connectionTimeout = 10000;

		_charset = "ISO-8859-1";

		// Initialisation des cookies à renvoyer
		// _cookiesToForward = searchCookiesToForward();

		// on ne fait plus initTokenCookie() dans le constructeur pour ne pas faire de
		// requête http à chaque lancement
		// ou lors des tests unitaires ou lors des tests d'intégration ou lors des tests
		// de charge avec des stories
		// initTokenCookie();
	}

	/**
	 * Cherche le cookie qui contient le token.
	 */
	protected void initTokenCookie() {
		try {
			_tokenCookie = findTokenCookie();
		} catch (final Spi4jSecurityException v_e) {
			// c_LOG.warn("Impossible d'initialiser le cookie contenant le token SSO", v_e);
		}
	}

	@Override
	public List<String> searchCookiesToForward() {

		final List<String> v_cookies = new ArrayList<>();

		final String v_searchCookiesUri = _ssoUrl + "/identity/getCookieNamesToForward";

		// Exécution de la requête
		final String v_reponse = doPost(v_searchCookiesUri);

		// Les résultats sont sur plusieurs lignes :
		// string=iPlanetDirectoryPro
		// string=amlbcookie
		final String[] v_lines = v_reponse.split("\n");
		for (final String v_line : v_lines) {
			// Le nom du cookie se trouve après le =
			final String[] v_splitline = v_line.split("=", 2);
			if (v_splitline.length < 2) {
				throw new IllegalArgumentException("Unknown cookie to forward : " + v_line);
			}
			// Pour l'instant on ne connait que le nom des cookies à renvoyer, mais pas leur
			// valeur
			v_cookies.add(v_splitline[1]);
		}

		return v_cookies;
	}

	/**
	 * @return le nom du cookie qui contiendra le token
	 */
	protected String findTokenCookie() {
		final String v_findTokenCookie = _ssoUrl + "/identity/getCookieNameForToken";

		// Exécution de la requête
		final String v_reponse = doPost(v_findTokenCookie);

		final String v_line = v_reponse.split("\n")[0];
		final String[] v_splitline = v_line.split("=", 2);
		if (v_splitline.length < 2) {
			throw new IllegalArgumentException("Unknown cookie to forward : " + v_line);
		}
		return v_splitline[1];

	}

	@Override
	public void logout(final SSOCookies p_cookies) {
		final String v_logoutUri = _ssoUrl + "/UI/Logout";
		try {
			doGetWithCookiesAsParameters(v_logoutUri, p_cookies);
		} catch (final Exception v_e) {
			// TODO Auto-generated catch block
			v_e.printStackTrace();
		}
	}

	// @Override
	// public SSOCookies login (final String p_user, final String p_password)
	// {
	// final String v_loginUri = _ssoUrl + "/UI/Login";
	//
	// // Exécution de la requête
	// // IDToken1 doit contenir le login de l'utilisateur
	// // IDToken2 doit contenir le mot de passe de l'utilisateur
	// final Map<String, String> v_parameters = new HashMap<>(2);
	// // encodage des chaines en UTF-8
	// final String v_encodingChargset = "UTF-8";
	// try
	// {
	// v_parameters.put("IDToken1", URLEncoder.encode(p_user, v_encodingChargset));
	// v_parameters.put("IDToken2", URLEncoder.encode(p_password,
	// v_encodingChargset));
	// }
	// catch (final UnsupportedEncodingException v_e)
	// {
	// throw new Spi4jRuntimeException("Charset inconnu pour connexion avec le SSO",
	// "Vérifier le charset utilisé lors de la connexion avec le SSO");
	// }
	//
	// final SSOCookies v_cookies = new SSOCookies();
	// doPost(v_loginUri, true, v_cookies, v_parameters);
	//
	// // si le cookie contenant le token n'a pas été initialisé
	// if (_tokenCookie == null)
	// {
	// // initialise le cookie contenant le token
	// initTokenCookie();
	// // si le cookie n'a toujours pas été trouvé
	// if (_tokenCookie == null)
	// {
	// // lancer une exception !
	// throw new Spi4jSecurityException("Cookie pour le token SSO inconnu");
	// }
	// }
	// final String v_token = v_cookies.get(_tokenCookie);
	// if (v_token == null)
	// {
	// throw new Spi4jSecurityException("La connexion de l'utilisateur " + p_user +
	// " a échoué");
	// }
	// return v_cookies;
	// }

	@Override
	public SSOCookies login(final String p_user, final String p_password) {
		// final String v_loginUri = _ssoUrl + "/json/authenticate";

		// Exécution de la requête
		// IDToken1 doit contenir le login de l'utili sateur
		// IDToken2 doit contenir le mot de passe de l'utilisateur
		// final MultivaluedMap<String, String> v_parameters = new MultivaluedMapImpl();
		// encodage des chaines en UTF-8
		// final String v_encodingChargset = "UTF-8";
		// try
		// {
		// v_parameters.add("username", URLEncoder.encode(p_user, v_encodingChargset));
		// v_parameters.add("password", URLEncoder.encode(p_password,
		// v_encodingChargset));
		// }
		// catch (final UnsupportedEncodingException v_e)
		// {
		// throw new Spi4jRuntimeException("Charset inconnu pour connexion avec le SSO",
		// "Vérifier le charset utilisé lors de la connexion avec le SSO");
		// }
		//
		// final SSOCookies v_cookies = new SSOCookies();
		// doPost(v_loginUri, true, v_cookies, v_parameters);

		// si le cookie contenant le token n'a pas été initialisé
		// if (_tokenCookie == null)
		// {
		// // initialise le cookie contenant le token
		// initTokenCookie();
		// // si le cookie n'a toujours pas été trouvé
		// if (_tokenCookie == null)
		// {
		// // lancer une exception !
		// throw new Spi4jSecurityException("Cookie pour le token SSO inconnu");
		// }
		// }
		// final String v_token = v_cookies.get(_tokenCookie);
		// if (v_token == null)
		// {
		// throw new Spi4jSecurityException("La connexion de l'utilisateur " + p_user +
		// " a échoué");
		// }
		// return v_cookies;
		return null;
	}

	@Override
	public boolean checkTokenValidity(final SSOCookies p_cookies) {
		final String v_checkTokenValidityUri = _ssoUrl + "/identity/isTokenValid";

		String v_reponse = null;
		try {
			v_reponse = doGetWithCookiesAsParameters(v_checkTokenValidityUri, p_cookies);
		} catch (final Exception v_e) {
			// TODO Auto-generated catch block
			v_e.printStackTrace();
		}

		// La réponse est "boolean=true" ou "boolean=false"
		// On lit dont ce qu'il y a après le = pour le parser en booléen
		if (v_reponse != null) {
			final String[] v_split = v_reponse.split("=", 2);
			if (v_split.length < 2) {
				throw new IllegalArgumentException("Unknown response for /identity/isTokenValid : " + v_reponse);
			}
			final String v_boolStr = v_split[1].trim();
			return Boolean.parseBoolean(v_boolStr);
		} else {
			return false;
		}
	}

	@Override
	public SSOUserAttributes getAttributes(final SSOCookies p_cookies) {
		final String v_attributesUri = _ssoUrl + "/identity/attributes";

		String v_reponse = null;
		try {
			v_reponse = doGetWithCookiesAsParameters(v_attributesUri, p_cookies);
		} catch (final Exception v_e) {
			// TODO Auto-generated catch block
			v_e.printStackTrace();
		}

		final SSOUserAttributes v_SSOUserAttributes = new SSOUserAttributes();
		String v_name = null;
		if (v_reponse != null) {
			final String[] v_lines = v_reponse.split("\n");
			if (v_lines != null) {
				for (final String v_line : v_lines) {
					final String[] v_split = v_line.trim().split("=", 2);
					if (v_split.length == 2) {
						final String v_key = v_split[0];
						final String v_value = v_split[1];
						if ("userdetails.token.id".equals(v_key)) {
							v_SSOUserAttributes.setTokenId(v_value);
						} else if ("userdetails.role".equals(v_key)) {
							v_SSOUserAttributes.getRoles().add(new SSOUserAttributes.Role(v_value));
						} else if ("userdetails.attribute.name".equals(v_key)) {
							v_name = v_value;
							v_SSOUserAttributes.createAttributeListByName(v_name);
						} else if ("userdetails.attribute.value".equals(v_key)) {
							v_SSOUserAttributes.getAttributeListByName(v_name).add(v_value);
						}
					}
				}
			}
		}
		return v_SSOUserAttributes;
	}

	/**
	 * Exécute une requête vers une URL en envoyant les cookies stockés sous forme
	 * de paramètres de requête
	 * 
	 * @param p_url     l'url
	 * @param p_cookies les cookies de l'utilisateur
	 * @return le contenu de la réponse du serveur
	 * @throws Exception exception si la connexion est impossible
	 */
	// protected String doGetWithCookiesAsParameters (final String p_url, final
	// SSOCookies p_cookies)
	// {
	// HttpURLConnection v_connection = null;
	// try
	// {
	// final URL v_url = new URL(p_url);
	// v_connection = (HttpURLConnection) v_url.openConnection();
	// // timeout
	// v_connection.setConnectTimeout(_connectionTimeout);
	// v_connection.setReadTimeout(_timeout);
	// addCookiesToConnection(p_cookies, v_connection);
	//
	// // envoi de la requête
	// v_connection.connect();
	//
	// // lecture de la réponse
	// return readResponse(v_connection);
	// }
	// catch (final IOException v_e)
	// {
	// throw new Spi4jSecurityException("Impossible de se connecter, serveur SSO
	// inaccessible : " + v_e.getMessage(),
	// v_e);
	// }
	// finally
	// {
	// if (v_connection != null)
	// {
	// v_connection.disconnect();
	// }
	// }
	// }

	protected String doGetWithCookiesAsParameters(final String p_url, final SSOCookies p_cookies) throws Exception {

		// try
		// {
		// final Client v_client = Client.create();
		// final WebResource v_webResource = v_client.resource(p_url);
		// final String v_cookieParam = addCookiesToConnection(p_cookies);
		//
		// final ClientResponse v_response = v_webResource.header("Content-Type",
		// "application/json;charset=UTF-8")
		// .header(c_COOKIE_HEADER, v_cookieParam)
		// .post(ClientResponse.class);
		//
		// // lecture de la réponse
		// return readResponse(v_response);
		// }
		// catch (final IOException v_e)
		// {
		// throw new Spi4jSecurityException("Impossible de se connecter, serveur SSO
		// inaccessible : " + v_e.getMessage(),
		// v_e);
		// }
		return null;
	}

	/**
	 * Exécution d'une requête POST
	 * 
	 * @param p_url                    l'adresse de la requête
	 * @param p_updateCookiesToForward true si les cookies à renvoyer doivent être
	 *                                 mis à jour
	 * @param p_cookies                les cookies de l'utilisateur
	 * @param p_parametersBody         les paramètres de la requête, qui seront
	 *                                 envoyés dans le body de la requête
	 * @return la réponse du serveur
	 */
	// protected String doPost (final String p_url, final boolean
	// p_updateCookiesToForward, final SSOCookies p_cookies,
	// final Map<String, String> p_parametersBody)
	// {
	// HttpURLConnection v_connection = null;
	// try
	// {
	// final URL v_url = new URL(p_url);
	// v_connection = (HttpURLConnection) v_url.openConnection();
	// // timeout
	// v_connection.setConnectTimeout(_connectionTimeout);
	// v_connection.setReadTimeout(_timeout);
	// // la méthode est POST
	// v_connection.setRequestMethod(c_METHOD_POST);
	//
	// // ajout des paramètres dans la requête
	// addParametersPost(p_parametersBody, v_connection);
	//
	// // on ne suit pas les redirections (les cookies se trouvent dans la première
	// réponse)
	// v_connection.setInstanceFollowRedirects(false);
	//
	// // envoi de la requête
	// v_connection.connect();
	//
	// if (p_updateCookiesToForward)
	// {
	// // récupération des cookies dans la réponse pour stocker le token
	// updateCookiesToForward(v_connection.getHeaderFields().get(c_SET_COOKIE_HEADER),
	// p_cookies);
	// }
	// return readResponse(v_connection);
	// }
	// catch (final IOException v_e)
	// {
	// throw new Spi4jSecurityException("Impossible de se connecter, serveur SSO
	// inaccessible : " + v_e.getMessage(),
	// v_e);
	// }
	// finally
	// {
	// if (v_connection != null)
	// {
	// v_connection.disconnect();
	// }
	// }
	// }

	protected String doPost(final String p_url, final boolean p_updateCookiesToForward, final SSOCookies p_cookies,
			final MultivaluedMap<String, String> p_parametersBody) {

		// try
		// {
		// final Client v_client = Client.create();
		// v_client.setConnectTimeout(_connectionTimeout);
		// v_client.setReadTimeout(_timeout);
		// final WebResource v_webResource = v_client.resource(p_url);
		// v_webResource.accept(MediaType.APPLICATION_JSON);
		// v_webResource.type(MediaType.APPLICATION_JSON);
		//
		// final ClientResponse v_response =
		// v_webResource.queryParams(p_parametersBody).post(ClientResponse.class);
		//
		// if (p_updateCookiesToForward)
		// {
		// // récupération des cookies dans la réponse pour stocker le token
		// //
		// updateCookiesToForward(v_connection.getHeaderFields().get(c_SET_COOKIE_HEADER),
		// p_cookies);
		// updateCookiesToForward(v_response, p_cookies);
		// }
		// return v_response.getEntity(String.class);
		// }
		// catch (final Exception v_e)
		// {
		// throw new Spi4jSecurityException("Impossible de se connecter, serveur SSO
		// inaccessible : " + v_e.getMessage(),
		// v_e);
		// }
		return null;

	}

	/**
	 * Ajoute des paramètres dans la requête POST
	 * 
	 * @param p_parametersBody les paramètres
	 * @param p_connection     la connexion
	 * @throws IOException erreur de connexion
	 */
	@SuppressWarnings("unused")
	private void addParametersPost(final Map<String, String> p_parametersBody, final HttpURLConnection p_connection)
			throws IOException {
		if (p_parametersBody != null && !p_parametersBody.isEmpty()) {
			// Envoi des paramètres dans le corps de la requête
			final StringBuilder v_str = new StringBuilder();
			boolean v_first = true;
			for (final Entry<String, String> v_parameter : p_parametersBody.entrySet()) {
				if (v_first) {
					v_first = false;
				} else {
					v_str.append('&');
				}
				v_str.append(v_parameter.getKey()).append('=').append(v_parameter.getValue());
			}
			p_connection.setDoOutput(true);
			p_connection.getOutputStream().write(v_str.toString().getBytes(_charset));
		}
	}

	/**
	 * Exécution d'une requête POST
	 * 
	 * @param p_url l'adresse de la requête
	 * @return la réponse du serveur
	 * @see #doPost(String, boolean, SSOCookies, MultivaluedMap)
	 */
	protected String doPost(final String p_url) {
		return doPost(p_url, false, null, null);
	}

	/**
	 * Ajoute les cookies à la connexion.
	 * 
	 * @param p_cookies les cookies
	 * @return la nouvelle valeur du cookie
	 */
	@SuppressWarnings("unused")
	private String addCookiesToConnection(final SSOCookies p_cookies) {
		final StringBuilder v_str = new StringBuilder();
		boolean v_first = true;
		for (final Entry<String, String> v_cookie : p_cookies.entrySet()) {
			if (v_first) {
				v_first = false;
			} else {
				v_str.append("; ");
			}
			v_str.append(v_cookie.getKey()).append('=').append(v_cookie.getValue());
		}
		return v_str.toString();
		// p_connection.addRequestProperty(c_COOKIE_HEADER, v_str.toString());
	}

	/**
	 * Lit la réponse de la requête.
	 * 
	 * @param p_response la réponse de la requête
	 * @return la chaine lue
	 * @throws Exception erreur de lecture
	 */
	// private String readResponse (final HttpURLConnection p_connection) throws
	// IOException
	// {
	// final InputStream v_is = p_connection.getInputStream();
	// int v_read;
	// final byte[] v_buffer = new byte[256];
	// final StringBuilder v_reponse = new StringBuilder();
	// while (v_is.available() > 0)
	// {
	// v_read = v_is.read(v_buffer);
	// v_reponse.append(new String(v_buffer, 0, v_read, _charset));
	// }
	// return v_reponse.toString();
	// }

	// private String readResponse (final ClientResponse p_response) throws
	// Exception
	// {
	// final JSONParser v_jsonParser = new JSONParser();
	// final JSONObject v_jsonObject = (JSONObject)
	// v_jsonParser.parse(p_response.getEntity(String.class));
	// System.out.println("Entity : " + v_jsonObject);
	//
	// return p_response.getEntity(String.class);
	// }

	/**
	 * Met à jour les cookies à renvoyer au serveur en cas de nouvelle requête.
	 * 
	 * @param p_response la réponse client
	 * @param p_cookies  les cookies de l'utilisateur
	 */
	// private void updateCookiesToForward (final ClientResponse p_response, final
	// SSOCookies p_cookies)
	// {
//      try
//      {
//         if (p_response == null)
//         {
//            throw new Spi4jRuntimeException("Aucun cookie retourné par le serveur SSO",
//                     "Vérifier que le serveur SSO fonctionne bien (" + _ssoUrl + ')');
//         }
//         final JSONParser v_jsonParser = new JSONParser();
//         JSONObject v_jsonObject;
//
//         v_jsonObject = (JSONObject) v_jsonParser.parse(p_response.getEntity(String.class));
//         p_cookies.put(c_SET_COOKIE_HEADER, (String) v_jsonObject.get(c_SET_COOKIE_HEADER));
//
//      }
//      catch (ClientHandlerException | UniformInterfaceException | ParseException v_e)
//      {
//         // TODO Auto-generated catch block
//         v_e.printStackTrace();
//      }
	//
	// }

	@Override
	public void setTimeout(final int p_timeout) {
		_timeout = p_timeout;
	}

	@Override
	public void setConnectionTimeout(final int p_connectionTimeout) {
		_connectionTimeout = p_connectionTimeout;
	}

	@Override
	public void setCharset(final String p_charset) {
		_charset = p_charset;
	}

}
