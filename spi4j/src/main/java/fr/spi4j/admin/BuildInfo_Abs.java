/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.admin;

/**
 * Interface pour les informations sur le build.
 * @author MINARM
 */
public abstract class BuildInfo_Abs
{

   /**
    * @return le nom de l'application
    */
   public abstract String getNomApplication ();

   /**
    * @return la version de l'application
    */
   public abstract String getVersion ();

   /**
    * @return la révision (eg. Gestionnaire de sources) de l'application
    */
   public abstract String getRevision ();

   /**
    * @return l'URL (eg. Gestionnaire de sources) de l'application
    */
   public abstract String getUrl ();

   /**
    * @return la version de Spi4J utilisée
    */
   public String getSpi4jVersion ()
   {
      return Spi4jBuildInfo.getInstance().getVersion();
   }
}
