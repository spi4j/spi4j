/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource.jdbc;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.CommonDataSource;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.persistence.resource.ResourcePhysical_Abs;
import fr.spi4j.persistence.resource.ResourceType_Enum;

/**
 * Resource physique d'accès à une base de données.
 * @author MINARM
 */
public class NonXAJndiResourcePhysical extends ResourcePhysical_Abs
{

   /** Le nom jndi du DataSource. */
   private final String _jndiName;

   private final CommonDataSource _dataSource;

   /**
    * Constructeur max : avec tous les attributs. Exemple :<code><pre>
    *    ResourcePhysical v_ResourcePhysical = new ResourcePhysical(...);
    * </pre></code>
    * @param p_jndiName
    *           (In)(*) Le nom jndi du DataSource.
    * @throws NamingException
    *            le nom JNDI n'a pas été trouvé
    */
   public NonXAJndiResourcePhysical (final String p_jndiName) throws NamingException
   {
      // Initialiser la filiation
      super(ResourceType_Enum.datasourceNonXA);
      _jndiName = p_jndiName;
      LogManager.getLogger(getClass()).info("Lookup de la datasource JNDI " + _jndiName);
      final Object v_lookup = new InitialContext().lookup(_jndiName);
      if (v_lookup instanceof CommonDataSource)
      {
         _dataSource = (CommonDataSource) v_lookup;
      }
      else
      {
         throw new NamingException("La ressource de nom \"" + p_jndiName + "\" n'est pas une CommonDataSource : "
                  + v_lookup.getClass().getName());
      }
   }

   /**
    * Permet d'obtenir le nom jndi du DataSource.
    * @return Le nom jndi du DataSource.
    */
   public final String getJndiName ()
   {
      return _jndiName;
   }

   /**
    * @return la datasource associée à ce nom JNDI
    */
   public CommonDataSource getDataSource ()
   {
      return _dataSource;
   }

   @Override
   public String getUrl ()
   {
      return _jndiName;
   }

   @Override
   public String getUser ()
   {
      throw new UnsupportedOperationException(
               "Récupérer le user ou le password n'a pas de sens pour une datasource JNDI");
   }

   @Override
   public String getPassword ()
   {
      throw new UnsupportedOperationException(
               "Récupérer le user ou le password n'a pas de sens pour une datasource JNDI");
   }

   /**
    * Permet d'obtenir une chaîne représentant tous les attributs du bean. Exemple :<code><pre>
    *    ResourcePhysical v_ResourcePhysical = new ResourcePhysical(...);
    *    // ...
    *    System.out.println(v_ResourcePhysical);
    * </pre></code>
    * @return La chaîne représentant tous les attributs du bean.
    */
   @Override
   public String toString ()
   {
      return getClass().getName() + "[_jndiName=" + _jndiName + ']';
   }
}
