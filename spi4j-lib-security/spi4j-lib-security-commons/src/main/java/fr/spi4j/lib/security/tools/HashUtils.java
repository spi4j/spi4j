/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Classe utilitaire de génération de hashs.
 * @author MINARM
 */
public final class HashUtils
{

   /**
    * Algorithme de hashage MD5.
    */
   public static final String c_md5 = "MD5";

   /**
    * Algorithme de hashage SHA-1.
    */
   public static final String c_sha1 = "SHA-1";

   /**
    * Algorithme de hashage SHA-256.
    */
   public static final String c_sha256 = "SHA-256";

   /**
    * Constructeur privé.
    */
   private HashUtils ()
   {
      super();
   }

   /**
    * Méthode pour hasher une chaîne de caractère
    * @param p_text
    *           Le texte que l'on veut hasher.
    * @param p_algorithm
    *           L'algorithme avec lequel on veut hasher le mot de passe
    * @return Le password hashé.
    */
   public static String hashText (final String p_text, final String p_algorithm)
   {
      if (p_text == null)
      {
         throw new IllegalArgumentException("Aucune chaîne de caractère définie.");
      }

      if (p_algorithm == null)
      {
         throw new Spi4jRuntimeException("Aucun algorithme défini.", "Choisissez un algorithme.");
      }

      final MessageDigest v_md;
      try
      {
         // On indique quel algorithme on veut utiliser
         v_md = MessageDigest.getInstance(p_algorithm);
      }
      catch (final NoSuchAlgorithmException v_e)
      {
         throw new Spi4jRuntimeException(v_e, "L'algorithme demandé (" + p_algorithm + ") n'existe pas.",
                  "Vérifiez le nom de l'algorithme que vous avez choisi.");
      }

      // On définit le tableau d'octets selon le mot de passe entré
      try
      {
         v_md.update(p_text.getBytes("UTF-8"));
      }
      catch (final UnsupportedEncodingException v_ex)
      {
         throw new IllegalStateException(v_ex);
      }
      final byte[] v_tabBytes = v_md.digest();

      // On définit un nouvel objet StringBuffer qui correspondra au hash final
      final StringBuilder v_hexString = new StringBuilder();
      for (final byte v_byte : v_tabBytes)
      {
         // On calcule l'octet en base 16
         final String v_hex = Integer.toHexString(0xff & v_byte);
         if (v_hex.length() == 1)
         {
            v_hexString.append('0');
         }
         // On concatène l'octet sortant à notre objet StringBuffer
         v_hexString.append(v_hex);
      }
      return v_hexString.toString();
   }

   /**
    * Méthode main pour faire un test ou pour calculer manuellement un mot de passe hashé.
    * @param p_args
    *           String[]
    */
   public static void main (final String[] p_args)
   {
      System.out.println(hashText("test", c_md5)); 
   }
}
