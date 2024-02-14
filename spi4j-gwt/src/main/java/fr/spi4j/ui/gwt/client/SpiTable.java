/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import fr.spi4j.ui.HasSelection_Itf;

/**
 * Table d'affichage avec toString (affichage seulement).
 * @param <TypeValue>
 *           le type de données à afficher
 * @author MINARM
 */
@SuppressWarnings("deprecation")
public class SpiTable<TypeValue> extends FlexTable implements HasSelection_Itf<TypeValue>,
         HasValueChangeHandlers<TypeValue>
{

   private final String _name;

   private List<TypeValue> _data;

   private TypeValue _value;

   private final List<Column> _columns = new ArrayList<Column>();

   private String _emptyText = "Aucune donnée";

   private final List<ValueChangeHandler<TypeValue>> _valueChangeHandlers = new ArrayList<ValueChangeHandler<TypeValue>>();

   /**
    * Constructeur.
    * @param p_name
    *           le nom du tableau (utile pour les radio boutons)
    */
   public SpiTable (final String p_name)
   {
      super();
      _name = p_name;
      addStyleName("spi-table");
      addStyleName("spi-table-" + p_name);
   }

   @Override
   public TypeValue getValue ()
   {
      return _value;
   }

   @Override
   public void setValue (final TypeValue p_value)
   {
      _value = p_value;
   }

   @Override
   public List<TypeValue> getList ()
   {
      return _data;
   }

   @Override
   public void setList (final List<TypeValue> p_list)
   {
      _data = p_list;
      removeAllRows();
      _value = null;

      buildHeader();

      int rowNumber = 1;

      if (p_list == null || p_list.isEmpty())
      {
         setHTML(rowNumber, 0, _emptyText);
         final Element v_noDataElement = getCellFormatter().getElement(rowNumber, 0);
         v_noDataElement.setAttribute("colspan", String.valueOf(_columns.size() + 1));
         v_noDataElement.setAttribute("align", "center");
         v_noDataElement.addClassName("spi-table-no-data");
      }
      else
      {
         boolean impair = true;
         for (final TypeValue v_typeValue : p_list)
         {
            getRowFormatter().addStyleName(rowNumber, impair ? "spi-table-row-odd" : "spi-table-row-even");
            int columnNumber = 0;
            final RadioButton v_radio = new RadioButton(_name);
            v_radio.addClickHandler(new ClickHandler()
            {
               @Override
               public void onClick (final ClickEvent p_event)
               {
                  if (v_radio.getValue())
                  {
                     setValue(v_typeValue);
                     fireValueChangeEvent(v_typeValue);
                  }
               }
            });
            getCellFormatter().addStyleName(rowNumber, columnNumber, "spi-table-cell");
            setWidget(rowNumber, columnNumber++, v_radio);

            for (final Column column : _columns)
            {
               Widget v_valueWidget;
               if (column.getRenderer() != null)
               {
                  v_valueWidget = column.getRenderer().render(v_typeValue);
               }
               else
               {
                  v_valueWidget = new HTML(v_typeValue.toString());
               }
               if (v_valueWidget == null)
               {
                  v_valueWidget = new Label();
               }
               getCellFormatter().addStyleName(rowNumber, columnNumber, "spi-table-cell");
               setWidget(rowNumber, columnNumber++, v_valueWidget);
            }
            rowNumber++;
            impair = !impair;
         }
      }
   }

   /**
    * Met à jour le texte en cas de table vide (sans donnée).
    * @param p_emptyText
    *           le texte à afficher si aucune donnée n'est présente dans la table
    */
   public void setEmptyText (final String p_emptyText)
   {
      _emptyText = p_emptyText;
   }

   /**
    * Construction du header.
    */
   private void buildHeader ()
   {
      int headerColumnNumber = 0;
      final Label selHeader = new Label("Sel.");
      selHeader.addStyleName("spi-table-header-cell");
      setWidget(0, headerColumnNumber++, selHeader);
      for (final Column column : _columns)
      {
         final Label columnHeader = new Label(column.getHeader());
         columnHeader.addStyleName("spi-table-header-cell");
         setWidget(0, headerColumnNumber++, columnHeader);
      }
      getRowFormatter().addStyleName(0, "spi-table-header");
   }

   /**
    * Ajoute une colonne dans la table.
    * @param header
    *           l'en-tête de la colonne
    * @param p_renderer
    *           le renderer des éléments de la colonne
    */
   public void addColumn (final String header, final SpiTableRenderer<TypeValue> p_renderer)
   {
      final Column v_column = new Column(header, p_renderer);
      _columns.add(v_column);

      // construction du header
      buildHeader();
   }

   /**
    * Classe interne de gestion de colonne.
    * @author MINARM
    */
   private class Column
   {
      private final String _header;

      private final SpiTableRenderer<TypeValue> _renderer;

      /**
       * Constructeur
       * @param header
       *           l'en-tête de la colonne
       * @param renderer
       *           le renderer des éléments de la colonne
       */
      public Column (final String header, final SpiTableRenderer<TypeValue> renderer)
      {
         super();
         _header = header;
         _renderer = renderer;
      }

      /**
       * @return l'en-tête de la colonne
       */
      public String getHeader ()
      {
         return _header;
      }

      /**
       * @return le renderer des éléments de la colonne
       */
      public SpiTableRenderer<TypeValue> getRenderer ()
      {
         return _renderer;
      }

   }

   /**
    * Envoie un événement de changement de valeur.
    * @param p_value
    *           la nouvelle valeur de la table
    */
   protected void fireValueChangeEvent (final TypeValue p_value)
   {
      final ValueChangeEvent<TypeValue> v_event = new SpiTableValueChangeEvent<TypeValue>(p_value);
      for (final ValueChangeHandler<TypeValue> v_handler : _valueChangeHandlers)
      {
         v_handler.onValueChange(v_event);
      }
   }

   @Override
   public HandlerRegistration addValueChangeHandler (final ValueChangeHandler<TypeValue> p_handler)
   {
      _valueChangeHandlers.add(p_handler);
      return null;
   }

   /**
    * Un événement de changement de valeur dans la table.
    * @param <TypeValue>
    *           le type de valeur de la table
    * @author MINARM
    */
   private static class SpiTableValueChangeEvent<TypeValue> extends ValueChangeEvent<TypeValue>
   {

      /**
       * Constructeur.
       * @param p_value
       *           la nouvelle valeur
       */
      protected SpiTableValueChangeEvent (final TypeValue p_value)
      {
         super(p_value);
      }

   }

}
