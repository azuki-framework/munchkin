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
package org.azkfw.munchkin.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

/**
 * このクラスは、設定ダイアログクラスです。
 *
 * @author Kawakicchi
 */
public class PreferencesDialog extends JDialog {

	/** serialVersionUID */
	private static final long serialVersionUID = -1318052522172404201L;

	/** listener list */
	private final List<PreferencesDialogListener> listeners;

	/**
	 * コンストラクタ
	 */
	public PreferencesDialog() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		listeners = new ArrayList<PreferencesDialogListener>();

		setSize(600, 800);
		// Cent
	}

	/**
	 * 設定ダイアログリスナーを追加する。
	 *
	 * @param l リスナー
	 */
	public synchronized void addPreferencesDialogListener(final PreferencesDialogListener l) {
		listeners.add(l);
	}
}
