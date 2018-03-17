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
package org.azkfw.munchkin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.bind.JAXB;

import org.azkfw.munchkin.entity.MunchkinEntity;
import org.azkfw.munchkin.entity.SQLHistoryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * このクラスは、Munchkinクラスです。
 * 
 * @author Kawakicchi
 */
public class Munchkin {

	/** Instance */
	private static final Munchkin INSTANCE = new Munchkin();
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(Munchkin.class);

	/** ベースディレクトリ */
	private File baseDir;
	/** */
	private MunchkinEntity config;

	/**
	 * コンストラクタ
	 */
	private Munchkin() {
	}

	/**
	 * インスタンスを取得する。
	 *
	 * @return インスタンス
	 */
	public static Munchkin getInstance() {
		return INSTANCE;
	}

	/**
	 * 設定を読み込む。
	 *
	 * @param baseDir ベースディレクトリ
	 * @return 結果
	 */
	public boolean load(final File baseDir) {
		this.baseDir = baseDir;

		// Load driver jar file
		final File driverDir = Paths.get(this.baseDir.getAbsolutePath(), "driver").toFile();
		if (driverDir.isDirectory()) {
			final File[] files = driverDir.listFiles();
			for (File file : files) {
				try {
					LOGGER.info("Load jar file.[{}]", file.getAbsolutePath());
					final ClassLoader loader = ClassLoader.getSystemClassLoader();
					final Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
					m.setAccessible(true);	//protectedメソッドにアクセス許可
					m.invoke(loader, file.toURI().toURL());
				} catch (MalformedURLException ex) {
					LOGGER.error("Driver library load error.", ex);
				} catch (Exception ex) {
					LOGGER.error("Driver library load error.", ex);
				}
			}
		}

		final File file = getConfigFile();
		if (file.isFile()) {
			LOGGER.debug("Load config file.[{}]", file.getAbsolutePath());
			config = JAXB.unmarshal(file, MunchkinEntity.class);
		} else {
			config = new MunchkinEntity();
		}

		Collections.sort(config.getHistorySqls(), new Comparator<SQLHistoryEntity>() {
			@Override
			public int compare(final SQLHistoryEntity o1, final SQLHistoryEntity o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});

		return true;
	}

	/**
	 * 設定を書き込む。
	 *
	 * @return 結果
	 */
	public boolean store() {
		final File file = getConfigFile();

		LOGGER.debug("Store config file.");
		JAXB.marshal(config, file);

		return true;
	}

	public MunchkinEntity getConfig() {
		return config;
	}

	private File getConfigFile() {
		return Paths.get(baseDir.getAbsolutePath(), "conf", "config.xml").toFile();
	}
}
