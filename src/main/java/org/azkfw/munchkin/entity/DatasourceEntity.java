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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * このクラスは、データソース設定を保持するクラスです。
 *
 * @author Kawakicchi
 */
@XmlType(propOrder = { "name", "user", "password", "driver", "url" })
public class DatasourceEntity {

	/** データソース名 */
	private String name;

	/** ユーザ名 */
	private String user;
	/** パスワード */
	private String password;
	/** ドライバ */
	private String driver;
	/** 接続文字列 */
	private String url;

	/**
	 * データソース名 を取得する。
	 *
	 * @return データソース名
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * データソース名 を設定する。
	 *
	 * @param name データソース名
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * ユーザ名 を取得する。
	 *
	 * @return ユーザ名
	 */
	@XmlElement
	public String getUser() {
		return user;
	}

	/**
	 * ユーザ名 を設定する。
	 *
	 * @param user ユーザ名
	 */
	public void setUser(final String user) {
		this.user = user;
	}

	/**
	 * パスワード を取得する。
	 *
	 * @return パスワード
	 */
	@XmlElement
	public String getPassword() {
		return password;
	}

	/**
	 * パスワード を設定する。
	 *
	 * @param password パスワード
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * ドライバ を取得する。
	 *
	 * @return ドライバ
	 */
	@XmlElement
	public String getDriver() {
		return driver;
	}

	/**
	 * ドライバ を設定する。
	 *
	 * @param driver ドライバ
	 */
	public void setDriver(final String driver) {
		this.driver = driver;
	}

	/**
	 * 接続文字列 を取得する。
	 *
	 * @return 接続文字列
	 */
	@XmlElement
	public String getUrl() {
		return url;
	}

	/**
	 * 接続文字列 を設定する。
	 *
	 * @param url 接続文字列
	 */
	public void setUrl(final String url) {
		this.url = url;
	}
}
