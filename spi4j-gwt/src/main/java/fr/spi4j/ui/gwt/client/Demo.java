/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import java.util.Arrays;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;

import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Entry-point de démonstration des composants de la librairie graphique Spi4J-GWT.
 * @author MINARM
 */
public class Demo implements EntryPoint
{

   @Override
   public void onModuleLoad ()
   {

      // masquer le div de chargement de la page.
      final RootPanel v_loadingPanel = RootPanel.get("loading");
      v_loadingPanel.setVisible(false);

      final FlexTable root = new FlexTable();
      int rowIndex = 0;
      root.setHTML(rowIndex, 0, "StringField");
      root.setWidget(rowIndex, 1, new SpiStringField());

      rowIndex++;
      root.setHTML(rowIndex, 0, "DoubleField");
      root.setWidget(rowIndex, 1, new SpiDoubleField());

      rowIndex++;
      root.setHTML(rowIndex, 0, "DateField");
      root.setWidget(rowIndex, 1, new SpiDateField());

      rowIndex++;
      root.setHTML(rowIndex, 0, "ComboBox");
      final SpiComboBox<GradeXto> combobox = new SpiComboBox<GradeXto>();
      combobox.setList(Arrays.asList(new GradeXto("Colonel"), new GradeXto("Lieutenant")));
      root.setWidget(rowIndex, 1, combobox);

      rowIndex++;
      root.setHTML(rowIndex, 0, "CheckBox");
      root.setWidget(rowIndex, 1, new SpiCheckBox("majeur"));

      RootPanel.get().add(root);

   }

   /**
    * Classe d'exemple d'un XTO pour remplir les composants.
    * @author MINARM
    */
   private static class GradeXto implements Xto_Itf<Long>
   {

      private static final long serialVersionUID = 1L;

      private final String libelle;

      /**
       * Constructeur avec le libellé du grade.
       * @param libelle_
       *           le libellé
       */
      public GradeXto (final String libelle_)
      {
         libelle = libelle_;
      }

      @Override
      public Long getId ()
      {
         return (long) libelle.hashCode();
      }

      @Override
      public void setId (final Long p_id)
      {
         // RAS
      }

      /**
       * @return le libellé du grade
       */
      public String getLibelle ()
      {
         return libelle;
      }

      @Override
      public String toString ()
      {
         return getLibelle();
      }

   }

}
