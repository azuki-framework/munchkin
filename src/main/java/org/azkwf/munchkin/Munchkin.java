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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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

		frm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				Munchkin.getInstance().destroy();
			}
		});
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
		ds.setConnectionTimeout(60 * 1000);
		ds.setMaximumPoolSize(1);

		File file = new File("datasource.properties");
		if (file.isFile()) {

			Properties p = new Properties();

			FileInputStream stream = null;
			try {
				stream = new FileInputStream(file);
				p.load(stream);

				ds.setDriverClassName(p.getProperty("datasource.driver"));
				ds.setJdbcUrl(p.getProperty("datasource.url"));
				ds.setUsername(p.getProperty("datasource.user"));
				ds.setPassword(p.getProperty("datasource.password"));

			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (null != stream) {
					try {
						stream.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}

	}

	public void destroy() {
		System.out.println("destory");
		ds.close();
	}

	public HikariDataSource getDatasource() {
		return ds;
	}

}
