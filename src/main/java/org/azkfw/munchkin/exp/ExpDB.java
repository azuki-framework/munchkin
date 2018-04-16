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
package org.azkfw.munchkin.exp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.azkfw.munchkin.database.model.ColumnModel;
import org.azkfw.munchkin.database.model.DatabaseModel;
import org.azkfw.munchkin.database.model.QueryHandler;
import org.azkfw.munchkin.database.model.RecordModel;
import org.azkfw.munchkin.writer.TableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * このクラスは、データベースからデータをエクスポートするクラスです。
 * 
 * @author Kawakicchi
 */
public class ExpDB {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpDB.class);

	/** モデル */
	private final DatabaseModel model;
	/** リスナー */
	private final List<ExpDBListeneer> listeners;

	/**
	 * コンストラクタ
	 *
	 * @param model データベースモデル
	 */
	public ExpDB(final DatabaseModel model) {
		this.model = model;
		this.listeners = new ArrayList<ExpDBListeneer>();
	}

	/**
	 * リスナーを追加する。
	 *
	 * @param listener リスナー
	 */
	public synchronized void addExpDBListener(final ExpDBListeneer listener) {
		listeners.add(listener);
	}

	/**
	 * エクスポートを行う。
	 *
	 * @param tableNames
	 * @param writer ライター
	 */
	public void exp(final List<String> tableNames, final TableWriter writer) {
		writer.open();
		for (String tableName : tableNames) {
			exportTable(tableName, writer);
		}
		writer.close();
	}

	private void exportTable(final String tableName, final TableWriter writer) {
		writer.table(tableName);

		final String sql = "select * from " + tableName;
		final List<Object> params = new ArrayList<Object>();

		try {
			LOGGER.debug("SQL : {}", sql);
			final long cnt = model.executeQuery(sql, params, new QueryHandler() {

				@Override
				public void column(final ColumnModel column) {
					writer.column(column);
				}

				@Override
				public void record(final long no, final RecordModel record) {
					writer.record(no, record);
				}
			});
			LOGGER.debug("Record : {}", cnt);

		} catch (SQLException ex) {
			LOGGER.error("Table data export error.", ex);
		}
	}
}
