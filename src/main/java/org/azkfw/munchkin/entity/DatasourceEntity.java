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

/**
 * このクラスは、データソース設定を保持するクラスです。
 *
 * @author Kawakicchi
 */
public class DatasourceEntity {

	/** 名前 */
	private String name;
	/** ユーザ名 */
	private String user;
	/** パスワード */
	private String password;
	/** ドライバ */
	private String driver;
	/** URL */
	private String url;

	/**
	 * nameを取得する。
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * nameを設定する。
	 *
	 * @param name name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * userを取得する。
	 *
	 * @return user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * userを設定する。
	 *
	 * @param user ss
	 */
	public void setUser(final String user) {
		this.user = user;
	}

	/**
	 * passwordを取得する。
	 *
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * passwordを設定する。
	 *
	 * @param password パスワード
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * driverを取得する。
	 *
	 * @return driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * driverを設定する。
	 *
	 * @param driver driver
	 */
	public void setDriver(final String driver) {
		this.driver = driver;
	}

	/**
	 * urlを取得する。
	 *
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * urlを設定する。
	 *
	 * @param url url
	 */
	public void setUrl(final String url) {
		this.url = url;
	}
}
