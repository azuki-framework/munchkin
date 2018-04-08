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
package org.azkfw.munchkin.diff;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.azkfw.munchkin.diff.excel.ExcelDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kawakicchi
 */
public class DiffDB {

	public static void main(final String[] args) {
		final File file1 = new File("sample1.xlsx");
		final File file2 = new File("sample2.xlsx");

		final Datasource datasource1 = new ExcelDatasource("", file1);
		final Datasource datasource2 = new ExcelDatasource("", file2);

		DiffDB diff = new DiffDB();
		diff.compare(datasource1, datasource2);
	}

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(DiffDB.class);

	public void compare(final Datasource datasource1, final Datasource datasource2) {
		compareDatasource(datasource1, datasource2);
	}

	private void compareDatasource(final Datasource ds1, final Datasource ds2) {
		try {
			ds1.open();
			ds2.open();

			final List<String> tblNames1 = ds1.getTableNames();
			final List<String> tblNames2 = ds2.getTableNames();

			final List<String> tblNames = new ArrayList<String>();
			for (String tblName : tblNames1) {
				if (tblNames2.contains(tblName)) {
					LOGGER.trace("Compare table.[{}]", tblName);
					tblNames.add(tblName);
				} else {
					LOGGER.trace("Not found table2.[{}]", tblName);
				}
			}
			for (String tblName : tblNames2) {
				if (!tblNames.contains(tblName)) {
					LOGGER.trace("Not found table1.[{}]", tblName);
				}
			}

			for (String tblName : tblNames) {
				final Table table1 = ds1.getTable(tblName);
				final Table table2 = ds2.getTable(tblName);
				compareTable(table1, table2);
			}

		} finally {
			ds1.close();
			ds2.close();
		}
	}

	private void compareTable(final Table tbl1, final Table tbl2) {
		final List<String> hdNames1 = tbl1.getHeaderNames();
		final List<String> hdNames2 = tbl2.getHeaderNames();

		final Set<String> hdNames = new HashSet<String>();
		for (String hdName : hdNames1) {
			if (hdNames2.contains(hdName)) {
				LOGGER.trace("Compare header.[{}]", hdName);
				hdNames.add(hdName);
			} else {
				LOGGER.trace("Not found header2.[{}]", hdName);
			}
		}
		for (String hdName : hdNames2) {
			if (!hdNames.contains(hdName)) {
				LOGGER.trace("Not found header1.[{}]", hdName);
			}
		}

		Record rcd1 = null;
		Record rcd2 = null;
		while (true) {
			if (null == rcd1) {
				rcd1 = tbl1.next();
				if (null != rcd1) {
					LOGGER.trace("Read new record1.");
				}
			}
			if (null == rcd2) {
				rcd2 = tbl2.next();
				if (null != rcd2) {
					LOGGER.trace("Read new record2.");
				}
			}

			if (null != rcd1 && null != rcd2) {
				final int rslt = isSameRecord(rcd1, rcd2);
				if (0 == rslt) {
					// compare 
					compareRecord(rcd1, rcd2);
					rcd1 = null;
					rcd2 = null;
				} else if (0 > rslt) {
					// check record1
					rcd1 = null;
				} else {
					// check record2
					rcd2 = null;
				}
			} else if (null != rcd1 && null == rcd2) {
				// check record1
				rcd1 = null;
			} else if (null == rcd1 && null != rcd2) {
				// check record2
				rcd2 = null;
			} else {
				break;
			}
		}
	}

	/**
	 * レコードを比較する。
	 *
	 * @param record1 レコード1
	 * @param record2 レコード2
	 */
	private boolean compareRecord(final Record record1, final Record record2) {
		boolean match = true;
		final List<Value> values1 = record1.getValues();
		final List<Value> values2 = record2.getValues();

		for (int i = 0; i < values1.size(); i++) {
			final Value value1 = values1.get(i);
			final Value value2 = values2.get(i);

			if (value1.valueToString().equals(value2.valueToString())) {

			} else {
				match = false;
			}
		}
		return match;
	}

	/**
	 * レコードを比較する。
	 *
	 * @param record1 レコード1
	 * @param record2 レコード2
	 * @return <ul>
	 *         <li>0 : 同一レコード</li>
	 *         <li>-1 : レコード1が小さい</li>
	 *         <li>1 : レコード2が小さい</li>
	 *         </ul>
	 */
	private int isSameRecord(final Record record1, final Record record2) {
		final List<Value> values1 = record1.getValues();
		final List<Value> values2 = record2.getValues();
		return values1.get(0).valueToString().compareTo(values2.get(0).valueToString());
	}
}
