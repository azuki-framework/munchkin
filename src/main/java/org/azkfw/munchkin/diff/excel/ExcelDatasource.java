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
package org.azkfw.munchkin.diff.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.azkfw.munchkin.diff.Datasource;
import org.azkfw.munchkin.diff.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitorjbl.xlsx.StreamingReader;

/**
 *
 * @author Kawakicchi
 */
public class ExcelDatasource implements Datasource {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDatasource.class);

	private static final Pattern PTN_SHEET_NAME = Pattern
			.compile("^([^\\s\\(]+)[\\s]*(\\([\\s]*([0-9]+)[\\s]*\\)){0,1}$");

	/** データソース名 */
	private final String name;

	/** ファイル */
	private final File file;

	private Workbook workbook;

	/** テーブル名一覧 */
	private List<String> tableNames;
	/** テーブルごとのシート名 Map<テーブル名, List<シート名>> */
	private Map<String, List<String>> sheetNameMap;

	/**
	 * コンストラクタ
	 *
	 * @param name データソース名
	 * @param file エクセルファイル
	 */
	public ExcelDatasource(final String name, final File file) {
		this.name = name;
		this.file = file;
	}

	@Override
	public boolean open() {
		boolean result = false;
		try {
			final InputStream stream = new FileInputStream(file);
			workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(stream);

			tableNames = new ArrayList<String>();
			final Map<String, List<TableSet>> tablesets = new HashMap<String, List<TableSet>>();
			for (Sheet sheet : workbook) {
				final String sheetName = sheet.getSheetName();

				final Matcher m = PTN_SHEET_NAME.matcher(sheetName);
				if (m.find()) {
					final String name = m.group(1);

					List<TableSet> tableset = tablesets.get(name);
					if (null == tableset) {
						tableNames.add(name);
						tableset = new ArrayList<ExcelDatasource.TableSet>();
						tablesets.put(name, tableset);
					}

					if (null == m.group(3)) {
						final TableSet ts = new TableSet(1, sheetName);
						tableset.add(ts);
					} else {
						final Integer index = Integer.parseInt(m.group(3));
						final TableSet ts = new TableSet(index, sheetName);
						tableset.add(ts);
					}
				} else {
					LOGGER.warn("Unmatch sheet name.[{}]", sheetName);
				}
			}

			for (List<TableSet> list : tablesets.values()) {
				Collections.sort(list, new Comparator<TableSet>() {
					@Override
					public int compare(final TableSet o1, final TableSet o2) {
						return o1.getIndex() - o2.getIndex();
					}
				});
			}

			sheetNameMap = new HashMap<String, List<String>>();
			for (String tableName : tableNames) {
				LOGGER.debug("Table {}", tableName);

				final List<String> sn = new ArrayList<String>();
				for (TableSet ts : tablesets.get(tableName)) {
					LOGGER.debug("  [{}] {}", ts.getIndex(), ts.getName());
					sn.add(ts.getName());
				}
				sheetNameMap.put(tableName, sn);
			}

			result = true;
		} catch (IOException ex) {
			LOGGER.error("Excel file open error.", ex);
		}
		return result;
	}

	@Override
	public void close() {
		if (null != workbook) {
			try {
				workbook.close();
			} catch (IOException ex) {
				LOGGER.error("Excel file close error.", ex);
			}
			workbook = null;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getTableNames() {
		return tableNames;
	}

	@Override
	public Table getTable(final String name) {
		final List<String> sheetNames = sheetNameMap.get(name);
		final ExcelTable table = new ExcelTable(this, name, sheetNames);
		return table;
	}

	/**
	 * シートを取得する。
	 *
	 * @param sheetName シート名
	 * @return シート
	 */
	public Sheet getSheet(final String sheetName) {
		return workbook.getSheet(sheetName);
	}

	private class TableSet {
		private int index;
		private String name;

		public TableSet(final int index, final String name) {
			this.index = index;
			this.name = name;
		}

		public int getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}
	}
}
