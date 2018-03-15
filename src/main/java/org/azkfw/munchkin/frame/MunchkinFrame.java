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
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.azkfw.munchkin.Munchkin;
import org.azkfw.munchkin.component.ConsolePanel;
import org.azkfw.munchkin.component.DBConditionPanel;
import org.azkfw.munchkin.component.DBConditionPanelListener;
import org.azkfw.munchkin.component.DBObjectDetailPanel;
import org.azkfw.munchkin.component.DBObjectListPanel;
import org.azkfw.munchkin.component.DBObjectListPanelListener;
import org.azkfw.munchkin.component.DataGridPanel;
import org.azkfw.munchkin.component.SQLEditorPanel;
import org.azkfw.munchkin.component.SQLEditorPanelListener;
import org.azkfw.munchkin.component.StatusBar;
import org.azkfw.munchkin.database.model.DatabaseModel;
import org.azkfw.munchkin.database.model.DatabaseModelFactory;
import org.azkfw.munchkin.database.model.MockDatabaseModel;
import org.azkfw.munchkin.database.model.ObjectDetailEntity;
import org.azkfw.munchkin.database.model.ObjectEntity;
import org.azkfw.munchkin.database.model.SchemaEntity;
import org.azkfw.munchkin.database.model.TypeEntity;
import org.azkfw.munchkin.dialog.DatasourceDialog;
import org.azkfw.munchkin.dialog.DatasourceDialogListener;
import org.azkfw.munchkin.dialog.DatasourceListDialog;
import org.azkfw.munchkin.dialog.VersionDialog;
import org.azkfw.munchkin.entity.DatasourceEntity;
import org.azkfw.munchkin.entity.MunchkinEntity;
import org.azkfw.munchkin.entity.SQLHistoryEntity;
import org.azkfw.munchkin.util.MunchkinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * このクラスは、Munchkinのメインフレームクラスです。
 * 
 * @author Kawakicchi
 */
