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

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.azkfw.munchkin.database.model.ColumnModel;
import org.azkfw.munchkin.database.model.DatabaseModel;
import org.azkfw.munchkin.database.model.QueryHandler;
import org.azkfw.munchkin.database.model.RecordModel;
import org.azkfw.munchkin.util.MunchkinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kawakicchi
 */
public class ExportDB {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportDB.class);

	private final DatabaseModel model;
	private File outputFile;

	private CellStyle styleHead;
	private CellStyle styleData1;
	private CellStyle styleData2;

	public ExportDB(final DatabaseModel model) {
		this.model = model;
	}

	public void setOutputFile(final File file) {
		this.outputFile = file;
	}

	public void export(final List<String> tableNames) {
		final Workbook workbook = new SXSSFWorkbook();

		final Font fontDef = workbook.getFontAt((short) 0);
		fontDef.setFontName("ＭＳ ゴシック");
		fontDef.setFontHeightInPoints((short) 12);

		final Font fontHead = workbook.createFont();
		fontHead.setFontName(fontDef.getFontName());
		fontHead.setFontHeightInPoints(fontDef.getFontHeightInPoints());
		if (fontHead instanceof XSSFFont) {
			final XSSFFont font = (XSSFFont) fontHead;
			font.setColor(new XSSFColor(new Color(255, 255, 255)));
		}
		final Font fontData = workbook.createFont();
		fontData.setFontName(fontDef.getFontName());
		fontData.setFontHeightInPoints(fontDef.getFontHeightInPoints());
		if (fontData instanceof XSSFFont) {
			final XSSFFont font = (XSSFFont) fontData;
			font.setColor(new XSSFColor(new Color(0, 0, 0)));
		}

		styleHead = workbook.createCellStyle();
		styleHead.setFont(fontHead);
		styleHead.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleHead.setBorderBottom(BorderStyle.MEDIUM);
		styleHead.setBorderRight(BorderStyle.THIN);
		if (styleHead instanceof XSSFCellStyle) {
			final XSSFCellStyle style = (XSSFCellStyle) styleHead;
			style.setFillForegroundColor(new XSSFColor(new Color(71, 125, 192)));
			style.setBottomBorderColor(new XSSFColor(new Color(255, 255, 255)));
			style.setRightBorderColor(new XSSFColor(new Color(255, 255, 255)));
		}

		styleData1 = workbook.createCellStyle();
		styleData1.setFont(fontData);
		styleData1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleData1.setBorderBottom(BorderStyle.THIN);
		styleData1.setBorderRight(BorderStyle.THIN);
		if (styleData1 instanceof XSSFCellStyle) {
			final XSSFCellStyle style = (XSSFCellStyle) styleData1;
			style.setFillForegroundColor(new XSSFColor(new Color(181, 203, 229)));
			style.setBottomBorderColor(new XSSFColor(new Color(255, 255, 255)));
			style.setRightBorderColor(new XSSFColor(new Color(255, 255, 255)));
		}
		styleData2 = workbook.createCellStyle();
		styleData2.setFont(fontData);
		styleData2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleData2.setBorderBottom(BorderStyle.THIN);
		styleData2.setBorderRight(BorderStyle.THIN);
		if (styleData2 instanceof XSSFCellStyle) {
			final XSSFCellStyle style = (XSSFCellStyle) styleData2;
			style.setFillForegroundColor(new XSSFColor(new Color(218, 230, 242)));
			style.setBottomBorderColor(new XSSFColor(new Color(255, 255, 255)));
			style.setRightBorderColor(new XSSFColor(new Color(255, 255, 255)));
		}

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
					for (int i = 0; i < column.count(); i++) {
						cell = row.createCell(i);
						cell.setCellStyle(styleHead);
						cell.setCellValue(column.getName(i));
					}
				}

				@Override
				public void record(final long no, final RecordModel record) {
					final CellStyle style = (0 == (no % 2)) ? styleData1 : styleData2;

					final Row row = sheet.createRow((int) (no)); // TODO: multi page

					Cell cell = null;
					for (int i = 0; i < column.count(); i++) {
						cell = row.createCell(i);
						cell.setCellStyle(style);
						cell.setCellValue(s(record.getValue(i)));
					}
				}
			});

			LOGGER.debug("Record : {}", cnt);

		} catch (SQLException ex) {
			LOGGER.error("Table data export error.", ex);
		}
	}

}
