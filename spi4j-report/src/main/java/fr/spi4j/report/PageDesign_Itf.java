/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report;

import java.io.IOException;
import java.io.OutputStream;

import fr.spi4j.report.util.DataSetEventAdapter_Abs;

/**
 * Classe des méthodes de Page liées au Design Engine.
 * @author MINARM
 */
public interface PageDesign_Itf extends Page_Itf
{
   /**
    * Permet de créer un DataSource et un DataSet.
    * @param p_dataSetName
    *           Le nom du Data Set à créer.
    * @param p_eventHandlerClass
    *           La classe de l'Event Handler.
    * @param p_dataSourceName
    *           Le nom du Data Source à associer.
    */
   void createDataSet (String p_dataSetName, Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass,
            String p_dataSourceName);

   /**
    * Permet de renseigner un élément List contenant un élément Group.
    * @param p_listElementName
    *           Le nom de l'élément list.
    * @param p_dataSetName
    *           Le nom du Data Set à associer.
    * @param p_dataGroupName
    *           Le nom des données servant de critère de groupement.
    */
   void makeListElementWithGroup (String p_listElementName, String p_dataSetName, String p_dataGroupName);

   /**
    * Permet de renseigner un élément Chart avec axes X,Y, type Histogramme.
    * @param p_chartName
    *           Le nom de l'élément Chart avec axes.
    * @param p_XAxisName
    *           Le nom des données à placer dans l'axe X.
    * @param p_YAxisName
    *           Le nom des données à placer dans l'axe Y.
    * @param p_dataSetName
    *           Le nom du Data Set permettant de remplir le DataBinding (permet d'agréger les champs du graphique).
    */
   void makeChartElementWithAxes (String p_chartName, String p_XAxisName, String p_YAxisName, String p_dataSetName);

   /**
    * Permet de renseigner un élément Chart avec axes, type 'Camembert'.
    * @param p_chartName
    *           Le nom de l'élément Chart sans axe.
    * @param p_dataCategoryName
    *           Le nom des données à placer dans le Category Definition.
    * @param p_dataNameSliceSize
    *           Le nom des données à placer dans le Slice Size Definition.
    * @param p_dataSetName
    *           Le nom du Data Set permettant de remplir le DataBinding (permet d'agréger les champs du graphique).
    */
   void makeChartElementWithoutAxes (String p_chartName, String p_dataCategoryName, String p_dataNameSliceSize,
            String p_dataSetName);

   /**
    * Permet de renseigner un élément Table ou List.
    * @param p_containerElementName
    *           Le nom de l'élément conteneur.
    * @param p_dataSetName
    *           Le nom du Data Set à associer.
    */
   void makeContainerElement (String p_containerElementName, String p_dataSetName);

   /**
    * Permet de créer un Data Source, un Data Set et de renseigner les éléments Data et l'élément conteneur (Table ou List)
    * @param p_dataSourceName
    *           Le nom du Data Source a créer.
    * @param p_dataSetName
    *           Le nom du Data Set a créer.
    * @param p_eventHandlerClass
    *           La classe de l'Event Handler.
    * @param p_containerElementName
    *           Le nom du conteneur (Table ou List) à créer.
    */
   void makeReportWithContainer (String p_dataSourceName, String p_dataSetName,
            Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass, String p_containerElementName);

   /**
    * Permet de créer un Data Source, un Data Set et de renseigner l'élément Chart avec axes X,Y, type Histogramme.
    * @param p_dataSourceName
    *           Le nom du Data Source a créer.
    * @param p_dataSetName
    *           Le nom du Data Set a créer.
    * @param p_eventHandlerClass
    *           La classe de l'Event Handler.
    * @param p_chartName
    *           Le nom de l'élément Chart avec axes.
    * @param p_XAxisName
    *           Le nom des données a placer dans l'axe X.
    * @param p_YAxisName
    *           Le nom des données a placer dans l'axe Y.
    */
   void makeReportWithChartWithAxes (String p_dataSourceName, String p_dataSetName,
            Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass, String p_chartName, String p_XAxisName,
            String p_YAxisName);

