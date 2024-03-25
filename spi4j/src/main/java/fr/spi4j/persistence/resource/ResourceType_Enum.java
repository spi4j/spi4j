/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.resource;

/**
 * Type des resources base de données (Oracle, MySQL ...).
 * <p>
 * Puisque ces types de ressources ne configurent pas les pools de connexions,
 * il est indispensable en production d'utiliser une dataSource du serveur
 * d'application dans JNDI et d'utiliser datasourceNonXA ou datasourceXA.
 *
 * @author MINARM
 */
public enum ResourceType_Enum {
	/** L'identifiant du gestionnaire de ressource Oracle 10i. */
	ressourceOracleXA_v10i("Oracle_v10i"),
	/** L'identifiant du gestionnaire de ressource Oracle 11i. */
	ressourceOracleXA_v11i("Oracle_v11i"),
	/** L'identifiant du gestionnaire de ressource Mysql. */
	ressourceMySqlXA_v5("MySql_v5"),
	/** L'identifiant du gestionnaire de ressource Derby. */
	ressourceDerbyXA_v("Derby_v?"),
	/** L'identifiant du gestionnaire de ressource H2. */
	ressourceH2XA_v("H2_v?"),
	/** L'identifiant du gestionnaire de ressource ODBC. */
	ressourceOdbc_v("Odbc_v?"),
	/** Datasource. */
	datasourceXA("Datasource"),

	/** L'identifiant du gestionnaire de ressource Oracle 11i. */
	ressourceOracleNonXA("OracleNonXA"),
	/** L'identifiant du gestionnaire de ressource Mysql. */
	ressourceMySqlNonXA("MySqlNonXA"),

	// Remarque : pour se connecter à PostgreSQL, utiliser le type de ressource
	// datasourceNonXA et une datasource dans l'annuaire JNDI du serveur
	// ou bien ressourceDbcpNonXA en s'assurant que les dépendances à commons-dbcp
	// et au driver jdbc postgresql sont présentes dans le classpath
	// (car on ne peut paramétrer une datasource du driver Postgresql avec une
	// simple url jdbc)

	/**
	 * L'identifiant du gestionnaire de ressource Apache Commons DBCP, sans
	 * configuration particulière du pool (qui peut convenir pour tout type de base
	 * de données connectée via une url, un user et un password et dont le driver
	 * jdbc est dans le classpath).
	 */
	ressourceDbcpNonXA("DbcpNonXA"),
	/** L'identifiant du gestionnaire de ressource H2. */
	ressourceH2NonXA("H2NonXA"),
	/** Datasource non XA. */
	datasourceNonXA("DatasourceNonXA");

	/** L'identifiant du gestionnaire de ressource. */
	private String _rmId;

	/**
	 * Constructeur.
	 *
	 * @param p_rmId String
	 */
	private ResourceType_Enum(final String p_rmId) {
		_rmId = p_rmId;
	}

	/**
	 * @return rmId
	 */
	public String getRmId() {
		return _rmId;
	}

	/**
	 * fromRmId.
	 *
	 * @param p_rmId String
	 * @return ResourceType_Enum
	 */
	public static ResourceType_Enum fromRmId(final String p_rmId) {
		// méthode utilisée dans spi4j-transaction-xa
		ResourceType_Enum v_return = null;
		for (final ResourceType_Enum v_ResourceType_Enum : ResourceType_Enum.values()) {
			if (p_rmId.equals(v_ResourceType_Enum._rmId)) {
				v_return = v_ResourceType_Enum;
			}
		}
		return v_return;
	}

	@Override
	public String toString() {
		return _rmId;
	}
}
