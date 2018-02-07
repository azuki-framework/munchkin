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
package org.azkfw.munchkin.dialog;

import java.util.List;

/**
 * このインターフェースは、設定ダイアログリスナー機能を定義するインターフェースです。
 * 
 * @author Kawakicchi
 * @param <ENTITY> 設定情報
 */
public class ConfigurationListDialogAdapter<ENTITY> implements ConfigurationListDialogListener<ENTITY> {

	@Override
	public void configurationListDialogCheck(final ConfigurationListDialogEvent<ENTITY> event,
			final List<ENTITY> entites) {

	}

	@Override
	public void configurationListDialogOk(final ConfigurationListDialogEvent<ENTITY> event, final List<ENTITY> entity) {

	}

	@Override
	public void configurationListDialogCancel(final ConfigurationListDialogEvent<ENTITY> event) {

	}

}
