/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.requirement;

/**
 * Interface des énumérations d'exigences.
 * @author MINARM
 */
// TODO Renommer les méthodes une fois que l'identifiant textuel de la Forge sera paramétrable
public interface Requirement_Itf
{
   /**
    * Constante indiquant qu'une exigence n'est pas implémentée.
    */
   String c_notImplemented = "notImplemented";

   /**
    * Constante indiquant qu'une exigence n'est pas implémentable.
    */
   String c_notImplementable = "notImplementable";

   /**
    * @return l'id de l'exigence.
    */
   String getId ();

   /**
    * @return le nom de l'exigence.
    */
   String getName ();

   /**
    * Obtenir la No de la version implémentée.
    * @return Le No de version implémentée (ex : "0.9").
    */
   // String getVersionImplem ();
   String get_versionImplem ();

   /**
    * Indiquer qu'une exigence n'est pas implémentée.
    */
   // void setVersionImplem ();
   void set_versionImplem ();

   /**
    * Affecter le No de version.
    * @param p_versionImplem
    *           La version implémentée.
    */
   // void setVersionImplem (String p_version);
   void set_versionImplem (String p_versionImplem);

   /**
    * Obtenir la No de la version du modèle.
    * @return Le No de version du modèle (ex : "1.0").
    */
   // String get_versionModel ();
   String get_versionModel ();
}
