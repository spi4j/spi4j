/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Bus de gestion des événements.
 * @author MINARM
 */
public class EventBus
{
   private final Map<EventType, Set<Presenter_Abs<?, ?>>> _listeners = new HashMap<EventType, Set<Presenter_Abs<?, ?>>>();

   /**
    * Place un listener en écoute sur un certain type d'événement.
    * @param p_eventType
    *           le type d'événement écouté
    * @param p_presenter
    *           le présenteur qui écoute l'événement
    */
   public void listen (final EventType p_eventType, final Presenter_Abs<?, ?> p_presenter)
   {
      Set<Presenter_Abs<?, ?>> v_presentersListening = _listeners.get(p_eventType);
      if (v_presentersListening == null)
      {
         v_presentersListening = new LinkedHashSet<Presenter_Abs<?, ?>>();
         _listeners.put(p_eventType, v_presentersListening);
      }
      v_presentersListening.add(p_presenter);
   }

   /**
    * Stoppe l'écoute d'un présenteur sur un type d'événement.
    * @param p_eventType
    *           le type d'événement écouté
    * @param p_presenter
    *           le présenteur qui écoute
    */
   public void stopListening (final EventType p_eventType, final Presenter_Abs<?, ?> p_presenter)
   {
      final Set<Presenter_Abs<?, ?>> v_presentersListening = _listeners.get(p_eventType);
      if (v_presentersListening == null)
      {
         throw new Spi4jRuntimeException("Aucun listener n'est en écoute sur l'événement " + p_eventType,
                  "La méthode listen doit avoir été appelée avant de faire un stopListening pour le présenteur "
                           + p_presenter.getClass().getName());
      }
      if (!v_presentersListening.contains(p_presenter))
      {
         throw new Spi4jRuntimeException("Le présenteur " + p_presenter.getClass().getName()
                  + " n'est pas en écoute sur l'événement " + p_eventType,
                  "La méthode listen doit avoir été appelée avant de faire un stopListening pour le présenteur "
                           + p_presenter.getClass().getName());
      }
      v_presentersListening.remove(p_presenter);
   }

   /**
    * Stoppe l'écoute d'un présenteur sur tous les types d'événement.
    * @param p_presenter
    *           le présenteur qui écoute
    */
   public void stopListening (final Presenter_Abs<?, ?> p_presenter)
   {
      for (final Entry<EventType, Set<Presenter_Abs<?, ?>>> v_entry : _listeners.entrySet())
      {
         v_entry.getValue().remove(p_presenter);
      }
   }

   /**
    * Envoi d'un événement dans le bus.
    * @param p_event
    *           l'événement envoyé
    */
   public void fireEvent (final Event p_event)
   {
      final Set<Presenter_Abs<?, ?>> v_presentersListening = _listeners.get(p_event.get_type());
      if (v_presentersListening != null)
      {
         for (final Presenter_Abs<?, ?> v_presenter : v_presentersListening)
         {
            v_presenter.onEvent(p_event);
         }
      }
   }

   /**
    * Permet de savoir si un présenteur écoute un certain type d'événement.
    * @param p_eventType
    *           le type d'événement écouté
    * @param p_presenter
    *           le présenteur qui écoute l'événement
    * @return true si le présenteur écoute ce type d'événement, false sinon
    */
   public boolean isListening (final EventType p_eventType, final Presenter_Abs<?, ?> p_presenter)
   {
      final Set<Presenter_Abs<?, ?>> v_presentersListening = _listeners.get(p_eventType);
      if (v_presentersListening == null)
      {
         return false;
      }
      return v_presentersListening.contains(p_presenter);
   }

}
