/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.requirement;

import fr.spi4j.requirement.Requirement_Itf;
import fr.spi4j.requirement.RequirementsUtil;

/**
 * Enumération des exigences pour les tests.
 * @author MINARM.
 */
public enum Requirement_EnumForTest implements Requirement_Itf
{
   /**
    * FCT_PERS_01 : <br/>
    * Lors d'une recherche de personnes par grade, les personnes trouvées ont toutes le même grade <br/>
    * Le grade peut être "Lieutenant", "Colonel", ... <br/>
    * Statement sur plusieurs lignes !
    */
   // Exigence utilisée dans : PersonneService.findListPersonneByGrade
   REQ_FCT_PERS_01("FCT_PERS_01", "0.1"),

   /**
    * TEC_PERS_02 : <br/>
    * Exigence technique sur une ligne
    */
   // Exigence utilisée dans : PersonneService.findListPersonneByGrade
   // Exigence utilisée dans : AdresseService.findListAdresseByVille
   // Exigence utilisée dans : PrintUtilsService.printPersonne
   // Exigence utilisée dans : PrintUtilsService.printPersonnes
   REQ_TEC_PERS_02("TEC_PERS_02", "0.1");

   /** Id. */
   private String _id;

   /** Nom. */
   private String _name;

   /** La version de l'exigence dans le modèle Requirement */
   private final String _versionModel;

   /** La version implémentée. */
   private String _versionImplem;

   /**
    * Constructeur de l'énumeration de l'exigence.
    * @param p_name
    *           le nom.
    * @param p_versionModel
    *           La version de l'exigence dans la modélisation.
    */
   private Requirement_EnumForTest (final String p_name, final String p_versionModel)
   {
      _id = toString().substring("REQ_".length());
      _name = p_name;
      _versionModel = p_versionModel;
   }

   /**
    * @return l'id de l'exigence.
    */
   @Override
   public String getId ()
   {
      return _id;
   }

   /**
    * @return le nom de l'exigence.
    */
   @Override
   public String getName ()
   {
      return _name;
   }

   @Override
   public void set_versionImplem ()
   {
      set_versionImplem(c_notImplemented);
   }

   @Override
   public String get_versionImplem ()
   {
      return _versionImplem;
   }

   @Override
   public void set_versionImplem (final String p_versionImplem)
   {
      // Vérifier que la version implémentée est égale à celle de la modélisation
      RequirementsUtil.checkRequirementVersion(this, p_versionImplem);
      // Si version pas encore affectée
      _versionImplem = p_versionImplem;
   }

   @Override
   public String get_versionModel ()
   {
      return _versionModel;
   }
}