public class MunchkinFrame extends AbstractMunchkinFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = 4632993014738209545L;

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MunchkinFrame.class);

	private static final int TAB_CONSOLE = 0;
	private static final int TAB_DATAGRID = 1;

	private final MunchkinEntity config;

	private DatabaseModel model;
	private Connection connection;

	private final JPanel toolBar;
	private final StatusBar statusBar;

	private final JSplitPane spltMain;
	private final JSplitPane spltLeft;
	private final JSplitPane spltRight;

	private final DBConditionPanel pnlCondition;
	private final DBObjectListPanel pnlObjectList;
	private final DBObjectDetailPanel pnlObjectDetail;
	private final SQLEditorPanel pnlSqlEditor;
	private final JTabbedPane tabBottom;
	private final ConsolePanel pnlConsole;
	private final DataGridPanel pnlDataGrid;

	public MunchkinFrame() {
		setTitle("Munchkin");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());

		config = Munchkin.getInstance().getConfig();

		model = new MockDatabaseModel();

		pnlCondition = new DBConditionPanel();
		pnlObjectList = new DBObjectListPanel();
		pnlObjectDetail = new DBObjectDetailPanel();
		pnlSqlEditor = new SQLEditorPanel();
		pnlConsole = new ConsolePanel();
		pnlDataGrid = new DataGridPanel();

		tabBottom = new JTabbedPane();
		tabBottom.add("コンソール", pnlConsole);
		tabBottom.add("データ", pnlDataGrid);

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

		pnlCondition.addDBConditionPanelListener(new DBConditionPanelListener() {
			@Override
			public void dbConditionPanelChengedType(final TypeEntity type) {
				final SchemaEntity schema = pnlCondition.getSelectSchema();
				getObjectList(schema, type);
			}

			@Override
			public void dbConditionPanelChengedSchema(final SchemaEntity schema) {
				final TypeEntity type = pnlCondition.getSelectType();
				getObjectList(schema, type);
			}
		});
		pnlObjectList.addDBObjectListPanelListener(new DBObjectListPanelListener() {
			@Override
			public void dbObjectListPanelChengedObject(final ObjectEntity object) {
				getDetail(object);
			}
		});
		pnlSqlEditor.addSQLEditorPanelListener(new SQLEditorPanelListener() {
			@Override
			public void sqlEditorPanelExecSQL(final String sql) {
				// TODO: スレッド化
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					String row = sql.toLowerCase().trim();
					if (row.startsWith("select")) {
						ps = connection.prepareStatement(sql);
						rs = ps.executeQuery();
						int cnt = pnlDataGrid.setData(rs);
						info("%d件 表示しました。", cnt);
						tabBottom.setSelectedIndex(TAB_DATAGRID);

						config.addHistorySql(new SQLHistoryEntity(sql));

					} else if (row.startsWith("insert")) {
						ps = connection.prepareStatement(sql);
						int cnt = ps.executeUpdate();
						info("%d件 登録しました。", cnt);

						config.addHistorySql(new SQLHistoryEntity(sql));

					} else if (row.startsWith("update")) {
						ps = connection.prepareStatement(sql);
						int cnt = ps.executeUpdate();
						info("%d件 更新しました。", cnt);

						config.addHistorySql(new SQLHistoryEntity(sql));

					} else if (row.startsWith("delete")) {
						ps = connection.prepareStatement(sql);
						int cnt = ps.executeUpdate();
						info("%d件 削除しました。", cnt);

						config.addHistorySql(new SQLHistoryEntity(sql));

					} else {
						LOGGER.error("Unknown sql.");
					}

				} catch (SQLException ex) {
					fatal("エラーが発生しました。[%s]", ex.getMessage());
					try {
						connection.rollback();
						info("ロールバックしました。");
					} catch (SQLException ex2) {

					}
					LOGGER.error("Execute sql error.", ex);
				} finally {
					if (null != rs) {
						try {
							rs.close();
						} catch (SQLException ex) {
							LOGGER.warn("ResultSet close error.", ex);
						}
					}
					if (null != ps) {
						try {
							ps.close();
						} catch (SQLException ex) {
							LOGGER.warn("PreparedStatement close error.", ex);
						}
					}
				}
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(final WindowEvent e) {
				doOpened();
			}

			@Override
			public void windowClosing(final WindowEvent e) {
				exit();
			}

			@Override
			public void windowClosed(final WindowEvent e) {
				doClosed();
			}
		});

		final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final Rectangle rect = env.getMaximumWindowBounds();
		rect.height -= 100;
		setBounds(rect);
	}

	public void exit() {
		if (null != connection) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
				LOGGER.warn("Connection rollback error.", ex);
			}
			try {
				connection.close();
			} catch (SQLException ex) {
				LOGGER.warn("Connection close error.", ex);
			}
		}

		dispose();
	}

	private void connect(final DatasourceEntity datasource) {
		Connection c = null;
		try {
			Class.forName(datasource.getDriver());
			c = DriverManager.getConnection(datasource.getUrl(), datasource.getUser(), datasource.getPassword());
			c.setAutoCommit(false);

			model = DatabaseModelFactory.create(c);
			connection = c;

			info("接続しました。[%s]", datasource.getName());

		} catch (ClassNotFoundException ex) {
			fatal("接続に失敗しました。[%s]", ex.getMessage());
			LOGGER.error("Driver class not found.", ex);
		} catch (SQLException ex) {
			fatal("接続に失敗しました。[%s]", ex.getMessage());
			LOGGER.error("Database connection error.", ex);
		}
	}

	private void refreshCondition() {
		try {
			final List<SchemaEntity> schemas = model.getSchemaList();
			final List<TypeEntity> types = model.getTypeList();

			pnlCondition.refresh(schemas, types);

			final SchemaEntity schema = model.getDefaultSchema();
			pnlCondition.setSelectSchema(schema);

		} catch (SQLException ex) {
			LOGGER.error("", ex);
		}
	}

	private void getObjectList(final SchemaEntity schema, final TypeEntity type) {
		try {
			final List<ObjectEntity> objects = model.getObjectList(schema, type);
			pnlObjectList.setObjectList(objects);
		} catch (SQLException ex) {
			LOGGER.error("", ex);
		}
	}

	private void getDetail(final ObjectEntity object) {
		try {
			final ObjectDetailEntity detail = model.getObjectDetail(object);
			pnlObjectDetail.setObjectDetail(detail);
		} catch (SQLException ex) {
			LOGGER.error("", ex);
		}
	}

	private void doOpened() {
		spltMain.setDividerLocation(spltMain.getWidth() / 4);
		spltLeft.setDividerLocation(spltLeft.getHeight() / 2);
		spltRight.setDividerLocation(spltRight.getHeight() / 2);

		pnlSqlEditor.setText(config.getSqlTextEditorText());

		if (MunchkinUtil.isEmpty(config.getDatasources())) {
			final DatasourceDialog dlg = new DatasourceDialog(this);
			dlg.addDatasourceDialogListener(new DatasourceDialogListener() {
				@Override
				public void datasourceDialogOK(final DatasourceDialog dialog, final DatasourceEntity datasource) {
					config.addDatasource(datasource);
					connect(datasource);
					refreshCondition();
				}

				@Override
				public void datasourceDialogCancel(final DatasourceDialog dialog) {
				}
			});
			dlg.setVisible(true);
		} else {
			final DatasourceEntity datasource = config.getDatasources().get(0);
			connect(datasource);
			refreshCondition();
		}
	}

	private void doClosed() {
		config.setSqlTextEditorText(pnlSqlEditor.getText());
	}

	public void info(final String message) {
		pnlConsole.info(message);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	public void info(final String message, final Object... objs) {
		pnlConsole.info(message, objs);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	public void fatal(final String message) {
		pnlConsole.fatal(message);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	public void fatal(final String message, final Object... objs) {
		pnlConsole.fatal(message, objs);
		tabBottom.setSelectedIndex(TAB_CONSOLE);
	}

	@Override
	protected void doMenuFileConnection() {
		final List<DatasourceEntity> datasources = new ArrayList<DatasourceEntity>();
		config.getDatasources().forEach(d -> datasources.add(new DatasourceEntity(d)));

		final DatasourceListDialog dlg = new DatasourceListDialog(this, datasources);
		dlg.setVisible(true);
	}

	@Override
	protected void doMenuFileExit() {
		exit();
	}

	@Override
	protected void doMenuHelpVersion() {
		final VersionDialog dlg = new VersionDialog(this);
		dlg.setVisible(true);
	}
}
