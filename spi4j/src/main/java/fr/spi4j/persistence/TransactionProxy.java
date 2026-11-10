/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

import fr.spi4j.ReflectUtil;
import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.persistence.resource.ResourceManager_Abs;
import fr.spi4j.persistence.resource.jdbc.JdbcResourceManager_Itf;
import fr.spi4j.persistence.resource.jdbc.NonXAJdbcResourceManager_Abs;

/**
 * Proxy des interfaces d'un composant (par exemple une instance de service) qui démarre une transaction avant puis qui commit la transaction après ou qui rollback la transaction en cas d'exception.
 */
public final class TransactionProxy implements InvocationHandler
{
   private final UserPersistence_Abs _UserPersistence;

   private final Object _delegate;

   /**
    * Constructeur.
    * @param p_UserPersistence
    *           UserPersistence de l'application.
    * @param p_delegate
    *           Composant
    */
   private TransactionProxy (final UserPersistence_Abs p_UserPersistence, final Object p_delegate)
   {
      super();
      _UserPersistence = p_UserPersistence;
      _delegate = p_delegate;
   }

   /**
    * Factory de création du proxy de transaction pour un composant (par exemple, un service).
    * @param <TypeService>
    *           Le type du du composant (par exemple, un service)
    * @param p_UserPersistence
    *           Le userPersistence de l'application
    * @param p_delegate
    *           Le composant
    * @return Le proxy du composant gérant les transactions
    */
   @SuppressWarnings("unchecked")
   public static <TypeService> TypeService createProxy (final UserPersistence_Abs p_UserPersistence,
            final TypeService p_delegate)
   {
      final TransactionProxy v_TransactionProxy = new TransactionProxy(p_UserPersistence, p_delegate);
      return (TypeService) Proxy.newProxyInstance(p_delegate.getClass().getClassLoader(), p_delegate.getClass()
               .getInterfaces(), v_TransactionProxy);
   }

   @Override
   public Object invoke (final Object p_proxy, final Method p_method, final Object[] p_args) throws Throwable
   {
      _UserPersistence.begin();
      try
      {
         final Object v_result;
         try
         {
            v_result = ReflectUtil.invokeMethod(p_method, _delegate, p_args);
         }
         finally
         {
            // Spécifique au mode XA :
            // si le gestionnaire de transactions est le mode XA de Spi4j, il faut appeler close() sur la connexion jdbc après une requête dans le DAO
            // (ce qui en soit est plus qu'étrange, mais c'était le cas au départ dans Dao_Jdbc et avant cela dans JavagenAdapter utilisé par Dao_Javagen)
            closeCurrentConnectionIfDaoXA();
         }
         _UserPersistence.commit();
         return v_result;
      }
      catch (final Throwable v_ex)
      {
         _UserPersistence.rollback();
         throw v_ex;
      }
   }

   /**
    * Spécifique mode XA : appelle close() sur la connexion jdbc du thread courant si c'est un proxy de DAO avec un gestionnaire de transaction XA.
    * @throws SQLException
    *            e
    */
   private void closeCurrentConnectionIfDaoXA () throws SQLException
   {
      if (_delegate instanceof Dao_Itf)
      {
         final Dao_Itf<?, ?, ?> v_dao = (Dao_Itf<?, ?, ?>) _delegate;
         final ResourceManager_Abs v_ResourceManager = v_dao.getResourceManager();
         if (v_ResourceManager instanceof JdbcResourceManager_Itf
                  && !(v_ResourceManager instanceof NonXAJdbcResourceManager_Abs))
         {
            ((JdbcResourceManager_Itf) v_ResourceManager).closeCurrentConnection();
         }
      }
   }
}
