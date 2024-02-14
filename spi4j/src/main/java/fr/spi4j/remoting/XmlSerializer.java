/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;

/**
 * Serializer Xml en utilisant XStream.
 * @author MINARM
 */
public class XmlSerializer implements SpiRemotingSerializer_Itf
{
   private static final String c_CONTENT_TYPE = "text/xml";

   private static final String c_XML_CHARSET_NAME = "UTF-8";

   private final XStream _xstream = new XStream();

   /**
    * Instanciation de XStream.
    * @return XStream
    */
   protected XStream getXStream ()
   {
      // format xml, utilise la dépendance XPP3 par défaut
      return _xstream;
   }

   @Override
   public void write (final Serializable p_request, final OutputStream p_output) throws IOException
   {
      final XStream v_xstream = getXStream();

      // on wrappe avec un CompactWriter pour gagner 25% en taille de flux (retours chariots)
      // et donc un peu en performances
      final CompactWriter v_writer = new CompactWriter(new OutputStreamWriter(p_output, c_XML_CHARSET_NAME));
      try
      {
         v_xstream.marshal(p_request, v_writer);
      }
      finally
      {
         v_writer.close();
      }
   }

   @Override
   public Object read (final InputStream p_input) throws IOException, ClassNotFoundException
   {
      final XStream v_xstream = getXStream();

      final InputStreamReader v_reader = new InputStreamReader(p_input, c_XML_CHARSET_NAME);
      // on lit l'objet dans la réponse par désérialisation xml
      // (ici la réponse contient directement l'objet demandé)
      try
      {
         return v_xstream.fromXML(v_reader);
      }
      finally
      {
         v_reader.close();
      }
   }

   @Override
   public String getContentType ()
   {
      return c_CONTENT_TYPE;
   }
}
