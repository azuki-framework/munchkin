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

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.azkfw.munchkin.diff.Record;
import org.azkfw.munchkin.diff.Value;

/**
 *
 * @author Kawakicchi
 */
public class ExcelRecord implements Record {

	private final ExcelTable table;

	private final Row row;
	private final List<ExcelValue> values;

	public ExcelRecord(final ExcelTable table, final Row row, final List<ExcelValue> values) {
		this.table = table;
		this.row = row;
		this.values = values;
	}

	@Override
	public Value getValue(final int index) {
		return null;
	}

	@Override
	public Value getValue(final String name) {
		return null;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Value> getValues() {
		return (List) values;
	}
}
