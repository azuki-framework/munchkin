/**
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
package org.azkwf.munchkin;

import org.azkwf.munchkin.component.MunchkinFrame;

import com.zaxxer.hikari.HikariDataSource;

/**
 *
 * @author Kawakicchi
 */
public class Munchkin {

	public static void main(final String[] args) {

		Munchkin.getInstance().initialize();

		MunchkinFrame frm = new MunchkinFrame();
		frm.setVisible(true);

		Munchkin.getInstance().destroy();
	}

	private static final Munchkin INSTANCE = new Munchkin();

	private HikariDataSource ds;

	private Munchkin() {

	}

	public static Munchkin getInstance() {
		return INSTANCE;
	}

	public void initialize() {
		ds = new HikariDataSource();
		ds.setDriverClassName("oracle.jdbc.OracleDriver");

		ds.setJdbcUrl("jdbc:oracle:thin:@(DESCRIPTION =(SOURCE_ROUTE=yes)(ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 1521))(CONNECT_DATA =(SERVICE_NAME = XE)))");
		ds.setUsername("XXX");
		ds.setPassword("XXX");

	}

	public void destroy() {

	}

	public HikariDataSource getDatasource() {
		return ds;
	}

}
