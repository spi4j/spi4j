/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.api.script.IUpdatableDataSetRow;
import org.eclipse.birt.report.engine.api.script.instance.IDataSetInstance;

import fr.spi4j.report.DataContainer;
import fr.spi4j.report.ReportException;

/**
 * Conteneur de beans abstrait (à hériter pour définir une classe correspondant aux données souhaitées et passées dans le constructeur). <br/>
 * En alternative à l'utilisation de cette classe, il suffirait d'hériter de ScriptedDataSetEventAdapter <br/>
 * et d'appeler dans la méthode fetch : "p_row.setColumnValue(v_attributeName, v_value)" pour chaque attribut.
 * @author MINARM
 */
public abstract class BirtDataSetBean_Abs extends DataSetEventAdapter_Abs
{
   private List<DataContainer> _listDataContainer;

   private List<?> _listBeans;

   private Map<DataContainer, Method> _gettersByDataContainer;

   private int _index;

   /**
    * Constructeur initialisant les données.
    */
   public BirtDataSetBean_Abs ()
   {
      super();
   }

   /**
    * Methode de la classe ScriptedDataSetEventAdapter
    * @param p_dataSet
    *           Le data set.
    * @param p_reportContext
    *           Report context.
    */
   @Override
   public void beforeOpen (final IDataSetInstance p_dataSet, final IReportContext p_reportContext)
   {
      // Initialiser les données
      this._listDataContainer = findListDataContainer();
      this._listBeans = findListData();
      if (_listDataContainer == null || _listDataContainer.isEmpty())
      {
         throw new IllegalArgumentException("listDataContainer ne doit pas être null ni vide");
      }
      if (_listBeans == null)
      {
         throw new IllegalArgumentException("listBeans ne doit pas être null");
      }

      _gettersByDataContainer = buildGetters(_listBeans);
   }

   /**
    * Methode de la classe ScriptedDataSetEventAdapter
    * @param p_dataSet
    *           Le dataSet.
    * @param p_row
    *           ?
    * @return true ou false selon la taille du tableau (mais ce n'est pas très clair).
    */
   @Override
   public boolean fetch (final IDataSetInstance p_dataSet, final IUpdatableDataSetRow p_row)
   {
      // si on ne veut pas utiliser

      boolean v_return = true;

      if (_index >= _listBeans.size())
      {
         _index = 0;
         v_return = false;
      }

      try
      {
         final Object v_bean = _listBeans.get(_index);
         for (final Map.Entry<DataContainer, Method> v_entry : _gettersByDataContainer.entrySet())
         {
            final DataContainer v_dataContainer = v_entry.getKey();
            final Method v_method = v_entry.getValue();
            // récupère la valeur en appelant le getter par réflexion
            final Object v_value = v_method.invoke(v_bean, (Object[]) null);
            // définit la valeur pour Birt
            final String v_columnName = v_dataContainer.get_columnName();
            p_row.setColumnValue(v_columnName, v_value);
         }
      }
      catch (final Exception v_e)
      {
         throw new ReportException("Problème avec p_dataSet=" + p_dataSet + " - p_row=" + p_row, v_e);
      }

      _index++;

      return v_return;
   }

   /**
    * Construire les getters à partir de la liste spécifiée.
    * @param p_listBeans
    *           Liste de beans (par exemple, une liste de DTOs).
    * @return La Map avec les getters.
    */
   private Map<DataContainer, Method> buildGetters (final List<?> p_listBeans)
   {
      final HashMap<DataContainer, Method> v_gettersByDataContainer = new HashMap<>(
               _listDataContainer.size());

      String v_columnName = null;
      if (!p_listBeans.isEmpty())
      {
         final Class<?> v_beanClass = p_listBeans.get(0).getClass();
         try
         {
            for (final DataContainer v_dataContainer : _listDataContainer)
            {
               v_columnName = v_dataContainer.get_columnName();
               // récupère le getter (le getter doit avoir pour nom "get" + le columnName du dataContainer)
               final Method v_getter = v_beanClass.getMethod("get" + v_columnName, (Class[]) null);
               v_gettersByDataContainer.put(v_dataContainer, v_getter);
            }
         }
         catch (final Exception v_e)
         {
            throw new ReportException("Problème pour constituer les getters avec v_columnName=" + v_columnName, v_e);
         }
      }

      return v_gettersByDataContainer;
   }

   /**
    * Obtention des données.
    * @return List
    */
   public abstract List<?> findListData ();

}
