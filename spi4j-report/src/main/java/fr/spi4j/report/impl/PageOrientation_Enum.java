package fr.spi4j.report.impl;

import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;

/**
 * Différentes valeurs possibles pour l'orientation. Si on ajoute un autre moteur que Birt, il faut ajouter une valeur dans le contructeur private.
 */
public enum PageOrientation_Enum
{
   /** Mode automatique */
   PageOrientationAuto(DesignChoiceConstants.PAGE_ORIENTATION_AUTO),
   /** Mode paysage */
   PageOrientationLandScape(DesignChoiceConstants.PAGE_ORIENTATION_LANDSCAPE),
   /** Mode portrait */
   PageOrientationPortrait(DesignChoiceConstants.PAGE_ORIENTATION_PORTRAIT);

   /** Le nom pour le moteur Birt pour l'orientation */
   private String _nomOrientationBirt;

   /**
    * Constructeur.
    * @param p_nomOrientationBirt
    *           String
    */
   private PageOrientation_Enum (final String p_nomOrientationBirt)
   {
      _nomOrientationBirt = p_nomOrientationBirt;
   }

   /**
    * Le nom Birt pour spécifier l'orientation.
    * @return Le nom à spécifier à Birt.
    */
   public String get_nomOrientationBirt ()
   {
      return _nomOrientationBirt;
   }
}
