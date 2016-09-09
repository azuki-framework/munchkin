package org.azkwf.munchkin.model;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbunit.database.DefaultMetadataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kawakicchi
 */
public class OracleResolvedSynonymMetaDataHandler extends DefaultMetadataHandler {

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(OracleResolvedSynonymMetaDataHandler.class);

	protected static class ResolvedSynonym {
		private String owner;

		private String tableName;

		public ResolvedSynonym() {
		}

		public ResolvedSynonym(String owner, String tableName) {
			this.setOwner(owner);
			this.setTableName(tableName);
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
	}

	private PreparedStatement cachedUserObjectsStatement;

	private PreparedStatement cachedAllSynonymsStatement;

	public PreparedStatement getCachedUserObjectsStatement() {
		return cachedUserObjectsStatement;
	}

	public void setCachedUserObjectsStatement(PreparedStatement cachedUserObjectsStatement) {
		this.cachedUserObjectsStatement = cachedUserObjectsStatement;
	}

	public PreparedStatement getCachedAllSynonymsStatement() {
		return cachedAllSynonymsStatement;
	}

	public void setCachedAllSynonymsStatement(PreparedStatement cachedAllSynonymsStatement) {
		this.cachedAllSynonymsStatement = cachedAllSynonymsStatement;
	}

	@Override
	public ResultSet getColumns(DatabaseMetaData databaseMetaData, String schemaName, String tableName) throws SQLException {

		this.setCachedUserObjectsStatement(databaseMetaData.getConnection().prepareStatement("SELECT object_type FROM user_objects WHERE object_name LIKE ?"));

		this.setCachedAllSynonymsStatement(databaseMetaData.getConnection().prepareStatement(
				"SELECT table_owner, table_name FROM all_synonyms WHERE owner LIKE ? AND synonym_name LIKE ?"));

		ResolvedSynonym resolvedSynonym = this.resolveSynonym(schemaName, tableName);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("    NULL        AS table_cat");
		sql.append("  , ?           AS table_schem");
		sql.append("  , ?           AS table_name");
		sql.append("  , column_name AS column_name");
		sql.append("  , DECODE (");
		sql.append("        data_type");
		sql.append("      , 'CHAR'          ,    1");
		sql.append("      , 'VARCHAR2'      ,   12");
		sql.append("      , 'NUMBER'        ,    3");
		sql.append("      , 'LONG'          ,   -1");
		sql.append("      , 'DATE'          ,   93");
		sql.append("      , 'RAW'           ,   -3");
		sql.append("      , 'LONG RAW'      ,   -4");
		sql.append("      , 'BLOB'          , 2004");
		sql.append("      , 'CLOB'          , 2005");
		sql.append("      , 'BFILE'         ,  -13");
		sql.append("      , 'FLOAT'         ,    6");
		sql.append("      , 'TIMESTAMP(6)'  ,   93");
		sql.append("      , 'TIMESTAMP(6) WITH TIME ZONE'       , -101");
		sql.append("      , 'TIMESTAMP(6) WITH LOCAL TIME ZONE' , -102");
		sql.append("      , 'INTERVAL YEAR(2) TO MONTH'         , -103");
		sql.append("      , 'INTERVAL DAY(2) TO SECOND(6)'      , -104");
		sql.append("      , 'BINARY_FLOAT'  ,  100");
		sql.append("      , 'BINARY_DOUBLE' ,  101");
		sql.append("      , 1111");
		sql.append("    )           AS data_type");
		sql.append("  , data_type   AS type_name");
		sql.append("  , DECODE (");
		sql.append("        data_precision");
		sql.append("      , NULL, DECODE (");
		sql.append("            data_type");
		sql.append("          , 'CHAR'     , char_length");
		sql.append("          , 'VARCHAR'  , char_length");
		sql.append("          , 'VARCHAR2' , char_length");
		sql.append("          , 'NVARCHAR2', char_length");
		sql.append("          , 'NCHAR'    , char_length");
		sql.append("          , data_length");
		sql.append("        )");
		sql.append("      , data_precision");
		sql.append("    )              AS column_size");
		sql.append("  , 0              AS buffer_length");
		sql.append("  , data_scale     AS decimal_digits");
		sql.append("  , 10             AS num_prec_radix");
		sql.append("  , DECODE (nullable, 'N', 0, 1) AS nullable");
		sql.append("  , NULL           AS remarks");
		sql.append("  , data_default   AS column_def");
		sql.append("  , 0              AS sql_data_type");
		sql.append("  , 0              AS sql_datetime_sub");
		sql.append("  , data_length    AS char_octet_length");
		sql.append("  , column_id      AS ordinal_position");
		sql.append("  , DECODE (nullable, 'N', 'NO', 'YES') AS is_nullable");
		sql.append(" FROM");
		sql.append("    all_tab_columns");
		sql.append(" WHERE");
		sql.append("     owner      LIKE ?");
		sql.append(" AND table_name LIKE ?");

		PreparedStatement statement = databaseMetaData.getConnection().prepareStatement(sql.toString());

		statement.clearParameters();
		statement.setString(1, schemaName);
		statement.setString(2, tableName);
		statement.setString(3, resolvedSynonym.getOwner());
		statement.setString(4, resolvedSynonym.getTableName());

		logger.debug("getColumns({}, {}, {}, {})", schemaName, tableName, resolvedSynonym.getOwner(), resolvedSynonym.getTableName());

		return statement.executeQuery();
	}

	protected boolean isSynonym(String objectName) throws SQLException {
		PreparedStatement statement = this.getCachedUserObjectsStatement();
		statement.clearParameters();
		statement.setString(1, objectName);
		ResultSet resultSet = statement.executeQuery();

		if (!resultSet.next()) {
			return false;
		}

		String objectType = resultSet.getString("OBJECT_TYPE");

		if (objectType == null) {
			throw new IllegalStateException(String.format("Target object type is unknown. objectName: %s", objectName));
		}

		return objectType.equalsIgnoreCase("SYNONYM");
	}

	protected ResolvedSynonym resolveSynonym(String schemaName, String objectName) throws SQLException {
		if (!this.isSynonym(objectName)) {
			return new ResolvedSynonym(schemaName, objectName);
		}

		PreparedStatement statement = this.getCachedAllSynonymsStatement();
		statement.clearParameters();
		statement.setString(1, schemaName);
		statement.setString(2, objectName);
		ResultSet resultSet = statement.executeQuery();

		if (!resultSet.next()) {
			return new ResolvedSynonym(schemaName, objectName);
		}

		String resolvedSchemaName = resultSet.getString("TABLE_OWNER");
		String resolvedObjectName = resultSet.getString("TABLE_NAME");

		return this.resolveSynonym(resolvedSchemaName, resolvedObjectName);
	}
}