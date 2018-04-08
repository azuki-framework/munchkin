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

import org.apache.poi.ss.usermodel.Cell;
import org.azkfw.munchkin.diff.Value;

/**
 *
 * @author Kawakicchi
 */
public class ExcelValue implements Value {

	private final Cell cell;

	public ExcelValue(final Cell cell) {
		this.cell = cell;
	}

	@Override
	public Object value() {
		Object obj = null;
		if (null != cell) {
			obj = cell.getStringCellValue();
		}
		return obj;
	}

	@Override
	public String valueToString() {
		String str = null;
		if (null != cell) {
			str = cell.getStringCellValue();
		}
		return str;
	}

}
