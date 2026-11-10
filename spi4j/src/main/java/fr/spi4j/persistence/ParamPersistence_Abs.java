/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.persistence.transaction.NonXAGlobalUserTransaction;
import jakarta.transaction.UserTransaction;

/**
 * Classe abstraite permettant de centraliser tout le paramétrage de la persistance.
 * @author MINARM
 */
public abstract class ParamPersistence_Abs
{

   /** Possibilité d'insérer des entités avec un identifiant en dur. */
   private static boolean canInsertWithId; // = false;

   /** Les instances 'ParamPersistence' de chaque application. */
   private static final Map<String, ParamPersistence_Abs> c_map_ParamPersistence = new HashMap<>();

   /** Les instances 'UserPersistence' de chaque application. */
   private static final Map<String, UserPersistence_Abs> c_map_UserPersistence = new HashMap<>();

   /** Map de Map avec idAppli donne '_map_ElemParamPersistence' de chaque application. */
   private static final Map<String, Map<Class<? extends Entity_Itf<?>>, ElemParamPersistence>> c_map_allElemParamPersistence = new HashMap<>();

   /** L'identifiant de l'application. */
   private final String _idAppli;

   /** Le 'UserTransaction' de l'application. */
   private final UserTransaction _UserTransaction;

   /**
    * Constructeur protégé.
    * @param p_idAppli
    *           l'id de l'application
    */
   protected ParamPersistence_Abs (final String p_idAppli)
   {
      this(p_idAppli, new NonXAGlobalUserTransaction());
   }

   /**
    * Constructeur protégé.
    * @param p_idAppli
    *           l'id de l'application
    * @param p_userTransaction
    *           User transaction qui pourrait être différente de NonXAGlobalUserTransaction (par exemple : UserTransactionImpl en mode XA)
    */
   protected ParamPersistence_Abs (final String p_idAppli, final UserTransaction p_userTransaction)
   {
      super();
      // Le paramètre 'p_idAppli' de type 'String' est obligatoire
      checkMandatoryIdAppli(p_idAppli);
      if (p_userTransaction == null)
      {
         throw new IllegalArgumentException(
                  "Le paramètre 'p_userTransaction' de type 'UserTransaction' est obligatoire");
      }

      _idAppli = p_idAppli;
      _UserTransaction = p_userTransaction;

      // Les instances de 'ElemParamPersistence' mémorisées suivant l'alias de l'application
      c_map_allElemParamPersistence.put(p_idAppli, new HashMap<Class<? extends Entity_Itf<?>>, ElemParamPersistence>());
   }

   /**
    * Retourne l'id de l'application.
    * @return l'id de l'application
    */
   public final String getIdAppli ()
   {
      return _idAppli;
   }

   /**
    * Retourne le UserTransaction.
    * @return le UserTransaction
    */
   public final UserTransaction getUserTransaction ()
   {
      return _UserTransaction;
   }

   /**
    * Vérifie que le paramètre idAppli est non null et lance une exception si null.
    * @param p_idAppli
    *           String
    */
   private static void checkMandatoryIdAppli (final String p_idAppli)
   {
      if (p_idAppli == null)
      {
         throw new IllegalArgumentException("Le paramètre 'p_idAppli' de type 'String' est obligatoire");
      }
   }

   /**
    * Ajouter une instance représentant les informations de persistance associé à un alias donné.
    * @param p_alias
    *           (In)(*) L'alias représentant l'entité et le DAO (ex : ParamPersistenceGen_Abs.c_PersonneAlias).
    * @param p_ElemParamPersistence
    *           L'instance à ajouter.
    */
   protected void addElemParamPersistence (final Class<? extends Entity_Itf<?>> p_alias,
            final ElemParamPersistence p_ElemParamPersistence)
   {
      // Le paramètre 'p_alias' de type 'String' est obligatoire
      if (p_alias == null)
      {
         throw new IllegalArgumentException("Le paramètre 'p_alias' de type 'String' est obligatoire");
      }
      final Map<Class<? extends Entity_Itf<?>>, ElemParamPersistence> v_map_elemParamPersistence = getMapElemParamPersistenceForApplication();
      v_map_elemParamPersistence.put(p_alias, p_ElemParamPersistence);
   }

   /**
    * Modifie le gestionnaire de persistance par défaut.
    * @param <TypeId>
    *           le type d'identifiant géré par le DAO (Long pour JDBC)
    * @param p_daoClass
    *           la classe d'implémentation du DAO par défaut
    * @param p_ElemResourceManager
    *           le nouveau gestionnaire de persistance par défaut
    */
   protected <TypeId> void setDefaultDao (
            final Class<? extends Dao_Itf<TypeId, ? extends Entity_Itf<TypeId>, ? extends ColumnsNames_Itf>> p_daoClass,
            final ElemResourceManager p_ElemResourceManager)
   {
      final Map<Class<? extends Entity_Itf<?>>, ElemParamPersistence> v_map_elemParamPersistence = getMapElemParamPersistenceForApplication();
      v_map_elemParamPersistence.put(NoEntity.class, new ElemParamPersistence(NoEntity.class, p_daoClass,
               p_ElemResourceManager));
   }

