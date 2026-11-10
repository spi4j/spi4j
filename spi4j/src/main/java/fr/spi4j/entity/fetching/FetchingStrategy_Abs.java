/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.entity.fetching;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Classe abstraite de FetchingStrategy dont hériteront toutes les classes générées de FetchingStrategy.
 * @param <TypeId>
 *           Type de l'identifiant du Entity
 * @param <TypeEntity>
 *           Type de Entity
 * @author MINARM
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
abstract public class FetchingStrategy_Abs<TypeId, TypeEntity extends Entity_Itf<TypeId>> implements Serializable
{
   private static final long serialVersionUID = 1L;

   private final FetchingStrategy_Abs<?, ?> _parent;

   @XmlAttribute(name = "attribute")
   private final ColumnsNames_Itf _attribute;

   @XmlAttribute(name = "fetch")
   private boolean _fetch = true;

   /**
    * Constructeur.
    */
   protected FetchingStrategy_Abs ()
   {
      super();
      _attribute = null;
      _parent = null;
      _fetch = true;
   }

   /**
    * Constructeur.
    * @param p_attribute
    *           Attribut du Entity
    * @param p_parent
    *           Fetching strategy parente
    */
   protected FetchingStrategy_Abs (final ColumnsNames_Itf p_attribute, final FetchingStrategy_Abs<?, ?> p_parent)
   {
      super();
      _attribute = p_attribute;
      _parent = p_parent;
      _fetch = false;
   }

   /**
    * @return Booléen fetch
    */
   public boolean isFetch ()
   {
      return _fetch;
   }

   /**
    * @param p_fetch
    *           boolean
    */
   public void setFetch (final boolean p_fetch)
   {
      this._fetch = p_fetch;
   }

   /**
    * @return l'attribut récupéré par la fetching stratégie
    */
   public ColumnsNames_Itf getAttribute ()
   {
      return _attribute;
   }

   /**
    * @return parent
    */
   public FetchingStrategy_Abs<?, ?> getParent ()
   {
      return _parent;
   }

   /**
    * @return path
    */
   public String path ()
   {
      final String v_result;
      if (_parent == null)
      {
         v_result = this.getClass().getSimpleName();
      }
      else
      {
         v_result = _parent.path() + '.' + _attribute.getLogicalColumnName();
      }
      return v_result;
   }

   /**
    * Retourne les relations filles pour lire l'état de l'arbre.
    * @return List
    */
   public List<FetchingStrategy_Abs<TypeId, ? extends Entity_Itf<TypeId>>> getChildren ()
   {
      return Collections.emptyList();
   }
}
