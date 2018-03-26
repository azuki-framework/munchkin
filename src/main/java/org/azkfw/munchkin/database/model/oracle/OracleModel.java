/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.azkfw.munchkin.database.model.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.azkfw.munchkin.database.model.TemplateDatabaseModel;
import org.azkfw.munchkin.database.model.entity.ObjectEntity;
import org.azkfw.munchkin.database.model.entity.SchemaEntity;
import org.azkfw.munchkin.database.model.entity.TypeEntity;
import org.azkfw.munchkin.util.MunchkinUtil;
import org.azkfw.munchkin.util.SQLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kawakicchi
 *
 */
public class OracleModel extends TemplateDatabaseModel {

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(OracleModel.class);

	private final boolean dbaRole;

	public OracleModel(final Connection connection) {
		super(connection);

		dbaRole = isDBARole(connection);
	}

	@Override
	protected final void sqlGetDefaultSchema(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    sys_context('userenv', 'session_user') AS name");
		builder.append("  , sys_context('userenv', 'session_user') AS label");
		builder.append("FROM");
		builder.append("    DUAL");
	}

	@Override
	protected final void sqlGetSchemaList(final SQLBuilder builder) {
		if (dbaRole) {
			builder.append("SELECT");
			builder.append("    username AS name");
			builder.append("  , username AS label");
			builder.append("FROM");
			builder.append("    dba_users");
			builder.append("ORDER BY");
			builder.append("    username");
		} else {
			builder.append("SELECT");
			builder.append("    username AS name");
			builder.append("  , username AS label");
			builder.append("FROM");
			builder.append("    user_users");
			builder.append("ORDER BY");
			builder.append("    username");
		}
	}

	@Override
	protected final void sqlGetTypeList(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    name  AS name");
		builder.append("  , label AS label");
		builder.append("FROM");
		builder.append("(");
		builder.append("            SELECT  1 AS \"order\", 'TABLE'        AS name, 'テーブル'     AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  2 AS \"order\", 'VIEW'         AS name, 'ビュー'       AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  3 AS \"order\", 'SYNONYM'      AS name, 'シノニム'      AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  4 AS \"order\", 'FUNCTION'     AS name, 'ファンクション'  AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  5 AS \"order\", 'PROCEDURE'    AS name, 'プロシージャ'  AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  6 AS \"order\", 'PACKAGE'      AS name, 'パッケージ'    AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  7 AS \"order\", 'PACKAGE BODY' AS name, 'パッケージ本体' AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  8 AS \"order\", 'SEQUENCE'     AS name, 'シーケンス'    AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  9 AS \"order\", 'TRIGGER'      AS name, 'トリガー'     AS label FROM DUAL");
		builder.append("  UNION ALL SELECT 10 AS \"order\", 'INDEX'        AS name, 'インデックス'  AS label FROM DUAL");
		builder.append(") t");
		builder.append("ORDER BY");
		builder.append("    \"order\"");
	}

	@Override
	protected final void sqlGetObjectList(final SQLBuilder builder, final SchemaEntity schema, final TypeEntity type) {
		if (dbaRole) {
			builder.append("SELECT");
			builder.append("    OWNER         AS schema");
			builder.append("  , OBJECT_NAME   AS name");
			builder.append("  , ''            AS label");
			builder.append("  , OBJECT_TYPE   AS type");
			builder.append("  , OWNER         AS owner");
			builder.append("  , LAST_DDL_TIME AS \"UPDATE\"");
			builder.append("  , STATUS        AS \"STATUS\"");
			builder.append("FROM");
			builder.append("    DBA_OBJECTS");
			builder.append("WHERE");
			builder.append("    OWNER       = ?", schema.getName());
			builder.append("AND OBJECT_TYPE = ?", type.getName());
			builder.append("ORDER BY");
			builder.append("    OBJECT_NAME");
		} else {
			builder.append("SELECT");
			builder.append("    ?             AS schema", schema.getName());
			builder.append("  , OBJECT_NAME   AS name");
			builder.append("  , ''            AS label");
			builder.append("  , OBJECT_TYPE   AS type");
			builder.append("  , ?             AS owner", schema.getName());
			builder.append("  , LAST_DDL_TIME AS \"UPDATE\"");
			builder.append("  , STATUS        AS \"STATUS\"");
			builder.append("FROM");
			builder.append("    USER_OBJECTS");
			builder.append("WHERE");
			builder.append("    OBJECT_TYPE = ?", type.getName());
			builder.append("ORDER BY");
			builder.append("    OBJECT_NAME");
		}
	}

	@Override
	protected void sqlGetObjectDetail(final SQLBuilder builder, final ObjectEntity object) {
		if (MunchkinUtil.isNull(object)) {

		} else if (MunchkinUtil.isEquals(object.getType(), "TABLE")) {
			if (dbaRole) {
				builder.append("SELECT");
				builder.append("    A.COLUMN_NAME AS \"列名\"");
				builder.append("  , B.COMMENTS    AS \"ラベル\"");
				builder.append("  , A.DATA_TYPE   AS \"データ型\"");
				builder.append("  , A.DATA_LENGTH AS LENGTH");
				builder.append("  , DECODE(A.NULLABLE, 'N', '○', '') AS NOTNULL");
				builder.append("FROM");
				builder.append("    DBA_TAB_COLUMNS  A");
				builder.append("  , DBA_COL_COMMENTS B");
				builder.append("WHERE");
				builder.append("    A.OWNER       = ?", object.getSchema());
				builder.append("AND A.TABLE_NAME  = ?", object.getName());
				builder.append("AND A.OWNER       = B.OWNER");
				builder.append("AND A.TABLE_NAME  = B.TABLE_NAME");
				builder.append("AND A.COLUMN_NAME = B.COLUMN_NAME");
				builder.append("ORDER BY");
				builder.append("    A.COLUMN_ID");
			} else {
				builder.append("SELECT");
				builder.append("    A.COLUMN_NAME AS \"列名\"");
				builder.append("  , B.COMMENTS    AS \"ラベル\"");
				builder.append("  , A.DATA_TYPE   AS \"データ型\"");
				builder.append("  , A.DATA_LENGTH AS LENGTH");
				builder.append("  , DECODE(A.NULLABLE, 'N', '○', '') AS NOTNULL");
				builder.append("FROM");
				builder.append("    USER_TAB_COLUMNS  A");
				builder.append("  , USER_COL_COMMENTS B");
				builder.append("WHERE");
				builder.append("    A.TABLE_NAME   = ?", object.getName());
				builder.append(" AND A.TABLE_NAME  = B.TABLE_NAME");
				builder.append(" AND A.COLUMN_NAME = B.COLUMN_NAME");
				builder.append("ORDER BY");
				builder.append("    A.COLUMN_ID");
			}
		} else {

		}
	}

	private boolean isDBARole(final Connection c) {
		boolean result = false;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			final StringBuffer sql = new StringBuffer();
			sql.append("SELECT");
			sql.append("    count(*) AS CNT");
			sql.append(" FROM");
			sql.append("    USER_ROLE_PRIVS");
			sql.append(" WHERE");
			sql.append("     USERNAME     = sys_context('userenv', 'session_user')");
			sql.append(" AND GRANTED_ROLE = 'DBA'");

			ps = getConnection().prepareStatement(sql.toString());

			rs = ps.executeQuery();

			if (rs.next()) {
				final int cnt = rs.getInt(1);
				if (0 < cnt) {
					result = true;
				}
			}
		} catch (SQLException ex) {
			LOGGER.error("DBA user check error.", ex);
		} finally {
			release(rs);
			release(ps);
		}
		return result;
	}
}
