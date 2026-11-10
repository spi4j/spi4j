/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.business.ServiceLogProxy;
import fr.spi4j.exception.Spi4jEntityNotFoundException;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.ParamPersistence_Abs;
import fr.spi4j.persistence.dao.Binary;
import fr.spi4j.persistence.dao.Cursor_Abs;
import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.persistence.dao.DatabaseType;
import fr.spi4j.persistence.dao.TableCriteria;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.persistence.resource.ResourceManager_Abs;
import fr.spi4j.persistence.resource.jdbc.JdbcResourceManager_Itf;
import fr.spi4j.type.XtopSup;

/**
 * Classe abstraite commune à tous les DAO JDBC.
 *
 * @author MINARM
 * @param <TypeEntity>   Type de l'entity
 * @param <Columns_Enum> Type de l'énumération des colonnes
 */
public abstract class DaoJdbc_Abs<TypeEntity extends Entity_Itf<Long>, Columns_Enum extends ColumnsNames_Itf>
		implements Dao_Itf<Long, TypeEntity, Columns_Enum> {

	private static final Logger c_log = LogManager.getLogger(DaoJdbc_Abs.class);

	/**
	 * Propriété indiquant si les selects doivent contenir le nom de toutes les
	 * colonnes ou seulement * (par défaut en JDBC)
	 * <p>
	 * Attention, cette propriété est dupliquée dans DaoJdbc_Abs (là ou elle est
	 * écrite)
	 */
	private static final boolean c_useColumnsInSelect = Boolean
			.parseBoolean(System.getProperty("jdbc.useColumnsInSelect", Boolean.toString(false)));

	/** Le gestionnaire de ressource associé au DAO. */
	private JdbcResourceManager_Itf _JdbcResourceManager;

	/** Le nom de la table. */
	private final String _tableName;

	/** Le nom des colonnes. */
	private final ColumnsNames_Itf[] _columnNames;

	/**
	 * Constructeur.
	 *
	 * @param p_tableName   (In) Nom de la table liée à ce DAO
	 * @param p_columnNames (In) Tableau de l'énumération des colonnes dans cette
	 *                      table
	 */
	public DaoJdbc_Abs(final String p_tableName, final ColumnsNames_Itf[] p_columnNames) {
		super();

		if (p_tableName == null) {
			throw new IllegalArgumentException(
					"Le paramètre 'tableName' est obligatoire, il ne peut pas être 'null'\n");
		}
		if (p_columnNames == null) {
			throw new IllegalArgumentException(
					"Le paramètre 'columnNames' est obligatoire, il ne peut pas être 'null'\n");
		}

		_tableName = p_tableName;
		_columnNames = p_columnNames;
	}

	/**
	 * Constructeur.
	 *
	 * @param p_tableName             (In) Nom de la table liée à ce DAO
	 * @param p_columnNames           (In) Tableau de l'énumération des colonnes
	 *                                dans cette table
	 * @param p_additionalColumnNames (In) Tableau de l'énumération des colonnes
	 *                                additionnelles dans cette table
	 */
	public DaoJdbc_Abs(final String p_tableName, final ColumnsNames_Itf[] p_columnNames,
			final ColumnsNames_Itf[] p_additionalColumnNames) {
		super();

		if (p_tableName == null) {
			throw new IllegalArgumentException(
					"Le paramètre 'tableName' est obligatoire, il ne peut pas être 'null'\n");
		}
		if (p_columnNames == null) {
			throw new IllegalArgumentException(
					"Le paramètre 'columnNames' est obligatoire, il ne peut pas être 'null'\n");
		}
		if (p_additionalColumnNames == null) {
			throw new IllegalArgumentException(
					"Le paramètre 'additionalColumnNames' est obligatoire, il ne peut pas être 'null'\n");
		}
		_tableName = p_tableName;

		final ColumnsNames_Itf[] v_columnNames = new ColumnsNames_Itf[p_columnNames.length
				+ p_additionalColumnNames.length];

		int v_iterator = 0;
		for (final ColumnsNames_Itf v_columnName : p_columnNames) {
			v_columnNames[v_iterator] = v_columnName;
			v_iterator++;
		}
		for (final ColumnsNames_Itf v_additionalColumnName : p_additionalColumnNames) {
			v_columnNames[v_iterator] = v_additionalColumnName;
			v_iterator++;
		}

		_columnNames = v_columnNames;

	}

	@Override
	public String getTableName() {
		return _tableName;
	}

	@Override
	public ColumnsNames_Itf getColumnId() {
		for (final ColumnsNames_Itf v_column : _columnNames) {
			if (v_column.isId()) {
				return v_column;
			}
		}

		throw new Spi4jRuntimeException("Aucune colonne 'PRIMARY KEY' n'a été trouvé dans la table " + _tableName,
				"Vérifier la modélisation de la table " + _tableName);
	}

	@Override
	public ResourceManager_Abs getResourceManager() {
		if (_JdbcResourceManager == null) {
			throw new Spi4jRuntimeException(null,
					"Aucun gestionnaire de ressource n'a été associé au DAO : " + this.getClass(), "???");
		}
		return (ResourceManager_Abs) _JdbcResourceManager;
	}

	@Override
	public void setResourceManager(final ResourceManager_Abs p_ResourceManager) {
		if (p_ResourceManager instanceof JdbcResourceManager_Itf) {
			_JdbcResourceManager = (JdbcResourceManager_Itf) p_ResourceManager;
		} else {
			throw new IllegalArgumentException("Le gestionnaire de ressource n'est pas compatible : ");
		}
	}

	/**
	 * Construit une Map des valeurs de l'entity, ayant les noms logiques des
	 * colonnes comme clés.
	 *
	 * @param p_Entity Entity
	 * @return Map
	 */
	abstract protected Map<String, Object> getMapValueByLogicalNameFromEntity(final TypeEntity p_Entity);

	/**
	 * Construit une instance d'entity correspondant à ce DAO à partir d'une Map des
	 * valeurs de l'entity, ayant les noms logiques des colonnes comme clés.
	 *
	 * @param p_map_valueByColumnName Map
	 * @return Map
	 */
	abstract protected TypeEntity getEntityFromMapValueByLogicalName(final Map<String, Object> p_map_valueByColumnName);

	@Override
	public void create(final TypeEntity p_Entity) throws Spi4jValidationException {
		// validation de l'entity passé en paramètre
		p_Entity.validate();

		final JdbcQueryBuilder<TypeEntity> v_JdbcQueryBuilder = createJdbcQueryBuilder();

		// mise à jour de la version pour création
		if (JdbcVersionHelper.isEntityVersionned(p_Entity)) {
			// on ne récupère pas l'ancienne valeur de version, car il n'y en a pas
			JdbcVersionHelper.updateVersionForEntity(p_Entity);
		}

		// Obtenir la Map de valeurs à partir du nom
		// la map contient donc la bonne version (car mise à jour juste au dessus)
		final Map<String, Object> v_map_valueByLogicalName;
		final Connection v_Connection = getCurrentConnection();
		final DatabaseType v_databaseType = DatabaseType.findTypeForConnection(v_Connection);
		final boolean v_entityHasId = p_Entity.getId() != null;
		final String v_insertQuery;

		if (v_entityHasId) {
			if (ParamPersistence_Abs.isInsertWithIdEnabled()) {
				v_map_valueByLogicalName = getMapValueByLogicalNameFromEntity(p_Entity);
				v_insertQuery = v_JdbcQueryBuilder.getInsertQueryWithoutSequence(v_databaseType);
			} else {
				throw new Spi4jRuntimeException("L'entité insérée possède déjà un identifiant.",
						"Vouliez-vous faire un update ? Voulez-vous forcer l'insertion d'entitées avec identifiant : ParamPersistence_Abs.enableInsertWithId(true)");
			}
		} else {
			v_map_valueByLogicalName = getMapValueByLogicalNameWithoutId(p_Entity);
			v_insertQuery = v_JdbcQueryBuilder.getInsertQuery(v_databaseType);
		}

		final JdbcStatementPreparator v_jdbcStatementPreparator = new JdbcStatementPreparator(v_insertQuery,
				v_map_valueByLogicalName);
		try {
			// préparation du statement
			final PreparedStatement v_Statement = v_jdbcStatementPreparator.prepareStatement(v_Connection, true);
			try {
				// exécution de la requête
				v_Statement.executeUpdate();
				if (!v_entityHasId) {
					final ResultSet v_ResultSet = v_Statement.getGeneratedKeys();
					try {
						// on se positionne sur la ligne retournée
						v_ResultSet.next();
						// pour récupérer l'id généré
						p_Entity.setId(v_ResultSet.getLong(1));
					} finally {
						// fermeture du résultat
						v_ResultSet.close();
					}
				}
			} finally {
				// fermeture du statement
				v_Statement.close();
			}
		} catch (final SQLException v_ex) {
			String v_message = "Problème lors du create avec p_Entity=" + p_Entity;
			if (v_databaseType == DatabaseType.ORACLE) {
				v_message = getMessageForOracle(v_ex, v_message);
			}
			throw new Spi4jRuntimeException(v_ex, v_message, "???");
		}
		// on n'encapsule pas de nouveau une Spi4jRuntimeException
		catch (final Spi4jRuntimeException v_ex) {
			throw v_ex;
		} catch (final Exception v_ex) {
			throw new Spi4jRuntimeException(v_ex, "Problème lors du create avec p_Entity=" + p_Entity, "???");
		}

	}

	/**
	 * Retourne la même chose que getMapValueByLogicalNameFromEntity, mais sans la
	 * valeur de l'id.
	 *
	 * @param p_Entity Entité
	 * @return Map
	 * @see #getMapValueByLogicalNameFromEntity
	 */
	private Map<String, Object> getMapValueByLogicalNameWithoutId(final TypeEntity p_Entity) {

		final Map<String, Object> v_map_valueByLogicalName = getMapValueByLogicalNameFromEntity(p_Entity);
		final JdbcQueryBuilder<TypeEntity> v_JdbcQueryBuilder = createJdbcQueryBuilder();
		final String v_pkColumnName = v_JdbcQueryBuilder.getPkColumnName();

		for (final ColumnsNames_Itf v_columnName : _columnNames) {
			if (v_pkColumnName.equalsIgnoreCase(v_columnName.getPhysicalColumnName())) {
				// on enlève de la map, le paramètre de la clé primaire
				v_map_valueByLogicalName.remove(v_columnName.getLogicalColumnName());
				break;
			}
		}

		return v_map_valueByLogicalName;

	}

	@Override
	public TypeEntity findById(final Long p_id) {
		if (p_id == null) {
			throw new Spi4jRuntimeException("La clé primaire de l'entité n'est pas renseigné", "???");
		}

		final JdbcQueryBuilder<TypeEntity> v_JdbcQueryBuilder = createJdbcQueryBuilder();
		final String v_pkColumnName = v_JdbcQueryBuilder.getPkColumnName();
		final List<TypeEntity> v_list = findByCriteria("where " + v_pkColumnName + " = :id",
				Collections.singletonMap("id", p_id));

		if (v_list.isEmpty()) {
			throw new Spi4jEntityNotFoundException(getTableName(), p_id);
		} else if (v_list.size() > 1) {
			throw new Spi4jRuntimeException("Plusieurs lignes trouvées.", "Vérifiez la clé primaire");
		}

		return v_list.get(0);
	}

	@Override
	public void update(final TypeEntity p_Entity) throws Spi4jValidationException {
		// validation de l'entity passé en paramètre
		p_Entity.validate();

		final JdbcQueryBuilder<TypeEntity> v_JdbcQueryBuilder = createJdbcQueryBuilder();

		// la requête qui va être construite
		final String v_updateQuery;
		// la map des paramètres de la requête
		final Map<String, Object> v_map_valueByLogicalName;

		// si l'entité est versionnée
		final boolean v_entityVersionned = JdbcVersionHelper.isEntityVersionned(p_Entity);
		if (v_entityVersionned) {
			// construction d'une requête avec contrainte sur la version
			v_updateQuery = v_JdbcQueryBuilder.getVersionnedUpdateQuery(p_Entity);
			// récupération de la version avant mise à jour et mise à jour de la nouvelle
			// version
			final Object v_oldLockVersion = JdbcVersionHelper.updateVersionForEntity(p_Entity);
			// construction de la map des paramètres
			v_map_valueByLogicalName = getMapValueByLogicalNameWithoutId(p_Entity);
			// test que la version de l'entité en cours est la bonne avec ajout du paramètre
			// de l'ancienne version
			v_map_valueByLogicalName.put(JdbcVersionHelper.c_requestParameter, v_oldLockVersion);
		} else {
			// construction de la map des paramètres
			v_map_valueByLogicalName = getMapValueByLogicalNameWithoutId(p_Entity);
			// sinon construction d'une requête update standard
			v_updateQuery = v_JdbcQueryBuilder.getUpdateQuery();
		}

		// ajout du paramètre pour l'id
		v_map_valueByLogicalName.put("id", p_Entity.getId());

		// exécute la mise à jour
		final int v_updated = executeUpdate(v_updateQuery, v_map_valueByLogicalName);
		if (v_updated == 0) {
			throwNoDataModified(p_Entity, v_entityVersionned);
		} else if (v_updated > 1) {
			// trop de lignes modifiées
			throw new Spi4jRuntimeException("Plusieurs lignes modifiées dans update.", "Vérifiez la clé primaire");
		}

	}

	@Override
	public void delete(final TypeEntity p_Entity) {
		final JdbcQueryBuilder<TypeEntity> v_JdbcQueryBuilder = createJdbcQueryBuilder();

		// la requête qui va être construite
		final String v_deleteQuery;
		// la map des paramètres de la requête
		final Map<String, Object> v_map_valueByLogicalName = new HashMap<>(2);
		v_map_valueByLogicalName.put("id", p_Entity.getId());

		final boolean v_entityVersionned = JdbcVersionHelper.isEntityVersionned(p_Entity);
		if (v_entityVersionned) {
			// si l'entité est versionnée
			v_deleteQuery = v_JdbcQueryBuilder.getVersionnedDeleteQuery(p_Entity);
			// récupération de la version courante
			final Object v_versionValue = JdbcVersionHelper.getVersionValue(p_Entity);
			// test que la version de l'entité en cours est la bonne
			v_map_valueByLogicalName.put(JdbcVersionHelper.c_requestParameter, v_versionValue);
		} else {
			// sinon construction d'une requête delete standard
			v_deleteQuery = v_JdbcQueryBuilder.getDeleteQuery();
		}

		// exécute la suppression
		final int v_deleted = executeUpdate(v_deleteQuery, v_map_valueByLogicalName);
		if (v_deleted == 0) {
			throwNoDataModified(p_Entity, v_entityVersionned);
		} else if (v_deleted > 1) {
			// trop de lignes supprimées
			throw new Spi4jRuntimeException("Plusieurs lignes supprimées dans delete.", "Vérifiez la clé primaire");
		}

	}

	/**
	 * Renvoie une erreur si aucune ligne n'a été mise à jour (delete / update),
	 * entité versionnée ou non
	 *
	 * @param p_Entity           l'entité
	 * @param p_entityVersionned true si l'entité est versionnée, false sinon
	 */
	private void throwNoDataModified(final TypeEntity p_Entity, final boolean p_entityVersionned) {

		if (p_entityVersionned) {
			// l'entité n'est plus dans la version connue
			final Long v_entityId = p_Entity.getId();
			final Object v_oldVersion = JdbcVersionHelper.getVersionValue(p_Entity);
			Object v_lastVersion;
			try {
				final TypeEntity v_oldEntity = findById(v_entityId);
				v_lastVersion = JdbcVersionHelper.getVersionValue(v_oldEntity);
			} catch (final Spi4jEntityNotFoundException v_e) {
				// donnée non trouvée, déjà supprimée
				v_lastVersion = "??";
			}
			throw JdbcVersionHelper.createExceptionForLock(p_Entity, v_oldVersion, v_lastVersion);
		} else {
			// l'entité n'est pas versionnée mais a été supprimée
			throw new Spi4jEntityNotFoundException(getTableName(), p_Entity.getId());
		}

	}

	@Override
	public int deleteAll() {
		final String v_deleteAllQuery = "delete from " + _tableName;
		return executeUpdate(v_deleteAllQuery, null);
	}

	@Override
	public int deleteByCriteria(final TableCriteria<Columns_Enum> p_tableCriteria) {
		if (p_tableCriteria == null) {
			throw new IllegalArgumentException(
					"Le paramètre 'tableCriteria' est obligatoire, il ne peut pas être 'null'\n");
		}

		final String v_description = p_tableCriteria.getDescription();
		if (!v_description.isEmpty()) {
			c_log.debug("Exécution de deleteByCriteria avec le critère : " + v_description);
		}

		final String v_criteriaSql = p_tableCriteria.getCriteriaSql();
		final Map<String, Object> v_mapValue = p_tableCriteria.getMapValue();
		return executeUpdate("delete from " + _tableName + v_criteriaSql, v_mapValue);

	}

	@Override
	public List<TypeEntity> findAll() {
		return findByCriteria("", null, -1, 0);
	}

	@Override
	public List<TypeEntity> findAll(final Columns_Enum p_orderByColumn) {
		if (p_orderByColumn == null) {
			throw new IllegalArgumentException(
					"Le paramètre 'orderByColumn' est obligatoire, il ne peut pas être 'null'\n");
		}
		return findByCriteria(" order by " + p_orderByColumn.getPhysicalColumnName(), null, -1, 0);
	}

	@Override
	public List<TypeEntity> findByColumn(final Columns_Enum p_column, final Object p_value) {
		if (p_column == null) {
			throw new IllegalArgumentException("Le paramètre 'column' est obligatoire, il ne peut pas être 'null'\n");
		}

		final Map<String, Object> v_map_valueByLogicalName;
		final String v_sqlCriteria;
		if (p_value == null) {
			v_map_valueByLogicalName = Collections.emptyMap();
			v_sqlCriteria = "where " + p_column.getPhysicalColumnName() + " is null";
		} else {
			v_map_valueByLogicalName = Collections.singletonMap(p_column.getLogicalColumnName(), p_value);
			if (p_value instanceof Collection) {
				v_sqlCriteria = "where " + p_column.getPhysicalColumnName() + " in :" + p_column.getLogicalColumnName();
			} else {
				v_sqlCriteria = "where " + p_column.getPhysicalColumnName() + " = :" + p_column.getLogicalColumnName();
			}
		}

		return findByCriteria(v_sqlCriteria, v_map_valueByLogicalName, -1, 0);
	}

	@Override
	public List<TypeEntity> findByCriteria(final String p_sqlCriteria,
			final Map<String, ? extends Object> p_map_value_by_name) {
		return findByCriteria(p_sqlCriteria, p_map_value_by_name, -1, 0);
	}

	@Override
	public List<TypeEntity> findByCriteria(final String p_sqlCriteria,
			final Map<String, ? extends Object> p_map_value_by_name, final int p_nbLignesMax,
			final int p_nbLignesStart) {

		if (p_sqlCriteria == null) {
			throw new IllegalArgumentException(
					"Le paramètre 'sqlCriteria' est obligatoire, il ne peut pas être 'null'\n");
		}
		final JdbcQueryBuilder<TypeEntity> v_JdbcQueryBuilder = createJdbcQueryBuilder();
		final String v_SelectQuery = v_JdbcQueryBuilder.getSelectQuery(buildSqlProjection()) + p_sqlCriteria;
		final Cursor_Abs v_cursor = executeQuery(v_SelectQuery, p_map_value_by_name, p_nbLignesMax, p_nbLignesStart);

		return getEntitiesFromCursor(v_cursor);

	}

	/**
	 * Retourne une liste d'entités à partir d'un Cursor.
	 * <p>
	 * Ce Cursor peut avoir été créé par la méthode
	 * {@link #findByCriteria(String, Map)} ou bien par la méthode
	 * {@link #executeQuery(String, Map, String...)}
	 * <p>
	 * Exemple : <code>
	 * final Cursor_Abs cursor = executeQuery(requeteSelect, mapParametres);
	 * final List<TypeEntity> result = getEntitiesFromCursor(cursor);
	 * </code>
	 *
	 * @param p_cursor le Cursor
	 * @return la liste des entités contenues dans le Cursor
	 */
	protected List<TypeEntity> getEntitiesFromCursor(final Cursor_Abs p_cursor) {
		final Map<String, Object> v_map_valueByLogicalName = new HashMap<>();
		final List<TypeEntity> v_result = new ArrayList<>();
		try {
			while (p_cursor.next()) {
				buildMapValueByLogicalNameFromCursor(p_cursor, v_map_valueByLogicalName);
				final TypeEntity v_entity = getEntityFromMapValueByLogicalName(v_map_valueByLogicalName);
				v_result.add(v_entity);
			}
		} finally {
			p_cursor.close();
		}

		return v_result;
	}

	/**
	 * Construit la map des valeurs pour la ligne lue dans le Curseur.
	 * <p>
	 * La map ne contiendra que les valeurs connues pour l'entité associée à ce DAO.
	 *
	 * @param p_cursor                 le curseur lu
	 * @param p_map_valueByLogicalName la map des valeurs à mettre à jour
	 */
	protected void buildMapValueByLogicalNameFromCursor(final Cursor_Abs p_cursor,
			final Map<String, Object> p_map_valueByLogicalName) {
		// optimisation : clear pas nécessaire puisqu'on redéfinit toutes les valeurs à
		// chaque ligne du curseur
		// et il est plus optimisé de ne pas faire clear() pour éviter de réinstancer à
		// chaque ligne autant de Map.Entry que de colonnes
		// v_map_valueByLogicalName.clear();

		for (final ColumnsNames_Itf v_column : _columnNames) {
			final Object v_value = getValueInRowForColumn(p_cursor, v_column);
			p_map_valueByLogicalName.put(v_column.getLogicalColumnName(), v_value);
		}

	}

	/**
	 * Construit la projection SQL de la requête
	 *
	 * @return la projection SQL (dans le select)
	 */
	private String buildSqlProjection() {
		// si la propriété System a été définie
		if (c_useColumnsInSelect) {
			// Construction de la projection de la requête
			final StringBuilder v_stringBuilder = new StringBuilder();
			for (final ColumnsNames_Itf v_columnName : _columnNames) {
				v_stringBuilder.append(_tableName).append('.').append(v_columnName.getPhysicalColumnName())
						.append(", ");
			}
			// si on a au moins une colonne, alors on retourne la liste des colonnes
			if (v_stringBuilder.length() >= 2) {
				v_stringBuilder.setLength(v_stringBuilder.length() - 2);
				return v_stringBuilder.toString();
			}
		}

		// sinon, par défaut, on utilise
		return _tableName + ".*";
	}

	/**
	 * Retourne la valeur d'une colonne dans le curseur en paramètre à la ligne
	 * courante de ce curseur.
	 *
	 * @param p_cursor Curseur
	 * @param p_column Colonne (ou alias) dans la requête select
	 * @return Valeur
	 */
	private static Object getValueInRowForColumn(final Cursor_Abs p_cursor, final ColumnsNames_Itf p_column) {

		final String v_physicalColumnName = p_column.getPhysicalColumnName();
		Object v_value = null;
		final Class<?> v_type = p_column.getTypeColumn();
		if (v_type == String.class) {
			v_value = p_cursor.getString(v_physicalColumnName);
		} else if (v_type == Boolean.class) {
			v_value = p_cursor.getBoolean(v_physicalColumnName);
		} else if (v_type == Long.class) {
			v_value = p_cursor.getLong(v_physicalColumnName);
		} else if (v_type == Integer.class) {
			v_value = p_cursor.getInteger(v_physicalColumnName);
		} else if (v_type == Float.class) {
			v_value = p_cursor.getFloat(v_physicalColumnName);
		} else if (v_type == Double.class) {
			v_value = p_cursor.getDouble(v_physicalColumnName);
		} else if (v_type == java.util.Date.class) {
			v_value = p_cursor.getDate(v_physicalColumnName);
		} else if (v_type == Timestamp.class) {
			v_value = p_cursor.getTimestamp(v_physicalColumnName);
		} else if (v_type == Time.class) {
			v_value = p_cursor.getTime(v_physicalColumnName);
		} else if (v_type == BigDecimal.class) {
			v_value = p_cursor.getBigDecimal(v_physicalColumnName);
		} else if (v_type == Byte.class) {
			v_value = p_cursor.getByte(v_physicalColumnName);
		} else if (v_type == Binary.class) {
			final Blob v_valueBlob = p_cursor.getBlob(v_physicalColumnName);
			if (v_valueBlob != null) {
				try {
					v_value = new Binary(v_valueBlob.getBinaryStream());
				} catch (final SQLException v_e) {
					// TODO Auto-generated catch block
				}
			}
		} else if (v_type == Blob.class) {
			v_value = p_cursor.getBlob(v_physicalColumnName);
		} else if (v_type == XtopSup.class) {
			v_value = new XtopSup(p_cursor.getString(v_physicalColumnName));
		} else {
			throw new Spi4jRuntimeException("Type inconnu: " + v_type,
					"Utilier un type connu comme type d'attribut dans l'entity");
		}

		return v_value;
	}

	@Override
	public List<TypeEntity> findByCriteria(final TableCriteria<Columns_Enum> p_tableCriteria) {
		if (p_tableCriteria == null) {
			throw new IllegalArgumentException(
					"Le paramètre 'tableCriteria' est obligatoire, il ne peut pas être 'null'\n");
		}

		final String v_description = p_tableCriteria.getDescription();
		if (!v_description.isEmpty()) {
			c_log.debug("Exécution de findByCriteria avec le critère : " + v_description);
		}

		final String v_criteriaSql = p_tableCriteria.getCriteriaSql();
		final Map<String, Object> v_mapValue = p_tableCriteria.getMapValue();
		final int v_nbLignesMax = p_tableCriteria.getNbLignesMax();
		final int v_nbLignesStart = p_tableCriteria.getNbLignesStart();

		return findByCriteria(v_criteriaSql, v_mapValue, v_nbLignesMax, v_nbLignesStart);
	}

	@Override
	public int executeUpdate(final String p_sqlQuery, final Map<String, ? extends Object> p_map_value_by_name,
			final String... p_commentaires) {
		if (p_sqlQuery == null) {
			throw new IllegalArgumentException("Le paramètre 'sqlQuery' est obligatoire, il ne peut pas être 'null'\n");
		}
		final int v_update;
		final Connection v_Connection = getCurrentConnection();

		c_log.debug("Exécution de executeUpdate");
		for (final String v_commentaire : p_commentaires) {
			c_log.debug(v_commentaire);
		}
		final JdbcStatementPreparator v_jdbcStatementPreparator = new JdbcStatementPreparator(p_sqlQuery,
				p_map_value_by_name);

		try {
			// préparation du statement
			final PreparedStatement v_Statement = v_jdbcStatementPreparator.prepareStatement(v_Connection, false);
			try {
				// exécution de l'update
				v_update = v_Statement.executeUpdate();
				// incrémente le compteur de reqûetes
				ServiceLogProxy.incrementRequestsCounterForCurrentThread();
			} finally {
				v_Statement.close();
			}
		} catch (final SQLException v_ex) {
			String v_message = "Erreur 'executeUpdate'";
			if (DatabaseType.findTypeForConnection(v_Connection) == DatabaseType.ORACLE) {
				v_message = getMessageForOracle(v_ex, v_message);
			}
			// on informe de la requête qui s'est mal déroulée
			throw new Spi4jRuntimeException(v_ex, v_message, "Vérifier la requête sql (p_sqlQuery=" + p_sqlQuery + ")\n"
					+ v_jdbcStatementPreparator.getFormattedSqlForException());
		}
		// on n'encapsule pas de nouveau une Spi4jRuntimeException
		catch (final Spi4jRuntimeException v_ex) {
			throw v_ex;
		} catch (final Exception v_ex) {
			throw new Spi4jRuntimeException(v_ex, "Erreur 'executeUpdate'", "Vérifier l'accès à la base de données");
		}
		return v_update;
	}

	/**
	 * Retourne des messages en français pour les utilisateurs et les développeurs
	 * de certaines erreurs courantes dans une base de données Oracle.
	 *
	 * @param p_exception      Exception SQL
	 * @param p_defaultMessage Message par défaut
	 * @return Message en français
	 */
	private String getMessageForOracle(final SQLException p_exception, final String p_defaultMessage) {
		final String v_result;
		final int v_errorCode = p_exception.getErrorCode();
		if (v_errorCode == 1438 || v_errorCode == 12899) {
			// Valeur trop grande pour ce champ
			v_result = "La valeur renseignée est trop grande pour être enregistrée.";
		} else if (v_errorCode == 2292) {
			// Violation de contrainte d'intégrité référentielle
			v_result = "L'élément est référencé, il ne peut pas être supprimé.";
		} else if (v_errorCode == 1) {
			// Violation de contrainte d'unicité sur un index unique
			v_result = "Cet élément a déjà été enregistré.";
		} else {
			v_result = p_defaultMessage;
		}
		return v_result;
	}

	@Override
	public Cursor_Abs executeQuery(final String p_sqlQuery, final Map<String, ? extends Object> p_map_value_by_name,
			final String... p_commentaires) {
		return executeQuery(p_sqlQuery, p_map_value_by_name, -1, 0, p_commentaires);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cursor_Abs executeQuery(final String p_sqlQuery, final Map<String, ? extends Object> p_map_value_by_name,
			final int p_nbLignesMax, final int p_nbLignesStart, final String... p_commentaires) {
		if (p_sqlQuery == null) {
			throw new IllegalArgumentException("Le paramètre 'sqlQuery' est obligatoire, il ne peut pas être 'null'\n");
		}
		final Connection v_Connection = getCurrentConnection();
		final DatabaseType v_databaseType = DatabaseType.findTypeForConnection(v_Connection);

		// initialisation d'une nouvelle map de paramètres
		final Map<String, Object> v_new_map_value_by_name;
		// tentative d'optimisation de la requête
		boolean v_optimizedQuery = false;
		String v_finalSqlQuery = p_sqlQuery;
		if ((p_nbLignesStart > 0 || p_nbLignesMax > 0) && v_databaseType != null) {
			if (p_map_value_by_name != null) {
				v_new_map_value_by_name = new HashMap<>(p_map_value_by_name);
			} else {
				v_new_map_value_by_name = new HashMap<>(2);
			}
			v_finalSqlQuery = v_databaseType.getLimitQuery(p_sqlQuery, p_nbLignesMax, p_nbLignesStart);
			v_optimizedQuery = v_databaseType.is_optimizedLimitQuery();
			if (v_finalSqlQuery.contains(" :" + DatabaseType.c_limitStartParameter)) {
				v_new_map_value_by_name.put(DatabaseType.c_limitStartParameter, p_nbLignesStart);
			}
			if (v_finalSqlQuery.contains(" :" + DatabaseType.c_limitMaxParameter)) {
				v_new_map_value_by_name.put(DatabaseType.c_limitMaxParameter, p_nbLignesMax);
			}
		} else {
			v_new_map_value_by_name = (Map<String, Object>) p_map_value_by_name;
		}

		if (v_databaseType == DatabaseType.ORACLE) {
			// mantis spi4j #1889 : il a été constaté un bug Oracle (sur un cas contenant un
			// rownum pour p_nbLignesMax et quand certaines colonnes sont sélectionnées)
			// certaines lignes ne sont parfois pas toutes retournées (base 10.2.0.3.0 et
			// driver jdbc 11.2.0.3.0, non reproduit avec Toad pour la même requête).
			// Il a été constaté que dans ce cas, l'ajout d'un espace à la fin (après le
			// rownum) permet de contourner le problème.
			v_finalSqlQuery = v_finalSqlQuery + ' ';
		}

		c_log.debug("Exécution de executeQuery");
		for (final String v_commentaire : p_commentaires) {
			c_log.debug(v_commentaire);
		}
		final JdbcStatementPreparator v_jdbcStatementPreparator = new JdbcStatementPreparator(v_finalSqlQuery,
				v_new_map_value_by_name);
		try {
			// préparation du statement
			final PreparedStatement v_Statement = v_jdbcStatementPreparator.prepareStatement(v_Connection, false);
			final ResultSet v_ResultSet = v_Statement.executeQuery();
			// si la requête a pu être optimisée
			final CursorSql v_cursorSql;
			if (v_optimizedQuery) {
				// on retourne tous les résultats de la requête dans le curseur
				v_cursorSql = new CursorSql(p_sqlQuery, v_ResultSet, -1, 0);
			} else {
				// si la requête n'a pas pu être optimisée, alors on il faut que le curseur se
				// positionne au bon endroit (temps important)
				v_cursorSql = new CursorSql(p_sqlQuery, v_ResultSet, p_nbLignesMax, p_nbLignesStart);
			}
			// incrémente le compteur de reqûetes
			ServiceLogProxy.incrementRequestsCounterForCurrentThread();
			// retourne le curseur
			return v_cursorSql;
		} catch (final Exception v_ex) {
			throw new Spi4jRuntimeException(v_ex, "Erreur 'executeQuery'", "Vérifier la requête sql (p_sqlQuery="
					+ p_sqlQuery + ")\n" + v_jdbcStatementPreparator.getFormattedSqlForException());
		}
	}

	/**
	 * Permet d'obtenir la connexion JDBC du thread courant (non null).
	 * <p>
	 * La ressource est automatiquement associée à la transaction du thread local.
	 * <p>
	 * Cette méthode lance une exception avec un message explicatif pour
	 * l'utilisateur si la connexion jdbc n'a pas pu être établi.
	 *
	 * @return La connection JDBC
	 */
	private Connection getCurrentConnection() {
		try {
			return _JdbcResourceManager.getCurrentConnection();
		} catch (final Exception v_ex) {
			// pas Spi4jRuntimeException ici pour avoir un message clair pour les
			// utilisateurs dans le cas où la base de données n'est pas accessible
			throw new RuntimeException(v_ex.getMessage(), v_ex);
		}
	}

	/**
	 * Crée une instance de JdbcQueryBuilder correspondant à la table et aux
	 * colonnes de ce DAO.
	 *
	 * @return JdbcQueryBuilder
	 */
	private JdbcQueryBuilder<TypeEntity> createJdbcQueryBuilder() {
		return new JdbcQueryBuilder<>(_tableName, _columnNames);
	}

	/**
	 * Vérifie la cohérence d'une table en base de données par rapport au classes
	 * Java.
	 *
	 * @throws SQLException e
	 */
	void checkDatabaseTable() throws SQLException {
		final Connection v_Connection = getCurrentConnection();
		DatabaseChecker.checkDatabaseTable(v_Connection, _tableName, _columnNames);
	}
}
