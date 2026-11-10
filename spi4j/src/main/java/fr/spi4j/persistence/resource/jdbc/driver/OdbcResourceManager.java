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
 *
 *         <pre>
 * Java a inclus un pilote de pont JDBC/ODBC comme solution de transition pour accéder aux sources de données ODBC,
 * mais il a toujours été considéré comme un pilote très limité et la recommandation a toujours été d'utiliser un
 * pilote JDBC à la place. À partir de Java 8,Oracle ne prend plus en charge le pont JDBC-ODBC. Oracle recommande
 * d'utiliser les pilotes JDBC fournis par le fournisseur de la base de données au lieu du pont JDBC-ODBC.
 *
 * Conservé temporairement, le test est cependant désactivé pour le momment.
 *         </pre>
 */
public class OdbcResourceManager extends NonXAJdbcResourceManager_Abs
{
   @Override
   protected CommonDataSource createDataSource (final ResourcePhysical_Abs p_ResourcePhysical) throws SQLException
   {
      final String v_url = p_ResourcePhysical.getUrl();
      final String v_user = p_ResourcePhysical.getUser();
      final String v_password = p_ResourcePhysical.getPassword();

      final Map<String, Object> v_props = new HashMap<>();
      v_props.put("databaseName", v_url);
      v_props.put("user", v_user);
      v_props.put("password", v_password);
      final DataSource v_ds = (DataSource) createDataSource("sun.jdbc.odbc.ee.DataSource", v_props);
      return v_ds;
   }
}
