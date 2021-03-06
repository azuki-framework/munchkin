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
package org.azkfw.munchkin.ui.panel;

import org.azkfw.munchkin.database.model.entity.SchemaEntity;
import org.azkfw.munchkin.database.model.entity.TypeEntity;

/**
 * このインターフェースは、オブジェクト条件パネルのイベントを定義するインターフェースです。
 * 
 * @author Kawakicchi
 */
public interface DBConditionPanelListener {

	/**
	 * スキーマが変更された場合に呼び出される。
	 *
	 * @param schema スキーマ
	 */
	void dbConditionPanelChengedSchema(SchemaEntity schema);

	/**
	 * タイプが変更された場合に呼び出される。
	 *
	 * @param type タイプ
	 */
	void dbConditionPanelChengedType(TypeEntity type);
}
