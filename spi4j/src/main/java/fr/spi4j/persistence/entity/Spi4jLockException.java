/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.entity;

/**
 * Classe d'exception pour le Lock Optimiste.
 * @author MINARM
 */
public class Spi4jLockException extends RuntimeException
{

   /**
    * SerialUid.
    */
   private static final long serialVersionUID = -1L;

   /**
    * Constructeur pour un problème de Lock Optimiste sur une entité.
    * @param p_instance
    *           l'instance qui n'a pas pu être modifiée
    * @param p_oldVersion
    *           l'ancienne version connue par le client
    * @param p_lastVersion
    *           la dernière version en base
    */
   public Spi4jLockException (final Entity_Itf<?> p_instance, final Object p_oldVersion, final Object p_lastVersion)
   {
      super("Objet déjà modifié ou supprimé : " + p_instance.getClass().getSimpleName() + " [" + p_instance.getId()
               + "] : \nVersion connue : " + p_oldVersion + "\nVersion en base : " + p_lastVersion);
   }

}
