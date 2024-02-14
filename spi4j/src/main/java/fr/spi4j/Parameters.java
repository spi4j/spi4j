/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j;

import jakarta.servlet.ServletContext;

/**
 * Paramètres de configuration de l'application.
 * @author MINARM
 */
public final class Parameters
{
   private static ServletContext _servletContext;

   	/**
	 * Paramètre pour demande de démarrage sur base H2.
	 */
	public static final String c_h2 = "database.h2";

   /**
    * Constructeur.
    */
   private Parameters ()
   {
      super();
   }

   /**
    * Initialise le contexte de servlet.
    * @param p_servletContext
    *           ServletContext
    */
   public static void initServletContext (final ServletContext p_servletContext)
   {
      _servletContext = p_servletContext;
   }

   /**
    * Retourne la valeur en propriété système
    * @param p_name
    *           Nom du paramètre
    * @param p_defaultValue
    *           Valeur par défaut
    * @return Valeur du paramètre
    */
   public static String getParameter (final String p_name, final String p_defaultValue)
   {
      // priorité aux paramètres en propriété système si le paramètre y est défini
      String v_result = System.getenv(p_name);
      if (v_result == null)
      {
         v_result = System.getProperty(p_name);
      }
      if (v_result == null && _servletContext != null)
      {
         // sinon priorité aux paramètres dans le contexte de servlet s'il y en a un et si le paramètre y est défini
         v_result = _servletContext.getInitParameter(p_name);
      }
      if (v_result == null)
      {
         // sinon utilisation de la valeur par défaut (en développement par exemple)
         v_result = p_defaultValue;
      }
      return v_result;
   }

   	/**
	 * Démarrage de la couche de persistence sur la base H2 (Junit ou base
	 * embarquée).
	 */
   public static void setH2Database ()
   {
		System.setProperty(c_h2, "true");
   }
}
