/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import fr.spi4j.ws.xto.Xto_Itf;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * XTO Simple.
 * @author MINARM
 */
@XmlRootElement(name = "SimpleXTO", namespace = "http://spi4j.fr/xto")
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleXto implements Xto_Itf<Long>
{

   private static final long serialVersionUID = 1L;

   @XmlElement(namespace = "", name = "id")
   private Long _id;

   @XmlElement(namespace = "", name = "str")
   private String _str;

   @XmlElement(namespace = "", name = "bool")
   private Boolean _bool;

   @XmlElement(namespace = "", name = "sousObjet")
   private SousObjetXto _sousObjet;

   @XmlElement(namespace = "", name = "tab_sousObjets")
   private List<SousObjetXto> _tab_sousObjets;

   @XmlElement(namespace = "", name = "map_intSousObjets")
   private Map<Integer, SousObjetXto> _map_intSousObjets;

   @XmlElement(namespace = "", name = "map_sousObjetsLong")
   private Map<SousObjetXto, Long> _map_sousObjetsLong;

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
    * Getter _str.
    * @return _str
    */
   public String get_str ()
   {
      return _str;
   }

   /**
    * Setter _str.
    * @param p_str
    *           nouveau _str
    */
   public void set_str (final String p_str)
   {
      _str = p_str;
   }

   /**
    * Getter _bool.
    * @return _bool
    */
   public Boolean get_bool ()
   {
      return _bool;
   }

   /**
    * Setter _bool.
    * @param p_bool
    *           nouveau _bool
    */
   public void set_bool (final Boolean p_bool)
   {
      _bool = p_bool;
   }

   /**
    * Getter _sousObjet.
    * @return _sousObjet
    */
   public SousObjetXto get_sousObjet ()
   {
      return _sousObjet;
   }

   /**
    * Setter _sousObjet.
    * @param p_sousObjet
    *           nouveau _sousObjet
    */
   public void set_sousObjet (final SousObjetXto p_sousObjet)
   {
      _sousObjet = p_sousObjet;
   }

   /**
    * Getter _tab_sousObjets.
    * @return _tab_sousObjets
    */
   public List<SousObjetXto> get_tab_sousObjets ()
   {
      return _tab_sousObjets;
   }

   /**
    * Setter _tab_sousObjets.
    * @param p_tab_sousObjets
    *           nouveau _tab_sousObjets.
    */
   public void set_tab_sousObjets (final List<SousObjetXto> p_tab_sousObjets)
   {
      _tab_sousObjets = p_tab_sousObjets;
   }

   /**
    * Getter _map_intSousObjets.
    * @return _map_intSousObjets
    */
   public Map<Integer, SousObjetXto> get_map_intSousObjets ()
   {
      return _map_intSousObjets;
   }

   /**
    * Setter _map_intSousObjets.
    * @param p_map_intSousObjets
    *           nouveau _map_intSousObjets
    */
   public void set_map_intSousObjets (final Map<Integer, SousObjetXto> p_map_intSousObjets)
   {
      _map_intSousObjets = p_map_intSousObjets;
   }

   /**
    * Getter _map_sousObjetsLong.
    * @return _map_sousObjetsLong
    */
   public Map<SousObjetXto, Long> get_map_sousObjetsLong ()
   {
      return _map_sousObjetsLong;
   }

   /**
    * Setter _map_sousObjetsLong.
    * @param p_map_sousObjetsLong
    *           nouveau _map_sousObjetsLong
    */
   public void set_map_sousObjetsLong (final Map<SousObjetXto, Long> p_map_sousObjetsLong)
   {
      _map_sousObjetsLong = p_map_sousObjetsLong;
   }

   @Override
   public String toString ()
   {
      final StringBuilder v_builder = new StringBuilder(getClass().getName());
      v_builder.append(" : ");
      for (final Field v_field : getClass().getDeclaredFields())
      {
         try
         {
            v_builder.append("\n\t- ").append(v_field.getName()).append(" = ").append(v_field.get(this));
         }
         catch (final Exception v_e)
         {
            v_builder.append("??");
         }
      }
      return v_builder.toString();
   }
}
