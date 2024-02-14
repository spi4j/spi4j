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
public class H2NonXAResourceManager extends NonXAJdbcResourceManager_Abs
{
   @Override
   protected CommonDataSource createDataSource (final ResourcePhysical_Abs p_resourcePhysical) throws SQLException
   {
      final Map<String, Object> v_props = new HashMap<>();
      v_props.put("user", p_resourcePhysical.getUser());
      v_props.put("password", p_resourcePhysical.getPassword());
      v_props.put("URL", p_resourcePhysical.getUrl());
      // cette classe de dataSource est compatible pour utilisation en tant que DataSource et en tant que XADataSource
      return (DataSource) createDataSource("org.h2.jdbcx.JdbcDataSource", v_props);
   }
}
