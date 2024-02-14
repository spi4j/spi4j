/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import java.io.Serializable;
import java.lang.reflect.Method;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Classe définissant un service demandé par l'application cliente avec ses arguments.
 */
public class RemotingRequest implements Serializable
{
   private static final long serialVersionUID = 143499618436121651L;

   private static final String[] c_noParameters = new String[0];

   private final String _interfaceServiceName;

   private final String _methodName;

   private final String[] _parameterTypes;

   private final Object[] _args;

   /**
    * Constructeur.
    * @param p_interfaceService
    *           Interface du service demandé
    * @param p_method
    *           Méthode java du service demandé
    * @param p_args
    *           Arguments demandés pour l'exécution du service
    */
   public RemotingRequest (final Class<?> p_interfaceService, final Method p_method, final Object[] p_args) 
   {
      super();

      // nom du service à appeler (nom complet de l'interface du service)
      _interfaceServiceName = p_interfaceService.getName();

      // nom de la méthode à appeler
      _methodName = p_method.getName();

      // Types des paramètres déclarés dans la méthode
      // (transmettre cela est nécessaire par exemple s'il y a surcharge de la méthode, et on sérialise les noms des classes et non les classes elles-même)
      final Class<?>[] v_parameterTypes = p_method.getParameterTypes();
      final int v_parameterTypesLength = v_parameterTypes.length;
      if (v_parameterTypesLength == 0)
      {
         _parameterTypes = c_noParameters;
      }
      else
      {
         _parameterTypes = new String[v_parameterTypesLength];
         for (int v_i = 0; v_i < v_parameterTypesLength; v_i++)
         {
            _parameterTypes[v_i] = v_parameterTypes[v_i].getName();
         }
      }

      // Valeurs des paramètres de la méthode (elles doivent être sérialisables)
      if (p_args != null && p_args.length != 0)
      {
         for (final Object v_arg : p_args)
         {
            if (v_arg != null && !(v_arg instanceof Serializable))
            {
               throw new Spi4jRuntimeException("Un paramètre du service n'est pas sérialisable (classe="
                        + v_arg.getClass() + ", valeur=" + v_arg + ")",
                        "Ajouter 'implements Serializable' sur la classe de ce paramètre");
            }
         }
      }
      _args = p_args;
   }

   /**
    * Retourne le nom (avec package) de l'interface du service demandé.
    * @return String
    */
   String getInterfaceServiceName ()
   {
      return _interfaceServiceName;
   }

   /**
    * Retourne le nom de la méthode java du service demandé.
    * @return String
    */
   String getMethodName ()
   {
      return _methodName;
   }

   /**
    * Retourne les types des paramètres de la méthode java du service demandé, sous forme de String[] et non de Class[].
    * @return String[]
    */
   String[] getParameterTypes ()
   {
      return _parameterTypes; 
   }

   /**
    * Retourne les arguments demandé pour l'exécution du service.
    * @return Object[]
    */
   Object[] getArgs ()
   {
      return _args; 
   }
}
