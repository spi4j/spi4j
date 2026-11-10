/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import java.lang.reflect.Field;
import java.util.Date;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.persistence.entity.Spi4jLockException;

/**
 * Classe de méthodes utilitaires pour la gestion du Lock Optimiste.
 * @author MINARM
 */
public final class JdbcVersionHelper
{

   /** Paramètre utilisé dans les requêtes update et delete. */
   static final String c_requestParameter = "spi4j_version_value";

   /**
    * Constructeur privé.
    */
   private JdbcVersionHelper ()
   {
      super();
   }

   /**
    * Vérifie si une entité est versionnée.
    * @param p_Entity
    *           l'entity
    * @return true si l'entité est versionnée, false sinon
    */
   static boolean isEntityVersionned (final Entity_Itf<Long> p_Entity)
   {
      // une entité versionnée en Jdbc doit implémenter cette interface
      return p_Entity instanceof JdbcOptimistLocked_Itf;
   }

   /**
    * Retourne le champ qui possède l'annotation 'JdbcVersion'.
    * @param p_Entity
    *           l'entité
    * @return le champ qui possède l'annotation 'JdbcVersion'
    */
   public static Field getVersionField (final Entity_Itf<?> p_Entity)
   {
      // cherche dans l'entité le champ annoté @JdbcVersion
      for (final Field v_field : p_Entity.getClass().getDeclaredFields())
      {
         if (v_field.isAnnotationPresent(JdbcVersion.class))
         {
            v_field.setAccessible(true);
            return v_field;
         }
      }
      throw new Spi4jRuntimeException("L'entité n'a aucun champ annoté @JdbcVersion", "Vérifier l'entité "
               + p_Entity.getClass());
   }

   /**
    * Retourne le nom physique de la colonne qui gère la version de l'entité.
    * @param p_Entity
    *           l'entité
    * @param p_columnNames
    *           la liste des colonnes de cette entité
    * @return le nom physique de la colonne qui gère la version de l'entité
    */
   static String getVersionPhysicalName (final Entity_Itf<Long> p_Entity, final ColumnsNames_Itf[] p_columnNames)
   {
      // récupère le champ qui contient l'annotation @JdbcVersion
      final Field v_field = getVersionField(p_Entity);

      // retire le _ en début du nom d'attribut
      String v_fieldName = v_field.getName();
      if (v_fieldName.startsWith("_"))
      {
         v_fieldName = v_fieldName.substring(1);
      }

      // cherche le Columns_Enum qui a le bon nom
      for (final ColumnsNames_Itf v_column : p_columnNames)
      {
         if (v_column.getLogicalColumnName().equals(v_fieldName))
         {
            return v_column.getPhysicalColumnName();
         }
      }
      throw new Spi4jRuntimeException(
               "Le champ " + v_fieldName + " n'a pas été trouvé dans l'énumération des colonnes", "Vérifier l'entité "
                        + p_Entity.getClass());
   }

   /**
    * Retourne la valeur de la version de cette entité.
    * @param p_Entity
    *           l'entité
    * @return la valeur de la version de cette entité
    */
   static Object getVersionValue (final Entity_Itf<Long> p_Entity)
   {
      // récupère le champ qui contient l'annotation @JdbcVersion
      final Field v_versionField = getVersionField(p_Entity);
      try
      {
         // récupère la valeur du champ pour cette entité
         return v_versionField.get(p_Entity);
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Impossible de récupérer la version de l'entité", "Vérifier l'entité "
                  + p_Entity.getClass());
      }
   }

   /**
    * Modifie la valeur de la version de cette entité.
    * @param p_Entity
    *           l'entité
    * @param p_newVersionValue
    *           la nouvelle valeur
    */
   private static void setVersionValue (final Entity_Itf<Long> p_Entity, final Object p_newVersionValue)
   {
      // récupère le champ qui contient l'annotation @JdbcVersion
      final Field v_versionField = getVersionField(p_Entity);
      try
      {
         // modifie la valeur du champ pour cette entité
         v_versionField.set(p_Entity, p_newVersionValue);
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(v_e, "Impossible de récupérer la version de l'entité", "Vérifier l'entité "
                  + p_Entity.getClass());
      }
   }

   /**
    * Met à jour la nouvelle version dans l'entité et retourne l'ancienne version.
    * @param p_Entity
    *           l'entité à mettre à jour
    * @return l'ancienne version de l'entité
    */
   static Object updateVersionForEntity (final Entity_Itf<Long> p_Entity)
   {
      // récupère la version actuelle de l'entité
      final Object v_oldLockValue = getVersionValue(p_Entity);

      // récupère le champ qui contient l'annotation @JdbcVersion
      final Field v_field = getVersionField(p_Entity);

      // si ce champ est un nombre
      if (Number.class.isAssignableFrom(v_field.getType()))
      {
         if (v_oldLockValue == null)
         {
            // s'il n'a pas encore de version, on positionne à 1
            setVersionValue(p_Entity, 1);
         }
         else
         {
            // s'il y a déjà une valeur dans le champ, on incrémente
            final long v_newLockValue = ((Number) v_oldLockValue).longValue() + 1;
            setVersionValue(p_Entity, v_newLockValue);
         }
      }
      // si le champ est une date, la nouvelle version est la date actuelle (sans millisecondes)
      else if (Date.class.isAssignableFrom(v_field.getType()))
      {
         // on ne stocke pas les millisecondes en base de données, donc pas dans l'entity non plus
         final long v_currentTime = System.currentTimeMillis() / 1000 * 1000;
         setVersionValue(p_Entity, new Date(v_currentTime));
      }
      else
      {
         // type de champ non géré pour la version (seulement nombre ou date)
         throw new Spi4jRuntimeException("Le type " + v_field.getType()
                  + " n'est pas géré en tant que version pour le lock optimiste", "Modéliser le champ "
                  + v_field.getName() + " avec un type Date ou Nombre");
      }
      return v_oldLockValue;
   }

   /**
    * Création de l'exception pour un problème de Lock Optimiste sur une entité.
    * @param p_instance
    *           l'instance qui n'a pas pu être modifiée
    * @param p_oldVersion
    *           l'ancienne version connue par le client
    * @param p_lastVersion
    *           la dernière version en base
    * @return Spi4JLockException
    */
   static Spi4jLockException createExceptionForLock (final Entity_Itf<?> p_instance, final Object p_oldVersion,
            final Object p_lastVersion)
   {
      throw new Spi4jLockException(p_instance, p_oldVersion, p_lastVersion);
   }
}
