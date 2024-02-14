/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.persistence.resource.ResourceManager_Abs;
import fr.spi4j.persistence.resource.ResourcePhysical_Abs;
import jakarta.transaction.SystemException;
import net.bull.javamelody.JdbcWrapper;

/**
 * Gestionnaire de ressources JDBC non XA.
 * @author MINARM
 */
public abstract class NonXAJdbcResourceManager_Abs extends ResourceManager_Abs implements JdbcResourceManager_Itf
{
   private DataSource _dataSource;

   private final ThreadLocal<Connection> _threadLocal = new ThreadLocal<>();

   private String _debugInfo;

   /**
    * Constructeur.
    */
   protected NonXAJdbcResourceManager_Abs ()
   {
      super();
   }

   /**
    * @return la connexion du thread courant ou null s'il n'y en a pas
    */
   protected Connection getCurrentConnectionIfExists ()
   {
      return _threadLocal.get();
   }

   /**
    * Définit la connexion du thread courant, qui peut être null s'il n'y en a plus
    * @param p_currentConnection
    *           Connexion courante ou null
    */
   private void setCurrentConnection (final Connection p_currentConnection)
   {
      if (p_currentConnection == null)
      {
         _threadLocal.remove();
      }
      else
      {
         _threadLocal.set(p_currentConnection);
      }
   }

   @Override
   public Connection getCurrentConnection () throws SystemException
   {
      Connection v_connection = getCurrentConnectionIfExists();
      if (v_connection == null)
      {
         // récupère une nouvelle connexion
         v_connection = getConnection();
         setCurrentConnection(v_connection);
      }
      return v_connection;
   }

   @Override
   public void closeCurrentConnection () throws SQLException
   {
      final Connection v_connection = getCurrentConnectionIfExists();
      // s'il n'y a pas de connexion jdbc associée au thread courant dans ce manager de ressource
      // c'est que l'application n'a pas eu besoin de cette ressource et donc il n'y a pas de close à faire
      if (v_connection != null)
      {
         try
         {
            v_connection.close();
         }
         finally
         {
            setCurrentConnection(null);
         }
      }
   }

   /**
    * Commit de la connexion jdbc du thread courant si elle existe.
    * @throws SQLException
    *            e
    */
   public void commitCurrentConnection () throws SQLException
   {
      final Connection v_connection = getCurrentConnectionIfExists();
      // s'il n'y a pas de connexion jdbc associée au thread courant dans ce manager de ressource
      // c'est que l'application n'a pas eu besoin de cette ressource et donc il n'y a pas de commit à faire
      if (v_connection != null)
      {
         v_connection.commit();
      }
   }

   /**
    * Rollback de la connexion jdbc du thread courant si elle existe.
    * @throws SQLException
    *            e
    */
   public void rollbackCurrentConnection () throws SQLException
   {
      final Connection v_connection = getCurrentConnectionIfExists();
      // s'il n'y a pas de connexion jdbc associée au thread courant dans ce manager de ressource
      // c'est que l'application n'a pas eu besoin de cette ressource et donc il n'y a pas de rollback à faire
      if (v_connection != null)
      {
         v_connection.rollback();
      }
   }

   /**
    * Obtention d'une nouvelle connexions jdbc depuis la DataSource (qui va certainement la récupérer dans son propre pool de connexions)
    * @return Connection
    * @throws SystemException
    *            e
    */
   public final Connection getConnection () throws SystemException
   {
      try
      {
         // inutile d'utiliser WrapJdbcConnection et un ConnectionEventListener pour purger le _connectionThreadLocal
         // puisque closeConnection() sera toujours appelée à la fin de la transaction
         final Connection v_connection = _dataSource.getConnection();
         // les connexions doivent toujours être en autoCommit false, sinon il n'y a pas de transaction
         v_connection.setAutoCommit(false);
         return v_connection;
      }
      catch (final SQLException v_exception)
      {
         final String v_message = "Erreur d'accès à la base de données (" + v_exception.getMessage().trim() + ")";
         // Afficher les informations de connexion à la base en cas de problème.
         // Les informations sont juste affichées dans les logs pour ne pas être remontés au client (faille de sécurité).
         if (_debugInfo != null)
         {
            LogManager.getRootLogger().error(v_message + "\n" + _debugInfo);
         }
         throw (SystemException) new SystemException(v_message).initCause(v_exception);
      }
   }

   /**
    * @return DataSource
    */
   protected final DataSource getDataSource ()
   {
      return _dataSource;
   }

