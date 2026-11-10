/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package bean;

import java.util.ArrayList;
import java.util.List;

import bean.data.Personne;
import bean.data.Produit;
import fr.spi4j.report.DataContainer;
import fr.spi4j.report.util.BirtDataSetBean_Abs;
import fr.spi4j.report.util.ReportUtils;

/**
 * Conteneur de bean 'Personne' (et 'Produit' par composition).
 * @author MINARM
 */
public class SampleBeanDataSet4TableImbriquee extends BirtDataSetBean_Abs
{
   /** La liste des colonnes à prendre en compte */
   public static final String[] c_tabColumns =
   {"_id", "_nom", "_prenom", "_age", "_lstProduit" };

   @Override
   public List<DataContainer> findListDataContainer ()
   {
      return ReportUtils.createListDataContainer(Personne.class, c_tabColumns);
   }

   @Override
   public List<Personne> findListData ()
   {
      final List<Produit> v_tabProduit1 = new ArrayList<>();
      v_tabProduit1.add(new Produit("Produit1", 5));
      v_tabProduit1.add(new Produit("Produit2", 2));

      final List<Produit> v_tabProduit2 = new ArrayList<>();
      v_tabProduit2.add(new Produit("Produit3", 2));
      v_tabProduit2.add(new Produit("Produit4", 3));

      final List<Produit> v_tabProduit3 = new ArrayList<>();
      v_tabProduit3.add(new Produit("Produit4", 1));
      v_tabProduit3.add(new Produit("Produit5", 5));
      v_tabProduit3.add(new Produit("Produit6", 7));

      final List<Produit> v_tabProduit4 = new ArrayList<>();
      v_tabProduit4.add(new Produit("Produit7", 1));

      final List<Personne> v_tabPersonne = new ArrayList<>();
      v_tabPersonne.add(new Personne("1", "BONO", "Jean", 20, v_tabProduit1));
      v_tabPersonne.add(new Personne("2", "DANLETAS", "Alphonse", 30, v_tabProduit2));
      v_tabPersonne.add(new Personne("3", "DUPONT", "Pierre", 50, v_tabProduit3));
      v_tabPersonne.add(new Personne("4", "DUPONT", "Jean", 120, v_tabProduit4));

      return v_tabPersonne;
   }
}
