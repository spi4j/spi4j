/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource.jdbc.driver;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;

import fr.spi4j.persistence.resource.ResourcePhysical_Abs;
import fr.spi4j.persistence.resource.jdbc.NonXAJdbcResourceManager_Abs;

/**
 * @author MINARM
 */
public class OracleNonXAResourceManager extends NonXAJdbcResourceManager_Abs
{
   @Override
   protected CommonDataSource createDataSource (final ResourcePhysical_Abs p_resourcePhysical) throws SQLException
   {
      final Map<String, Object> v_props = new HashMap<>();
      v_props.put("URL", p_resourcePhysical.getUrl());
      v_props.put("user", p_resourcePhysical.getUser());
      v_props.put("password", p_resourcePhysical.getPassword());
      return (DataSource) createDataSource("oracle.jdbc.pool.OracleDataSource", v_props);
   }
}
