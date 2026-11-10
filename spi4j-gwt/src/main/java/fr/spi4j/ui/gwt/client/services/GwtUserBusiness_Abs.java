package fr.spi4j.ui.gwt.client.services;

import java.util.HashMap;
import java.util.Map;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Classe abstraite de gestion des services GWT.
 * @author MINARM
 */
public abstract class GwtUserBusiness_Abs
{

   // il est préférable d'avoir ces proxys sous forme de singletons pour ne pas réinstancer les instances et les proxys à chaque récupération
   private final Map<String, Object> _bindings = new HashMap<String, Object>();

   /**
    * Retourne l'implémentation (ou un proxy) d'un service préalablement enregistré par la méthode bind.
    * @param p_serviceInterface
    *           Classe de l'interface implémentée par le service recherché
    * @return Service
    */
   public Object getBinding (final Class<?> p_serviceInterface)
   {
      Object v_result = _bindings.get(p_serviceInterface.getName());
      if (v_result == null)
      {
         if (_bindings.isEmpty())
         {
            // s'il n'y a aucun binding de service défini, on initialise par défaut les bindings des implémentations "client"
            initBindings();
            v_result = _bindings.get(p_serviceInterface.getName());
         }
         if (v_result == null)
         {
            throw new Spi4jRuntimeException("Binding non défini pour la classe: " + p_serviceInterface,
                     "vérifier l'initialisation de l'application");
         }
      }
      return v_result;
   }

   /**
    * Enregistre l'implémentation (ou un proxy) d'un service avec comme clé la classe de l'interface implémentée par le service.
    * @param p_serviceInterface
    *           Classe de l'interface
    * @param p_service
    *           Service (ou proxy du service)
    */
   protected void bind (final Class<?> p_serviceInterface, final Object p_service)
   {
      _bindings.put(p_serviceInterface.getName(), p_service);
   }

   /**
    * Méthode à implémenter pour initialiser les bindings des implémentations client des services.
    */
   abstract protected void initBindings ();

}
