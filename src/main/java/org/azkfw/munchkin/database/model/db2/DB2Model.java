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
package org.azkfw.munchkin.database.model.db2;

import java.sql.Connection;

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
 * DB2 データベースモデル
 *
 * @author Kawakicchi
 */
public class DB2Model extends TemplateDatabaseModel {

	/** logger */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(DB2Model.class);

	/**
	 * コンストラクタ
	 *
	 * @param connection コネクション情報
	 */
	public DB2Model(final Connection connection) {
		super(connection);
	}

	@Override
	protected final void sqlGetDefaultSchema(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    TRIM(CURRENT SCHEMA) AS name");
		builder.append("  , TRIM(CURRENT SCHEMA) AS label");
		builder.append("FROM");
		builder.append("    SYSIBM.SYSDUMMY1");
	}

	@Override
	protected final void sqlGetSchemaList(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("DISTINCT");
		builder.append("    TRIM(CREATOR) AS name");
		builder.append("  , TRIM(CREATOR) AS label");
		builder.append("FROM");
		builder.append("    SYSIBM.SYSTABLES");
		builder.append("ORDER BY");
		builder.append("    TRIM(CREATOR)");
	}

	@Override
	protected final void sqlGetTypeList(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    name  AS name");
		builder.append("  , label AS label");
		builder.append("FROM");
		builder.append("(");
		builder.append("            SELECT 1 AS \"order\", 'TABLE' AS name, 'テーブル' AS label FROM SYSIBM.SYSDUMMY1");
		builder.append("  UNION ALL SELECT 2 AS \"order\", 'VIEW'  AS name, 'ビュー'  AS label FROM SYSIBM.SYSDUMMY1");
		builder.append(") AS t");
		builder.append("ORDER BY");
		builder.append("    \"order\"");
	}

	@Override
	protected final void sqlGetObjectList(final SQLBuilder builder, final SchemaEntity schema, final TypeEntity type) {
		if (MunchkinUtil.isNull(type)) {

		} else if (MunchkinUtil.isEqualsAny(type.getName(), "TABLE", "VIEW")) {
			builder.append("SELECT");
			builder.append("    TRIM(CREATOR) AS schema");
			builder.append("  , TRIM(NAME)    AS name");
			if (MunchkinUtil.isEquals(type.getName(), "VIEW")) {
				builder.append("  , 'VIEW' AS type");
			} else {
				builder.append("  , 'TABLE' AS type");
			}
			builder.append("  , TRIM(REMARKS) AS label");
			builder.append("FROM");
			builder.append("    SYSIBM.SYSTABLES");
			builder.append("WHERE");
			if (MunchkinUtil.isEquals(type.getName(), "VIEW")) {
				builder.append("    TYPE    = ?", "V");
			} else {
				builder.append("    TYPE    = ?", "T");
			}
			if (null != schema) {
				builder.append("AND CREATOR = ?", schema.getName());
			}
		} else {

		}
	}

	@Override
	protected final void sqlGetPrimaryKey(final SQLBuilder builder, final TableObjectEntity table) {
		builder.append("SELECT");
		builder.append("    TRIM(A.COLNAME) AS name");
		builder.append("  , TRIM(B.REMARKS) AS label");
		builder.append("FROM");
		builder.append("    SYSCAT.KEYCOLUSE  A");
		builder.append("  , SYSIBM.SYSCOLUMNS B");
		builder.append("WHERE");
		builder.append("    A.TABSCHEMA = ?");
		builder.append("AND A.TABNAME   = ?");
		builder.append("AND A.TABSCHEMA = B.TBCREATOR");
		builder.append("AND A.TABNAME   = B.TABNAME");
		builder.append("AND A.COLNAME   = B.NAME");
		builder.append("ORDER BY");
		builder.append("    A.COLSEQ");
	}

	@Override
	protected void sqlGetObjectDetail(final SQLBuilder builder, final ObjectEntity object) {
		if (MunchkinUtil.isNull(object)) {

		} else if (MunchkinUtil.isEquals(object.getType(), "TABLE")) {
			builder.append("SELECT");
			builder.append("    TRIM(NAME)    AS \"列名\"");
			builder.append("  , TRIM(REMARKS) AS \"コメント\"");
			builder.append("  , TRIM(COLTYPE) AS \"タイプ\"");
			builder.append("  , TRIM(NULLS)   AS \"NULLABLE\"");
			builder.append("FROM");
			builder.append("    SYSIBM.SYSCOLUMNS");
			builder.append("WHERE");
			builder.append("    TBCREATOR = ?", object.getSchema());
			builder.append("AND TBNAME    = ?", object.getName());
			builder.append("ORDER BY");
			builder.append("    COLNO");

		} else {

		}
	}

}
