package fr.spi4j.report.util;

import java.util.List;

import org.eclipse.birt.report.engine.api.script.eventadapter.ScriptedDataSetEventAdapter;

import fr.spi4j.report.DataContainer;

/**
 * Classe permettant de définir le comportement pour les données d'édition.
 * @author MINARM
 */
abstract public class DataSetEventAdapter_Abs extends ScriptedDataSetEventAdapter
{
   /**
    * Constructeur par défaut.
    */
   public DataSetEventAdapter_Abs ()
   {
      super();
   }

   /**
    * Création de la liste des colonnes et des types à afficher.
    * @return La liste décrivant la structure de données de l'édition.
    */
   abstract public List<DataContainer> findListDataContainer ();

}
