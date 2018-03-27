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
package org.azkfw.munchkin.database.model.entity;

import java.io.Serializable;

/**
 * カラム情報を保持するエンティティクラス
 * 
 * @author kawakicchi
 */
public class ColumnEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -161088285927082063L;

	/** 名前 */
	private final String name;
	/** ラベル */
	private final String label;

	/**
	 * コンストラクタ
	 *
	 * @param name 名前
	 * @param label ラベル
	 */
	public ColumnEntity(final String name, final String label) {
		super();
		this.name = name;
		this.label = label;
	}

	/**
	 * 名前を取得する。
	 *
	 * @return 名前
	 */
	public String getName() {
		return name;
	}

	/**
	 * ラベルを取得する。
	 *
	 * @return ラベル
	 */
	public String getLabel() {
		return label;
	}
}
