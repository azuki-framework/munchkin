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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.azkfw.munchkin.database.model.db2.DB2Model;
import org.azkfw.munchkin.database.model.oracle.OracleModel;
import org.azkfw.munchkin.database.model.postgresql.PostgreSQLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kawakicchi
 */
public class DatabaseModelFactory {

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseModelFactory.class);

	private DatabaseModelFactory() {

	}

	public static DatabaseModel create(final Connection connection) throws SQLException {
		final DatabaseMetaData meta = connection.getMetaData();
		LOGGER.trace("DatabaseName    : {}", meta.getDatabaseProductName());
		LOGGER.trace("DatabaseVersion : {}", meta.getDatabaseProductVersion());
		LOGGER.trace("DriverName      : {}", meta.getDriverName());
		LOGGER.trace("DriverVersion   : {}", meta.getDriverVersion());

		final String name = meta.getDatabaseProductName().toUpperCase();

		DatabaseModel model = null;
		if (-1 != name.indexOf("ORACLE")) {
			model = new OracleModel(connection);
		} else if (-1 != name.indexOf("DB2")) {
			model = new DB2Model(connection);
		} else if (-1 != name.indexOf("POSTGRESQL")) {
			model = new PostgreSQLModel(connection);
		} else {
			LOGGER.error("Unsupported database model.[{}]", name);
		}
		return model;
	}
}
