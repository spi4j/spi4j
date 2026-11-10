/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

/**
 * Helper for client creation. Based on client pool connection.
 *
 * @author MINARM
 */
public class RsClientFactory {

	/**
	 * @param p_uri :
	 * @return The WebTarget.
	 */
	public static WebTarget get_target(final String p_uri) {
		final Client v_client = ClientBuilder.newBuilder().sslContext(getSSLContext())
				.hostnameVerifier((s1, s2) -> true).build();

		return v_client.target(p_uri);
	}

	/**
	 * Retrieve a very permissive SSL context. For development purpose.
	 *
	 * @return a default permissive SSL context.
	 */
	private static SSLContext getSSLContext() {
		// IgnoreSSL.
		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance(RsConstants.c_tls);
			sslcontext.init(null, new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(final X509Certificate[] arg0, final String arg1)
						throws CertificateException {
					// RAS.
				}

				@Override
				public void checkServerTrusted(final X509Certificate[] arg0, final String arg1)
						throws CertificateException {
					// RAS.
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new java.security.SecureRandom());
		} catch (final Exception e) {
			// RAS.
		}
		return sslcontext;
	}
}
