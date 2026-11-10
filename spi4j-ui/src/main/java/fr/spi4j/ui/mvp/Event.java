/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

import java.util.HashMap;
import java.util.Map;

/**
 * Evénement émis par un présenteur et qui peut être intercepté par d'autres présenteur, par l'intermédiaire de l'EventBus.
 * @author MINARM
 */
public class Event
{
   private final EventType _type;

   private final Presenter_Abs<?, ?> _source;

   private final Map<String, Object> _attributes;

   /**
    * Constructeur.
    * @param p_type
    *           le type de l'événement
    * @param p_source
    *           la source de l'événement (le présenteur qui emet l'événement
    */
   public Event (final EventType p_type, final Presenter_Abs<?, ?> p_source)
   {
      _type = p_type;
      _source = p_source;
      _attributes = new HashMap<String, Object>();
   }

   /**
    * @return le type de l'événement
    */
   public EventType get_type ()
   {
      return _type;
   }

   /**
    * @return la source de l'événement (le présenteur qui l'a émis)
    */
   public Presenter_Abs<?, ?> get_source ()
   {
      return _source;
   }

   /**
    * @return une map d'attributs supplémentaires liés à cet événement
    */
   public Map<String, Object> get_attributes ()
   {
      return _attributes;
   }

   /**
    * Récupère un attribut supplémentaire qui a été positioné dans cet événement.
    * @param p_key
    *           la clé de l'attribut
    * @return la valeur de l'attribut (ou null si aucun attribut n'a été positioné pour cette clé)
    */
   public Object getAttribute (final String p_key)
   {
      return _attributes.get(p_key);
   }

   /**
    * Ajoute un attribut supplémentaire dans cet événement.
    * @param p_key
    *           la clé de l'attribut
    * @param p_value
    *           la valeur de l'attribut
    */
   public void addAttribute (final String p_key, final Object p_value)
   {
      _attributes.put(p_key, p_value);
   }
}
