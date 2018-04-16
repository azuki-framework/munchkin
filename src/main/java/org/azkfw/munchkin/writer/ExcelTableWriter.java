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
package org.azkfw.munchkin.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.azkfw.munchkin.database.model.ColumnModel;
import org.azkfw.munchkin.database.model.RecordModel;
import org.azkfw.munchkin.excel.style.RichTableExcelStyle;
import org.azkfw.munchkin.excel.style.TableExcelStyle;
import org.azkfw.munchkin.util.MunchkinUtil;

/**
 * このクラスは、エクセルファイルにテーブルデータを書出すクラスです。
 * 
 * @author Kawakicchi
 */
public class ExcelTableWriter extends AbstractTableWriter {

	private Workbook workbook = new SXSSFWorkbook();

	private Sheet sheet;

	private TableExcelStyle style;

	private File file;

	public ExcelTableWriter(final File file) {
		this.file = file;
	}

	@Override
	public final void open() {
		style = new RichTableExcelStyle();
		workbook = new SXSSFWorkbook();

		style.install(workbook);
	}

	@Override
	public final void close() {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
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

	@Override
	public final void table(final String name) {
		sheet = workbook.createSheet(name);
	}

	private ColumnModel column;

	@Override
	public final void column(final ColumnModel model) {
		this.column = model;

		final Row row = sheet.createRow(0);
		Cell cell = null;
		for (int col = 0; col < column.count(); col++) {
			cell = row.createCell(col);
			cell.setCellStyle(style.getTableHeaderStyle(col));
			cell.setCellValue(column.getName(col));
		}
	}

	@Override
	public final void record(final long no, final RecordModel model) {
		final Row row = sheet.createRow((int) (no)); // TODO: multi page

		Cell cell = null;
		for (int col = 0; col < column.count(); col++) {
			final Object value = model.getValue(col);

			cell = row.createCell(col);
			cell.setCellStyle(style.getTableDataStyle(col, (int) no, value));
			cell.setCellValue(s(value));
		}

	}

	private static String s(final Object obj) {
		String s = "(NULL)";
		if (null != obj) {
			s = obj.toString();
		}
		return s;
	}
}
