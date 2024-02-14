/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Classe Helper pour DtoUtil et EntityUtil
 * @author MINARM
 */
public final class SerializationHelper
{
   /**
    * Constructeur.
    */
   private SerializationHelper ()
   {
      super();
   }

   /**
    * Clone/Copie complète d'un identifiable.
    * @param <T>
    *           Type de l'identifiable
    * @param p_entity
    *           Identifiable_Itf
    * @return Le identifiable cloné
    */
   @SuppressWarnings("unchecked")
   public static <T extends Identifiable_Itf<?>> T deepClone (final T p_entity)
   {
      // sérialisation en mémoire et désérialisation sont le moyen le plus simple et le plus sûre pour avoir une copie complète de l'identifiable, y compris références liées
      final ByteArrayOutputStream v_byteArray = new ByteArrayOutputStream();
      try
      {
         final ObjectOutputStream v_output = new ObjectOutputStream(v_byteArray);
         try
         {
            v_output.writeObject(p_entity);
         }
         finally
         {
            v_output.close();
         }
         final ObjectInputStream v_input = new ObjectInputStream(new ByteArrayInputStream(v_byteArray.toByteArray()));
         try
         {
            return (T) v_input.readObject();
         }
         finally
         {
            v_input.close();
         }
      }
      catch (final ClassNotFoundException v_ex)
      {
         // ne peut pas arriver mais au cas où
         throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
      }
      catch (final IOException v_ex)
      {
         // ne peut pas arriver mais au cas où
         throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
      }
   }

   /**
    * Clone/Copie d'un Identifiable, sans cloner les références et même si l identifiable n'implémente pas Cloneable.
    * @param <T>
    *           Type du Identifable
    * @param p_identifiable
    *           Identifiable_Itf
    * @return Le Identifiable cloné
    */
   @SuppressWarnings("unchecked")
   public static <T extends Identifiable_Itf<?>> T clone (final T p_identifiable)
   {
      try
      {
         final Class<T> v_identifiableClass = (Class<T>) p_identifiable.getClass();
         // la classe du DTO doit avoir un constructeur public sans paramètre
         final T v_identifiable = v_identifiableClass.getDeclaredConstructor().newInstance();
         Class<?> v_class = v_identifiableClass;
         while (v_class != Object.class)
         {
            for (final Field v_field : v_class.getDeclaredFields())
            {
               // si le champ est static, on ne le copie pas
               if (!Modifier.isStatic(v_field.getModifiers()))
               {
                  v_field.setAccessible(true);
                  v_field.set(v_identifiable, v_field.get(p_identifiable));
               }
            }
            v_class = v_class.getSuperclass();
         }
         return v_identifiable;
      }
      catch (final Exception v_e)
      {
         throw new IllegalArgumentException(v_e);
      }
   }
}
