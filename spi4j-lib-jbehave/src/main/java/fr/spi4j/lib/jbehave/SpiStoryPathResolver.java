/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import org.jbehave.core.Embeddable;
import org.jbehave.core.io.AbstractStoryPathResolver;

/**
 * Classe de recherche des .story.
 * @author MINARM
 */
public class SpiStoryPathResolver extends AbstractStoryPathResolver
{

   /**
    * Constructeur par défaut. (Extension par défaut = .story).
    */
   public SpiStoryPathResolver ()
   {
      super(".story");
   }

   @Override
   protected String resolveName (final Class<? extends Embeddable> p_embeddableClass)
   {
      String v_name = p_embeddableClass.getSimpleName();
      v_name = removeSuffix(v_name, "Story");
      v_name = removeSuffix(v_name, "_");
      return v_name;
   }

   /**
    * Suppression d'un suffixe dans une chaine de caractères.
    * @param p_name
    *           la chaine initiale
    * @param p_suffix
    *           le suffixe à supprimer
    * @return la chaine sans son suffixe s'il a pu être trouvé, la chaine initiale sinon
    */
   private String removeSuffix (final String p_name, final String p_suffix)
   {
      String v_name = p_name;
      if (v_name.endsWith(p_suffix))
      {
         v_name = v_name.substring(0, v_name.length() - p_suffix.length());
      }
      return v_name;
   }

}
