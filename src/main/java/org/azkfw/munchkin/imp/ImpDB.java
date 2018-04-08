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
package org.azkfw.munchkin.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.azkfw.munchkin.database.model.DatabaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitorjbl.xlsx.StreamingReader;

/**
 * このクラスは、データベースへデータをインポートするクラスです。
 * 
 * @author Kawakicchi
 */
public class ImpDB {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ImpDB.class);

	/** モデル */
	private final DatabaseModel model;
	/** リスナー */
	private final List<ImpDBListener> listeners;

	/**
	 * コンストラクタ
	 *
	 * @param model データベースモデル
	 */
	public ImpDB(final DatabaseModel model) {
		this.model = model;
		this.listeners = new ArrayList<ImpDBListener>();
	}

	public synchronized void addImpDBListener(final ImpDBListener listener) {
		listeners.add(listener);
	}

	public void imp(final File inputFile) {
		InputStream is = null;
		Workbook workbook = null;
		try {
			is = new FileInputStream(inputFile);
			workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is);

			for (Sheet sheet : workbook) {
				System.out.println(sheet.getSheetName());
				for (Row row : sheet) {
					for (Cell cell : row) {
						System.out.println(cell.getStringCellValue());
					}
				}
			}
		} catch (IOException ex) {
			LOGGER.error("Table data import error.", ex);
		} finally {
			if (null != workbook) {
				try {
					workbook.close();
				} catch (IOException ex) {

				}
			}
			if (null != is) {
				try {
					is.close();
				} catch (IOException ex) {

				}
			}
		}
	}
}
