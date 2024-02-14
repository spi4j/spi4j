/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.ReflectUtil;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.resource.jdbc.driver.DbcpNonXAResourceManager;
import fr.spi4j.persistence.resource.jdbc.driver.H2NonXAResourceManager;
import fr.spi4j.persistence.resource.jdbc.driver.MysqlNonXAResourceManager;
import fr.spi4j.persistence.resource.jdbc.driver.NonXADatasourceManager;
import fr.spi4j.persistence.resource.jdbc.driver.OdbcResourceManager;
import fr.spi4j.persistence.resource.jdbc.driver.OracleNonXAResourceManager;
import jakarta.transaction.SystemException;

/**
 * @author MINARM
 * @author Source : SimpleJTA by Dibyendu Majumdar (http://www.simplejta.org/)
 */
public abstract class ResourceManager_Abs
{
   private static final Logger c_log = LogManager.getLogger(ResourceManager_Abs.class);

   private static final Map<Class<?>, Class<?>> c_map_primitiveTypes = new HashMap<>();

   private static final Map<String, Map<ResourcePhysical_Abs, ResourceManager_Abs>> c_map_allResourceManager = new HashMap<>();

   private static final Map<ResourceType_Enum, Class<? extends ResourceManager_Abs>> c_map_resourceManagerImplementation = new HashMap<>();
   static
   {
      c_map_primitiveTypes.put(Integer.class, Integer.TYPE);
      c_map_primitiveTypes.put(Long.class, Long.TYPE);
      c_map_primitiveTypes.put(Short.class, Short.TYPE);
      c_map_primitiveTypes.put(Boolean.class, Boolean.TYPE);
      c_map_primitiveTypes.put(Double.class, Double.TYPE);
      c_map_primitiveTypes.put(Float.class, Float.TYPE);
      c_map_primitiveTypes.put(Character.class, Character.TYPE);
      c_map_primitiveTypes.put(Byte.class, Byte.TYPE);

      loadResourceManagerNonXA();
   }

   /**
    * @param p_ResourcePhysical
    *           .
    * @throws SystemException
    *            e
    */
   protected abstract void initResourcePhysical (ResourcePhysical_Abs p_ResourcePhysical) throws SystemException;

   /**
    * @return Map des ResourceManager.
    */
   protected static final Map<String, Map<ResourcePhysical_Abs, ResourceManager_Abs>> getAllResourceManager ()
   {
      return c_map_allResourceManager;
   }

   /**
    * Charge le gestionnaire de ressources.
    */
   private static void loadResourceManagerNonXA ()
   {
      try
      {
         c_log.debug("Chargement des gestionnaires de ressources");

         registerResourceManager(ResourceType_Enum.ressourceOdbc_v, OdbcResourceManager.class);
         registerResourceManager(ResourceType_Enum.ressourceOracleNonXA, OracleNonXAResourceManager.class);
         registerResourceManager(ResourceType_Enum.ressourceMySqlNonXA, MysqlNonXAResourceManager.class);
         registerResourceManager(ResourceType_Enum.ressourceDbcpNonXA, DbcpNonXAResourceManager.class);
         registerResourceManager(ResourceType_Enum.ressourceH2NonXA, H2NonXAResourceManager.class);
         registerResourceManager(ResourceType_Enum.datasourceNonXA, NonXADatasourceManager.class);

         c_log.debug("Les gestionnaires de ressources sont chargés");
      }
      catch (final Exception v_e)
      {
         c_log.error("Le chargement des gestionnaires de ressources a échoué. ", v_e);
      }
   }

   /**
    * @param p_ResourceType_Enum
    *           .
    * @param p_class
    *           .
    */
   protected static void registerResourceManager (final ResourceType_Enum p_ResourceType_Enum,
            final Class<? extends ResourceManager_Abs> p_class)
   {
      c_map_resourceManagerImplementation.put(p_ResourceType_Enum, p_class);
   }

   /**
    * Permet d'obtenir un gestionnaire de ressource.
    * @param p_tmId
    *           (In) L'identifiant du gestionnaire de transaction
    * @param p_ResourcePhysical
    *           La ressource physique
    * @return Un gestionnaire de ressource
    */
   public static synchronized ResourceManager_Abs getResourceManager (final String p_tmId,
            final ResourcePhysical_Abs p_ResourcePhysical)
   {
      // Obtenir la map de gestionnaires de ressource associés à un gestionnaire de transaction
      Map<ResourcePhysical_Abs, ResourceManager_Abs> v_map_ResourceManager = c_map_allResourceManager.get(p_tmId);
      // Le gestionnaire de ressource à retourner
      ResourceManager_Abs v_rm = null;

      if (p_ResourcePhysical == null)
      {
         throw new Spi4jRuntimeException("Le paramètre ResourcePhysical ne doit pas être null", "");
      }

      if (v_map_ResourceManager == null)
      {
         v_map_ResourceManager = new HashMap<>();
         c_map_allResourceManager.put(p_tmId, v_map_ResourceManager);
      }
      else
      {
         // Parcourir les ressources physiques
         for (final Map.Entry<ResourcePhysical_Abs, ResourceManager_Abs> v_Entry : v_map_ResourceManager.entrySet())
         {
            final ResourcePhysical_Abs v_ResourcePhysical = v_Entry.getKey();
            // Si les ressources physiques sont identiques
            if (v_ResourcePhysical.equals(p_ResourcePhysical))
            {
               // Récupérer le gestionnaire de ressource
               v_rm = v_Entry.getValue();
            }
         }
      }
      // Si le gestionnaire de ressource n'a pas été récupéré
      if (v_rm == null)
      {
         v_rm = createResourceManager(p_tmId, p_ResourcePhysical);
         // Enregistrer le gestionnaire de ressource dans la map
         v_map_ResourceManager.put(p_ResourcePhysical, v_rm);
      }
      return v_rm;
   }

