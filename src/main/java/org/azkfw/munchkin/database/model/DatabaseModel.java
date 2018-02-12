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

/**
 *
 * @author Kawakicchi
 *
 */
public interface DatabaseModel {

	boolean execute(String sql) throws SQLException;

	boolean execute(String sql, List<Object> params) throws SQLException;

	int executeUpdate(String sql) throws SQLException;

	int executeUpdate(String sql, List<Object> params) throws SQLException;

	List<Map<String, Object>> executeQuery(String sql) throws SQLException;

	List<Map<String, Object>> executeQuery(String sql, List<Object> params) throws SQLException;

	SchemaEntity getDefaultSchema() throws SQLException;

	List<SchemaEntity> getSchemaList() throws SQLException;

	List<TypeEntity> getTypeList() throws SQLException;

	List<ObjectEntity> getObjectList(SchemaEntity schema, TypeEntity type) throws SQLException;

	ObjectDetailEntity getObjectDetail(ObjectEntity object) throws SQLException;
}
