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
package org.azkfw.munchkin.frame;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;

import org.azkfw.munchkin.component.DBObjectPanel;
import org.azkfw.munchkin.component.StatusBar;
import org.azkfw.munchkin.dialog.DatasourceConfigurationListDialog;
import org.azkfw.munchkin.entity.DatasourceEntity;
import org.azkfw.munchkin.message.Label;

/**
 * @author Kawakicchi
 */
public class MunchkinFrame extends JFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = 4632993014738209545L;

	private final StatusBar statusBar;

	private final JMenuBar menuBar;
	private final JMenu menuFile;
	private final JMenuItem menuFileConnection;
	private final JMenuItem menuFileExit;
	private final JMenu menuHelp;

	private final JSplitPane pnlSplit;
	private final DBObjectPanel pnlObject;

	public MunchkinFrame() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());

		menuBar = new JMenuBar();
		menuFile = new JMenu(Label.MENU_FILE.toString());
		menuBar.add(menuFile);
		menuFileConnection = new JMenuItem(Label.MENU_FILE_CONNECTION.toString());
		menuFile.add(menuFileConnection);
		menuFile.addSeparator();
		menuFileExit = new JMenuItem(Label.MENU_FILE_EXIT.toString());
		menuFile.add(menuFileExit);
		menuHelp = new JMenu(Label.MENU_HELP.toString());
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);

		pnlObject = new DBObjectPanel();

		pnlSplit = new JSplitPane();
		pnlSplit.setLeftComponent(pnlObject);
		add(BorderLayout.CENTER, pnlSplit);

		statusBar = new StatusBar();
		add(BorderLayout.SOUTH, statusBar);

		menuFileConnection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doMenuFileConnection();
			}
		});
		menuFileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doMenuFileExit();
			}
		});
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(final WindowEvent e) {
			}

			@Override
			public void windowIconified(final WindowEvent e) {
			}

			@Override
			public void windowDeiconified(final WindowEvent e) {
			}

			@Override
			public void windowDeactivated(final WindowEvent e) {
			}

			@Override
			public void windowClosing(final WindowEvent e) {
				exit();
			}

			@Override
			public void windowClosed(final WindowEvent e) {
			}

			@Override
			public void windowActivated(final WindowEvent e) {
			}
		});

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle rect = env.getMaximumWindowBounds();
		setBounds(rect);
	}

	public void exit() {
		dispose();
	}

	private void openConnection() {
		List<DatasourceEntity> datasources = new ArrayList<DatasourceEntity>();
		for (int i = 0; i < 5; i++) {
			DatasourceEntity ds = new DatasourceEntity();
			ds.setName(String.format("サンプル%02d", (i + 1)));
			ds.setUser(String.format("tester%02d", (i + 1)));
			ds.setPassword("pass");
			ds.setDriver("org.postgresql.Driver");
			ds.setUrl("jdbc:postgresql://locahost/db_test");
			datasources.add(ds);
		}

		DatasourceConfigurationListDialog dlg = new DatasourceConfigurationListDialog(this);
		dlg.setConfiguration(datasources);
		dlg.setVisible(true);
	}

	private void doMenuFileConnection() {
		openConnection();
	}

	private void doMenuFileExit() {
		exit();
	}
}
