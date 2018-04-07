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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.azkfw.munchkin.database.model.entity.ColumnEntity;
import org.azkfw.munchkin.database.model.entity.ObjectDetailEntity;
import org.azkfw.munchkin.database.model.entity.ObjectEntity;
import org.azkfw.munchkin.database.model.entity.SchemaEntity;
import org.azkfw.munchkin.database.model.entity.TableObjectEntity;
import org.azkfw.munchkin.database.model.entity.TypeEntity;
import org.azkfw.munchkin.util.MunchkinUtil;
import org.azkfw.munchkin.util.SQLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Kawakicchi
 */
public abstract class TemplateDatabaseModel extends AbstractDatabaseModel {

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateDatabaseModel.class);

	/**
	 * コンストラクタ
	 *
	 * @param connection コネクション
	 */
	public TemplateDatabaseModel(final Connection connection) {
		super(connection);
	}

	/**
	 * <p>
	 * 以下の項目をSELECT句に指定すること。
	 * <ul>
	 * <li>name - スキーマ名(必須)</li>
	 * <li>label - ラベル</li>
	 * </ul>
	 * </p>
	 * 
	 * @param builder
	 */
	protected abstract void sqlGetDefaultSchema(SQLBuilder builder);

	@Override
	public final SchemaEntity getDefaultSchema() throws SQLException {
		SchemaEntity schema = null;

		final MySQLBuilder builder = new MySQLBuilder();
		sqlGetDefaultSchema(builder);

		final String sql = builder.getSQL();
		final List<Object> params = builder.getParams();

		if (MunchkinUtil.isNotEmpty(sql)) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = getConnection().prepareStatement(sql);
				int i = 1;
				for (final Object val : params) {
					ps.setObject(i++, val);
				}

				LOGGER.trace("SQL : {}", sql);
				LOGGER.trace("Parameter : {}", params);
				rs = ps.executeQuery();
				rs.setFetchSize(100);

				if (rs.next()) {
					final String name = rs.getString("name");
					final String label = rs.getString("label");

					schema = new SchemaEntity(name, label);
				}

			} catch (SQLException ex) {
				LOGGER.error("Get default schema error.", ex);
				LOGGER.error("SQL : {}", sql);
				LOGGER.error("Parameter : {}", params);
				throw ex;
			} finally {
				release(rs);
				release(ps);
			}
		}
		return schema;
	}

	/**
	 * <p>
	 * 以下の項目をSELECT句に指定すること。
	 * <ul>
	 * <li>name - スキーマ名(必須)</li>
	 * <li>label - ラベル</li>
	 * </ul>
	 * </p>
	 * 
	 * @param builder
	 */
	protected abstract void sqlGetSchemaList(SQLBuilder builder);

	@Override
	public final List<SchemaEntity> getSchemaList() throws SQLException {
		final List<SchemaEntity> schemas = new ArrayList<SchemaEntity>();

		final MySQLBuilder builder = new MySQLBuilder();
		sqlGetSchemaList(builder);

		final String sql = builder.getSQL();
		final List<Object> params = builder.getParams();

		if (MunchkinUtil.isNotEmpty(sql)) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = getConnection().prepareStatement(sql);
				int i = 1;
				for (final Object val : params) {
					ps.setObject(i++, val);
				}

				LOGGER.trace("SQL : {}", sql);
				LOGGER.trace("Parameter : {}", params);
				rs = ps.executeQuery();
				rs.setFetchSize(100);

				while (rs.next()) {
					final String name = rs.getString("name");
					final String label = rs.getString("label");

					final SchemaEntity schema = new SchemaEntity(name, label);
					schemas.add(schema);
				}

			} catch (SQLException ex) {
				LOGGER.error("Get schema list error.", ex);
				LOGGER.error("SQL : {}", sql);
				LOGGER.error("Parameter : {}", params);
				throw ex;
			} finally {
				release(rs);
				release(ps);
			}
		}
		return schemas;
	}

	/**
	 * <p>
	 * 以下の項目をSELECT句に指定すること。
	 * <ul>
	 * <li>name - タイプ名(必須)</li>
	 * <li>label - ラベル</li>
	 * </ul>
	 * </p>
	 * 
	 * @param builder
	 */
	protected abstract void sqlGetTypeList(SQLBuilder builder);

	@Override
	public final List<TypeEntity> getTypeList() throws SQLException {
		final List<TypeEntity> types = new ArrayList<TypeEntity>();

		final MySQLBuilder builder = new MySQLBuilder();
		sqlGetTypeList(builder);

		final String sql = builder.getSQL();
		final List<Object> params = builder.getParams();

		if (MunchkinUtil.isNotEmpty(sql)) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = getConnection().prepareStatement(sql);
				int i = 1;
				for (final Object val : params) {
					ps.setObject(i++, val);
				}

				LOGGER.trace("SQL : {}", sql);
				LOGGER.trace("Parameter : {}", params);
				rs = ps.executeQuery();
				rs.setFetchSize(100);

				while (rs.next()) {
					final String name = rs.getString("name");
					final String label = rs.getString("label");

					final TypeEntity type = new TypeEntity(name, label);
					types.add(type);
				}

			} catch (SQLException ex) {
				LOGGER.error("Get type list error.", ex);
				LOGGER.error("SQL : {}", sql);
				LOGGER.error("Parameter : {}", params);
				throw ex;
			} finally {
				release(rs);
				release(ps);
			}
		}
		return types;
	}

	/**
	 * 
	 * <p>
	 * 以下の項目をSELECT句に指定すること。
	 * <ul>
	 * <li>schema - スキーマ名(必須)</li>
	 * <li>name - オブジェクト名(必須)</li>
	 * <li>label - ラベル</li>
	 * <li>type - タイプ(必須)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param builder
	 * @param schema
	 * @param type
	 */
	protected abstract void sqlGetObjectList(SQLBuilder builder, SchemaEntity schema, TypeEntity type);

	@Override
	public final List<ObjectEntity> getObjectList(final SchemaEntity schema, final TypeEntity type) throws SQLException {
		final List<ObjectEntity> objects = new ArrayList<ObjectEntity>();

		final MySQLBuilder builder = new MySQLBuilder();
		sqlGetObjectList(builder, schema, type);

		final String sql = builder.getSQL();
		final List<Object> params = builder.getParams();

		if (MunchkinUtil.isNotEmpty(sql)) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = getConnection().prepareStatement(sql);
				int i = 1;
				for (final Object val : params) {
					ps.setObject(i++, val);
				}

				LOGGER.trace("SQL : {}", sql);
				LOGGER.trace("Parameter : {}", params);
				rs = ps.executeQuery();
				rs.setFetchSize(100);

				while (rs.next()) {
					final String sche = rs.getString("schema");
					final String name = rs.getString("name");
					final String label = rs.getString("label");
					final String typ = rs.getString("type");

					final ObjectEntity object = new ObjectEntity(sche, name, label, typ);
					objects.add(object);
				}

			} catch (SQLException ex) {
				LOGGER.error("Get object list error.", ex);
				LOGGER.error("SQL : {}", sql);
				LOGGER.error("Parameter : {}", params);
				throw ex;
			} finally {
				release(rs);
				release(ps);
			}
		}
		return objects;
	}

	/**
	 * <p>
	 * 以下の項目をSELECT句に指定すること。
	 * <ul>
	 * <li>name - オブジェクト名(必須)</li>
	 * <li>label - ラベル</li>
	 * </ul>
	 * </p>
	 * 
	 * @param builder
	 * @param table
	 */
	protected abstract void sqlGetPrimaryKey(SQLBuilder builder, TableObjectEntity table);

	@Override
	public final List<ColumnEntity> getPrimaryKey(final TableObjectEntity table) throws SQLException {
		final List<ColumnEntity> columns = new ArrayList<ColumnEntity>();

		final MySQLBuilder builder = new MySQLBuilder();
		sqlGetPrimaryKey(builder, table);

		final String sql = builder.getSQL();
		final List<Object> params = builder.getParams();

		if (MunchkinUtil.isNotEmpty(sql)) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = getConnection().prepareStatement(sql);
				int i = 1;
				for (final Object val : params) {
					ps.setObject(i++, val);
				}

				LOGGER.trace("SQL : {}", sql);
				LOGGER.trace("Parameter : {}", params);
				rs = ps.executeQuery();
				rs.setFetchSize(100);

				while (rs.next()) {
					final String name = rs.getString("name");
					final String label = rs.getString("label");

					final ColumnEntity column = new ColumnEntity(name, label);
					columns.add(column);
				}

			} catch (SQLException ex) {
				LOGGER.error("Get primary key error.", ex);
				LOGGER.error("SQL : {}", sql);
				LOGGER.error("Parameter : {}", params);
				throw ex;
			} finally {
				release(rs);
				release(ps);
			}
		}
		return columns;
	}

	/**
	 * 
	 * @param builder
	 * @param object
	 */
	protected abstract void sqlGetObjectDetail(SQLBuilder builder, ObjectEntity object);

	@Override
	public final ObjectDetailEntity getObjectDetail(final ObjectEntity object) throws SQLException {
		ObjectDetailEntity detail = null;

		final MySQLBuilder builder = new MySQLBuilder();
		sqlGetObjectDetail(builder, object);

		final String sql = builder.getSQL();
		final List<Object> params = builder.getParams();

		if (MunchkinUtil.isNotEmpty(sql)) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = getConnection().prepareStatement(sql);
				int i = 1;
				for (final Object val : params) {
					ps.setObject(i++, val);
				}

				LOGGER.trace("SQL : {}", sql);
				LOGGER.trace("Parameter : {}", params);
				rs = ps.executeQuery();
				rs.setFetchSize(100);

				final List<String> columnNames = new ArrayList<String>();
				final ResultSetMetaData meta = rs.getMetaData();
				for (i = 0; i < meta.getColumnCount(); i++) {
					columnNames.add(meta.getColumnName(i + 1));
				}

				final List<List<Object>> records = new ArrayList<List<Object>>();
				while (rs.next()) {
					final List<Object> record = new ArrayList<Object>();

					for (i = 0; i < meta.getColumnCount(); i++) {
						record.add(rs.getObject(i + 1));
					}

					records.add(record);
				}

				detail = new ObjectDetailEntity(columnNames, records);

			} catch (SQLException ex) {
				LOGGER.error("Get primary key error.", ex);
				LOGGER.error("SQL : {}", sql);
				LOGGER.error("Parameter : {}", params);
				throw ex;
			} finally {
				release(rs);
				release(ps);
			}
		}
		return detail;
	}

	private static class MySQLBuilder implements SQLBuilder {

		private final StringBuilder sql;
		private final List<Object> params;

		public MySQLBuilder() {
			sql = new StringBuilder();
			params = new ArrayList<Object>();
		}

		public String getSQL() {
			return sql.toString();
		}

		public List<Object> getParams() {
			return params;
		}

		@Override
		public void append(final String line) {
			sql.append(line);
			sql.append("\n");
		}

		@Override
		public void append(final String line, final Object... params) {
			sql.append(line);
			sql.append("\n");
			for (Object param : params) {
				this.params.add(param);
			}
		}
	}
}
