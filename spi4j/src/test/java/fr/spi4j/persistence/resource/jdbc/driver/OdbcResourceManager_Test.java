/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource.jdbc.driver;

import java.sql.SQLException;

import fr.spi4j.persistence.resource.DefaultResourcePhysical;
import fr.spi4j.persistence.resource.ResourceType_Enum;

/**
 * Test unitaire de la classe OdbcResourceManager.
 *
 * <pre>
 * Java a inclus un pilote de pont JDBC/ODBC comme solution de transition pour accéder aux sources de données ODBC,
 * mais il a toujours été considéré comme un pilote très limité et la recommandation a toujours été d'utiliser un
 * pilote JDBC à la place. À partir de Java 8,Oracle ne prend plus en charge le pont JDBC-ODBC. Oracle recommande
 * d'utiliser les pilotes JDBC fournis par le fournisseur de la base de données au lieu du pont JDBC-ODBC.
 *
 * Conservé temporairement, le test est cependant désactivé pour le momment.
 * </pre>
 *
 * @author MINARM
 */
public class OdbcResourceManager_Test
{

   /**
    * Test.
    * @throws SQLException
    *            e
    */
   // @Test
   public void testOdbc () throws SQLException
   {
      new OdbcResourceManager().createDataSource(
               new DefaultResourcePhysical("db_AppWhite", "aw", "aw", ResourceType_Enum.ressourceOdbc_v));
   }
}
