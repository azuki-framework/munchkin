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
import java.util.Date;
import java.util.List;

import org.azkfw.munchkin.Munchkin;
import org.azkfw.munchkin.database.model.DatabaseModel;
import org.azkfw.munchkin.database.model.DatabaseModelFactory;
import org.azkfw.munchkin.database.model.entity.ObjectDetailEntity;
import org.azkfw.munchkin.database.model.entity.ObjectEntity;
import org.azkfw.munchkin.database.model.entity.SchemaEntity;
import org.azkfw.munchkin.database.model.entity.TypeEntity;
import org.azkfw.munchkin.entity.DatasourceEntity;
import org.azkfw.munchkin.entity.MunchkinConfigEntity;
import org.azkfw.munchkin.entity.MunchkinDatasourceEntity;
import org.azkfw.munchkin.entity.MunchkinHistoryEntity;
import org.azkfw.munchkin.entity.SQLHistoryEntity;
import org.azkfw.munchkin.task.TaskManager;
import org.azkfw.munchkin.ui.dialog.DatasourceDialog;
import org.azkfw.munchkin.ui.dialog.DatasourceDialogListener;
import org.azkfw.munchkin.ui.dialog.DatasourceListDialog;
import org.azkfw.munchkin.ui.dialog.DatasourcesDialog;
import org.azkfw.munchkin.ui.dialog.DatasourcesDialogListener;
import org.azkfw.munchkin.ui.dialog.ExportDialog;
import org.azkfw.munchkin.ui.dialog.VersionDialog;
import org.azkfw.munchkin.ui.panel.DBConditionPanelListener;
import org.azkfw.munchkin.ui.panel.DBObjectDetailPanelListener;
import org.azkfw.munchkin.ui.panel.DBObjectListPanelListener;
import org.azkfw.munchkin.ui.panel.DataGridPanelListener;
import org.azkfw.munchkin.ui.panel.SQLEditorPanelListener;
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

	private final MunchkinConfigEntity config;
	private final MunchkinDatasourceEntity datasource;
	private final MunchkinHistoryEntity history;

	private final TaskManager manager;

	private DatabaseModel model;
	private Connection connection;

	private Boolean busy = Boolean.FALSE;

	public MunchkinFrame() {

		config = Munchkin.getInstance().getConfig();
		datasource = Munchkin.getInstance().getDatasource();
		history = Munchkin.getInstance().getHistory();

		manager = new TaskManager();
		manager.start();

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

			@Override
			public void dbObjectListPanelDoubleClick(final String value) {
				pnlSqlEditor.insert(value);
			}
		});
		pnlObjectDetail.addDBObjectDetailListener(new DBObjectDetailPanelListener() {
			@Override
			public void dbObjectDetailPanelDoubleClick(final String value) {
				pnlSqlEditor.insert(value);
			}
		});
		pnlSqlEditor.addSQLEditorPanelListener(new SQLEditorPanelListener() {
			@Override
			public void sqlEditorPanelExecSQL(final String sql) {
				execute(sql);
			}
		});
		pnlDataGrid.addDataGridPanelListener(new DataGridPanelListener() {
			@Override
			public void dataGridPanelDoubleClick(final Object value) {
				pnlSqlEditor.insert(value.toString());
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
		rect.height -= 140;
		setBounds(rect);
	}

	public void connect(final DatasourceEntity datasource) {
		synchronized (busy) {
			if (busy) {
				return;
			}
			busy = Boolean.TRUE;
			setStatusMessage("データベースへ接続中...[%s]", datasource.getName());
		}

		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Connection c = null;
				try {
					Class.forName(datasource.getDriver());
					c = DriverManager.getConnection(datasource.getUrl(), datasource.getUser(), datasource.getPassword());
					c.setAutoCommit(false);

					model = DatabaseModelFactory.create(c);
					connection = c;

					setStatusMessage("データベースへ接続しました。[%s]", datasource.getName());
					info("データベースへ接続しました。[%s]", datasource.getName());

				} catch (ClassNotFoundException ex) {
					setStatusMessage("データベースの接続に失敗しました。");
					fatal("データベースの接続に失敗しました。[%s]", ex.getMessage());
					LOGGER.error("Driver class not found.", ex);

					busy = Boolean.FALSE;
					return;
				} catch (SQLException ex) {
					setStatusMessage("データベースの接続に失敗しました。");
					fatal("データベースの接続に失敗しました。[%s]", ex.getMessage());
					LOGGER.error("Database connection error.", ex);

					busy = Boolean.FALSE;
					return;
				} catch (Exception ex) {
					setStatusMessage("データベースの接続に失敗しました。");
					fatal("データベースの接続に失敗しました。[%s]", ex.getMessage());
					LOGGER.error("Undefined error.", ex);

					busy = Boolean.FALSE;
					return;
				}

				try {
					final List<SchemaEntity> schemas = model.getSchemaList();
					final List<TypeEntity> types = model.getTypeList();

					pnlCondition.refresh(schemas, types);

					final SchemaEntity schema = model.getDefaultSchema();
					pnlCondition.setSelectSchema(schema);

				} catch (SQLException ex) {
					LOGGER.error("", ex);

					busy = Boolean.FALSE;
					return;
				} catch (Exception ex) {
					LOGGER.error("Undefined error.", ex);

					busy = Boolean.FALSE;
					return;
				}

				busy = Boolean.FALSE;
			}
		});
		thread.start();
	}

	public void execute(final String sql) {
		synchronized (busy) {
			if (busy) {
				return;
			}
			busy = Boolean.TRUE;
			setStatusMessage("SQL実行中...");
		}

		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				pnlSqlEditor.setExecuteButtonEnabled(false);

				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					final String row = sql.toLowerCase().trim();

					if (row.startsWith("select")) {
						final Date date = new Date();

						final long start = System.currentTimeMillis();
						ps = connection.prepareStatement(sql);
						rs = ps.executeQuery();
						final long end = System.currentTimeMillis();

						long cnt = pnlDataGrid.setData(rs);
						if (-1 == cnt) {
							info("%d件 表示しました。", pnlDataGrid.getMaxReadSize());
						} else {
							info("%d件 表示しました。", cnt);
						}
						tabBottom.setSelectedIndex(TAB_DATAGRID);

						final long time = end - start;
						pnlSqlHistory.addSqlHistory(new SQLHistoryEntity(date, time, sql));

					} else if (row.startsWith("insert")) {
						final Date date = new Date();

						final long start = System.currentTimeMillis();
						final int cnt = model.executeUpdate(sql);
						final long end = System.currentTimeMillis();

						info("%d件 登録しました。", cnt);

						final long time = end - start;
						pnlSqlHistory.addSqlHistory(new SQLHistoryEntity(date, time, sql));

					} else if (row.startsWith("update")) {
						final Date date = new Date();

						final long start = System.currentTimeMillis();
						final int cnt = model.executeUpdate(sql);
						final long end = System.currentTimeMillis();

						info("%d件 更新しました。", cnt);

						final long time = end - start;
						pnlSqlHistory.addSqlHistory(new SQLHistoryEntity(date, time, sql));

					} else if (row.startsWith("delete")) {
						final Date date = new Date();

						final long start = System.currentTimeMillis();
						final int cnt = model.executeUpdate(sql);
						final long end = System.currentTimeMillis();

						info("%d件 削除しました。", cnt);

						final long time = end - start;
						pnlSqlHistory.addSqlHistory(new SQLHistoryEntity(date, time, sql));

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
				} catch (Exception ex) {
					LOGGER.error("Undefined error.", ex);

				} finally {
					MunchkinUtil.release(rs);
					MunchkinUtil.release(ps);
				}

				pnlSqlEditor.setExecuteButtonEnabled(true);

				busy = Boolean.FALSE;
			}
		});
		thread.start();
	}

	public void exit() {
		manager.stopForWait();

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

	private void getObjectList(final SchemaEntity schema, final TypeEntity type) {
		try {
			if (MunchkinUtil.isNotNullAll(schema, type)) {
				final List<ObjectEntity> objects = model.getObjectList(schema, type);
				pnlObjectList.setObjectList(objects);
			}
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

		// 
		pnlSqlEditor.setText(config.getSqlTextEditorText());
		pnlSqlHistory.setSqlHistorys(history.getHistorySqls());

		if (MunchkinUtil.isEmpty(datasource.getDatasources())) {
			final DatasourceDialog dlg = new DatasourceDialog(this);
			dlg.addDatasourceDialogListener(new DatasourceDialogListener() {
				@Override
				public void datasourceDialogOK(final DatasourceDialog dialog, final DatasourceEntity ds) {
					datasource.addDatasource(ds);
					connect(ds);
				}

				@Override
				public void datasourceDialogCancel(final DatasourceDialog dialog) {
				}
			});
			dlg.setVisible(true);
		} else {
			final DatasourceEntity ds = datasource.getDatasources().get(0);
			connect(ds);
		}
	}

	private void doClosed() {
		// 
		config.setSqlTextEditorText(pnlSqlEditor.getText());
		history.setHistorySqls(pnlSqlHistory.getSqlHistorys());
	}

	@Override
	protected void doMenuFileConnection() {
		final List<DatasourceEntity> datasources = new ArrayList<DatasourceEntity>();
		datasource.getDatasources().forEach(d -> datasources.add(new DatasourceEntity(d)));

		final DatasourceListDialog dlg = new DatasourceListDialog(this, datasources);
		dlg.setVisible(true);
	}

	@Override
	protected void doMenuFileDatasource() {
		final List<DatasourceEntity> bufDatasources = new ArrayList<DatasourceEntity>();
		datasource.getDatasources().forEach(ds -> bufDatasources.add(new DatasourceEntity(ds)));

		final DatasourcesDialog dlg = new DatasourcesDialog(this, bufDatasources);
		dlg.addDatasourcesDialogListener(new DatasourcesDialogListener() {
			@Override
			public void datasourcesDialogClosedOk(final DatasourcesDialog dialog, final List<DatasourceEntity> dss) {
				datasource.setDatasources(dss);
			}

			@Override
			public void datasourcesDialogClosedCancel(final DatasourcesDialog dialog) {
			}
		});
		dlg.setVisible(true);
	}

	@Override
	protected void doMenuFileExit() {
		exit();
	}

	@Override
	protected void doMenuSqlExecute() {
		final String sql = pnlSqlEditor.getSelectedText();
		if (MunchkinUtil.isNotEmpty(sql)) {
			execute(sql);
		}
	}

	@Override
	protected void doMenuToolExport() {
		final ExportDialog dlg = new ExportDialog(this, model);
		dlg.setVisible(true);
	}

	@Override
	protected void doMenuHelpVersion() {
		final VersionDialog dlg = new VersionDialog(this);
		dlg.setVisible(true);
	}
}
