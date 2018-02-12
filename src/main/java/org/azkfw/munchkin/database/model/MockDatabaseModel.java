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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kawakicchi
 *
 */
public class MockDatabaseModel implements DatabaseModel {

	private final SchemaEntity defaultSchema;
	private final List<SchemaEntity> schemaList;
	private final List<TypeEntity> typeList;

	public MockDatabaseModel() {
		defaultSchema = new SchemaEntity("USER2", "ユーザ２");

		schemaList = new ArrayList<SchemaEntity>();
		schemaList.add(new SchemaEntity("USER1", "ユーザ１"));
		schemaList.add(new SchemaEntity("USER2", "ユーザ２"));
		schemaList.add(new SchemaEntity("USER3", "ユーザ３"));

		typeList = new ArrayList<TypeEntity>();
		typeList.add(new TypeEntity("TABLE", "テーブル"));
		typeList.add(new TypeEntity("VIEW", "ビュー"));
	}

	@Override
	public boolean execute(final String sql) throws SQLException {
		return execute(sql, null);
	}

	@Override
	public boolean execute(final String sql, final List<Object> params) throws SQLException {
		return false;
	}

	@Override
	public int executeUpdate(final String sql) throws SQLException {
		return executeUpdate(sql, null);
	}

	@Override
	public int executeUpdate(final String sql, final List<Object> params) throws SQLException {
		return -1;
	}

	@Override
	public List<Map<String, Object>> executeQuery(final String sql) throws SQLException {
		return executeQuery(sql, null);
	}

	@Override
	public List<Map<String, Object>> executeQuery(final String sql, final List<Object> params) throws SQLException {
		return new ArrayList<Map<String, Object>>();
	}

	@Override
	public SchemaEntity getDefaultSchema() {
		return defaultSchema;
	}

	@Override
	public List<SchemaEntity> getSchemaList() {
		return new ArrayList<SchemaEntity>(schemaList);
	}

	@Override
	public List<TypeEntity> getTypeList() {
		return new ArrayList<TypeEntity>(typeList);
	}

	@Override
	public List<ObjectEntity> getObjectList(final SchemaEntity schema, final TypeEntity type) {
		final List<ObjectEntity> objectList = new ArrayList<ObjectEntity>();

		for (int i = 0; i < 100; i++) {
			final ObjectEntity object = new ObjectEntity("Schema", "Object" + (i + 1), "オブジェクト" + (i + 1), "タイプ"
					+ (i + 1));
			objectList.add(object);
		}

		return objectList;
	}

	@Override
	public ObjectDetailEntity getObjectDetail(final ObjectEntity object) throws SQLException {
		return null;
	}

}
