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
package org.azkwf.munchkin.excel.util;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.azkfw.munchkin.excel.util.ExcelUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * このクラスは、{@link ExcelUtil} クラスを評価するテストクラスです。
 *
 * @author Kawakicchi
 */
@RunWith(JUnit4.class)
public class ExcelUtilTest {

	@Test
	public void getValue() throws Exception {
		final InputStream stream = getClass().getResourceAsStream("/org/azkfw/munchkin/excel/util/format.xlsx");
		final Workbook workbook = WorkbookFactory.create(stream);

		final Sheet sheet = workbook.getSheetAt(0);

		for (int rowNum = sheet.getFirstRowNum(); rowNum <= sheet.getLastRowNum(); rowNum++) {
			final Row row = sheet.getRow(rowNum);
			for (int colNum = row.getFirstCellNum(); colNum < row.getLastCellNum(); colNum++) {
				final Cell cell = row.getCell(colNum);

				final Object obj = ExcelUtil.getValue(cell);

				System.out.println(String.format("%d-%d %s", rowNum, colNum, obj));
			}
		}

		workbook.close();
		stream.close();
	}
}