   /**
    * Création du ResourceManager
    * @param p_tmId
    *           (In) L'identifiant du gestionnaire de transaction
    * @param p_ResourcePhysical
    *           La ressource physique
    * @return ResourceManager_Abs
    */
   private static ResourceManager_Abs createResourceManager (final String p_tmId,
            final ResourcePhysical_Abs p_ResourcePhysical)
   {
      // Obtenir l'identifiant du gestionnaire de ressource
      final String v_rmId = p_ResourcePhysical.getResourceType().getRmId();
      // Récupérer la classe de gestionnaire de ressource correspondant au type de ressource
      final Class<? extends ResourceManager_Abs> v_class = c_map_resourceManagerImplementation.get(p_ResourcePhysical
               .getResourceType());
      if (v_class == null)
      {
         throw new Spi4jRuntimeException(null, "L'identifiant du gestionnaire de ressource est inconnu: " + v_rmId,
                  "Vérifier que l'identifiant du gestionnaire de ressource '" + v_rmId
                           + "' est bien est bien associé à un gestionnaire de ressource dans la classe '"
                           + ResourceManager_Abs.class.getName() + "'.");
      }
      final ResourceManager_Abs v_rm;
      // Instancier la classe
      try
      {
         v_rm = v_class.getDeclaredConstructor().newInstance();
      }
      catch (final IllegalAccessException v_ex)
      {
         throw new Spi4jRuntimeException(v_ex, "Le gestionnaire de ressource '" + v_rmId + "' ne peut être instancié.",
                  "Vérifier la visibilité du constructeur par défaut de la classe '" + v_class.getName() + "'.");
      }
      catch (final Exception v_ex)
      {
         throw new Spi4jRuntimeException(v_ex, "Le gestionnaire de ressource '" + v_rmId + "' ne peut être instancié.",
                  "Vérifier l'existence d'un constructeur par défaut de la classe '" + v_class.getName() + "'.");
      }
      // Récupérer le gestionnaire de transaction
      try
      {
         v_rm.initTransactionManager(p_tmId);
      }
      catch (final SystemException v_ex)
      {
         throw new Spi4jRuntimeException(v_ex, "Le gestionnaire de transaction ne peut pas être récupéré.", "");
      }

      try
      {
         // Initialiser la ressource physique du gestionnaire de ressource
         v_rm.initResourcePhysical(p_ResourcePhysical);
      }
      catch (final SystemException v_ex)
      {
         throw new Spi4jRuntimeException(v_ex, "La ressource physique ne peut être initialisée.", "");
      }
      return v_rm;
   }

   /**
    * Initialisation du TransactionManager dans l'implémentation XA.
    * @param p_tmId
    *           String
    * @throws SystemException
    *            e
    */
   @SuppressWarnings("unused")
   protected void initTransactionManager (final String p_tmId) throws SystemException
   {
      // Rien par défaut en mode non XA
   }

   /**
    * Permet de créer une nouvelle instance de Datasource.
    * @param p_className
    *           Le nom de la classe du Datasource.
    * @param p_props
    *           La map contenant les paramètres d'initialisation.
    * @return Une instance du Datasource
    * @throws SQLException
    *            si problème SQL
    */
   protected static Object createDataSource (final String p_className, final Map<String, Object> p_props)
            throws SQLException
   {
      try
      {
         final Class<?> v_cl = Class.forName(p_className);
         c_log.debug("Création d'une instance du DataSource: " + p_className);
         final Object v_ds = v_cl.getDeclaredConstructor().newInstance();
         for (final Entry<String, Object> v_entry : p_props.entrySet())
         {
            String v_methodName = v_entry.getKey();
            v_methodName = "set" + v_methodName.substring(0, 1).toUpperCase() + v_methodName.substring(1);
            final Object v_value = v_entry.getValue();
            invokeMethod(v_cl, v_ds, v_methodName, v_value);
            c_log.debug("DataSource: " + p_className + "." + v_methodName + "(" + v_value.toString() + ")");
         }
         return v_ds;
      }
      catch (final Throwable v_e)
      {
         throw (SQLException) new SQLException("SPI4J-TRANSACTION-E011: La création du DataSource a échoué: "
                  + p_className).initCause(v_e);
      }
   }

   /**
    * Appelle une méthode sur un objet.
    * @param p_cl
    *           Le type de l'instance
    * @param p_instance
    *           L'objet sur lequel la méthode sera appelée
    * @param p_methodName
    *           Le nom de la méthode
    * @param p_param
    *           Le paramètre de la méthode
    * @return Le résultat de la méthode
    * @throws Throwable
    *            si la méthode n'est pas trouvée ou si un problème a lieu lors de l'invocation
    */
   private static Object invokeMethod (final Class<?> p_cl, final Object p_instance, final String p_methodName,
            final Object p_param) throws Throwable
   {
      // on prend le type primitif si il y en a un
      Class<?> v_paramClass = c_map_primitiveTypes.get(p_param.getClass());
      // ou le type de l'objet sinon
      if (v_paramClass == null)
      {
         v_paramClass = p_param.getClass();
      }

      final Method v_method = p_cl.getMethod(p_methodName, v_paramClass);
      return ReflectUtil.invokeMethod(v_method, p_instance, p_param);
   }
}
