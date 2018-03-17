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
 *
 * @author Kawakicchi
 *
 */
public enum DatabaseDriver {
	/** */
	MySQL("MySQL", "com.mysql.jdbc.Driver", "jdbc:mysql://[ホスト名]:[ポート番号]/[データベース名]"),
	/** */
	PostgreSQL("PostgreSQL", "org.postgresql.Driver", "jdbc:postgresql://[ホスト名]:[ポート番号]/[データベース名]"),
	/** */
	Oracle("Oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@[ホスト名]:[ポート番号]:[SID]"),
	/** */
	DB2("DB2", "com.ibm.db2.jcc.DB2Driver", "jdbc:db2://[ホスト名]:[ポート番号]/[データベース名]"),
	/** */
	SQLServer("SQLServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver",
			"jdbc:sqlserver://[ホスト名]:[ポート番号];databaseName=[データベース名];");

	private final String label;
	private final String driver;
	private final String template;

	private DatabaseDriver(final String label, final String driver, final String template) {
		this.label = label;
		this.driver = driver;
		this.template = template;
	}

	public String getLabel() {
		return label;
	}

	public String getDriver() {
		return driver;
	}

	public String getTemplate() {
		return template;
	}

	@Override
	public String toString() {
		return String.format("%s [%s]", label, driver);
	}

	public static DatabaseDriver getDriver(final String driver) {
		DatabaseDriver d = null;
		for (DatabaseDriver t : DatabaseDriver.values()) {
			if (t.driver.equals(driver)) {
				d = t;
				break;
			}
		}
		return d;
	}
}
