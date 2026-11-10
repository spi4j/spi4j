/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource.jdbc.driver;

import java.sql.SQLException;

import javax.sql.CommonDataSource;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.resource.ResourcePhysical_Abs;
import fr.spi4j.persistence.resource.jdbc.NonXAJdbcResourceManager_Abs;
import fr.spi4j.persistence.resource.jdbc.NonXAJndiResourcePhysical;

/**
 * @author MINARM
 */
public class NonXADatasourceManager extends NonXAJdbcResourceManager_Abs
{
   @Override
   protected CommonDataSource createDataSource (final ResourcePhysical_Abs p_resourcePhysical) throws SQLException
   {
      if (p_resourcePhysical instanceof NonXAJndiResourcePhysical)
      {
         final NonXAJndiResourcePhysical v_jndiResourcePhysical = (NonXAJndiResourcePhysical) p_resourcePhysical;
         final CommonDataSource v_ds = v_jndiResourcePhysical.getDataSource();
         return v_ds;
      }
      else
      {
         throw new Spi4jRuntimeException("La ressource pour ce type de ressource devrait être une ressource JNDI", "");
      }
   }
}
