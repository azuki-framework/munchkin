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
package org.azkfw.munchkin.database.model;

import java.sql.Connection;

import org.azkfw.munchkin.util.MunchkinUtil;

/**
 *
 * @author Kawakicchi
 *
 */
public class OracleModel extends TemplateDatabaseModel {

	public OracleModel(final Connection connection) {
		super(connection);

	}

	@Override
	protected final void sqlGetDefaultSchema(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    '' AS name");
		builder.append("  , '' AS label");
	}

	@Override
	protected final void sqlGetSchemaList(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    username as name");
		builder.append("  , username as label");
		builder.append("FROM");
		builder.append("    dba_users");
		builder.append("ORDER BY");
		builder.append("    username");
	}

	@Override
	protected final void sqlGetTypeList(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    name  AS name");
		builder.append("  , label AS label");
		builder.append("FROM");
		builder.append("(");
		builder.append("            SELECT  1 AS order, 'TABLE'        AS name, 'テーブル'     AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  2 AS order, 'VIEW'         AS name, 'ビュー'       AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  3 AS order, 'SYNONYM'      AS name, 'シノニム'      AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  4 AS order, 'FUNCTION'     AS name, 'ファンクション'  AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  5 AS order, 'PROCEDURE'    AS name, 'プロシージャ'  AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  6 AS order, 'PACKAGE'      AS name, 'パッケージ'    AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  7 AS order, 'PACKAGE BODY' AS name, 'パッケージ本体' AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  8 AS order, 'SEQUENCE'     AS name, 'シーケンス'    AS label FROM DUAL");
		builder.append("  UNION ALL SELECT  9 AS order, 'TRIGGER'      AS name, 'トリガー'     AS label FROM DUAL");
		builder.append("  UNION ALL SELECT 10 AS order, 'INDEX'        AS name, 'インデックス'  AS label FROM DUAL");
		builder.append(") AS t");
		builder.append("ORDER BY");
		builder.append("    order");
	}

	@Override
	protected final void sqlGetObjectList(final SQLBuilder builder, final SchemaEntity schema, final TypeEntity type) {
		builder.append("SELECT");
		builder.append("    OWNER       AS schema");
		builder.append("  , OBJECT_NAME AS name");
		builder.append("  , ''          AS label");
		builder.append("  , OBJECT_TYPE AS type");
		builder.append("  , OWNER       AS owner");
		builder.append("  , LAST_DDL_TIME AS \"UPDATE\"");
		builder.append("  , STATUS        AS \"STATUS\"");
		builder.append("FROM");
		builder.append("    DBA_OBJECTS");
		builder.append("WHERE");
		builder.append("    OWNER       = ?", schema.getName());
		builder.append("AND OBJECT_TYPE = ?", type.getName());
		builder.append("ORDER BY");
		builder.append("    OBJECT_NAME");
	}

	@Override
	protected void sqlGetObjectDetail(final SQLBuilder builder, final ObjectEntity object) {
		if (MunchkinUtil.isNull(object)) {

		} else if (MunchkinUtil.isEquals(object.getType(), "TABLE")) {
			builder.append("SELECT");
			builder.append("    COLUMN_NAME AS \"列名\"");
			builder.append("  , DATA_TYPE   AS \"データ型\"");
			builder.append("  , DATA_LENGTH as LENGTH");
			builder.append("  , DECODE(NULLABLE, 'N', '○', '') as NOTNULL");
			builder.append("FROM");
			builder.append("    DBA_TAB_COLUMNS");
			builder.append("WHERE");
			builder.append("    OWNER = ?", object.getSchema());
			builder.append("AND TABLE_NAME = ?", object.getName());
			builder.append("ORDER BY");
			builder.append("    COLUMN_ID");
		} else {

		}
	}
}
