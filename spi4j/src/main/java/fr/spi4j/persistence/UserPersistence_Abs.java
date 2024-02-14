/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.ProxyFactory_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.persistence.dao.jdbc.DaoJdbc_Abs;
import fr.spi4j.persistence.dao.jdbc.DatabaseChecker;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.persistence.resource.ResourceManager_Abs;
import jakarta.transaction.UserTransaction;

/**
 * Classe abstraite permettant de centraliser tous les traitements de persistance.
 * @author MINARM
 */
public abstract class UserPersistence_Abs
{

   /** Le paramétrage de l'application. */
   private final ParamPersistence_Abs _ParamPersistence;

   /** Le 'UserTransaction' de l'application. */
   private final UserTransaction _UserTransaction;

   private ProxyFactory_Itf _daoProxyFactory;

   /**
    * Constructeur privé.
    * @param p_ParamPersistence
    *           Le ParamPersistence
    */
   protected UserPersistence_Abs (final ParamPersistence_Abs p_ParamPersistence)
   {
      super();
      _ParamPersistence = p_ParamPersistence;
      _UserTransaction = p_ParamPersistence.getUserTransaction();
   }

   /**
    * Initialisation de la factory de proxies pour les DAOs.
    * @param p_proxyFactory
    *           la factory de proxies
    */
   public void initDaoProxyFactory (final ProxyFactory_Itf p_proxyFactory)
   {
      _daoProxyFactory = p_proxyFactory;
   }

   /**
    * Permet d'obtenir le gestionnaire de transaction de l'application.
    * @return Le gestionnaire de transaction.
    */
   public final UserTransaction getUserTransaction ()
   {
      return _UserTransaction;
   }

   /**
    * Permet de démarrer une transaction.
    */
   public void begin ()
   {
      try
      {
         getUserTransaction().begin();
      }
      catch (final Exception v_ex)
      {
         throw new Spi4jRuntimeException(v_ex, v_ex.getMessage(), "");
      }
   }

   /**
    * Permet de valider une transaction.
    */
   public void commit ()
   {
      try
      {
         getUserTransaction().commit();
      }
      catch (final Exception v_ex)
      {
         throw new Spi4jRuntimeException(v_ex, v_ex.getMessage(), "");
      }
   }

   /**
    * Permet d'invalider une transaction.
    */
   public void rollback ()
   {
      try
      {
         getUserTransaction().rollback();
      }
      catch (final Exception v_ex)
      {
         throw new Spi4jRuntimeException(v_ex, v_ex.getMessage(), "");
      }
   }

   /**
    * Modifie la transaction associée avec le thread courant telle que la transaction se termine forcément par un rollback, même si un commit est appelé.
    */
   public void setRollbackOnly ()
   {
      try
      {
         getUserTransaction().setRollbackOnly();
      }
      catch (final Exception v_ex)
      {
         throw new Spi4jRuntimeException(v_ex, v_ex.getMessage(), "");
      }
   }

   /**
    * Obtenir l'instance de l'entité à partir de son alias.
    * @param <T>
    *           Type générique de l'entity
    * @param p_alias
    *           (In)(*) L'alias de l'entité désirée.
    * @return L'instance désirée.
    */
   @SuppressWarnings("unchecked")
   protected <T extends Entity_Itf<?>> T getEntity (final Class<? extends Entity_Itf<?>> p_alias)
   {
      final ElemParamPersistence v_ElemParamPersistence = getElemParamPersistence(p_alias);
      // Obtenir la classe de l'entité
      final Class<? extends Entity_Itf<?>> v_ClassEntity = v_ElemParamPersistence.getEntity();
      // Obtenir l'instance désirée (du type 'v_ClassEntity')
      final T v_Entity = (T) getInstanceOfClass(v_ClassEntity);

      return v_Entity;
   }

   /**
    * Obtenir l'instance de DAO à partir de son alias.
    * @param <T>
    *           Type générique du DAO
    * @param p_alias
    *           (In)(*) L'alias du DAO désiré.
    * @return L'instance désirée.
    */
   @SuppressWarnings("unchecked")
   protected <T extends Dao_Itf<?, ?, ?>> T getDao (final Class<? extends Entity_Itf<?>> p_alias)
   {
      final ElemParamPersistence v_ElemParamPersistence = getElemParamPersistence(p_alias);
      // Obtenir la classe du DAO
      final Class<? extends Dao_Itf<?, ?, ?>> v_ClassDao = v_ElemParamPersistence.getDao();

      // il est inutile de mettre un proxy de transaction sur les DAOs
      // puisque tous les appels aux DAOs passent par des services
      // et puisque les services ont déjà des proxys de transaction
      // v_Dao = TransactionProxy.createProxy(this, v_Dao);

      final T v_Dao;
      if (_daoProxyFactory == null)
      {
         // Obtenir l'instance désirée (du type 'v_ClassDao')
         v_Dao = (T) getInstanceOfClass(v_ClassDao);
      }
      else
      {
         final Class<?>[] v_interfaces = v_ClassDao.getInterfaces();
         final Class<Dao_Itf<?, ?, ?>> v_interface;
         if (v_interfaces.length == 0)
         {
            v_interface = null;
         }
         else
         {
            v_interface = (Class<Dao_Itf<?, ?, ?>>) v_interfaces[0];
         }
         v_Dao = (T) _daoProxyFactory.getProxiedService(v_interface, v_ClassDao.getName());
      }
      // Obtenir la ressource associée
      final ResourceManager_Abs v_ResourceManager = v_ElemParamPersistence.getResourceManager();
      // Affecter la ressource nécessaire au fonctionnement du DAO
      v_Dao.setResourceManager(v_ResourceManager);

      return v_Dao;
   }

