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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azkfw.munchkin.util.MunchkinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kawakicchi
 */
public abstract class AbstractDatabaseModel implements DatabaseModel {

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseModel.class);
	/** connection */
	private final Connection connection;

	/**
	 * コンストラクタ
	 *
	 * @param connection コネクション
	 */
	public AbstractDatabaseModel(final Connection connection) {
		this.connection = connection;
	}

	@Override
	public boolean execute(final String sql) throws SQLException {
		return execute(sql, null);
	}

	@Override
	public boolean execute(final String sql, final List<Object> params) throws SQLException {
		boolean rslt = false;
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement(sql);

			if (MunchkinUtil.isNotNull(params)) {
				int index = 0;
				for (Object param : params) {
					ps.setObject(++index, param);
				}
			}

			rslt = ps.execute();

		} catch (SQLException ex) {
			LOGGER.error("SQL execute error.", ex);
			LOGGER.error("SQL : {}", sql);
			LOGGER.error("Parameter : {}", params);
			throw ex;
		} finally {
			release(ps);
		}
		return rslt;
	}

	@Override
	public int executeUpdate(final String sql) throws SQLException {
		return executeUpdate(sql, null);
	}

	@Override
	public int executeUpdate(final String sql, final List<Object> params) throws SQLException {
		int rslt = -1;
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement(sql);

			if (MunchkinUtil.isNotNull(params)) {
				int index = 0;
				for (Object param : params) {
					ps.setObject(++index, param);
				}
			}

			rslt = ps.executeUpdate();

		} catch (SQLException ex) {
			LOGGER.error("SQL execute error.", ex);
			LOGGER.error("SQL : {}", sql);
			LOGGER.error("Parameter : {}", params);
			throw ex;
		} finally {
			release(ps);
		}
		return rslt;
	}

	@Override
	public List<Map<String, Object>> executeQuery(final String sql) throws SQLException {
		return executeQuery(sql, (List<Object>) null);
	}

	@Override
	public List<Map<String, Object>> executeQuery(final String sql, final List<Object> params) throws SQLException {
		final List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();

		executeQuery(sql, params, new QueryHandler() {

			private List<String> names;

			@Override
			public void column(final ColumnModel column) {
				names = new ArrayList<String>(column.getNames());
			}

			@Override
			public void record(final long no, final RecordModel record) {
				final Map<String, Object> rd = new HashMap<String, Object>();
				for (String name : names) {
					rd.put(name, record.getValue(name));
				}
				records.add(rd);
			}
		});

		return records;
	}

	@Override
	public long executeQuery(final String sql, final List<Object> params, final QueryHandler handler)
			throws SQLException {
		long cnt = 0;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getConnection().prepareStatement(sql);

			if (MunchkinUtil.isNotNull(params)) {
				int index = 0;
				for (Object param : params) {
					ps.setObject(++index, param);
				}
			}

			rs = ps.executeQuery();
			rs.setFetchSize(1000);

			final ResultSetMetaData meta = rs.getMetaData();

			final ColumnModel column = new MyColumnModel(meta);
			handler.column(column);

			while (rs.next()) {

				final RecordModel record = new MyRecordModel(meta, rs);
				handler.record(cnt + 1, record);

				cnt++;
			}

		} catch (SQLException ex) {
			LOGGER.error("SQL execute error.", ex);
			LOGGER.error("SQL : {}", sql);
			LOGGER.error("Parameter : {}", params);
			throw ex;
		} finally {
			release(rs);
			release(ps);
		}

		return cnt;
	}

	/**
	 * コネクションを取得する。
	 *
	 * @return コネクション
	 */
	protected final Connection getConnection() {
		return connection;
	}

	/**
	 * {@link PreparedStatement} を解放する。
	 *
	 * @param ps {@link PreparedStatement}
	 */
	protected final void release(final PreparedStatement ps) {
		if (null != ps) {
			try {
				ps.close();
			} catch (SQLException ex) {
				LOGGER.warn("PreparedStatement close error.", ex);
			}
		}
	}

	/**
	 * {@link ResultSet} を解放する。
	 *
	 * @param rs {@link ResultSet}
	 */
	protected final void release(final ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException ex) {
				LOGGER.warn("ResultSet close error.", ex);
			}
		}
	}

	private class MyColumnModel implements ColumnModel {

		private final int count;
		private final List<String> names;

		public MyColumnModel(final ResultSetMetaData meta) throws SQLException {
			count = meta.getColumnCount();
			names = new ArrayList<String>();
			for (int col = 1; col <= meta.getColumnCount(); col++) {
				names.add(meta.getColumnName(col));
			}
		}

		@Override
		public int count() {
			return count;
		}

		@Override
		public String getName(final int index) {
			return names.get(index);
		}

		@Override
		public List<String> getNames() {
			return new ArrayList<String>(names);
		}
	}

	private class MyRecordModel implements RecordModel {

		private List<Object> values;
		private Map<String, Object> nameValues;

		public MyRecordModel(final ResultSetMetaData meta, final ResultSet rs) throws SQLException {
			values = new ArrayList<Object>();
			nameValues = new HashMap<String, Object>();
			for (int col = 1; col <= meta.getColumnCount(); col++) {
				final String name = meta.getColumnName(col);
				final Object value = rs.getObject(col);

				values.add(value);
				nameValues.put(name, value);
			}
		}

		@Override
		public Object getValue(final int index) {
			return values.get(index);
		}

		@Override
		public Object getValue(final String name) {
			return nameValues.get(name);
		}

		@Override
		public List<Object> values() {
			return new ArrayList<Object>(values);
		}
	}
}
