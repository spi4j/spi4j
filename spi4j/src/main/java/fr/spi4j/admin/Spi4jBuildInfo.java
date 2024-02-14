/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.admin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Récupèration ou affichage des informations sur le build.<br/>
 * (clés : build.number, build.id, build.svnRevision, build.svnUrl, build.projectVersion, etc telles que définies dans le pom.xml)
 * @author MINARM
 */
public final class Spi4jBuildInfo extends BuildInfo_Abs
{

   private static final Spi4jBuildInfo c_instance = new Spi4jBuildInfo();

   private final Map<String, String> _infos;

   /**
    * Constructeur.
    */
   private Spi4jBuildInfo ()
   {
      super();
      _infos = loadInfos();
   }

   /**
    * @return les infos chargées depuis le fichier properties construit par la pic
    */
   private Map<String, String> loadInfos ()
   {
      final InputStream v_input = getClass().getResourceAsStream("/spi4j-build-info.properties");
      if (v_input == null)
      {
         return Collections.emptyMap();
      }

      final Properties v_properties = new Properties();
      try
      {
         try
         {
            v_properties.load(v_input);
         }
         finally
         {
            v_input.close();
         }
      }
      catch (final IOException v_e)
      {
         // n'est pas censé arrivé puisque le stream et donc le fichier existe à cet endroit
         throw new IllegalStateException(v_e);
      }

      final Map<String, String> v_map = new LinkedHashMap<>();
      for (final Entry<Object, Object> v_entry : v_properties.entrySet())
      {
         final String v_key = (String) v_entry.getKey();
         // on ne garde que les propriétés correspondant au pattern souhaité et pas les autres propriétés
         if (v_key.startsWith("build."))
         {
            v_map.put(v_key, (String) v_entry.getValue());
         }
      }
      return Collections.unmodifiableMap(v_map);
   }

   /**
    * @return l'instance du BuildInfo
    */
   public static Spi4jBuildInfo getInstance ()
   {
      return c_instance;
   }

   /**
    * @return Retourne l'identifiant unique de cette application
    */
   public String getUniqueId ()
   {
      return _infos.get("build.tag");
   }

   @Override
   public String getNomApplication ()
   {
      return _infos.get("build.projectArtifactId");
   }

   @Override
   public String getVersion ()
   {
      return _infos.get("build.projectVersion");
   }

   @Override
   public String getRevision ()
   {
      return _infos.get("build.svnRevision");
   }

   @Override
   public String getUrl ()
   {
      return _infos.get("build.svnUrl");
   }
}
