/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.DataType;
import org.eclipse.birt.chart.model.attribute.GroupingUnitType;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.data.Query;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.SeriesGrouping;
import org.eclipse.birt.chart.model.data.impl.QueryImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesGroupingImpl;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.model.api.ActionHandle;
import org.eclipse.birt.report.model.api.DataItemHandle;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.DesignFileException;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.ListGroupHandle;
import org.eclipse.birt.report.model.api.ListHandle;
import org.eclipse.birt.report.model.api.MasterPageHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ReportItemHandle;
import org.eclipse.birt.report.model.api.ResultSetColumnHandle;
import org.eclipse.birt.report.model.api.ScriptDataSetHandle;
import org.eclipse.birt.report.model.api.ScriptDataSourceHandle;
import org.eclipse.birt.report.model.api.SessionHandle;
import org.eclipse.birt.report.model.api.SimpleMasterPageHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.TextItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.command.ContentException;
import org.eclipse.birt.report.model.api.command.NameException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.ColumnHint;
import org.eclipse.birt.report.model.api.elements.structures.ComputedColumn;
import org.eclipse.birt.report.model.api.elements.structures.EmbeddedImage;
import org.eclipse.birt.report.model.api.elements.structures.HideRule;
import org.eclipse.birt.report.model.api.elements.structures.ResultSetColumn;
import org.eclipse.birt.report.model.api.elements.structures.SortKey;
import org.eclipse.birt.report.model.elements.interfaces.IDataSetModel;

import com.ibm.icu.util.ULocale;

import fr.spi4j.report.DataContainer;
import fr.spi4j.report.PageDesign_Itf;
import fr.spi4j.report.ReportException;
import fr.spi4j.report.util.BirtUtils;
import fr.spi4j.report.util.DataSetEventAdapter_Abs;
import fr.spi4j.report.util.ReportUtils;

/**
 * Création/gestion d'une page de type BIRT.
 * @author MINARM
 */
public class PageBIRT extends Page_Abs implements PageDesign_Itf
{
   /** Le nom du Report */
   private final String _reportName;

   /** Le nom de la MasterPage */
   private final String _MasterPageName;

   /** L'orientation de la page */
   private final PageOrientation_Enum _PageOrientation_Enum;

   private SessionHandle _session;

   private ReportDesignHandle _report;

   /**
    * Constructeur de la class PageBIRT.
    * @param p_reportName
    *           (In) Le nom logique du rapport.
    */
   public PageBIRT (final String p_reportName)
   {
      // Par défaut : on prend la 1ère MasterPage
      this(p_reportName, PageOrientation_Enum.PageOrientationAuto);
   }

   /**
    * Constructeur max.
    * @param p_reportName
    *           Le nom du report.
    * @param p_PageOrientation_Enum
    *           L'orientation désirée.
    */
   public PageBIRT (final String p_reportName, final PageOrientation_Enum p_PageOrientation_Enum)
   {
      this(p_reportName, null, p_PageOrientation_Enum);
   }

   /**
    * Constructeur max.
    * @param p_reportName
    *           Le nom du report.
    * @param p_MasterPageName
    *           Le nom de la MasterPage.
    * @param p_PageOrientation_Enum
    *           L'orientation désirée.
    */
   public PageBIRT (final String p_reportName, final String p_MasterPageName,
            final PageOrientation_Enum p_PageOrientation_Enum)
   {
      super();
      _reportName = p_reportName;
      _MasterPageName = p_MasterPageName;
      _PageOrientation_Enum = p_PageOrientation_Enum;
   }

   /**
    * Obtenir l'instance représentant le report.
    * @return Le ReportDesignHandle désiré.
    */
   public ReportDesignHandle get_report ()
   {
      return _report;
   }

   /**
    * Obtenir le nom du report.
    * @return Le nom désiré.
    */
   public String get_reportName ()
   {
      return _reportName;
   }

   /**
    * Obtenir la valeur définissant l'orientation.
    * @return L'orientation de la page.
    */
   public PageOrientation_Enum get_PageOrientation_Enum ()
   {
      return _PageOrientation_Enum;
   }

   /**
    * Le nom de la MasterPage sur laquelle se bas la page.
    * @return Le nom désiré.
    */
   public String get_MasterPageName ()
   {
      return _MasterPageName;
   }

   /**
    * Permet d'ouvrir un rapport.
    * @param p_reportName
    *           (In) Le nom du rapport à ouvrir.
    * @return Le rapport ouvert.
    */
   private ReportDesignHandle openReport (final String p_reportName)
   {
      try
      {
         // Ouverture du rapport
         final InputStream v_input = getClass().getResourceAsStream(p_reportName);
         if (v_input == null)
         {
            throw new IllegalArgumentException("La ressource \"" + p_reportName + "\" n'a pas été trouvée.");
         }
         try
         {
            return getSession().openDesign(p_reportName, v_input);
         }
         finally
         {
            v_input.close();
         }
      }
      catch (final IOException v_ex)
      {
         throw new ReportException("Problème d'I/O pour ouvrir \"" + p_reportName + "\"", v_ex);
      }
      catch (final DesignFileException v_ex)
      {
         throw new ReportException("Problème de design pour ouvrir \"" + p_reportName + "\"", v_ex);
      }
   }

