/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jValidationException;

/**
 * DTO Simple.
 * @author MINARM
 */
public class SimpleDto implements Dto_Itf<Long>
{
   /**
    * SerialUid.
    */
   private static final long serialVersionUID = -1;

   private Long _id;

   private String _str;

   private Boolean _bool;

   private SousObjetDto _sousObjet;

   private List<SousObjetDto> _tab_sousObjets;

   private Map<Integer, SousObjetDto> _map_intSousObjets;

   private Map<SousObjetDto, Long> _map_sousObjetsLong;

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
   public SousObjetDto get_sousObjet ()
   {
      return _sousObjet;
   }

   /**
    * Setter _sousObjet.
    * @param p_sousObjet
    *           nouveau _sousObjet
    */
   public void set_sousObjet (final SousObjetDto p_sousObjet)
   {
      _sousObjet = p_sousObjet;
   }

   /**
    * Getter _tab_sousObjets.
    * @return _tab_sousObjets
    */
   public List<SousObjetDto> get_tab_sousObjets ()
   {
      return _tab_sousObjets;
   }

   /**
    * Setter _tab_sousObjets.
    * @param p_tab_sousObjets
    *           nouveau _tab_sousObjets.
    */
   public void set_tab_sousObjets (final List<SousObjetDto> p_tab_sousObjets)
   {
      _tab_sousObjets = p_tab_sousObjets;
   }

   /**
    * Getter _map_intSousObjets.
    * @return _map_intSousObjets
    */
   public Map<Integer, SousObjetDto> get_map_intSousObjets ()
   {
      return _map_intSousObjets;
   }

   /**
    * Setter _map_intSousObjets.
    * @param p_map_intSousObjets
    *           nouveau _map_intSousObjets
    */
   public void set_map_intSousObjets (final Map<Integer, SousObjetDto> p_map_intSousObjets)
   {
      _map_intSousObjets = p_map_intSousObjets;
   }

   /**
    * Getter _map_sousObjetsLong.
    * @return _map_sousObjetsLong
    */
   public Map<SousObjetDto, Long> get_map_sousObjetsLong ()
   {
      return _map_sousObjetsLong;
   }

   /**
    * Setter _map_sousObjetsLong.
    * @param p_map_sousObjetsLong
    *           nouveau _map_sousObjetsLong
    */
   public void set_map_sousObjetsLong (final Map<SousObjetDto, Long> p_map_sousObjetsLong)
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

   @Override
   public void validate () throws Spi4jValidationException
   {
      // RAS
   }
}
