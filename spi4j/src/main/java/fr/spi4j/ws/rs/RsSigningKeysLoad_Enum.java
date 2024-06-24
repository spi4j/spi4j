/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONObject;

import fr.spi4j.ws.rs.exception.RsUnexpectedException;

/**
 * @author MINARM
 */
public enum RsSigningKeysLoad_Enum {
	/**
	 * One shot for symmetric key in production or can be also used for
	 * development..If asymmetric, the keyPair is created at each start of the
	 * server...
	 */
	auto {
		@Override
		Map<String, Key> load(final String p_resourcePath, final RsSigningAlgo_Enum p_signingAlgo)
				throws RsUnexpectedException {
			// With this mode, no JWKS.
			if (p_signingAlgo.is_jsonWebKey()) {
				throw new NotImplementedException(
						"Chargement de la configuration des jetons impossible, vérifier les paramètres !");
			}
			try {
				if (p_signingAlgo.is_symmetricKey()) {
					return RsSigningKeyHelper.createSigningKey(p_signingAlgo);
				}
				return RsSigningKeyHelper.createSigningKeyPair(p_signingAlgo);
			}

	catch (final Exception p_exception) {
				throw new RsUnexpectedException("Impossible de configurer les jetons : " + p_exception.getMessage());
			}
		}

	},

	/**
	 * The JWKS can be retrieved from an URL.
	 */
	http {
		@Override
		public Map<String, Key> load(final String p_resourcePath, final RsSigningAlgo_Enum p_signingAlgo)
				throws RsUnexpectedException {
			// With this mode, allow JWKS uniquely.
			if (!p_signingAlgo.is_jsonWebKey()) {
				throw new NotImplementedException(
						"Chargement de la configuration des jetons impossible, vérifier les paramètres !");
			}

			try {
				return RsSigningKeyHelper.createSigningWebKeys(new JSONObject(
						RsClientFactory.get_target(p_resourcePath).request().get().readEntity(String.class)));
			} catch (final Exception p_exception) {
				throw new RsUnexpectedException(p_exception,
						"Impossible de récupérer le Json Web KeySet pour la configuration des jetons sur l''URI : {0}",
						p_resourcePath);
			}
		}
	},

	/**
	 * The JWKS can be retrieved from another local file.
	 */
	file {
		@Override
		@SuppressWarnings("resource")
		public Map<String, Key> load(final String p_resourcePath, final RsSigningAlgo_Enum p_signingAlgo)
				throws RsUnexpectedException {
			try {
				if (p_signingAlgo.is_symmetricKey()) {
					throw new NotImplementedException(
							"Chargement de la configuration des jetons impossible, vérifier les paramètres !");
				}

				// Try for a Json Web Key Set.
				if (p_signingAlgo.is_jsonWebKey()) {
					return RsSigningKeyHelper.createSigningWebKeys(new JSONObject(
							IOUtils.toString(new FileInputStream(p_resourcePath), Charset.defaultCharset())));
				}
				// Try for an asymmetric key in ".der" format.
				return RsSigningKeyHelper.createSigningKeyPair(
						Files.readAllBytes(Paths
								.get(p_resourcePath + File.separator + RsConstants.c_signing_key_private_filename)),
						Files.readAllBytes(
								Paths.get(p_resourcePath + File.separator + RsConstants.c_signing_key_public_filename)),
						p_signingAlgo);
			} catch (final Exception p_exception) {
				throw new RsUnexpectedException(p_exception.getMessage());
			}
		}
	},

	/**
	 * The keys can be retrieved from a KeyStore.
	 */
	keystore {
		@Override
		public Map<String, Key> load(final String p_resourcePath, final RsSigningAlgo_Enum p_signingAlgo)
				throws RsUnexpectedException {
			try {
				final KeyStore v_keyStore = KeyStore.getInstance(RsConstants.c_keystore_default_type);
				v_keyStore.load(new FileInputStream(p_resourcePath), "my-keystore-password".toCharArray());
				return RsSigningKeyHelper.createSigningKeys(v_keyStore);
			} catch (final Exception p_exception) {
				throw new RsUnexpectedException(p_exception.getMessage());
			}
		}
	};

	/**
	 * Retrieve all the signing keys (private / public / certificate).
	 *
	 * @param p_resourcePath : The path for the key(s) resources, it can be a file,
	 *                       an url or a keystore path.
	 * @return A JSON object for the key(s).
	 * @throws RsUnexpectedException : An unexpected exception.
	 */
	abstract Map<String, Key> load(final String p_resourcePath, final RsSigningAlgo_Enum p_signingAlgo)
			throws RsUnexpectedException;

}