   /**
    * @return la session avec instanciation si nécessaire.
    */
   private SessionHandle getSession ()
   {
      if (_session == null)
      {
         _session = BirtUtils.getInstance().getDesignEngine().newSessionHandle(ULocale.forLocale(Locale.getDefault()));
      }

      return _session;
   }

   /**
    * @return le rapport avec ouverture si nécessaire.
    */
   private ReportDesignHandle getReport ()
   {
      if (_report == null)
      {
         _report = openReport(_reportName);
      }
      return _report;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void writeReport (final OutputStream p_outputStream)
   {
      final IRunAndRenderTask v_task;
      try
      {
         try
         {
            // Recuperation du rapport contenant le modele de MasterPage
            final ReportDesignHandle v_rapportMasterPage = getReport();
            // Ouverture du rapport
            final IReportRunnable v_rapportRunnable = BirtUtils.getInstance().getReportEngine()
                     .openReportDesign(v_rapportMasterPage);
            final ReportDesignHandle v_ReportDesignHandle = (ReportDesignHandle) v_rapportRunnable.getDesignHandle();
            MasterPageHandle v_MasterPageHandle;
            // Si on a spécifié une MasterPage
            if (_MasterPageName != null)
            {
               v_MasterPageHandle = v_ReportDesignHandle.findMasterPage(_MasterPageName);

            }
            // Si pas de MasterPage spécifié : on prend le 1er
            else
            {
               // Recuperation et sauvegarde des Header/Footer modeles
               final SlotHandle v_masterPagesModele = v_rapportMasterPage.getMasterPages();
               final List<DesignElementHandle> v_tab_modele = v_masterPagesModele.getContents();
               // Obtenir la 1ère MasterPage
               v_MasterPageHandle = (MasterPageHandle) v_tab_modele.get(0);
            }

            // Si on a spécifié un orientation par API
            if (_PageOrientation_Enum != null)
            {
               // Si pas trouvé la MasterPage
               if (v_MasterPageHandle == null)
               {
                  throw new ReportException("Problème pour trouver la MasterPage \"" + _MasterPageName
                           + "\" dans le report \"" + _reportName + "\"");
               }
               // Si on a trouvé la MasterPage
               else
               {
                  v_MasterPageHandle.setOrientation(_PageOrientation_Enum.get_nomOrientationBirt());
               }
            }

            // Création d'une task pour lancer et faire un rendu des reports
            v_task = BirtUtils.getInstance().getReportEngine().createRunAndRenderTask(v_rapportRunnable);
            // Création de l'instance des options de rendu
            final PDFRenderOption v_options = new PDFRenderOption();

            // Cible de l'option
            v_options.setOutputStream(p_outputStream);
            // Option de rendu: format PDF
            v_options.setOutputFormat("pdf");

            // Appliquer les options
            v_task.setRenderOption(v_options);
            // Lancer la génération
            v_task.run();
            // Vérifier qu'il n'y a pas eu d'erreur d'exécution
            doVerifExecutionError(v_task);
            // Fermer le moteur
            v_task.close();
         }
         finally
         {
            // Fermer le rapport et la session
            close();
         }
      }
      catch (final SemanticException v_e)
      {
         throw new ReportException("Problème sémantique lors de l'écriture du report", v_e);
      }
      catch (final EngineException v_e)
      {
         throw new ReportException("Problème lors de l'écriture du report", v_e);
      }
   }

   /**
    * Vérifier s'il n'y a pas eu d'erreur durant l'exécution de la tâche d'édition.
    * @param p_task
    *           La tâche à vérifier.
    */
   private void doVerifExecutionError (final IRunAndRenderTask p_task)
   {
      final int v_status = p_task.getStatus();
      // S'il le statut est NOK
      if (v_status != IEngineTask.STATUS_SUCCEEDED)
      {
         // Version moteur Birt
         final String v_jarNameBirtEngine = p_task.getEngine().getVersion();
         // Obtenir le status en clair
         final String v_statusFmt = getStatusFmt(v_status);
         // Instancier une 'ReportException'
         final ReportException v_ReportException = new ReportException("Il y a eu une erreur lors (" + v_statusFmt
                  + ") de l'édition (moteur Birt = " + v_jarNameBirtEngine + ")");
         // Obtenir les erreurs survenues
         @SuppressWarnings("unchecked")
         final List<Throwable> v_lstException = p_task.getErrors();
         // Si aucune erreur mémorisée
         if (v_lstException.size() == 0)
         {
            throw new IllegalStateException("Problème entre le status (= " + v_statusFmt
                     + ") et p_task.getErrors() qui est vide");
         }
         // Si au moins une erreur mémorisée dans la tâche de report 'v_task'
         else
         {
            v_ReportException.initCause(v_lstException.get(0));
         }
         // Propoger l'exception
         throw v_ReportException;
      } // FIN if (v_task.getStatus() != v_task.STATUS_SUCCEEDED)
   }

   /**
    * Retourne un libellé pour le status d'une tâche Birt.
    * @param p_status
    *           int
    * @return String
    */
   private String getStatusFmt (final int p_status)
   {
      final String v_return;

      if (p_status == IEngineTask.STATUS_CANCELLED)
      {
         v_return = "STATUS_CANCELLED";
      }
      else if (p_status == IEngineTask.STATUS_FAILED)
      {
         v_return = "STATUS_FAILED";
      }
      else if (p_status == IEngineTask.STATUS_NOT_STARTED)
      {
         v_return = "STATUS_NOT_STARTED";
      }
      else if (p_status == IEngineTask.STATUS_RUNNING)
      {
         v_return = "STATUS_RUNNING";
      }
      else if (p_status == IEngineTask.STATUS_SUCCEEDED)
      {
         v_return = "STATUS_SUCCEEDED";
      }
      else
      {
         throw new IllegalArgumentException("Status inconnu (p_status=" + p_status + ")");
      }

      return v_return;
   }

   /**
    * Ferme le rapport et la session.
    */
   private void close ()
   {
      try
      {
         if (_report != null)
         {
            _report.close();
            _report = null;
         }
      }
      finally
      {
         if (_session != null)
         {
            try
            {
               _session.closeAll(false);
               _session = null;
            }
            catch (final IOException v_erreur)
            {
               throw new ReportException("Problème pour fermer le rapport", v_erreur);
            }
         }
      }
   }

   /**
    * Permet de créer un Data Source.
    * @param p_dataSourceName
    *           Le nom du Data Source à créer.
    */
   private void createDataSource (final String p_dataSourceName)
   {
      try
      {
         // Création d'une fabrique d'élément
         final ElementFactory v_elementFactory = getReport().getElementFactory();
         // Création du DataSource
         final ScriptDataSourceHandle v_dataSource = v_elementFactory.newScriptDataSource(p_dataSourceName);
         // Ajout du DataSource au rapport
         getReport().getDataSources().add(v_dataSource);
      }
      catch (final ContentException v_erreur)
      {
         throw new ReportException("Problème de contenu avec le DataSource \"" + p_dataSourceName + "\"", v_erreur);

      }
      catch (final NameException v_erreur)
      {
         throw new ReportException("Problème de nomage avec le DataSource \"" + p_dataSourceName + "\"", v_erreur);
      }
   }

   @Override
   public void createDataSet (final String p_dataSetName,
            final Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass, final String p_dataSourceName)
   {
      final String v_columnName_cour = null;
      try
      {
         // Création d'une fabrique d'élément
         final ElementFactory v_elementFactory = getReport().getElementFactory();
         // Creation du Data Source
         createDataSource(p_dataSourceName);
         // Création du Data Set
         final ScriptDataSetHandle v_dataSet = v_elementFactory.newScriptDataSet(p_dataSetName);
         // Affectation du 'Event Handler Class' au Data Set
         v_dataSet.setEventHandlerClass(p_eventHandlerClass.getName());
         // Affectation du Data Source au Data Set
         v_dataSet.setDataSource(p_dataSourceName);
         // Ajout du Data Set au rapport
         getReport().getDataSets().add(v_dataSet);

         // Définition des colonnes et des résultats du DataSet
         final PropertyHandle v_columnHintPropertyHandle = v_dataSet.getPropertyHandle(IDataSetModel.COLUMN_HINTS_PROP);
         final PropertyHandle v_resultSetPropertyHandle = v_dataSet.getPropertyHandle(IDataSetModel.RESULT_SET_PROP);

         // Obtenir la liste de 'DataContainer' à partir de la 'eventHandlerClass'
         final List<DataContainer> v_tab_DataContainerOfEventHandlerClass = extractDataContainer(p_eventHandlerClass);
         int v_i = 1;
         // Parcourir les éléments définissant la structure de données
         for (final DataContainer v_dataContainer : v_tab_DataContainerOfEventHandlerClass)
         {
            final ColumnHint v_columnHint = StructureFactory.createColumnHint();
            v_columnHint.setProperty("columnName", v_dataContainer.get_columnName());
            v_columnHintPropertyHandle.addItem(v_columnHint);

            final ResultSetColumn v_resultSetColumn = StructureFactory.createResultSetColumn();
            v_resultSetColumn.setColumnName(v_dataContainer.get_columnName());
            v_resultSetColumn.setPosition(v_i);
            v_resultSetColumn.setDataType(v_dataContainer.get_columnType());
            v_resultSetPropertyHandle.addItem(v_resultSetColumn);
            v_i++;
         }
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Problème dans le createDataSet \"" + p_dataSetName + "\" - v_columnName_cour="
                  + v_columnName_cour + " et p_dataSourceName=" + p_dataSourceName, v_erreur);
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public void makeContainerElement (final String p_containerElementName, final String p_dataSetName)
   {
      // Recherche de l'élément conteneur
      final ReportItemHandle v_reportItemHandle = findElementInReport(p_containerElementName);
      // Recherche du Data Set
      final DataSetHandle v_dataSet = getReport().findDataSet(p_dataSetName);
      if (v_dataSet == null)
      {
         throw new IllegalArgumentException("DataSet " + p_dataSetName + " non trouvé");
      }

      try
      {
         // Affectation du Data Set à l'élément conteneur
         v_reportItemHandle.setDataSet(v_dataSet);

         // Remplissage du DataBinding associé au DataSet
         final Iterator<ResultSetColumnHandle> v_computedIterator = v_dataSet.resultSetHintsIterator();
         while (v_computedIterator.hasNext())
         {
            final ResultSetColumnHandle v_resultSetColumnHandle = v_computedIterator.next();
            final String v_columnName = v_resultSetColumnHandle.getColumnName();
            final String v_dataType = v_resultSetColumnHandle.getDataType();
            final ComputedColumn v_computedColumn = StructureFactory.createComputedColumn();
            v_computedColumn.setName(v_columnName);
            v_computedColumn.setExpression("dataSetRow[\"" + v_columnName + "\"]");
            v_computedColumn.setDataType(v_dataType);
            v_reportItemHandle.addColumnBinding(v_computedColumn, true);
            // Le "data" devant est obligatoire : c'est une sorte de nameSpace pour Birt
            final DataItemHandle v_dataItemHandle = findElementInReport("data" + v_columnName);
            // On remplit l'élément data
            final ComputedColumn v_computedColumnForData = StructureFactory.createComputedColumn();
            v_computedColumnForData.setName("Colum" + v_columnName);
            v_computedColumnForData.setExpression("dataSetRow[\"" + v_columnName + "\"]");
            v_computedColumnForData.setDataType(v_dataType);
            v_dataItemHandle.addColumnBinding(v_computedColumnForData, true);
            v_dataItemHandle.setResultSetColumn("Colum" + v_columnName);
         }
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Problème de sémantic avec p_containerElementName=" + p_containerElementName
                  + " - p_dataSetName" + p_dataSetName, v_erreur);
      }
   }

   @Override
   public void makeListElementWithGroup (final String p_listElementName, final String p_dataSetName,
            final String p_dataGroupName)
   {
      try
      {
         // Recherche de l'élément List
         final ListHandle v_listHandle = findElementInReport(p_listElementName);
         // Création de type de groupage
         final SortKey v_sortKey = StructureFactory.createSortKey();
         v_sortKey.setKey("row[\"" + p_dataGroupName + "\"]");
         // Affectation du type de groupage au groupe
         final ListGroupHandle v_listGroupHandle = (ListGroupHandle) v_listHandle.getGroups().get(0);
         v_listGroupHandle.setKeyExpr("row[\"" + p_dataGroupName + "\"]");
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Problème de sémantic avec p_listElementName=" + p_listElementName
                  + " - p_dataSetName=" + p_dataSetName + " - p_dataGroupName" + p_dataGroupName, v_erreur);
      }

      makeContainerElement(p_listElementName, p_dataSetName);
   }

