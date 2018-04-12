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
package org.azkfw.munchkin.excel.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaError;

/**
 *
 * @author Kawakicchi
 */
public class ExcelUtil {

	public static String getValueToString(final Cell cell) {
		String str = "";

		final CellType type = cell.getCellTypeEnum();
		if (CellType.STRING == type) {
			//System.out.println("String");
			str = cell.getStringCellValue();

		} else if (CellType.NUMERIC == type) {
			if (DateUtil.isCellDateFormatted(cell)) {
				//System.out.println("Date");
				//obj = cell.getDateCellValue();

			} else {
				//System.out.println("Numeric");

			}

		} else if (CellType.BOOLEAN == type) {
			//System.out.println("Boolean");

		} else if (CellType.FORMULA == type) {
			//System.out.println("Formula");

			final CellType fType = cell.getCachedFormulaResultTypeEnum();
			if (CellType.STRING == fType) {
				//System.out.println("String");

			} else if (CellType.NUMERIC == fType) {
				//System.out.println("Numeric");

			} else if (CellType.BOOLEAN == fType) {
				//System.out.println("Boolean");

			} else if (CellType.ERROR == fType) {
				// System.out.println("Error");
				final byte code = cell.getErrorCellValue();
				final FormulaError error = FormulaError.forInt(code);

			} else {
				System.out.println("Unknown");
			}

		} else if (CellType.BLANK == type) {
			System.out.println("Blank"); // 結合セル

		} else if (CellType.ERROR == type) {
			// System.out.println("Error");
			final byte code = cell.getErrorCellValue();
			final FormulaError error = FormulaError.forInt(code);
			str = error.getString();

		} else {
			System.out.println("Unknown");

		}
		return str;
	}

	public static Object getValue(final Cell cell) {
		Object obj = null;

		final CellType type = cell.getCellTypeEnum();
		if (CellType.STRING == type) {
			//System.out.println("String");
			obj = cell.getStringCellValue();

		} else if (CellType.NUMERIC == type) {
			if (DateUtil.isCellDateFormatted(cell)) {
				//System.out.println("Date");
				//obj = cell.getDateCellValue();
				obj = DateUtil.getJavaDate(cell.getNumericCellValue());

			} else {
				//System.out.println("Numeric");
				obj = cell.getNumericCellValue();

			}

		} else if (CellType.BOOLEAN == type) {
			//System.out.println("Boolean");
			obj = cell.getBooleanCellValue();

		} else if (CellType.FORMULA == type) {
			//System.out.println("Formula");

			final CellType fType = cell.getCachedFormulaResultTypeEnum();
			if (CellType.STRING == fType) {
				//System.out.println("String");
				obj = cell.getStringCellValue();

			} else if (CellType.NUMERIC == fType) {
				//System.out.println("Numeric");
				obj = cell.getNumericCellValue();

			} else if (CellType.BOOLEAN == fType) {
				//System.out.println("Boolean");
				obj = cell.getBooleanCellValue();

			} else if (CellType.ERROR == fType) {
				// System.out.println("Error");
				final byte code = cell.getErrorCellValue();
				final FormulaError error = FormulaError.forInt(code);
				obj = error.getString();

			} else {
				System.out.println("Unknown");
			}

		} else if (CellType.BLANK == type) {
			System.out.println("Blank"); // 結合セル

		} else if (CellType.ERROR == type) {
			// System.out.println("Error");
			final byte code = cell.getErrorCellValue();
			final FormulaError error = FormulaError.forInt(code);
			obj = error.getString();

		} else {
			System.out.println("Unknown");

		}
		return obj;
	}
}
