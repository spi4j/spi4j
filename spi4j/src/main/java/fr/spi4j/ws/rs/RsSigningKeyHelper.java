/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.impl.crypto.MacProvider;

/**
 * Utility class for tokens keys.
 *
 * @author MINARM
 */
public final class RsSigningKeyHelper {

	/**
	 * The jjwt resolver for keys from jwks.
	 */
	private SigningKeyResolverAdapter _keyResolver;

	/**
	 * Constructor. All errors are treaded by jjwt parser.
	 */
	RsSigningKeyHelper() {

		// !NOTICE : There is a difference between signing/verifying and
		// encrypting/decrypting data but the semantics can be similar. You sign data
		// with a private key that only controlled sources have so anyone who receives
		// the information can use your public key to validate this information was
		// indeed sent by you and is the same information you intended to send out. BUT
		// you encrypt data with a public key and decrypt with a private key. This
		// sounds opposite but really follows the same logical concept as signing.

		_keyResolver = new SigningKeyResolverAdapter()

		{
			@Override
			@SuppressWarnings("rawtypes")
			public Key resolveSigningKey(final JwsHeader p_headers, final Claims p_claims) {
				// The key id should be be in the headers for the token.
				String v_key = String.valueOf(p_headers.get(RsConstants.c_token_key_id));

				// Try in the claims...
				if (null == v_key) {
					v_key = String.valueOf(p_claims.get(RsConstants.c_token_key_id));
				}

				// If the key id not found..
				if (null == v_key) {
					throw new JwtException(
							"Impossible de trouver l'identifiant de la clé de chiffrement pour le jeton !");
				}

				// Return the Key associated with the id of key defined in the token.
				return RsFilter_Abs.get_config().get_tokenSigningKeys()
						.get(String.valueOf(p_headers.get(RsConstants.c_token_key_id)));
			}
		};
	}

	/**
	 * Holder to create a Singleton value for the RsSigninKeyHelper (used for the
	 * resolver).
	 *
	 * @author MINARM
	 */
	private static class Holder {
		public static RsSigningKeyHelper _instance = new RsSigningKeyHelper();
	}

	/**
	 * Retrieve directly the unique instance of the signingKeyResolver.
	 *
	 * @return The signingKeyResolver for the token.
	 */
	static SigningKeyResolverAdapter get_signinKeyResolver() {
		return Holder._instance._keyResolver;
	}

	/**
	 * Retrieve the signing key for token creation.Permits to find the specified key
	 * more efficiently.
	 *
	 * @return The signing key.
	 */
	static Key get_signingKeyForCreation(final String p_createSigningKeyId) {
		// Here for symmetric and asymmetric -> the private key.
		// Not concerned for OIDC.
		return RsFilter_Abs.get_config().get_tokenSigningKeys().get(p_createSigningKeyId);
	}

	/**
	 * Create the Key for JWT in case of symmetric algorithm (auto generated).
	 *
	 * @param p_tokenSignatureAlgorithm : The algorithm chosen for the secret
	 *                                  encryption.
	 * @return A map with an unique operational key.
	 */
	static Map<String, Key> createSigningKey(final RsSigningAlgo_Enum p_tokenSignatureAlgorithm) {
		final Map<String, Key> v_lstKeys = new HashMap<>();

		// Create a symmetric key with an automatic generated secret.
		final Key v_tokenKey = MacProvider.generateKey(p_tokenSignatureAlgorithm.get_algorithm());

		// Add the unique key in the map.
		v_lstKeys.put(RsConstants.c_token_signing_key_private, v_tokenKey);

		// As static, no defensive copy.
		return v_lstKeys;
	}

	/**
	 * Create the KeyPair for JWT in case of asymmetric algorithm (auto generated).
	 *
	 * @param p_tokenSignatureAlgorithm : The algorithm chosen for the secret
	 *                                  encryption.
	 * @return A map with a private and public operational keys
	 * @throws NoSuchAlgorithmException : No provider for the algorithm.
	 */
	static Map<String, Key> createSigningKeyPair(final RsSigningAlgo_Enum p_tokenSignatureAlgorithm)
			throws NoSuchAlgorithmException {
		final Map<String, Key> v_lstKeys = new HashMap<>();

		KeyPairGenerator v_keyGenerator;
		v_keyGenerator = KeyPairGenerator.getInstance(p_tokenSignatureAlgorithm.get_algorithm().getFamilyName());
		v_keyGenerator.initialize(RsConstants.c_token_rsa_key_min_size);
		v_keyGenerator.genKeyPair();

		// Put the private Key and the public key in a map (separate the KeyPair).
		v_lstKeys.put(RsConstants.c_token_signing_key_private, v_keyGenerator.generateKeyPair().getPrivate());
		v_lstKeys.put(RsConstants.c_token_signing_key_public, v_keyGenerator.generateKeyPair().getPublic());

		// As static, no defensive copy.
		return v_lstKeys;
	}

