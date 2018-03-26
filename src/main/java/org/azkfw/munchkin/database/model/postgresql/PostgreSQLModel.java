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
package org.azkfw.munchkin.database.model.postgresql;

import java.sql.Connection;

import org.azkfw.munchkin.database.model.TemplateDatabaseModel;
import org.azkfw.munchkin.database.model.entity.ObjectEntity;
import org.azkfw.munchkin.database.model.entity.SchemaEntity;
import org.azkfw.munchkin.database.model.entity.TypeEntity;
import org.azkfw.munchkin.util.MunchkinUtil;
import org.azkfw.munchkin.util.SQLBuilder;

/**
 *
 * @author Kawakicchi
 */
public class PostgreSQLModel extends TemplateDatabaseModel {

	public PostgreSQLModel(final Connection connection) {
		super(connection);

	}

	@Override
	protected final void sqlGetDefaultSchema(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    current_schema() AS name");
		builder.append("  , current_schema() AS label");
	}

	@Override
	protected final void sqlGetSchemaList(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    nspname as name");
		builder.append("  , nspname as label");
		builder.append("FROM");
		builder.append("    pg_catalog.pg_namespace");
		builder.append("ORDER BY");
		builder.append("    nspname");
	}

	@Override
	protected final void sqlGetTypeList(final SQLBuilder builder) {
		builder.append("SELECT");
		builder.append("    name  AS name");
		builder.append("  , label AS label");
		builder.append("FROM");
		builder.append("(");
		builder.append("            SELECT 1 AS \"order\", 'TABLE' AS name, 'テーブル' AS label");
		builder.append("  UNION ALL SELECT 2 AS \"order\", 'VIEW'  AS name, 'ビュー'  AS label");
		builder.append(") AS t");
		builder.append("ORDER BY");
		builder.append("    \"order\"");
	}

	@Override
	protected final void sqlGetObjectList(final SQLBuilder builder, final SchemaEntity schema, final TypeEntity type) {
		if (MunchkinUtil.isNull(type)) {

		} else if (MunchkinUtil.isEquals(type.getName(), "TABLE")) {
			builder.append("SELECT");
			builder.append("    A.schemaname  AS schema");
			builder.append("  , A.relname     AS name");
			builder.append("  , B.description AS label");
			builder.append("  , 'table'       AS type");
			builder.append("  , ''            AS owner");
			builder.append("FROM");
			builder.append("    pg_stat_all_tables A");
			// builder.append("    pg_stat_user_tables a");
			builder.append("    LEFT OUTER JOIN");
			builder.append("        pg_description B");
			builder.append("    ON");
			builder.append("        A.relid    = B.objoid");
			builder.append("    AND B.objsubid = 0");
			if (MunchkinUtil.isNotNull(schema)) {
				builder.append("WHERE");
				builder.append("    A.schemaname = ?", schema.getName());
			}
			builder.append("ORDER BY");
			builder.append("    A.relname");
		} else {

		}
	}

