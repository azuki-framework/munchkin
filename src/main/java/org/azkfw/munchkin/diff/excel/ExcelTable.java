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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.azkfw.munchkin.diff.Record;
import org.azkfw.munchkin.diff.Table;
import org.azkfw.munchkin.util.MunchkinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kawakicchi
 */
public class ExcelTable implements Table {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelTable.class);

	private final ExcelDatasource datasource;

	/** テーブル名 */
	private final String name;
	private final List<String> sheetNames;

	private int sheetIndex;

	private List<String> fixedHeadNames;

	private Sheet currentSheet;
	private Iterator<Row> currentRows;

	private List<String> currentHeadNames;
	private Map<Integer, Integer> currentHeadColNumIndex;
	private Map<Integer, String> currentHeadColNumName;

	public ExcelTable(final ExcelDatasource datasource, final String name, final List<String> sheetNames) {
		this.datasource = datasource;

		this.name = name;
		this.sheetNames = sheetNames;

		this.sheetIndex = 0;

		readHeader();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getHeaderNames() {
		return fixedHeadNames;
	}

	@Override
	public Record next() {
		Record record = null;

		while (!currentRows.hasNext()) {
			if (!readHeader()) {
				break;
			}
		}

		if (currentRows.hasNext()) {
			final Row row = currentRows.next();

			final List<ExcelValue> values = new ArrayList<ExcelValue>(currentHeadNames.size());
			for (int i = 0; i < currentHeadNames.size(); i++) {
				values.add(null);
			}
			for (Cell cell : row) {
				final int colNum = cell.getColumnIndex();
				if (currentHeadColNumIndex.containsKey(colNum)) {
					final int index = currentHeadColNumIndex.get(colNum);
					final ExcelValue value = new ExcelValue(cell);
					values.set(index, value);
				} else {
					// ヘッダーで定義されていない列
				}
			}
			for (int i = 0; i < values.size(); i++) {
				if (null == values.get(i)) {
					values.set(i, new ExcelValue(null));
				}
			}

			record = new ExcelRecord(this, row, values);
		}
		return record;
	}

	private boolean readHeader() {
		boolean result = false;
		if (sheetNames.size() > sheetIndex) {
			final String sheetName = sheetNames.get(sheetIndex++);
			currentSheet = datasource.getSheet(sheetName);
			currentRows = currentSheet.iterator();

			currentHeadNames = new ArrayList<String>();
			currentHeadColNumIndex = new HashMap<Integer, Integer>();
			currentHeadColNumName = new HashMap<Integer, String>();
			while (currentRows.hasNext()) {
				final Row row = currentRows.next();
				int index = 0;
				for (Cell cell : row) {
					final int colNum = cell.getColumnIndex();
					final String headName = getString(cell);
					if (MunchkinUtil.isNotEmpty(headName)) {
						currentHeadNames.add(headName);
						currentHeadColNumIndex.put(colNum, index++);
						currentHeadColNumName.put(colNum, headName);
					}
				}
				if (MunchkinUtil.isNotEmpty(currentHeadNames)) {
					break;
				}
			}

			if (null == fixedHeadNames) {
				fixedHeadNames = new ArrayList<String>(currentHeadNames);
			}

			LOGGER.debug("Header names : {}", currentHeadNames);
		}
		return result;
	}

	private String getString(final Cell cell) {
		String val = null;
		if (null != cell) {
			val = cell.getStringCellValue();
		}
		return val;
	}
}
