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
package org.azkfw.munchkin.ui.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.azkfw.munchkin.message.Label;

/**
 *
 * @author Kawakicchi
 *
 */
public abstract class AbstractMunchkinFrame extends JFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = 4043598093275010649L;

	private final JMenuBar menuBar;
	private final JMenu menuFile;
	private final JMenuItem menuFileConnection;
	private final JMenuItem menuFileDatasource;
	private final JMenuItem menuFileExit;
	private final JMenu menuTool;
	private final JMenuItem menuToolExport;
	private final JMenu menuHelp;
	private final JMenuItem menuHelpVersion;

	public AbstractMunchkinFrame() {

		menuBar = new JMenuBar();

		menuFile = new JMenu(Label.MENU_FILE.toString());
		menuFileConnection = new JMenuItem(Label.MENU_FILE_CONNECTION.toString());
		menuFile.add(menuFileConnection);
		menuFileDatasource = new JMenuItem(Label.MENU_FILE_DATASOURCE.toString());
		menuFile.add(menuFileDatasource);
		menuFile.addSeparator();
		menuFileExit = new JMenuItem(Label.MENU_FILE_EXIT.toString());
		menuFile.add(menuFileExit);
		menuBar.add(menuFile);

		menuTool = new JMenu(Label.MENU_TOOL.toString());
		menuToolExport = new JMenuItem(Label.MENU_TOOL_EXPORT.toString());
		menuTool.add(menuToolExport);
		menuBar.add(menuTool);

		menuHelp = new JMenu(Label.MENU_HELP.toString());
		menuHelpVersion = new JMenuItem(Label.MENU_HELP_VERSION.toString());
		menuHelp.add(menuHelpVersion);
		menuBar.add(menuHelp);

		setJMenuBar(menuBar);

		menuFileConnection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doMenuFileConnection();
			}
		});
		menuFileDatasource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doMenuFileDatasource();
			}
		});
		menuFileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doMenuFileExit();
			}
		});
		menuHelpVersion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doMenuHelpVersion();
			}
		});
	}

	protected abstract void doMenuFileConnection();

	protected abstract void doMenuFileDatasource();

	protected abstract void doMenuFileExit();

	protected abstract void doMenuHelpVersion();
}
