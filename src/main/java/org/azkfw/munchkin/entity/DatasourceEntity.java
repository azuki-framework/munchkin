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

import java.awt.Color;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.azkfw.munchkin.util.MunchkinUtil;

/**
 * このクラスは、データソース設定を保持するクラスです。
 *
 * @author Kawakicchi
 */
@XmlType(propOrder = { "name", "user", "password", "driver", "url", "colorHexString", "memo" })
public class DatasourceEntity {

	/** データソース名 */
	private String name;
	/** カラー */
	private Color color;
	/** メモ */
	private String memo;

	/** ユーザ名 */
	private String user;
	/** パスワード */
	private String password;
	/** ドライバ */
	private String driver;
	/** 接続文字列 */
	private String url;

	public DatasourceEntity() {
		name = "";
		color = Color.white;
		memo = "";

		user = "";
		password = "";
		driver = "";
		url = "";
	}

	public DatasourceEntity(final DatasourceEntity datasource) {
		name = datasource.name;
		color = datasource.color;
		memo = datasource.memo;

		user = datasource.user;
		password = datasource.password;
		driver = datasource.driver;
		url = datasource.url;
	}

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
	 * カラー を取得する。
	 *
	 * @return カラー
	 */
	@XmlTransient
	public Color getColor() {
		return color;
	}

	/**
	 * カラー を設定する。
	 *
	 * @param color カラー
	 */
	public void setColor(final Color color) {
		this.color = color;
	}

	@XmlAttribute(name = "color")
	public String getColorHexString() {
		return MunchkinUtil.getHexadecimalFromColor(color);
	}

	public void setColorHexString(final String hex) {
		color = MunchkinUtil.getColorFormHexadecimal(hex);
	}

	/**
	 * メモ を取得する。
	 *
	 * @return メモ
	 */
	@XmlAttribute
	public String getMemo() {
		return memo;
	}

	/**
	 * メモ を設定する。
	 *
	 * @param memo メモ
	 */
	public void setMemo(final String memo) {
		this.memo = memo;
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
