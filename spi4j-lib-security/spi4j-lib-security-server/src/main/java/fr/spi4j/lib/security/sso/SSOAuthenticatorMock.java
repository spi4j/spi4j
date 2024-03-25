/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.sso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.lib.security.exception.Spi4jSecurityException;
import fr.spi4j.lib.security.sso.SSOUserAttributes.Role;

/**
 * Mock d'un SSO Authenticator à des fins de tests seulement.
 * 
 * @author MINARM
 */
public class SSOAuthenticatorMock implements SSOAuthenticator_Itf {

	static final String c_userGood = "erwan.garel";

	static final String c_passwordGood = "qCBWGciB";

	static final String c_userBad = "erwan.garel2";

	static final String c_passwordBad = "abc";

	static final String c_userName = "GAREL Erwan";

	private static final String c_roleLib = "id=ugrp.cosi,ou=group,dc=opensso,dc=java,dc=net";

	private static final Logger c_log = LogManager.getLogger(SSOAuthenticator.class);

	private boolean _stateLogin;

	private String _user;

	private final String _SSO_URL;

	// private String _password;

	/**
	 * @param p_SSO_URL url du sso
	 */
	public SSOAuthenticatorMock(final String p_SSO_URL) {
		_SSO_URL = p_SSO_URL;
	}

	@Override
	public SSOCookies login(final String p_user, final String p_password) {
		if (!c_userGood.equals(p_user) || !c_passwordGood.equals(p_password)) {
			throw new Spi4jSecurityException("La connexion de l'utilisateur " + p_user + " a échoué");
		}
		_stateLogin = true;
		_user = p_user;
		// _password = p_password;
		return new SSOCookies();
	}

	@Override
	public boolean checkTokenValidity(final SSOCookies p_cookies) {
		return _stateLogin;
	}

	@Override
	public SSOUserAttributes getAttributes(final SSOCookies p_cookies) {
		final SSOUserAttributes v_SSOUserAttributes = new SSOUserAttributes();
		// Création 'uid'
		v_SSOUserAttributes.createAttributeListByName("uid");
		v_SSOUserAttributes.getAttributeListByName("uid").add(_user);
		v_SSOUserAttributes.setTokenId(_SSO_URL);
		// Création 'cn'
		v_SSOUserAttributes.createAttributeListByName("cn");
		v_SSOUserAttributes.getAttributeListByName("cn").add(c_userName);
		// Création de 'Role'
		v_SSOUserAttributes.getRoles().add(new Role(c_roleLib));
		return v_SSOUserAttributes;
	}

	@Override
	public void logout(final SSOCookies p_cookies) {
		// _password = null;
		_stateLogin = false;
	}

	@Override
	public List<String> searchCookiesToForward() {
		return Collections.emptyList();
	}

	@Override
	public void setTimeout(final int p_timeout) {
		// RAS
	}

	@Override
	public void setConnectionTimeout(final int p_connectionTimeout) {
		// RAS
	}

	@Override
	public void setCharset(final String p_charset) {
		// RAS
	}

	/**
	 * Retourne l'instance du SSO Authenticator utilisée pour les tests.
	 * <p>
	 * Retourne l'instance réelle du SSO Authenticator si le serveur est joignable
	 * ou un SSO Authenticator mocké (bouchon) si le serveur n'est pas joignable.
	 * 
	 * @param p_url l'url du serveur SSO
	 * @return l'instance du SSO Authenticator utilisée pour les tests
	 */
	public static SSOAuthenticator_Itf getSSOAuthenticator(final String p_url) {
		InputStream v_is = null;
		try {
			v_is = new URL(p_url).openStream();
			c_log.info("Utilisation de l'implémentation réelle du SSO Authenticator");
			return new SSOAuthenticator(p_url);
		} catch (final IOException v_e) {
			c_log.info("Utilisation du mock de SSO Authenticator");
			return new SSOAuthenticatorMock(p_url);
		} finally {
			if (v_is != null) {
				try {
					v_is.close();
				} catch (final IOException v_e) {
					c_log.warn("Impossible de fermer le flux", v_e);
				}
			}
		}
	}

}
