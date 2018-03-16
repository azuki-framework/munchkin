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
package org.azkfw.munchkin.ui;

import java.awt.Font;

/**
 *
 * @author Kawakicchi
 */
public class FontHelper {

	private static final String FONT_NAME_MONOSPACED;

	static {
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			FONT_NAME_MONOSPACED = "ＭＳ ゴシック";
		} else if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			FONT_NAME_MONOSPACED = "Osaka-Mono";
		} else {
			FONT_NAME_MONOSPACED = Font.MONOSPACED;
		}
	}

	public static String getMonospacedFontName() {
		return FONT_NAME_MONOSPACED;
	}
}