   /**
    * Permet de renseigner le Data Binding d'un Chart.
    * @param p_dataSetName
    *           Le Data Set permettant de remplir le Data Binding.
    * @param p_extendedItemHandle
    *           L'élément Chart.
    */
   @SuppressWarnings("unchecked")
   private void makeDataBindingChart (final String p_dataSetName, final ExtendedItemHandle p_extendedItemHandle)
   {
      try
      {
         // Recherche du Data Set
         final DataSetHandle v_dataSet = getReport().findDataSet(p_dataSetName);
         if (v_dataSet == null)
         {
            throw new IllegalArgumentException("DataSet " + p_dataSetName + " non trouvé");
         }
         final Iterator<ResultSetColumnHandle> v_computedIterator = v_dataSet.resultSetHintsIterator();

         // Remplissage du DataBinding associe au DataSet du Chart
         while (v_computedIterator.hasNext())
         {
            final ResultSetColumnHandle v_resultSetColumnHandle = v_computedIterator.next();
            final String v_columnName = v_resultSetColumnHandle.getColumnName();
            final ComputedColumn v_computedColumn = StructureFactory.createComputedColumn();
            v_computedColumn.setName(v_columnName);
            v_computedColumn.setDataType(v_resultSetColumnHandle.getDataType());
            v_computedColumn.setExpression("dataSetRow[\"" + v_columnName + "\"]");

            p_extendedItemHandle.addColumnBinding(v_computedColumn, true);

         }
         p_extendedItemHandle.setDataSet(v_dataSet);
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Problème de sémantic avec p_dataSetName=" + p_dataSetName
                  + " - p_extendedItemHandle=" + p_extendedItemHandle, v_erreur);
      }
   }

   @Override
   public void makeChartElementWithAxes (final String p_chartName, final String p_XAxisName, final String p_YAxisName,
            final String p_dataSetName)
   {
      // Recherche de l'élément Chart
      final ExtendedItemHandle v_extendedItemHandle = findElementInReport(p_chartName);
      try
      {
         final ChartWithAxes v_graphAvecAxes = (ChartWithAxes) v_extendedItemHandle.getReportItem().getProperty(
                  "chart.instance");

         // Axe X
         final Axis v_xAxisPrimary = v_graphAvecAxes.getPrimaryBaseAxes()[0];
         final SeriesDefinition v_xSeriesDefinition = v_xAxisPrimary.getSeriesDefinitions().get(0);
         // Définition d'un regroupement de donnée
         final SeriesGrouping v_dataGroupOnX = SeriesGroupingImpl.create();
         v_dataGroupOnX.setEnabled(true);
         v_dataGroupOnX.setGroupingInterval(1);
         v_dataGroupOnX.setGroupType(DataType.TEXT_LITERAL);
         v_dataGroupOnX.setAggregateExpression("Sum");
         v_dataGroupOnX.setGroupingUnit(GroupingUnitType.STRING_LITERAL);
         v_xSeriesDefinition.setGrouping(v_dataGroupOnX);
         final Series v_xSeries = v_xSeriesDefinition.getSeries().get(0);
         // Requete a executer dans l'axe X
         Query v_requete = QueryImpl.create("row[\"" + p_XAxisName + "\"]");
         v_xSeries.getDataDefinition().clear();
         v_xSeries.getDataDefinition().add(v_requete);

         // Axe Y
         final Axis v_yAxisPrimary = v_graphAvecAxes.getPrimaryOrthogonalAxis(v_xAxisPrimary);
         final SeriesDefinition v_ySeriesDefinition = v_yAxisPrimary.getSeriesDefinitions().get(0);
         final Series v_ySeries = v_ySeriesDefinition.getSeries().get(0);
         // Requete a exécuter dans l'axe Y
         v_requete = QueryImpl.create("row[\"" + p_YAxisName + "\"]");
         v_ySeries.getDataDefinition().clear();
         v_ySeries.getDataDefinition().add(v_requete);
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Problème de sémantic avec p_chartName=" + p_chartName + " - p_dataSetName="
                  + p_dataSetName + " - p_XAxisName=" + p_XAxisName + " - p_YAxisName=" + p_YAxisName, v_erreur);
      }

      // Remplissage du DataBinding associe au DataSet du Chart
      makeDataBindingChart(p_dataSetName, v_extendedItemHandle);
   }

   @Override
   public void makeChartElementWithoutAxes (final String p_chartName, final String p_dataCategoryName,
            final String p_dataNameSliceSize, final String p_dataSetName)
   {
      // Recherche de l'element Chart
      final ExtendedItemHandle v_extendedItemHandle = findElementInReport(p_chartName);
      try
      {
         final ChartWithoutAxes v_graphSansAxes = (ChartWithoutAxes) v_extendedItemHandle.getReportItem().getProperty(
                  "chart.instance");

         // Legende
         final SeriesDefinition v_legendSeriesDefinition = v_graphSansAxes.getSeriesDefinitions().get(0);
         // Definition d'un regroupement de donnees
         final SeriesGrouping v_dataGroupOnLegend = SeriesGroupingImpl.create();
         v_dataGroupOnLegend.setEnabled(true);
         v_dataGroupOnLegend.setGroupingInterval(1);
         v_dataGroupOnLegend.setGroupType(DataType.TEXT_LITERAL);
         v_dataGroupOnLegend.setAggregateExpression("Sum");
         v_dataGroupOnLegend.setGroupingUnit(GroupingUnitType.STRING_LITERAL);
         v_legendSeriesDefinition.setGrouping(v_dataGroupOnLegend);
         final Series v_legend = v_legendSeriesDefinition.getSeries().get(0);
         // Requete a executer dans le Category Definition
         Query v_requete = QueryImpl.create("row[\"" + p_dataCategoryName + "\"]");
         v_legend.getDataDefinition().clear();
         v_legend.getDataDefinition().add(v_requete);

         // Valeur
         final SeriesDefinition v_valeurSeriesDefinition = SeriesDefinitionImpl.create();
         final PieSeries v_valeur = (PieSeries) PieSeriesImpl.create();
         // Requete a executer dans le Slice Size Definition
         v_requete = QueryImpl.create("row[\"" + p_dataNameSliceSize + "\"]");
         v_valeur.getDataDefinition().clear();
         v_valeur.getDataDefinition().add(v_requete);

         // Création d'une 'Series' supplementaire puis ajout
         v_legendSeriesDefinition.getSeriesDefinitions().add(v_valeurSeriesDefinition);
         v_valeurSeriesDefinition.getSeries().add(v_valeur);
         // Suppression de la 1ere 'Series'
         v_legendSeriesDefinition.getSeriesDefinitions().remove(0);
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Probème de sémantic avec p_chartName=" + p_chartName + " - p_dataCategoryName="
                  + p_dataCategoryName + " - p_dataNameSliceSize=" + p_dataNameSliceSize + " - p_dataSetName="
                  + p_dataSetName, v_erreur);
      }

      // Remplissage du DataBinding associe au DataSet du Chart
      makeDataBindingChart(p_dataSetName, v_extendedItemHandle);
   }

   @Override
   @SuppressWarnings("unchecked")
   public void changeMasterPage (final String p_reportMasterPageModelName)
   {
      try
      {
         // Recuperation du rapport contenant le modele de MasterPage
         final ReportDesignHandle v_rapportMasterPage = openReport(p_reportMasterPageModelName);
         final SlotHandle v_headerModele;
         final SlotHandle v_footerModele;
         try
         {
            // Recuperation et sauvegarde des Header/Footer modeles
            final SlotHandle v_masterPagesModele = v_rapportMasterPage.getMasterPages();
            final List<DesignElementHandle> v_tab_modele = v_masterPagesModele.getContents();
            final SimpleMasterPageHandle v_simpleMasterPageModele = (SimpleMasterPageHandle) v_tab_modele.get(0);
            v_headerModele = v_simpleMasterPageModele.getPageHeader();
            v_footerModele = v_simpleMasterPageModele.getPageFooter();
         }
         finally
         {
            v_rapportMasterPage.close();
            // Fermeture du du rapport contenant le modele de MasterPage
         }

         // Suppression de la MasterPage du report
         final SlotHandle v_masterPages = getReport().getMasterPages();
         final List<DesignElementHandle> v_tab_masterPage = v_masterPages.getContents();
         final SimpleMasterPageHandle v_simpleMasterPageNew = (SimpleMasterPageHandle) v_tab_masterPage.get(0);

         List<DesignElementHandle> v_tab_designElement;

         // Suppression des elements du Header de la MasterPage principale
         v_tab_designElement = v_simpleMasterPageNew.getPageHeader().getContents();
         for (final DesignElementHandle v_designElementHandle : v_tab_designElement)
         {
            v_designElementHandle.drop();
         }
         // Ajout du nouveau header a partir du Header modele
         v_tab_designElement = v_headerModele.getContents();
         for (final DesignElementHandle v_designElementHandle : v_tab_designElement)
         {
            v_simpleMasterPageNew.getPageHeader().add(v_designElementHandle);
         }

         // Suppression des elements du Footer de la MasterPage principale
         v_tab_designElement = v_simpleMasterPageNew.getPageFooter().getContents();
         for (final DesignElementHandle v_designElementHandle : v_tab_designElement)
         {
            v_designElementHandle.drop();
         }
         // Ajout du nouveau Footer a partir du Footer modele
         v_tab_designElement = v_footerModele.getContents();
         for (final DesignElementHandle v_designElementHandle : v_tab_designElement)
         {
            v_simpleMasterPageNew.getPageFooter().add(v_designElementHandle);
         }
      }
      catch (final ContentException v_erreur)
      {
         throw new ReportException("Problème de contenu avec le reportMasterPageModelName \""
                  + p_reportMasterPageModelName + "\"", v_erreur);

      }
      catch (final NameException v_erreur)
      {
         throw new ReportException("Problème de nomage avec le reportMasterPageModelName \""
                  + p_reportMasterPageModelName + "\"", v_erreur);
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Problème de sémantic avec le reportMasterPageModelName \""
                  + p_reportMasterPageModelName + "\"", v_erreur);
      }
   }

   @Override
   public void createInternalLink (final String p_labelElementName, final String p_bookmarkName)
   {
      try
      {
         // Recherche de l'element Label sur lequel appliquer le lien
         final LabelHandle v_labelHandle = findElementInReport(p_labelElementName);
         final ActionHandle v_ActionHandle = v_labelHandle.getActionHandle();
         // Si pas d'ActionHandle pour le lien HyperText
         if (v_ActionHandle == null)
         {
            throw new IllegalArgumentException("Problème avec l'ActionHandle non trouvé sur l'élément \""
                     + p_labelElementName + "\" - réalisation du bookmark \"" + p_bookmarkName + "\" impossible");
         }
         // Transformation de l'element Label en lien interne
         v_ActionHandle.setLinkType(DesignChoiceConstants.ACTION_LINK_TYPE_BOOKMARK_LINK);
         v_ActionHandle.setTargetBookmark(p_bookmarkName);
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Problème sémantic avec p_labelElementName=" + p_labelElementName
                  + " - p_bookmarkName=" + p_bookmarkName, v_erreur);
      }
   }

   @Override
   public void createURLLink (final String p_textElementName, final String p_URL)
   {
      try
      {
         // Recherche de l'element Text sur lequel appliquer le lien
         final TextItemHandle v_TextItemHandle = findElementInReport(p_textElementName);
         // Transformation de l'element Text en lien href
         v_TextItemHandle.setContentType("html");
         v_TextItemHandle.setContent("<A HREF=\"" + p_URL + "\">" + p_URL + "</A>");
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Problème pour faire le createURLLink sur l'élément \"" + p_textElementName
                  + "\" avec p_URL=" + p_URL, v_erreur);
      }
   }

   @Override
   public void setText (final String p_labelElementName, final String p_text)
   {
      try
      {
         // Obtenir l'élément
         final ReportItemHandle v_ReportItemHandle = findElementInReport(p_labelElementName);
         // Si c'est du type 'Label'
         if (v_ReportItemHandle instanceof LabelHandle)
         {
            final LabelHandle v_LabelHandle = (LabelHandle) v_ReportItemHandle;
            // Affecter la valeur au label 'p_labelElementName'
            v_LabelHandle.setText(p_text);
         }
         // Si c'est du type 'Text'
         else if (v_ReportItemHandle instanceof TextItemHandle)
         {
            final TextItemHandle v_TextItemHandle = (TextItemHandle) v_ReportItemHandle;
            // Affecter la valeur au text 'p_labelElementName'
            v_TextItemHandle.setContent(p_text);
         }
         // Cas non prévu : type d'élément incorrect
         else
         {
            throw new IllegalArgumentException("Problème pour affecter la valeur dans l'élément \""
                     + p_labelElementName + "\" (de type \"" + v_ReportItemHandle.getClass().getName()
                     + "\") - dans le rptDesign il faut que cet élément soit de type 'Label' ou de type 'Text'");
         }
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Problème pour faire le setLabelText sur l'élément \"" + p_labelElementName + "\"",
                  v_erreur);
      }
   }

   @Override
   public void hideVisibility (final String p_elementName)
   {
      // Recherche de l'element a masquer
      final ReportItemHandle v_element = findElementInReport(p_elementName);
      // Traitement pour masquer l'element
      final HideRule v_hideRule = StructureFactory.createHideRule();
      v_hideRule.setFormat("all");
      v_hideRule.setExpression("true");
      final PropertyHandle v_propertyHandle = v_element.getPropertyHandle("visibility");

      try
      {
         v_propertyHandle.addItem(v_hideRule);
      }
      catch (final SemanticException v_erreur)
      {
         throw new ReportException("Problème pour masquer l'élément \"" + p_elementName + "\"", v_erreur);
      }
   }

   @Override
   public void makePictureElementEmbedded (final String p_imageElementName, final String p_imageToAddName,
            final String p_imageToAddType)
   {
      final byte[] v_bytes = ReportUtils.transformPictureToByte(p_imageToAddName);
      makePictureElementEmbedded(p_imageElementName, v_bytes, p_imageToAddType);
   }

   @Override
   public void makePictureElementEmbedded (final String p_imageElementName, final byte[] p_imageToAdd,
            final String p_imageToAddType)
   {
      // Recherche de l'element image
      final EmbeddedImage v_image = getReport().findImage(p_imageElementName);
      if (v_image == null)
      {
         throw new IllegalArgumentException("Image " + p_imageElementName + " non trouvée");
      }

      // Setter du type de l'image
      v_image.setType(p_imageToAddType);

      // Setter de l'url source de l'image (en tableau de byte)
      v_image.setData(p_imageToAdd);
   }

   @Override
   public void makeReportWithContainer (final String p_dataSourceName, final String p_dataSetName,
            final Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass, final String p_containerElementName)
   {
      createDataSet(p_dataSetName, p_eventHandlerClass, p_dataSourceName);

      // Affectation du Data Set à l'élément Table
      makeContainerElement(p_containerElementName, p_dataSetName);
   }

   @Override
   public void makeReportWithListAndGroup (final String p_dataSourceName, final String p_dataSetName,
            final Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass, final String p_listElementName,
            final String p_dataGroupName)
   {
      createDataSet(p_dataSetName, p_eventHandlerClass, p_dataSourceName);

      // Affectation du Data Set a l'element Table
      makeListElementWithGroup(p_listElementName, p_dataSetName, p_dataGroupName);
   }

   @Override
   public void makeReportWithChartWithAxes (final String p_dataSourceName, final String p_dataSetName,
            final Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass, final String p_chartName,
            final String p_XAxisName, final String p_YAxisName)
   {
      // Vérifier que les colonnes sont présentes dans les 'DataContainer'
      doVerifDataContainer(p_eventHandlerClass, p_XAxisName, p_YAxisName);
      // Créer le DataSet
      createDataSet(p_dataSetName, p_eventHandlerClass, p_dataSourceName);

      // Affectation du Data Set à l'élément Chart
      makeChartElementWithAxes(p_chartName, p_XAxisName, p_YAxisName, p_dataSetName);
   }

   @Override
   public void makeReportWithChartWithoutAxes (final String p_dataSourceName, final String p_dataSetName,
            final Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass, final String p_chartName,
            final String p_dataCategoryName, final String p_dataNameSliceSize)
   {
      // Vérifier que les colonnes sont présentes dans les 'DataContainer'
      doVerifDataContainer(p_eventHandlerClass, p_dataCategoryName, p_dataNameSliceSize);
      // Créer le DataSet
      createDataSet(p_dataSetName, p_eventHandlerClass, p_dataSourceName);
      // Affectation du Data Set a l'element Chart
      makeChartElementWithoutAxes(p_chartName, p_dataCategoryName, p_dataNameSliceSize, p_dataSetName);
   }

   /**
    * Vérifier que les colonnes sont présentes dans les 'DataContainer'.
    * @param p_eventHandlerClass
    *           Le 'EventHandler' contenant une liste de 'DataContainer' à vérifier.
    * @param p_dataCategoryName
    *           Le nom de la colonne 1ère colonne à vérifier (= le nom pour le SUM par exemple).
    * @param p_dataNameSliceSize
    *           Le nom de la colonne 2ème colonne à vérifier (= le nom pour la taille par exemple).
    */
   private void doVerifDataContainer (final Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass,
            final String p_dataCategoryName, final String p_dataNameSliceSize)
   {
      // Obtenir la liste de 'DataContainer' à partir de la 'eventHandlerClass'
      final List<DataContainer> v_tab_DataContainerOfEventHandlerClass = extractDataContainer(p_eventHandlerClass);
      // Vérifier que la colonne 'p_dataCategoryName' est présente dans 'v_tab_DataContainerOfEventHandlerClass'
      verifDataContainer(v_tab_DataContainerOfEventHandlerClass, p_dataCategoryName);
      // Vérifier que la colonne 'p_dataNameSliceSize' est présente dans 'v_tab_DataContainerOfEventHandlerClass'
      verifDataContainer(v_tab_DataContainerOfEventHandlerClass, p_dataNameSliceSize);
   }

   /**
    * Extraire la liste de 'DataContainer' du 'EventHandlerClass'.
    * @param p_eventHandlerClass
    *           La classe dont on veut extraire la liste de 'DataContainer'.
    * @return La liste désirée.
    */
   private List<DataContainer> extractDataContainer (final Class<? extends DataSetEventAdapter_Abs> p_eventHandlerClass)
   {
      final List<DataContainer> v_tab_DataContainer;
      try
      {
         // Instancier le EventHandler sans initialiser les données
         final DataSetEventAdapter_Abs v_DataSetEventAdapter_Abs = p_eventHandlerClass.getDeclaredConstructor().newInstance();
         // Obtenir les 'DataContainer' (i.e. les colonnes à vérifier)
         v_tab_DataContainer = v_DataSetEventAdapter_Abs.findListDataContainer();
      }
      catch (final Exception v_e)
      {
         throw new ReportException("Problème lors de l'instanciation de 'p_eventHandlerClass'", v_e);
      }

      return v_tab_DataContainer;
   }

   /**
    * Vérifier que la colonne existe dans le tableau spécifié.
    * @param p_tab_DataContainer
    *           Le tableau de 'DataContainer' (= le tableau de colonnes).
    * @param p_columnName
    *           La colonne à vérifier.
    */
   private void verifDataContainer (final List<DataContainer> p_tab_DataContainer, final String p_columnName)
   {
      boolean v_isTrouve = false;

      // Parcourir la liste de 'DataContainer' jusqu'a trouvé la colonne
      for (int v_i = 0; (v_i < p_tab_DataContainer.size()) && (v_isTrouve == false); v_i++)
      {
         final DataContainer v_DataContainer = p_tab_DataContainer.get(v_i);
         // Si on a trouvé la colonne 'p_columnName'
         if (v_DataContainer.get_columnName().equals(p_columnName))
         {
            v_isTrouve = true;
         }
      }

      // Si pas trouvé
      if (v_isTrouve == false)
      {
         throw new ReportException("La colonne \"" + p_columnName
                  + "\" n'existe pas dans la liste des colonnes (i.e. DataContainer)");
      }
   }

   /**
    * Trouve un élément dans le rptdesign selon son nom.
    * @param <T>
    *           Type
    * @param p_name
    *           Nom de l'élément
    * @return L'élément
    */
   @SuppressWarnings("unchecked")
   private <T extends DesignElementHandle> T findElementInReport (final String p_name)
   {
      final DesignElementHandle v_element = getReport().findElement(p_name);
      if (v_element == null)
      {
         throw new IllegalArgumentException("Elément \"" + p_name + "\" non trouvé dans le rptDesign \"" + _reportName
                  + "\"");
      }
      return (T) v_element;
   }

   @Override
   public void save (final OutputStream p_output) throws IOException
   {
      // serialize identique à _report.saveAs, mais dans un flux
      getReport().serialize(p_output);
   }

   @Override
   public String toString ()
   {
      return getClass().getSimpleName() + "[_reportName=" + _reportName + " - _MasterPageName=" + _MasterPageName
               + " - _PageOrientation_Enum=" + _PageOrientation_Enum + "]";
   }
}
