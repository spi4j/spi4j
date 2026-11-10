/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.mapper;

import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jValidationException;

/**
 * Sous objet DTO.
 * @author MINARM
 */
public class SousObjetDto implements Dto_Itf<Long>
{

   /**
    * SerialUid.
    */
   private static final long serialVersionUID = -1;

   private Long _id;

   private String _value;

   private SimpleDto _parent;

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
   public SimpleDto get_parent ()
   {
      return _parent;
   }

   /**
    * Setter _parent.
    * @param p_parent
    *           nouveau _parent
    */
   public void set_parent (final SimpleDto p_parent)
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

   @Override
   public void validate () throws Spi4jValidationException
   {
      // RAS
   }

}
