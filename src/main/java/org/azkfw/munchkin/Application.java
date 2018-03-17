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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import org.azkfw.munchkin.ui.frame.MunchkinFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * このクラスは、Munchkinのアプリケーションクラスです。
 * 
 * @author Kawakicchi
 */
public class Application {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	/**
	 * メイン関数
	 *
	 * @param args 引数
	 */
	public static void main(final String[] args) throws Exception {

		String strBaseDir = "./";
		for (int i = 0; i < args.length; i++) {
			String s = args[i];
			if ("--baseDir".equals(s)) {
				if (i + 1 < args.length) {
					strBaseDir = args[++i];
				}
			}
		}

		LOGGER.info("BaseDir : {}", strBaseDir);

		final File baseDir = new File(strBaseDir);

		System.setProperty("apple.awt.application.name", "Munchkin");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Munchkin");
		System.setProperty("com.apple.macos.useScreenMenuBar", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		final Munchkin munchkin = Munchkin.getInstance();
		munchkin.load(baseDir);

		final MunchkinFrame frm = new MunchkinFrame();

		frm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(final WindowEvent e) {
			}

			@Override
			public void windowClosed(final WindowEvent e) {
				munchkin.store();
			}
		});

		frm.setVisible(true);
	}
}
