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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class StatusBar extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = -5638113732514695773L;

	private final JLabel lblMessage;

	private final JProgressBar progressBar;

	public StatusBar() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, 30));
		setBorder(new EmptyBorder(2, 2, 2, 2));

		lblMessage = new JLabel("");
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(200, 0));

		add(BorderLayout.CENTER, lblMessage);
		add(BorderLayout.EAST, progressBar);
	}
}
