/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.security;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import fr.spi4j.ReflectUtil;
import fr.spi4j.lib.security.annotations.AcceptUnauthentified;
import fr.spi4j.lib.security.annotations.Permissions;
import fr.spi4j.lib.security.exception.UnauthorizedActionException;

/**
 * Proxy de sécurité pour vérifier que l'utilisateur connecté a les permissions pour invoquer les méthodes des services.
 * @author MINARM
 * @param <TypeService>
 *           Type du service
 */
public final class SecurityProxy<TypeService> implements InvocationHandler
{

   private final Object _delegate;

   /**
    * Constructeur privé.
    * @param p_delegate
    *           l'objet proxysé
    */
   private SecurityProxy (final TypeService p_delegate)
   {
      super();
      _delegate = p_delegate;
   }

   @Override
   public Object invoke (final Object p_proxy, final Method p_method, final Object[] p_args) throws Throwable
   {
      // vérification de l'authentification et des permissions
      checkPermission(p_method);

      // si ok, on invoque directement le service
      return ReflectUtil.invokeMethod(p_method, _delegate, p_args);
   }

   /**
    * Vérification de l'authentification et des permissions.
    * @param p_method
    *           Method
    */
   static void checkPermission (final Method p_method)
   {
      // Si la sécurité est activée, on vérifie tous les éléments de sécurité
      if (Spi4jSecurity_Abs.getInstance().isEnabled())
      {
         // on vérifie que l'utilisateur est connecté et ses permissions seulement
         // si la méthode ne contient pas l'annotation @AcceptUnauthentified
         if (!p_method.isAnnotationPresent(AcceptUnauthentified.class))
         {
            // On récupère l'utilisateur dans la session
            // (si l'utilisateur courant n'existe pas dans la session, une UnauthentifiedUserException est jetée)
            final User_Itf v_utilisateurDto = Spi4jSecurity_Abs.getInstance().getCurrentUser();

            // On récupère les annotations de la méthode demandée
            final Permissions v_permissionAnnotation = p_method.getAnnotation(Permissions.class);
            if (v_permissionAnnotation != null)
            {
               // On vérifie que l'utilisateur courant a les permissions pour faire l'action
               final boolean v_estAutorise = Spi4jSecurity_Abs.getInstance().hasPermissions(
                        v_permissionAnnotation.value(), v_permissionAnnotation.operator());
               if (!v_estAutorise)
               {
                  // Et on lance une exception pour dire qu'il n'a pas les permissions
                  throw UnauthorizedActionException.createException(v_utilisateurDto.getIdentifiant(),
                           Arrays.asList(v_permissionAnnotation.value()), v_permissionAnnotation.operator());
               }
            }
         }
      }
   }

   /**
    * Création d'un proxy sur une classe
    * @param <TypeService>
    *           Le type du service
    * @param p_delegate
    *           Le service proxysé
    * @return Le proxy du service
    */
   @SuppressWarnings("unchecked")
   public static <TypeService> TypeService createProxy (final TypeService p_delegate)
   {
      final SecurityProxy<TypeService> v_securityProxy = new SecurityProxy<>(p_delegate);

      return (TypeService) Proxy.newProxyInstance(p_delegate.getClass().getClassLoader(), p_delegate.getClass()
               .getInterfaces(), v_securityProxy);
   }

}
