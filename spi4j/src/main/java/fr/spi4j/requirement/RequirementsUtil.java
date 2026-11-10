/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.requirement;

import org.apache.logging.log4j.LogManager;

/**
 * Classe utilitaire pour les exigences.
 * @author MINARM
 */
public final class RequirementsUtil
{

   /**
    * Constructeur par défaut.
    */
   private RequirementsUtil ()
   {
      // RAS
   }

   /**
    * Permet de vérfier que la version implémentée et la version du modèle pour l'exigence sont en égales.
    * @param p_Requirement_Itf
    *           L'exigence concernée.
    * @param p_versionImplem
    *           La version implémentée dans les tests.
    */
   public static void checkRequirementVersion (final Requirement_Itf p_Requirement_Itf, final String p_versionImplem)
   {
      // Si la version à déjà été affectée
      if (p_Requirement_Itf.get_versionImplem() != null)
      {
         throw new RequirementException(p_Requirement_Itf, "La version a déjà été affectée",
                  "Vérifier que le set_versionImplem() n'a été appelé qu'une seule fois pour l'exigence : "
                           + p_Requirement_Itf.getName());
      }
      // Si pas encore d'implémentation de l'exigence
      else if (Requirement_Itf.c_notImplemented.equals(p_versionImplem))
      {
         throw new RequirementException(p_Requirement_Itf, "Exigence pas encore implémentée", "Pour l'exigence "
                  + p_Requirement_Itf.getName() + ", il faut :\n" + "   - soit faire un appel à : " + p_Requirement_Itf
                  + ".set_versionImplem(Requirement_Itf.c_notImplementable)\n"
                  + "   - soit spécifier la version comme suit : " + p_Requirement_Itf + ".set_versionImplem(\""
                  + p_Requirement_Itf.get_versionModel() + "\")");
      }
      // Si exigence non implémentable
      else if (Requirement_Itf.c_notImplementable.equals(p_versionImplem))
      {
         // RAS
         LogManager.getLogger(RequirementsUtil.class).debug("Exigence non implémentable : " + p_Requirement_Itf);
      }
      // Si la version du modèle et de l'implémentation sont différentes
      else if (p_Requirement_Itf.get_versionModel().equals(p_versionImplem) == false)
      {
         throw new RequirementException(p_Requirement_Itf, "La version du modèle (="
                  + p_Requirement_Itf.get_versionModel() + ") ne correspond pas à la version implémentée (= "
                  + p_versionImplem + ")", "Vérifier l'implémentation de l'exigence : " + p_Requirement_Itf.getName());
      }
   }
}
