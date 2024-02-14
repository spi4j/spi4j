/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.spi4j.business.ApplicationService_Itf;
import fr.spi4j.business.UserBusiness_Abs;
import fr.spi4j.lib.security.annotations.Permissions;
import fr.spi4j.remoting.RemotingServlet;

/**
 * Classe utilitaire pour lire les permissions.
 * @author MINARM
 */
public final class PermissionsUtils
{

   /**
    * Constructeur privé.
    */
   private PermissionsUtils ()
   {
      super();
   }

   /**
    * @return la liste des permissions par opération de service.
    */
   public static Map<String, Permissions> getMapPermissionsByOperation ()
   {
      final Map<String, Permissions> v_map = new LinkedHashMap<>();

      final UserBusiness_Abs v_userBusiness = RemotingServlet.getUserBusiness();
      final Collection<ApplicationService_Itf> v_services = v_userBusiness.getListServices();

      for (final ApplicationService_Itf v_service : v_services)
      {
         final Class<?> v_serviceItf = v_service.getClass().getInterfaces()[0];
         for (final Method v_method : findMethods(v_serviceItf))
         {
            v_map.put(getMethodId(v_serviceItf, v_method), v_method.getAnnotation(Permissions.class));
         }
      }

      return v_map;
   }

   /**
    * Cherche les méthodes directes et héritées d'une interface de service.
    * @param p_serviceItf
    *           l'interface du service
    * @return les méthodes directes et héritées d'une interface de service
    */
   private static Set<Method> findMethods (final Class<?> p_serviceItf)
   {
      final Set<Method> v_methods = new HashSet<>();
      // ajoute les méthodes déclarées dans ce service
      for (final Method v_method : p_serviceItf.getDeclaredMethods())
      {
         v_methods.add(v_method);
      }
      // cherche les méthodes déclarées dans les interfaces parentes
      for (final Class<?> v_superInterface : p_serviceItf.getInterfaces())
      {
         // récupère les méthodes d'une interface parente
         final List<Method> v_methodsParentToAdd = new ArrayList<>();
         for (final Method v_methodParent : findMethods(v_superInterface))
         {
            // si la méthode trouvée dans le parent n'a pas été redéfinie dans la classe courante, on peut l'ajouter
            boolean v_okToAdd = true;
            for (final Method v_existingMethod : v_methods)
            {
               if (methodEquals(v_existingMethod, v_methodParent))
               {
                  v_okToAdd = false;
                  break;
               }
            }
            if (v_okToAdd)
            {
               v_methodsParentToAdd.add(v_methodParent);
            }
         }
         v_methods.addAll(v_methodsParentToAdd);
      }
      return v_methods;
   }

   /**
    * Retourne une chaine de caractère qui correspond à une méthode dans un service donnée.
    * @param p_serviceItf
    *           la classe du service
    * @param p_method
    *           la méthode
    * @return une chaine représentant la méthode dans ce service
    */
   public static String getMethodId (final Class<?> p_serviceItf, final Method p_method)
   {
      return p_serviceItf.getCanonicalName() + '.' + p_method.getName()
               + p_method.toString().substring(p_method.toString().indexOf('('));
   }

   /**
    * Compare deux méthodes
    * @param p_method1
    *           la première méthode
    * @param p_method2
    *           la seconde méthode
    * @return Si les méthodes sont égales
    */
   private static boolean methodEquals (final Method p_method1, final Method p_method2)
   {
      // vérification du nom
      if (!p_method1.getName().equals(p_method2.getName()))
      {
         return false;
      }

      // vérification du type de retour
      if (!typesAreSimilar(p_method1.getReturnType(), p_method2.getReturnType()))
      {
         return false;
      }

      final Class<?>[] v_params1 = p_method1.getParameterTypes();
      final Class<?>[] v_params2 = p_method2.getParameterTypes();

      // vérification du nombre de paramètres
      if (v_params1.length == v_params2.length)
      {
         // vérification du type de chaque paramètre
         for (int v_i = 0; v_i < v_params1.length; v_i++)
         {
            if (!typesAreSimilar(v_params1[v_i], v_params2[v_i]))
            {
               return false;
            }
         }
         return true;
      }
      return false;
   }

   /**
    * Vérification si des types sont similaires.
    * @param p_type1
    *           type 1
    * @param p_type2
    *           type 2
    * @return true si les types sont similaires, false sinon
    */
   private static boolean typesAreSimilar (final Class<?> p_type1, final Class<?> p_type2)
   {
      return p_type1.isAssignableFrom(p_type2) || p_type2.isAssignableFrom(p_type1);
   }

}
