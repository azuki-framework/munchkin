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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.azkfw.munchkin.database.model.ColumnModel;
import org.azkfw.munchkin.database.model.DatabaseModel;
import org.azkfw.munchkin.database.model.QueryHandler;
import org.azkfw.munchkin.database.model.RecordModel;
import org.azkfw.munchkin.excel.style.RichTableExcelStyle;
import org.azkfw.munchkin.excel.style.TableExcelStyle;
import org.azkfw.munchkin.util.MunchkinUtil;
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

	private final TableExcelStyle style;

	/**
	 * コンストラクタ
	 *
	 * @param model データベースモデル
	 */
	public ExpDB(final DatabaseModel model) {
		this.model = model;
		this.listeners = new ArrayList<ExpDBListeneer>();

		this.style = new RichTableExcelStyle();
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
	 * @param outputFile
	 */
	public void exp(final List<String> tableNames, final File outputFile) {
		final Workbook workbook = new SXSSFWorkbook();

		style.install(workbook);

		for (String tableName : tableNames) {
			exportTable(tableName, workbook);
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outputFile);
			workbook.write(fos);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (MunchkinUtil.isNotNull(fos)) {
				try {
					fos.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		try {
			workbook.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static String s(final Object obj) {
		String s = "(NULL)";
		if (null != obj) {
			s = obj.toString();
		}
		return s;
	}

	private void exportTable(final String tableName, final Workbook workbook) {
		final Sheet sheet = workbook.createSheet(tableName);

		String sql = "select * from " + tableName;
		List<Object> params = new ArrayList<Object>();

		try {
			LOGGER.debug("SQL : {}", sql);
			final long cnt = model.executeQuery(sql, params, new QueryHandler() {
				private ColumnModel column;

				@Override
				public void column(final ColumnModel column) {
					this.column = column;

					final Row row = sheet.createRow(0);

					Cell cell = null;
					for (int col = 0; col < column.count(); col++) {
						cell = row.createCell(col);
						cell.setCellStyle(style.getTableHeaderStyle(col));
						cell.setCellValue(column.getName(col));
					}
				}

				@Override
				public void record(final long no, final RecordModel record) {
					final Row row = sheet.createRow((int) (no)); // TODO: multi page

					Cell cell = null;
					for (int col = 0; col < column.count(); col++) {
						final Object value = record.getValue(col);

						cell = row.createCell(col);
						cell.setCellStyle(style.getTableDataStyle(col, (int) no, value));
						cell.setCellValue(s(value));
					}
				}
			});

			LOGGER.debug("Record : {}", cnt);

		} catch (SQLException ex) {
			LOGGER.error("Table data export error.", ex);
		}
	}
}
