/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPOutputStream;

/**
 * Classe utilitaire pour les tests de remoting.
 * @author MINARM
 */
final class SerializationUtil
{
   /**
    * Constructeur.
    */
   private SerializationUtil ()
   {
      super();
   }

   /**
    * Sérialise un objet en gzip.
    * @param p_serializable
    *           Serializable
    * @return byte[]
    * @throws IOException
    *            e
    */
   static byte[] serializeAndGZip (final Serializable p_serializable) throws IOException
   {
      final ByteArrayOutputStream v_byteArrayOutputStream = new ByteArrayOutputStream();
      final ObjectOutputStream v_objectOutputStream = new ObjectOutputStream(new GZIPOutputStream(
               v_byteArrayOutputStream));
      try
      {
         v_objectOutputStream.writeObject(p_serializable);
      }
      finally
      {
         v_objectOutputStream.close();
      }
      return v_byteArrayOutputStream.toByteArray();
   }

   /**
    * Sérialise un objet sans gzip.
    * @param p_serializable
    *           Serializable
    * @return byte[]
    * @throws IOException
    *            e
    */
   static byte[] serialize (final Serializable p_serializable) throws IOException
   {
      final ByteArrayOutputStream v_byteArrayOutputStream = new ByteArrayOutputStream();
      final ObjectOutputStream v_objectOutputStream = new ObjectOutputStream(v_byteArrayOutputStream);
      try
      {
         v_objectOutputStream.writeObject(p_serializable);
      }
      finally
      {
         v_objectOutputStream.close();
      }
      return v_byteArrayOutputStream.toByteArray();
   }
}
