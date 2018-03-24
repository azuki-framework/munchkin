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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.azkfw.munchkin.label.Label;
import org.azkfw.munchkin.ui.component.StatusBar;
import org.azkfw.munchkin.ui.panel.ConsolePanel;
import org.azkfw.munchkin.ui.panel.DBConditionPanel;
import org.azkfw.munchkin.ui.panel.DBObjectDetailPanel;
import org.azkfw.munchkin.ui.panel.DBObjectListPanel;
import org.azkfw.munchkin.ui.panel.DataGridPanel;
import org.azkfw.munchkin.ui.panel.SQLEditorPanel;
import org.azkfw.munchkin.ui.panel.SqlHistoryPanel;

/**
 *
 * @author Kawakicchi
 *
 */
public abstract class AbstractMunchkinFrame extends JFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = 4043598093275010649L;

	protected static final int TAB_CONSOLE = 0;
	protected static final int TAB_DATAGRID = 1;
	protected static final int TAB_SQLHISTORY = 2;

	private final JMenuBar menuBar;
	private final JMenu menuFile;
	private final JMenuItem menuFileConnection;
	private final JMenuItem menuFileDatasource;
	private final JMenuItem menuFileExit;
	private final JMenu menuSql;
	private final JMenuItem menuSqlExecute;
	private final JMenu menuTool;
	private final JMenuItem menuToolExport;
	private final JMenu menuHelp;
	private final JMenuItem menuHelpVersion;

	protected final JPanel toolBar;
	protected final StatusBar statusBar;
	protected final DBConditionPanel pnlCondition;
	protected final DBObjectListPanel pnlObjectList;
	protected final DBObjectDetailPanel pnlObjectDetail;
	protected final SQLEditorPanel pnlSqlEditor;
	protected final JTabbedPane tabBottom;
	protected final ConsolePanel pnlConsole;
	protected final DataGridPanel pnlDataGrid;
	protected final SqlHistoryPanel pnlSqlHistory;

	private final JSplitPane spltMain;
	private final JSplitPane spltLeft;
	private final JSplitPane spltRight;

	public AbstractMunchkinFrame() {
		setTitle("Munchkin");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());

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

		menuSql = new JMenu(Label.MENU_SQL.toString());
		menuSqlExecute = new JMenuItem(Label.MENU_SQL_EXECUTE.toString());
		menuSql.add(menuSqlExecute);
		menuBar.add(menuSql);

		menuTool = new JMenu(Label.MENU_TOOL.toString());
		menuToolExport = new JMenuItem(Label.MENU_TOOL_EXPORT.toString());
		menuTool.add(menuToolExport);
		menuBar.add(menuTool);

		menuHelp = new JMenu(Label.MENU_HELP.toString());
		menuHelpVersion = new JMenuItem(Label.MENU_HELP_VERSION.toString());
		menuHelp.add(menuHelpVersion);
		menuBar.add(menuHelp);

		setJMenuBar(menuBar);

		pnlCondition = new DBConditionPanel();
		pnlObjectList = new DBObjectListPanel();
		pnlObjectDetail = new DBObjectDetailPanel();
		pnlSqlEditor = new SQLEditorPanel();
		pnlConsole = new ConsolePanel();
		pnlDataGrid = new DataGridPanel();
		pnlSqlHistory = new SqlHistoryPanel();

		tabBottom = new JTabbedPane();
		tabBottom.add("コンソール", pnlConsole);
		tabBottom.add("データ", pnlDataGrid);
		tabBottom.add("実行履歴", pnlSqlHistory);

		final JPanel pnlOb = new JPanel();
		pnlOb.setLayout(new BorderLayout());
		pnlOb.add(BorderLayout.NORTH, pnlCondition);
		pnlOb.add(BorderLayout.CENTER, pnlObjectList);

		spltLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		spltLeft.setTopComponent(pnlOb);
		spltLeft.setBottomComponent(pnlObjectDetail);

		spltRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		spltRight.setTopComponent(pnlSqlEditor);
		spltRight.setBottomComponent(tabBottom);

		spltMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		spltMain.setLeftComponent(spltLeft);
		spltMain.setRightComponent(spltRight);
		add(BorderLayout.CENTER, spltMain);

		toolBar = new JPanel();
		toolBar.setPreferredSize(new Dimension(0, 30));
		add(BorderLayout.NORTH, toolBar);
		statusBar = new StatusBar();
		add(BorderLayout.SOUTH, statusBar);

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
		menuSqlExecute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doMenuSqlExecute();
			}
		});
		menuHelpVersion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doMenuHelpVersion();
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(final WindowEvent e) {
				spltMain.setDividerLocation(spltMain.getWidth() / 4);
				spltLeft.setDividerLocation(spltLeft.getHeight() / 2);
				spltRight.setDividerLocation(spltRight.getHeight() / 2);
			}
		});
	}

	protected final void setStatusMessage(final String message) {
		statusBar.setMessage(message);
	}

	protected final void setStatusMessage(final String format, final Object... args) {
		statusBar.setMessage(format, args);
	}

	/**
	 * ログ[FATAL]を出力する。
	 *
	 * @param message メッセージ
	 */
	protected final void fatal(final String message) {
		pnlConsole.fatal(message);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	/**
	 * ログ[FATAL]を出力する。
	 *
	 * @param format フォーマット
	 * @param objs パラメータ
	 */
	protected final void fatal(final String format, final Object... objs) {
		pnlConsole.fatal(format, objs);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	/**
	 * ログ[WARN]を出力する。
	 *
	 * @param message メッセージ
	 */
	protected final void warn(final String message) {
		pnlConsole.warn(message);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	/**
	 * ログ[WARN]を出力する。
	 *
	 * @param format フォーマット
	 * @param objs パラメータ
	 */
	protected final void warn(final String format, final Object... objs) {
		pnlConsole.warn(format, objs);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	/**
	 * ログ[INFO]を出力する。
	 *
	 * @param message メッセージ
	 */
	protected final void info(final String message) {
		pnlConsole.info(message);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	/**
	 * ログ[INFO]を出力する。
	 *
	 * @param format フォーマット
	 * @param objs パラメータ
	 */
	protected final void info(final String format, final Object... objs) {
		pnlConsole.info(format, objs);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	/**
	 * ログ[DEBUG]を出力する。
	 *
	 * @param message メッセージ
	 */
	protected final void debug(final String message) {
		pnlConsole.debug(message);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	/**
	 * ログ[DEBUG]を出力する。
	 *
	 * @param format フォーマット
	 * @param objs パラメータ
	 */
	protected final void debug(final String format, final Object... objs) {
		pnlConsole.debug(format, objs);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	/**
	 * ログ[TRACE]を出力する。
	 *
	 * @param message メッセージ
	 */
	protected final void trace(final String message) {
		pnlConsole.trace(message);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	/**
	 * ログ[TRACE]を出力する。
	 *
	 * @param format フォーマット
	 * @param objs パラメータ
	 */
	protected final void trace(final String format, final Object... objs) {
		pnlConsole.trace(format, objs);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	protected abstract void doMenuFileConnection();

	protected abstract void doMenuFileDatasource();

	protected abstract void doMenuFileExit();

	protected abstract void doMenuSqlExecute();

	protected abstract void doMenuHelpVersion();
}
