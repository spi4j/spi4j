/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.filter;

import java.io.IOException;

import fr.spi4j.persistence.UserPersistence_Abs;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * Implémentation de javax.servlet.Filter qui place toutes les requêtes http
 * passant par le filtre dans une transaction.
 * <p>
 * Le but est que même si les services sont chacun transactionnels, alors les
 * appels de services successifs sont globalement dans une transaction, par
 * exemple pour des appels de services successifs depuis une servlet d'un client
 * léger web (jsf, jsp, etc).
 * <p>
 * Lors de l'initialisation de la webapp, il faut appeler la méthode
 * "TransactionFilter.initUserPersistence(XxParamPersistence.getUserPersistence())"
 * pour que ce filtre connaisse le "userPersistence" permettant de gérer la
 * transaction.
 *
 * @author MINARM
 */
public final class TransactionFilter implements Filter {
	private static UserPersistence_Abs userPersistence;

	/**
	 * Initialisation du UserPersistence.
	 *
	 * @param p_UserPersistence UserPersistence_Abs
	 */
	public static void initUserPersistence(final UserPersistence_Abs p_UserPersistence) {
		userPersistence = p_UserPersistence;
	}

	/**
	 * Modifie la transaction associée avec le thread courant telle que la
	 * transaction se termine forcément par un rollback, même si un commit est
	 * appelé.
	 */
	public static void setRollbackOnly() {
		userPersistence.setRollbackOnly();
	}

	@Override
	public void doFilter(final ServletRequest p_req, final ServletResponse p_res, final FilterChain p_chain)
			throws IOException, ServletException {
		if (userPersistence == null) {
			throw new IllegalStateException(
					"Le userPersistence n'est pas initialisé : appeler 'TransactionFilter.initUserPersistence(ParamPersistenceGen_Abs.getUserPersistence())' lors de l'initialisation de la webapp");
		}

		userPersistence.begin();
		try {
			p_chain.doFilter(p_req, p_res);
			userPersistence.commit();
		} catch (final Throwable v_ex) {
			userPersistence.rollback();

			if (v_ex instanceof IOException) {
				throw (IOException) v_ex;
			} else if (v_ex instanceof ServletException) {
				throw (ServletException) v_ex;
			} else if (v_ex instanceof RuntimeException) {
				throw (RuntimeException) v_ex;
			} else if (v_ex instanceof Error) {
				throw (Error) v_ex;
			} else {
				// ne peut pas arriver mais au cas où
				throw new ServletException(v_ex);
			}
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
