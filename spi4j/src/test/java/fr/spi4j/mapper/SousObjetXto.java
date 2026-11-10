/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.mapper;

import fr.spi4j.ws.xto.Xto_Itf;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Sous objet XTO.
 * @author MINARM
 */
@XmlRootElement(name = "SousObjetXTO", namespace = "http://spi4j.fr/xto")
@XmlAccessorType(XmlAccessType.FIELD)
public class SousObjetXto implements Xto_Itf<Long>
{

   private static final long serialVersionUID = 1L;

   @XmlElement(namespace = "", name = "id")
   private Long _id;

   @XmlElement(namespace = "", name = "value")
   private String _value;

   @XmlElement(namespace = "", name = "parent")
   private SimpleXto _parent;

   @Override
   public Long getId ()
   {
      return _id;
   }

   @Override
   public void setId (final Long p_id)
   {
      _id = p_id;
   }

   /**
    * Getter _value.
    * @return _value
    */
   public String get_value ()
   {
      return _value;
   }

   /**
    * Setter _value.
    * @param p_value
    *           nouveau _value
    */
   public void set_value (final String p_value)
   {
      _value = p_value;
   }

   /**
    * Getter _parent.
    * @return _parent
    */
   public SimpleXto get_parent ()
   {
      return _parent;
   }

   /**
    * Setter _parent.
    * @param p_parent
    *           nouveau _parent
    */
   public void set_parent (final SimpleXto p_parent)
   {
      _parent = p_parent;
   }

   @Override
   public String toString ()
   {
      final StringBuilder v_builder = new StringBuilder(getClass().getName());
      v_builder.append(" : ").append(_value).append(", parent = ");
      if (_parent == null)
      {
         v_builder.append("null");
      }
      else
      {
         v_builder.append(_parent.getId());
      }
      return v_builder.toString();
   }

}
