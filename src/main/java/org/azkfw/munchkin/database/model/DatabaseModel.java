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
package org.azkfw.munchkin.database.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.azkfw.munchkin.database.model.entity.ObjectDetailEntity;
import org.azkfw.munchkin.database.model.entity.ObjectEntity;
import org.azkfw.munchkin.database.model.entity.SchemaEntity;
import org.azkfw.munchkin.database.model.entity.TypeEntity;

/**
 * このインターフェースは、データベースのモデルを定義するインターフェースです。
 * 
 * @author Kawakicchi
 */
public interface DatabaseModel {

	/**
	 * SQLを実行する。
	 *
	 * @param sql SQL文
	 * @return 結果
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	boolean execute(String sql) throws SQLException;

	/**
	 * SQLを実行する。
	 *
	 * @param sql SQL文
	 * @param params パラメータ
	 * @return 結果
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	boolean execute(String sql, List<Object> params) throws SQLException;

	/**
	 * SQLを実行する。
	 *
	 * @param sql SQL文
	 * @return 更新件数
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	int executeUpdate(String sql) throws SQLException;

	/**
	 * SQLを実行する。
	 *
	 * @param sql SQL文
	 * @param params パラメータ
	 * @return 更新件数
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	int executeUpdate(String sql, List<Object> params) throws SQLException;

	/**
	 * SQLを実行する。
	 *
	 * @param sql SQL文
	 * @return 結果
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	List<Map<String, Object>> executeQuery(String sql) throws SQLException;

	/**
	 * SQLを実行する。
	 *
	 * @param sql SQL文
	 * @param params パラメータ
	 * @return 結果
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	List<Map<String, Object>> executeQuery(String sql, List<Object> params) throws SQLException;

	/**
	 * SQLを実行する。
	 *
	 * @param sql SQL文
	 * @param params パラメータ
	 * @param handler ハンドラ
	 * @return 結果
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	long executeQuery(String sql, List<Object> params, QueryHandler handler) throws SQLException;

	/**
	 * デフォルトスキーマ情報を取得する。
	 *
	 * @return スキーマ情報
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	SchemaEntity getDefaultSchema() throws SQLException;

	/**
	 * スキーマ情報一覧を取得する。
	 *
	 * @return スキーマ情報一覧
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	List<SchemaEntity> getSchemaList() throws SQLException;

	/**
	 * タイプ情報一覧を取得する。
	 *
	 * @return タイプ情報一覧
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	List<TypeEntity> getTypeList() throws SQLException;

	/**
	 * オブジェクト情報一覧を取得する。
	 *
	 * @param schema スキーマ情報
	 * @param type タイプ情報
	 * @return オブジェクト情報一覧
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	List<ObjectEntity> getObjectList(SchemaEntity schema, TypeEntity type) throws SQLException;

	/**
	 * オブジェクトの詳細情報を取得する。
	 *
	 * @param object オブジェクト情報
	 * @return オブジェクト詳細情報
	 * @throws SQLException SQL操作に起因する問題が発生した場合
	 */
	ObjectDetailEntity getObjectDetail(ObjectEntity object) throws SQLException;
}
