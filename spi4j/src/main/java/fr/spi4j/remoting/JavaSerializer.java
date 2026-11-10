/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Serializer Java.
 * @author MINARM
 */
public class JavaSerializer implements SpiRemotingSerializer_Itf
{

   @Override
   public void write (final Serializable p_request, final OutputStream p_output) throws IOException
   {
      final ObjectOutputStream v_outputObject = new ObjectOutputStream(p_output);
      try
      {
         // on écrit l'objet en sérialisation java
         v_outputObject.writeObject(p_request);
      }
      finally
      {
         v_outputObject.close();
      }
   }

   @Override
   public Object read (final InputStream p_input) throws IOException, ClassNotFoundException
   {
      final ObjectInputStream v_inputObject = new ObjectInputStream(p_input);
      try
      {
         // on écrit l'objet en sérialisation java
         return v_inputObject.readObject();
      }
      finally
      {
         v_inputObject.close();
      }
   }

   @Override
   public String getContentType ()
   {
      return java.awt.datatransfer.DataFlavor.javaSerializedObjectMimeType;
   }

}
