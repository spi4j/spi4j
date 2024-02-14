/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author MINARM
 */
public enum RsSigningAlgo_Enum
{

   // ########################################
   // To create a certificate (key pair).
   // ########################################
   // Generate a 2048-bit RSA private key
   // $ openssl genrsa -out private_key.pem 2048
   // Convert private Key to PKCS#8 format (so Java can read it)
   // $ openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt
   // Output public key portion in DER format (so Java can read it)
   // $ openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der


   /**
    * Basic symmetric algorithm (HMAC + SHA256) (128-bits of security).
    */
   symmetric(Boolean.TRUE, Boolean.FALSE, SignatureAlgorithm.HS256),

   /**
    * Symmetric algorithm (HMAC + SHA512) (256-bits of security).
    */
   symmetricHighSecure(Boolean.TRUE, Boolean.FALSE, SignatureAlgorithm.HS512),

   /**
    * Basic asymmetric algorithm (RSASSA-PKCS1-v1_5 + SHA256).
    */
   asymmetric(Boolean.FALSE, Boolean.FALSE, SignatureAlgorithm.RS256),

   /**
    * Asymmetric algorithm (RSASSA-PKCS1-v1_5 + SHA512).
    */
   asymmetricHighSecure(Boolean.FALSE, Boolean.FALSE, SignatureAlgorithm.RS512),

   /**
    * Asymmetric algorithm from a Json Web Keys (JWKS).
    */
   asymmetricJsonWebKey(Boolean.FALSE, Boolean.TRUE, SignatureAlgorithm.RS256),

   /**
    * Asymmetric algorithm from a Json Web Keys (JWKS).
    */
   asymmetricJsonWebKeyHighSecure(Boolean.FALSE, Boolean.TRUE, SignatureAlgorithm.RS512);

   /**
    * Is the key symmetric ? (equivalent to isHmac() / isRsa())
    */
   final private boolean _symmetricKey;

   /**
    * The signature algorithm for the key.
    */
   final private SignatureAlgorithm _algorithm;

   /**
    * The signature algorithm is jwk provided.
    */
   final private boolean _jwk;

   /**
    * Constructor.
    */
   private RsSigningAlgo_Enum (final boolean p_symmetricKey, final boolean p_jwk, final SignatureAlgorithm p_algorithm)
   {
      _symmetricKey = p_symmetricKey;
      _algorithm = p_algorithm;
      _jwk = p_jwk;
   }

   /**
    * Check if the chosen key is symmetric.
    * @return true if the key is symmetric, else false.
    */
   public boolean is_symmetricKey ()
   {
      return _symmetricKey;
   }

   /**
    * Check if the chosen key is a Json web key.
    * @return true if the key is a Json web key (jwk/jwks).
    */
   public boolean is_jsonWebKey ()
   {
      return _jwk;
   }

   /**
    * Retrieve the signature algorithm for the chosen key.
    * @return the signature algorithm.
    */
   public SignatureAlgorithm get_algorithm ()
   {
      return _algorithm;
   }
}