   /**
    * Obtenir l'instance représentant les informations de persistance à partir d'un alias donné.
    * @param p_alias
    *           (In)(*) L'alias représentant l'entité et le DAO (ex : ParamPersistenceGen_Abs.c_PersonneAlias).
    * @return L'instance désirée.
    */
   protected ElemParamPersistence getElemParamPersistence (final Class<? extends Entity_Itf<?>> p_alias)
   {
      // Le paramètre 'p_alias' de type 'String' est obligatoire
      if (p_alias == null)
      {
         throw new IllegalArgumentException("Le paramètre 'p_alias' de type 'String' est obligatoire");
      }
      // Obtenir l'instance dans la 'Map'
      final Map<Class<? extends Entity_Itf<?>>, ElemParamPersistence> v_map_elemParamPersistence = getMapElemParamPersistenceForApplication();
      final ElemParamPersistence v_elemParamPersistence = v_map_elemParamPersistence.get(p_alias);
      if (v_elemParamPersistence == null)
      {
         throw new Spi4jRuntimeException("Aucun paramétrage de persistance défini pour cet alias d'entité (p_alias="
                  + p_alias + ")", "Ajouter le paramétrage de la persistence pour l'alias d'entité '" + p_alias
                  + "' dans la classe ParamPersistenceApp");
      }
      return v_elemParamPersistence;
   }

   /**
    * @return Toutes les classes d'interfaces des entitées.
    */
   protected Set<Class<? extends Entity_Itf<?>>> getAllEntities ()
   {
      return getMapElemParamPersistenceForApplication().keySet();
   }

   /**
    * Retourne la map des ElemParamPersistence pour l'application liée à cette instance de ParamPersistence.
    * @return Map
    */
   private Map<Class<? extends Entity_Itf<?>>, ElemParamPersistence> getMapElemParamPersistenceForApplication ()
   {
      return c_map_allElemParamPersistence.get(getIdAppli());
   }

   /**
    * Obtenir l'instance du Singleton.
    * @param p_idAppli
    *           (In)(*) L'identifiant de l'application est obligatoire.
    * @return L'instance du Singleton.
    */
   protected static ParamPersistence_Abs getParamPersistence (final String p_idAppli)
   {
      checkMandatoryIdAppli(p_idAppli);
      // Obtenir le 'ParamPersistence' à partir de son identifiant applicatif
      return c_map_ParamPersistence.get(p_idAppli);
   }

   /**
    * Permet d'affecter pour mémoriser l'instance du Singleton.
    * @param p_idAppli
    *           (In)(*) L'identifiant de l'application est obligatoire.
    * @param p_paramPersistence
    *           (In) Pour mémoriser l'instance du Singleton.
    * @see #getParamPersistence(String)
    */
   protected static final void setParamPersistence (final String p_idAppli,
            final ParamPersistence_Abs p_paramPersistence)
   {
      checkMandatoryIdAppli(p_idAppli);
      // Affecter le paramètre sans vérifier la valeur
      c_map_ParamPersistence.put(p_idAppli, p_paramPersistence);
   }

   /**
    * Obtenir l'instance du Singleton.
    * @param p_idAppli
    *           (In)(*) L'identifiant de l'application est obligatoire.
    * @return L'instance du Singleton.
    */
   protected static UserPersistence_Abs getUserPersistence (final String p_idAppli)
   {
      checkMandatoryIdAppli(p_idAppli);
      // Obtenir le 'UserPersistence' à partir de son identifiant applicatif
      return c_map_UserPersistence.get(p_idAppli);
   }

   /**
    * Permet d'affecter pour mémoriser l'instance du Singleton.
    * @param p_idAppli
    *           (In)(*) L'identifiant de l'application est obligatoire.
    * @param p_userPersistence
    *           (In) Pour mémoriser l'instance du Singleton.
    * @see #getUserPersistence(String)
    */
   protected static final void setUserPersistence (final String p_idAppli, final UserPersistence_Abs p_userPersistence)
   {
      checkMandatoryIdAppli(p_idAppli);
      // Affecter le paramètre sans vérifier la valeur
      c_map_UserPersistence.put(p_idAppli, p_userPersistence);
   }

   /** Initilisation du paramétrage de la persistance. */
   protected abstract void initElemParamPersistence ();

   /**
    * Effectue des actions après chargement de la configuration de persistance.
    */
   protected abstract void afterInit ();

   /**
    * Autoriser l'insertion d'entités possédant un identifiant (faux par défaut).
    * @param p_enableInsertWithId
    *           état
    */
   public static void enableInsertWithId (final boolean p_enableInsertWithId)
   {
      LogManager.getLogger(ParamPersistence_Abs.class)
               .debug("Autorisation de l'insert avec id : " + p_enableInsertWithId);
      canInsertWithId = p_enableInsertWithId;
   }

   /**
    * Retourne true si l'insertion d'entités possédant un identifiant est autorisée (faux par défaut).
    * @return état
    */
   public static boolean isInsertWithIdEnabled ()
   {
      return canInsertWithId;
   }

}