   @Override
   protected void initResourcePhysical (final ResourcePhysical_Abs p_ResourcePhysical) throws SystemException
   {
      // Initialisation des informations de debug (utiles entre autres en cas d'erreur de connexion) : toString de la ressource physique
      _debugInfo = p_ResourcePhysical.toString();
      try
      {
         final CommonDataSource v_dataSource = createDataSource(p_ResourcePhysical);
         if (v_dataSource instanceof DataSource)
         {
            _dataSource = (DataSource) v_dataSource;
         }
         else
         {
            throw new SystemException(
                     "Cette implémentation n'est pas prévue pour des connexions XA, vous devez utiliser une DataSource non XA\n"
                              + "ou bien si vous souhaitez des connexions XA utiliser un resource manager supportant XA tel que ceux du module spi4j-transaction-xa");
         }

         if (JdbcResourceManager_Itf.c_monitoring_enabled)
         {
            // cela permet d'avoir accès aux statistiques de la base de données à partir du monitoring
            // (en Oracle : connection ouvertes et requêtes en cours, requêtes les plus longues, locks...)
            JdbcWrapper.registerSpringDataSource(p_ResourcePhysical.getUrl(), _dataSource);
            // cela permet d'avoir accès aux statistiques des requêtes sql à partir du monitoring (si WrapJdbcConnection ne le fait pas déjà)
            _dataSource = JdbcWrapper.SINGLETON.createDataSourceProxy(_dataSource);
         }
      }
      catch (final SQLException v_ex)
      {
         throw (SystemException) new SystemException("La création du DataSource a échoué: ").initCause(v_ex);
      }
   }

   /**
    * Appelle commit et close sur toutes les connexions non XA.<br/>
    * (s'il y a un problème, alors appelle rollback et close autant que possible sur toutes les connexions non XA)
    * @throws SystemException
    *            e
    */
   public static void commitAndCloseAllNonXAConnections () throws SystemException
   {
      try
      {
         final List<NonXAJdbcResourceManager_Abs> v_list = getAllNonXAResourceManager();
         for (final NonXAJdbcResourceManager_Abs v_resourceManager : v_list)
         {
            // on commit
            // (si exception, le catch Throwable ci-dessous appelera les rollback et surtout tous les close)
            v_resourceManager.commitCurrentConnection();
            // et on libère la connexion jdbc
            v_resourceManager.closeCurrentConnection();
         }
      }
      catch (final Throwable v_ex)
      {
         // si il y a une exception ou une error, on essaye de faire des rollback sur ce que l'on peut
         // et dans tous les cas il faut absolument fermer toutes les connexions que l'on peut fermer.
         rollbackAndCloseAllNonXAConnections();

         // puis on dit qu'il y a eu un problème en lançant une exception à l'appelant (si le rollback n'a pas déjà lancé l'exception)
         final SystemException v_systemException = new SystemException(v_ex.getMessage());
         v_systemException.initCause(v_ex);
         throw v_systemException;
      }
   }

   /**
    * Appelle rollback et close autant que possible sur toutes les connexions non XA.
    * @throws SystemException
    *            e
    */
   public static void rollbackAndCloseAllNonXAConnections () throws SystemException
   {
      final List<NonXAJdbcResourceManager_Abs> v_list = getAllNonXAResourceManager();
      Throwable v_throwable = null;
      for (final NonXAJdbcResourceManager_Abs v_resourceManager : v_list)
      {
         try
         {
            try
            {
               v_resourceManager.rollbackCurrentConnection();
            }
            finally
            {
               // même si le rollback ne fonctionne pas on essaye de libérer la connexion jdbc
               v_resourceManager.closeCurrentConnection();
            }
         }
         catch (final Throwable v_ex)
         {
            if (v_throwable == null)
            {
               v_throwable = v_ex;
            }
            // si il y a une exception ou une error, on continue à faire rollback et à fermer toutes les connexions que l'on peut fermer.
            // Il faut absolument appeler close() sur toutes les connexions même si une partie se passe mal.
            continue;
         }
      }
      if (v_throwable != null)
      {
         // puis s'il y a eu une exception ou une error, on dit qu'il y a eu un problème en lançant une exception à l'appelant
         // avec le premier des problèmes en tant que cause
         final SystemException v_systemException = new SystemException(v_throwable.getMessage());
         v_systemException.initCause(v_throwable);
         throw v_systemException;
      }
   }

   /**
    * @return Liste des ResourceManager (instances de NonXAResourceManager_Abs et si il existe des ResourceManagers XA, alors lance une exception)
    * @throws SystemException
    *            e
    */
   private static List<NonXAJdbcResourceManager_Abs> getAllNonXAResourceManager () throws SystemException
   {
      final List<NonXAJdbcResourceManager_Abs> v_result = new ArrayList<>();
      for (final Map<ResourcePhysical_Abs, ResourceManager_Abs> v_mapResourceManager : getAllResourceManager().values())
      {
         for (final ResourceManager_Abs v_resourceManager : v_mapResourceManager.values())
         {
            if (v_resourceManager instanceof NonXAJdbcResourceManager_Abs)
            {
               v_result.add((NonXAJdbcResourceManager_Abs) v_resourceManager);
            }
            else
            {
               throw new SystemException(
                        "Ce ResoureManager n'est pas une instance de NonXAResourceManager et pour utiliser des ressources XA vous devriez plutôt utiliser l'implémentation XA de UserTransaction");
            }
         }
      }
      return v_result;
   }

   /**
    * createDataSource.
    * @param p_resourcePhysical
    *           ResourcePhysical
    * @return CommonDataSource
    * @throws SQLException
    *            e
    */
   protected abstract CommonDataSource createDataSource (ResourcePhysical_Abs p_resourcePhysical) throws SQLException;
}
