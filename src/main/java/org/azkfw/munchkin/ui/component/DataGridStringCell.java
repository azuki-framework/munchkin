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
package org.azkfw.munchkin.ui.component;

import java.awt.Insets;

import javax.swing.JTextPane;
import javax.swing.text.StyledEditorKit;

/**
 *
 * @author Kawakicchi
 */
public class DataGridStringCell extends JTextPane {

	/** serialVersionUID */
	private static final long serialVersionUID = -3719195992712551281L;

	public DataGridStringCell() {
		this(null);
	}

	public DataGridStringCell(final StyledEditorKit kit) {
		setEditable(false);
		setMargin(new Insets(2, 2, 2, 2));
		if (null != kit) {
			setEditorKit(kit);
		}
	}
}
