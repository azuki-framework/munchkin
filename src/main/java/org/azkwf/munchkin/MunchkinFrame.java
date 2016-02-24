/**
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
package org.azkwf.munchkin;

import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * @author Kawakicchi
 *
 */
public class MunchkinFrame extends JFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = 7576477907765452922L;

	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu menuHelp;
	private JMenuItem menuFileExit;
	
	private DBObjectPanel pnlObject;

	public MunchkinFrame() {
		setTitle("Munchkin");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		pnlObject = new DBObjectPanel(new TestDBObjectModel());
		pnlObject.setLocation(0, 0);
		add(pnlObject);
		
		initMenu();
		addListener();
		
		setBounds(10, 10, 800, 600);
	}

	private void doResize() {
		Insets insets = getInsets();
		int width = getWidth() - (insets.left + insets.right);
		int height = getHeight() - (insets.top + insets.bottom) - menuBar.getHeight();
	
		pnlObject.setSize(width, height);
	}

	private void initMenu() {
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		menuFile = new JMenu("File");
		menuBar.add(menuFile);

		menuFileExit = new JMenuItem("Exit");
		menuFile.add(menuFileExit);

		menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);
	}

	private void addListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
			}
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
			@Override
			public void windowClosed(WindowEvent e) {
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				doResize();
			}
			@Override
			public void componentResized(ComponentEvent e) {
				doResize();
			}
		});
	}
}
