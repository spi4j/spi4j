/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.admin;

/**
 * Enumération des services disponibles
 * @author MINARM
 */
enum AdministrationServiceEnum implements AdministrationService_Itf
{
   INDEX("/", "index.html"),

   CLEAR_CACHES("/clearCaches", "clearCaches.html"),

   CHECK_DATABASE("/checkDatabase", "checkDatabase.html");

   private final String _path;

   private final String _servletContent;

   /**
    * Constructeur
    * @param p_path
    *           l'url de ce service
    * @param p_servletContent
    *           le chemin de la jsp à afficher à l'utilisateur (les jsp doivent se trouver dans src/main/resources/fr/spi4j/admin/)
    */
   private AdministrationServiceEnum (final String p_path, final String p_servletContent)
   {
      _path = p_path;
      _servletContent = p_servletContent;
   }

   @Override
   public String getServletContentPath ()
   {
      return _servletContent;
   }

   /**
    * Retrouve un service à partir de son url
    * @param p_path
    *           l'url cherchée
    * @return le service trouvée ou null si aucun service n'est défini à cette url
    */
   public static AdministrationServiceEnum getServiceFromPath (final String p_path)
   {
      if (p_path == null || p_path.isEmpty())
      {
         throw new RuntimeException("Pour atteindre l'index, rajoutez un / à la fin de l'URL");
      }
      for (final AdministrationServiceEnum v_service : values())
      {
         if (v_service._path.equalsIgnoreCase(p_path))
         {
            return v_service;
         }
      }
      return null;
   }
}
