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
import org.azkfw.munchkin.database.model.entity.TableObjectEntity;
import org.azkfw.munchkin.database.model.entity.TypeEntity;
import org.azkfw.munchkin.util.MunchkinUtil;
import org.azkfw.munchkin.util.SQLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Oracle データベースモデル
 * 
 * @author Kawakicchi
 */
public class OracleModel extends TemplateDatabaseModel {

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(OracleModel.class);

	/** DBAロール */
	private final boolean dbaRole;

	/**
	 * コンストラクタ
	 *
	 * @param connection コネクション情報
	 */
	public OracleModel(final Connection connection) {
		super(connection);

		dbaRole = isDBARole(connection);
	}

	@Override
	protected final void sqlGetDefaultSchema(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    SYS_CONTEXT('USERENV', 'SESSION_USER') AS name");
		builder.append("  , SYS_CONTEXT('USERENV', 'SESSION_USER') AS label");
		builder.append("FROM");
		builder.append("    DUAL");
	}

	@Override
	protected final void sqlGetSchemaList(final SQLBuilder builder) {
		if (dbaRole) {
			builder.append("SELECT");
			builder.append("    USERNAME AS name");
			builder.append("  , USERNAME AS label");
			builder.append("FROM");
			builder.append("    DBA_USERS");
			builder.append("ORDER BY");
			builder.append("    USERNAME");
		} else {
			builder.append("SELECT");
			builder.append("    USERNAME AS name");
			builder.append("  , USERNAME AS label");
			builder.append("FROM");
			builder.append("    USER_USERS");
			builder.append("ORDER BY");
			builder.append("    USERNAME");
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
		if (MunchkinUtil.isNull(type)) {

		} else if (MunchkinUtil.isEqualsAny(type.getName(), "TABLE", "VIEW")) {
			if (dbaRole) {
				builder.append("SELECT");
				builder.append("    A.OWNER         AS schema");
				builder.append("  , A.OBJECT_NAME   AS name");
				builder.append("  , B.COMMENTS      AS label");
				builder.append("  , A.OBJECT_TYPE   AS type");
				builder.append("  , A.OWNER         AS owner");
				builder.append("  , A.LAST_DDL_TIME AS \"UPDATE\"");
				builder.append("  , A.STATUS        AS \"STATUS\"");
				builder.append("FROM");
				builder.append("    DBA_OBJECTS      A");
				builder.append("  , DBA_TAB_COMMENTS B");
				builder.append("WHERE");
				builder.append("    A.OWNER       = B.OWNER");
				builder.append("AND A.OBJECT_NAME = B.TABLE_NAME");
				builder.append("AND A.OBJECT_TYPE = B.TABLE_TYPE");
				if (null != schema) {
					builder.append("AND A.OWNER       = ?", schema.getName());
				}
				builder.append("AND A.OBJECT_TYPE = ?", type.getName());
				builder.append("ORDER BY");
				builder.append("    A.OBJECT_NAME");
			} else {
				builder.append("SELECT");
				builder.append("    ''              AS schema");
				builder.append("  , A.OBJECT_NAME   AS name");
				builder.append("  , B.COMMENTS      AS label");
				builder.append("  , A.OBJECT_TYPE   AS type");
				builder.append("  , NULL            AS owner");
				builder.append("  , A.LAST_DDL_TIME AS \"UPDATE\"");
				builder.append("  , A.STATUS        AS \"STATUS\"");
				builder.append("FROM");
				builder.append("    USER_OBJECTS      A");
				builder.append("  , USER_TAB_COMMENTS B");
				builder.append("WHERE");
				builder.append("    A.OBJECT_NAME = B.TABLE_NAME");
				builder.append("AND A.OBJECT_TYPE = B.TABLE_TYPE");
				builder.append("AND A.OBJECT_TYPE = ?", type.getName());
				builder.append("ORDER BY");
				builder.append("    A.OBJECT_NAME");
			}
		} else if (MunchkinUtil.isEqualsAny(type.getName(), "SYNONYM", "FUNCTION", "PROCEDURE", "PACKAGE",
				"PACKAGE BODY", "SEQUENCE", "TRIGGER", "INDEX")) {
			if (dbaRole) {
				builder.append("SELECT");
				builder.append("    A.OWNER         AS schema");
				builder.append("  , A.OBJECT_NAME   AS name");
				builder.append("  , ''              AS label");
				builder.append("  , A.OBJECT_TYPE   AS type");
				builder.append("  , A.OWNER         AS owner");
				builder.append("  , A.LAST_DDL_TIME AS \"UPDATE\"");
				builder.append("  , A.STATUS        AS \"STATUS\"");
				builder.append("FROM");
				builder.append("    DBA_OBJECTS      A");
				builder.append("WHERE");
				builder.append("    A.OBJECT_TYPE = ?", type.getName());
				if (null != schema) {
					builder.append("AND A.OWNER       = ?", schema.getName());
				}
				builder.append("ORDER BY");
				builder.append("    A.OBJECT_NAME");
			} else {
				builder.append("SELECT");
				builder.append("    NULL            AS schema");
				builder.append("  , A.OBJECT_NAME   AS name");
				builder.append("  , ''              AS label");
				builder.append("  , A.OBJECT_TYPE   AS type");
				builder.append("  , NULL            AS owner");
				builder.append("  , A.LAST_DDL_TIME AS \"UPDATE\"");
				builder.append("  , A.STATUS        AS \"STATUS\"");
				builder.append("FROM");
				builder.append("    USER_OBJECTS      A");
				builder.append("WHERE");
				builder.append("    A.OBJECT_TYPE = ?", type.getName());
				builder.append("ORDER BY");
				builder.append("    A.OBJECT_NAME");
			}
		}
	}

	@Override
	protected final void sqlGetPrimaryKey(final SQLBuilder builder, final TableObjectEntity table) {
		if (dbaRole) {
			builder.append("SELECT");
			builder.append("    A.COLUMN_NAME AS name");
			builder.append("  , C.COMMENTS    AS LABEL");
			builder.append("FROM");
			builder.append("    DBA_CONS_COLUMNS A");
			builder.append("  , DBA_CONSTRAINTS  B");
			builder.append("  , DBA_COL_COMMENTS C");
			builder.append("WHERE");
			builder.append("    B.OWNER           = ?", table.getSchema());
			builder.append("AND B.TABLE_NAME      = ?", table.getName());
			builder.append("AND B.CONSTRAINT_TYPE = ?", "P");
			builder.append("AND A.OWNER           = B.OWNER");
			builder.append("AND A.CONSTRAINT_NAME = B.CONSTRAINT_NAME");
			builder.append("AND B.OWNER           = C.OWNER");
			builder.append("AND B.TABLE_NAME      = C.TABLE_NAME");
			builder.append("AND A.COLUMN_NAME     = C.COLUMN_NAME");
			builder.append("ORDER BY");
			builder.append("    A.POSITION");
		} else {
			builder.append("SELECT");
			builder.append("    A.COLUMN_NAME AS name");
			builder.append("  , C.COMMENTS    AS LABEL");
			builder.append("FROM");
			builder.append("    USER_CONS_COLUMNS A");
			builder.append("  , USER_CONSTRAINTS  B");
			builder.append("  , USER_COL_COMMENTS C");
			builder.append("WHERE");
			builder.append("    B.TABLE_NAME      = ?", table.getName());
			builder.append("AND B.CONSTRAINT_TYPE = ?", "P");
			builder.append("AND A.OWNER           = B.OWNER");
			builder.append("AND A.CONSTRAINT_NAME = B.CONSTRAINT_NAME");
			builder.append("AND B.TABLE_NAME      = C.TABLE_NAME");
			builder.append("AND A.COLUMN_NAME     = C.COLUMN_NAME");
			builder.append("ORDER BY");
			builder.append("    A.POSITION");
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
				builder.append("  , CASE A.CHAR_USED");
				builder.append("      WHEN 'B' THEN A.DATA_TYPE || '(' || A.DATA_LENGTH || ' BYTE)' ");
				builder.append("      WHEN 'C' THEN A.DATA_TYPE || '(' || A.CHAR_LENGTH || ' CHAR)' ");
				builder.append("      ELSE A.DATA_TYPE");
				builder.append("    END           AS \"データ型\"");
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
				builder.append("  , CASE A.CHAR_USED");
				builder.append("      WHEN 'B' THEN A.DATA_TYPE || '(' || A.DATA_LENGTH || ' BYTE)' ");
				builder.append("      WHEN 'C' THEN A.DATA_TYPE || '(' || A.CHAR_LENGTH || ' CHAR)' ");
				builder.append("      ELSE A.DATA_TYPE");
				builder.append("    END           AS \"データ型\"");
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