   /**
    * Obtenir une instance de DAO non branchée à une entité.
    * @return le DAO par défaut
    */
   public Dao_Itf<?, ?, ?> getDefaultDao ()
   {
      return getDao(NoEntity.class);
   }

   /**
    * Retourne l'ElemParamPersistence selon l'alias en paramètre et selon idAppli.
    * @param p_alias
    *           String
    * @return ElemParamPersistence
    */
   private ElemParamPersistence getElemParamPersistence (final Class<? extends Entity_Itf<?>> p_alias)
   {
      // Obtenir l'instance décrivant le triplet (Entity, DAO, Ressource) pour l'alias 'p_alias'
      return _ParamPersistence.getElemParamPersistence(p_alias);
   }

   /**
    * Obtenir l'instance à partir de la classe spécifiée.
    * @param <T>
    *           Type du résultat (évite à l'appelant de faire le cast)
    * @param p_Class
    *           (In)(*) La classe à instancier (nécessite un constructeur par défaut).
    * @return L'instance désirée.
    */
   @SuppressWarnings("unchecked")
   private <T> T getInstanceOfClass (final Class<?> p_Class)
   {
      try
      {
         // Instancier l'entité désiré (cf. 'v_ClassName')
         return (T) p_Class.getDeclaredConstructor().newInstance();
      }

      catch (final IllegalAccessException v_err)
      {
         final String v_msgError = "Problème d'accès au constructeur par défaut de la classe '" + p_Class.getName()
                  + "'";
         final String v_msgSolution = "Vérifier que le constructeur de la classe '" + p_Class.getName()
                  + "' est 'public'";
         throw new Spi4jRuntimeException(v_err, v_msgError, v_msgSolution);
      }
      catch (final Exception v_err)
      {
         final String v_msgError = "Problème lors de l'instanciation de '" + p_Class.getName() + "'";
         final String v_msgSolution = "Vérifier le traitement du constructeur de la classe '" + p_Class.getName() + "'";
         throw new Spi4jRuntimeException(v_err, v_msgError, v_msgSolution);
      }
   }

   /**
    * Vérifie la cohérence de la base de données par rapport à la version de l'application Java.
    */
   public void checkDatabase ()
   {
      LogManager.getLogger(UserPersistence_Abs.class)
               .info("Vérification de la cohérence des tables de l'application " + _ParamPersistence.getIdAppli());

      begin();

      try
      {
         final StringBuilder v_sb = new StringBuilder();
         for (final Class<? extends Entity_Itf<?>> v_entity : _ParamPersistence.getAllEntities())
         {
            // l'entity NoEntity du defaultDao n'a pas de table
            if (v_entity != NoEntity.class)
            {
               final Dao_Itf<?, ?, ?> v_dao = this.getDao(v_entity);
               try
               {
                  DatabaseChecker.checkDatabaseTable((DaoJdbc_Abs<?, ?>) v_dao);
               }
               catch (final Exception v_ex)
               {
                  v_sb.append(v_ex.getMessage()).append('\n');
               }
            }
         }
         if (v_sb.length() > 0)
         {
            v_sb.insert(0,
                     "Les tables en base de données ne sont pas en cohérence avec cette version d'application :\n");
            throw new Spi4jRuntimeException(v_sb.toString(),
                     "Contactez l'administrateur pour vérifier la cohérence des tables dans la base de données");
         }
      }
      finally
      {
         rollback();
      }
   }

   /**
    * Permet d'indiquer aux DaoJdbc que les requêtes select formées doivent contenir le nom de toutes les colonnes au lieu de simplement *.<br />
    * Exemple :
    * <li>select PERSONNE.ID, PERSONNE.NOM, PERSONNE.PRENOM from PERSONNE</li><br />
    * au lieu de
    * <li>select PERSONNE.* from PERSONNE</li>
    */
   protected static void useColumnsInSelect ()
   {
      // Nom de la propriété système indiquant si les selects doivent contenir le nom de toutes les colonnes ou seulement * (par défaut en JDBC)
      // Attention, cette propriété est dupliquée dans DaoJdbc_Abs (là ou elle est lue)
      System.setProperty("jdbc.useColumnsInSelect", Boolean.toString(true));
   }
}
