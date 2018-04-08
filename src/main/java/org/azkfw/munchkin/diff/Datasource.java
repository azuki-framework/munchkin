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

import java.util.List;

/**
 *
 * @author Kawakicchi
 */
public interface Datasource {

	/**
	 * データソースを開く。
	 *
	 * @return
	 */
	boolean open();

	/**
	 * データソースを閉じる。
	 */
	void close();

	/**
	 * データソース名を取得する。
	 *
	 * @return データソース名
	 */
	String getName();

	/**
	 * テーブル名一覧を取得する。
	 *
	 * @return テーブル名一覧
	 */
	List<String> getTableNames();

	/**
	 * テーブル情報を取得する。
	 *
	 * @param name テーブル名
	 * @return テーブル情報
	 */
	Table getTable(String name);
}
