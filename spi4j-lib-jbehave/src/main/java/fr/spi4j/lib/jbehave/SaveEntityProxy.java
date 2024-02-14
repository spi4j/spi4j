/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import fr.spi4j.ReflectUtil;

/**
 * Proxy qui stocke les instances des DTOs créés durant la story.
 * @author MINARM
 * @param <TypeService>
 *           Type du service
 */
public final class SaveEntityProxy<TypeService> implements InvocationHandler
{

   private static final ThreadLocal<SpiStory_Abs> c_story = new ThreadLocal<>();

   private final Object _delegate;

   /**
    * Constructeur privé.
    * @param p_delegate
    *           l'objet proxysé
    */
   private SaveEntityProxy (final TypeService p_delegate)
   {
      super();
      _delegate = p_delegate;
   }

   @Override
   public Object invoke (final Object p_proxy, final Method p_method, final Object[] p_args) throws Throwable
   {
      // on invoque le service
      final Object v_obj = ReflectUtil.invokeMethod(p_method, _delegate, p_args);
      final SpiStory_Abs v_story = c_story.get();
      if (v_story != null && v_story.hasToBeSaved(p_proxy, p_method, p_args))
      {
         v_story.saveEntity(v_obj);
      }
      return v_obj;
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
      final SaveEntityProxy<TypeService> v_Proxy = new SaveEntityProxy<>(p_delegate);

      return (TypeService) Proxy.newProxyInstance(p_delegate.getClass().getClassLoader(), p_delegate.getClass()
               .getInterfaces(), v_Proxy);
   }

   /**
    * Initialise la story courante pour ce proxy.
    * @param p_story
    *           la story courante
    */
   public static void setStory (final SpiStory_Abs p_story)
   {
      c_story.set(p_story);
   }

   /**
    * Finalisation du test et suppression des données de la story.
    */
   public static void endTest ()
   {
      c_story.get().clearDataFile();
      c_story.remove();
   }

   /**
    * Initialise le prochain identifiant et le prochain type pour l'objet à stocker l'objet dans la story.
    * @param p_nextIdentifiant
    *           le prochain identifiant
    * @param p_nextType
    *           le prochain type
    */
   public static void setNextTypeToSave (final String p_nextIdentifiant, final String p_nextType)
   {
      c_story.get().setNextTypeToSave(p_nextIdentifiant, p_nextType);
   }

}