	/**
	 * Create the keyPair for asymmetric keys from ".der" files.
	 *
	 * @param keyBytesPrivate : The content of the ".der" file for private key.
	 * @param keyBytesPublic  : The content of the ".der" file for public key.
	 * @return A map with a private and public operational keys.
	 * @throws NoSuchAlgorithmException : No provider for the algorithm.
	 * @throws InvalidKeySpecException  :
	 */
	static Map<String, Key> createSigningKeyPair(final byte[] keyBytesPrivate, final byte[] keyBytesPublic,
			final RsSigningAlgo_Enum p_tokenSignatureAlgorithm)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		final Map<String, Key> v_lstKeys = new HashMap<>();
		final KeyFactory v_kf = KeyFactory.getInstance(p_tokenSignatureAlgorithm.get_algorithm().getFamilyName());

		// Private key.
		v_lstKeys.put(RsConstants.c_token_signing_key_private,
				v_kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytesPrivate)));

		// Public key.
		v_lstKeys.put(RsConstants.c_token_signing_key_public,
				v_kf.generatePublic(new X509EncodedKeySpec(keyBytesPublic)));

		// As static, no defensive copy.
		return v_lstKeys;
	}

	/**
	 * Create and store all the keys from the Json Web Key Set (from file or http or
	 * keystore).
	 *
	 * @param p_keys : The Json object retrieved from file / http url / keystore.
	 * @return A map with all operational keys with their id.
	 * @throws InvalidKeySpecException  :
	 * @throws NoSuchAlgorithmException :
	 */
	static Map<String, Key> createSigningWebKeys(final JSONObject p_keys)
			throws InvalidKeySpecException, NoSuchAlgorithmException {

		// As we already use jjwt (org.jsonwebtoken) for token management (encode and
		// decode) JWKS is not fully supported yet with this library. We could change a
		// take Nimbus JOSE + JWT. But for now we simply use this very simple code.

		final Map<String, Key> v_lstKeys = new HashMap<>();
		final JSONArray v_keys = p_keys.getJSONArray(RsConstants.c_jwk_keys_arrays);

		for (int i = 0; i < v_keys.length(); i++) {
			final BigInteger v_modulus = new BigInteger(1,
					Base64.getUrlDecoder().decode(v_keys.getJSONObject(i).getString(RsConstants.c_jwk_modulus)));

			final BigInteger v_exponent = new BigInteger(1,
					Base64.getUrlDecoder().decode(v_keys.getJSONObject(i).getString(RsConstants.c_jwk_exponent)));

			v_lstKeys.put(v_keys.getJSONObject(i).getString(RsConstants.c_token_key_id),
					KeyFactory.getInstance(String.valueOf(v_keys.getJSONObject(i).get(RsConstants.c_jwk_family_name)))
							.generatePublic(new RSAPublicKeySpec(v_modulus, v_exponent)));
		}
		// As static, no defensive copy.
		return v_lstKeys;
	}

	/**
	 * Create and store all the keys from the Json Web Key Set.
	 *
	 * @param p_keystore : The keystore where are located the keys.
	 * @return A map with all operational keys with their id.
	 * @throws KeyStoreException :
	 */
	@SuppressWarnings("rawtypes")
	static Map<String, Key> createSigningKeys(final KeyStore p_keystore) throws KeyStoreException {
		final Map<String, Key> v_lstKeys = new HashMap<>();

		for (final Enumeration v_enum = p_keystore.aliases(); v_enum.hasMoreElements();) {
			final Certificate v_cert = p_keystore.getCertificate((String) v_enum.nextElement());
			v_lstKeys.put(v_enum.toString(), v_cert.getPublicKey());
		}
		// As static, no defensive copy.
		return v_lstKeys;
	}
}
