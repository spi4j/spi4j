/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import java.io.IOException;

import org.apache.logging.log4j.ThreadContext;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

/**
 * Filtre de servlet positionnant l'identifiant de l'utilisateur dans le
 * contexte ThreadContext de Log4j (%X{user}) et positionnant getRemoteUser()
 * sur la requête http passée à la chaine de servlet (en particulier, pour avoir
 * cet utilisateur dans la liste des requêtes http en cours et dans la liste des
 * sessions http).
 * 
 * @author MINARM
 */
public class RemoteUserFilter implements Filter {
	/**
	 * La clé de stockage dans le ThreadContext de Log4j pour l'utilisateur courant.
	 */
	private static final String c_threadContextKeyUser = "user";

	@Override
	public void doFilter(final ServletRequest p_request, final ServletResponse p_response, final FilterChain p_chain)
			throws IOException, ServletException {
		if (!(p_request instanceof HttpServletRequest)) {
			// ce n'est pas une requête http, ce filtre ne fait rien sur cette requête
			p_chain.doFilter(p_request, p_response);
			return;
		}
		final HttpServletRequest v_request = (HttpServletRequest) p_request;
		if (v_request.getRemoteUser() != null) {
			// il y a déjà un remoteUser dans la requête, ce filtre ne fait rien sur cette
			// requête (hormis positionner le contexte ThreadContext dans Log4j)
			ThreadContext.put(c_threadContextKeyUser, v_request.getRemoteUser());

			try {
				p_chain.doFilter(p_request, p_response);
			} finally {
				ThreadContext.clearAll();
			}
			return;
		}
		final HttpSession v_session = v_request.getSession(false);
		if (v_session == null || v_session.getAttribute(Spi4jServerSecurity.c_keyUser) == null) {
			// cette requête n'est pas encore liée à une session http ou bien l'utilisateur
			// n'est pas encore authentifié avec cette session,
			// ce filtre ne fait rien sur cette requête (il fera probablement quelque chose
			// sur la requête http suivante du même client)
			p_chain.doFilter(p_request, p_response);
			return;
		}

		// on récupère l'identifiant de l'utilisateur dans la session http
		final User_Itf v_utilisateur = (User_Itf) v_session.getAttribute(Spi4jServerSecurity.c_keyUser);
		final String v_remoteUser = v_utilisateur.getIdentifiant();

		// on implémente une requête http identique à celle d'origine hormis que
		// getRemoteUser() retourne l'identifiant de l'utilisateur.
		// Ainsi, le monitoring afficher l'identifiant de l'utilisateur dans les
		// requêtes en cours et dans la liste des sessions http (demande de Didier
		// Féret)
		// et toute autre librairie pourra utiliser la fonction standard JavaEE
		// getRemoteUser() pour connaître l'identifiant de l'utilisateur connecté.
		final HttpServletRequest v_wrappedRequest = new HttpServletRequestWrapper(v_request) {
			@Override
			public String getRemoteUser() {
				return v_remoteUser;
			}
		};

		// on positionne le contexte ThreadContext dans Log4j : le pattern des messages
		// de log pourra utiliser "%X{user}"
		ThreadContext.put(c_threadContextKeyUser, v_remoteUser);

		try {
			// on passe l'exécution à la chaîne de servlet avec la requête wrappée
			p_chain.doFilter(v_wrappedRequest, p_response);
		} finally {
			ThreadContext.clearAll();
		}
	}

	@Override
	public void init(final FilterConfig p_filterConfig) throws ServletException {
		// RAS
	}

	@Override
	public void destroy() {
		// RAS
	}
}
