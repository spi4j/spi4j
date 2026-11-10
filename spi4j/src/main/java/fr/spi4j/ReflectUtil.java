package fr.spi4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Classe utilitaire pour utiliser de la réflection (essentiellement Method.invoke).
 * @author MINARM
 */
public final class ReflectUtil
{
   /**
    * Constructeur.
    */
   private ReflectUtil ()
   {
      super();
   }

   /**
    * Invoque une méthode sur un objet avec des paramètres, mais en désencapsulant une éventuelle InvocationTargetException.
    * @param p_method
    *           Method
    * @param p_obj
    *           Instance d'objet sur laquelle va être invoquée la méthode
    * @param p_args
    *           Paramètres d'invocation de la méthode
    * @return Résultat
    * @throws Throwable
    *            Si exception ou error dans la méthode exécutée
    */
   public static Object invokeMethod (final Method p_method, final Object p_obj, final Object... p_args)
            throws Throwable
   {
      try
      {
         return p_method.invoke(p_obj, p_args);
      }
      catch (final InvocationTargetException v_ex)
      {
         if (v_ex.getCause() != null)
         {
            throw v_ex.getCause();
         }
         throw v_ex;
      }
   }

   /**
    * Crée une instance de classe à partir du nom de la classe d'implémentation (constructeur sans paramètres requis).
    * @param p_className
    *           Nom de la classe d'implémentation
    * @return Service
    */
   public static Object createInstance (final String p_className)
   {
      try
      {
         return Class.forName(p_className).getDeclaredConstructor().newInstance();
      }
      catch (final Exception v_ex)
      {
         throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
      }
   }
}
