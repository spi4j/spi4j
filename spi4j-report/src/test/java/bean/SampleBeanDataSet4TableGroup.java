/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package bean;

import java.util.ArrayList;
import java.util.List;

import bean.data.Personne;
import fr.spi4j.report.DataContainer;
import fr.spi4j.report.util.BirtDataSetBean_Abs;
import fr.spi4j.report.util.ReportUtils;

/**
 * Conteneur de bean 'Personne'.
 * @author MINARM
 */
public class SampleBeanDataSet4TableGroup extends BirtDataSetBean_Abs
{
   /** La liste des colonnes à prendre en compte */
   public static final String[] c_tabColumns =
   {"_id", "_nom", "_nomAndPrenom", "_age" };

   @Override
   public List<DataContainer> findListDataContainer ()
   {
      return ReportUtils.createListDataContainer(Personne.class, c_tabColumns);
   }

   @Override
   public List<Personne> findListData ()
   {
      final List<Personne> v_tab_beans = new ArrayList<>();
      v_tab_beans.add(new Personne("1", "BONO", "Jean", 20));
      v_tab_beans.add(new Personne("2", "DANLETAS", "Alphonse", 30));
      v_tab_beans.add(new Personne("3", "DUPONT", "Pierre", 50));
      v_tab_beans.add(new Personne("4", "DUPONT", "Jean", 120));
      return v_tab_beans;
   }
}
