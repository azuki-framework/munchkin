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
package org.azkfw.munchkin.message;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Label {
	/** */
	DATASOURCE_NAME,
	/** */
	DATASOURCE_MEMO,
	/** s */
	DATASOURCE_USER,
	/** */
	DATASOURCE_PASSWORD,
	/** */
	DATASOURCE_DRIVER,
	/** */
	DATASOURCE_URL,
	/** */
	BUTTON_OK,
	/** */
	BUTTON_CANCEL,
	/** */
	BUTTON_CREATE,
	/** */
	BUTTON_MODIFY,
	/** */
	BUTTON_DELETE,
	/** */
	MENU_FILE,
	/** */
	MENU_FILE_CONNECTION,
	/** */
	MENU_FILE_EXIT,
	/** */
	MENU_HELP,
	/** */
	MENU_HELP_VERSION,
	/** */
	TITLE_DATASOURCE_LIST,
	/** */
	TITLE_VERSION,
	/** */
	NONE;

	@Override
	public String toString() {
		try {
			String name = name().toLowerCase().replaceAll("_", ".");
			return ResourceBundle.getBundle(this.getClass().getName(), Locale.getDefault()).getString(name);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
