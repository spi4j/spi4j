/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Filtre de servlet qui récupère la session http en la créant si besoin et qui
 * la rend disponible dans le code pour le thread courant durant toute la durée
 * de la requête.
 * <p>
 * Ce filtre de servlet peut être utilisé aussi bien dans un contexte client
 * léger tel que JSF que dans un contexte RDA avec le remoting Spi4J.
 *
 * @author MINARM
 */
public class HttpSessionFilter implements Filter {
	private static final ThreadLocal<HttpSession> c_threadLocal = new ThreadLocal<>();

	private static boolean initialized;

	/**
	 * Retourne la session http liée au client exécutant la requête en cours.
	 * <p>
	 * Cette session peut être utilisée pour stocker les informations et profils de
	 * l'utilisateur courant (quand il est authentifié) et les profils de cet
	 * utilisateur peuvent alors être utilisés pour vérifier que cet utilisateur a
	 * le droit d'exécuter une méthode en particulier sur un service (par exemple,
	 * avec un SecurityProxy vérifiant que l'utilisateur est authentifié dans la
	 * session http et que ses profils correspondent à ceux autorisés dans le
	 * paramètre d'une annotation sur la méthode du service).
	 *
	 * @return HttpSession
	 */
	public static HttpSession getSessionForCurrentThread() {
		if (!initialized) {
			throw new IllegalStateException(
					"Le filtre HttpSessionFilter n'a pas été initialisé, vous devez ajouter une déclaration de filter et de filter-mapping dans le fichier web.xml de votre webapp");
		}
		return c_threadLocal.get();
	}

	@Override
	public void doFilter(final ServletRequest p_request, final ServletResponse p_response, final FilterChain p_chain)
			throws IOException, ServletException {
		if (!(p_request instanceof HttpServletRequest)) {
			// ce n'est pas une requête http, ce filtre ne fait rien sur cette requête
			p_chain.doFilter(p_request, p_response);
			return;
		}
		final HttpServletRequest v_request = (HttpServletRequest) p_request;

		// récupère la session http du client et crée la session si elle ne l'est pas
		// déjà
		// (ce qui positionnera un cookie avec jsessionId dans la réponse)
		final HttpSession v_session = v_request.getSession();
		bindHttpSessionOnCurrentThread(v_session);

		try {
			p_chain.doFilter(p_request, p_response);
		} finally {
			unbindHttpSessionOnCurrentThread();
		}
	}

	/**
	 * Associe une session http avec le thread courant (cette session doit
	 * absolument être désassociée avec unbind avant que le thread ne serve à une
	 * autre requête http).
	 *
	 * @param p_session HttpSession
	 */
	private static void bindHttpSessionOnCurrentThread(final HttpSession p_session) {
		c_threadLocal.set(p_session);
	}

	/**
	 * Désassocie la session http du thread courant.
	 */
	private static void unbindHttpSessionOnCurrentThread() {
		c_threadLocal.remove();
	}

	/**
	 * Définit la valeur de initialized
	 *
	 * @param p_initialized boolean
	 */
	private static void setInitialized(final boolean p_initialized) {
		initialized = p_initialized;
	}

	@Override
	public void init(final FilterConfig p_filterConfig) throws ServletException {
		setInitialized(true);
	}

	@Override
	public void destroy() {
		// RAS
	}
}
