/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.exception;

import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Classe d'exception lorsqu'une entité n'est pas trouvée en base (suppression concurrente ?). Il faut surement raffraichir les données sur le client.
 * @author MINARM
 */
public class Spi4jEntityNotFoundException extends RuntimeException
{

   /**
    * SerialUid.
    */
   private static final long serialVersionUID = 4793201612441957811L;

   /**
    * Constructeur sans paramètres pour sérialisation GWT.
    */
   protected Spi4jEntityNotFoundException ()
   {
      // RAS
   }

   /**
    * Constructeur pour une entité non trouvée en base.
    * @param <TypeId>
    *           le type d'id de cette entité
    * @param p_typeEntity
    *           le type d'entité qui n'a pas été trouvée
    * @param p_entityId
    *           l'id de l'entité qui n'a pas été trouvée
    */
   public <TypeId> Spi4jEntityNotFoundException (final Class<Entity_Itf<TypeId>> p_typeEntity, final TypeId p_entityId)
   {
      this(p_typeEntity.getName(), p_entityId, null);
   }

   /**
    * Constructeur pour une entité non trouvée en base.
    * @param <TypeId>
    *           le type d'id de cette entité
    * @param p_tableName
    *           le nom de la table dans laquelle l'entité est cherchée
    * @param p_entityId
    *           l'id de l'entité qui n'a pas été trouvée
    */
   public <TypeId> Spi4jEntityNotFoundException (final String p_tableName, final TypeId p_entityId)
   {
      this(p_tableName, p_entityId, null);
   }

   /**
    * Constructeur pour une entité non trouvée en base, avec une cause.
    * @param <TypeId>
    *           le type d'id de cette entité
    * @param p_entityName
    *           le nom de l'entité ou de la table dans laquelle l'entité n'a pas été trouvée
    * @param p_entityId
    *           l'id de l'entité qui n'a pas été trouvée
    * @param p_cause
    *           la cause de la non validité
    */
   public <TypeId> Spi4jEntityNotFoundException (final String p_entityName, final TypeId p_entityId,
            final Throwable p_cause)
   {
      super("Entité non trouvée en base : " + p_entityName + ", id = " + p_entityId, p_cause);
   }

}