	@Override
	protected void sqlGetObjectDetail(final SQLBuilder builder, final ObjectEntity object) {
		if (MunchkinUtil.isNull(object)) {

		} else if (MunchkinUtil.isEquals(object.getType(), "TABLE")) {
			builder.append("SELECT");
			//builder.append("    tbl.relname     AS \"テーブル名\"");
			//builder.append("  , pd.description  AS \"テーブルコメント\"");
			builder.append("    colname         AS \"列名\"");
			builder.append("  , tbl.description AS \"コメント\"");
			builder.append("  , CASE");
			builder.append("        WHEN size IS NULL");
			builder.append("            THEN datatype");
			builder.append("            ELSE datatype || ' (' || size || ')'");
			builder.append("    END AS \"データ型\"");
			builder.append("  , CASE");
			builder.append("        WHEN \"notnull\" IS NOT NULL AND \"default\" IS NOT NULL");
			builder.append("            THEN \"notnull\" || ' ' || \"default\"");
			builder.append("        ELSE COALESCE(\"notnull\", \"default\")");
			builder.append("    END AS \"属性\"");
			builder.append("FROM");
			builder.append("    (");

			builder.append("        SELECT");
			builder.append("            cls.oid");
			builder.append("          , cls.relname");
			builder.append("          , attr.attnum  AS idx");
			builder.append("          , attr.attname AS colname");
			builder.append("          , CASE typ.typname");
			builder.append("                WHEN 'int2'   THEN 'SMALLINT'");
			builder.append("                WHEN 'int4'   THEN 'INT'");
			builder.append("                WHEN 'int8'   THEN 'BIGINT'");
			builder.append("                WHEN 'float4' THEN 'REAL'");
			builder.append("                WHEN 'float8' THEN 'DOUBLE'");
			builder.append("                WHEN 'bpchar' THEN 'CHAR'");
			builder.append("                ELSE UPPER(typ.typname)");
			builder.append("            END AS datatype");
			builder.append("          , CASE WHEN attr.atttypmod > 0 THEN");
			builder.append("                CASE typ.typname");
			builder.append("                    WHEN 'numeric'");
			builder.append("                        THEN (attr.atttypmod - 4) / 65536");
			builder.append("                    WHEN 'decimal'");
			builder.append("                        THEN (attr.atttypmod - 4) / 65536");
			builder.append("                    WHEN 'date'");
			builder.append("                        THEN COALESCE(attr.atttypmod - 4, 10)");
			builder.append("                    WHEN 'time'");
			builder.append("                        THEN COALESCE(attr.atttypmod - 4, 8)");
			builder.append("                    WHEN 'timestamp'");
			builder.append("                        THEN COALESCE(attr.atttypmod - 4, 19)");
			builder.append("                    ELSE attr.atttypmod - 4");
			builder.append("                END");
			builder.append("            END AS size");
			builder.append("          , CASE attr.attnotnull");
			builder.append("                WHEN TRUE THEN 'NOT NULL'");
			builder.append("                ELSE NULL");
			builder.append("            END AS notnull");
			builder.append("          , CASE attr.atthasdef");
			builder.append("                WHEN TRUE THEN 'DEFAULT ' || adef.adsrc");
			builder.append("                ELSE NULL");
			builder.append("            END AS default");
			builder.append("          , pd.description");
			builder.append("        FROM");
			builder.append("            pg_class cls");
			builder.append("            INNER JOIN");
			builder.append("                pg_attribute attr");
			builder.append("            ON");
			builder.append("                cls.oid = attr.attrelid");
			builder.append("            INNER JOIN");
			builder.append("                pg_type typ");
			builder.append("            ON");
			builder.append("                attr.atttypid = typ.oid");
			builder.append("            LEFT JOIN");
			builder.append("                pg_description pd");
			builder.append("            ON");
			builder.append("                pd.objoid   = cls.oid");
			builder.append("            AND pd.objoid   = attr.attrelid");
			builder.append("            AND pd.objsubid = attr.attnum");
			builder.append("            LEFT OUTER JOIN");
			builder.append("                pg_attrdef adef");
			builder.append("            ON");
			builder.append("                cls.oid = adef.adrelid");
			builder.append("            AND attr.attnum = adef.adnum");
			builder.append("        WHERE");
			builder.append("            cls.oid IN (");
			builder.append("                SELECT");
			builder.append("                    cls.oid");
			builder.append("                FROM");
			builder.append("                    pg_class cls");
			builder.append("                    INNER JOIN");
			builder.append("                        pg_namespace nsp");
			builder.append("                    ON");
			builder.append("                        cls.relnamespace = nsp.oid");
			builder.append("                    INNER JOIN");
			builder.append("                        pg_user usr");
			builder.append("                    ON");
			builder.append("                        cls.relowner = usr.usesysid");
			builder.append("                WHERE");
			builder.append("                    cls.relkind = 'r'");
			builder.append("                AND nsp.nspname = ?", object.getSchema());
			builder.append("                ORDER BY");
			builder.append("                    nsp.nspname");
			builder.append("                  , usr.usename");
			builder.append("                  , cls.relname");
			builder.append("                ");
			builder.append("            )");
			builder.append("        AND attr.attnum >= 0");
			builder.append("        AND attr.attisdropped IS NOT TRUE");
			builder.append("        AND typ.typisdefined");
			builder.append("        AND cls.relname = ?", object.getName());

			builder.append("    ) AS tbl");
			builder.append("    LEFT JOIN");
			builder.append("        pg_description pd");
			builder.append("    ON  tbl.oid     = pd.objoid");
			builder.append("    AND pd.objsubid = 0");
			builder.append("ORDER BY");
			builder.append("    oid");
			builder.append("  , idx");

		} else {

		}
	}

}
