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
				for (int i = 0; i < params.size(); i++) {
					ps.setObject(i + 1, params.get(i));
				}
			}
			rslt = ps.execute();
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
				for (int i = 0; i < params.size(); i++) {
					ps.setObject(i + 1, params.get(i));
				}
			}
			rslt = ps.executeUpdate();
		} finally {
			release(ps);
		}
		return rslt;
	}

	@Override
	public List<Map<String, Object>> executeQuery(final String sql) throws SQLException {
		return executeQuery(sql, null);
	}

	@Override
	public List<Map<String, Object>> executeQuery(final String sql, final List<Object> params) throws SQLException {
		final List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getConnection().prepareStatement(sql);
			if (MunchkinUtil.isNotNull(params)) {
				for (int i = 0; i < params.size(); i++) {
					ps.setObject(i + 1, params.get(i));
				}
			}
			rs = ps.executeQuery();
			rs.setFetchSize(1000);

			final ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				final Map<String, Object> record = new HashMap<String, Object>();
				for (int i = 0; i < meta.getColumnCount(); i++) {
					record.put(meta.getColumnName(i + 1), rs.getObject(i + 1));
				}
				records.add(record);
			}

		} finally {
			release(rs);
			release(ps);
		}
		return records;
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
}
