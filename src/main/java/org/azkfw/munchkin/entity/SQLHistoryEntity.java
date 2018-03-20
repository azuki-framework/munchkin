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

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * このクラスは、SQL実行履歴情報を保持するエンティティクラスです。
 * 
 * @author Kawakicchi
 */
public class SQLHistoryEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 8438700471851379667L;

	/** 実行日時 */
	private Date date;
	/** 処理時間[ms] */
	private long time;
	/** 実行SQL */
	private String sql;

	/**
	 * コンストラクタ
	 */
	public SQLHistoryEntity() {
	}

	/**
	 * コンストラクタ
	 *
	 * @param date 実行日時
	 * @param time 処理時間[ms]
	 * @param sql 実行SQL
	 */
	public SQLHistoryEntity(final Date date, final long time, final String sql) {
		this.date = date;
		this.time = time;
		this.sql = sql;
	}

	/**
	 * 実行日時を取得する。
	 *
	 * @return 実行日時
	 */
	@XmlAttribute
	public Date getDate() {
		return date;
	}

	/**
	 * 実行日時を設定する。
	 *
	 * @param date 実行日時
	 */
	public void setDate(final Date date) {
		this.date = date;
	}

	/**
	 * 処理時間を取得する。
	 *
	 * @return 処理時間[ms]
	 */
	@XmlAttribute
	public long getTime() {
		return time;
	}

	/**
	 * 処理時間を設定する。
	 *
	 * @param time 処理時間[ms]
	 */
	public void setTime(final long time) {
		this.time = time;
	}

	/**
	 * 実行SQLを取得する。
	 *
	 * @return 実行SQL
	 */
	@XmlValue
	public String getSql() {
		return sql;
	}

	/**
	 * 実行SQLを設定する。
	 *
	 * @param sql 実行SQL
	 */
	public void setSql(final String sql) {
		this.sql = sql;
	}
}
