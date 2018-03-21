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
package org.azkfw.munchkin.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * このクラスは、履歴情報を保持するエンティティクラスです。
 * 
 * @author Kawakicchi
 */
@XmlRootElement(name = "munchkin-history")
public class MunchkinHistoryEntity {

	/** SQL実行履歴情報リスト */
	private final List<SQLHistoryEntity> historySqls;

	/**
	 * コンストラクタ
	 */
	public MunchkinHistoryEntity() {
		historySqls = new ArrayList<SQLHistoryEntity>();
	}

	/**
	 * SQL実行履歴情報リストを取得する。
	 *
	 * @return SQL実行履歴情報リスト
	 */
	@XmlElementWrapper(name = "sqls")
	@XmlElement(name = "sql")
	public List<SQLHistoryEntity> getHistorySqls() {
		return historySqls;
	}

	/**
	 * SQL実行履歴情報リストを設定する。
	 *
	 * @param historys SQL実行履歴情報リスト
	 */
	public void setHistorySqls(final List<SQLHistoryEntity> historys) {
		historySqls.clear();
		historySqls.addAll(historys);
	}
}
