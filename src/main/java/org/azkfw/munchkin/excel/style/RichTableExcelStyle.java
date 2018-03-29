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
package org.azkfw.munchkin.excel.style;

import java.awt.Color;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.azkfw.munchkin.database.model.entity.ColumnEntity;

/**
 *
 * @author Kawakicchi
 *
 */
public class RichTableExcelStyle implements TableExcelStyle {

	private CellStyle styleHead;
	private CellStyle styleData1;
	private CellStyle styleData2;

	@Override
	public void install(final Workbook workbook) {

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
	}

	@Override
	public CellStyle getTableHeaderStyle(final int col) {
		return getTableHeaderStyle(col, null);
	}

	@Override
	public CellStyle getTableHeaderStyle(final int col, final ColumnEntity column) {
		CellStyle style = null;
		style = styleHead;
		return style;
	}

	@Override
	public CellStyle getTableDataStyle(final int col, final int row, final Object value) {
		return getTableDataStyle(col, row, null, value);
	}

	@Override
	public CellStyle getTableDataStyle(final int col, final int row, final ColumnEntity column, final Object value) {
		CellStyle style = null;
		if (0 == (row % 2)) {
			style = styleData1;
		} else {
			style = styleData2;
		}
		return style;
	}
}
