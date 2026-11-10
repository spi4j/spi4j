/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import java.util.List;

import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.view.client.ProvidesKey;

import fr.spi4j.Identifiable_Itf;
import fr.spi4j.ui.HasSelection_Itf;

/**
 * Utilisation d'une combo box en GWT.
 * @param <TypeValue>
 *           le type de données
 * @author MINARM
 */
public class SpiComboBox<TypeValue> extends ValueListBox<TypeValue> implements HasSelection_Itf<TypeValue>
{

   private List<TypeValue> _data;

   /**
    * Constructeur.
    */
   public SpiComboBox ()
   {
      this(new AbstractRenderer<TypeValue>()
      {
         @Override
         public String render (final TypeValue object)
         {
            if (object == null)
            {
               return "";
            }
            return object.toString();
         }
      });
   }

   /**
    * Constructeur.
    * @param renderer
    *           le renderer
    */
   public SpiComboBox (final Renderer<TypeValue> renderer)
   {
      super(renderer, new ProvidesKey<TypeValue>()
      {
         @Override
         public Object getKey (final TypeValue item)
         {
            if (item == null)
            {
               return null;
            }
            if (item instanceof Identifiable_Itf)
            {
               return ((Identifiable_Itf<?>) item).getId();
            }
            return item.toString();
         }
      });
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
      setAcceptableValues(p_list);
   }

   /**
    * Précise si le champ est modifiable.
    * @param newEnable
    *           true si le champ est modifiable, false sinon
    */
   public void setEditable (final boolean newEnable)
   {
      if (newEnable)
      {
         getElement().removeAttribute("disabled");
      }
      else
      {
         getElement().setAttribute("disabled", "");
      }
   }
}