   /**
    * Permet de créer un Data Source, un Data Set et de renseigner l'élément Chart sans axes, type 'Camembert'.
    * @param p_dataSourceName
    *           Le nom du Data Source a créer.
    * @param p_dataSetName
    *           Le nom du Data Set a créer.
    * @param p_eventHandlerClass
    *           La classe de l'Event Handler Class.
    * @param p_chartName
    *           Le nom de l'élément Chart sans axes.
    * @param p_dataCategoryName
    *           Le nom des données a placer dans le Category Definition.
    * @param p_dataNameSliceSize
    *           Le nom des données a placer dans le Slice Size Definition.
    */
   void makeReportWithChartWithoutAxes (String p_dataSourceName, String p_dataSetName,
            Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass, String p_chartName,
            String p_dataCategoryName, String p_dataNameSliceSize);

   /**
    * Permet de créer un Data Source, un Data Set et de renseigner les éléments Data et l'élément List avec la condition de groupage.
    * @param p_dataSourceName
    *           Le nom du Data Source à créer.
    * @param p_dataSetName
    *           Le nom du Data Set à créer.
    * @param p_eventHandlerClass
    *           La classe de l'Event Handler.
    * @param p_listElementName
    *           Le nom de l'élément List.
    * @param p_dataGroupName
    *           Le nom de la condition de groupage.
    */
   void makeReportWithListAndGroup (String p_dataSourceName, String p_dataSetName,
            Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass, String p_listElementName,
            String p_dataGroupName);

   /**
    * Permet de créer un lien interne sur un élément de type 'Label' pour pointer sur un 'Bookmark'.
    * @param p_labelElementName
    *           Le nom de l'élément Label sur lequel appliquer le lien.
    * @param p_bookmarkName
    *           Le nom du bookmark sur lequel faire pointer le lien.
    */
   void createInternalLink (String p_labelElementName, String p_bookmarkName);

   /**
    * Permet de créer un lien de type URL sur un élément de type 'Text'.
    * @param p_textElementName
    *           Le nom de l'élément Text sur lequel appliquer le lien.
    * @param p_URL
    *           L'URL vers laquelle faire pointer le lien.
    */
   void createURLLink (String p_textElementName, String p_URL);

   /**
    * Permet d'affecter la valeur (qui peut-être multi-lignes) sur un 'Label' ou un 'Text' nommé.
    * @param p_labelElementName
    *           Le nom de l'élément Label sur lequel affecter le texte.
    * @param p_text
    *           Le texte à affecter.
    */
   void setText (String p_labelElementName, String p_text);

   /**
    * Permet de masquer la visibilité d'un élément.
    * @param p_elementName
    *           L'élément à masquer.
    */
   void hideVisibility (String p_elementName);

   /**
    * Permet de renseigner un élément image </br> Ajout de type embedded.
    * @param p_imageElementName
    *           Le nom de l'élément image.
    * @param p_imageToAddName
    *           Le nom de l'image à associer à l'élément image.
    * @param p_imageToAddType
    *           Type de l'image parmi DesignChoiceConstants.IMAGE_TYPE_IMAGE_*
    */
   void makePictureElementEmbedded (String p_imageElementName, String p_imageToAddName, String p_imageToAddType);

   /**
    * Permet de renseigner un élément image </br> Ajout de type embedded.
    * @param p_imageElementName
    *           Le nom de l'élément image.
    * @param p_imageToAdd
    *           Le contenu de l'image à associer à l'élément image.
    * @param p_imageToAddType
    *           Type de l'image parmi DesignChoiceConstants.IMAGE_TYPE_IMAGE_*
    */
   void makePictureElementEmbedded (final String p_imageElementName, final byte[] p_imageToAdd,
            final String p_imageToAddType);

   /**
    * Permet de changer la Master Page d'un rapport.
    * @param p_reportMasterPageModelName
    *           Le nom du rapport contenant la Master Page servant de modèle.
    */
   void changeMasterPage (String p_reportMasterPageModelName);

   /**
    * Enregistre un rptdesign qui a été modifié avec les méthodes ci-dessous : <br/>
    * utile en test pour comparer le résultat de ces méthodes avec ce qui serait fait avec le designer BIRT.
    * @param p_output
    *           OutputStream
    * @throws IOException
    *            e
    */
   void save (OutputStream p_output) throws IOException;
}
